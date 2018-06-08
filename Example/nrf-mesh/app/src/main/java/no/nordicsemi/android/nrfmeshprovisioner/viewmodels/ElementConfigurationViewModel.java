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

package no.nordicsemi.android.nrfmeshprovisioner.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import no.nordicsemi.android.meshprovisioner.ProvisioningSettings;
import no.nordicsemi.android.meshprovisioner.configuration.ProvisionedMeshNode;
import no.nordicsemi.android.nrfmeshprovisioner.livedata.ExtendedMeshNode;
import no.nordicsemi.android.nrfmeshprovisioner.livedata.ProvisioningLiveData;
import no.nordicsemi.android.nrfmeshprovisioner.livedata.ProvisioningStateLiveData;
import no.nordicsemi.android.nrfmeshprovisioner.repository.ElementConfigurationRepository;

public class ElementConfigurationViewModel extends ViewModel {


    private final ElementConfigurationRepository mElementConfigurationRepository;

    @Inject
    ElementConfigurationViewModel(final ElementConfigurationRepository elementConfigurationRepository) {
        super();
        this.mElementConfigurationRepository = elementConfigurationRepository;
        mElementConfigurationRepository.registerBroadcastReceiver();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mElementConfigurationRepository.unregisterBroadcastReceiver();
        mElementConfigurationRepository.unbindService();
    }

    public ExtendedMeshNode getExtendedMeshNode(){
        return mElementConfigurationRepository.getExtendedMeshNode();
    }

    public LiveData<Boolean> isConnected() {
        return mElementConfigurationRepository.isConnected();
    }

    public void setMeshNode(final ProvisionedMeshNode node) {
        mElementConfigurationRepository.setMeshNode(node);
    }

    public ProvisioningStateLiveData getProvisioningState() {
        return mElementConfigurationRepository.getProvisioningState();
    }

    /**
     * Returns the Element Configuration repository
     * @return repository for configuring elements
     */
    public ElementConfigurationRepository getElementConfigurationRepository() {
        return mElementConfigurationRepository;
    }

    public void sendGetCompositionData() {
        mElementConfigurationRepository.sendGetCompositionData();
    }

    public ProvisioningLiveData getProvisioningData() {
        return mElementConfigurationRepository.getProvisioningData();
    }

    public void setSelectedAppKey(final int appKeyIndex, final String appkey) {
        mElementConfigurationRepository.setSelectedAppKey(appKeyIndex, appkey);
    }

    public void sendAppKeyAdd(final int appKeyIndex, final String appKey) {
        mElementConfigurationRepository.addAppKey(appKeyIndex, appKey);
    }
}
