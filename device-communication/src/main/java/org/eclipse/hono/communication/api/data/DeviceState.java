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
 * The device state.
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceState {

    private String updateTime;
    private String binaryData;

    /**
     * Creates a new device state.
     */
    public DeviceState() {
    }

    /**
     * Creates a new device state.
     *
     * @param updateTime Cloud update time
     * @param binaryData Binary data
     */
    public DeviceState(final String updateTime, final String binaryData) {
        this.updateTime = updateTime;
        this.binaryData = binaryData;
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DeviceState deviceState = (DeviceState) o;
        return Objects.equals(updateTime, deviceState.updateTime) &&
                Objects.equals(binaryData, deviceState.binaryData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(updateTime, binaryData);
    }

    @Override
    public String toString() {

        return "class DeviceState {\n" +
                "    updateTime: " + toIndentedString(updateTime) + "\n" +
                "    binaryData: " + toIndentedString(binaryData) + "\n" +
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
