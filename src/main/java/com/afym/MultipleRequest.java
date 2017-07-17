package com.afym;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultipleRequest {
    public final static String URL = "http://fakerestapi.azurewebsites.net/api/Books";
    public final static Logger logger = Logger.getLogger(MultipleRequest.class);

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(5);
        service.execute(new UrlRequest("R01", URL, 4));
        service.execute(new UrlRequest("R02", URL, 1));
        service.execute(new UrlRequest("R03", URL, 7));
        service.execute(new UrlRequest("R04", URL, 2));
        service.execute(new UrlRequest("R05", URL, 4));
        service.shutdown();
    }
}

class UrlRequest implements Runnable {
    private String code;
    private URL url;
    private long seconds = 1000;
    private URLConnection urlConnection;
    private String response;
    private boolean ok = true;

    public UrlRequest(String code, String url, long seconds) {
        this.code = code;
        this.seconds = seconds;

        try {
            this.initURL(url);
        } catch (IOException exception) {
            MultipleRequest.logger.error("<< Error in constructor :: " + exception.getMessage() + " >>");
            MultipleRequest.logger.error(exception);
            MultipleRequest.logger.error("<< Final error block >>");
        }
    }

    private void initURL(String url) throws IOException {
        this.response = "";
        this.url = new URL(url);
        this.urlConnection = this.url.openConnection();
        MultipleRequest.logger.info(this.code + " ==> inside the initURL method ...");
    }

    private String getEncoding() {
        String encoding = this.urlConnection.getContentEncoding();
        MultipleRequest.logger.info(this.code + " ==> inside the getEncoding method ...");
        return encoding == null ? "UTF-8" : encoding;
    }

    private void buildResponse() throws IOException {
        InputStream input = this.urlConnection.getInputStream();
        this.response = IOUtils.toString(input, this.getEncoding());
        MultipleRequest.logger.info(this.code + " ==> inside the buildResponse method ...");
        MultipleRequest.logger.info(this.code + " ==> response : " + this.response);
    }

    private void simulateTime(){
        try {
            MultipleRequest.logger.info(this.code + " ==> inside the simulateTime method <begin> ...");
            TimeUnit.SECONDS.sleep(this.seconds);
            MultipleRequest.logger.info(this.code + " ==> inside the simulateTime method <end> ...");
        } catch (InterruptedException exception) {
            MultipleRequest.logger.error("<< Error in simulateTime() :: " + exception.getMessage() + " >>");
            MultipleRequest.logger.error(exception);
            MultipleRequest.logger.error("<< Final error block >>");
        }
    }

    public String getResponse() {
        return this.response;
    }

    public boolean isOk() {
        return this.ok;
    }

    public void run() {
        try {
            this.simulateTime();
            this.buildResponse();
        } catch (IOException exception) {
            this.ok = false;
            MultipleRequest.logger.error("<< Error in buildResponse() :: " + exception.getMessage() + " >>");
            MultipleRequest.logger.error(exception);
            MultipleRequest.logger.error("<< Final error block >>");
        }
    }
}
