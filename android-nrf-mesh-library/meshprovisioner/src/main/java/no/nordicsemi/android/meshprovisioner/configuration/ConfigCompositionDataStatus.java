package no.nordicsemi.android.meshprovisioner.configuration;

import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import no.nordicsemi.android.meshprovisioner.InternalTransportCallbacks;
import no.nordicsemi.android.meshprovisioner.MeshConfigurationStatusCallbacks;
import no.nordicsemi.android.meshprovisioner.messages.AccessMessage;
import no.nordicsemi.android.meshprovisioner.messages.ControlMessage;
import no.nordicsemi.android.meshprovisioner.messages.Message;
import no.nordicsemi.android.meshprovisioner.models.VendorModel;
import no.nordicsemi.android.meshprovisioner.opcodes.ConfigMessageOpCodes;
import no.nordicsemi.android.meshprovisioner.utils.AddressUtils;
import no.nordicsemi.android.meshprovisioner.utils.DeviceFeatureUtils;
import no.nordicsemi.android.meshprovisioner.utils.Element;
import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;
import no.nordicsemi.android.meshprovisioner.utils.SigModelParser;

public final class ConfigCompositionDataStatus extends ConfigMessage {

    private static final String TAG = ConfigCompositionDataStatus.class.getSimpleName();

    private int companyIdentifier;
    private int productIdentifier;
    private int versionIdentifier;
    private int crpl;
    private int features;

    private boolean relayFeatureSupported;
    private boolean proxyFeatureSupported;
    private boolean friendFeatureSupported;
    private boolean lowPowerFeatureSupported;
    private int mUnicastAddress;

    private Map<Integer, Element> mElements = new LinkedHashMap<>();


    public ConfigCompositionDataStatus(final Context context, final ProvisionedMeshNode unprovisionedMeshNode,
                                       InternalTransportCallbacks transportCallbacks, final MeshConfigurationStatusCallbacks meshConfigurationStatusCallbacks) {
        super(context, unprovisionedMeshNode);
        this.mInternalTransportCallbacks = transportCallbacks;
        this.mConfigStatusCallbacks = meshConfigurationStatusCallbacks;
    }

    @Override
    public ConfigMessageState getState() {
        return ConfigMessageState.COMPOSITION_DATA_STATUS;
    }

    public void parseData(final byte[] pdu) {
        parseMessage(pdu);
    }

    private void parseMessage(final byte[] pdu) {
        final Message message = mMeshTransport.parsePdu(mSrc, pdu);
        if (message != null) {
            if (message instanceof AccessMessage) {
                final AccessMessage accessMessage = ((AccessMessage) message);
                final int opcode = accessMessage.getOpCode();//MeshParserUtils.getOpCode(accessPayload, opCodeLength);
                if (opcode == ConfigMessageOpCodes.CONFIG_COMPOSITION_DATA_STATUS) {
                    Log.v(TAG, "Received composition data status");
                    final int offset = +2; //Ignoring the opcode and the parameter received
                    pareCompositionDataPages(accessMessage, offset);
                    mProvisionedMeshNode.setCompositionData(this);
                    mConfigStatusCallbacks.onCompositionDataStatusReceived(mProvisionedMeshNode);
                    updateSavedProvisionedNode(mContext, mProvisionedMeshNode);
                } else {
                    mConfigStatusCallbacks.onUnknownPduReceived(mProvisionedMeshNode);
                }
            } else {
                parseControlMessage((ControlMessage) message);
            }
        }
    }

    /**
     * Parses the identifiers and the payloads
     *
     * @param message underlying access message
     * @param offset  offset to start parsing from
     */
    private void pareCompositionDataPages(final AccessMessage message, final int offset) {
        final byte[] accessPayload = message.getAccessPdu();

        //Bluetooth SIG 16-bit company identifier
        companyIdentifier = accessPayload[3] << 8 | accessPayload[2];
        Log.v(TAG, "Company identifier: " + String.format(Locale.US, "%04X", companyIdentifier));

        //16-bit vendor-assigned product identifier;
        productIdentifier = accessPayload[5] << 8 | accessPayload[4];
        Log.v(TAG, "Product identifier: " + String.format(Locale.US, "%04X", productIdentifier));

        //16-bit vendor-assigned product version identifier;
        versionIdentifier = accessPayload[7] << 8 | accessPayload[6];
        Log.v(TAG, "Version identifier: " + String.format(Locale.US, "%04X", versionIdentifier));

        //16-bit representation of the minimum number of replay protection list entries in a device
        crpl = accessPayload[9] << 8 | accessPayload[8];
        Log.v(TAG, "crpl: " + String.format(Locale.US, "%04X", crpl));

        //16-bit device features
        features = accessPayload[11] << 8 | accessPayload[10];
        Log.v(TAG, "Features: " + String.format(Locale.US, "%04X", features));

        relayFeatureSupported = DeviceFeatureUtils.supportsRelayFeature(features);
        Log.v(TAG, "Relay feature: " + relayFeatureSupported);

        proxyFeatureSupported = DeviceFeatureUtils.supportsProxyFeature(features);
        Log.v(TAG, "Proxy feature: " + proxyFeatureSupported);

        friendFeatureSupported = DeviceFeatureUtils.supportsFriendFeature(features);
        Log.v(TAG, "Friend feature: " + friendFeatureSupported);

        lowPowerFeatureSupported = DeviceFeatureUtils.supportsLowPowerFeature(features);
        Log.v(TAG, "Low power feature: " + lowPowerFeatureSupported);

        // Parsing the elements which is a variable number of octets
        // Elements contain following
        // location descriptor,
        // Number of SIG model IDs in this element
        // Number of vendor model in this element
        // SIG model ID octents - Variable
        // Vendor model ID octents - Variable
        parseElements(accessPayload, message.getSrc(), 12);
        Log.v(TAG, "Number of elements: " + mElements.size());
    }

    /**
     * Parses the elements
     *
     * @param accessPayload underlying payload containing the elements
     * @param src           source address
     * @param offset        offset within the payload
     */
    private void parseElements(final byte[] accessPayload, final byte[] src, final int offset) {
        int tempOffset = offset;
        int counter = 0;
        byte[] elementAddress = null;
        while (tempOffset < accessPayload.length) {
            final Map<Integer, MeshModel> models = new LinkedHashMap<>();
            final int locationDescriptor = accessPayload[tempOffset + 1] << 8 | accessPayload[tempOffset];
            Log.v(TAG, "Location identifier: " + String.format(Locale.US, "%04X", locationDescriptor));

            tempOffset = tempOffset + 2;
            final int numSigModelIds = accessPayload[tempOffset];//buffer.get();
            Log.v(TAG, "Number of sig models: " + String.format(Locale.US, "%04X", numSigModelIds));

            tempOffset = tempOffset + 1;
            final int numVendorModelIds = accessPayload[tempOffset];//buffer.get();
            Log.v(TAG, "Number of vendor models: " + String.format(Locale.US, "%04X", numVendorModelIds));

            tempOffset = tempOffset + 1;
            if (numSigModelIds > 0) {
                for (int i = 0; i < numSigModelIds; i++) {
                    final int modelId = accessPayload[tempOffset + 1] << 8 | accessPayload[tempOffset];
                    models.put(modelId, SigModelParser.getSigModel(modelId)); // sig models are 16-bit
                    Log.v(TAG, "Sig model ID " + i + " : " + String.format(Locale.US, "%04X", modelId));
                    tempOffset = tempOffset + 2;
                }
            }

            if (numVendorModelIds > 0) {
                for (int i = 0; i < numVendorModelIds; i++) {
                    // vendor models are 32-bit that contains a 16-bit company identifier and a 16-bit model identifier
                    final int modelIdentifier = accessPayload[tempOffset + 1] << 24 | accessPayload[tempOffset] << 16 |
                            accessPayload[tempOffset + 3] << 8 | accessPayload[tempOffset + 2];
                    models.put(modelIdentifier, new VendorModel(modelIdentifier));
                    Log.v(TAG, "Vendor - model ID " + i + " : " + String.format(Locale.US, "%08X", modelIdentifier));
                    tempOffset = tempOffset + 4;
                }
            }

            if (counter == 0) {
                elementAddress = src;
            } else {
                elementAddress = AddressUtils.incrementUnicastAddress(elementAddress);
            }
            counter++;
            final Element element = new Element(elementAddress, locationDescriptor, numSigModelIds, numVendorModelIds, models);
            final int unicastAddress = AddressUtils.getUnicastAddressInt(elementAddress);
            mElements.put(unicastAddress, element);
            mUnicastAddress = unicastAddress;
        }
    }


    public int getCompanyIdentifier() {
        return companyIdentifier;
    }

    public int getProductIdentifier() {
        return productIdentifier;
    }

    public int getVersionIdentifier() {
        return versionIdentifier;
    }

    public int getCrpl() {
        return crpl;
    }

    public int getFeatures() {
        return features;
    }

    public boolean isRelayFeatureSupported() {
        return relayFeatureSupported;
    }

    public boolean isProxyFeatureSupported() {
        return proxyFeatureSupported;
    }

    public boolean isFriendFeatureSupported() {
        return friendFeatureSupported;
    }

    public boolean isLowPowerFeatureSupported() {
        return lowPowerFeatureSupported;
    }

    public Map<Integer, Element> getElements() {
        return mElements;
    }

    /**
     * Returns the unicast address
     *
     * @return unicast address
     */
    public int getUnicastAddress() {
        return mUnicastAddress;
    }

    @Override
    public final void sendSegmentAcknowledgementMessage(final ControlMessage controlMessage) {
        final ControlMessage message = mMeshTransport.createSegmentBlockAcknowledgementMessage(controlMessage);
        Log.v(TAG, "Sending acknowledgement: " + MeshParserUtils.bytesToHex(message.getNetworkPdu().get(0), false));
        mInternalTransportCallbacks.sendPdu(mProvisionedMeshNode, message.getNetworkPdu().get(0));
        mConfigStatusCallbacks.onBlockAcknowledgementSent(mProvisionedMeshNode);
    }

    private int parseCompanyIdentifier(final short companyIdentifier) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(companyIdentifier).getShort(0);
    }

    private int parseProductIdentifier(final short productIdentifier) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(productIdentifier).getShort(0);
    }

    private int parseVersionIdentifier(final short versionIdentifier) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(versionIdentifier).getShort(0);
    }

    private int parseCrpl(final short companyIdentifier) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(companyIdentifier).getShort(0);
    }

    private int parseFeatures(final short companyIdentifier) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(companyIdentifier).getShort(0);
    }
}
