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

import android.os.Parcel;
import android.os.Parcelable;
import no.nordicsemi.android.mesh.logger.MeshLogger;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import no.nordicsemi.android.mesh.sensorutils.DeviceProperty;
import no.nordicsemi.android.mesh.sensorutils.SensorDescriptor;
import no.nordicsemi.android.mesh.sensorutils.SensorSamplingFunction;

import static no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes.SENSOR_DESCRIPTOR_STATUS;
import static no.nordicsemi.android.mesh.utils.MeshParserUtils.bytesToInt;
import static no.nordicsemi.android.mesh.utils.MeshParserUtils.unsignedBytesToInt;

/**
 * SensorStatus Message.
 */
@SuppressWarnings({"WeakerAccess"})
public final class SensorDescriptorStatus extends ApplicationStatusMessage implements Parcelable, SceneStatuses {
    private static final String TAG = SensorDescriptorStatus.class.getSimpleName();
    private static final int OP_CODE = SENSOR_DESCRIPTOR_STATUS;
    private DescriptorStatusResult result;

    private static final Creator<SensorDescriptorStatus> CREATOR = new Creator<SensorDescriptorStatus>() {
        @Override
        public SensorDescriptorStatus createFromParcel(Parcel in) {
            final AccessMessage message = in.readParcelable(AccessMessage.class.getClassLoader());
            return new SensorDescriptorStatus(message);
        }

        @Override
        public SensorDescriptorStatus[] newArray(int size) {
            return new SensorDescriptorStatus[size];
        }
    };

    /**
     * Constructs the GenericOnOffStatus mMessage.
     *
     * @param message Access Message
     */
    public SensorDescriptorStatus(@NonNull final AccessMessage message) {
        super(message);
        this.mMessage = message;
        this.mParameters = message.getParameters();
        parseStatusParameters();
    }

    @Override
    void parseStatusParameters() {
        if (mParameters.length == 2) {
            result = new PropertyNotFound(DeviceProperty.from((short) unsignedBytesToInt(mParameters[0], mParameters[1])));
        } else {
            int offset = 0;
            final ArrayList<SensorDescriptor> sensorDescriptors = new ArrayList<>();
            DeviceProperty property;
            short positiveTolerance;
            short negativeTolerance;
            SensorSamplingFunction samplingFunction;
            byte measurementPeriod;
            byte updateInterval;
            while (offset < mParameters.length) {
                property = DeviceProperty.from((short) unsignedBytesToInt(mParameters[offset], mParameters[offset + 1]));
                positiveTolerance = (short) bytesToInt(new byte[]{(byte) (mParameters[offset + 3] & 0x0F), mParameters[offset + 2]});
                //encode(new byte[]{(byte) (mParameters[offset + 1] & 0x0F), mParameters[offset]});
                negativeTolerance = (short) bytesToInt(new byte[]{
                        (byte) ((mParameters[offset + 4] & 0xF0) >> 4),
                        (byte) (mParameters[offset + 4] << 4 | ((mParameters[offset + 3] & 0xF0) >> 4))});
                samplingFunction = SensorSamplingFunction.from(mParameters[offset + 5]);
                measurementPeriod = mParameters[offset + 6];
                updateInterval = mParameters[offset + 7];
                final SensorDescriptor descriptor = new SensorDescriptor(property, positiveTolerance, negativeTolerance,
                        samplingFunction, measurementPeriod, updateInterval);
                MeshLogger.debug(TAG, "Sensor Descriptor: " + descriptor.toString());
                sensorDescriptors.add(descriptor);
                offset += 8;
            }
            result = new SensorDescriptors(sensorDescriptors);
        }
    }

    public DescriptorStatusResult getResult() {
        return result;
    }

    @Override
    public int getOpCode() {
        return OP_CODE;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        final AccessMessage message = (AccessMessage) mMessage;
        dest.writeParcelable(message, flags);
    }

    public class PropertyNotFound implements DescriptorStatusResult {

        public final DeviceProperty property;

        public PropertyNotFound(DeviceProperty property) {
            this.property = property;
        }
    }

    public class SensorDescriptors implements DescriptorStatusResult {

        public final List<SensorDescriptor> descriptors;

        public SensorDescriptors(List<SensorDescriptor> descriptors) {
            this.descriptors = descriptors;
        }
    }

    public interface DescriptorStatusResult {
    }
}
