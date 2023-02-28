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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A list of a device states.
 **/
public class ListDeviceStatesResponse {

    private List<DeviceState> deviceStates = new ArrayList<>();

    /**
     * Creates a new ListDeviceStatesResponse.
     */
    public ListDeviceStatesResponse() {
    }

    /**
     * Creates a new ListDeviceStatesResponse.
     *
     * @param deviceStates The device states
     */
    public ListDeviceStatesResponse(final List<DeviceState> deviceStates) {
        this.deviceStates = deviceStates;
    }

    @JsonProperty("deviceStates")
    public List<DeviceState> getDeviceStates() {
        return deviceStates;
    }

    public void setDeviceStates(final List<DeviceState> deviceStates) {
        this.deviceStates = deviceStates;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final var listDeviceStatesResponse = (ListDeviceStatesResponse) o;
        return Objects.equals(deviceStates, listDeviceStatesResponse.deviceStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceStates);
    }

    @Override
    public String toString() {

        return "class ListDeviceStatesResponse {\n" +
                "    deviceStates: " + toIndentedString(deviceStates) + "\n" +
                "}";
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(final Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
