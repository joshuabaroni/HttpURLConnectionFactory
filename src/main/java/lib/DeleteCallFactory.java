package lib;

import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

public class DeleteCallFactory extends CallFactory {

//====================================== UNIQUE METHODS ========================================

    /**
     * A basic delete call
     * @param urlStr the URL of the Rest API endpoint
     * @return the newly created HttpUrlConnection object
     * @throws IOException
     */
    public static HttpURLConnection call(String urlStr) throws IOException {
        HttpURLConnection con = CallFactory.call(urlStr);
        con.setRequestMethod("DELETE");
        return con;
    }

    /**
     * A delete call with parameters added
     * @param urlStr the URL of the Rest API endpoint
     * @param params key value pair; 0=parameter, 1=value
     */
    public static HttpURLConnection addParams(String urlStr, Map<String, String> params) throws IOException {
        HttpURLConnection con = CallFactory.addParams(urlStr, params);
        con.setRequestMethod("DELETE");
        return con;
    }

//====================================== INHERITED METHODS ========================================

}
