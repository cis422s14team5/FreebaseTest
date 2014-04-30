package com.thedragons;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import java.io.FileInputStream;
import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class Main {

    public static Properties properties = new Properties();

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        // searchAPI();
        topicAPI();
        // reconciliationAPI();
    }

    public void searchAPI() {
        try {
            properties.load(new FileInputStream("freebase.properties"));
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
            url.put("query", "Blade Runner");
            // url.put("filter", "(all type:/music/artist created:\"The Lady Killer\")");
            url.put("limit", "10");
            url.put("indent", "true");
            url.put("key", properties.get("API_KEY"));
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
            JSONArray results = (JSONArray)response.get("result");
            for (Object result : results) {
                System.out.println(JsonPath.read(result,"$.name").toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void topicAPI() {
        try {
            properties.load(new FileInputStream("freebase.properties"));
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            String topicId = "/en/blade_runner";
            GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/topic" + topicId);
            url.put("key", properties.get("API_KEY"));
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject topic = (JSONObject)parser.parse(httpResponse.parseAsString());
            System.out.println(JsonPath.read(topic,"$.property['/type/object/name'].values[0].value").toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void reconciliationAPI() {
        try {
            properties.load(new FileInputStream("freebase.properties"));
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/reconcile");
            url.put("name", "Prometheus");
            url.put("kind", "/film/film");
            url.put("prop", "/film/film/directed_by:Ridley Scott");
            url.put("key", properties.get("API_KEY"));
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
            JSONArray candidates = (JSONArray)response.get("candidate");
            for (Object candidate : candidates) {
                System.out.print(JsonPath.read(candidate,"$.mid").toString()+" (");
                System.out.println(JsonPath.read(candidate,"$.confidence").toString()+")");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
