package com.jariasbecera.tools.arcgisonline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aol")
public class AolController {

    @GetMapping(value = "/auth")
    public String auth() {

        String url = "https://www.arcgis.com/sharing/generateToken";
        String username = "AppMun";
        String password = "appmp10*-";
        String request = "gettoken*-";
        String expiration = "60";
        String f = "json";
        String referer = "f";

        try {
            String rta = autenticar(url, username, password, request, expiration, f, referer);

            JSONObject jsonObject = new JSONObject(rta);

            return jsonObject.getString("token");

        } catch (Exception e) {

            e.printStackTrace();
            return ("error");
        }
        // return new String("{name:\"John\", age:31, city:\"New York\"}");
    }

    private String autenticar(String url, String username, String password, String request, String expiration, String f,
            String referer) throws Exception {

        HttpPost post = new HttpPost(url);

        String respuesta = "";

        // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("username", username));
        urlParameters.add(new BasicNameValuePair("password", password));
        urlParameters.add(new BasicNameValuePair("request", request));
        urlParameters.add(new BasicNameValuePair("expiration", expiration));
        urlParameters.add(new BasicNameValuePair("f", f));
        urlParameters.add(new BasicNameValuePair("referer", referer));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            respuesta = EntityUtils.toString(entity);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return respuesta;

    }

    @GetMapping(value = "/file")
    public void file(HttpServletResponse response) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("src/main/resources/SOL_SOLICITUD_202204081619.csv"));
            String line = reader.readLine();
            while (line != null) {
                ServletOutputStream so = response.getOutputStream();
                so.println("   -    " + line);

                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
