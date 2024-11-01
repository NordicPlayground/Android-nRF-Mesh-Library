package no.nordicsemi.android.mesh.sensorutils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import no.nordicsemi.android.mesh.utils.SensorFormat;

/**
 * Device Property
 */

public class DeviceProperty {
    public final static DeviceProperty AVERAGE_AMBIENT_TEMPERATURE_IN_A_PERIOD_OF_DAY = new DeviceProperty((short) 0x0001);
    public final static DeviceProperty AVERAGE_INPUT_CURRENT = new DeviceProperty((short) 0x0002);
    public final static DeviceProperty AVERAGE_INPUT_VOLTAGE = new DeviceProperty((short) 0x0003);
    public final static DeviceProperty AVERAGE_OUTPUT_CURRENT = new DeviceProperty((short) 0x0004);
    public final static DeviceProperty AVERAGE_OUTPUT_VOLTAGE = new DeviceProperty((short) 0x0005);
    public final static DeviceProperty CENTER_BEAM_INTENSITY_AT_FULL_POWER = new DeviceProperty((short) 0x0006);
    public final static DeviceProperty CHROMATICITY_TOLERANCE = new DeviceProperty((short) 0x0007);
    public final static DeviceProperty COLOR_RENDERING_INDEX_R9 = new DeviceProperty((short) 0x0008);
    public final static DeviceProperty COLOR_RENDERING_INDEX_RA = new DeviceProperty((short) 0x0009);
    public final static DeviceProperty DEVICE_APPEARANCE = new DeviceProperty((short) 0x000A);
    public final static DeviceProperty DEVICE_COUNTRY_OF_ORIGIN = new DeviceProperty((short) 0x000B);
    public final static DeviceProperty DEVICE_DATE_OF_MANUFACTURE = new DeviceProperty((short) 0x000C);
    public final static DeviceProperty DEVICE_ENERGY_USE_SINCE_TURN_ON = new DeviceProperty((short) 0x000D);
    public final static DeviceProperty DEVICE_FIRMWARE_REVISION = new DeviceProperty((short) 0x000E);
    public final static DeviceProperty DEVICE_GLOBAL_TRADE_ITEM_NUMBER = new DeviceProperty((short) 0x000F);
    public final static DeviceProperty DEVICE_HARDWARE_REVISION = new DeviceProperty((short) 0x0010);
    public final static DeviceProperty DEVICE_MANUFACTURER_NAME = new DeviceProperty((short) 0x0011);
    public final static DeviceProperty DEVICE_MODEL_NUMBER = new DeviceProperty((short) 0x0012);
    public final static DeviceProperty DEVICE_OPERATING_TEMPERATURE_RANGE_SPECIFICATION = new DeviceProperty((short) 0x0013);
    public final static DeviceProperty DEVICE_OPERATING_TEMPERATURE_STATISTICAL_VALUES = new DeviceProperty((short) 0x0014);
    public final static DeviceProperty DEVICE_OVER_TEMPERATURE_EVENT_STATISTICS = new DeviceProperty((short) 0x0015);
    public final static DeviceProperty DEVICE_POWER_RANGE_SPECIFICATION = new DeviceProperty((short) 0x0016);
    public final static DeviceProperty DEVICE_RUN_TIME_SINCE_TURN_ON = new DeviceProperty((short) 0x0017);
    public final static DeviceProperty DEVICE_RUNTIME_WARRANTY = new DeviceProperty((short) 0x0018);
    public final static DeviceProperty DEVICE_SERIAL_NUMBER = new DeviceProperty((short) 0x0019);
    public final static DeviceProperty DEVICE_SOFTWARE_REVISION = new DeviceProperty((short) 0x001A);
    public final static DeviceProperty DEVICE_UNDER_TEMPERATURE_EVENT_STATISTICS = new DeviceProperty((short) 0x001B);
    public final static DeviceProperty INDOOR_AMBIENT_TEMPERATURE_STATISTICAL_VALUES = new DeviceProperty((short) 0x001C);
    public final static DeviceProperty INITIAL_CIE1931_CHROMATICITY_COORDINATES = new DeviceProperty((short) 0x001D);
    public final static DeviceProperty INITIAL_CORRELATED_COLOR_TEMPERATURE = new DeviceProperty((short) 0x001E);
    public final static DeviceProperty INITIAL_LUMINOUS_FLUX = new DeviceProperty((short) 0x001F);
    public final static DeviceProperty INITIAL_PLANCKIAN_DISTANCE = new DeviceProperty((short) 0x0020);
    public final static DeviceProperty INPUT_CURRENT_RANGE_SPECIFICATION = new DeviceProperty((short) 0x0021);
    public final static DeviceProperty INPUT_CURRENT_STATISTICS = new DeviceProperty((short) 0x0022);
    public final static DeviceProperty INPUT_OVER_CURRENT_EVENT_STATISTICS = new DeviceProperty((short) 0x0023);
    public final static DeviceProperty INPUT_OVER_RIPPLE_VOLTAGE_EVENT_STATISTICS = new DeviceProperty((short) 0x0024);
    public final static DeviceProperty INPUT_OVER_VOLTAGE_EVENT_STATISTICS = new DeviceProperty((short) 0x0025);
    public final static DeviceProperty INPUT_UNDERCURRENT_EVENT_STATISTICS = new DeviceProperty((short) 0x0026);
    public final static DeviceProperty INPUT_UNDER_VOLTAGE_EVENT_STATISTICS = new DeviceProperty((short) 0x0027);
    public final static DeviceProperty INPUT_VOLTAGE_RANGE_SPECIFICATION = new DeviceProperty((short) 0x0028);
    public final static DeviceProperty INPUT_VOLTAGE_RIPPLE_SPECIFICATION = new DeviceProperty((short) 0x0029);
    public final static DeviceProperty INPUT_VOLTAGE_STATISTICS = new DeviceProperty((short) 0x002A);
    public final static DeviceProperty LIGHT_CONTROL_AMBIENT_LUX_LEVEL_ON = new DeviceProperty((short) 0x002B);
    public final static DeviceProperty LIGHT_CONTROL_AMBIENT_LUX_LEVEL_PROLONG = new DeviceProperty((short) 0x002C);
    public final static DeviceProperty LIGHT_CONTROL_AMBIENT_LUX_LEVEL_STANDBY = new DeviceProperty((short) 0x002D);
    public final static DeviceProperty LIGHT_CONTROL_LIGHTNESS_ON = new DeviceProperty((short) 0x002E);
    public final static DeviceProperty LIGHT_CONTROL_LIGHTNESS_PROLONG = new DeviceProperty((short) 0x002F);
    public final static DeviceProperty LIGHT_CONTROL_LIGHTNESS_STANDBY = new DeviceProperty((short) 0x0030);
    public final static DeviceProperty LIGHT_CONTROL_REGULATOR_ACCURACY = new DeviceProperty((short) 0x0031);
    public final static DeviceProperty LIGHT_CONTROL_REGULATOR_KID = new DeviceProperty((short) 0x0032);
    public final static DeviceProperty LIGHT_CONTROL_REGULATOR_KIU = new DeviceProperty((short) 0x0033);
    public final static DeviceProperty LIGHT_CONTROL_REGULATOR_KPD = new DeviceProperty((short) 0x0034);
    public final static DeviceProperty LIGHT_CONTROL_REGULATOR_KPU = new DeviceProperty((short) 0x0035);
    public final static DeviceProperty LIGHT_CONTROL_TIME_FADE = new DeviceProperty((short) 0x0036);
    public final static DeviceProperty LIGHT_CONTROL_TIME_FADE_ON = new DeviceProperty((short) 0x0037);
    public final static DeviceProperty LIGHT_CONTROL_TIME_FADE_STANDBY_AUTO = new DeviceProperty((short) 0x0038);
    public final static DeviceProperty LIGHT_CONTROL_TIME_FADE_STANDBY_MANUAL = new DeviceProperty((short) 0x0039);
    public final static DeviceProperty LIGHT_CONTROL_TIME_OCCUPANCY_DELAY = new DeviceProperty((short) 0x003A);
    public final static DeviceProperty LIGHT_CONTROL_TIME_PROLONG = new DeviceProperty((short) 0x003B);
    public final static DeviceProperty LIGHT_CONTROL_TIME_RUN_ON = new DeviceProperty((short) 0x003C);
    public final static DeviceProperty LUMEN_MAINTENANCE_FACTOR = new DeviceProperty((short) 0x003D);
    public final static DeviceProperty LUMINOUS_EFFICACY = new DeviceProperty((short) 0x003E);
    public final static DeviceProperty LUMINOUS_ENERGY_SINCE_TURN_ON = new DeviceProperty((short) 0x003F);
    public final static DeviceProperty LUMINOUS_EXPOSURE = new DeviceProperty((short) 0x0040);
    public final static DeviceProperty LUMINOUS_FLUX_RANGE = new DeviceProperty((short) 0x0041);
    public final static DeviceProperty MOTION_SENSED = new DeviceProperty((short) 0x0042);
    public final static DeviceProperty MOTION_THRESHOLD = new DeviceProperty((short) 0x0043);
    public final static DeviceProperty OPEN_CIRCUIT_EVENT_STATISTICS = new DeviceProperty((short) 0x0044);
    public final static DeviceProperty OUTDOOR_STATISTICAL_VALUES = new DeviceProperty((short) 0x0045);
    public final static DeviceProperty OUTPUT_CURRENT_RANGE = new DeviceProperty((short) 0x0046);
    public final static DeviceProperty OUTPUT_CURRENT_STATISTICS = new DeviceProperty((short) 0x0047);
    public final static DeviceProperty OUTPUT_RIPPLE_VOLTAGE_SPECIFICATION = new DeviceProperty((short) 0x0048);
    public final static DeviceProperty OUTPUT_VOLTAGE_RANGE = new DeviceProperty((short) 0x0049);
    public final static DeviceProperty OUTPUT_VOLTAGE_STATISTICS = new DeviceProperty((short) 0x004A);
    public final static DeviceProperty OVER_OUTPUT_RIPPLE_VOLTAGE_EVENT_STATISTICS = new DeviceProperty((short) 0x004B);
    public final static DeviceProperty PEOPLE_COUNT = new DeviceProperty((short) 0x004C);
    public final static DeviceProperty PRESENCE_DETECTED = new DeviceProperty((short) 0x004D);
    public final static DeviceProperty PRESENT_AMBIENT_LIGHT_LEVEL = new DeviceProperty((short) 0x004E);
    public final static DeviceProperty PRESENT_AMBIENT_TEMPERATURE = new DeviceProperty((short) 0x004F);
    public final static DeviceProperty PRESENT_CIE1931_CHROMATICITY_COORDINATES = new DeviceProperty((short) 0x0050);
    public final static DeviceProperty PRESENT_CORRELATED_COLOR_TEMPERATURE = new DeviceProperty((short) 0x0051);
    public final static DeviceProperty PRESENT_DEVICE_INPUT_POWER = new DeviceProperty((short) 0x0052);
    public final static DeviceProperty PRESENT_DEVICE_OPERATING_EFFICIENCY = new DeviceProperty((short) 0x0053);
    public final static DeviceProperty PRESENT_DEVICE_OPERATING_TEMPERATURE = new DeviceProperty((short) 0x0054);
    public final static DeviceProperty PRESENT_ILLUMINANCE = new DeviceProperty((short) 0x0055);
    public final static DeviceProperty PRESENT_INDOOR_AMBIENT_TEMPERATURE = new DeviceProperty((short) 0x0056);
    public final static DeviceProperty PRESENT_INPUT_CURRENT = new DeviceProperty((short) 0x0057);
    public final static DeviceProperty PRESENT_INPUT_RIPPLE_VOLTAGE = new DeviceProperty((short) 0x0058);
    public final static DeviceProperty PRESENT_INPUT_VOLTAGE = new DeviceProperty((short) 0x0059);
    public final static DeviceProperty PRESENT_LUMINOUS_FLUX = new DeviceProperty((short) 0x005A);
    public final static DeviceProperty PRESENT_OUTDOOR_AMBIENT_TEMPERATURE = new DeviceProperty((short) 0x005B);
    public final static DeviceProperty PRESENT_OUTPUT_CURRENT = new DeviceProperty((short) 0x005C);
    public final static DeviceProperty PRESENT_OUTPUT_VOLTAGE = new DeviceProperty((short) 0x005D);
    public final static DeviceProperty PRESENT_PLANCKIAN_DISTANCE = new DeviceProperty((short) 0x005E);
    public final static DeviceProperty PRESENT_RELATIVE_OUTPUT_RIPPLE_VOLTAGE = new DeviceProperty((short) 0x005F);
    public final static DeviceProperty RELATIVE_DEVICE_ENERGY_USE_IN_A_PERIOD_OF_DAY = new DeviceProperty((short) 0x0060);
    public final static DeviceProperty RELATIVE_DEVICE_RUNTIME_IN_A_GENERIC_LEVEL_RANGE = new DeviceProperty((short) 0x0061);
    public final static DeviceProperty RELATIVE_EXPOSURE_TIME_IN_AN_ILLUMINANCE_RANGE = new DeviceProperty((short) 0x0062);
    public final static DeviceProperty RELATIVE_RUNTIME_IN_A_CORRELATED_COLOR_TEMPERATURE_RANGE = new DeviceProperty((short) 0x0063);
    public final static DeviceProperty RELATIVE_RUNTIME_IN_A_DEVICE_OPERATING_TEMPERATURE_RANGE = new DeviceProperty((short) 0x0064);
    public final static DeviceProperty RELATIVE_RUNTIME_IN_AN_INPUT_CURRENT_RANGE = new DeviceProperty((short) 0x0065);
    public final static DeviceProperty RELATIVE_RUNTIME_IN_AN_INPUT_VOLTAGE_RANGE = new DeviceProperty((short) 0x0066);
    public final static DeviceProperty SHORT_CIRCUIT_EVENT_STATISTICS = new DeviceProperty((short) 0x0067);
    public final static DeviceProperty TIME_SINCE_MOTION_SENSED = new DeviceProperty((short) 0x0068);
    public final static DeviceProperty TIME_SINCE_PRESENCE_DETECTED = new DeviceProperty((short) 0x0069);
    public final static DeviceProperty TOTAL_DEVICE_ENERGY_USE = new DeviceProperty((short) 0x006A);
    public final static DeviceProperty TOTAL_DEVICE_OFF_ON_CYCLES = new DeviceProperty((short) 0x006B);
    public final static DeviceProperty TOTAL_DEVICE_POWER_ON_CYCLES = new DeviceProperty((short) 0x006C);
    public final static DeviceProperty TOTAL_DEVICE_POWER_ON_TIME = new DeviceProperty((short) 0x006D);
    public final static DeviceProperty TOTAL_DEVICE_RUNTIME = new DeviceProperty((short) 0x006E);
    public final static DeviceProperty TOTAL_LIGHT_EXPOSURE_TIME = new DeviceProperty((short) 0x006F);
    public final static DeviceProperty TOTAL_LUMINOUS_ENERGY = new DeviceProperty((short) 0x0070);
    public final static DeviceProperty DESIRED_AMBIENT_TEMPERATURE = new DeviceProperty((short) 0x0071);
    public final static DeviceProperty PRECISE_TOTAL_DEVICE_ENERGY_USE = new DeviceProperty((short) 0x0072);
    public final static DeviceProperty POWER_FACTOR = new DeviceProperty((short) 0x0073);
    public final static DeviceProperty SENSOR_GAIN = new DeviceProperty((short) 0x0074);
    public final static DeviceProperty PRECISE_PRESENT_AMBIENT_TEMPERATURE = new DeviceProperty((short) 0x0075);
    public final static DeviceProperty PRESENT_AMBIENT_RELATIVE_HUMIDITY = new DeviceProperty((short) 0x0076);
    public final static DeviceProperty PRESENT_AMBIENT_CARBONDIOXIDE_CONCENTRATION = new DeviceProperty((short) 0x0077);
    public final static DeviceProperty PRESENT_AMBIENT_VOLATILE_ORGANIC_COMPOUNDS_CONCENTRATION = new DeviceProperty((short) 0x0078);
    public final static DeviceProperty PRESENT_AMBIENT_NOISE = new DeviceProperty((short) 0x0079);
    // These are undefined in mesh device properties v2 = (short) 0x007A
    // These are undefined in mesh device properties v2 = (short) 0x007B
    // These are undefined in mesh device properties v2 = (short) 0x007C
    // These are undefined in mesh device properties v2 = (short) 0x007D
    // These are undefined in mesh device properties v2 = (short) 0x007E
    // These are undefined in mesh device properties v2 = (short) 0x007F
    public final static DeviceProperty ACTIVE_ENERGY_LOAD_SIDE = new DeviceProperty((short) 0x0080);
    public final static DeviceProperty ACTIVE_POWER_LOAD_SIDE = new DeviceProperty((short) 0x0081);
    public final static DeviceProperty AIR_PRESSURE = new DeviceProperty((short) 0x0082);
    public final static DeviceProperty APPARENT_ENERGY = new DeviceProperty((short) 0x0083);
    public final static DeviceProperty APPARENT_POWER = new DeviceProperty((short) 0x0084);
    public final static DeviceProperty APPARENT_WIND_DIRECTION = new DeviceProperty((short) 0x0085);
    public final static DeviceProperty APPARENT_WIND_SPEED = new DeviceProperty((short) 0x0086);
    public final static DeviceProperty DEW_POINT = new DeviceProperty((short) 0x0087);
    public final static DeviceProperty EXTERNAL_SUPPLY_VOLTAGE = new DeviceProperty((short) 0x0088);
    public final static DeviceProperty EXTERNAL_SUPPLY_VOLTAGE_FREQUENCY = new DeviceProperty((short) 0x0089);
    public final static DeviceProperty GUST_FACTOR = new DeviceProperty((short) 0x008A);
    public final static DeviceProperty HEAT_INDEX = new DeviceProperty((short) 0x008B);
    public final static DeviceProperty LIGHT_DISTRIBUTION = new DeviceProperty((short) 0x008C);
    public final static DeviceProperty LIGHT_SOURCE_CURRENT = new DeviceProperty((short) 0x008D);
    public final static DeviceProperty LIGHT_SOURCE_ON_TIME_NOT_RESETTABLE = new DeviceProperty((short) 0x008E);
    public final static DeviceProperty LIGHT_SOURCE_ON_TIME_RESETTABLE = new DeviceProperty((short) 0x008F);
    public final static DeviceProperty LIGHT_SOURCE_OPEN_CIRCUIT_STATISTICS = new DeviceProperty((short) 0x0090);
    public final static DeviceProperty LIGHT_SOURCE_OVERALL_FAILURES_STATISTICS = new DeviceProperty((short) 0x0091);
    public final static DeviceProperty LIGHT_SOURCE_SHORT_CIRCUIT_STATISTICS = new DeviceProperty((short) 0x0092);
    public final static DeviceProperty LIGHT_SOURCE_START_COUNTER_RESETTABLE = new DeviceProperty((short) 0x0093);
    public final static DeviceProperty LIGHT_SOURCE_TEMPERATURE = new DeviceProperty((short) 0x0094);
    public final static DeviceProperty LIGHT_SOURCE_THERMAL_DERATING_STATISTICS = new DeviceProperty((short) 0x0095);
    public final static DeviceProperty LIGHT_SOURCE_THERMAL_SHUTDOWN_STATISTICS = new DeviceProperty((short) 0x0096);
    public final static DeviceProperty LIGHT_SOURCE_TOTAL_POWER_ON_CYCLES = new DeviceProperty((short) 0x0097);
    public final static DeviceProperty LIGHT_SOURCE_VOLTAGE = new DeviceProperty((short) 0x0098);
    public final static DeviceProperty LUMINAIRE_COLOR = new DeviceProperty((short) 0x0099);
    public final static DeviceProperty LUMINAIRE_IDENTIFICATION_NUMBER = new DeviceProperty((short) 0x009A);
    public final static DeviceProperty LUMINAIRE_MANUFACTURER_GTIN = new DeviceProperty((short) 0x009B);
    public final static DeviceProperty LUMINAIRE_NOMINAL_INPUT_POWER = new DeviceProperty((short) 0x009C);
    public final static DeviceProperty LUMINAIRE_NOMINAL_MAXIMUM_AC_MAINS_VOLTAGE = new DeviceProperty((short) 0x009D);
    public final static DeviceProperty LUMINAIRE_NOMINAL_MINIMUM_AC_MAINS_VOLTAGE = new DeviceProperty((short) 0x009E);
    public final static DeviceProperty LUMINAIRE_POWER_AT_MINIMUM_DIM_LEVEL = new DeviceProperty((short) 0x009F);
    public final static DeviceProperty LUMINAIRE_TIME_OF_MANUFACTURE = new DeviceProperty((short) 0x00A0);
    public final static DeviceProperty MAGNETIC_DECLINATION = new DeviceProperty((short) 0x00A1);
    public final static DeviceProperty MAGNETIC_FLUX_DENSITY_2D = new DeviceProperty((short) 0x00A2);
    public final static DeviceProperty MAGNETIC_FLUX_DENSITY_3D = new DeviceProperty((short) 0x00A3);
    public final static DeviceProperty NOMINAL_LIGHT_OUTPUT = new DeviceProperty((short) 0x00A4);
    public final static DeviceProperty OVERALL_FAILURE_CONDITION = new DeviceProperty((short) 0x00A5);
    public final static DeviceProperty POLLEN_CONCENTRATION = new DeviceProperty((short) 0x00A6);
    public final static DeviceProperty PRESENT_INDOOR_RELATIVE_HUMIDITY = new DeviceProperty((short) 0x00A7);
    public final static DeviceProperty PRESENT_OUTDOOR_RELATIVE_HUMIDITY = new DeviceProperty((short) 0x00A8);
    public final static DeviceProperty PRESSURE = new DeviceProperty((short) 0x00A9);
    public final static DeviceProperty RAINFALL = new DeviceProperty((short) 0x00AA);
    public final static DeviceProperty RATED_MEDIAN_USEFUL_LIFE_OF_LUMINAIRE = new DeviceProperty((short) 0x00AB);
    public final static DeviceProperty RATED_MEDIAN_USEFUL_LIGHT_SOURCE_STARTS = new DeviceProperty((short) 0x00AC);
    public final static DeviceProperty REFERENCE_TEMPERATURE = new DeviceProperty((short) 0x00AD);
    public final static DeviceProperty TOTAL_DEVICE_STARTS = new DeviceProperty((short) 0x00AE);
    public final static DeviceProperty TRUE_WIND_DIRECTION = new DeviceProperty((short) 0x00AF);
    public final static DeviceProperty TRUE_WIND_SPEED = new DeviceProperty((short) 0x00B0);
    public final static DeviceProperty UV_INDEX = new DeviceProperty((short) 0x00B1);
    public final static DeviceProperty WIND_CHILL = new DeviceProperty((short) 0x00B2);
    public final static DeviceProperty LIGHT_SOURCE_TYPE = new DeviceProperty((short) 0x00B3);
    public final static DeviceProperty LUMINAIRE_IDENTIFICATION_STRING = new DeviceProperty((short) 0x00B4);
    public final static DeviceProperty OUTPUT_POWER_LIMITATION = new DeviceProperty((short) 0x00B5);
    public final static DeviceProperty THERMAL_DERATING = new DeviceProperty((short) 0x00B6);
    public final static DeviceProperty OUTPUT_CURRENT_PERCENT = new DeviceProperty((short) 0x00B7);

    private final short propertyId;

    DeviceProperty(final short property) {
        this.propertyId = property;
    }

    public short getPropertyId() {
        return this.propertyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceProperty that = (DeviceProperty) o;
        return propertyId == that.propertyId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyId);
    }

    public static DeviceProperty from(final short propertyId) {
        return from(SensorFormat.FORMAT_B, propertyId);
    }

    public static DeviceProperty from(final SensorFormat sensorFormat, final short propertyId) {
        if (sensorFormat == SensorFormat.FORMAT_B) {
            return new DeviceProperty(propertyId);
        } else {
            return new DeviceProperty((short) (propertyId & 0x7FFF));
        }
    }

    public static String getPropertyName(final DeviceProperty deviceProperty) {
        if (AVERAGE_AMBIENT_TEMPERATURE_IN_A_PERIOD_OF_DAY.equals(deviceProperty)) {
            return "Average Ambient Temperature In A Period Of Day";
        } else if (AVERAGE_INPUT_CURRENT.equals(deviceProperty)) {
            return "Average Input Current";
        } else if (AVERAGE_INPUT_VOLTAGE.equals(deviceProperty)) {
            return "Average Input Voltage";
        } else if (AVERAGE_OUTPUT_CURRENT.equals(deviceProperty)) {
            return "Average Output Current";
        } else if (AVERAGE_OUTPUT_VOLTAGE.equals(deviceProperty)) {
            return "Average Output Voltage";
        } else if (CENTER_BEAM_INTENSITY_AT_FULL_POWER.equals(deviceProperty)) {
            return "Center Beam Intensity At Full Power";
        } else if (CHROMATICITY_TOLERANCE.equals(deviceProperty)) {
            return "Chromaticity Tolerance";
        } else if (COLOR_RENDERING_INDEX_R9.equals(deviceProperty)) {
            return "Color Rendering Index R9";
        } else if (COLOR_RENDERING_INDEX_RA.equals(deviceProperty)) {
            return "Color Rendering Index Ra";
        } else if (DEVICE_APPEARANCE.equals(deviceProperty)) {
            return "Device Appearance";
        } else if (DEVICE_COUNTRY_OF_ORIGIN.equals(deviceProperty)) {
            return "Device Country Of Origin";
        } else if (DEVICE_DATE_OF_MANUFACTURE.equals(deviceProperty)) {
            return "Device Date Of Manufacture";
        } else if (DEVICE_ENERGY_USE_SINCE_TURN_ON.equals(deviceProperty)) {
            return "Device Energy Use Since Turn On";
        } else if (DEVICE_FIRMWARE_REVISION.equals(deviceProperty)) {
            return "Device Firmware Revision";
        } else if (DEVICE_GLOBAL_TRADE_ITEM_NUMBER.equals(deviceProperty)) {
            return "Device Global Trade Item Number";
        } else if (DEVICE_HARDWARE_REVISION.equals(deviceProperty)) {
            return "Device Hardware Revision";
        } else if (DEVICE_MANUFACTURER_NAME.equals(deviceProperty)) {
            return "Device Manufacturer Name";
        } else if (DEVICE_MODEL_NUMBER.equals(deviceProperty)) {
            return "Device Model Number";
        } else if (DEVICE_OPERATING_TEMPERATURE_RANGE_SPECIFICATION.equals(deviceProperty)) {
            return "Device Operating Temperature Range Specification";
        } else if (DEVICE_OPERATING_TEMPERATURE_STATISTICAL_VALUES.equals(deviceProperty)) {
            return "Device Operating Temperature Statistical Values";
        } else if (DEVICE_OVER_TEMPERATURE_EVENT_STATISTICS.equals(deviceProperty)) {
            return "Device Over Temperature Event Statistics";
        } else if (DEVICE_POWER_RANGE_SPECIFICATION.equals(deviceProperty)) {
            return "Device Power Range Specification";
        } else if (DEVICE_RUN_TIME_SINCE_TURN_ON.equals(deviceProperty)) {
            return "Device Runtime Since Turn On";
        } else if (DEVICE_RUNTIME_WARRANTY.equals(deviceProperty)) {
            return "Device Runtime Warranty";
        } else if (DEVICE_SERIAL_NUMBER.equals(deviceProperty)) {
            return "Device Serial Number";
        } else if (DEVICE_SOFTWARE_REVISION.equals(deviceProperty)) {
            return "Device Software Revision";
        } else if (DEVICE_UNDER_TEMPERATURE_EVENT_STATISTICS.equals(deviceProperty)) {
            return "Device Under Temperature Event Statistics";
        } else if (INDOOR_AMBIENT_TEMPERATURE_STATISTICAL_VALUES.equals(deviceProperty)) {
            return "Indoor Ambient Temperature Statistical Values";
        } else if (INITIAL_CIE1931_CHROMATICITY_COORDINATES.equals(deviceProperty)) {
            return "Initial CIE 1931 Chromaticity Coordinates";
        } else if (INITIAL_CORRELATED_COLOR_TEMPERATURE.equals(deviceProperty)) {
            return "Initial Correlated Color Temperature";
        } else if (INITIAL_LUMINOUS_FLUX.equals(deviceProperty)) {
            return "Initial Luminous Flux";
        } else if (INITIAL_PLANCKIAN_DISTANCE.equals(deviceProperty)) {
            return "Initial Planckian Distance";
        } else if (INPUT_CURRENT_RANGE_SPECIFICATION.equals(deviceProperty)) {
            return "Input Current Range Specification";
        } else if (INPUT_CURRENT_STATISTICS.equals(deviceProperty)) {
            return "Input Current Statistics";
        } else if (INPUT_OVER_CURRENT_EVENT_STATISTICS.equals(deviceProperty)) {
            return "Input Over Current Event Statistics";
        } else if (INPUT_OVER_RIPPLE_VOLTAGE_EVENT_STATISTICS.equals(deviceProperty)) {
            return "Input Over Ripple Voltage Event Statistics";
        } else if (INPUT_OVER_VOLTAGE_EVENT_STATISTICS.equals(deviceProperty)) {
            return "Input Over Voltage Event Statistics";
        } else if (INPUT_UNDERCURRENT_EVENT_STATISTICS.equals(deviceProperty)) {
            return "Input Under Current Event Statistics";
        } else if (INPUT_UNDER_VOLTAGE_EVENT_STATISTICS.equals(deviceProperty)) {
            return "Input Under Voltage Event Statistics";
        } else if (INPUT_VOLTAGE_RANGE_SPECIFICATION.equals(deviceProperty)) {
            return "Input Voltage Range Specification";
        } else if (INPUT_VOLTAGE_RIPPLE_SPECIFICATION.equals(deviceProperty)) {
            return "Input Voltage Ripple Specification";
        } else if (INPUT_VOLTAGE_STATISTICS.equals(deviceProperty)) {
            return "Input Voltage Statistics";
        } else if (LIGHT_CONTROL_AMBIENT_LUX_LEVEL_ON.equals(deviceProperty)) {
            return "Light Control Ambient LuxLevel On";
        } else if (LIGHT_CONTROL_AMBIENT_LUX_LEVEL_PROLONG.equals(deviceProperty)) {
            return "Light Control Ambient LuxLevel Prolong";
        } else if (LIGHT_CONTROL_AMBIENT_LUX_LEVEL_STANDBY.equals(deviceProperty)) {
            return "Light Control Ambient LuxLevel Standby";
        } else if (LIGHT_CONTROL_LIGHTNESS_ON.equals(deviceProperty)) {
            return "Light Control Lightness On";
        } else if (LIGHT_CONTROL_LIGHTNESS_PROLONG.equals(deviceProperty)) {
            return "Light Control Lightness Prolong";
        } else if (LIGHT_CONTROL_LIGHTNESS_STANDBY.equals(deviceProperty)) {
            return "Light Control Lightness Standby";
        } else if (LIGHT_CONTROL_REGULATOR_ACCURACY.equals(deviceProperty)) {
            return "Light Control Regulator Accuracy";
        } else if (LIGHT_CONTROL_REGULATOR_KID.equals(deviceProperty)) {
            return "Light Control Regulator Kid";
        } else if (LIGHT_CONTROL_REGULATOR_KIU.equals(deviceProperty)) {
            return "Light Control Regulator Kiu";
        } else if (LIGHT_CONTROL_REGULATOR_KPD.equals(deviceProperty)) {
            return "Light Control Regulator Kpd";
        } else if (LIGHT_CONTROL_REGULATOR_KPU.equals(deviceProperty)) {
            return "Light Control Regulator Kpu";
        } else if (LIGHT_CONTROL_TIME_FADE.equals(deviceProperty)) {
            return "Light Control Time Fade";
        } else if (LIGHT_CONTROL_TIME_FADE_ON.equals(deviceProperty)) {
            return "Light Control Time Fade On";
        } else if (LIGHT_CONTROL_TIME_FADE_STANDBY_AUTO.equals(deviceProperty)) {
            return "Light Control Time Fade Standby Auto";
        } else if (LIGHT_CONTROL_TIME_FADE_STANDBY_MANUAL.equals(deviceProperty)) {
            return "Light Control Time Fade Standby Manual";
        } else if (LIGHT_CONTROL_TIME_OCCUPANCY_DELAY.equals(deviceProperty)) {
            return "Light Control Time Occupancy Delay";
        } else if (LIGHT_CONTROL_TIME_PROLONG.equals(deviceProperty)) {
            return "Light Control Time Prolong";
        } else if (LIGHT_CONTROL_TIME_RUN_ON.equals(deviceProperty)) {
            return "Light Control Time Run On";
        } else if (LUMEN_MAINTENANCE_FACTOR.equals(deviceProperty)) {
            return "Lumen Maintenance Factor";
        } else if (LUMINOUS_EFFICACY.equals(deviceProperty)) {
            return "Luminous Efficacy";
        } else if (LUMINOUS_ENERGY_SINCE_TURN_ON.equals(deviceProperty)) {
            return "Luminous Energy Since Turn On";
        } else if (LUMINOUS_EXPOSURE.equals(deviceProperty)) {
            return "Luminous Exposure";
        } else if (LUMINOUS_FLUX_RANGE.equals(deviceProperty)) {
            return "Luminous Flux Range";
        } else if (MOTION_SENSED.equals(deviceProperty)) {
            return "Motion Sensed";
        } else if (MOTION_THRESHOLD.equals(deviceProperty)) {
            return "Motion Threshold";
        } else if (OPEN_CIRCUIT_EVENT_STATISTICS.equals(deviceProperty)) {
            return "Open Circuit Event Statistics";
        } else if (OUTDOOR_STATISTICAL_VALUES.equals(deviceProperty)) {
            return "Outdoor Statistical Values";
        } else if (OUTPUT_CURRENT_RANGE.equals(deviceProperty)) {
            return "Output Current Range";
        } else if (OUTPUT_CURRENT_STATISTICS.equals(deviceProperty)) {
            return "Output Current Statistics";
        } else if (OUTPUT_RIPPLE_VOLTAGE_SPECIFICATION.equals(deviceProperty)) {
            return "Output Ripple Voltage Specification";
        } else if (OUTPUT_VOLTAGE_RANGE.equals(deviceProperty)) {
            return "Output Voltage Range";
        } else if (OUTPUT_VOLTAGE_STATISTICS.equals(deviceProperty)) {
            return "Output Voltage Statistics";
        } else if (OVER_OUTPUT_RIPPLE_VOLTAGE_EVENT_STATISTICS.equals(deviceProperty)) {
            return "Over Output Ripple Voltage Event Statistics";
        } else if (PEOPLE_COUNT.equals(deviceProperty)) {
            return "People Count";
        } else if (PRESENCE_DETECTED.equals(deviceProperty)) {
            return "Presence Detected";
        } else if (PRESENT_AMBIENT_LIGHT_LEVEL.equals(deviceProperty)) {
            return "Present Ambient Light Level";
        } else if (PRESENT_AMBIENT_TEMPERATURE.equals(deviceProperty)) {
            return "Present Ambient Temperature";
        } else if (PRESENT_CIE1931_CHROMATICITY_COORDINATES.equals(deviceProperty)) {
            return "Present CIE 1931 Chromaticity Coordinates";
        } else if (PRESENT_CORRELATED_COLOR_TEMPERATURE.equals(deviceProperty)) {
            return "Present Correlated Color Temperature";
        } else if (PRESENT_DEVICE_INPUT_POWER.equals(deviceProperty)) {
            return "Present Device Input Power";
        } else if (PRESENT_DEVICE_OPERATING_EFFICIENCY.equals(deviceProperty)) {
            return "Present Device Operating Efficiency";
        } else if (PRESENT_DEVICE_OPERATING_TEMPERATURE.equals(deviceProperty)) {
            return "Present Device Operating Temperature";
        } else if (PRESENT_ILLUMINANCE.equals(deviceProperty)) {
            return "Present Illuminance";
        } else if (PRESENT_INDOOR_AMBIENT_TEMPERATURE.equals(deviceProperty)) {
            return "Present Indoor Ambient Temperature";
        } else if (PRESENT_INPUT_CURRENT.equals(deviceProperty)) {
            return "Present Input Current";
        } else if (PRESENT_INPUT_RIPPLE_VOLTAGE.equals(deviceProperty)) {
            return "Present Input Ripple Voltage";
        } else if (PRESENT_INPUT_VOLTAGE.equals(deviceProperty)) {
            return "Present Input Voltage";
        } else if (PRESENT_LUMINOUS_FLUX.equals(deviceProperty)) {
            return "Present Luminous Flux";
        } else if (PRESENT_OUTDOOR_AMBIENT_TEMPERATURE.equals(deviceProperty)) {
            return "Present Outdoor Ambient Temperature";
        } else if (PRESENT_OUTPUT_CURRENT.equals(deviceProperty)) {
            return "Present Output Current";
        } else if (PRESENT_OUTPUT_VOLTAGE.equals(deviceProperty)) {
            return "Present Output Voltage";
        } else if (PRESENT_PLANCKIAN_DISTANCE.equals(deviceProperty)) {
            return "Present Planckian Distance";
        } else if (PRESENT_RELATIVE_OUTPUT_RIPPLE_VOLTAGE.equals(deviceProperty)) {
            return "Present Relative Output Ripple Voltage";
        } else if (RELATIVE_DEVICE_ENERGY_USE_IN_A_PERIOD_OF_DAY.equals(deviceProperty)) {
            return "Relative Device Energy Use In A Period Of Day";
        } else if (RELATIVE_DEVICE_RUNTIME_IN_A_GENERIC_LEVEL_RANGE.equals(deviceProperty)) {
            return "Relative Device Runtime In A Generic Level Range";
        } else if (RELATIVE_EXPOSURE_TIME_IN_AN_ILLUMINANCE_RANGE.equals(deviceProperty)) {
            return "Relative Exposure Time In An Illuminance Range";
        } else if (RELATIVE_RUNTIME_IN_A_CORRELATED_COLOR_TEMPERATURE_RANGE.equals(deviceProperty)) {
            return "Relative Runtime In A Correlated Color Temperature Range";
        } else if (RELATIVE_RUNTIME_IN_A_DEVICE_OPERATING_TEMPERATURE_RANGE.equals(deviceProperty)) {
            return "Relative Runtime In A Device Operating Temperature Range";
        } else if (RELATIVE_RUNTIME_IN_AN_INPUT_CURRENT_RANGE.equals(deviceProperty)) {
            return "Relative Runtime In An Input Current Range";
        } else if (RELATIVE_RUNTIME_IN_AN_INPUT_VOLTAGE_RANGE.equals(deviceProperty)) {
            return "Relative Runtime In An Input Voltage Range";
        } else if (SHORT_CIRCUIT_EVENT_STATISTICS.equals(deviceProperty)) {
            return "Short Circuit Event Statistics";
        } else if (TIME_SINCE_MOTION_SENSED.equals(deviceProperty)) {
            return "Time Since Motion Sensed";
        } else if (TIME_SINCE_PRESENCE_DETECTED.equals(deviceProperty)) {
            return "Time Since Presence Detected";
        } else if (TOTAL_DEVICE_ENERGY_USE.equals(deviceProperty)) {
            return "Total Device Energy Use";
        } else if (TOTAL_DEVICE_OFF_ON_CYCLES.equals(deviceProperty)) {
            return "Total Device Off On Cycles";
        } else if (TOTAL_DEVICE_POWER_ON_CYCLES.equals(deviceProperty)) {
            return "Total Device Power On Cycles";
        } else if (TOTAL_DEVICE_POWER_ON_TIME.equals(deviceProperty)) {
            return "Total Device Power On Time";
        } else if (TOTAL_DEVICE_RUNTIME.equals(deviceProperty)) {
            return "Total Device Runtime";
        } else if (TOTAL_LIGHT_EXPOSURE_TIME.equals(deviceProperty)) {
            return "Total Light Exposure Time";
        } else if (TOTAL_LUMINOUS_ENERGY.equals(deviceProperty)) {
            return "Total Luminous Energy";
        } else if (DESIRED_AMBIENT_TEMPERATURE.equals(deviceProperty)) {
            return "Desired Ambient Temperature";
        } else if (PRECISE_TOTAL_DEVICE_ENERGY_USE.equals(deviceProperty)) {
            return "Precise Total Device Energy Use";
        } else if (POWER_FACTOR.equals(deviceProperty)) {
            return "Power Factor";
        } else if (SENSOR_GAIN.equals(deviceProperty)) {
            return "Sensor Gain";
        } else if (PRECISE_PRESENT_AMBIENT_TEMPERATURE.equals(deviceProperty)) {
            return "Precise Present Ambient Temperature";
        } else if (PRESENT_AMBIENT_RELATIVE_HUMIDITY.equals(deviceProperty)) {
            return "Present Ambient Relative Humidity";
        } else if (PRESENT_AMBIENT_CARBONDIOXIDE_CONCENTRATION.equals(deviceProperty)) {
            return "Present Ambient Carbon Dioxide Concentration";
        } else if (PRESENT_AMBIENT_VOLATILE_ORGANIC_COMPOUNDS_CONCENTRATION.equals(deviceProperty)) {
            return "Present Ambient Volatile Organic Compounds Concentration";
        } else if (PRESENT_AMBIENT_NOISE.equals(deviceProperty)) {
            return "Present Ambient Noise";
        } else if (ACTIVE_ENERGY_LOAD_SIDE.equals(deviceProperty)) {
            return "Active Energy Loadside";
        } else if (ACTIVE_POWER_LOAD_SIDE.equals(deviceProperty)) {
            return "Active Power Loadside";
        } else if (AIR_PRESSURE.equals(deviceProperty)) {
            return "Air Pressure";
        } else if (APPARENT_ENERGY.equals(deviceProperty)) {
            return "Apparent Energy";
        } else if (APPARENT_POWER.equals(deviceProperty)) {
            return "Apparent Power";
        } else if (APPARENT_WIND_DIRECTION.equals(deviceProperty)) {
            return "Apparent Wind Direction";
        } else if (APPARENT_WIND_SPEED.equals(deviceProperty)) {
            return "Apparent Wind Speed";
        } else if (DEW_POINT.equals(deviceProperty)) {
            return "Dew Point";
        } else if (EXTERNAL_SUPPLY_VOLTAGE.equals(deviceProperty)) {
            return "External Supply Voltage";
        } else if (EXTERNAL_SUPPLY_VOLTAGE_FREQUENCY.equals(deviceProperty)) {
            return "External Supply Voltage Frequency";
        } else if (GUST_FACTOR.equals(deviceProperty)) {
            return "Gust Factor";
        } else if (HEAT_INDEX.equals(deviceProperty)) {
            return "Heat Index";
        } else if (LIGHT_DISTRIBUTION.equals(deviceProperty)) {
            return "Light Distribution";
        } else if (LIGHT_SOURCE_CURRENT.equals(deviceProperty)) {
            return "Light Source Current";
        } else if (LIGHT_SOURCE_ON_TIME_NOT_RESETTABLE.equals(deviceProperty)) {
            return "Light Source On Time Not Resettable";
        } else if (LIGHT_SOURCE_ON_TIME_RESETTABLE.equals(deviceProperty)) {
            return "Light Source On Time Resettable";
        } else if (LIGHT_SOURCE_OPEN_CIRCUIT_STATISTICS.equals(deviceProperty)) {
            return "Light Source Open Circuit Statistics";
        } else if (LIGHT_SOURCE_OVERALL_FAILURES_STATISTICS.equals(deviceProperty)) {
            return "Light Source Overall Failures Statistics";
        } else if (LIGHT_SOURCE_SHORT_CIRCUIT_STATISTICS.equals(deviceProperty)) {
            return "Light Source Short Circuit Statistics";
        } else if (LIGHT_SOURCE_START_COUNTER_RESETTABLE.equals(deviceProperty)) {
            return "Light Source Start Counter Resettable";
        } else if (LIGHT_SOURCE_TEMPERATURE.equals(deviceProperty)) {
            return "Light Source Temperature";
        } else if (LIGHT_SOURCE_THERMAL_DERATING_STATISTICS.equals(deviceProperty)) {
            return "Light Source Thermal Derating Statistics";
        } else if (LIGHT_SOURCE_THERMAL_SHUTDOWN_STATISTICS.equals(deviceProperty)) {
            return "Light Source Thermal Shutdown Statistics";
        } else if (LIGHT_SOURCE_TOTAL_POWER_ON_CYCLES.equals(deviceProperty)) {
            return "Light Source Total Power On Cycles";
        } else if (LIGHT_SOURCE_VOLTAGE.equals(deviceProperty)) {
            return "Light Source Voltage";
        } else if (LUMINAIRE_COLOR.equals(deviceProperty)) {
            return "Luminaire Color";
        } else if (LUMINAIRE_IDENTIFICATION_NUMBER.equals(deviceProperty)) {
            return "Luminaire Identification Number";
        } else if (LUMINAIRE_MANUFACTURER_GTIN.equals(deviceProperty)) {
            return "Luminaire Manufacturer GTIN";
        } else if (LUMINAIRE_NOMINAL_INPUT_POWER.equals(deviceProperty)) {
            return "Luminaire Nominal Input Power";
        } else if (LUMINAIRE_NOMINAL_MAXIMUM_AC_MAINS_VOLTAGE.equals(deviceProperty)) {
            return "Luminaire Nominal Maximum AC Mains Voltage";
        } else if (LUMINAIRE_NOMINAL_MINIMUM_AC_MAINS_VOLTAGE.equals(deviceProperty)) {
            return "Luminaire Nominal Minimum AC Mains Voltage";
        } else if (LUMINAIRE_POWER_AT_MINIMUM_DIM_LEVEL.equals(deviceProperty)) {
            return "Luminaire Power At Minimum Dim Level";
        } else if (LUMINAIRE_TIME_OF_MANUFACTURE.equals(deviceProperty)) {
            return "Luminaire Time Of Manufacture";
        } else if (MAGNETIC_DECLINATION.equals(deviceProperty)) {
            return "Magnetic Declination";
        } else if (MAGNETIC_FLUX_DENSITY_2D.equals(deviceProperty)) {
            return "Magnetic Flux Density - 2D";
        } else if (MAGNETIC_FLUX_DENSITY_3D.equals(deviceProperty)) {
            return "Magnetic Flux Density - 3D";
        } else if (NOMINAL_LIGHT_OUTPUT.equals(deviceProperty)) {
            return "Nominal Light Output";
        } else if (OVERALL_FAILURE_CONDITION.equals(deviceProperty)) {
            return "Overall Failure Condition";
        } else if (POLLEN_CONCENTRATION.equals(deviceProperty)) {
            return "Pollen Concentration";
        } else if (PRESENT_INDOOR_RELATIVE_HUMIDITY.equals(deviceProperty)) {
            return "Present Indoor Relative Humidity";
        } else if (PRESENT_OUTDOOR_RELATIVE_HUMIDITY.equals(deviceProperty)) {
            return "Present Outdoor Relative Humidity";
        } else if (PRESSURE.equals(deviceProperty)) {
            return "Pressure";
        } else if (RAINFALL.equals(deviceProperty)) {
            return "Rainfall";
        } else if (RATED_MEDIAN_USEFUL_LIFE_OF_LUMINAIRE.equals(deviceProperty)) {
            return "Rated Median Useful Life Of Luminaire";
        } else if (RATED_MEDIAN_USEFUL_LIGHT_SOURCE_STARTS.equals(deviceProperty)) {
            return "Rated Median Useful Light Source Starts";
        } else if (REFERENCE_TEMPERATURE.equals(deviceProperty)) {
            return "Reference Temperature";
        } else if (TOTAL_DEVICE_STARTS.equals(deviceProperty)) {
            return "Total Device Starts";
        } else if (TRUE_WIND_DIRECTION.equals(deviceProperty)) {
            return "True Wind Direction";
        } else if (TRUE_WIND_SPEED.equals(deviceProperty)) {
            return "True Wind Speed";
        } else if (UV_INDEX.equals(deviceProperty)) {
            return "UV Index";
        } else if (WIND_CHILL.equals(deviceProperty)) {
            return "Wind Chill";
        } else if (LIGHT_SOURCE_TYPE.equals(deviceProperty)) {
            return "Light Source Type";
        } else if (LUMINAIRE_IDENTIFICATION_STRING.equals(deviceProperty)) {
            return "Luminaire Identification String";
        } else if (OUTPUT_POWER_LIMITATION.equals(deviceProperty)) {
            return "Output Power Limitation";
        } else if (THERMAL_DERATING.equals(deviceProperty)) {
            return "Thermal Derating";
        } else if (OUTPUT_CURRENT_PERCENT.equals(deviceProperty)) {
            return "Output Current Percent";
        } else {
            return "Custom Device Property";
        }
    }

    public static DevicePropertyCharacteristic<?> getCharacteristic(@NonNull final DeviceProperty deviceProperty,
                                                                    @NonNull final byte[] data,
                                                                    int offset,
                                                                    final int length) {
        if (PRESENCE_DETECTED.equals(deviceProperty)) {
            return data.length == 0 ? new Bool(false) : new Bool(data[offset] == 0x01);
        } else if (LIGHT_CONTROL_REGULATOR_ACCURACY.equals(deviceProperty) ||
                OUTPUT_RIPPLE_VOLTAGE_SPECIFICATION.equals(deviceProperty) ||
                INPUT_VOLTAGE_RIPPLE_SPECIFICATION.equals(deviceProperty) ||
                OUTPUT_CURRENT_PERCENT.equals(deviceProperty) ||
                LUMEN_MAINTENANCE_FACTOR.equals(deviceProperty) ||
                MOTION_SENSED.equals(deviceProperty) ||
                MOTION_THRESHOLD.equals(deviceProperty) ||
                PRESENT_DEVICE_OPERATING_EFFICIENCY.equals(deviceProperty) ||
                PRESENT_RELATIVE_OUTPUT_RIPPLE_VOLTAGE.equals(deviceProperty) ||
                PRESENT_INPUT_RIPPLE_VOLTAGE.equals(deviceProperty)) {
            return new Percentage8(data, offset);
        } else if (DESIRED_AMBIENT_TEMPERATURE.equals(deviceProperty) ||
                PRESENT_AMBIENT_TEMPERATURE.equals(deviceProperty) ||
                PRESENT_INDOOR_AMBIENT_TEMPERATURE.equals(deviceProperty) ||
                PRESENT_OUTDOOR_AMBIENT_TEMPERATURE.equals(deviceProperty)) {
            return new Temperature(data, offset, 1);
        } else if (PRECISE_PRESENT_AMBIENT_TEMPERATURE.equals(deviceProperty) ||
                PRESENT_DEVICE_OPERATING_TEMPERATURE.equals(deviceProperty)) {
            return new Temperature(data, offset, 2);
        } else if (PEOPLE_COUNT.equals(deviceProperty)) {
            return new Count(data, offset, 2);
        } else if (PRESENT_AMBIENT_RELATIVE_HUMIDITY.equals(deviceProperty) ||
                PRESENT_INDOOR_RELATIVE_HUMIDITY.equals(deviceProperty) ||
                PRESENT_OUTDOOR_RELATIVE_HUMIDITY.equals(deviceProperty)) {
            return new Humidity(data, offset);
        } else if (LIGHT_CONTROL_LIGHTNESS_ON.equals(deviceProperty) ||
                LIGHT_CONTROL_LIGHTNESS_PROLONG.equals(deviceProperty) ||
                LIGHT_CONTROL_LIGHTNESS_STANDBY.equals(deviceProperty)) {
            return new PerceivedLightness(data, offset);
        } else if (TIME_SINCE_MOTION_SENSED.equals(deviceProperty) ||
                TIME_SINCE_PRESENCE_DETECTED.equals(deviceProperty)) {
            return new TimeSecond(data, offset, 2);
        } else if (LIGHT_SOURCE_START_COUNTER_RESETTABLE.equals(deviceProperty) ||
                LIGHT_SOURCE_TOTAL_POWER_ON_CYCLES.equals(deviceProperty) ||
                RATED_MEDIAN_USEFUL_LIGHT_SOURCE_STARTS.equals(deviceProperty) ||
                TOTAL_DEVICE_OFF_ON_CYCLES.equals(deviceProperty) ||
                TOTAL_DEVICE_POWER_ON_CYCLES.equals(deviceProperty) ||
                TOTAL_DEVICE_STARTS.equals(deviceProperty)) {
            return new Count(data, offset, 3);
        } else if (LIGHT_CONTROL_AMBIENT_LUX_LEVEL_ON.equals(deviceProperty) ||
                LIGHT_CONTROL_AMBIENT_LUX_LEVEL_PROLONG.equals(deviceProperty) ||
                LIGHT_CONTROL_AMBIENT_LUX_LEVEL_STANDBY.equals(deviceProperty) ||
                PRESENT_AMBIENT_LIGHT_LEVEL.equals(deviceProperty) ||
                PRESENT_ILLUMINANCE.equals(deviceProperty)) {
            return new Illuminance(data, offset);
        } else if (DEVICE_RUN_TIME_SINCE_TURN_ON.equals(deviceProperty) ||
                DEVICE_RUNTIME_WARRANTY.equals(deviceProperty) ||
                RATED_MEDIAN_USEFUL_LIFE_OF_LUMINAIRE.equals(deviceProperty) ||
                TOTAL_DEVICE_POWER_ON_TIME.equals(deviceProperty) ||
                TOTAL_DEVICE_RUNTIME.equals(deviceProperty) ||
                TOTAL_LIGHT_EXPOSURE_TIME.equals(deviceProperty)) {
            return new TimeHour24(data, offset);
        } else if (LIGHT_CONTROL_TIME_FADE.equals(deviceProperty) ||
                LIGHT_CONTROL_TIME_FADE_ON.equals(deviceProperty) ||
                LIGHT_CONTROL_TIME_FADE_STANDBY_AUTO.equals(deviceProperty) ||
                LIGHT_CONTROL_TIME_FADE_STANDBY_MANUAL.equals(deviceProperty) ||
                LIGHT_CONTROL_TIME_OCCUPANCY_DELAY.equals(deviceProperty) ||
                LIGHT_CONTROL_TIME_PROLONG.equals(deviceProperty) ||
                LIGHT_CONTROL_TIME_RUN_ON.equals(deviceProperty)) {
            return new TimeMillisecond24(data, offset);
        } else if (DEVICE_DATE_OF_MANUFACTURE.equals(deviceProperty) ||
                LUMINAIRE_TIME_OF_MANUFACTURE.equals(deviceProperty)) {
            return new DateUtc(data, offset);
        } else if (PRESSURE.equals(deviceProperty) ||
                AIR_PRESSURE.equals(deviceProperty)) {
            return new Pressure(data, offset);
        } else if (LIGHT_CONTROL_REGULATOR_KID.equals(deviceProperty) ||
                LIGHT_CONTROL_REGULATOR_KIU.equals(deviceProperty) ||
                LIGHT_CONTROL_REGULATOR_KPD.equals(deviceProperty) ||
                LIGHT_CONTROL_REGULATOR_KPU.equals(deviceProperty) ||
                SENSOR_GAIN.equals(deviceProperty)) {
            return new Coefficient(data, offset);
        } else if (DEVICE_FIRMWARE_REVISION.equals(deviceProperty) ||
                DEVICE_SOFTWARE_REVISION.equals(deviceProperty)) {
            return new FixedString(data, offset, 8);
        } else if (DEVICE_HARDWARE_REVISION.equals(deviceProperty) ||
                DEVICE_SERIAL_NUMBER.equals(deviceProperty)) {
            return new FixedString(data, offset, 16);
        } else if (DEVICE_MODEL_NUMBER.equals(deviceProperty) ||
                LUMINAIRE_COLOR.equals(deviceProperty) ||
                LUMINAIRE_IDENTIFICATION_NUMBER.equals(deviceProperty)) {
            return new FixedString(data, offset, 24);
        } else if (DEVICE_MANUFACTURER_NAME.equals(deviceProperty)) {
            return new FixedString(data, offset, 36);
        } else if (LUMINAIRE_IDENTIFICATION_STRING.equals(deviceProperty)) {
            return new FixedString(data, offset, 64);
        } else if (ACTIVE_ENERGY_LOAD_SIDE.equals(deviceProperty) ||
                PRECISE_TOTAL_DEVICE_ENERGY_USE.equals(deviceProperty)) {
            return new Energy32(data, offset);
        } else if (ACTIVE_POWER_LOAD_SIDE.equals(deviceProperty) ||
                LUMINAIRE_NOMINAL_INPUT_POWER.equals(deviceProperty) ||
                LUMINAIRE_POWER_AT_MINIMUM_DIM_LEVEL.equals(deviceProperty) ||
                PRESENT_DEVICE_INPUT_POWER.equals(deviceProperty)) {
            return new Power(data, offset);
        } else if (PRESENT_INPUT_CURRENT.equals(deviceProperty) ||
                PRESENT_OUTPUT_CURRENT.equals(deviceProperty)) {
            return new ElectricCurrent(data, offset);
        } else {
            return new UnknownCharacteristic(data, offset, length);
        }
    }
}