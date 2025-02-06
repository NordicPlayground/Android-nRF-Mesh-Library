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

package no.nordicsemi.android.mesh.transport;

import static androidx.room.ForeignKey.CASCADE;

import android.annotation.SuppressLint;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.Features;
import no.nordicsemi.android.mesh.MeshNetwork;
import no.nordicsemi.android.mesh.NetworkKey;
import no.nordicsemi.android.mesh.NodeKey;
import no.nordicsemi.android.mesh.Provisioner;
import no.nordicsemi.android.mesh.models.ConfigurationServerModel;
import no.nordicsemi.android.mesh.models.SigModelParser;
import no.nordicsemi.android.mesh.provisionerstates.UnprovisionedMeshNode;
import no.nordicsemi.android.mesh.utils.MeshParserUtils;
import no.nordicsemi.android.mesh.utils.NetworkTransmitSettings;
import no.nordicsemi.android.mesh.utils.RelaySettings;
import no.nordicsemi.android.mesh.utils.SecureUtils;
import no.nordicsemi.android.mesh.utils.SparseIntArrayParcelable;

@SuppressWarnings({"WeakerAccess"})
@Entity(tableName = "nodes",
        foreignKeys = @ForeignKey(entity = MeshNetwork.class,
                parentColumns = "mesh_uuid",
                childColumns = "mesh_uuid",
                onUpdate = CASCADE, onDelete = CASCADE),
        indices = @Index("mesh_uuid"))
public final class ProvisionedMeshNode extends ProvisionedBaseMeshNode {

    public static final Creator<ProvisionedMeshNode> CREATOR = new Creator<ProvisionedMeshNode>() {
        @Override
        public ProvisionedMeshNode createFromParcel(Parcel in) {
            return new ProvisionedMeshNode(in);
        }

        @Override
        public ProvisionedMeshNode[] newArray(int size) {
            return new ProvisionedMeshNode[size];
        }
    };

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public ProvisionedMeshNode() {
    }

    /**
     * Constructor to be used only by hte library
     *
     * @param node {@link UnprovisionedMeshNode}
     */
    @Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public ProvisionedMeshNode(final UnprovisionedMeshNode node) {
        uuid = node.getDeviceUuid().toString();
        //isConfigured = node.isConfigured();
        nodeName = node.getNodeName();
        mAddedNetKeys.add(new NodeKey(node.getKeyIndex()));
        mFlags = node.getFlags();
        unicastAddress = node.getUnicastAddress();
        deviceKey = node.getDeviceKey();
        ttl = node.getTtl();
        mTimeStampInMillis = node.getTimeStamp();
        // Here we add some dummy elements with empty models to occupy the addresses in use.
        for (int i = 0; i < node.getProvisioningCapabilities().getNumberOfElements(); i++) {
            mElements.put(unicastAddress + i, new Element(unicastAddress + i, 0, new HashMap<>()));
        }
        security = node.isSecure() ? HIGH : LOW;
    }

    /**
     * Constructor to be used only by the library
     *
     * @param provisioner {@link Provisioner}
     * @param netKeys     List of {@link NetworkKey}
     * @param appKeys     List of {@link ApplicationKey}
     */
    @Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @SuppressLint("UseSparseArrays")
    public ProvisionedMeshNode(@NonNull final Provisioner provisioner,
                               @NonNull final List<NetworkKey> netKeys,
                               @NonNull final List<ApplicationKey> appKeys) {
        this.meshUuid = provisioner.getMeshUuid();
        uuid = provisioner.getProvisionerUuid();
        //isConfigured = true;
        nodeName = provisioner.getProvisionerName();
        for (NetworkKey key : netKeys) {
            mAddedNetKeys.add(new NodeKey(key.getKeyIndex(), false));
        }
        for (ApplicationKey key : appKeys) {
            mAddedAppKeys.add(new NodeKey(key.getKeyIndex(), false));
        }
        if (provisioner.getProvisionerAddress() != null)
            unicastAddress = provisioner.getProvisionerAddress();
        sequenceNumber = 0;
        deviceKey = SecureUtils.generateRandomNumber();
        ttl = provisioner.getGlobalTtl();
        mTimeStampInMillis = System.currentTimeMillis();
        final MeshModel model = SigModelParser.getSigModel(SigModelParser.CONFIGURATION_CLIENT);
        final HashMap<Integer, MeshModel> models = new HashMap<>();
        models.put(model.getModelId(), model);
        final Element element = new Element(unicastAddress, 0, models);
        final HashMap<Integer, Element> elements = new HashMap<>();
        elements.put(unicastAddress, element);
        mElements = elements;
        nodeFeatures = new Features(Features.UNSUPPORTED, Features.UNSUPPORTED, Features.UNSUPPORTED, Features.UNSUPPORTED);
    }

    @Ignore
    ProvisionedMeshNode(Parcel in) {
        //noinspection ConstantConditions
        uuid = in.readString();
        isConfigured = in.readByte() != 1;
        nodeName = in.readString();
        in.readList(mAddedNetKeys, NodeKey.class.getClassLoader());
        mFlags = in.createByteArray();
        unicastAddress = in.readInt();
        deviceKey = in.createByteArray();
        ttl = (Integer) in.readValue(Integer.class.getClassLoader());
        sequenceNumber = in.readInt();
        companyIdentifier = (Integer) in.readValue(Integer.class.getClassLoader());
        productIdentifier = (Integer) in.readValue(Integer.class.getClassLoader());
        versionIdentifier = (Integer) in.readValue(Integer.class.getClassLoader());
        crpl = (Integer) in.readValue(Integer.class.getClassLoader());
        nodeFeatures = (Features) in.readValue(Features.class.getClassLoader());
        in.readMap(mElements, Element.class.getClassLoader());
        sortElements(mElements);
        in.readList(mAddedAppKeys, NodeKey.class.getClassLoader());
        mTimeStampInMillis = in.readLong();
        mSeqAuth = in.readParcelable(SparseIntArrayParcelable.class.getClassLoader());
        secureNetworkBeaconSupported = (Boolean) in.readValue(Boolean.class.getClassLoader());
        networkTransmitSettings = in.readParcelable(NetworkTransmitSettings.class.getClassLoader());
        relaySettings = in.readParcelable(RelaySettings.class.getClassLoader());
        excluded = in.readInt() != 1;

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeByte((byte) (isConfigured ? 1 : 0));
        dest.writeString(nodeName);
        dest.writeList(mAddedNetKeys);
        dest.writeByteArray(mFlags);
        dest.writeInt(unicastAddress);
        dest.writeByteArray(deviceKey);
        dest.writeValue(ttl);
        dest.writeInt(sequenceNumber);
        dest.writeValue(companyIdentifier);
        dest.writeValue(productIdentifier);
        dest.writeValue(versionIdentifier);
        dest.writeValue(crpl);
        dest.writeValue(nodeFeatures);
        dest.writeMap(mElements);
        dest.writeList(mAddedAppKeys);
        dest.writeLong(mTimeStampInMillis);
        dest.writeParcelable(mSeqAuth, flags);
        dest.writeValue(secureNetworkBeaconSupported);
        dest.writeParcelable(networkTransmitSettings, flags);
        dest.writeParcelable(relaySettings, flags);
        dest.writeInt((excluded ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Map<Integer, Element> getElements() {
        return mElements;
    }

    /**
     * Check if an unicast address is the address of an element
     *
     * @param unicastAddress the address to check
     * @return if this address is the address of an element
     */
    public boolean hasUnicastAddress(final int unicastAddress) {
        if (unicastAddress == getUnicastAddress())
            return true;
        for (Element element : mElements.values()) {
            if (element.getElementAddress() == unicastAddress)
                return true;
        }
        return false;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setElements(final Map<Integer, Element> elements) {
        mElements = elements;
    }

    public byte[] getDeviceKey() {
        return deviceKey;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setDeviceKey(final byte[] deviceKey) {
        this.deviceKey = deviceKey;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the sequence number
     * <p>
     * This is only meant to be used internally within the library.
     * However this is open now for users to set the sequence number manually in provisioner node.
     * </p>
     *
     * @param sequenceNumber sequence number of the node
     */
    public void setSequenceNumber(final int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Integer getCompanyIdentifier() {
        return companyIdentifier;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setCompanyIdentifier(final Integer companyIdentifier) {
        this.companyIdentifier = companyIdentifier;
    }

    public Integer getProductIdentifier() {
        return productIdentifier;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setProductIdentifier(final Integer productIdentifier) {
        this.productIdentifier = productIdentifier;
    }

    public Integer getVersionIdentifier() {
        return versionIdentifier;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setVersionIdentifier(final Integer versionIdentifier) {
        this.versionIdentifier = versionIdentifier;
    }

    public Integer getCrpl() {
        return crpl;
    }

    public void setCrpl(final Integer crpl) {
        this.crpl = crpl;
    }

    /**
     * Returns the {@link Features} of the node
     */
    public Features getNodeFeatures() {
        return nodeFeatures;
    }

    /**
     * Set {@link Features} of the node
     *
     * @param features feature set supported by the node
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setNodeFeatures(final Features features) {
        this.nodeFeatures = features;
    }

    /**
     * Returns the list of Network keys added to this node
     */
    public List<NodeKey> getAddedNetKeys() {
        return Collections.unmodifiableList(mAddedNetKeys);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setAddedNetKeys(final List<NodeKey> addedNetKeyIndexes) {
        mAddedNetKeys = addedNetKeyIndexes;
    }

    /**
     * Adds a NetKey index that was added to the node
     *
     * @param index NetKey index
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setAddedNetKeyIndex(final int index) {
        if (!MeshParserUtils.isNodeKeyExists(mAddedNetKeys, index)) {
            mAddedNetKeys.add(new NodeKey(index));
        }
    }

    /**
     * Update a net key's updated state
     *
     * @param index NetKey index
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void updateAddedNetKey(final int index) {
        final NodeKey nodeKey = MeshParserUtils.getNodeKey(mAddedNetKeys, index);
        if (nodeKey != null) {
            nodeKey.setUpdated(true);
        }
    }

    /**
     * Update the added net key list of the node
     *
     * @param indexes NetKey index
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void updateNetKeyList(final List<Integer> indexes) {
        mAddedNetKeys.clear();
        for (Integer index : indexes) {
            mAddedNetKeys.add(new NodeKey(index, false));
        }
    }

    /**
     * Removes an NetKey index that was added to the node
     *
     * @param index NetKey index
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void removeAddedNetKeyIndex(final int index) {
        for (int i = 0; i < mAddedNetKeys.size(); i++) {
            final int keyIndex = mAddedNetKeys.get(i).getIndex();
            if (keyIndex == index) {
                mAddedNetKeys.remove(i);
                for (Element element : mElements.values()) {
                    for (MeshModel model : element.getMeshModels().values()) {
                        if (model.getModelId() == SigModelParser.CONFIGURATION_SERVER) {
                            final ConfigurationServerModel configServerModel = (ConfigurationServerModel) model;
                            if (configServerModel.getHeartbeatPublication() != null &&
                                    configServerModel.getHeartbeatPublication().getNetKeyIndex() == index) {
                                configServerModel.setHeartbeatPublication(null);
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    /**
     * Returns the list of added AppKey indexes to the node
     */
    public List<NodeKey> getAddedAppKeys() {
        return mAddedAppKeys;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setAddedAppKeys(final List<NodeKey> addedAppKeyIndexes) {
        mAddedAppKeys = addedAppKeyIndexes;
    }

    /**
     * Adds an AppKey index that was added to the node
     *
     * @param index AppKey index
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setAddedAppKeyIndex(final int index) {
        if (!MeshParserUtils.isNodeKeyExists(mAddedAppKeys, index)) {
            this.mAddedAppKeys.add(new NodeKey(index));
        }
    }

    /**
     * Update an app key's updated state
     *
     * @param index AppKey index
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void updateAddedAppKey(final int index) {
        final NodeKey nodeKey = MeshParserUtils.getNodeKey(mAddedNetKeys, index);
        if (nodeKey != null) {
            nodeKey.setUpdated(true);
        }
    }

    /**
     * Update the added net key list of the node
     *
     * @param netKeyIndex NetKey Index
     * @param indexes     AppKey indexes
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void updateAppKeyList(final int netKeyIndex, @NonNull final List<Integer> indexes, @NonNull final List<ApplicationKey> keyIndexes) {
        if (mAddedAppKeys.isEmpty()) {
            mAddedAppKeys.addAll(addAppKeyList(indexes, new ArrayList<>()));
        } else {
            final ArrayList<NodeKey> tempList = new ArrayList<>(mAddedAppKeys);
            for (ApplicationKey applicationKey : keyIndexes) {
                if (applicationKey.getBoundNetKeyIndex() == netKeyIndex) {
                    for (NodeKey nodeKey : mAddedAppKeys) {
                        if (nodeKey.getIndex() == applicationKey.getKeyIndex()) {
                            tempList.remove(nodeKey);
                        }
                    }
                }
            }
            mAddedAppKeys.clear();
            addAppKeyList(indexes, tempList);
            mAddedAppKeys.addAll(tempList);
        }
    }

    private List<NodeKey> addAppKeyList(@NonNull final List<Integer> indexes, @NonNull final ArrayList<NodeKey> tempList) {
        for (Integer index : indexes) {
            tempList.add(new NodeKey(index, false));
        }
        return tempList;
    }

    /**
     * Removes an AppKey index that was added to the node
     *
     * @param index AppKey index
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void removeAddedAppKeyIndex(final int index) {
        for (int i = 0; i < mAddedAppKeys.size(); i++) {
            final int keyIndex = mAddedAppKeys.get(i).getIndex();
            if (keyIndex == index) {
                mAddedAppKeys.remove(i);
                for (Map.Entry<Integer, Element> elementEntry : getElements().entrySet()) {
                    final Element element = elementEntry.getValue();
                    for (Map.Entry<Integer, MeshModel> modelEntry : element.getMeshModels().entrySet()) {
                        final MeshModel model = modelEntry.getValue();
                        if (model != null) {
                            for (int j = 0; j < model.getBoundAppKeyIndexes().size(); j++) {
                                final int boundKeyIndex = model.getBoundAppKeyIndexes().get(j);
                                if (boundKeyIndex == index) {
                                    model.mBoundAppKeyIndexes.remove(j);
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    /**
     * Sets the data from the {@link ConfigCompositionDataStatus}
     *
     * @param configCompositionDataStatus Composition data status object
     */
    void setCompositionData(
            @NonNull final ConfigCompositionDataStatus configCompositionDataStatus) {
        companyIdentifier = configCompositionDataStatus.getCompanyIdentifier();
        productIdentifier = configCompositionDataStatus.getProductIdentifier();
        versionIdentifier = configCompositionDataStatus.getVersionIdentifier();
        crpl = configCompositionDataStatus.getCrpl();
        final boolean relayFeatureSupported = configCompositionDataStatus.isRelayFeatureSupported();
        final boolean proxyFeatureSupported = configCompositionDataStatus.isProxyFeatureSupported();
        final boolean friendFeatureSupported = configCompositionDataStatus.isFriendFeatureSupported();
        final boolean lowPowerFeatureSupported = configCompositionDataStatus.isLowPowerFeatureSupported();
        nodeFeatures = new Features(friendFeatureSupported ? Features.DISABLED : Features.UNSUPPORTED,
                lowPowerFeatureSupported ? Features.DISABLED : Features.UNSUPPORTED,
                proxyFeatureSupported ? Features.DISABLED : Features.UNSUPPORTED,
                relayFeatureSupported ? Features.DISABLED : Features.UNSUPPORTED);

        LinkedHashMap<Integer, Element> modifiableNewElements = new LinkedHashMap<>(configCompositionDataStatus.getElements());

        // For each old element
        for (Map.Entry<Integer, Element> newElementEntry : configCompositionDataStatus.getElements().entrySet()) {
            final Element newElement = newElementEntry.getValue();
            // Fetch same element in new composition data
            final Element oldElement = mElements.get(newElementEntry.getKey());

            LinkedHashMap<Integer, MeshModel> updatedElementModels = new LinkedHashMap<>(newElement.getMeshModels());

            // Old element existed previously, may have same mesh models
            if (oldElement != null) {
                // For each model in that element
                for (Map.Entry<Integer, MeshModel> modelEntry : newElement.getMeshModels().entrySet()) {

                    // Override new element models with old to ensure we keep mesh model info
                    if (oldElement.getMeshModels().containsKey(modelEntry.getKey())) {
                        updatedElementModels.put(modelEntry.getKey(), oldElement.meshModels.get(modelEntry.getKey()));
                    }
                }

                Element newUpdatedElement = new Element(newElement.elementAddress, newElement.locationDescriptor, updatedElementModels, newElement.name);

                modifiableNewElements.put(newElementEntry.getKey(), newUpdatedElement);
            }
        }
        mElements.putAll(modifiableNewElements);
    }

    /**
     * Sets the bound app key data from the {@link ConfigModelAppStatus}
     *
     * @param configModelAppStatus ConfigModelAppStatus containing the bound app key information
     */
    void setAppKeyBindStatus(
            @NonNull final ConfigModelAppStatus configModelAppStatus) {
        if (configModelAppStatus.isSuccessful()) {
            final Element element = mElements.get(configModelAppStatus.getElementAddress());
            if (element != null) {
                final int modelIdentifier = configModelAppStatus.getModelIdentifier();
                final MeshModel model = element.getMeshModels().get(modelIdentifier);
                if (model != null) {
                    final int appKeyIndex = configModelAppStatus.getAppKeyIndex();
                    model.setBoundAppKeyIndex(appKeyIndex);
                }
            }
        }
    }

    /**
     * Sets the unbind app key data from the {@link ConfigModelAppStatus}
     *
     * @param configModelAppStatus ConfigModelAppStatus containing the unbound app key information
     */
    void setAppKeyUnbindStatus(
            @NonNull final ConfigModelAppStatus configModelAppStatus) {
        if (configModelAppStatus.isSuccessful()) {
            final Element element = mElements.get(configModelAppStatus.getElementAddress());
            if (element != null) {
                final int modelIdentifier = configModelAppStatus.getModelIdentifier();
                final MeshModel model = element.getMeshModels().get(modelIdentifier);
                final int appKeyIndex = configModelAppStatus.getAppKeyIndex();
                if (model != null) {
                    model.removeBoundAppKeyIndex(appKeyIndex);
                }
            }
        }
    }

    private void sortElements(final Map<Integer, Element> unorderedElements) {
        final Set<Integer> unorderedKeys = unorderedElements.keySet();

        final List<Integer> orderedKeys = new ArrayList<>(unorderedKeys);
        Collections.sort(orderedKeys);
        for (int key : orderedKeys) {
            mElements.put(key, unorderedElements.get(key));
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setSeqAuth(final int src, final int seqAuth) {
        mSeqAuth.put(src, seqAuth);
    }

    public Integer getSeqAuth(final int src) {
        if (mSeqAuth.size() == 0) {
            return null;
        }

        return mSeqAuth.get(src);
    }

    public boolean isExist(final int modelId) {
        for (Map.Entry<Integer, Element> elementEntry : mElements.entrySet()) {
            final Element element = elementEntry.getValue();
            for (Map.Entry<Integer, MeshModel> modelEntry : element.getMeshModels().entrySet()) {
                final MeshModel model = modelEntry.getValue();
                if (model != null && model.getModelId() == modelId) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Increments the sequence number
     */
    public int incrementSequenceNumber() {
        return sequenceNumber = sequenceNumber + 1;
    }
}
