package com.adp.common.httpurlconnectionfactory;

import com.adp.exceptions.ContentTypeNotSupportedException;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

/**
 * Used to create calls. Do not call this class directly.
 */
public abstract class CallFactory {

    /**
     * Enum containing the type of timeout to be used by an HttpURLConnection.
     * Use with CallFactory.addTimeout
     */
    public enum TimeoutType {
        CONNECT_TIMEOUT(0),
        READ_TIMEOUT(1);

        public final int INT_DESIGNATION;

        TimeoutType(int INT_DESIGNATION) {
            this.INT_DESIGNATION = INT_DESIGNATION;
        }

        public int getValue() {
            return INT_DESIGNATION;
        }
    }

    // functional methods to be implemented
    public static HttpURLConnection call(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        return con;
    }

    public static HttpURLConnection addParams(String urlStr, Map<String, String> params) throws IOException {
        urlStr += "?";
        // adds all key value pairs from map to the url as params
        for (String paramKey : params.keySet()) {
            urlStr += paramKey + "=" + params.get(paramKey) + "&";
        }
        // trims last '&' off of urlStr
        urlStr = urlStr.substring(0, urlStr.length() - 1);
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        return con;
    }

    /**
     * Sets the body of the request.
     * Currently supported body ContentTypes:
     *  - application/text
     *  - application/json
     *  - application/x-www-form-urlencoded
     * @param con the HttpURLConnection object
     * @param body the body to be added to this call
     * @param <T>
     * @return
     * @throws ContentTypeNotSupportedException
     * @throws IOException
     */
    public static <T> HttpURLConnection setBody (HttpURLConnection con, T body) throws ContentTypeNotSupportedException, IOException {
        con.setDoInput(true);
        con.setDoOutput(true);

        byte[] data;
        OutputStream os;
        switch (con.getContentType()) {
            case "application/x-www-form-urlencoded":
                // file bytes body
                if (body instanceof Serializable) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(body);
                    oos.flush();
                    oos.close();
                    // TODO more efficient way to do this?

                    data = bos.toByteArray(); // TODO method call?
                    os = con.getOutputStream();
                    os.write( data );
                    os.close();
                    con.setRequestProperty("Content-Length", "" + Integer.toString(data.length));
                } else {
                    throw new NotSerializableException();
                }
                break;
            case "application/json":
                // JSON body
                JSONObject json;
                if (body instanceof String) {
                    json = new JSONObject(body);
                } else if (body instanceof JSONObject) {
                    json = (JSONObject) body;
                } else if (body instanceof Map) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonString = objectMapper.writeValueAsString(body);
                    json = new JSONObject(jsonString);
                } else {
                    throw new JSONException("The Request Body you provided is not in a valid JSON format");
                }
                data = json.toString().getBytes();
                os = con.getOutputStream();
                os.write( data );
                os.close();
                break;
            case "application/text":
                // String body
                if (body instanceof String) {
                    data = ((String) body).getBytes();
                    os = con.getOutputStream();
                    os.write(data);
                    os.close();
                }
                break;
            default:
                throw new ContentTypeNotSupportedException();
        }
        return con;
    }

    /**
     * Adds Content-Type headers to an HttpURLConnection object
     * @param con the HttpUrlConnection to add headers to
     * @param contentType the type of content the get call is expected to return
     * @return the HttpURLConnection with headers
     */
    public static HttpURLConnection addContentType(HttpURLConnection con, String contentType) {
        con.setRequestProperty("Content-Type", contentType);
        return con;
    }

    /**
     * Adds username and password
     * @param con
     * @param uname
     * @param pwd
     * @return
     */
    public static HttpURLConnection addAuthentication(HttpURLConnection con, String uname, String pwd) {
        String authString = uname + ":" + pwd;
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
        String authHeader = "Basic " + encodedAuth;
        con.setRequestProperty ("Authorization", authHeader);
        return con;
    }

    /**
     * Reads the header field of an HttpURLConnection object
     * @param con the HttpUrlConnection to read the content-type header from
     * @return a String containing the header information of this HttpURLConnection
     */
    public static String readHeaders(HttpURLConnection con) {
        return con.getHeaderField("Content-Type");
    }

    /**
     * Adds a timeout of specified type to an HttpURLConnection object
     * @param con the HttpUrlConnection to add timeout to
     * @param timeoutValue the value of the timeout
     * @param timeoutType the type of the timeout. use the enum values available in TimeoutType
     * @return the HttpUrlConnection with timeout added
     */
    public static HttpURLConnection addTimeout(HttpURLConnection con, int timeoutValue, TimeoutType timeoutType) {
        switch(timeoutType.getValue()) {
            case 0:
                con.setConnectTimeout(timeoutValue);
                break;
            case 1:
                con.setReadTimeout(timeoutValue);
                break;
        }
        return con;
    }

    /**
     * Adds redirect handlers which run for cases of a 300+ response code
     * @param con the HttpUrlConnection to add redirect handlers to
     * @param status the status code that was received
     * @param redirectUrl the url to redirect the connection to
     * @param setInstanceFollowRedirects boolean that enables=true or disables=false redirects for a specific url
     * @return the HttpUrlConnection with timeout added
     * @throws IOException
     * @throws MalformedURLException
     */
    @Deprecated
    public static HttpURLConnection addRedirectHandlers(HttpURLConnection con, HttpStatus status, String redirectUrl, boolean setInstanceFollowRedirects) throws IOException, MalformedURLException {
        con.setInstanceFollowRedirects(setInstanceFollowRedirects);
//        HttpUrlConnection.setFollowRedirects(false); // disables redirects for all connections
        if (status.value() == HttpURLConnection.HTTP_MOVED_TEMP
                || status.value() == HttpURLConnection.HTTP_MOVED_PERM) {
            con.setRequestProperty("Location", redirectUrl);
            // TODO is this the correct way to implement the location header?
            String location = con.getHeaderField("Location");
            URL newUrl = new URL(location);
            con = (HttpURLConnection) newUrl.openConnection();
        }
        return con;
    }

    /**
     * // TODO figure out how to write out ErrorStream/ContentStream
     * Reads the response given by the HttpURLConnection con. Closes the connection.
     * @param con the HttpUrlConnection to read a response from
     * @return JSONObject containing the body and status of the response
     * @throws IOException
     */
    public static JSONObject readResponse(HttpURLConnection con) throws IOException {
        int code = con.getResponseCode();

        if (code > 299) {
            ByteSource byteSource = new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    return con.getErrorStream();
                }
            };

            String responseJson = byteSource.asCharSource(Charsets.UTF_8).read();
            return new JSONObject(responseJson);
        } else {
            ByteSource byteSource = new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    return con.getInputStream();
                }
            };

            String responseJson = byteSource.asCharSource(Charsets.UTF_8).read();
            return new JSONObject(responseJson);
        }
    }

}
