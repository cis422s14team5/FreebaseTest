package com.thedragons;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

// TODO not getting the compete episode list


public class Main {

    public static Properties properties = new Properties();

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        searchAPI("agents of shield", "tv");
    }

    public void searchAPI(String title, String type) {
        String filter = "";
        if (type.equals("film")) {
            filter = "(all type:/film/film)";
        } else if (type.equals("tv")) {
            filter = "(all type:/tv/tv_program)";
        }

        try {
            properties.load(new FileInputStream("freebase.properties"));
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
            url.put("query", title);
            url.put("filter", filter);
            url.put("key", properties.get("API_KEY"));
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
            JSONArray results = (JSONArray) response.get("result");
            JSONObject resultMap = (JSONObject) results.get(0);

            topicAPI(resultMap.get("id").toString(), type);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void topicAPI(String topicId, String type) {
        try {
            properties.load(new FileInputStream("freebase.properties"));
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/topic" + topicId);
            url.put("key", properties.get("API_KEY"));
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject topic = (JSONObject) parser.parse(httpResponse.parseAsString());

            if (type.equals("film")) {
                filmOutput(topic);
            } else if (type.equals("tv")) {
                tvOutput(topic);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void filmOutput(JSONObject topic) {
        String title = JsonPath.read(topic,"$.property['/type/object/name'].values[0].value").toString();
        String director = String.format("Directed by %s", JsonPath.read(topic,
                "$.property['/film/film/directed_by'].values[0].text").toString());
        String description =
                JsonPath.read(topic,"$.property['/common/topic/description'].values[0].value").toString();
        String rating = String.format("Rated %s", JsonPath.read(topic,
                "$property['/film/film/rating'].values[0].text").toString());

        String website;
        try {
            website =
                    JsonPath.read(topic,
                            "$.property['/common/topic/official_website'].values[0].value").toString();
        } catch (com.jayway.jsonpath.InvalidPathException e) {
            website = "No website listed.";
        }

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

    public void tvOutput(JSONObject topic) {
        String title = JsonPath.read(topic,"$.property['/type/object/name'].values[0].value").toString();

        JSONArray creators = JsonPath.read(topic,
                "$.property['/tv/tv_program/program_creator'].values");
        ArrayList<String> creatorList = new ArrayList<>();
        for (Object obj : creators) {
            JSONObject creator = (JSONObject) obj;
            creatorList.add(creator.get("text").toString());
        }

        JSONArray descriptions =
                JsonPath.read(topic, "$.property['/common/topic/description'].values");
        ArrayList<String> descriptionList = new ArrayList<>();
        for (Object obj : descriptions) {
            JSONObject description = (JSONObject) obj;
            descriptionList.add(description.get("value").toString());
        }

        String numSeasons = String.format("Seasons: %s", (int) Float.parseFloat(JsonPath.read(topic,
                "$.property['/tv/tv_program/number_of_seasons'].values[0].value").toString()));
        String numEpisodes = String.format("%s", (int) Float.parseFloat(JsonPath.read(topic,
                "$.property['/tv/tv_program/number_of_episodes'].values[0].value").toString()));

        JSONArray episodes =
                JsonPath.read(topic,"$.property['/tv/tv_program/episodes'].values");
        ArrayList<String> episodeList = new ArrayList<>();
        for (Object obj : episodes) {
            JSONObject episode = (JSONObject) obj;
            episodeList.add(episode.get("text").toString());
        }

        String website;
        try {
            website =
                    JsonPath.read(topic,
                            "$.property['/common/topic/official_website'].values[0].value").toString();
        } catch (com.jayway.jsonpath.InvalidPathException e) {
            website = "No website listed.";
        }

//        String image =
//                JsonPath.read(topic,
//                        "$.property['/common/topic/image']").toString();

        System.out.println(title);
        System.out.println("");

        System.out.print("Created by: ");
        for (int i = 0; i < creatorList.size(); i++) {
            System.out.print(creatorList.get(i));
            if (i != creatorList.size() - 1) {
                System.out.print(", ");
            } else {
                System.out.println(".");
            }
        }

        System.out.println("");
        descriptionList.forEach(System.out::println);
        System.out.println("");
        System.out.println(website);
        System.out.println("");
        System.out.println(numSeasons);
        System.out.println("");
        System.out.printf("Episodes: (%s)%n", numEpisodes);
        episodeList.forEach(System.out::println);
        //System.out.println(episodes.get("values"));
//        System.out.println("");
//        System.out.println(image);
    }

    public void write(String string) {
        try (Writer output = new BufferedWriter(new FileWriter(new File("filmOutput.json")))) {
            output.write(string);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
