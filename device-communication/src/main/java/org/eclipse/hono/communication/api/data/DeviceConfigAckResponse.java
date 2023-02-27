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
 * Device config response acknowledgment object.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceConfigAckResponse {
    private String version;
    private String tenantId;
    private String deviceId;

    /**
     * Creates a new DeviceConfigAckResponse.
     */
    public DeviceConfigAckResponse() {
    }

    /**
     * Creates a new DeviceConfigAckResponse.
     *
     * @param version  Device config version
     * @param tenantId Tenant id
     * @param deviceId device id
     */
    public DeviceConfigAckResponse(final String version, final String tenantId, final String deviceId) {
        this.version = version;
        this.tenantId = tenantId;
        this.deviceId = deviceId;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }


    @JsonProperty("tenantId")
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    @JsonProperty("deviceId")
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final String deviceId) {
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DeviceConfigAckResponse that = (DeviceConfigAckResponse) o;
        return version.equals(that.version) && tenantId.equals(that.tenantId) && deviceId.equals(that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, tenantId, deviceId);
    }

}
