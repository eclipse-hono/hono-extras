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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The device configuration entity object.
 **/
public class DeviceConfigEntity {


    private int version;
    private String tenantId;
    private String deviceId;

    private String cloudUpdateTime;
    private String deviceAckTime;
    private String binaryData;


    /**
     * Creates new DeviceConfigEntity.
     */
    public DeviceConfigEntity() {
    }


    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    @JsonProperty("tenantId")
    public String getTenantId() {
        return tenantId;
    }

    @JsonProperty("tenant_id")
    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    @JsonProperty("deviceId")
    public String getDeviceId() {
        return deviceId;
    }

    @JsonProperty("device_id")
    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
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
    public String toString() {
        return "DeviceConfigEntity{" +
                "version=" + version +
                ", tenantId='" + tenantId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", cloudUpdateTime='" + cloudUpdateTime + '\'' +
                ", deviceAckTime='" + deviceAckTime + '\'' +
                ", binaryData='" + binaryData + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final var that = (DeviceConfigEntity) o;
        return version == that.version && tenantId.equals(that.tenantId) && deviceId.equals(that.deviceId) && cloudUpdateTime.equals(that.cloudUpdateTime) && Objects.equals(deviceAckTime, that.deviceAckTime) && binaryData.equals(that.binaryData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, tenantId, deviceId, cloudUpdateTime, deviceAckTime, binaryData);
    }


}
