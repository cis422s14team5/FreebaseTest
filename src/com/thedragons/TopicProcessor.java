package com.thedragons;

import com.jayway.jsonpath.JsonPath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;


public class TopicProcessor {

    public ArrayList<String> filmOutput(JSONObject topic) {
        ArrayList<String> output = new ArrayList<>();
        
        String title = JsonPath.read(topic, "$.property['/type/object/name'].values[0].value").toString();
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

        output.clear();
        output.add(title);
        output.add("");
        output.add(director);
        output.add("");
        output.add(rating);
        output.add("");
        output.add(description);
        output.add("");
        output.add(website);
        output.add("");

        return output;

//        System.out.println("");
//        System.out.println(image);
    }

    public ArrayList<String> tvOutput(JSONObject topic) {
        ArrayList<String> output = new ArrayList<>();
        
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

        output.clear();
        output.add(title);
        output.add("");

        String creatorsTemp = "";
        creatorsTemp += "Created by: ";
        for (int i = 0; i < creatorList.size(); i++) {
            creatorsTemp += creatorList.get(i);
            if (i != creatorList.size() - 1) {
                creatorsTemp += ", ";
            } else {
                creatorsTemp += ".";
            }
        }
        output.add(creatorsTemp);

        output.add("");
        descriptionList.forEach(output::add);
        output.add("");
        output.add(website);
        output.add("");
        output.add(numSeasons);
        output.add("");
        output.add(String.format("Episodes: (%s)%n", numEpisodes));
        episodeList.forEach(output::add);
        output.add("");

        return output;

//        System.out.println(episodes.get("values"));
//        System.out.println("");
//        System.out.println(image);
    }
}
