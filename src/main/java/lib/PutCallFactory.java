package lib;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

public class PutCallFactory extends CallFactory {

//====================================== UNIQUE METHODS ========================================

    /**
     * A basic put call
     *
     * @param urlStr the URL of the Rest API endpoint
     * @return the newly created HttpUrlConnection object
     * @throws IOException
     */
    public static HttpURLConnection call(String urlStr) throws IOException {
        HttpURLConnection con = CallFactory.call(urlStr);
        con.setRequestMethod("PUT");
        return con;
    }

    /**
     * A put call with parameters added
     *
     * @param urlStr the URL of the Rest API endpoint
     * @param params key value pair; 0=parameter, 1=value
     */
    public static HttpURLConnection addParams(String urlStr, Map<String, String> params) throws IOException {
        HttpURLConnection con = CallFactory.addParams(urlStr, params);
        con.setRequestMethod("PUT");
        return con;
    }


//====================================== INHERITED METHODS ========================================

}