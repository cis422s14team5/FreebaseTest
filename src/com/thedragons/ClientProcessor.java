package com.thedragons;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;

public class ClientProcessor {

    private static final int FILM = 0;
    private static final int TV = 1;

    private int state = FILM;

    TopicProcessor processor = new TopicProcessor();

    public void processServer(String input) {
        System.out.println();
        String[] outputArray = input.split("");

        if (outputArray[0].equals("{")) {
            Object obj = JSONValue.parse(input);
            JSONObject topic = (JSONObject) obj;

            ArrayList<String> outputList = new ArrayList<>();
            switch (state) {
                case FILM:
                    outputList = processor.filmOutput(topic);
                    break;
                case TV:
                    outputList = processor.tvOutput(topic);
                default:
                    break;
            }
            outputList.forEach(System.out::println);

            System.out.println();
            System.out.println("Enter a title to search again, film or tv to switch filters, or quit.");
        } else {
            System.out.println("Server: " + input);
        }
    }

    public String processUser(String input) {

        if (input.equals("film")) {
            state = FILM;
        } else if (input.equals("tv")) {
            state = TV;
        }

        return input;
    }


}
