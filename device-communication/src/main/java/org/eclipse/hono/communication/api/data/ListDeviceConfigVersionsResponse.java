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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * A list of a device config versions.
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListDeviceConfigVersionsResponse {

    private List<DeviceConfig> deviceConfigs = new ArrayList<>();

    /**
     * Creates a new ListDeviceConfigVersionsResponse.
     */
    public ListDeviceConfigVersionsResponse() {

    }

    /**
     * Creates a new ListDeviceConfigVersionsResponse.
     *
     * @param deviceConfigs The device configs
     */
    public ListDeviceConfigVersionsResponse(final List<DeviceConfig> deviceConfigs) {
        this.deviceConfigs = deviceConfigs;
    }


    @JsonProperty("deviceConfigs")
    public List<DeviceConfig> getDeviceConfigs() {
        return deviceConfigs;
    }

    public void setDeviceConfigs(final List<DeviceConfig> deviceConfigs) {
        this.deviceConfigs = deviceConfigs;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final var listDeviceConfigVersionsResponse = (ListDeviceConfigVersionsResponse) o;
        return Objects.equals(deviceConfigs, listDeviceConfigVersionsResponse.deviceConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceConfigs);
    }

    @Override
    public String toString() {

        return "class ListDeviceConfigVersionsResponse {\n" +
                "    deviceConfigs: " + toIndentedString(deviceConfigs) + "\n" +
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
