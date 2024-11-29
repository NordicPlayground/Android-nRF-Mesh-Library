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

package no.nordicsemi.android.mesh;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.transport.ControlMessage;
import no.nordicsemi.android.mesh.transport.MeshMessage;

/**
 * Callbacks to notify the status of the mesh messages
 */
public interface MeshStatusCallbacks {

    /**
     * Notifies if a transaction has failed
     * <p>
     * As of now this is only triggered if the incomplete timer has expired for a given segmented message.
     * The incomplete timer will wait for a minimum of 10 seconds on receiving a segmented message.
     * If all segments are not received during this period, that transaction shall be considered as failed.
     * </p>
     *
     * @param dst                       Unique dst address of the device
     * @param hasIncompleteTimerExpired Flag that notifies if the incomplete timer had expired
     */
    void onTransactionFailed(final int dst, final boolean hasIncompleteTimerExpired);

    /**
     * Notifies if an unknown pdu was received
     *
     * @param src           Address where the message originated from
     * @param accessPayload Access payload of the message
     */
    void onUnknownPduReceived(final int src, final byte[] accessPayload);

    /**
     * Notifies when a block acknowledgement has been processed
     *
     * <p>
     * This callback is invoked after {@link MeshManagerCallbacks#onMeshPduCreated(byte[])} where a mesh pdu is created.
     * </p>
     *
     * @param dst     Destination address to which the block ack was sent
     * @param message Control message containing the block acknowledgement
     */
    void onBlockAcknowledgementProcessed(final int dst, @NonNull final ControlMessage message);

    /**
     * Notifies if a block acknowledgement was received
     *
     * @param src     Source address from which the block ack was received
     * @param message Control message containing the block acknowledgement
     */
    void onBlockAcknowledgementReceived(final int src, @NonNull final ControlMessage message);

    /**
     * Notifies if a heartbeat message was received
     *
     * @param src     Source address from which the heartbeat message was received
     * @param message Control message containing the heartbeat message
     */
    void onHeartbeatMessageReceived(final int src, @NonNull final ControlMessage message);

    /**
     * Callback to notify the mesh message has been processed to be sent to the bearer
     *
     * <p>
     * This callback is invoked after {@link MeshManagerCallbacks#onMeshPduCreated(byte[])} where
     * a mesh pdu is created and is ready to be sent.
     * </p>
     *
     * @param dst         Destination address to be sent
     * @param meshMessage {@link MeshMessage} containing the message that was sent
     */
    void onMeshMessageProcessed(final int dst, @NonNull final MeshMessage meshMessage);

    /**
     * Callback to notify that a mesh status message was received from the bearer
     *
     * @param src         Source address where the message originated from
     * @param meshMessage {@link MeshMessage} containing the message that was received
     */
    void onMeshMessageReceived(final int src, @NonNull final MeshMessage meshMessage);

    /**
     * Callback to notify if the decryption failed of a received mesh message
     *
     * @param meshLayer    Mesh layer name
     * @param errorMessage Error message
     */
    void onMessageDecryptionFailed(final String meshLayer, final String errorMessage);
}
