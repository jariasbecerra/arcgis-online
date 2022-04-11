package com.jariasbecera.tools.arcgisonline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

            int i = 0;
            while (line != null) {
                ServletOutputStream so = response.getOutputStream();
                String[] valores = line.split(";");
                so.println(i + " - " + valores[0] + " - " + valores[1] + " - " + valores[2]);
                
                llamadoAol("","");
                
                // read next line
                i++;
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String llamadoAol(String url, String token) throws Exception {
        HttpPost post = new HttpPost(url);
        String respuesta = "";

        // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("username", token));
        //urlParameters.add(new BasicNameValuePair("password", password));
        //urlParameters.add(new BasicNameValuePair("request", request));
        //urlParameters.add(new BasicNameValuePair("expiration", expiration));
        //urlParameters.add(new BasicNameValuePair("f", f));
        //urlParameters.add(new BasicNameValuePair("referer", referer));
        
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

    private String llamadoURLGet(String url) throws Exception {

        HttpGet get = new HttpGet(url);
        String respuesta = "";

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            respuesta = EntityUtils.toString(entity);
            //respuesta =  response.getStatusLine().getStatusCode()+"";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return respuesta;
    }    


    @GetMapping(value = "/filexls")
    public void filexls(HttpServletResponse response) {
    
        try {

            FileInputStream file = new FileInputStream(new File("src/main/resources/FOTOS.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
       
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rows = sheet.getLastRowNum();
            

            for (int i = 1; i <= rows; i++) {
                XSSFRow row = sheet.getRow(i);
        
                System.out.println(row.getRowNum());
                //System.out.println("Valor: "+row.getCell(4).getStringCellValue());                
                String rta = "";
                XSSFCell cell = row.createCell(5); 

                try{
                    rta = llamadoURLGet(row.getCell(4).getStringCellValue());                                                 
                    cell.setCellValue( rta);                     
                }catch(Exception e) {
                    rta="200";
                    cell.setCellValue( rta);
                    //e.printStackTrace();
                }


                rta = "";
                cell = row.createCell(7); 

                try{
                    rta = llamadoURLGet(row.getCell(6).getStringCellValue());                                                 
                    cell.setCellValue( rta);                     
                }catch(Exception e) {
                    rta="200";
                    cell.setCellValue( rta);
                    //e.printStackTrace();
                }
                
                
                rta = "";
                cell = row.createCell(9); 

                try{
                    rta = llamadoURLGet(row.getCell(8).getStringCellValue());                                                 
                    cell.setCellValue( rta);                     
                }catch(Exception e) {
                    rta="200";
                    cell.setCellValue( rta);
                    //e.printStackTrace();
                }                

            }
            
            
            FileOutputStream outFile =new FileOutputStream(new File("src/main/resources/FOTOS2.xlsx"));
            workbook.write(outFile);
            outFile.close();
            
            
            workbook.close();
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }    

}
