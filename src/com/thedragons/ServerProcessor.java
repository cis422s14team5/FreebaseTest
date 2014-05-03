package com.thedragons;


public class ServerProcessor {

    private static final int WAITING = 0;
    private static final int CONNECTED = 1;
    private static final int FILM = 2;
    private static final int TV = 3;

    private int state;
    private Freebase freebase;

    public ServerProcessor() {
        freebase = new Freebase(); 
        state = WAITING;
    }

    public String processInput(String input) {
        String output = "";

        switch (state) {
            case WAITING:
                state = CONNECTED;
                output = ("Connected to the Freebase Movie and TV Server. Enter \"tv\" to search for TV shows or " +
                          "\"film\" to search for films.");
                break;

            case CONNECTED:
                if (input.contains("film")) {
                    state = FILM;
                    output = ("Enter a <film title> to search.");
                } else if (input.contains("tv")) {
                    state = TV;
                    output = ("Enter a <tv show title> to search.");
                } else {
                    output = ("Enter \"tv\" to search for TV shows or \"film\" to search for films.");
                }
                break;

            case FILM:
                if (input.equals("tv")) {
                    state = TV;
                    output = ("Enter a <tv show title> to search or \"film\" to search for films.");
                } else if (!(input.equals("quit") && !input.equals("tv") &&  !input.equals(""))) {
                    freebase.search(input, "film");
                    output = freebase.getTopic().toJSONString();
                } else {
                    output = ("Enter a <film title> to search or \"tv\" to search for TV shows.");
                }
                break;

            case TV:
                if (input.equals("film")) {
                    state = FILM;
                    output = ("Enter a <film title> to search or \"tv\" to search for TV shows.");
                } else if (!(input.equals("quit") && !input.equals("film") && !input.equals(""))) {
                    freebase.search(input, "tv");
                    output = freebase.getTopic().toJSONString();
                } else {
                    output = ("Enter a <tv show title> to search or \"film\" to search for films.");
                }
                break;

            default:
                break;
        }

        if (input.equals("quit")) {
            output = ("Bye.");
            state = WAITING;
        }

        return output;
    }

}
