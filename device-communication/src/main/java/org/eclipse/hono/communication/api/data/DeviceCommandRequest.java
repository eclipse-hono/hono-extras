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
 * Command json object structure.
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceCommandRequest {

    private String binaryData;
    private String subfolder;

    /**
     * Creates a new DeviceCommandRequest.
     */
    public DeviceCommandRequest() {

    }

    /**
     * Creates a new DeviceCommandRequest.
     *
     * @param binaryData Binary data
     * @param subfolder  THe subfolder
     */
    public DeviceCommandRequest(final String binaryData, final String subfolder) {
        this.binaryData = binaryData;
        this.subfolder = subfolder;
    }


    @JsonProperty("binaryData")
    public String getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(final String binaryData) {
        this.binaryData = binaryData;
    }


    @JsonProperty("subfolder")
    public String getSubfolder() {
        return subfolder;
    }

    public void setSubfolder(final String subfolder) {
        this.subfolder = subfolder;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DeviceCommandRequest deviceCommandRequest = (DeviceCommandRequest) o;
        return Objects.equals(binaryData, deviceCommandRequest.binaryData) &&
                Objects.equals(subfolder, deviceCommandRequest.subfolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(binaryData, subfolder);
    }

    @Override
    public String toString() {

        return "class DeviceCommandRequest {\n" +
                "    binaryData: " + toIndentedString(binaryData) + "\n" +
                "    subfolder: " + toIndentedString(subfolder) + "\n" +
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
