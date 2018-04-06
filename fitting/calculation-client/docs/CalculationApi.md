# CalculationApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**deleteCalculationResource**](CalculationApi.md#deleteCalculationResource) | **DELETE** /calculation/{calculation_id} | Deletes the specified calculation
[**deleteInputFileDownloadResource**](CalculationApi.md#deleteInputFileDownloadResource) | **DELETE** /calculation/{calculation_id}/input/{relative_path} | Delete input file
[**deleteOutputFileDownloadResource**](CalculationApi.md#deleteOutputFileDownloadResource) | **DELETE** /calculation/{calculation_id}/output/{relative_path} | Delete output file
[**getCalculationList**](CalculationApi.md#getCalculationList) | **GET** /calculation/ | Returns a list of available calculations
[**getCalculationResource**](CalculationApi.md#getCalculationResource) | **GET** /calculation/{calculation_id} | Returns the status of the specified calculation
[**getInputFileDownloadResource**](CalculationApi.md#getInputFileDownloadResource) | **GET** /calculation/{calculation_id}/input/{relative_path} | List all running jobs for a calculation
[**getInputFileListResource**](CalculationApi.md#getInputFileListResource) | **GET** /calculation/{calculation_id}/input | Upload input file
[**getJobResource**](CalculationApi.md#getJobResource) | **GET** /calculation/{calculation_id}/jobs | List all running jobs for a calculation
[**getOutputFileDownloadResource**](CalculationApi.md#getOutputFileDownloadResource) | **GET** /calculation/{calculation_id}/output/{relative_path} | Download output file
[**getOutputFileListResource**](CalculationApi.md#getOutputFileListResource) | **GET** /calculation/{calculation_id}/output | List all running jobs for a calculation
[**postCalculationList**](CalculationApi.md#postCalculationList) | **POST** /calculation/ | Creates new calculation
[**postCalculationResource**](CalculationApi.md#postCalculationResource) | **POST** /calculation/{calculation_id} | Update parameters
[**postCancelCalculationAction**](CalculationApi.md#postCancelCalculationAction) | **POST** /calculation/{calculation_id}/cancel | Abort the specified calculation
[**postInputFileListResource**](CalculationApi.md#postInputFileListResource) | **POST** /calculation/{calculation_id}/input | Upload input file
[**postRunCalculationAction**](CalculationApi.md#postRunCalculationAction) | **POST** /calculation/{calculation_id}/run | Start a run of this calculation


<a name="deleteCalculationResource"></a>
# **deleteCalculationResource**
> deleteCalculationResource(calculationId)

Deletes the specified calculation

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
try {
    apiInstance.deleteCalculationResource(calculationId);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#deleteCalculationResource");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="deleteInputFileDownloadResource"></a>
# **deleteInputFileDownloadResource**
> deleteInputFileDownloadResource(calculationId, relativePath)

Delete input file

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
String relativePath = "relativePath_example"; // String | 
try {
    apiInstance.deleteInputFileDownloadResource(calculationId, relativePath);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#deleteInputFileDownloadResource");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |
 **relativePath** | **String**|  |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="deleteOutputFileDownloadResource"></a>
# **deleteOutputFileDownloadResource**
> deleteOutputFileDownloadResource(calculationId, relativePath)

Delete output file

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
String relativePath = "relativePath_example"; // String | 
try {
    apiInstance.deleteOutputFileDownloadResource(calculationId, relativePath);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#deleteOutputFileDownloadResource");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |
 **relativePath** | **String**|  |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getCalculationList"></a>
# **getCalculationList**
> CalculationStatusList getCalculationList()

Returns a list of available calculations

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
try {
    CalculationStatusList result = apiInstance.getCalculationList();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#getCalculationList");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**CalculationStatusList**](CalculationStatusList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getCalculationResource"></a>
# **getCalculationResource**
> Status getCalculationResource(calculationId)

Returns the status of the specified calculation

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
try {
    Status result = apiInstance.getCalculationResource(calculationId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#getCalculationResource");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |

### Return type

[**Status**](Status.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getInputFileDownloadResource"></a>
# **getInputFileDownloadResource**
> getInputFileDownloadResource(calculationId, relativePath)

List all running jobs for a calculation

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
String relativePath = "relativePath_example"; // String | 
try {
    apiInstance.getInputFileDownloadResource(calculationId, relativePath);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#getInputFileDownloadResource");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |
 **relativePath** | **String**|  |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getInputFileListResource"></a>
# **getInputFileListResource**
> FileList getInputFileListResource(calculationId)

Upload input file

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
try {
    FileList result = apiInstance.getInputFileListResource(calculationId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#getInputFileListResource");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |

### Return type

[**FileList**](FileList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getJobResource"></a>
# **getJobResource**
> JobIdList getJobResource(calculationId)

List all running jobs for a calculation

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
try {
    JobIdList result = apiInstance.getJobResource(calculationId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#getJobResource");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |

### Return type

[**JobIdList**](JobIdList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getOutputFileDownloadResource"></a>
# **getOutputFileDownloadResource**
> getOutputFileDownloadResource(calculationId, relativePath)

Download output file

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
String relativePath = "relativePath_example"; // String | 
try {
    apiInstance.getOutputFileDownloadResource(calculationId, relativePath);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#getOutputFileDownloadResource");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |
 **relativePath** | **String**|  |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getOutputFileListResource"></a>
# **getOutputFileListResource**
> FileList getOutputFileListResource(calculationId)

List all running jobs for a calculation

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
try {
    FileList result = apiInstance.getOutputFileListResource(calculationId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#getOutputFileListResource");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |

### Return type

[**FileList**](FileList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="postCalculationList"></a>
# **postCalculationList**
> CalculationId postCalculationList(payload)

Creates new calculation

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
Calculation payload = new Calculation(); // Calculation | 
try {
    CalculationId result = apiInstance.postCalculationList(payload);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#postCalculationList");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **payload** | [**Calculation**](Calculation.md)|  |

### Return type

[**CalculationId**](CalculationId.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="postCalculationResource"></a>
# **postCalculationResource**
> CalculationStatus postCalculationResource(calculationId)

Update parameters

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
try {
    CalculationStatus result = apiInstance.postCalculationResource(calculationId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#postCalculationResource");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |

### Return type

[**CalculationStatus**](CalculationStatus.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="postCancelCalculationAction"></a>
# **postCancelCalculationAction**
> postCancelCalculationAction(calculationId)

Abort the specified calculation

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
try {
    apiInstance.postCancelCalculationAction(calculationId);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#postCancelCalculationAction");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="postInputFileListResource"></a>
# **postInputFileListResource**
> postInputFileListResource(calculationId, file)

Upload input file

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
File file = new File("/path/to/file.txt"); // File | 
try {
    apiInstance.postInputFileListResource(calculationId, file);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#postInputFileListResource");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |
 **file** | **File**|  |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

<a name="postRunCalculationAction"></a>
# **postRunCalculationAction**
> RunId postRunCalculationAction(calculationId, payload)

Start a run of this calculation

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String calculationId = "calculationId_example"; // String | 
Run payload = new Run(); // Run | 
try {
    RunId result = apiInstance.postRunCalculationAction(calculationId, payload);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#postRunCalculationAction");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **calculationId** | **String**|  |
 **payload** | [**Run**](Run.md)|  |

### Return type

[**RunId**](RunId.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

