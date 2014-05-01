package com.thedragons;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;

import java.io.*;
import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

// TODO handle path not found issues on some requests
// TODO write TV search


public class Main {

    public static Properties properties = new Properties();

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        searchAPI();
    }

    public void searchAPI() {
        try {
            properties.load(new FileInputStream("freebase.properties"));
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
            url.put("query", "blade runner");
            url.put("filter", "(all type:/film/film)");
            url.put("key", properties.get("API_KEY"));
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
            JSONArray results = (JSONArray)response.get("result");
            JSONObject resultMap = (JSONObject)results.get(0);

            topicAPI(resultMap.get("id").toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void topicAPI(String topicId) {
        try {
            properties.load(new FileInputStream("freebase.properties"));
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            //String topicId = "/en/blade_runner";
            GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/topic" + topicId);
            url.put("key", properties.get("API_KEY"));
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject topic = (JSONObject)parser.parse(httpResponse.parseAsString());

            output(topic);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void output(JSONObject topic) {
        String title = JsonPath.read(topic,"$.property['/type/object/name'].values[0].value").toString();
        String director = "Directed by " +
                JsonPath.read(topic,"$.property['/film/film/directed_by'].values[0].text").toString();
        String description =
                JsonPath.read(topic,"$.property['/common/topic/description'].values[0].value").toString();
        String rating = "Rated " +
                JsonPath.read(topic, "$property['/film/film/rating'].values[0].text").toString();
        String website =
                JsonPath.read(topic,
                        "$.property['/common/topic/official_website'].values[0].value").toString();
//        String image =
//                JsonPath.read(topic,
//                        "$.property['/common/topic/image']").toString();

        System.out.println(title);
        System.out.println("");
        System.out.println(director);
        System.out.println("");
        System.out.println(rating);
        System.out.println("");
        System.out.println(description);
        System.out.println("");
        System.out.println(website);
//        System.out.println("");
//        System.out.println(image);
    }

    public void write(String string) {
        try (Writer output = new BufferedWriter(new FileWriter(new File("output.json")))) {
            output.write(string);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
