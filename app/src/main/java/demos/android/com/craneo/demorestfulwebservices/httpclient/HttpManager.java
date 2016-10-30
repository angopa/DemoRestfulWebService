package demos.android.com.craneo.demorestfulwebservices.httpclient;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by crane on 10/29/2016.
 */

public class HttpManager {

    /**
     * Simple method to get a Connection without credentials
     * @param uri
     * @return
     */
    public static String getData(String uri){

        BufferedReader reader = null;

        try {
            //Start the connection
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            StringBuilder sb = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            //Get all the information in the reader.
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally{
            //close the connection
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    /**
     * This method is use when tried to get data from a Secure URL.
     * @param uri
     * @param userName
     * @param password
     * @return
     */
    public static String getData(String uri, String userName, String password){

        BufferedReader reader = null;
        //If the connection fail we can have the code
        HttpURLConnection con = null;

        //Created the encrypted information for the user and the password, to send by the URL.
        byte[] loginBytes = (userName+":"+password).getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

        try {
            URL url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();

            //Set a header value "Authorization" with the value loginBuilder.toString()
            con.addRequestProperty("Authorization", loginBuilder.toString());

            StringBuilder sb = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            try {
                int status = con.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return null;
        }finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
}
