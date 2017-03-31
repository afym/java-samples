package com.afym;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Make soap request using a CURL implementation base on
 * URL and save in a file called /tmp/response.xml
 * Documentation : http://www.webservicex.net/ws/WSDetails.aspx?CATID=12&WSID=56
 * wsdl : http://www.webservicex.net/globalweather.asmx?WSDL
 * action : GetCitiesByCountry
 * request : <Country>Germany</Country>
 *
 * @version 1.0
 * @author afym
 */
public class SoapMainCurl {
    public static void main(String[] args) {
        SoapClient client = new SoapClient("http://www.webservicex.net/globalweather.asmx");
        client.setSoapAction("http://www.webserviceX.NET/GetCitiesByCountry");
        client.setSoapBody(getSoapBody());
        client.setFilePath("/tmp/response.xml");
        client.setHost("www.webservicex.net");
        client.makeRequest();
    }

    public static String getSoapBody() {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://www.webserviceX.NET\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <web:GetCitiesByCountry>\n" +
                "         <!--Optional:-->\n" +
                "         <web:CountryName>Germany</web:CountryName>\n" +
                "      </web:GetCitiesByCountry>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }
}

class SoapClient {
    private HttpURLConnection connection;
    private String soapUrl;
    private String soapAction;
    private String soapBody;
    private String filePath;
    private String host;

    public void setHost(String host) {
        this.host = host;
    }

    public SoapClient(String soapUrl){
        this.soapUrl = soapUrl;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public void setSoapBody(String soapBody) {
        this.soapBody = soapBody;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean makeRequest(){
        try {
            this.buildConnection();
            this.buildProperties();
            this.saveResponseInFile();
        } catch (Exception e) {
            e.getStackTrace();
        }

        return true;
    }

    private void buildConnection() throws  IOException{
        URL url = new URL(this.soapUrl);
        this.connection = (HttpURLConnection) url.openConnection();
    }

    private void buildProperties() throws ProtocolException{
        this.connection.setRequestMethod("POST");
        this.connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        this.connection.setRequestProperty("Accept", "application/xml;");
        this.connection.setRequestProperty("Content-Length", Integer.toString(this.soapBody.length()));
        this.connection.setRequestProperty("SOAPAction", this.soapAction);
        this.connection.setRequestProperty("Host", this.host);
        this.connection.setRequestProperty("Connection", "Keep-Alive");
        this.connection.setRequestProperty("User-Agent", "Apache-HttpClient/4.1.1 (java 1.5)");
        this.connection.setDoInput(true);
        this.connection.setDoOutput(true);
    }

    private void saveResponseInFile() throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(this.connection.getOutputStream());
        writer.write(this.soapBody);
        writer.flush();
        writer.close();

        int responseCode = this.connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            OutputStream outputStream = new FileOutputStream(new File(this.filePath));

            int read = 0;
            byte[] bytes = new byte[2048];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }
}