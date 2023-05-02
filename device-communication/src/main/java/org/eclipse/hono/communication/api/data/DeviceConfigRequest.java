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
 * Request body for modifying device configs.
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceConfigRequest {

    private String versionToUpdate;
    private String binaryData;

    /**
     * Creates a new DeviceConfigRequest.
     */
    public DeviceConfigRequest() {

    }

    /**
     * Creates a new DeviceConfigRequest.
     *
     * @param versionToUpdate Version to update
     * @param binaryData      The binary data
     */
    public DeviceConfigRequest(final String versionToUpdate, final String binaryData) {
        this.versionToUpdate = versionToUpdate;
        this.binaryData = binaryData;
    }


    @JsonProperty("versionToUpdate")
    public String getVersionToUpdate() {
        return versionToUpdate;
    }

    public void setVersionToUpdate(final String versionToUpdate) {
        this.versionToUpdate = versionToUpdate;
    }


    @JsonProperty("binaryData")
    public String getBinaryData() {
        return binaryData;
    }

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
        final var deviceConfigRequest = (DeviceConfigRequest) o;
        return Objects.equals(versionToUpdate, deviceConfigRequest.versionToUpdate) &&
                Objects.equals(binaryData, deviceConfigRequest.binaryData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionToUpdate, binaryData);
    }

    @Override
    public String toString() {

        return "class DeviceConfigRequest {\n" +
                "    versionToUpdate: " + toIndentedString(versionToUpdate) + "\n" +
                "    binaryData: " + toIndentedString(binaryData) + "\n" +
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
