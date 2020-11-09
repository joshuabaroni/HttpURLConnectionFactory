package com.adp.common.httpurlconnectionfactory;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A Functional class for creating get calls
 */
public class GetCallFactory extends CallFactory {

//====================================== UNIQUE METHODS ========================================

    /**
     * A basic get call
     * @param urlStr the URL of the Rest API endpoint
     * @return the newly created HttpUrlConnection object
     * @throws IOException
     */
    public static HttpURLConnection call(String urlStr) throws IOException {
        HttpURLConnection con = CallFactory.call(urlStr);
        con.setRequestMethod("GET");
        return con;
    }

    /**
     * A get call with parameters added
     * @param urlStr the URL of the Rest API endpoint
     * @param params key value pair; 0=parameter, 1=value
     */
    public static HttpURLConnection addParams(String urlStr, Map<String, String> params) throws IOException {
        HttpURLConnection con = CallFactory.addParams(urlStr, params);
        con.setRequestMethod("GET");
        return con;
    }

    /**
     * Experimental: Do not use as there are hardcoded values
     * @param con the HttpUrlConnection to add cookies to
     * @param cookieManager TODO research proper cookieManager use cases
     * @return the HttpUrlConnection with cookies added
     * @throws IOException
     */
    @Deprecated
    public static HttpURLConnection addCookies (HttpURLConnection con, CookieManager cookieManager) throws IOException{
        String cookiesHeader = con.getHeaderField("Set-Cookie");
        List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);

        cookies.forEach(cookie -> cookieManager.getCookieStore().add(null, cookie));

        Optional<HttpCookie> usernameCookie = cookies.stream()
                .findAny().filter(cookie -> cookie.getName().equals("username"));
        if (usernameCookie == null) {
            cookieManager.getCookieStore().add(null, new HttpCookie("username", "john"));
        }

        URL url = con.getURL();
        con.disconnect();
        con = (HttpURLConnection) url.openConnection();

        // TODO check Collection<String> conversion
//        con.setRequestProperty("Cookie",
//                StringUtils.join(cookieManager.getCookieStore().getCookies(), ';'));

        return con;
    }

    /**
     * Returns a full response in string format to the user. Closes the connection.
     * @param con the HttpUrlConnection to build a full response from
     * @return the response as a String
     * @throws IOException
     */
    public static String getFullResponse(HttpURLConnection con) throws IOException {
        StringBuilder fullResponseBuilder = new StringBuilder();

        // read status and message
        fullResponseBuilder.append(con.getResponseCode())
                .append(" ")
                .append(con.getResponseMessage())
                .append("\n");

        // read headers
        con.getHeaderFields().entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .forEach(entry -> {
                    // read response content
                    fullResponseBuilder.append(entry.getKey()).append(": ");
                    List headerValues = entry.getValue();
                    Iterator it = headerValues.iterator();
                    if (it.hasNext()) {
                        fullResponseBuilder.append(it.next());
                        while (it.hasNext()) {
                            fullResponseBuilder.append(", ").append(it.next());
                        }
                    }
                    fullResponseBuilder.append("\n");
                });

        return fullResponseBuilder.toString();
    }

//====================================== INHERITED METHODS ========================================

}