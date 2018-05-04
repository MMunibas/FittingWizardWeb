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

import java.io.Serializable;
import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.Calculation;
import io.swagger.client.model.Run;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Status
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-04-13T10:04:30.475Z")
public class Status implements Serializable {
  @SerializedName("last_run")
  private String lastRun = null;

  @SerializedName("status")
  private String status = null;

  @SerializedName("message")
  private String message = null;

  @SerializedName("calculation_parameters")
  private Calculation calculationParameters = null;

  @SerializedName("run_parameters")
  private Run runParameters = null;

  @SerializedName("input_files")
  private List<String> inputFiles = new ArrayList<String>();

  public Status lastRun(String lastRun) {
    this.lastRun = lastRun;
    return this;
  }

   /**
   * Calculation status
   * @return lastRun
  **/
  @ApiModelProperty(example = "2018-04-06_07-40-38-579644_-4bl6", required = true, value = "Calculation status")
  public String getLastRun() {
    return lastRun;
  }

  public void setLastRun(String lastRun) {
    this.lastRun = lastRun;
  }

  public Status status(String status) {
    this.status = status;
    return this;
  }

   /**
   * Calculation status
   * @return status
  **/
  @ApiModelProperty(example = "Running", required = true, value = "Calculation status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Status message(String message) {
    this.message = message;
    return this;
  }

   /**
   * Status message
   * @return message
  **/
  @ApiModelProperty(example = "Step 1 / 10", required = true, value = "Status message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Status calculationParameters(Calculation calculationParameters) {
    this.calculationParameters = calculationParameters;
    return this;
  }

   /**
   * Parameters for the fitting algorithm
   * @return calculationParameters
  **/
  @ApiModelProperty(required = true, value = "Parameters for the fitting algorithm")
  public Calculation getCalculationParameters() {
    return calculationParameters;
  }

  public void setCalculationParameters(Calculation calculationParameters) {
    this.calculationParameters = calculationParameters;
  }

  public Status runParameters(Run runParameters) {
    this.runParameters = runParameters;
    return this;
  }

   /**
   * Parameters for the fitting algorithm
   * @return runParameters
  **/
  @ApiModelProperty(required = true, value = "Parameters for the fitting algorithm")
  public Run getRunParameters() {
    return runParameters;
  }

  public void setRunParameters(Run runParameters) {
    this.runParameters = runParameters;
  }

  public Status inputFiles(List<String> inputFiles) {
    this.inputFiles = inputFiles;
    return this;
  }

  public Status addInputFilesItem(String inputFilesItem) {
    this.inputFiles.add(inputFilesItem);
    return this;
  }

   /**
   * Uploaded input files
   * @return inputFiles
  **/
  @ApiModelProperty(example = "[\"somefile.json\"]", required = true, value = "Uploaded input files")
  public List<String> getInputFiles() {
    return inputFiles;
  }

  public void setInputFiles(List<String> inputFiles) {
    this.inputFiles = inputFiles;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Status status = (Status) o;
    return Objects.equals(this.lastRun, status.lastRun) &&
        Objects.equals(this.status, status.status) &&
        Objects.equals(this.message, status.message) &&
        Objects.equals(this.calculationParameters, status.calculationParameters) &&
        Objects.equals(this.runParameters, status.runParameters) &&
        Objects.equals(this.inputFiles, status.inputFiles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lastRun, status, message, calculationParameters, runParameters, inputFiles);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Status {\n");
    
    sb.append("    lastRun: ").append(toIndentedString(lastRun)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    calculationParameters: ").append(toIndentedString(calculationParameters)).append("\n");
    sb.append("    runParameters: ").append(toIndentedString(runParameters)).append("\n");
    sb.append("    inputFiles: ").append(toIndentedString(inputFiles)).append("\n");
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
