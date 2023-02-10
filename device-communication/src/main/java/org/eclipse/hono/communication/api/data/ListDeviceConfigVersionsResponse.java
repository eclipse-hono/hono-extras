package org.eclipse.hono.communication.api.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A list of a device config versions
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListDeviceConfigVersionsResponse {

    private List<DeviceConfig> deviceConfigs = new ArrayList<>();

    public ListDeviceConfigVersionsResponse() {

    }

    public ListDeviceConfigVersionsResponse(List<DeviceConfig> deviceConfigs) {
        this.deviceConfigs = deviceConfigs;
    }


    @JsonProperty("deviceConfigs")
    public List<DeviceConfig> getDeviceConfigs() {
        return deviceConfigs;
    }

    public void setDeviceConfigs(List<DeviceConfig> deviceConfigs) {
        this.deviceConfigs = deviceConfigs;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListDeviceConfigVersionsResponse listDeviceConfigVersionsResponse = (ListDeviceConfigVersionsResponse) o;
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
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
