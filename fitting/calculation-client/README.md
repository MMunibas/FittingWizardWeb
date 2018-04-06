# swagger-java-client

## Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-java-client</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "io.swagger:swagger-java-client:1.0.0"
```

### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/swagger-java-client-1.0.0.jar
* target/lib/*.jar

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DefaultApi;

import java.io.File;
import java.util.*;

public class DefaultApiExample {

    public static void main(String[] args) {
        
        DefaultApi apiInstance = new DefaultApi();
        try {
            AlgorithmList result = apiInstance.getAlgorithmList();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#getAlgorithmList");
            e.printStackTrace();
        }
    }
}

```

## Documentation for API Endpoints

All URIs are relative to *https://localhost*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DefaultApi* | [**getAlgorithmList**](docs/DefaultApi.md#getAlgorithmList) | **GET** /algorithms | Returns a list of calculations
*DefaultApi* | [**getVersionInfo**](docs/DefaultApi.md#getVersionInfo) | **GET** /info | Returns the version of all calculation scripts
*CalculationApi* | [**deleteCalculationResource**](docs/CalculationApi.md#deleteCalculationResource) | **DELETE** /calculation/{calculation_id} | Deletes the specified calculation
*CalculationApi* | [**deleteInputFileDownloadResource**](docs/CalculationApi.md#deleteInputFileDownloadResource) | **DELETE** /calculation/{calculation_id}/input/{relative_path} | Delete input file
*CalculationApi* | [**deleteOutputFileDownloadResource**](docs/CalculationApi.md#deleteOutputFileDownloadResource) | **DELETE** /calculation/{calculation_id}/output/{relative_path} | Delete output file
*CalculationApi* | [**getCalculationList**](docs/CalculationApi.md#getCalculationList) | **GET** /calculation/ | Returns a list of available calculations
*CalculationApi* | [**getCalculationResource**](docs/CalculationApi.md#getCalculationResource) | **GET** /calculation/{calculation_id} | Returns the status of the specified calculation
*CalculationApi* | [**getInputFileDownloadResource**](docs/CalculationApi.md#getInputFileDownloadResource) | **GET** /calculation/{calculation_id}/input/{relative_path} | List all running jobs for a calculation
*CalculationApi* | [**getInputFileListResource**](docs/CalculationApi.md#getInputFileListResource) | **GET** /calculation/{calculation_id}/input | Upload input file
*CalculationApi* | [**getJobResource**](docs/CalculationApi.md#getJobResource) | **GET** /calculation/{calculation_id}/jobs | List all running jobs for a calculation
*CalculationApi* | [**getOutputFileDownloadResource**](docs/CalculationApi.md#getOutputFileDownloadResource) | **GET** /calculation/{calculation_id}/output/{relative_path} | Download output file
*CalculationApi* | [**getOutputFileListResource**](docs/CalculationApi.md#getOutputFileListResource) | **GET** /calculation/{calculation_id}/output | List all running jobs for a calculation
*CalculationApi* | [**postCalculationList**](docs/CalculationApi.md#postCalculationList) | **POST** /calculation/ | Creates new calculation
*CalculationApi* | [**postCalculationResource**](docs/CalculationApi.md#postCalculationResource) | **POST** /calculation/{calculation_id} | Update parameters
*CalculationApi* | [**postCancelCalculationAction**](docs/CalculationApi.md#postCancelCalculationAction) | **POST** /calculation/{calculation_id}/cancel | Abort the specified calculation
*CalculationApi* | [**postInputFileListResource**](docs/CalculationApi.md#postInputFileListResource) | **POST** /calculation/{calculation_id}/input | Upload input file
*CalculationApi* | [**postRunCalculationAction**](docs/CalculationApi.md#postRunCalculationAction) | **POST** /calculation/{calculation_id}/run | Start a run of this calculation


## Documentation for Models

 - [AlgorithmList](docs/AlgorithmList.md)
 - [Calculation](docs/Calculation.md)
 - [CalculationId](docs/CalculationId.md)
 - [CalculationStatus](docs/CalculationStatus.md)
 - [CalculationStatusList](docs/CalculationStatusList.md)
 - [FileList](docs/FileList.md)
 - [JobIdList](docs/JobIdList.md)
 - [Run](docs/Run.md)
 - [RunId](docs/RunId.md)
 - [ServiceInfo](docs/ServiceInfo.md)
 - [Status](docs/Status.md)


## Documentation for Authorization

All endpoints do not require authorization.
Authentication schemes defined for the API:

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author



