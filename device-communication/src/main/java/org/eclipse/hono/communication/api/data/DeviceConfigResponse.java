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
public class DeviceConfigResponse {


    private int version;
    private String cloudUpdateTime;
    private String binaryData;

  
    public DeviceConfigResponse() {
    }


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCloudUpdateTime() {
        return cloudUpdateTime;
    }

    public void setCloudUpdateTime(String cloudUpdateTime) {
        this.cloudUpdateTime = cloudUpdateTime;
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
                ", cloudUpdateTime='" + cloudUpdateTime + '\'' +
                ", binaryData='" + binaryData + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceConfigResponse that = (DeviceConfigResponse) o;
        return version == that.version && cloudUpdateTime.equals(that.cloudUpdateTime) && binaryData.equals(that.binaryData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, cloudUpdateTime, binaryData);
    }


}
