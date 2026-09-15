package cs2110;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

import static cs2110.DataUtilities.*;
import static cs2110.DataAnalysis.*;
import static cs2110.DataUtilities.DedupPolicy.KEEP_FIRST;

public class Simulation {

    /**
     * Reads data from a text file `fileName` and returns an array of `View`s.
     */
    static View[] fromFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        // Count number of lines in file
        int numRecords = 0;
        while (br.readLine() != null) {
            numRecords++;
        }
        br.close();

        View[] records = new View[numRecords];

        // Parse data to create array
        br = new BufferedReader(new FileReader(fileName));
        String line = br.readLine();
        int i = 0;
        while (line != null) {
            String[] parts = line.split(",");
            String userID = parts[0];
            String videoID = parts[1];
            LocalDateTime timestamp = LocalDateTime.parse(parts[2]);
            records[i] = new View(userID, videoID, timestamp);
            i++;
            line = br.readLine();
        }
        br.close();

        return records;
    }

    /**
     * Prints the entries of `records` one per line.
     */
    static void printRecordArray(View[] records) {
        for (int i = 0; i < records.length; i++) {
            System.out.printf("%d: %s viewed %s @ %s\n", i+1, records[i].userID(),
                records[i].videoID(), records[i].timestamp());
        }
    }

    /**
     * Main method to experiment with the methods in the `DataAnalysis` class. Feel free to add
     * to or modify this method to run additional queries and "playtest" your implementations.
     * You are not submitting this file. We have provided three ".txt" data files that you can
     * load in this method. Feel free to also develop your own, but make sure to stick to the
     * format of the existing ones.
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            System.out.print("Which data file would you like to load (e.g., small.txt)? ");
            View[] views = fromFile(in.nextLine());

            System.out.println("\nLoaded " + views.length + " view records:");
            printRecordArray(views);

            System.out.println("\nFirst view for each video: ");
            printRecordArray(firstVideoViews(views));

            View[] videos = deduplicatingSort(views, BY_VIDEO_ID, KEEP_FIRST);
            for (int i=0; i<videos.length; i++) {
                String videoID = videos[i].videoID();
                System.out.printf("\n%s has %d total views", videoID, totalViews(views, videoID));
            }


            // FIXME: Add more queries to this branch to experiment with your code further.
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}
