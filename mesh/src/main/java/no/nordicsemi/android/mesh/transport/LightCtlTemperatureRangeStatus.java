package no.nordicsemi.android.mesh.transport;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import no.nordicsemi.android.mesh.logger.MeshLogger;
import no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.mesh.utils.MeshAddress;

/**
 * To be used as a wrapper class to create generic light ctl temperature range status message.
 */
public class LightCtlTemperatureRangeStatus extends ApplicationStatusMessage implements Parcelable {

    private static final String TAG = LightCtlStatus.class.getSimpleName();
    private static final int LIGHT_CTL_TEMPERATURE_RANGE_STATUS_LENGHT = 5;
    private static final int OP_CODE = ApplicationMessageOpCodes.LIGHT_CTL_TEMPERATURE_RANGE_STATUS;
    private static final Creator<LightCtlTemperatureRangeStatus> CREATOR = new Creator<LightCtlTemperatureRangeStatus>() {
        @Override
        public LightCtlTemperatureRangeStatus createFromParcel(Parcel in) {
            final AccessMessage message = in.readParcelable(AccessMessage.class.getClassLoader());
            return new LightCtlTemperatureRangeStatus(message);
        }

        @Override
        public LightCtlTemperatureRangeStatus[] newArray(int size) {
            return new LightCtlTemperatureRangeStatus[size];
        }
    };
    private int mStatusCode;
    private int mRangeMin;
    private int mRangeMax;

    /**
     * Constructs LightCtlTemperatureRangeStatus message
     *
     * @param message access message
     */
    public LightCtlTemperatureRangeStatus(@NonNull final AccessMessage message) {
        super(message);
        this.mMessage = message;
        this.mParameters = message.getParameters();
        parseStatusParameters();
    }

    @Override
    void parseStatusParameters() {
        MeshLogger.verbose(TAG, "Received light ctl temperature range status from: " + MeshAddress.formatAddress(mMessage.getSrc(), true));
        final ByteBuffer buffer = ByteBuffer.wrap(mParameters).order(ByteOrder.LITTLE_ENDIAN);
        mStatusCode = buffer.get() & 0xFF;
        mRangeMin = buffer.getShort() & 0xFFFF;
        mRangeMax = buffer.getShort() & 0xFFFF;
        MeshLogger.verbose(TAG, "Status Code: " + mStatusCode);
        MeshLogger.verbose(TAG, "Range Min: " + mRangeMin);
        MeshLogger.verbose(TAG, "Range Max: " + mRangeMax);
    }

    @Override
    public int getOpCode() {
        return OP_CODE;
    }

    /**
     * Returns the status code of the light ctl temperature range status message
     *
     * @return status code
     */
    public final int getStatusCode() {
        return mStatusCode;
    }

    /**
     * Returns the range min of the light ctl temperature range status message
     *
     * @return range min
     */
    public final int getRangeMin() {
        return mRangeMin;
    }

    /**
     * Returns the range max of the light ctl temperature range status message
     *
     * @return range max
     */
    public final int getRangeMax() {
        return mRangeMax;
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
}
