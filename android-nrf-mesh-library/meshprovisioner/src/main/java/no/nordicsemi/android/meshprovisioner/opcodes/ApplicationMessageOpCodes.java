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

package no.nordicsemi.android.meshprovisioner.opcodes;

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
    public static final short GENERIC_ON_OFF_STATUS = (short) 0x8204;
    //config relay
    public static final int CONFIG_RELAY_GET = 0x8026;
    public static final int CONFIG_RELAY_SET = 0x8027;
    public static final int CONFIG_RELAY_STATUS = 0x8028;
    //config network transmit
    public static final int CONFIG_NETWORK_TRANSMIT_GET = 0x8023;
    public static final int CONFIG_NETWORK_TRANSMIT_SET = 0x8024;
    public static final int CONFIG_NETWORK_TRANSMIT_STATUS = 0x8025;

    //config default ttl
    public static final int CONFIG_DEFAULT_TTL_GET = 0x800C;
    public static final int CONFIG_DEFAULT_TTL_SET = 0x800D;
    public static final int CONFIG_DEFAULT_TTL_STATUS = 0x800E;

    //config gatt proxy
    public static final int CONFIG_GATT_PROXY_GET = 0x8012;
    public static final int CONFIG_GATT_PROXY_SET = 0x8013;
    public static final int CONFIG_GATT_PROXY_STATUS = 0x8014;
}
