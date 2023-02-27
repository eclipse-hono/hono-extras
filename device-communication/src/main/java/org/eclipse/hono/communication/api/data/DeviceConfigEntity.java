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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }


    public String getCloudUpdateTime() {
        return cloudUpdateTime;
    }

    public void setCloudUpdateTime(final String cloudUpdateTime) {
        this.cloudUpdateTime = cloudUpdateTime;
    }

    public String getDeviceAckTime() {
        return deviceAckTime;
    }

    public void setDeviceAckTime(final String deviceAckTime) {
        this.deviceAckTime = deviceAckTime;
    }

    public String getBinaryData() {
        return binaryData;
    }

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
