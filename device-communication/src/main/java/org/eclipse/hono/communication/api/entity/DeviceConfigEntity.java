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

package org.eclipse.hono.communication.api.entity;

import java.util.Objects;

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

    public DeviceConfigEntity(int version,
                              String tenantId,
                              String deviceId,
                              String cloudUpdateTime,
                              String deviceAckTime,
                              String binaryData) {
        this.version = version;
        this.tenantId = tenantId;
        this.deviceId = deviceId;
        this.cloudUpdateTime = cloudUpdateTime;
        this.deviceAckTime = deviceAckTime;
        this.binaryData = binaryData;
    }

    public DeviceConfigEntity(String tenantId, String deviceId, DeviceConfig deviceConfig) {
        this.version = Integer.parseInt(deviceConfig.getVersion());
        this.cloudUpdateTime = deviceConfig.getCloudUpdateTime();
        this.deviceAckTime = deviceConfig.getDeviceAckTime();
        this.binaryData = deviceConfig.getBinaryData();
    }

    public DeviceConfigEntity() {
    }


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public String getCloudUpdateTime() {
        return cloudUpdateTime;
    }

    public void setCloudUpdateTime(String cloudUpdateTime) {
        this.cloudUpdateTime = cloudUpdateTime;
    }

    public String getDeviceAckTime() {
        return deviceAckTime;
    }

    public void setDeviceAckTime(String deviceAckTime) {
        this.deviceAckTime = deviceAckTime;
    }

    public String getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(String binaryData) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceConfigEntity that = (DeviceConfigEntity) o;
        return version == that.version && tenantId.equals(that.tenantId) && deviceId.equals(that.deviceId) && cloudUpdateTime.equals(that.cloudUpdateTime) && Objects.equals(deviceAckTime, that.deviceAckTime) && binaryData.equals(that.binaryData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, tenantId, deviceId, cloudUpdateTime, deviceAckTime, binaryData);
    }


}
