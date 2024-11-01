package no.nordicsemi.android.mesh.sensorutils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import no.nordicsemi.android.mesh.utils.SensorFormat;

/**
 * A Marshalled Property ID (MPID) is a concatenation of a 1-bit Format field,
 * a 4-bit or 7-bit Length of the Property Value field, and an 11-bit or 16-bit Property ID.
 */
public class MarshalledPropertyId implements Parcelable {
    private final SensorFormat sensorFormat;
    private final int length;
    private final DeviceProperty propertyId;

    /**
     * Constructs a MarshalledPropertyID
     *
     * @param sensorFormat {@link SensorFormat}
     * @param length       length of the property value
     * @param propertyId   property id
     */
    public MarshalledPropertyId(@NonNull final SensorFormat sensorFormat, final int length, final DeviceProperty propertyId) {
        this.sensorFormat = sensorFormat;
        this.length = length;
        this.propertyId = propertyId;
    }

    protected MarshalledPropertyId(Parcel in) {
        sensorFormat = SensorFormat.from((byte) in.readInt());
        length = in.readInt();
        propertyId = DeviceProperty.from((short) in.readInt());
    }

    public static final Creator<MarshalledPropertyId> CREATOR = new Creator<MarshalledPropertyId>() {
        @Override
        public MarshalledPropertyId createFromParcel(Parcel in) {
            return new MarshalledPropertyId(in);
        }

        @Override
        public MarshalledPropertyId[] newArray(int size) {
            return new MarshalledPropertyId[size];
        }
    };

    /**
     * Returns the format for the Sensor Data
     *
     * @return {@link SensorFormat}
     */
    public SensorFormat getSensorFormat() {
        return sensorFormat;
    }

    /**
     * Returns the length, a 4-bit or 7-bit value defining the length of the property value.
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the device property
     **/
    public DeviceProperty getPropertyId() {
        return propertyId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(sensorFormat.ordinal());
        dest.writeInt(length);
        dest.writeInt(propertyId.getPropertyId());
    }

    @Override
    public String toString() {
        return "MarshalledPropertyId{" +
                "sensorFormat=" + SensorFormat.formatField(sensorFormat) +
                ", length=" + length +
                ", propertyId=" + DeviceProperty.getPropertyName(propertyId) +
                '}';
    }
}
