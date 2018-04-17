/*
 * Fitting service API
 * Provides operations for fitting algorithms
 *
 * OpenAPI spec version: 0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;

/**
 * CalculationId
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-04-13T10:04:30.475Z")
public class CalculationId {
  @SerializedName("calculation")
  private String calculation = null;

  public CalculationId calculation(String calculation) {
    this.calculation = calculation;
    return this;
  }

   /**
   * Id of new calculation
   * @return calculation
  **/
  @ApiModelProperty(example = "2018-04-05_10-03-41-054461_OEW1L", required = true, value = "Id of new calculation")
  public String getCalculation() {
    return calculation;
  }

  public void setCalculation(String calculation) {
    this.calculation = calculation;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CalculationId calculationId = (CalculationId) o;
    return Objects.equals(this.calculation, calculationId.calculation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(calculation);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CalculationId {\n");
    
    sb.append("    calculation: ").append(toIndentedString(calculation)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

