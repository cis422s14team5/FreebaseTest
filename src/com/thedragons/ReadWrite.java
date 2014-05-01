package com.thedragons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;


/**
 * Contains methods used to read and write to the file system. Used to read and write to the book.txt file.
 */
public class ReadWrite {

    /**
     * Reads from a from the specified path into an arrayList
     * @param path is the path to the file that must be read.
     * @return a list where each element in a line of the file.
     */
    protected ArrayList<String> read(Path path) {
        ArrayList<String> input = new ArrayList<>();

        try {
            BufferedReader reader = Files.newBufferedReader(path, Charset.forName("US-ASCII"));
            String line;
            while ((line = reader.readLine()) != null) {
                input.add(line.substring(0, line.length() - 4));
            }
            reader.close();
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        return input;
    }

    /**
     * Writes the array list to a file at the specified path.
     * @param path is the path to the file.
     * @param addressBookList is the arrayList where each element will become a line in the file.
     */
    protected void write(Path path, ArrayList<String> addressBookList) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("US-ASCII"));
            for (String string : addressBookList) {
                writer.write(string + ".tsv\n");
            }
            writer.close();
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }
}
