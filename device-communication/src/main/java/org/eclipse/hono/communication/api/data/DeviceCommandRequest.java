package org.eclipse.hono.communication.api.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Command json object structure
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceCommandRequest {

    private String binaryData;
    private String subfolder;

    public DeviceCommandRequest() {

    }

    public DeviceCommandRequest(String binaryData, String subfolder) {
        this.binaryData = binaryData;
        this.subfolder = subfolder;
    }


    @JsonProperty("binaryData")
    public String getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(String binaryData) {
        this.binaryData = binaryData;
    }


    @JsonProperty("subfolder")
    public String getSubfolder() {
        return subfolder;
    }

    public void setSubfolder(String subfolder) {
        this.subfolder = subfolder;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeviceCommandRequest deviceCommandRequest = (DeviceCommandRequest) o;
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
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
