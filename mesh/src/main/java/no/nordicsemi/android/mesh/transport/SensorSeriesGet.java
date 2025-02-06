package no.nordicsemi.android.mesh.transport;

import java.nio.ByteBuffer;

import androidx.annotation.NonNull;
import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.mesh.sensorutils.DeviceProperty;
import no.nordicsemi.android.mesh.utils.SecureUtils;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
 * SensorColumnGet message.
 */
public class SensorSeriesGet extends ApplicationMessage {

    private static final String TAG = SensorSeriesGet.class.getSimpleName();
    private static final int OP_CODE = ApplicationMessageOpCodes.SENSOR_SERIES_GET;

    private final DeviceProperty property;
    private byte[] rawValueX1;
    private byte[] rawValueX2;

    /**
     * Constructs SensorColumnGet message.
     *
     * @param appKey   {@link ApplicationKey} key for this message.
     * @param property {@link DeviceProperty} device property.
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    public SensorSeriesGet(@NonNull final ApplicationKey appKey,
                           @NonNull final DeviceProperty property) {
        super(appKey);
        this.property = property;
        assembleMessageParameters();
    }

    /**
     * Constructs SensorColumnGet message.
     *
     * @param appKey     {@link ApplicationKey} key for this message.
     * @param property   {@link DeviceProperty} device property.
     * @param rawValueX1 Raw value x
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    public SensorSeriesGet(@NonNull final ApplicationKey appKey,
                           @NonNull final DeviceProperty property,
                           @NonNull final byte[] rawValueX1,
                           @NonNull final byte[] rawValueX2) {
        super(appKey);
        this.property = property;
        this.rawValueX1 = rawValueX1;
        this.rawValueX2 = rawValueX2;
        assembleMessageParameters();
    }

    @Override
    public int getOpCode() {
        return OP_CODE;
    }

    @Override
    void assembleMessageParameters() {
        mAid = SecureUtils.calculateK4(mAppKey.getKey());
        if (rawValueX1 == null) {
            mParameters = ByteBuffer.allocate(2).order(LITTLE_ENDIAN)
                    .putShort(property.getPropertyId())
                    .array();
        } else {
            mParameters = ByteBuffer.allocate(2 + rawValueX1.length + rawValueX2.length).order(LITTLE_ENDIAN)
                    .putShort(property.getPropertyId())
                    .put(rawValueX1)
                    .put(rawValueX2)
                    .array();
        }
    }
}
