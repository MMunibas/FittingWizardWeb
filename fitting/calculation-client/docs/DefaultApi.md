# DefaultApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getAlgorithmList**](DefaultApi.md#getAlgorithmList) | **GET** /algorithms | Returns a list of calculations
[**getVersionInfo**](DefaultApi.md#getVersionInfo) | **GET** /info | Returns the version of all calculation scripts


<a name="getAlgorithmList"></a>
# **getAlgorithmList**
> AlgorithmList getAlgorithmList()

Returns a list of calculations

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
try {
    AlgorithmList result = apiInstance.getAlgorithmList();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#getAlgorithmList");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**AlgorithmList**](AlgorithmList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getVersionInfo"></a>
# **getVersionInfo**
> ServiceInfo getVersionInfo()

Returns the version of all calculation scripts

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
try {
    ServiceInfo result = apiInstance.getVersionInfo();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#getVersionInfo");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**ServiceInfo**](ServiceInfo.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

