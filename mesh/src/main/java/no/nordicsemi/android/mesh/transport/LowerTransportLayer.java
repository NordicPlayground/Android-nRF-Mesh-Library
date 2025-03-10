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

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import no.nordicsemi.android.mesh.MeshManagerApi;
import no.nordicsemi.android.mesh.control.BlockAcknowledgementMessage;
import no.nordicsemi.android.mesh.control.HeartbeatMessage;
import no.nordicsemi.android.mesh.logger.MeshLogger;
import no.nordicsemi.android.mesh.opcodes.TransportLayerOpCodes;
import no.nordicsemi.android.mesh.utils.ExtendedInvalidCipherTextException;
import no.nordicsemi.android.mesh.utils.MeshAddress;
import no.nordicsemi.android.mesh.utils.MeshParserUtils;

/**
 * LowerTransportLayer implementation of the mesh network architecture as per the mesh profile specification.
 * <p>
 * This class generates the messages as per the lower transport layer requirements, segmentation and reassembly of mesh messages sent and received,
 * retransmitting messages.
 * </p>
 */
abstract class LowerTransportLayer extends UpperTransportLayer {

    private static final String TAG = LowerTransportLayer.class.getSimpleName();
    private static final int BLOCK_ACK_TIMER = 150; //Increased from minimum value 150;
    private static final int UNSEGMENTED_HEADER = 0;
    private static final int SEGMENTED_HEADER = 1;
    private static final int UNSEGMENTED_MESSAGE_HEADER_LENGTH = 1;
    private static final int SEGMENTED_MESSAGE_HEADER_LENGTH = 4;
    private static final int UNSEGMENTED_ACK_MESSAGE_HEADER_LENGTH = 3;
    private static final long INCOMPLETE_TIMER_DELAY = 10 * 1000; // According to the spec the incomplete timer must be a minimum of 10 seconds.

    private final SparseArray<byte[]> segmentedAccessMessageMap = new SparseArray<>();
    private final SparseArray<byte[]> segmentedControlMessageMap = new SparseArray<>();
    LowerTransportLayerCallbacks mLowerTransportLayerCallbacks;
    private boolean mSegmentedAccessAcknowledgementTimerStarted;
    private Integer mSegmentedAccessBlockAck;
    private boolean mSegmentedControlAcknowledgementTimerStarted;
    private Integer mSegmentedControlBlockAck;
    private boolean mIncompleteTimerStarted;
    /**
     * Runnable for incomplete timer
     */
    private final Runnable mIncompleteTimerRunnable = new Runnable() {
        @Override
        public void run() {
            mLowerTransportLayerCallbacks.onIncompleteTimerExpired();
            //Reset the incomplete timer flag once it expires
            mIncompleteTimerStarted = false;
        }
    };
    private boolean mBlockAckSent;
    private long mDuration;

    /**
     * Sets the lower transport layer callbacks
     *
     * @param callbacks {@link LowerTransportLayerCallbacks} callbacks
     */
    abstract void setLowerTransportLayerCallbacks(@NonNull final LowerTransportLayerCallbacks callbacks);

    /**
     * Creates the network layer pdu
     *
     * @param message message with underlying data
     * @return Complete pdu message that is ready to be sent
     */
    protected abstract Message createNetworkLayerPDU(@NonNull final Message message);

    @Override
    void createMeshMessage(@NonNull final Message message) {
        super.createMeshMessage(message);
        if (message instanceof AccessMessage) {
            createLowerTransportAccessPDU((AccessMessage) message);
        } else {
            createLowerTransportControlPDU((ControlMessage) message);
        }
    }

    @Override
    void createVendorMeshMessage(@NonNull final Message message) {
        if (message instanceof AccessMessage) {
            super.createVendorMeshMessage(message);
            createLowerTransportAccessPDU((AccessMessage) message);
        } else {
            createLowerTransportControlPDU((ControlMessage) message);
        }
    }

    @Override
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public final void createLowerTransportAccessPDU(@NonNull final AccessMessage message) {
        final byte[] upperTransportPDU = message.getUpperTransportPdu();
        final SparseArray<byte[]> lowerTransportAccessPduMap;
        if (upperTransportPDU.length <= MAX_UNSEGMENTED_ACCESS_PAYLOAD_LENGTH) {
            message.setSegmented(false);
            final byte[] lowerTransportPDU = createUnsegmentedAccessMessage(message);
            lowerTransportAccessPduMap = new SparseArray<>();
            lowerTransportAccessPduMap.put(0, lowerTransportPDU);
        } else {
            message.setSegmented(true);
            lowerTransportAccessPduMap = createSegmentedAccessMessage(message);
        }

        message.setLowerTransportAccessPdu(lowerTransportAccessPduMap);
    }

    @Override
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public final void createLowerTransportControlPDU(@NonNull final ControlMessage message) {
        switch (message.getPduType()) {
            case MeshManagerApi.PDU_TYPE_PROXY_CONFIGURATION:
                final SparseArray<byte[]> lowerTransportControlPduArray = new SparseArray<>();
                lowerTransportControlPduArray.put(0, message.getTransportControlPdu());
                message.setLowerTransportControlPdu(lowerTransportControlPduArray);
                break;
            case MeshManagerApi.PDU_TYPE_NETWORK:
                final byte[] transportControlPdu = message.getTransportControlPdu();
                if (transportControlPdu.length <= MAX_UNSEGMENTED_CONTROL_PAYLOAD_LENGTH) {
                    MeshLogger.verbose(TAG, "Creating unsegmented transport control");
                    createUnsegmentedControlMessage(message);
                } else {
                    MeshLogger.verbose(TAG, "Creating segmented transport control");
                    createSegmentedControlMessage(message);
                }
        }
    }

    @Override
    final void reassembleLowerTransportAccessPDU(@NonNull final AccessMessage accessMessage) {
        final SparseArray<byte[]> lowerTransportAccessPdu = removeLowerTransportAccessMessageHeader(accessMessage);
        final byte[] upperTransportPdu = MeshParserUtils.concatenateSegmentedMessages(lowerTransportAccessPdu);
        accessMessage.setUpperTransportPdu(upperTransportPdu);
    }

    @Override
    final void reassembleLowerTransportControlPDU(@NonNull final ControlMessage controlMessage) {
        final SparseArray<byte[]> lowerTransportPdu = removeLowerTransportControlMessageHeader(controlMessage);
        final byte[] lowerTransportControlPdu = MeshParserUtils.concatenateSegmentedMessages(lowerTransportPdu);
        controlMessage.setTransportControlPdu(lowerTransportControlPdu);
    }

    /**
     * Removes the transport header of the access message.
     *
     * @param message access message received.
     * @return map containing the messages.
     */
    private SparseArray<byte[]> removeLowerTransportAccessMessageHeader(@NonNull final AccessMessage message) {
        final SparseArray<byte[]> messages = message.getLowerTransportAccessPdu();
        if (message.isSegmented()) {
            for (int i = 0; i < messages.size(); i++) {
                final byte[] data = messages.get(i);
                final int length = data.length - SEGMENTED_MESSAGE_HEADER_LENGTH; //header size of unsegmented messages is 4;
                messages.put(i, removeHeader(data, 4, length));
            }
        } else {
            final byte[] data = messages.get(0);
            final int length = data.length - UNSEGMENTED_MESSAGE_HEADER_LENGTH; //header size of unsegmented messages is 1;
            messages.put(0, removeHeader(data, 1, length));
        }
        return messages;
    }

    /**
     * Removes the transport header of the control message.
     *
     * @param message control message.
     * @return map containing the messages.
     */
    private SparseArray<byte[]> removeLowerTransportControlMessageHeader(@NonNull final ControlMessage message) {
        final SparseArray<byte[]> messages = message.getLowerTransportControlPdu();
        if (messages.size() > 1) {
            for (int i = 0; i < messages.size(); i++) {
                final byte[] data = messages.get(i);
                final int length = data.length - SEGMENTED_MESSAGE_HEADER_LENGTH; //header size of unsegmented messages is 4;
                messages.put(i, removeHeader(data, 4, length));
            }
        } else {
            final int opCode = message.getOpCode();
            final byte[] data;
            final int length;
            if (opCode == TransportLayerOpCodes.SAR_ACK_OPCODE) {
                data = messages.get(0);
                length = data.length - UNSEGMENTED_ACK_MESSAGE_HEADER_LENGTH; //header size of unsegmented acknowledgement messages is 3;
                messages.put(0, removeHeader(data, UNSEGMENTED_ACK_MESSAGE_HEADER_LENGTH, length));
            } else {
                data = messages.get(0);
                length = data.length - UNSEGMENTED_MESSAGE_HEADER_LENGTH; //header size of unsegmented messages is 1;
                messages.put(0, removeHeader(data, UNSEGMENTED_MESSAGE_HEADER_LENGTH, length));
            }
        }
        return messages;
    }

    /**
     * Removes the header from a given array.
     *
     * @param data   message.
     * @param offset header offset.
     * @param length header length.
     * @return an array without the header.
     */
    private byte[] removeHeader(@NonNull final byte[] data, final int offset, final int length) {
        final ByteBuffer buffer = ByteBuffer.allocate(length).order(ByteOrder.BIG_ENDIAN);
        buffer.put(data, offset, length);
        return buffer.array();
    }

    /**
     * Creates an unsegmented access message.
     *
     * @param message access message.
     * @return Unsegmented access message.
     */
    private byte[] createUnsegmentedAccessMessage(@NonNull final AccessMessage message) {
        final byte[] encryptedUpperTransportPDU = message.getUpperTransportPdu();
        final int seg = message.isSegmented() ? 1 : 0;
        final int akfAid = ((message.getAkf() << 6) | message.getAid());
        final byte header = (byte) (((seg << 7) | akfAid));
        final ByteBuffer lowerTransportBuffer = ByteBuffer.allocate(1 + encryptedUpperTransportPDU.length).order(ByteOrder.BIG_ENDIAN);
        lowerTransportBuffer.put(header);
        lowerTransportBuffer.put(encryptedUpperTransportPDU);
        final byte[] lowerTransportPDU = lowerTransportBuffer.array();
        MeshLogger.verbose(TAG, "Unsegmented Lower transport access PDU " + MeshParserUtils.bytesToHex(lowerTransportPDU, false));
        return lowerTransportPDU;
    }

    /**
     * Creates a segmented access message.
     *
     * @param message access message.
     * @return Segmented access message.
     */
    private SparseArray<byte[]> createSegmentedAccessMessage(@NonNull final AccessMessage message) {
        final byte[] encryptedUpperTransportPDU = message.getUpperTransportPdu();
        final int akfAid = ((message.getAkf() << 6) | message.getAid());
        final int aszmic = message.getAszmic();
        final byte[] sequenceNumber = message.getSequenceNumber();
        int seqZero = MeshParserUtils.calculateSeqZero(sequenceNumber);

        final int numberOfSegments = (encryptedUpperTransportPDU.length + (MAX_SEGMENTED_ACCESS_PAYLOAD_LENGTH - 1)) / MAX_SEGMENTED_ACCESS_PAYLOAD_LENGTH;
        final int segN = numberOfSegments - 1; //Zero based segN
        final SparseArray<byte[]> lowerTransportPduMap = new SparseArray<>();
        int offset = 0;
        int length;
        for (int segO = 0; segO < numberOfSegments; segO++) {
            //Here we calculate the size of the segments based on the offset and the maximum payload of a segment access message
            length = Math.min(encryptedUpperTransportPDU.length - offset, MAX_SEGMENTED_ACCESS_PAYLOAD_LENGTH);
            final ByteBuffer lowerTransportBuffer = ByteBuffer.allocate(SEGMENTED_MESSAGE_HEADER_LENGTH + length).order(ByteOrder.BIG_ENDIAN);
            lowerTransportBuffer.put((byte) ((SEGMENTED_HEADER << 7) | akfAid));
            lowerTransportBuffer.put((byte) ((aszmic << 7) | ((seqZero >> 6) & 0x7F)));
            lowerTransportBuffer.put((byte) (((seqZero << 2) & 0xFC) | ((segO >> 3) & 0x03)));
            lowerTransportBuffer.put((byte) (((segO << 5) & 0xE0) | ((segN) & 0x1F)));
            lowerTransportBuffer.put(encryptedUpperTransportPDU, offset, length);
            offset += length;

            final byte[] lowerTransportPDU = lowerTransportBuffer.array();
            MeshLogger.verbose(TAG, "Segmented Lower transport access PDU: " + MeshParserUtils.bytesToHex(lowerTransportPDU, false) + " " + segO + " of " + numberOfSegments);
            lowerTransportPduMap.put(segO, lowerTransportPDU);
        }
        return lowerTransportPduMap;
    }

    /**
     * Creates an unsegmented control.
     *
     * @param message control message.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    private void createUnsegmentedControlMessage(@NonNull final ControlMessage message) {
        int pduLength;
        final ByteBuffer lowerTransportBuffer;
        message.setSegmented(false);
        final int opCode = message.getOpCode();
        final byte[] parameters = message.getParameters();
        final byte[] upperTransportControlPDU = message.getTransportControlPdu();
        final int header = (byte) ((UNSEGMENTED_HEADER << 7) | opCode);
        if (parameters != null) {
            pduLength = UNSEGMENTED_MESSAGE_HEADER_LENGTH + parameters.length + upperTransportControlPDU.length;
            lowerTransportBuffer = ByteBuffer.allocate(pduLength).order(ByteOrder.BIG_ENDIAN);
            lowerTransportBuffer.put((byte) header);
            lowerTransportBuffer.put(parameters);
        } else {
            pduLength = UNSEGMENTED_MESSAGE_HEADER_LENGTH + upperTransportControlPDU.length;
            lowerTransportBuffer = ByteBuffer.allocate(pduLength).order(ByteOrder.BIG_ENDIAN);
            lowerTransportBuffer.put((byte) header);
        }

        lowerTransportBuffer.put(upperTransportControlPDU);
        final byte[] lowerTransportPDU = lowerTransportBuffer.array();
        MeshLogger.verbose(TAG, "Unsegmented Lower transport control PDU " + MeshParserUtils.bytesToHex(lowerTransportPDU, false));
        final SparseArray<byte[]> lowerTransportControlPduMap = new SparseArray<>();
        lowerTransportControlPduMap.put(0, lowerTransportPDU);
        message.setLowerTransportControlPdu(lowerTransportControlPduMap);
    }

    /**
     * Creates a segmented control message.
     *
     * @param controlMessage control message to be sent.
     */
    private void createSegmentedControlMessage(@NonNull final ControlMessage controlMessage) {
        controlMessage.setSegmented(false);
        final byte[] encryptedUpperTransportControlPDU = controlMessage.getTransportControlPdu();
        final int opCode = controlMessage.getOpCode();
        final int rfu = 0;
        final byte[] sequenceNumber = controlMessage.getSequenceNumber();
        final int seqZero = MeshParserUtils.calculateSeqZero(sequenceNumber);

        final int numberOfSegments = (encryptedUpperTransportControlPDU.length + (MAX_SEGMENTED_CONTROL_PAYLOAD_LENGTH - 1)) / MAX_SEGMENTED_CONTROL_PAYLOAD_LENGTH;
        final int segN = numberOfSegments - 1; //Zero based segN
        final SparseArray<byte[]> lowerTransportControlPduMap = new SparseArray<>();
        int offset = 0;
        int length;
        for (int segO = 0; segO < numberOfSegments; segO++) {
            //Here we calculate the size of the segments based on the offset and the maximum payload of a segment access message
            length = Math.min(encryptedUpperTransportControlPDU.length - offset, MAX_SEGMENTED_CONTROL_PAYLOAD_LENGTH);
            final ByteBuffer lowerTransportBuffer = ByteBuffer.allocate(SEGMENTED_MESSAGE_HEADER_LENGTH + length).order(ByteOrder.BIG_ENDIAN);
            lowerTransportBuffer.put((byte) ((SEGMENTED_HEADER << 7) | opCode));
            lowerTransportBuffer.put((byte) ((rfu << 7) | ((seqZero >> 6) & 0x7F)));
            lowerTransportBuffer.put((byte) (((seqZero << 2) & 0xFC) | ((segO >> 3) & 0x03)));
            lowerTransportBuffer.put((byte) (((segO << 5) & 0xE0) | (segN & 0x1F)));
            lowerTransportBuffer.put(encryptedUpperTransportControlPDU, offset, length);
            offset += length;

            final byte[] lowerTransportPDU = lowerTransportBuffer.array();
            MeshLogger.verbose(TAG, "Segmented Lower transport access PDU: " + MeshParserUtils.bytesToHex(lowerTransportPDU, false) + " " + segO + " of " + numberOfSegments);
            lowerTransportControlPduMap.put(segO, lowerTransportPDU);
        }
        controlMessage.setLowerTransportControlPdu(lowerTransportControlPduMap);
    }

    /**
     * Checks if the received message is a segmented message
     *
     * @param lowerTransportHeader header for the lower transport pdu
     * @return true if segmented and false if not
     */
    /*package*/
    final boolean isSegmentedMessage(final byte lowerTransportHeader) {
        return ((lowerTransportHeader >> 7) & 0x01) == 1;
    }

    /**
     * Parses a unsegmented lower transport access pdu
     *
     * @param pdu            The complete pdu was received from the node. This is already de-obfuscated
     *                       and decrypted at network layer.
     * @param ivIndex        IV Index of the received pdu
     * @param sequenceNumber Sequence number of the message.
     */
    /*package*/
    final AccessMessage parseUnsegmentedAccessLowerTransportPDU(@NonNull final byte[] pdu,
                                                                final int ivIndex,
                                                                @NonNull final byte[] sequenceNumber) {
        AccessMessage message = null;
        final byte header = pdu[10]; //Lower transport pdu starts here
        final int seg = (header >> 7) & 0x01;
        final int akf = (header >> 6) & 0x01;
        final int aid = header & 0x3F;
        if (seg == 0) { //Unsegmented message
            MeshLogger.debug(TAG, "IV Index of received message: " + ivIndex);
            final int seqAuth = (ivIndex << 24) | MeshParserUtils.convert24BitsToInt(sequenceNumber);
            final byte[] src = MeshParserUtils.getSrcAddress(pdu);
            final int srcAdd = MeshParserUtils.unsignedBytesToInt(src[1], src[0]);
            MeshLogger.debug(TAG, "SeqAuth: " + seqAuth);
            if (!isValidSeqAuth(seqAuth, srcAdd)) {
                return null;
            }
            mMeshNode.setSeqAuth(srcAdd, seqAuth);
            // We do not need to rely on the sequence number here
            // Setting hte sequence number here will reset the already incremented sequence number for a message sent to all nodes.
            // mMeshNode.setSequenceNumber(MeshParserUtils.convert24BitsToInt(sequenceNumber));
            message = new AccessMessage();
            if (akf == 0) {// device key was used to encrypt
                final int lowerTransportPduLength = pdu.length - 10;
                final ByteBuffer lowerTransportBuffer = ByteBuffer.allocate(lowerTransportPduLength).order(ByteOrder.BIG_ENDIAN);
                lowerTransportBuffer.put(pdu, 10, lowerTransportPduLength);
                final byte[] lowerTransportPDU = lowerTransportBuffer.array();
                final SparseArray<byte[]> messages = new SparseArray<>();
                messages.put(0, lowerTransportPDU);
                message.setSegmented(false);
                message.setAszmic(0); //aszmic is always 0 for unsegmented access messages
                message.setAkf(akf);
                message.setAid(aid);
                message.setLowerTransportAccessPdu(messages);
            } else {
                final int lowerTransportPduLength = pdu.length - 10;
                final ByteBuffer lowerTransportBuffer = ByteBuffer.allocate(lowerTransportPduLength).order(ByteOrder.BIG_ENDIAN);
                lowerTransportBuffer.put(pdu, 10, lowerTransportPduLength);
                final byte[] lowerTransportPDU = lowerTransportBuffer.array();
                final SparseArray<byte[]> messages = new SparseArray<>();
                messages.put(0, lowerTransportPDU);
                message.setSegmented(false);
                message.setAszmic(0); //aszmic is always 0 for unsegmented access messages
                message.setAkf(akf);
                message.setAid(aid);
                message.setLowerTransportAccessPdu(messages);
            }
        }
        return message;
    }

    /**
     * Parses a segmented lower transport access pdu.
     *
     * @param ttl            TTL of the acknowledgement
     * @param pdu            The complete pdu was received from the node. This is already de-obfuscated and decrypted at network layer.
     * @param ivIndex        Current IV Index of the network
     * @param sequenceNumber Sequence number
     */
    /*package*/
    final AccessMessage parseSegmentedAccessLowerTransportPDU(final int ttl,
                                                              @NonNull final byte[] pdu,
                                                              final int ivIndex,
                                                              @NonNull final byte[] sequenceNumber) {
        final byte header = pdu[10]; //Lower transport pdu starts here
        final int akf = (header >> 6) & 0x01;
        final int aid = header & 0x3F;

        final int szmic = (pdu[11] >> 7) & 0x01;
        final int seqZero = ((pdu[11] & 0x7F) << 6) | ((pdu[12] & 0xFC) >> 2);
        final int segO = ((pdu[12] & 0x03) << 3) | ((pdu[13] & 0xE0) >> 5);
        final int segN = ((pdu[13]) & 0x1F);

        final byte[] src = MeshParserUtils.getSrcAddress(pdu);
        final byte[] dst = MeshParserUtils.getDstAddress(pdu);

        final int blockAckSrc = MeshParserUtils.unsignedBytesToInt(dst[1], dst[0]); //Destination of the received packet would be the source for the ack
        final int blockAckDst = MeshParserUtils.unsignedBytesToInt(src[1], src[0]); //Source of the received packet would be the destination for the ack

        MeshLogger.verbose(TAG, "SEG O: " + segO);
        MeshLogger.verbose(TAG, "SEG N: " + segN);

        final int seqNumber = getTransportLayerSequenceNumber(MeshParserUtils.convert24BitsToInt(sequenceNumber), seqZero);
        final int seqAuth = ivIndex << 24 | seqNumber;
        final Integer lastSeqAuth = mMeshNode.getSeqAuth(blockAckDst);
        if (lastSeqAuth != null)
            MeshLogger.verbose(TAG, "Last SeqAuth value " + lastSeqAuth);

        MeshLogger.verbose(TAG, "Current SeqAuth value " + seqAuth);

        final int payloadLength = pdu.length - 10;
        final ByteBuffer payloadBuffer = ByteBuffer.allocate(payloadLength);
        payloadBuffer.put(pdu, 10, payloadLength);

        //Check if the current SeqAuth value is greater than the last and if the incomplete timer has not started, start it!
        if ((lastSeqAuth == null || lastSeqAuth < seqAuth)) {
            // We do not need to rely on the sequence number here
            // Setting hte sequence number here will reset the already incremented sequence number for a message sent to all nodes.
            // mMeshNode.setSequenceNumber(seqNumber);
            segmentedAccessMessageMap.clear();
            segmentedAccessMessageMap.put(segO, payloadBuffer.array());
            mMeshNode.setSeqAuth(blockAckDst, seqAuth);

            MeshLogger.verbose(TAG, "Starting incomplete timer for src: " + MeshAddress.formatAddress(blockAckDst, false));
            initIncompleteTimer();

            // Start acknowledgement calculation and timer only for messages directed to a unicast address.
            if (MeshAddress.isValidUnicastAddress(dst)) {
                // Calculate the initial block acknowledgement value
                mSegmentedAccessBlockAck = BlockAcknowledgementMessage.calculateBlockAcknowledgement(null, segO);
                //Start the block acknowledgement timer irrespective of which segment was received first
                initSegmentedAccessAcknowledgementTimer(seqZero, ttl, blockAckSrc, blockAckDst, segN);
            }

            // We need to ensure there could be an unsegmented message that could be sent as a
            // segmented message i.e. segO = 0 and segN = 0. This will ensure the multi-segmented
            // messages that may not arrive in order will be reassembled correctly or rather ignored.
            if (segO == 0 && segN == 0) {
                if (MeshAddress.isValidUnicastAddress(dst)) {
                    mSegmentedAccessBlockAck = BlockAcknowledgementMessage.calculateBlockAcknowledgement(mSegmentedAccessBlockAck, segO);
                    handleImmediateBlockAcks(seqZero, ttl, blockAckSrc, blockAckDst, segN);
                } else {
                    //We should cancel the incomplete timer since we have received all segments
                    cancelIncompleteTimer();
                }

                final AccessMessage accessMessage = new AccessMessage();
                accessMessage.setAszmic(szmic);
                accessMessage.setSequenceNumber(MeshParserUtils.getSequenceNumberBytes(seqNumber));
                accessMessage.setAkf(akf);
                accessMessage.setAid(aid);
                accessMessage.setSegmented(true);
                final SparseArray<byte[]> segmentedMessages = segmentedAccessMessageMap.clone();
                accessMessage.setLowerTransportAccessPdu(segmentedMessages);
                return accessMessage;
            }

        } else {
            //if the seqauth values are the same and the init complete timer has already started for a received segmented message, we need to restart the incomplete timer
            if (lastSeqAuth == seqAuth) {
                if (mIncompleteTimerStarted) {
                    if (segmentedAccessMessageMap.get(segO) == null) {
                        segmentedAccessMessageMap.put(segO, payloadBuffer.array());
                    }
                    final int receivedSegmentedMessageCount = segmentedAccessMessageMap.size();
                    MeshLogger.verbose(TAG, "Received segment message count: " + receivedSegmentedMessageCount);
                    //Add +1 to segN since its zero based
                    if (receivedSegmentedMessageCount != (segN + 1)) {
                        restartIncompleteTimer();
                        mSegmentedAccessBlockAck = BlockAcknowledgementMessage.calculateBlockAcknowledgement(mSegmentedAccessBlockAck, segO);
                        MeshLogger.verbose(TAG, "Restarting incomplete timer for src: " + MeshAddress.formatAddress(blockAckDst, false));

                        //Start acknowledgement calculation and timer only for messages directed to a unicast address.
                        //We also have to make sure we restart the acknowledgement timer only if the acknowledgement timer is not active and the incomplete timer is active
                        if (MeshAddress.isValidUnicastAddress(dst) && !mSegmentedAccessAcknowledgementTimerStarted) {
                            MeshLogger.verbose(TAG, "Restarting block acknowledgement timer for src: " + MeshAddress.formatAddress(blockAckDst, false));
                            //Start the block acknowledgement timer irrespective of which segment was received first
                            initSegmentedAccessAcknowledgementTimer(seqZero, ttl, blockAckSrc, blockAckDst, segN);
                        }
                    } else {
                        if (MeshAddress.isValidUnicastAddress(dst)) {
                            mSegmentedAccessBlockAck = BlockAcknowledgementMessage.calculateBlockAcknowledgement(mSegmentedAccessBlockAck, segO);
                            handleImmediateBlockAcks(seqZero, ttl, blockAckSrc, blockAckDst, segN);
                        } else {
                            //We should cancel the incomplete timer since we have received all segments
                            cancelIncompleteTimer();
                        }

                        final AccessMessage accessMessage = new AccessMessage();
                        accessMessage.setAszmic(szmic);
                        accessMessage.setSequenceNumber(MeshParserUtils.getSequenceNumberBytes(seqNumber));
                        accessMessage.setAkf(akf);
                        accessMessage.setAid(aid);
                        accessMessage.setSegmented(true);
                        final SparseArray<byte[]> segmentedMessages = segmentedAccessMessageMap.clone();
                        accessMessage.setLowerTransportAccessPdu(segmentedMessages);
                        return accessMessage;
                    }
                } else {
                    MeshLogger.verbose(TAG, "Ignoring message since the incomplete timer has expired and all messages have been received");
                }
            }
        }
        return null;
    }

    /**
     * Send immediate block acknowledgement
     *
     * @param seqZero seqzero of the message
     * @param ttl     ttl of the message
     * @param src     source address of the message
     * @param dst     destination address of the message
     * @param segN    total segment count
     */
    private void handleImmediateBlockAcks(final int seqZero, final int ttl, final int src, final int dst, final int segN) {
        cancelIncompleteTimer();
        sendBlockAck(seqZero, ttl, src, dst, segN);
    }

    /**
     * Parses a unsegmented lower transport control pdu.
     *
     * @param decryptedProxyPdu The complete pdu was received from the node. This is already de-obfuscated and decrypted at network layer.
     */
    /*package*/
    final void parseUnsegmentedControlLowerTransportPDU(@NonNull final ControlMessage controlMessage,
                                                        @NonNull final byte[] decryptedProxyPdu) throws ExtendedInvalidCipherTextException {

        final SparseArray<byte[]> unsegmentedMessages = new SparseArray<>();
        final int lowerTransportPduLength = decryptedProxyPdu.length - 10;
        final ByteBuffer lowerTransportBuffer = ByteBuffer.allocate(lowerTransportPduLength).order(ByteOrder.BIG_ENDIAN);
        lowerTransportBuffer.put(decryptedProxyPdu, 10, lowerTransportPduLength);
        final byte[] lowerTransportPDU = lowerTransportBuffer.array();
        unsegmentedMessages.put(0, lowerTransportPDU);
        final int opCode;
        final int pduType = decryptedProxyPdu[0];
        switch (pduType) {
            case MeshManagerApi.PDU_TYPE_NETWORK:
                final byte header = decryptedProxyPdu[10]; //Lower transport pdu starts here
                opCode = header & 0x7F;
                controlMessage.setPduType(MeshManagerApi.PDU_TYPE_NETWORK);//Set the pdu type here
                controlMessage.setAszmic(0);
                controlMessage.setOpCode(opCode);
                controlMessage.setLowerTransportControlPdu(unsegmentedMessages);
                parseLowerTransportLayerPDU(controlMessage);
                break;
            case MeshManagerApi.PDU_TYPE_PROXY_CONFIGURATION:
                controlMessage.setPduType(MeshManagerApi.PDU_TYPE_PROXY_CONFIGURATION);
                controlMessage.setLowerTransportControlPdu(unsegmentedMessages);
                parseUpperTransportPDU(controlMessage);
                break;
        }
    }

    /**
     * Parses a segmented lower transport control pdu.
     *
     * @param pdu The complete pdu was received from the node. This is already de-obfuscated and decrypted at network layer.
     */
    /*package*/
    final ControlMessage parseSegmentedControlLowerTransportPDU(@NonNull final byte[] pdu) {

        final byte header = pdu[10]; //Lower transport pdu starts here
        final int akf = (header >> 6) & 0x01;
        final int aid = header & 0x3F;

        final int szmic = (pdu[11] >> 7) & 0x01;
        final int seqZero = ((pdu[11] & 0x7F) << 6) | ((pdu[12] & 0xFC) >> 2);
        final int segO = ((pdu[12] & 0x3) << 3) | ((pdu[13] & 0xe0) >> 5);
        final int segN = ((pdu[13]) & 0x1F);

        final int ttl = pdu[2] & 0x7F;
        final byte[] src = MeshParserUtils.getSrcAddress(pdu);
        final byte[] dst = MeshParserUtils.getDstAddress(pdu);

        final int blockAckSrc = MeshParserUtils.unsignedBytesToInt(dst[1], dst[0]); //Destination of the received packet would be the source for the ack
        final int blockAckDst = MeshParserUtils.unsignedBytesToInt(src[1], src[0]); //Source of the received packet would be the destination for the ack

        MeshLogger.verbose(TAG, "SEG O: " + segO);
        MeshLogger.verbose(TAG, "SEG N: " + segN);

        //Start the timer irrespective of which segment was received first
        initSegmentedControlAcknowledgementTimer(seqZero, ttl, blockAckDst, blockAckSrc, segN);
        mSegmentedControlBlockAck = BlockAcknowledgementMessage.calculateBlockAcknowledgement(mSegmentedControlBlockAck, segO);
        MeshLogger.verbose(TAG, "Block acknowledgement value for " + mSegmentedControlBlockAck + " Seg O " + segO);

        final int payloadLength = pdu.length - 10;

        final ByteBuffer payloadBuffer = ByteBuffer.allocate(payloadLength);
        payloadBuffer.put(pdu, 10, payloadLength);
        segmentedControlMessageMap.put(segO, payloadBuffer.array());

        //Check the message count against the zero-based segN;
        final int receivedSegmentedMessageCount = segmentedControlMessageMap.size() - 1;
        if (segN == receivedSegmentedMessageCount) {
            MeshLogger.verbose(TAG, "All segments received");
            //Remove the incomplete timer if all segments were received
            mHandler.removeCallbacks(mIncompleteTimerRunnable);
            MeshLogger.verbose(TAG, "Block ack sent? " + mBlockAckSent);
            if (mDuration > System.currentTimeMillis() && !mBlockAckSent) {
                if (MeshAddress.isValidUnicastAddress(dst)) {
                    mHandler.removeCallbacksAndMessages(null);
                    MeshLogger.verbose(TAG, "Cancelling Scheduled block ack and incomplete timer, sending an immediate block ack");
                    sendBlockAck(seqZero, ttl, blockAckSrc, blockAckDst, segN);
                    //mBlockAckSent = false;
                }
            }
            final int upperTransportSequenceNumber = getTransportLayerSequenceNumber(MeshParserUtils.getSequenceNumberFromPDU(pdu), seqZero);
            final byte[] sequenceNumber = MeshParserUtils.getSequenceNumberBytes(upperTransportSequenceNumber);
            final ControlMessage message = new ControlMessage();
            message.setAszmic(szmic);
            message.setSequenceNumber(sequenceNumber);
            message.setAkf(akf);
            message.setAid(aid);
            message.setSegmented(true);
            final SparseArray<byte[]> segmentedMessages = segmentedControlMessageMap.clone();
            segmentedControlMessageMap.clear();
            message.setLowerTransportControlPdu(segmentedMessages);
            return message;
        }

        return null;
    }

    /**
     * Start incomplete timer for segmented messages.
     */
    private void initIncompleteTimer() {
        mHandler.postDelayed(mIncompleteTimerRunnable, INCOMPLETE_TIMER_DELAY);
        mIncompleteTimerStarted = true;
    }

    /**
     * Restarts the incomplete timer
     */
    private void restartIncompleteTimer() {
        //Remove the existing incomplete timer
        if (mIncompleteTimerStarted) {
            mHandler.removeCallbacks(mIncompleteTimerRunnable);
        }
        //Call init to start the timer again
        initIncompleteTimer();
    }

    /**
     * Cancels an already started the incomplete timer
     */
    private void cancelIncompleteTimer() {
        //Remove the existing incomplete timer
        mIncompleteTimerStarted = false;
        mHandler.removeCallbacks(mIncompleteTimerRunnable);
    }

    /**
     * Start acknowledgement timer for segmented messages.
     *
     * @param seqZero Seqzero of the segmented messages.
     * @param ttl     TTL of the segmented messages.
     * @param dst     Destination address.
     */
    private void initSegmentedAccessAcknowledgementTimer(final int seqZero, final int ttl, final int src, final int dst, final int segN) {
        if (!mSegmentedAccessAcknowledgementTimerStarted) {
            mSegmentedAccessAcknowledgementTimerStarted = true;
            MeshLogger.verbose(TAG, "TTL: " + ttl);
            final int duration = (BLOCK_ACK_TIMER + (50 * ttl));
            MeshLogger.verbose(TAG, "Duration: " + duration);
            mDuration = System.currentTimeMillis() + duration;
            mHandler.postDelayed(() -> {
                MeshLogger.verbose(TAG, "Acknowledgement timer expiring");
                sendBlockAck(seqZero, ttl, src, dst, segN);
            }, duration);
        }
    }

    /**
     * Start acknowledgement timer for segmented messages.
     *
     * @param seqZero Seqzero of the segmented messages.
     * @param ttl     TTL of the segmented messages.
     * @param src     Source address which is the element address
     * @param dst     Destination address.
     */
    private void initSegmentedControlAcknowledgementTimer(final int seqZero, final int ttl, final int src, final int dst, final int segN) {
        if (!mSegmentedControlAcknowledgementTimerStarted) {
            mSegmentedControlAcknowledgementTimerStarted = true;
            final int duration = BLOCK_ACK_TIMER + (50 * ttl);
            mDuration = System.currentTimeMillis() + duration;
            mHandler.postDelayed(() -> sendBlockAck(seqZero, ttl, src, dst, segN), duration);
        }
    }

    /**
     * Send block acknowledgement
     *
     * @param seqZero Seqzero of the segmented messages.
     * @param ttl     TTL of the segmented messages.
     * @param src     Source address which is the element address
     * @param dst     Destination address.
     */
    private void sendBlockAck(final int seqZero, final int ttl, final int src, final int dst, final int segN) {
        final int blockAck = mSegmentedAccessBlockAck;
        if (BlockAcknowledgementMessage.hasAllSegmentsBeenReceived(blockAck, segN)) {
            MeshLogger.verbose(TAG, "All segments received cancelling incomplete timer");
            cancelIncompleteTimer();
        }

        final byte[] upperTransportControlPdu = createAcknowledgementPayload(seqZero, blockAck);
        MeshLogger.verbose(TAG, "Block acknowledgement payload: " + MeshParserUtils.bytesToHex(upperTransportControlPdu, false));
        final ControlMessage controlMessage = new ControlMessage();
        controlMessage.setOpCode(TransportLayerOpCodes.SAR_ACK_OPCODE);
        controlMessage.setTransportControlPdu(upperTransportControlPdu);
        controlMessage.setTtl(ttl);
        controlMessage.setPduType(MeshManagerApi.PDU_TYPE_NETWORK);
        controlMessage.setSrc(src);
        controlMessage.setDst(dst);
        controlMessage.setIvIndex(mUpperTransportLayerCallbacks.getIvIndex());
        final int sequenceNumber = mUpperTransportLayerCallbacks.getNode(controlMessage.getSrc()).incrementSequenceNumber();
        final byte[] sequenceNum = MeshParserUtils.getSequenceNumberBytes(sequenceNumber);
        controlMessage.setSequenceNumber(sequenceNum);
        mBlockAckSent = true;
        mLowerTransportLayerCallbacks.sendSegmentAcknowledgementMessage(controlMessage);
        mSegmentedAccessAcknowledgementTimerStarted = false;
    }

    /**
     * Creates the acknowledgement parameters.
     *
     * @param seqZero              Seqzero of the message.
     * @param blockAcknowledgement Block acknowledgement
     * @return acknowledgement parameters.
     */
    private byte[] createAcknowledgementPayload(final int seqZero, final int blockAcknowledgement) {
        final int obo = 0;
        final int rfu = 0;

        final ByteBuffer buffer = ByteBuffer.allocate(6).order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte) ((obo << 7) | (seqZero >> 6) & 0x7F));
        buffer.put((byte) (((seqZero << 2) & 0xFC) | rfu));
        buffer.putInt(blockAcknowledgement);
        return buffer.array();
    }

    /**
     * Parse transport layer control pdu.
     *
     * @param controlMessage underlying message containing the access pdu.
     */
    private void parseLowerTransportLayerPDU(@NonNull final ControlMessage controlMessage) {
        //First we reassemble the transport layer message if its a segmented message
        reassembleLowerTransportControlPDU(controlMessage);
        final byte[] transportControlPdu = controlMessage.getTransportControlPdu();
        final int opCode = controlMessage.getOpCode();

        if (opCode == TransportLayerOpCodes.SAR_ACK_OPCODE) {
            final BlockAcknowledgementMessage acknowledgement = new BlockAcknowledgementMessage(transportControlPdu);
            controlMessage.setTransportControlMessage(acknowledgement);
        }

        if (opCode == TransportLayerOpCodes.HEARTBEAT_OPCODE) {
            final HeartbeatMessage heartbeatMessage = new HeartbeatMessage(controlMessage);
            controlMessage.setTransportControlMessage(heartbeatMessage);
        }
    }

    /**
     * Validates Sequence authentication value.
     *
     * @param seqAuth Sequence authentication.
     * @param src     Source address.
     */
    private boolean isValidSeqAuth(final int seqAuth,
                                   final int src) {
        final Integer lastSeqAuth = mMeshNode.getSeqAuth(src);
        return lastSeqAuth == null || lastSeqAuth < seqAuth;
    }
}
