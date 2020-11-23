package lib;

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

import org.springframework.web.multipart.MultipartFile;
import src.main.java.exceptions.ContentTypeNotSupportedException;
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
     * Adds String as body to HttpURLConnection request. Specify type of string content in content-type header
     * @param con the HttpURLConnection
     * @param body the body to be added
     * @return the HttpURLConnection after adding the body
     */
    public static HttpURLConnection addBody(HttpURLConnection con, String body) {
        String charsetName = "UTF-8";
        byte[] outputInBytes;
        try {
            outputInBytes = body.getBytes(charsetName);
            OutputStream os = con.getOutputStream();
            os.write( outputInBytes );
            os.flush();
            os.close();
        } catch (UnsupportedEncodingException uee) {
            System.out.println("Charset " + charsetName + " failed. " + uee.getMessage());
            uee.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("There was an issue adding the specified body to the HttpURLConnection obeject with the hashcode: " + con.toString());
            ioe.printStackTrace();
        }
        return con;
    }

    public static HttpURLConnection addBody(HttpURLConnection con, MultipartFile body) {
        // TODO Multipart body impl
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
