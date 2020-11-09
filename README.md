# HttpURLConnectionFactory
Functional Classes for using the java.net.HttpURLConnection class provided by oracle to make REST calls.

## How to use:
### GET
```
HttpURLConnection conn = GetCallFactory.callWithParams(<POST call url>);
GetCallFactory.addAuthentication(conn, <username>, <password>);
//...
String output = GetCallFactory.readResponse(conn);
  // or
String output = GetCallFactory.getFullResponse(conn);
conn.disconnect();
```
  
### POST
```
HttpURLConnection conn = PostCallFactory.call(<POST call url>);
PostCallFactory.setBody(conn, <Map with Params>);
...
//...
String output = PostCallFactory.readResponse(conn);
conn.disconnect();
```
  
### PUT and DELETE follow the above formats

HttpURLConnection documentation is available [here](https://docs.oracle.com/javase/8/docs/api/java/net/HttpURLConnection.html)

## Javadoc
You can generate a javadoc for this library by following these steps:
 - [Intellij](https://www.jetbrains.com/help/idea/working-with-code-documentation.html)
 - [Eclipse](https://www.codejava.net/ides/eclipse/how-to-generate-javadoc-in-eclipse)
