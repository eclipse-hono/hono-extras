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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * The device configuration.
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceConfig {

    private String version;
    private String cloudUpdateTime;
    private String deviceAckTime;
    private String binaryData;

    public DeviceConfig() {

    }

    public DeviceConfig(String version, String cloudUpdateTime, String deviceAckTime, String binaryData) {
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
    public void setVersion(String version) {
        this.version = version;
    }


    @JsonProperty("cloudUpdateTime")
    public String getCloudUpdateTime() {
        return cloudUpdateTime;
    }

    @JsonProperty("cloud_update_time")
    public void setCloudUpdateTime(String cloudUpdateTime) {
        this.cloudUpdateTime = cloudUpdateTime;
    }


    @JsonProperty("deviceAckTime")
    public String getDeviceAckTime() {
        return deviceAckTime;
    }

    @JsonProperty("device_ack_time")
    public void setDeviceAckTime(String deviceAckTime) {
        this.deviceAckTime = deviceAckTime;
    }


    @JsonProperty("binaryData")
    public String getBinaryData() {
        return binaryData;
    }

    @JsonProperty("binary_data")
    public void setBinaryData(String binaryData) {
        this.binaryData = binaryData;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeviceConfig deviceConfig = (DeviceConfig) o;
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
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
