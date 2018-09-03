/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.meshprovisioner;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import no.nordicsemi.android.meshprovisioner.configuration.ConfigMessage;
import no.nordicsemi.android.meshprovisioner.configuration.MeshModel;
import no.nordicsemi.android.meshprovisioner.configuration.ProvisionedMeshNode;
import no.nordicsemi.android.meshprovisioner.configuration.SequenceNumber;
import no.nordicsemi.android.meshprovisioner.utils.AddressUtils;
import no.nordicsemi.android.meshprovisioner.utils.InterfaceAdapter;
import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;
import no.nordicsemi.android.meshprovisioner.utils.SecureUtils;


public class MeshManagerApi implements InternalTransportCallbacks, InternalMeshManagerCallbacks {

    public static final byte PDU_TYPE_PROVISIONING = 0x03;
    /**
     * Mesh provisioning service UUID
     */
    public final static UUID MESH_PROXY_UUID = UUID.fromString("00001828-0000-1000-8000-00805F9B34FB");
    private static final String TAG = MeshManagerApi.class.getSimpleName();
    private static final String PROVISIONED_NODES_FILE = "PROVISIONED_FILES";
    private static final String CONFIGURATION_SRC = "CONFIGURATION_SRC";
    private static final String SRC = "SRC";
    //PDU types
    private static final byte PDU_TYPE_NETWORK = 0x00;
    private static final byte PDU_TYPE_MESH_BEACON = 0x01;
    private static final byte PDU_TYPE_PROXY_CONFIGURATION = 0x02;
    //GATT level segmentation
    private static final byte SAR_COMPLETE = 0b00;
    private static final byte GATT_SAR_START = 0b01;
    private static final byte GATT_SAR_CONTINUATION = 0b10;
    private static final byte GATT_SAR_END = 0b11;
    //GATT level segmentation mask
    private static final int GATT_SAR_MASK = 0xC0;
    private static final int GATT_SAR_UNMASK = 0x3F;
    private static final int SAR_BIT_OFFSET = 6;
    /**
     * Length of the random number required to calculate the hash containing the node id
     */
    private final static int HASH_RANDOM_NUMBER_LENGTH = 64; //in bits
    private static final int ADVERTISEMENT_TYPE_NETWORK_ID = 0x00;
    private static final int ADVERTISEMENT_TYPE_NODE_IDENTITY = 0x01;
    /**
     * Offset of the hash contained in the advertisement service data
     */
    private final static int ADVERTISED_HASH_OFFSET = 1;
    /**
     * Length of the hash contained in the advertisement service data
     */
    private final static int ADVERTISED_HASH_LENGTH = 8;
    /**
     * Offset of the hash contained in the advertisement service data
     */
    private final static int ADVERTISED_RANDOM_OFFSET = 9;
    /**
     * Length of the hash contained in the advertisement service data
     */
    private final static int ADVERTISED_RANDOM_LENGTH = 8;
    /**
     * Offset of the network id contained in the advertisement service data
     */
    private final static int ADVERTISED_NETWWORK_ID_OFFSET = 1;
    /**
     * Length of the network id contained in the advertisement service data
     */
    private final static int ADVERTISED_NETWWORK_ID_LENGTH = 8;
    private final Map<Integer, ProvisionedMeshNode> mProvisionedNodes = new LinkedHashMap<>();
    private final ProvisioningSettings mProvisioningSettings;
    private Context mContext;
    private Gson mGson;
    private byte[] mConfigurationSrc = {0x07, (byte) 0xFF}; //0x07FF;
    private MeshManagerTransportCallbacks mTransportCallbacks;
    private MeshProvisioningHandler mMeshProvisioningHandler;
    private MeshConfigurationHandler mMeshConfigurationHandler;
    private byte[] mIncomingBuffer;
    private int mIncomingBufferOffset;
    private byte[] mOutgoingBuffer;
    private int mOutgoingBufferOffset;

    public MeshManagerApi(final Context context) {
        this.mContext = context;
        this.mProvisioningSettings = new ProvisioningSettings(context);
        initGson();
        initProvisionedNodes();
        intiConfigurationSrc();
        mMeshProvisioningHandler = new MeshProvisioningHandler(context, this, this);
        mMeshConfigurationHandler = new MeshConfigurationHandler(context, this, this);
    }

    private void intiConfigurationSrc() {
        final SharedPreferences preferences = mContext.getSharedPreferences(CONFIGURATION_SRC, Context.MODE_PRIVATE);
        final int tempSrc = preferences.getInt(SRC, 0);
        if(tempSrc != 0)
            mConfigurationSrc = new byte[]{(byte) ((tempSrc >> 8) & 0xFF), (byte) (tempSrc & 0xFF)};
    }

    public void setProvisionerManagerTransportCallbacks(final MeshManagerTransportCallbacks transportCallbacks) {
        mTransportCallbacks = transportCallbacks;
    }

    public void setProvisioningStatusCallbacks(final MeshProvisioningStatusCallbacks callbacks) {
        mMeshProvisioningHandler.setProvisioningCallbacks(callbacks);
    }

    public void setConfigurationCallbacks(final MeshConfigurationStatusCallbacks callbacks) {
        mMeshConfigurationHandler.setConfigurationCallbacks(callbacks);
    }

    public ConfigMessage.MessageState getConfigurationState() {
        return mMeshConfigurationHandler.getConfigurationState();
    }

    public Map<Integer, ProvisionedMeshNode> getProvisionedNodes() {
        return mProvisionedNodes;
    }

    /**
     * Returns the default provisioning settings from {@link ProvisioningSettings}
     *
     * @return provisioning settings
     */
    public ProvisioningSettings getProvisioningSettings() {
        return mProvisioningSettings;
    }

    /**
     * Returns the source unicast address set to the the library in the mesh network
     *
     * @return byte array containing the address
     */
    public byte[] getConfiguratorSrc() {
        return mConfigurationSrc;
    }

    /**
     * Set the source unicast address to the the library in the mesh network. This method will check if the addres is already taken by a node
     *
     * @return true is successful
     */
    public boolean setConfiguratorSrc(final byte[] configurationSrc) throws IllegalArgumentException {
        final int tempSrc = (configurationSrc[0] & 0xFF) << 8 | (configurationSrc[1] & 0xFF);
        if(MeshParserUtils.validateUnicastAddressInput(mContext, tempSrc)) {
            if(!mProvisionedNodes.containsKey(tempSrc)){
                mConfigurationSrc = configurationSrc;
                saveSrc();

                //Set the configuration source for all provisioned nodes
                for(Map.Entry<Integer, ProvisionedMeshNode> entry : mProvisionedNodes.entrySet()) {
                    entry.getValue().setConfigurationSrc(mConfigurationSrc);
                }

                //Save all nodes
                saveProvisionedNodes();
                return true;
            } else {
                throw new IllegalArgumentException("Address already occupied by a node");
            }
        }
        return false;
    }

    private void initGson() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.enableComplexMapKeySerialization();
        gsonBuilder.registerTypeAdapter(MeshModel.class, new InterfaceAdapter<MeshModel>());
        gsonBuilder.setPrettyPrinting();
        mGson = gsonBuilder.create();
    }

    /**
     * Load serialized provisioned nodes from preferences
     */
    private void initProvisionedNodes() {
        final SharedPreferences preferences = mContext.getSharedPreferences(PROVISIONED_NODES_FILE, Context.MODE_PRIVATE);
        final Map<String, ?> nodes = preferences.getAll();

        if (!nodes.isEmpty()) {
            final List<Integer> orderedKeys = reOrderProvisionedNodes(nodes);
            mProvisionedNodes.clear();
            for (int orderedKey : orderedKeys) {
                final String key = String.format(Locale.US, "0x%04X", orderedKey);
                final String json = preferences.getString(key, null);
                if (json != null) {
                    final ProvisionedMeshNode node = mGson.fromJson(json, ProvisionedMeshNode.class);
                    final int unicastAddress = AddressUtils.getUnicastAddressInt(node.getUnicastAddress());
                    mProvisionedNodes.put(unicastAddress, node);
                }
            }
        }
    }

    /**
     * Order the keys so that the nodes are read in insertion order
     *
     * @param nodes list containing unordered nodes
     * @return node list
     */
    private List<Integer> reOrderProvisionedNodes(final Map<String, ?> nodes) {
        final Set<String> unorderedKeys = nodes.keySet();
        final List<Integer> orderedKeys = new ArrayList<>();
        for (String k : unorderedKeys) {
            final int key = Integer.decode(k);
            orderedKeys.add(key);
        }
        Collections.sort(orderedKeys);
        return orderedKeys;
    }

    @Override
    public void onNodeProvisioned(final ProvisionedMeshNode meshNode) {
        final int unicastAddress = AddressUtils.getUnicastAddressInt(meshNode.getUnicastAddress());
        mProvisionedNodes.put(unicastAddress, meshNode);
        saveProvisionedNode(meshNode);
    }

    private void saveSrc() {
        final SharedPreferences preferences = mContext.getSharedPreferences(CONFIGURATION_SRC, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SRC, (mConfigurationSrc[0] & 0xFF) << 8 | (mConfigurationSrc[1] & 0xFF));
        editor.apply();
    }

    /**
     * Serialize and save provisioned node
     */
    private void saveProvisionedNode(final ProvisionedMeshNode node) {
        final SharedPreferences preferences = mContext.getSharedPreferences(PROVISIONED_NODES_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        final String unicastAddress = MeshParserUtils.bytesToHex(node.getUnicastAddress(), true);
        final String provisionedNode = mGson.toJson(node);
        editor.putString(unicastAddress, provisionedNode);
        editor.apply();
    }

    /**
     * Serialize and save all provisioned nodes
     */
    private void saveProvisionedNodes() {
        final SharedPreferences preferences = mContext.getSharedPreferences(PROVISIONED_NODES_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        for(Map.Entry<Integer, ProvisionedMeshNode> entry : mProvisionedNodes.entrySet()) {
            final ProvisionedMeshNode node = entry.getValue();
            final String unicastAddress = MeshParserUtils.bytesToHex(node.getUnicastAddress(), true);
            final String provisionedNode = mGson.toJson(node);
            editor.putString(unicastAddress, provisionedNode);
        }
        editor.apply();
    }

    /**
     * Serialize and save provisioned node
     */
    private void deleteProvisionedNode(final ProvisionedMeshNode node) {
        final SharedPreferences preferences = mContext.getSharedPreferences(PROVISIONED_NODES_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        final String unicastAddress = MeshParserUtils.bytesToHex(node.getUnicastAddress(), true);
        editor.remove(unicastAddress);
        editor.apply();
    }

    /**
     * Clear provisioned ndoes
     */
    private void clearProvisionedNodes() {
        final SharedPreferences preferences = mContext.getSharedPreferences(PROVISIONED_NODES_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public void onUnicastAddressChanged(final int unicastAddress) {
        //Now that we have received the unicast addresses assigned to element addresses,
        //increment it here again so the next node to be provisioned will have the next available address in the network
        int unicastAdd = unicastAddress + 1;
        //We check if the incremented unicast address is already taken by the app/configurator
        final int tempSrc = (mConfigurationSrc[0] & 0xFF) << 8 | (mConfigurationSrc[1] & 0xFF);
        if(unicastAdd == tempSrc) {
            unicastAdd = unicastAddress + 1;
        }
        mProvisioningSettings.setUnicastAddress(unicastAdd);

    }

    /**
     * Handles notifications received by the client.
     * <p>
     * This method will check if the library should wait for more data in case of a gatt layer segmentation.
     * If its required the method will remove the segmentation bytes and combine the data together.
     * </p>
     *
     * @param meshNode mesh node that the pdu was received from
     * @param data     pdu received by the client
     */
    public final void handleNotifications(BaseMeshNode meshNode, final int mtuSize, final byte[] data) {

        byte[] unsegmentedPdu;
        if (!shouldWaitForMoreData(data)) {
            unsegmentedPdu = data;
        } else {
            final byte[] combinedPdu = appendPdu(mtuSize, data);
            if (combinedPdu == null)
                return;
            else {
			    //unsegmentedPdu = removeSegmentation(mtuSize, combinedPdu);
				// Segmentation removal logic is updated in appendPdu(...) routine.
                unsegmentedPdu = combinedPdu;
            }
        }
        parseNotifications(meshNode, unsegmentedPdu);
    }


    /**
     * Parses notifications received by the client.
     *
     * @param meshNode       mesh node that the pdu was received from
     * @param unsegmentedPdu pdu received by the client.
     */
    private void parseNotifications(final BaseMeshNode meshNode, final byte[] unsegmentedPdu) {
        switch (unsegmentedPdu[0]) {
            case PDU_TYPE_NETWORK:
                //Network PDU
                Log.v(TAG, "Received network pdu: " + MeshParserUtils.bytesToHex(unsegmentedPdu, true));
                mMeshConfigurationHandler.parseConfigurationNotifications((ProvisionedMeshNode) meshNode, unsegmentedPdu);
                break;
            case PDU_TYPE_MESH_BEACON:
                //Mesh beacon
                Log.v(TAG, "Received mesh beacon: " + MeshParserUtils.bytesToHex(unsegmentedPdu, true));
                break;
            case PDU_TYPE_PROXY_CONFIGURATION:
                //Proxy configuration
                Log.v(TAG, "Received proxy configuration message: " + MeshParserUtils.bytesToHex(unsegmentedPdu, true));
                break;
            case PDU_TYPE_PROVISIONING:
                //Provisioning PDU
                Log.v(TAG, "Received provisioning message: " + MeshParserUtils.bytesToHex(unsegmentedPdu, true));
                mMeshProvisioningHandler.parseProvisioningNotifications(unsegmentedPdu);
                break;
        }
    }

    public final void handleWrites(BaseMeshNode meshNode, final int mtuSize, final byte[] data) {
        byte[] unsegmentedPdu;
        if (!shouldWaitForMoreData(data)) {
            unsegmentedPdu = data;
        } else {
            final byte[] combinedPdu = appendWritePdu(mtuSize, data);
            if (combinedPdu == null)
                return;
            else {
                unsegmentedPdu = removeSegmentation(mtuSize, combinedPdu);
            }
        }
        handleWriteCallbacks(meshNode, unsegmentedPdu);
    }

    /**
     * Handles callbacks after writing to characteristics to maintain/update the state machine
     *
     * @param meshNode mesh node
     * @param data     written to the peripheral
     */
    private void handleWriteCallbacks(final BaseMeshNode meshNode, final byte[] data) {
        switch (data[0]) {
            case PDU_TYPE_NETWORK:
                //Network PDU
                Log.v(TAG, "Network pdu sent: " + MeshParserUtils.bytesToHex(data, true));
                mMeshConfigurationHandler.handleConfigurationWriteCallbacks((ProvisionedMeshNode) meshNode, data);
                break;
            case PDU_TYPE_MESH_BEACON:
                //Mesh beacon
                Log.v(TAG, "Mesh beacon pdu sent: " + MeshParserUtils.bytesToHex(data, true));
                break;
            case PDU_TYPE_PROXY_CONFIGURATION:
                //Proxy configuration
                Log.v(TAG, "Proxy configuration pdu sent: " + MeshParserUtils.bytesToHex(data, true));
                break;
            case PDU_TYPE_PROVISIONING:
                //Provisioning PDU
                Log.v(TAG, "Provisioning pdu sent: " + MeshParserUtils.bytesToHex(data, true));
                mMeshProvisioningHandler.handleProvisioningWriteCallbacks();
                break;
        }
    }

    @Override
    public void sendPdu(final BaseMeshNode meshNode, byte[] pdu) {
        final int mtu = mTransportCallbacks.getMtu();
        mTransportCallbacks.sendPdu(meshNode, applySegmentation(mtu, pdu));
    }

    @Override
    public void updateMeshNode(final ProvisionedMeshNode meshNode) {
        if (meshNode != null) {
            final int unicast = AddressUtils.getUnicastAddressInt(meshNode.getUnicastAddress());
            //We update the mesh node in our map of mesh nodes
            mProvisionedNodes.put(unicast, meshNode);
            saveProvisionedNode(meshNode);
        }
    }

    @Override
    public void onMeshNodeReset(final ProvisionedMeshNode meshNode) {
        if (meshNode != null) {
            final int unicast = AddressUtils.getUnicastAddressInt(meshNode.getUnicastAddress());
            deleteProvisionedNode(meshNode);
            mProvisionedNodes.remove(unicast);
        }
    }

    private boolean shouldWaitForMoreData(final byte[] pdu) {
        final int gattSar = (pdu[0] & GATT_SAR_MASK) >> SAR_BIT_OFFSET;
        switch (gattSar) {
            case GATT_SAR_START:
            case GATT_SAR_CONTINUATION:
            case GATT_SAR_END:
                return true;
            default:
                return false;
        }
    }

    /**
     * Appends the PDUs that are segmented at gatt layer.
     *
     * @param mtuSize mtu size supported by the device/node
     * @param pdu     pdu received by the provisioner
     * @return the combine pdu or returns null if not complete.
     */
    private byte[] appendPdu(final int mtuSize, final byte[] pdu) {
	    // Extract the GATT PDU's SAR type
        final int gattSar_t = (pdu[0] & GATT_SAR_MASK) >> SAR_BIT_OFFSET;
        if (mIncomingBuffer == null) {
            final int length = Math.min(pdu.length, mtuSize);
            mIncomingBufferOffset = 0;
            mIncomingBufferOffset += length;
            mIncomingBuffer = pdu;
        } else {
            final int length = Math.min(pdu.length, mtuSize);
            byte[] buffer = null;
            if(gattSar_t == GATT_SAR_START) {
			    // If SAR Type is 'Start' then copy the bytes as is
                buffer = new byte[mIncomingBuffer.length + length];
                System.arraycopy(mIncomingBuffer, 0, buffer, 0, mIncomingBufferOffset);
                System.arraycopy(pdu, 0, buffer, mIncomingBufferOffset, length);
                mIncomingBufferOffset += length;
            } else if(gattSar_t == GATT_SAR_CONTINUATION || gattSar_t == GATT_SAR_END){
			    // If SAR Type is 'Continuation' or 'End' strip the SAR from byte stream
                buffer = new byte[mIncomingBuffer.length + length-1];
                System.arraycopy(mIncomingBuffer, 0, buffer, 0, mIncomingBufferOffset);
                System.arraycopy(pdu, 1, buffer, mIncomingBufferOffset, length - 1);
                mIncomingBufferOffset += length - 1;
            }

            mIncomingBuffer = buffer;
            if (length < mtuSize && gattSar_t == GATT_SAR_END) {
			    // When complete PDU is assembled, Make the SAR type as 'Complete PDU'
                mIncomingBuffer[0] = (byte)(mIncomingBuffer[0] & GATT_SAR_UNMASK);
                final byte packet[] = mIncomingBuffer;
                mIncomingBuffer = null;
                return packet;
            }
        }
        return null;
    }

    /**
     * Appends the PDUs that are segmented at gatt layer.
     *
     * @param mtuSize mtu size supported by the device/node
     * @param pdu     pdu received by the provisioner
     * @return the combine pdu or returns null if not complete.
     */
    private byte[] appendWritePdu(final int mtuSize, final byte[] pdu) {
        if (mOutgoingBuffer == null) {
            final int length = Math.min(pdu.length, mtuSize);
            mOutgoingBufferOffset = 0;
            mOutgoingBufferOffset += length;
            mOutgoingBuffer = pdu;
        } else {
            final int length = Math.min(pdu.length, mtuSize);
            final byte[] buffer = new byte[mOutgoingBuffer.length + length];
            System.arraycopy(mOutgoingBuffer, 0, buffer, 0, mOutgoingBufferOffset);
            System.arraycopy(pdu, 0, buffer, mOutgoingBufferOffset, length);
            mOutgoingBufferOffset += length;
            mOutgoingBuffer = buffer;
            if (length < mtuSize) {
                final byte packet[] = mOutgoingBuffer;
                mOutgoingBuffer = null;
                return packet;
            }
        }
        return null;
    }

    private byte[] applySegmentation(final int mtuSize, final byte[] pdu) {
        int srcOffset = 0;
        int dstOffset = 0;
        final int chunks = (pdu.length + (mtuSize - 1)) / mtuSize;

        final int pduType = pdu[0];
        if (chunks > 1) {
            final byte[] segmentedBuffer = new byte[pdu.length + chunks - 1];
            int length;
            for (int i = 0; i < chunks; i++) {
                if (i == 0) {
                    length = Math.min(pdu.length - srcOffset, mtuSize);
                    System.arraycopy(pdu, srcOffset, segmentedBuffer, dstOffset, length);
                    segmentedBuffer[0] = (byte) ((GATT_SAR_START << 6) | pduType);
                } else if (i == chunks - 1) {
                    length = Math.min(pdu.length - srcOffset, mtuSize);
                    segmentedBuffer[dstOffset] = (byte) ((GATT_SAR_END << 6) | pduType);
                    System.arraycopy(pdu, srcOffset, segmentedBuffer, dstOffset + 1, length);
                } else {
                    length = Math.min(pdu.length - srcOffset, mtuSize - 1);
                    segmentedBuffer[dstOffset] = (byte) ((GATT_SAR_CONTINUATION << 6) | pduType);
                    System.arraycopy(pdu, srcOffset, segmentedBuffer, dstOffset + 1, length);
                }
                srcOffset += length;
                dstOffset += mtuSize;
            }
            return segmentedBuffer;
        }
        return pdu;
    }

    private byte[] removeSegmentation(final int mtuSize, final byte[] data) {
        int srcOffset = 0;
        int dstOffset = 0;
        final int chunks = (data.length + (mtuSize - 1)) / mtuSize;
        if (chunks > 1) {
            final byte[] buffer = new byte[data.length - (chunks - 1)];
            int length;
            for (int i = 0; i < chunks; i++) {
                // when removing segmentation bits we only remove the start because the pdu type would be the same for each segment.
                // Therefore we can ignore this pdu type byte as they are already put together in the ble
                if (i == 0) {
                    length = Math.min(buffer.length - dstOffset, mtuSize);
                    System.arraycopy(data, srcOffset, buffer, dstOffset, length);
                    buffer[0] = (byte) (buffer[0] & GATT_SAR_UNMASK);
                } else if (i == chunks - 1) {
                    length = Math.min(buffer.length - dstOffset, mtuSize);
                    System.arraycopy(data, srcOffset + 1, buffer, dstOffset, length);
                } else {
                    length = Math.min(buffer.length - dstOffset, mtuSize) - 1;
                    System.arraycopy(data, srcOffset + 1, buffer, dstOffset, length);
                }
                srcOffset += mtuSize;
                dstOffset += length;
            }
            return buffer;
        }
        return data;
    }

    /**
     * Starts the provisioning process
     */
    public void startProvisioning(@NonNull final String address, final String nodeName, @NonNull final String networkKeyValue, final int keyIndex, final int flags, final int ivIndex, final int unicastAddress, final int globalTtl) throws IllegalArgumentException {
        //We must save all the provisioning data here so that they could be reused when provisioning the next devices
        mProvisioningSettings.setNetworkKey(networkKeyValue);
        mProvisioningSettings.setKeyIndex(keyIndex);
        mProvisioningSettings.setFlags(flags);
        mProvisioningSettings.setIvIndex(ivIndex);
        mProvisioningSettings.setUnicastAddress(unicastAddress);
        mProvisioningSettings.setGlobalTtl(globalTtl);
        mMeshProvisioningHandler.startProvisioning(address, nodeName, networkKeyValue, keyIndex, flags, ivIndex, unicastAddress, globalTtl, mConfigurationSrc);
    }

    /**
     * Set the provisioning confirmation
     *
     * @param pin confirmation pin
     */
    public final void setProvisioningConfirmation(final String pin) {
        mMeshProvisioningHandler.setProvisioningConfirmation(pin);
    }

    /**
     * Generate network id
     *
     * @return network id
     */
    public String generateNetworkId(final byte[] networkKey) {
        return MeshParserUtils.bytesToHex(SecureUtils.calculateK3(networkKey), false);
    }

    /**
     * Checks if the hashes match
     *
     * @param meshNode    mesh node to match with
     * @param serviceData advertised service data
     * @return true if the hashes match or false otherwise
     */
    public boolean nodeIdentityMatches(final ProvisionedMeshNode meshNode, final byte[] serviceData) {
        final byte[] advertisedHash = getAdvertisedHash(serviceData);
        //If there is no advertised hash return false as this is used to match against the generated hash
        if (advertisedHash == null) {
            return false;
        }

        //If there is no advertised random return false as this is used to generate the hash to match against the advertised
        final byte[] random = getAdvertisedRandom(serviceData);
        if (random == null) {
            return false;
        }

        //if generated hash is null return false
        final byte[] generatedHash = SecureUtils.calculateHash(meshNode.getIdentityKey(), random, meshNode.getUnicastAddress());

        final boolean flag = Arrays.equals(advertisedHash, generatedHash);
        if (flag) {
            meshNode.setNodeIdentifier(MeshParserUtils.bytesToHex(advertisedHash, false));
        }
        return flag;
    }

    /**
     * Checks if the node is advertising with Node Identity
     *
     * @param serviceData advertised service data
     * @return returns true if the node is advertising with Node Identity or false otherwise
     */
    public boolean isAdvertisedWithNodeIdentity(final byte[] serviceData) {
        return serviceData != null &&
                serviceData[ADVERTISED_HASH_OFFSET - 1] == ADVERTISEMENT_TYPE_NODE_IDENTITY;
    }

    /**
     * Returns the advertised hash
     *
     * @param serviceData advertised service data
     * @return returns the advertised hash
     */
    private byte[] getAdvertisedHash(final byte[] serviceData) {
        if (serviceData == null)
            return null;
        final ByteBuffer expectedBufferHash = ByteBuffer.allocate(ADVERTISED_HASH_LENGTH).order(ByteOrder.BIG_ENDIAN);
        expectedBufferHash.put(serviceData, ADVERTISED_HASH_OFFSET, ADVERTISED_HASH_LENGTH);
        return expectedBufferHash.array();
    }

    /**
     * Returns the advertised random
     *
     * @param serviceData advertised service data
     * @return returns the advertised random
     */
    private byte[] getAdvertisedRandom(final byte[] serviceData) {
        if (serviceData == null || serviceData.length <= ADVERTISED_RANDOM_LENGTH)
            return null;
        final ByteBuffer expectedBufferHash = ByteBuffer.allocate(ADVERTISED_RANDOM_LENGTH).order(ByteOrder.BIG_ENDIAN);
        expectedBufferHash.put(serviceData, ADVERTISED_RANDOM_OFFSET, ADVERTISED_RANDOM_LENGTH);
        return expectedBufferHash.array();
    }

    /**
     * Checks if the network ids match
     *
     * @param networkId   network id of the mesh
     * @param serviceData advertised service data
     * @return returns true if the network ids match or false otherwise
     */
    public boolean networkIdMatches(final String networkId, final byte[] serviceData) {
        final byte[] advertisedNetworkId = getAdvertisedNetworkId(serviceData);
        return advertisedNetworkId != null && networkId.equals(MeshParserUtils.bytesToHex(advertisedNetworkId, false).toUpperCase());
    }

    /**
     * Returns the advertised hash
     *
     * @param serviceData advertised service data
     * @return returns the advertised hash
     */
    public boolean isAdvertisingWithNetworkIdentity(final byte[] serviceData) {
        return serviceData != null && serviceData[ADVERTISED_NETWWORK_ID_OFFSET - 1] == ADVERTISEMENT_TYPE_NETWORK_ID;
    }

    /**
     * Returns the advertised network identity
     *
     * @param serviceData advertised service data
     * @return returns the advertised network identity
     */
    private byte[] getAdvertisedNetworkId(final byte[] serviceData) {
        if (serviceData == null)
            return null;
        final ByteBuffer advertisedNetowrkID = ByteBuffer.allocate(ADVERTISED_NETWWORK_ID_LENGTH).order(ByteOrder.BIG_ENDIAN);
        advertisedNetowrkID.put(serviceData, ADVERTISED_NETWWORK_ID_OFFSET, ADVERTISED_HASH_LENGTH);
        return advertisedNetowrkID.array();
    }

    /**
     * Get composition data of the node
     *
     * @param meshNode corresponding mesh node
     */
    public void getCompositionData(final ProvisionedMeshNode meshNode) {
        final int aszmic = 0;
        mMeshConfigurationHandler.sendCompositionDataGet(meshNode, aszmic);
    }

    /**
     * adds the given the app key to the global app key list on the node
     *
     * @param meshNode    corresponding mesh node
     * @param appKeyIndex index of the app key in the global app key list
     * @param appKey      application key
     */
    public void addAppKey(final ProvisionedMeshNode meshNode, final int appKeyIndex, final String appKey) {
        if (appKey == null || appKey.isEmpty())
            throw new IllegalArgumentException(mContext.getString(R.string.error_null_key));
        mMeshConfigurationHandler.sendAppKeyAdd(meshNode, appKeyIndex, appKey, 0);
    }

    /**
     * binding the app key
     *
     * @param meshNode       corresponding mesh node
     * @param elementAddress elementAddress
     * @param model          16-bit SIG Model Identifier or 32-bit Vendor Model identifier
     * @param appKeyIndex    index of the app key
     */
    public void bindAppKey(final ProvisionedMeshNode meshNode, final byte[] elementAddress, final MeshModel model, final int appKeyIndex) {
        mMeshConfigurationHandler.bindAppKey(meshNode, 0, elementAddress, model.getModelId(), appKeyIndex);
    }

    /**
     * Set a publish address for configuration model
     *
     * @param provisionedMeshNode            Mesh node containing the model
     * @param elementAddress                 Address of the element containing the model
     * @param publishAddress                 Address to which the model must publish
     * @param appKeyIndex                    Application key index
     * @param modelIdentifier                Identifier of the model. This could be 16-bit SIG Model or a 32-bit Vendor model identifier
     * @param credentialFlag                 Credential flag, set 0 to use master credentials and 1 for friendship credentials. If there is not friendship credentials master key material will be used by default
     * @param publishTtl                     Default ttl value for outgoing messages
     * @param publishPeriod                  Period for periodic status publishing
     * @param publishRetransmitCount         Number of retransmissions for each published message
     * @param publishRetransmitIntervalSteps Number of 50-millisecond steps between retransmissions
     */
    public void setConfigModelPublishAddress(final ProvisionedMeshNode provisionedMeshNode, final byte[] elementAddress, final byte[] publishAddress,
                                             final int appKeyIndex, final int modelIdentifier, final int credentialFlag, final int publishTtl,
                                             final int publishPeriod, final int publishRetransmitCount, final int publishRetransmitIntervalSteps) {
        mMeshConfigurationHandler.setConfigModelPublishAddress(provisionedMeshNode, 0, elementAddress, publishAddress,
                appKeyIndex, modelIdentifier, credentialFlag, publishTtl, publishPeriod, publishRetransmitCount, publishRetransmitIntervalSteps);
    }

    /**
     * Set a subscription address for configuration model
     *
     * @param meshNode            Mesh node containing the model
     * @param elementAddress      Address of the element containing the model
     * @param subscriptionAddress Address to which the model must subscribe
     * @param modelIdentifier     Identifier of the model. This could be 16-bit SIG Model or a 32-bit Vendor model identifier
     */
    public void addSubscriptionAddress(final ProvisionedMeshNode meshNode, final byte[] elementAddress, final byte[] subscriptionAddress,
                                       final int modelIdentifier) {
        mMeshConfigurationHandler.addSubscriptionAddress(meshNode, 0, elementAddress, subscriptionAddress, modelIdentifier);
    }

    /**
     * Delete a subscription address for configuration model
     *
     * @param meshNode            Mesh node containing the model
     * @param elementAddress      Address of the element containing the model
     * @param subscriptionAddress Address to which the model must subscribe
     * @param modelIdentifier     Identifier of the model. This could be 16-bit SIG Model or a 32-bit Vendor model identifier
     */
    public void deleteSubscriptionAddress(final ProvisionedMeshNode meshNode, final byte[] elementAddress, final byte[] subscriptionAddress,
                                          final int modelIdentifier) {
        mMeshConfigurationHandler.deleteSubscriptionAddress(meshNode, 0, elementAddress, subscriptionAddress, modelIdentifier);
    }

    public void resetMeshNetwork() {
        mProvisionedNodes.clear();
        clearProvisionedNodes();
        SequenceNumber.resetSequenceNumber(mContext);
        mProvisioningSettings.clearProvisioningData();
        mProvisioningSettings.generateProvisioningData();
    }

    /**
     * Send generic on off get to mesh node
     *
     * @param node        mesh node to send generic on off get
     * @param model       model to control
     * @param appKeyIndex application key index
     */
    public void getGenericOnOff(final ProvisionedMeshNode node, final MeshModel model, final byte[] dstAddress, final int appKeyIndex) {

        if (!model.getBoundAppKeyIndexes().isEmpty()) {
            if (appKeyIndex >= 0) {
                if (dstAddress == null)
                    throw new IllegalArgumentException("Destination address cannot be null!");
                mMeshConfigurationHandler.getGenericOnOff(node, model, dstAddress, false, appKeyIndex);
            } else {
                throw new IllegalArgumentException("Invalid app key index!");
            }
        } else {
            throw new IllegalArgumentException("Please bind an app key to this model to control this model!");
        }
    }

    /**
     * Send generic on off set to mesh node
     *
     * @param node                 mesh node to send generic on off get
     * @param model                model to control
     * @param dstAddress           address of the element the mesh model belongs to
     * @param appKeyIndex          application key index
     * @param transitionSteps      the number of steps
     * @param transitionResolution the resolution for the number of steps
     * @param delay                message execution delay in 5ms steps. After this delay milliseconds the model will execute the required behaviour.
     * @param state                on off state
     */
    public void setGenericOnOff(final ProvisionedMeshNode node, final MeshModel model, final byte[] dstAddress, final int appKeyIndex, @Nullable final Integer transitionSteps,
                                @Nullable final Integer transitionResolution, @Nullable final Integer delay, final boolean state) {
        if (!model.getBoundAppKeyIndexes().isEmpty()) {
            if (appKeyIndex >= 0) {
                if (dstAddress == null)
                    throw new IllegalArgumentException("Destination address cannot be null!");
                mMeshConfigurationHandler.setGenericOnOff(node, model, dstAddress, false, appKeyIndex, transitionSteps, transitionResolution, delay, state);
            } else {
                throw new IllegalArgumentException("Invalid app key index!");
            }
        } else {
            throw new IllegalArgumentException("Please bind an app key to this model to control this model!");
        }
    }

    /**
     * Send generic on off set unacknowledged message to mesh node
     *
     * @param node                 mesh node to send generic on off get
     * @param model                model to control
     * @param dstAddress           address of the element the mesh model belongs to
     * @param appKeyIndex          application key index
     * @param transitionSteps      the number of steps
     * @param transitionResolution the resolution for the number of steps
     * @param delay                message execution delay in 5ms steps. After this delay milliseconds the model will execute the required behaviour.
     * @param state                on off state
     */
    public void setGenericOnOffUnacknowledged(final ProvisionedMeshNode node, final MeshModel model, final byte[] dstAddress, final int appKeyIndex, @Nullable final Integer transitionSteps,
                                              @Nullable final Integer transitionResolution, @Nullable final Integer delay, final boolean state) {
        if (!model.getBoundAppKeyIndexes().isEmpty()) {
            if (appKeyIndex >= 0) {
                if (dstAddress == null)
                    throw new IllegalArgumentException("Destination address cannot be null!");
                mMeshConfigurationHandler.setGenericOnOffUnacknowledged(node, model, dstAddress, false, appKeyIndex, transitionSteps, transitionResolution, delay, state);
            } else {
                throw new IllegalArgumentException("Invalid app key index!");
            }
        } else {
            throw new IllegalArgumentException("Please bind an app key to this model to control this model!");
        }
    }

    /**
     * Resets the specific mesh node
     *
     * @param provisionedMeshNode mesh node to be reset
     */
    public void resetMeshNode(@NonNull final ProvisionedMeshNode provisionedMeshNode) {
        if(provisionedMeshNode == null)
            throw new IllegalArgumentException("Mesh node cannot be null!");
        mMeshConfigurationHandler.resetMeshNode(provisionedMeshNode);
    }
}
