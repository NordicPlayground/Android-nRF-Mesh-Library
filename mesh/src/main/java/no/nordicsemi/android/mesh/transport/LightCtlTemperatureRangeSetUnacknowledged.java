package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.logger.MeshLogger;
import no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.mesh.utils.SecureUtils;

/**
 * To be used as a wrapper class to create light ctl temperature range set unacknowledged message.
 */
public class LightCtlTemperatureRangeSetUnacknowledged extends ApplicationMessage {

    private static final String TAG = LightCtlSet.class.getSimpleName();
    private static final int OP_CODE = ApplicationMessageOpCodes.LIGHT_CTL_TEMPERATURE_RANGE_SET_UNACKNOWLEDGED;
    private static final int LIGHT_CTL_TEMPERATURE_RANGE_SET_PARAMS_LENGTH = 4;

    private final int mRangeMin;
    private final int mRangeMax;

    /**
     * Constructs LightCtlTemperatureRangeSetUnacknowledged message.
     *
     * @param appKey   {@link ApplicationKey} key for this message
     * @param rangeMin Minimum temperature value
     * @param rangeMax Maximum temperature value
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    public LightCtlTemperatureRangeSetUnacknowledged(@NonNull final ApplicationKey appKey,
                                                     final int rangeMin,
                                                     final int rangeMax) throws IllegalArgumentException {
        super(appKey);
        if (rangeMin >= rangeMax)
            throw new IllegalArgumentException("RangeMin must be less than RangeMax");
        if (rangeMin < 0x0320 || rangeMin > 0x4E20)
            throw new IllegalArgumentException("RangeMin must be between 800 and 20000");
        if (rangeMax > 0x4E20)
            throw new IllegalArgumentException("RangeMax must be between 800 and 20000");
        this.mRangeMin = rangeMin;
        this.mRangeMax = rangeMax;
        assembleMessageParameters();
    }

    @Override
    public int getOpCode() {
        return OP_CODE;
    }

    @Override
    void assembleMessageParameters() {
        mAid = SecureUtils.calculateK4(mAppKey.getKey());
        final ByteBuffer paramsBuffer;
        MeshLogger.verbose(TAG, "Range Min: " + mRangeMin);
        MeshLogger.verbose(TAG, "Range Max: " + mRangeMax);
        paramsBuffer = ByteBuffer.allocate(LIGHT_CTL_TEMPERATURE_RANGE_SET_PARAMS_LENGTH).order(ByteOrder.LITTLE_ENDIAN);
        paramsBuffer.putShort((short) mRangeMin);
        paramsBuffer.putShort((short) mRangeMax);

        mParameters = paramsBuffer.array();
    }
}
