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

package no.nordicsemi.android.mesh.opcodes;

public class ApplicationMessageOpCodes {

    /**
     * Opcode for the "Generic OnOff Get" message.
     */
    public static final int GENERIC_ON_OFF_GET = 0x8201;

    /**
     * Opcode for the "Generic OnOff Set" message.
     */
    public static final int GENERIC_ON_OFF_SET = 0x8202;

    /**
     * Opcode for the "Generic OnOff Set Unacknowledged" message.
     */
    public static final int GENERIC_ON_OFF_SET_UNACKNOWLEDGED = 0x8203;

    /**
     * Opcode for the "Generic OnOff Status" message.
     */
    public static final int GENERIC_ON_OFF_STATUS = 0x8204;

    /**
     * Opcode for the "Generic Level Get" message.
     */
    public static final int GENERIC_LEVEL_GET = 0x8205;

    /**
     * Opcode for the "Generic Level Set" message.
     */
    public static final int GENERIC_LEVEL_SET = 0x8206;

    /**
     * Opcode for the "Generic Delta Set" message.
     */
    public static final int GENERIC_DELTA_SET = 0x8209;

    /**
     * Opcode for the "Generic Power Level Get" message.
     */
    public static final int GENERIC_POWER_LEVEL_GET = 0x8215;

    /**
     * Opcode for the "Generic Power Level Set" message.
     */
    public static final int GENERIC_POWER_LEVEL_SET = 0x8216;

    /**
     * Opcode for the "Generic Power Level Set Unacknowledged" message.
     */
    public static final int GENERIC_POWER_LEVEL_SET_UNACKNOWLEDGED = 0x8217;

    /**
     * Opcode for the "Generic Power Level Status" message.
     */
    public static final int GENERIC_POWER_LEVEL_STATUS = 0x8218;

    /**
     * Opcode for the "Generic Location Global Get" message
     */
    public static final int GENERIC_LOCATION_GLOBAL_GET = 0x8225;

    /**
     * Opcode for the "Generic Location Global Status" message
     */
    public static final int GENERIC_LOCATION_GLOBAL_STATUS = 0x40;

    /**
     * Opcode for the "Generic Location Global Set" message
     */
    public static final int GENERIC_LOCATION_GLOBAL_SET = 0x41;

    /**
     * Opcode for the "Generic Location Global Set Unacknowledged" message
     */
    public static final int GENERIC_LOCATION_GLOBAL_SET_UNACKNOWLEDGED = 0x42;

    /**
     * Opcode for the "Generic Level Set Unacknowledged" message.
     */
    public static final int GENERIC_LEVEL_SET_UNACKNOWLEDGED = 0x8207;

    /**
     * Opcode for the "Generic Level Status" message.
     */
    public static final int GENERIC_LEVEL_STATUS = 0x8208;

    /**
     * Opcode for the "Generic Battery Get" message.
     */
    public static final int GENERIC_BATTERY_GET = 0x8223;

    /**
     * Opcode for the "Generic Battery Status" message.
     */
    public static final int GENERIC_BATTERY_STATUS = 0x8224;

    /**
     * Opcode for the "Light Lightness Get" message
     */
    public static final int LIGHT_LIGHTNESS_GET = 0x824B;

    /**
     * Opcode for the "Light Lightness Set" message
     */
    public static final int LIGHT_LIGHTNESS_SET = 0x824C;

    /**
     * Opcode for the "Light Lightness Set Unacknowledged" message
     */
    public static final int LIGHT_LIGHTNESS_SET_UNACKNOWLEDGED = 0x824D;

    /**
     * Opcode for the "Light Lightness Status" message
     */
    public static final int LIGHT_LIGHTNESS_STATUS = 0x824E;

    /**
     * Opcode for the "Light Ctl Get" message
     */
    public static final int LIGHT_CTL_GET = 0x825D;

    /**
     * Opcode for the "Light Ctl Set" message
     */
    public static final int LIGHT_CTL_SET = 0x825E;

    /**
     * Opcode for the "Light Ctl Set Unacknowledged" message
     */
    public static final int LIGHT_CTL_SET_UNACKNOWLEDGED = 0x825F;

    /**
     * Opcode for the "Light Ctl Status" message
     */
    public static final int LIGHT_CTL_STATUS = 0x8260;

    /**
     * Opcode for the "Light Ctl Temperature Range Get" message
     */
    public static final int LIGHT_CTL_TEMPERATURE_RANGE_GET = 0x8262;

    /**
     * Opcode for the "Light Ctl Temperature Range Status" message
     */
    public static final int LIGHT_CTL_TEMPERATURE_RANGE_STATUS = 0x8263;

    /**
     * Opcode for the "Light Ctl Temperature Range Set" message
     */
    public static final int LIGHT_CTL_TEMPERATURE_RANGE_SET = 0x826B;

    /**
     * Opcode for the "Light Ctl Temperature Range Set Unacknowledged" message
     */
    public static final int LIGHT_CTL_TEMPERATURE_RANGE_SET_UNACKNOWLEDGED = 0x826C;

    /**
     * Opcode for the "Light Hsl Get" message
     */
    public static final int LIGHT_HSL_GET = 0x826D;

    /**
     * Opcode for the "Light Hsl Set" message
     */
    public static final int LIGHT_HSL_SET = 0x8276;

    /**
     * Opcode for the "Light Hsl Set Unacknowledged" message
     */
    public static final int LIGHT_HSL_SET_UNACKNOWLEDGED = 0x8277;

    /**
     * Opcode for the "Light Hsl Status" message
     */
    public static final int LIGHT_HSL_STATUS = 0x8278;

    /**
     * Opcode for the "Light LC Mode Get" message
     */
    public static final int LIGHT_LC_MODE_GET = 0x8291;

    /**
     * Opcode for the "Light LC Mode Set" message
     */
    public static final int LIGHT_LC_MODE_SET = 0x8292;

    /**
     * Opcode for the "Light LC Mode Set Unacknowledged" message
     */
    public static final int LIGHT_LC_MODE_SET_UNACKNOWLEDGED = 0x8293;

    /**
     * Opcode for the "Light LC Mode Status" message
     */
    public static final int LIGHT_LC_MODE_STATUS = 0x8294;

    /**
     * Opcode for the "Light LC Occupancy Mode Get" message
     */
    public static final int LIGHT_LC_OCCUPANCY_MODE_GET = 0x8295;

    /**
     * Opcode for the "Light LC Occupancy Mode Set" message
     */
    public static final int LIGHT_LC_OCCUPANCY_MODE_SET = 0x8296;

    /**
     * Opcode for the "Light LC Occupancy Mode Set Unacknowledged" message
     */
    public static final int LIGHT_LC_OCCUPANCY_MODE_SET_UNACKNOWLEDGED = 0x8297;

    /**
     * Opcode for the "Light LC Occupancy Mode Status" message
     */
    public static final int LIGHT_LC_OCCUPANCY_MODE_STATUS = 0x8298;

    /**
     * Opcode for the "Light LC Light On Off Get" message
     */
    public static final int LIGHT_LC_LIGHT_ON_OFF_GET = 0x8299;

    /**
     * Opcode for the "Light LC Light On Off Set" message
     */
    public static final int LIGHT_LC_LIGHT_ON_OFF_SET = 0x829A;

    /**
     * Opcode for the "Light LC Light On Off Set Unacknowledged" message
     */
    public static final int LIGHT_LC_LIGHT_ON_OFF_SET_UNACKNOWLEDGED = 0x829B;

    /**
     * Opcode for the "Light LC Light On Off Status" message
     */
    public static final int LIGHT_LC_LIGHT_ON_OFF_STATUS = 0x829C;

    /**
     * Opcode for the "Light LC Property Get" message
     */
    public static final int LIGHT_LC_PROPERTY_GET = 0x829D;

    /**
     * Opcode for the "Light LC Property Set" message
     */
    public static final int LIGHT_LC_PROPERTY_SET = 0x62;

    /**
     * Opcode for the "Light LC Property Set Unacknowledged" message
     */
    public static final int LIGHT_LC_PROPERTY_SET_UNACKNOWLEDGED = 0x63;

    /**
     * Opcode for the "Light LC Property Status" message
     */
    public static final int LIGHT_LC_PROPERTY_STATUS = 0x64;

    /**
     * Opcode for the "Scene Status" message
     */
    public static final int SCENE_STATUS = 0x5E;

    /**
     * Opcode for the "Scene Register Status" message
     */
    public static final int SCENE_REGISTER_STATUS = 0x8245;

    /**
     * Opcode for the "Scene Get" message
     */
    public static final int SCENE_GET = 0x8241;

    /**
     * Opcode for the "Scene Register Get" message
     */
    public static final int SCENE_REGISTER_GET = 0x8244;

    /**
     * Opcode for the "Scene Recall" message
     */
    public static final int SCENE_RECALL = 0x8242;

    /**
     * Opcode for the "Scene Recall Unacknowledged" message
     */
    public static final int SCENE_RECALL_UNACKNOWLEDGED = 0x8243;

    /**
     * Opcode for the "Scene Store" message
     */
    public static final int SCENE_STORE = 0x8246;

    /**
     * Opcode for the "Scene Store Unacknowledged" message
     */
    public static final int SCENE_STORE_UNACKNOWLEDGED = 0x8247;

    /**
     * Opcode for the "Scene Delete" message
     */
    public static final int SCENE_DELETE = 0x829E;

    /**
     * Opcode for the "Scene Delete Unacknowledged" message
     */
    public static final int SCENE_DELETE_UNACKNOWLEDGED = 0x829F;

    /**
     * Opcode for the "Sensor Descriptor Get" message
     */
    public static final int SENSOR_DESCRIPTOR_GET = 0x8230;

    /**
     * Opcode for the "Sensor Descriptor Status" message
     */
    public static final int SENSOR_DESCRIPTOR_STATUS = 0x51;

    /**
     * Opcode for the "Sensor Get" message
     */
    public static final int SENSOR_GET = 0x8231;

    /**
     * Opcode for the "Sensor Status" message
     */
    public static final int SENSOR_STATUS = 0x52;

    /**
     * Opcode for the "Sensor Column Get" message
     */
    public static final int SENSOR_COLUMN_GET = 0x8232;

    /**
     * Opcode for the "Sensor Status" message
     */
    public static final int SENSOR_COLUMN_STATUS = 0x53;

    /**
     * Opcode for the "Sensor Series Get" message
     */
    public static final int SENSOR_SERIES_GET = 0x8233;

    /**
     * Opcode for the "Sensor Series Status" message
     */
    public static final int SENSOR_SERIES_STATUS = 0x54;

    /**
     * Opcode for the "Sensor Cadence Get" message
     */
    public static final int SENSOR_CADENCE_GET = 0x8234;

    /**
     * Opcode for the "Sensor Cadence Set" message
     */
    public static final int SENSOR_CADENCE_SET = 0x55;

    /**
     * Opcode for the "Sensor Cadence Set Unacknowledged" message
     */
    public static final int SENSOR_CADENCE_SET_UNACKNOWLEDGED = 0x56;
    /**
     * Opcode for the "Sensor Cadence Status" message
     */
    public static final int SENSOR_CADENCE_STATUS = 0x57;

    /**
     * Opcode for the "Sensor Settings Get" message
     */
    public static final int SENSOR_SETTINGS_GET = 0x8235;

    /**
     * Opcode for the "Sensor Settings Status" message
     */
    public static final int SENSOR_SETTINGS_STATUS = 0x58;

    /**
     * Opcode for the "Sensor Setting Get" message
     */
    public static final int SENSOR_SETTING_GET = 0x8236;

    /**
     * Opcode for the "Sensor Setting Set" message
     */
    public static final int SENSOR_SETTING_SET = 0x59;

    /**
     * Opcode for the "Sensor Setting Set" message
     */
    public static final int SENSOR_SETTING_SET_UNACKNOWLEDGED = 0x5A;

    /**
     * Opcode for the "Sensor Settings Status" message
     */
    public static final int SENSOR_SETTING_STATUS = 0x5B;

    /**
     * Opcode for the "Scheduler Get" message
     */
    public static final int SCHEDULER_GET = 0x8249;

    /**
     * Opcode for the "Scheduler Status" message
     */
    public static final int SCHEDULER_STATUS = 0x824A;

    /**
     * Opcode for the "Scheduler Action Get" message
     */
    public static final int SCHEDULER_ACTION_GET = 0x8248;

    /**
     * Opcode for the "Scheduler Action Set" message
     */
    public static final int SCHEDULER_ACTION_SET = 0x60;

    /**
     * Opcode for the "Scheduler Action Status" message
     */
    public static final int SCHEDULER_ACTION_STATUS = 0x5F;

    /**
     * Opcode for the "Time Zone Get" message
     */
    public static final int TIME_ZONE_GET = 0x823B;

    /**
     * Opcode for the "Time Zone Set" message
     */
    public static final int TIME_ZONE_SET = 0x823C;

    /**
     * Opcode for the "Time Zone Status" message
     */
    public static final int TIME_ZONE_STATUS = 0x823D;

    /**
     * Opcode for the "Generic Default Transition Time Get" message
     */
    public static final int GENERIC_DEFAULT_TRANSITION_TIME_GET = 0x820D;

    /**
     * Opcode for the "Generic Default Transition Time Set" message
     */
    public static final int GENERIC_DEFAULT_TRANSITION_TIME_SET = 0x820E;

    /**
     * Opcode for the "Generic Default Transition Time Status" message
     */
    public static final int GENERIC_DEFAULT_TRANSITION_TIME_STATUS = 0x8210;

    /**
     * Opcode for the "OnPowerUp Get" message
     */
    public static final int GENERIC_ON_POWER_UP_GET = 0x8211;

    /**
     * Opcode for the "OnPowerUp Set" message
     */
    public static final int GENERIC_ON_POWER_UP_SET = 0x8213;

    /**
     * Opcode for the "OnPowerUp Status" message
     */
    public static final int GENERIC_ON_POWER_UP_STATUS = 0x8212;

    /**
     * Opcode for the "Generic Admin Property Status" message
     */
    public static final int GENERIC_ADMIN_PROPERTY_STATUS = 0x4A;

    /**
     * Opcode for the "Generic Manufacturer Property Status" message
     */
    public static final int GENERIC_MANUFACTURER_PROPERTY_STATUS = 0x46;

    /**
     * Opcode for the "Generic User Property Status" message
     */
    public static final int GENERIC_USER_PROPERTY_STATUS = 0x4E;

    /**
     * Opcode for the "Generic User Property Set" message
     */
    public static final int GENERIC_USER_PROPERTY_SET = 0x4C;

    /**
     * Opcode for the "Generic Admin Property Set" message
     */
    public static final int GENERIC_ADMIN_PROPERTY_SET = 0x48;

    /**
     * Opcode for the "Generic Manufacturer Property Set" message
     */
    public static final int GENERIC_MANUFACTURER_PROPERTY_SET = 0x44;

    /**
     * Opcode for the "Time Get" message
     */
    public static final int TIME_GET = 0x8237;

    /**
     * Opcode for the "Time Set" message
     */
    public static final int TIME_SET = 0x5C;

    /**
     * Opcode for the "Time Status" message
     */
    public static final int TIME_STATUS = 0x5D;


    /**
     * Opcode for the "Health Current Status" message
     */
    public static final int HEALTH_CURRENT_STATUS = 0x04;

    /**
     * Opcode for the "Health Fault Status" message
     */
    public static final int HEALTH_FAULT_STATUS = 0x05;
}
