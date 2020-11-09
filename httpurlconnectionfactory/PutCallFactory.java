package com.adp.common.httpurlconnectionfactory;

import com.adp.exceptions.ContentTypeNotSupportedException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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