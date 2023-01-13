package org.eclipse.hono.communication.api.entity;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for modifying device configs
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceConfigRequest   {
  
  private String versionToUpdate;
  private String binaryData;

  public DeviceConfigRequest () {

  }

  public DeviceConfigRequest (String versionToUpdate, String binaryData) {
    this.versionToUpdate = versionToUpdate;
    this.binaryData = binaryData;
  }

    
  @JsonProperty("versionToUpdate")
  public String getVersionToUpdate() {
    return versionToUpdate;
  }
  public void setVersionToUpdate(String versionToUpdate) {
    this.versionToUpdate = versionToUpdate;
  }

    
  @JsonProperty("binaryData")
  public String getBinaryData() {
    return binaryData;
  }
  public void setBinaryData(String binaryData) {
    this.binaryData = binaryData;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeviceConfigRequest deviceConfigRequest = (DeviceConfigRequest) o;
    return Objects.equals(versionToUpdate, deviceConfigRequest.versionToUpdate) &&
        Objects.equals(binaryData, deviceConfigRequest.binaryData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(versionToUpdate, binaryData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeviceConfigRequest {\n");
    
    sb.append("    versionToUpdate: ").append(toIndentedString(versionToUpdate)).append("\n");
    sb.append("    binaryData: ").append(toIndentedString(binaryData)).append("\n");
    sb.append("}");
    return sb.toString();
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
