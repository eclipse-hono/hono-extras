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
 * Device config response acknowledgment object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceConfigAckResponse {
    private String version;
    private String tenantId;
    private String deviceId;
    public DeviceConfigAckResponse() {
    }
    public DeviceConfigAckResponse(String version, String tenantId, String deviceId) {
        this.version = version;
        this.tenantId = tenantId;
        this.deviceId = deviceId;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    @JsonProperty("tenantId")
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @JsonProperty("deviceId")
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "DeviceConfigAckResponse{" +
                "version='" + version + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceConfigAckResponse that = (DeviceConfigAckResponse) o;
        return version.equals(that.version) && tenantId.equals(that.tenantId) && deviceId.equals(that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, tenantId, deviceId);
    }

}
