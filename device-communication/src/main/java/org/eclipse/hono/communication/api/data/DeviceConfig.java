/*
 * ***********************************************************
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
 *  <p>
 *  See the NOTICE file(s) distributed with this work for additional
 *  information regarding copyright ownership.
 *  <p>
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0
 *  <p>
 *  SPDX-License-Identifier: EPL-2.0
 * **********************************************************
 *
 */

package org.eclipse.hono.communication.api.data;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * The device configuration.
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceConfig {

    private String version;
    private String cloudUpdateTime;
    private String deviceAckTime;
    private String binaryData;

    /**
     * Creates a new device config.
     */
    public DeviceConfig() {

    }

    /**
     * Creates a new device config.
     *
     * @param version         Config version
     * @param cloudUpdateTime Cloud update time
     * @param deviceAckTime   Device ack time
     * @param binaryData      Binary data
     */
    public DeviceConfig(final String version, final String cloudUpdateTime, final String deviceAckTime, final String binaryData) {
        this.version = version;
        this.cloudUpdateTime = cloudUpdateTime;
        this.deviceAckTime = deviceAckTime;
        this.binaryData = binaryData;
    }


    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(final String version) {
        this.version = version;
    }


    @JsonProperty("cloudUpdateTime")
    public String getCloudUpdateTime() {
        return cloudUpdateTime;
    }

    @JsonProperty("cloud_update_time")
    public void setCloudUpdateTime(final String cloudUpdateTime) {
        this.cloudUpdateTime = cloudUpdateTime;
    }


    @JsonProperty("deviceAckTime")
    public String getDeviceAckTime() {
        return deviceAckTime;
    }

    @JsonProperty("device_ack_time")
    public void setDeviceAckTime(final String deviceAckTime) {
        this.deviceAckTime = deviceAckTime;
    }


    @JsonProperty("binaryData")
    public String getBinaryData() {
        return binaryData;
    }

    @JsonProperty("binary_data")
    public void setBinaryData(final String binaryData) {
        this.binaryData = binaryData;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DeviceConfig deviceConfig = (DeviceConfig) o;
        return Objects.equals(version, deviceConfig.version) &&
                Objects.equals(cloudUpdateTime, deviceConfig.cloudUpdateTime) &&
                Objects.equals(deviceAckTime, deviceConfig.deviceAckTime) &&
                Objects.equals(binaryData, deviceConfig.binaryData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, cloudUpdateTime, deviceAckTime, binaryData);
    }

    @Override
    public String toString() {

        return "class DeviceConfig {\n" +
                "    version: " + toIndentedString(version) + "\n" +
                "    cloudUpdateTime: " + toIndentedString(cloudUpdateTime) + "\n" +
                "    deviceAckTime: " + toIndentedString(deviceAckTime) + "\n" +
                "    binaryData: " + toIndentedString(binaryData) + "\n" +
                "}";
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(final Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
