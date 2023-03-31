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
 * The device state entity object.
 **/
public class DeviceStateEntity {

    private String id;
    private String tenantId;
    private String deviceId;
    private String updateTime;
    private String binaryData;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
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

    @JsonProperty("updateTime")
    public String getUpdateTime() {
        return updateTime;
    }

    @JsonProperty("update_time")
    public void setUpdateTime(final String updateTime) {
        this.updateTime = updateTime;
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
        return "DeviceStateEntity{" +
                "id=" + id +
                ", tenantId='" + tenantId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", updateTime='" + updateTime + '\'' +
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
        final DeviceStateEntity that = (DeviceStateEntity) o;
        return id.equals(that.id) && tenantId.equals(that.tenantId) && deviceId.equals(that.deviceId)
                && updateTime.equals(that.updateTime) && binaryData.equals(that.binaryData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId, deviceId, updateTime, binaryData);
    }

}
