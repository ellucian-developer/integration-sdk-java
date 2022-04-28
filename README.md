# Ellucian Ethos Integration SDK

Ethos Integration SDK provides utilities and libraries that make it easier for developers to quickly start building Ethos-based integrations.

The Ethos Integration SDK for Java allows you to easily develop Java applications that integrate with Ellucian Ethos Integration. The SDK
builds and executes HTTP requests and manages the responses. This allows your application to use the Java library methods to communicate
with Ethos Integration, without the need to call the REST APIs directly.

The Ethos Integration SDK for Java simplifies use of Ethos Integration by providing a set of libraries that Java developers are familiar with. 
The Ethos Integration SDK makes the application development process less expensive and more efficient.

## Table of contents

1. [Setup](#setup)
1. [Quick Start](#quick-start)
1. [Full API Doc](#full-api-documentation)
1. [Examples](#examples)


# Setup
This SDK is available for download from the [Maven Central Repository](https://central.sonatype.org/). 

The following is the Maven dependency for this SDK:
```
<dependency>
    <groupId>com.ellucian.ethos.integration.sdk</groupId>
    <artifactId>integration-sdk-java</artifactId>
    <version>0.3.0</version> <!-- Check the version and use the latest -->
</dependency>
```

Before using the SDK, you will need to download and install the following required software:
 
* a Java Development Kit (minimum version 11)
* Maven

You will need an API key from an Ethos Integration application.  It is expected that the application that the API key belongs 
to is already configured properly in Ethos Integration.  Please refer to Ellucian documentation for more information about how to get 
an API key and configure Ethos Integration applications. 

We also recommend:
* A Java IDE such as
    * [IntelliJ](https://www.jetbrains.com/idea/)
    * [Visual Studio Code for Java](https://code.visualstudio.com/docs/languages/java)
    * [Eclipse](https://www.eclipse.org/)

In general your choice of Java IDE will work fine as long as you can manage Maven dependencies with it.

For development of this SDK, we used [Amazon Coretto JDK 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/what-is-corretto-11.html).
See [here](https://aws.amazon.com/corretto/faqs/) for reasons why this was chosen.

If your development environment is on a unix-based platform, [SDKMan](https://sdkman.io/) can be a helpful tool to facilitate the required
downloads needed for the Ethos SDK prerequisites, such as Java and Maven, although SDKMan is not required.

# Quick Start
To make API requests against the Ethos Integration services, you will first need to create a client object.  To create a client 
object, use the **EthosClientBuilder**, found in the `com.ellucian.ethos.integration.client` package.
The types of clients that you can create are as follows:
* `EthosConfigurationClient` - make requests for getting config data, such as /appConfig and /available-resources
* `EthosErrorsClient` - perform create, read, and delete operations against the EI errors service
* `EthosMessagesClient` - get messages from a subscriber queue
* `EthosProxyClient` - perform CRUD operations for Ethos Data Models and other resources using the EI proxy API

To use the EthosClientBuilder, you will need to create a new instance of it, passing a valid Ethos Integration API key to the 
constructor.  The API key is used to get an access token from Ethos Integration, allowing the client to authenticate to the EI 
services when making requests.

Here is a quick example of creating a proxy client and making a simple GET request for the 'courses' resource.  See the 
[examples](#examples) section for more detailed code examples.
```java
import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.proxy.EthosProxyClient;

// Create Proxy Client
String apiKey = "11111111-1111-1111-1111-111111111111";
EthosProxyClient ethosProxyClient = new EthosClientBuilder(apiKey).buildEthosProxyClient();
EthosResponse response = ethosProxyClient.get("courses");
```

The `EthosResponse` object is returned from many of the methods in the proxy client.  It holds information about the HTTP response, 
such as the content (response body), response headers, and status code.  It also holds a `requestedUrl` property that can show you 
the URL that was built by the client and used to make the request.

There are overloaded methods in the `EthosProxyClient` that let you get the response as a String or as a `com.fasterxml.jackson.databind.JsonNode` object. 
These response types contain only the content portion of the `EthosResponse`.
```java
// get as String
String response = ethosProxyClient.getAsString​("courses");

// get as JsonNode
JsonNode response = ethosProxyClient.getAsJsonNode("courses");
```
The [Jackson Library](https://github.com/FasterXML/jackson-docs) is heavily used in this SDK to manage serializing and parsing 
JSON objects.  The `JsonNode` objects make it easy to read the JSON properties of the responses.  It provides a way to generically 
manage all the Ethos Data Models and other resource types that could be returned from Ethos Integration without having to create and 
manage POJO's for every possible one.

Here is an example of getting properties from a 'courseJson' response object that is a `JsonNode` representing the 'courses' Ethos Data Model.
```java
// get the course description
String desc = courseJson.get("description").asText();

// get the course number
int num = courseJson.get("number").asInt();

// get the titles array and iterate over it
JsonNode titles = courseJson.get("titles");
Iterator<JsonNode> it = titles.iterator();
while (it.hasNext()) {
    JsonNode title = it.next();
    String titleValue = title.get("value").asText();
    // do other processing
}
```
The proxy client is different than the other clients because the response objects could be in any JSON format that the authoritative 
applications return.  This is why it is necessary to handle the responses as Strings or generic JSON nodes.

The other clients, such as `EthosMessagesClient` and `EthosErrorsClient`, return more specialized objects since the responses 
have defined JSON schemas.  They return `EthosError` and `ChangeNotification` objects.  See the full API doc for more details.


# Full API Documentation
The full SDK API JavaDoc is hosted on our Github Pages site, located at (https://ellucianethos.github.io/integrationSDKDoc/java).

# Examples
The following are code-snippet examples of how to use the Ethos Integration SDK for Java.  For more in-depth examples 
please refer to the [integration SDK Java example project](https://github.com/ellucian-developer/devexp-eijsdk-examples) in Github.

### Making Requests to the Proxy API

Get a page of 'courses' resources using a specific version.
```java
EthosProxyClient ethosProxyClient = new EthosClientBuilder(apiKey).buildEthosProxyClient();
EthosResponse response = ethosProxyClient.get("courses", "application/vnd.hedtech.integration.v16.1.0+json");
```

Get a page of 'persons' resources for a major version.
```java
// get the full version header for v12 of 'persons'
EthosConfigurationClient configClient = new EthosClientBuilder(apiKey).buildEthosConfigurationClient();
String version = configClient.getVersionHeader​("persons", 12);

EthosResponse response = ethosProxyClient.get("persons", version);
```

Get a single 'employees' row using an ID.
```java
EthosResponse response = ethosProxyClient.getById("employees", "11111111-1111-1111-1111-111111111111");
```

Create a new record with a POST request.
```java
EthosResponse response = ethosProxyClient.post("colors", "{ \"id\":\"00000000-0000-0000-0000-000000000000\", \"name\": \"green\" }");
```

Update a record with a PUT request.
```java
EthosResponse response = ethosProxyClient.put("colors", "11111111-1111-1111-1111-111111111111", "{ \"name\": \"forest green\" }");
```

Delete a record with a DELETE request.
```java
ethosProxyClient.delete("colors", "11111111-1111-1111-1111-111111111111");
```

Make requests that use paging.  This will handle sending multiple HTTP requests to get multiple pages of data with a single operation.  
**Use caution when trying to get all pages of a resource in a single request.  Depending on the resource, this could cause a long 
running operation that will cause a timeout, or it could return a huge amount of data that could cause an out of memory error.**
```java
// get all the pages of data for the 'buildings' resource using the default page size
List<EthosResponse> ethosResponseList = ethosProxyClient.getAllPages( "buildings" );

// get the max page size for the 'persons' resource
int pageSize = ethosProxyClient.getMaxPageSize( "persons" );

// get 5 pages of persons data using the maximum allowed page size
// return the data as a list of JsonNodes
List<JsonNode> jsonNodeList = ethosProxyClient.getPagesAsJsonNodes( "persons", pageSize, 5 );

// get 2 pages of courses data starting from an offset of 100
// return the data as a list of Strings
List<String> stringList = ethosProxyClient.getPagesFromOffsetAsStrings( "courses", 100, 2 );
```

### Making Asynchronous Requests to the Proxy API

It is also possible to get the pages of data with an asychronous request.  These are all detailed in JavaDoc and also the examples package.
In general, all methods for paging are available asynchronously by adding `Async` to the method name, and using `EthosProxyClientAsync`.

Data is then wrapped in a `CompleteableFuture` object, which is Java's version of a Promise (JavaScript's term) or Future (C#'s term).

```java
EthosProxyClientAsync ethosProxyClient = new EthosClientBuilder(apiKey).buildEthosProxyClientAsync();
CompletableFuture<List<String>> asyncResponse = ethosProxyClient.getAllPagesFromOffsetAsStringsAsync(resourceName, offset);
// While the CompletableFuture thread is running, additional operations can be performed.  For the sake of
// demonstrating this we are just running a few printlns to print out the current time.
System.out.println(LocalDateTime.now());
try {
    Thread.sleep(250);
} catch (InterruptedException e) {
    e.printStackTrace();
}
System.out.println(LocalDateTime.now());
// using .join here so there is no exception thrown - all exceptions will be unchecked.  This is the
// same as in C# where the default is to use the async processing without checked exceptions.
List<String> stringList = asyncResponse.join();
```
Note that, due to `CompletableFuture`'s limitations, it can only throw a `CompletionException`.  This is a runtime Exception so catching it will wrap the
actual exception.

### Making Criteria Filter Requests to the Proxy API

Requests using criteria filters can be made using the EthosFilterQueryClient.  SimpleCriteria can be built using the SimpleCriteria.Builder, 
from which a CriteriaFilter is built and used by the EthosFilterQueryClient.  This is covered in the Javadoc and example code.
Some knowledge of the desired criteria filter syntax is needed for use with the given Ethos resource.  The following is a brief 
example code snippet.

```java
String resource = "persons";
String yourAPIKey = "11111111-1111-1111-1111-111111111111";  // This is a dummy value, but would be your API key value. 
EthosFilterQueryClient ethosFilterQueryClient = new EthosClientBuilder(apiKey)
                                                .buildEthosFilterQueryClient();
CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                .withSimpleCriteriaArray("names", "firstName", "John")
                                .buildCriteriaFilter();
EthosFilterQueryClient ethosFilterQueryClient = getEthosFilterQueryClient();
try {
    // The criteriaFilter generates the following syntax when making the request using the ethosFilterQueryClient below: 
    // ?criteria={"names":[{"firstName":"John"}]}
    EthosResponse ethosResponse = ethosFilterQueryClient.getWithCriteriaFilter( resource, criteriaFilter );
    // Handle the response as desired...
}
catch( IOException ioe ) {
    ioe.printStackTrace();
}
```

Examples of using a named query filter and/or filter map are also available in the example code.
 
### Consuming Subscriber Messages

Call the consume endpoint to get the default number of new messages from your application's subscriber queue.  The messages are 
returned as ChangeNotification objects.
```java
EthosMessagesClient messagesClient = new EthosClientBuilder(apiKey).buildMessagesClient();
List<ChangeNotification> cnList = messagesClient.consume();
```

Call the consume endpoint to get available messages from your application's subscriber queue, starting after message ID of '10'.
```java
List<ChangeNotification> cnList = messagesClient.consumeFromId(10);
```
The '10' parameter sent to this method is used to send the `lastProcessedID` query parameter in the HTTP request to the /consume 
endpoint.  This parameter can be used to indicate the ID of the last message that was successfully processed. It can be used 
to retrieve messages that have already been retrieved. The messages in the queue have sequential ID's, and the lastProcessedID parameter 
corresponds to the ID of a message in the queue.
Here is an example of how lastProcessedID can be used. If the application consuming the messages retrieves messages 1-10, 
but only successfully processes messages 1-5, it can set the lastProcessedID parameter to 5 in the next invocation. That will give 
the application messages 6-10 again.

Check to see how many messages are available in your application's subscriber queue.
```java
int numMessages = messagesClient.getNumAvailableMessages();
```

### Automated Polling for Subscriber Messages

Setup automated polling for subscribing to ChangeNotification messages.  The SDK can automatically provide ChangeNotification 
messages to a client application when the client application configures a notification poll service to subscribe to a client implementation
of the appropriate abstract subscriber.  As an example, the following shows a client application implementation of an abstract
subscriber, which then receives notifications from the SDK in an automated polling fashion:

```java
    // Client implementation of a change notification subscriber...
    public class MyEthosChangeNotificationSubscriber extends AbstractEthosChangeNotificationSubscriber {
        public MyEthosChangeNotificationSubscriber() {
            super();
        }
        public MyEthosChangeNotificationSubscriber( Integer numNotifications ) {
            this();
            this.numNotifications = numNotifications;
        }
        @Override
        public void onChangeNotification(ChangeNotification changeNotification) {
            // Code to handle the changeNotification here...
        }
        @Override
        public void onChangeNotificationError(Throwable throwable) {
            // Code to handle the error here...
        }
    }
```
Example of using the client application subscriber implementation above, to have it receive notifications.
This code would reside in a client application that uses the SDK.
```java
    // Specify the number of notifications to retrieve per poll attempt.  
    int numNotifications = 10;
    
    // The number of seconds to wait between poll attempts when retrieving notification messages.
    long pollingIntervalSeconds = 5;
    
    // Setup the change notification service with an apiKey, or could also use an EthosClientBuilder instead.    
    EthosChangeNotificationService cnService = new EthosChangeNotificationService.Builder(apiKey)
                                               .build();
    
    // Instantiate the publisher with the change notification service and the polling interval.
    EthosChangeNotificationPollService ethosChangeNotificationPollService = new EthosChangeNotificationPollService( cnService, pollingIntervalSeconds );
    
    // Instantiate the subscriber with the number of notifications to use per poll request.
    MyChangeNotificationSubscriber myChangeNotificationSubscriber = new MyChangeNotificationSubscriber( numNotifications );
    
    // Subscribe the client application subscriber implementation to the change notification publisher.  This will begin
    // the subscription process and the subscriber will start receiving messages.
    ethosChangeNotificationPollService.subscribe( myChangeNotificationSubscriber );

    // When ready to stop receiving notifications, just cancel the subscription.  No further notifications will be 
    // received after the current polling operation completes.
    if( myChangeNotificationSubscriber.isSubscriptionRunning() ) {
        myChangeNotificationSubscriber.cancelSubscription();
    }
```

### Getting Configuration Info

Get the configuration information for the application to which the API key belongs.
```java
EthosConfigurationClient configClient = new EthosClientBuilder(apiKey).buildEthosConfigurationClient();

// get app configuration as a String
String appConfig = configClient.getAppConfig();

// get app configuration as a JsonNode
JsonNode appConfig = configClient.getAppConfigJson();
```

Get the list of available resources from the authoritative applications in your tenant.  This calls the /available-resources 
endpoint of Ethos Integration, and returns that data in a String or JsonNode format.
```java
// get as String
String availableResources = configClient.getAllAvailableResources();

// get as JsonNode
JsonNode availableResources = configClient.getAllAvailableResourcesAsJson();
```
There is also an option to get the available resources data that only pertains to your application.  If your application has 
credentials configured to call one or more authoritative application API's, then it will have an `ownerOverrides` array defined in the 
app config.  This ownerOverrides array determines which authoritative app will serve requests for different resources.  When you get 
the available resource data specific to your application, the response will be limited to resources and authoritative apps in your application's 
ownerOverrides config.
```java
// get as String
String availableResources = configClient.getAvailableResourcesForApp();

// get as JsonNode
JsonNode availableResources = configClient.getAvailableResourcesForAppAsJson();
```

### Managing Ethos Errors

Create an error in the Ethos Integration errors service.
```java
EthosErrorsClient errorsClient = new EthosClientBuilder(apiKey).buildEthosErrorsClient();

// create an EthosError object from a JSON string
String json = "{" +
              "    \"id\": \"00000000-0000-0000-0000-000000000000\"," +
              "    \"dateTime\": \"2020-10-27T03:10:44.827Z\"," +
              "    \"severity\": \"error\"," +
              "    \"responseCode\": 500," +
              "    \"description\": \"Internal Server Error\"," +
              "    \"details\": \"This is a more info on the info error\"," +
              "    \"applicationId\": \"00000000-0000-0000-0000-000000000000\"," +
              "    \"applicationName\": \"Banner\"," +
              "    \"correlationId\": \"2468UserMade3242134\"," +
              "    \"resource\": {" +
              "        \"id\": \"00000000-0000-0000-0000-000000000000\"," +
              "        \"name\": \"persons\"" +
              "     }," +
              "    \"applicationSubtype\": \"EMA\"" +
              "}";
EthosError error = ErrorFactory.createErrorFromJson(json);

// post to errors service
EthosResponse response = errorsClient.post(error);
```

Get a single page of errors for your tenant from the errors service.
```java
EthosResponse response = errorsClient.get();
```

Get an initial page of errors for your tenant from the errors service as a list of EthosError objects.
```java
List<EthosError> ethosErrorList = errorsClient.getAsEthosErrors();
```

### Getting Data in a Banner MEP Tenant Environment  
  
An overview of Banner MEP and instructions on how to configure an Ethos tenant for Banner MEP is outside of the scope of this documentation.  This will outline how to use API keys   
from the different applications to pull data for the different VPDI codes.  
  
When an Ethos tenant environment is configured for a Banner MEP institution, it will have multiple Banner applications setup that point to the same   
Banner implementation, but with different URI's to pull data for different VPDI codes.  There will also be separate client or subscribing applications   
setup to make proxy requests and receive change-notifications from the different Banner apps.  
  
In this code example, let's assume that I am working with a tenant environment that has Banner applications setup for 3 VPDI codes representing 3 different campuses of an institution:  
- NORTH  
- SOUTH  
- MAIN  
  
Likewise, there will be 3 client applications that are used to get data from the 3 different Banner campuses.  
```java  
// API keys for my 3 client apps  
String northKey = "11111111-1111-1111-1111-111111111111";  
String southKey = "22222222-2222-2222-2222-222222222222";  
String mainKey  = "33333333-3333-3333-3333-333333333333";  
  
// get 'students' data through the proxy api for NORTH campus  
EthosProxyClient northProxyClient = new EthosClientBuilder(northKey).buildEthosProxyClient();  
EthosResponse response = northProxyClient.get("students");  
  
// get change-notifications from the messages service for SOUTH campus  
EthosMessagesClient southMessagesClient = new EthosClientBuilder(southKey).buildMessagesClient();  
List<ChangeNotification> cnList = southMessagesClient.consume();  
  
// get data through the proxy and get change-notifications for the MAIN campus  
EthosClientBuilder mainClientBuilder = new EthosClientBuilder(mainKey);  
// proxy  
EthosProxyClient mainProxyClient = mainClientBuilder.buildEthosProxyClient();  
EthosResponse response = mainProxyClient.get("students");  
// messages  
EthosMessagesClient mainMessagesClient = mainClientBuilder.buildMessagesClient();  
List<ChangeNotification> cnList = mainMessagesClient.consume();  
```
