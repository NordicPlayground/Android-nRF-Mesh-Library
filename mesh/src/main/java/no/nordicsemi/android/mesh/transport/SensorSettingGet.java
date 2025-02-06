package no.nordicsemi.android.mesh.transport;

import java.nio.ByteBuffer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.mesh.sensorutils.DeviceProperty;
import no.nordicsemi.android.mesh.utils.SecureUtils;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
 * SensorSettingsGet message.
 */
public class SensorSettingGet extends ApplicationMessage {

    private static final String TAG = SensorSettingGet.class.getSimpleName();
    private static final int OP_CODE = ApplicationMessageOpCodes.SENSOR_SETTING_GET;

    private final DeviceProperty propertyId;
    private final DeviceProperty sensorSettingPropertyId;

    /**
     * Constructs SensorSettingsGet message.
     *
     * @param appKey                  {@link ApplicationKey} Key for this message.
     * @param propertyId              {@link DeviceProperty} Device property.
     * @param sensorSettingPropertyId {@link DeviceProperty} Sensor setting property ID.
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    public SensorSettingGet(@NonNull final ApplicationKey appKey,
                            @Nullable final DeviceProperty propertyId,
                            @Nullable final DeviceProperty sensorSettingPropertyId) {
        super(appKey);
        this.propertyId = propertyId;
        this.sensorSettingPropertyId = sensorSettingPropertyId;
        assembleMessageParameters();
    }

    @Override
    public int getOpCode() {
        return OP_CODE;
    }

    @Override
    void assembleMessageParameters() {
        mAid = SecureUtils.calculateK4(mAppKey.getKey());
        if (propertyId != null)
            mParameters = ByteBuffer.allocate(4).order(LITTLE_ENDIAN)
                    .putShort(propertyId.getPropertyId())
                    .putShort(sensorSettingPropertyId.getPropertyId())
                    .array();
    }

    /**
     * Returns the property id.
     */
    public DeviceProperty getPropertyId() {
        return propertyId;
    }

    /**
     * Returns the Sensor Setting Property id.
     */
    public DeviceProperty getSensorSettingPropertyId() {
        return sensorSettingPropertyId;
    }
}
