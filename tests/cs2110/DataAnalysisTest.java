package cs2110;

import static cs2110.DataAnalysis.*;
import static cs2110.DataUtilities.*;
import static org.junit.jupiter.api.Assertions.*;
import static cs2110.Simulation.*;
import static cs2110.DataUtilities.DedupPolicy.*;

import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DataAnalysisTest {

    /* Note: These tests are meant to serve as basic correctness checks and examples
     * of how to set up unit tests for these methods. They do NOT come close to offering
     * good coverage of the `DataAnalysis` class. We encourage you to do additional
     * testing to gain confidence in the correctness of your submission.
     */

    @DisplayName("The `Simulation.fromFile()` method correctly parses "
            + "the contents of a file to a `View` array.")
    @Test
    void testFromFile() throws IOException {
        View[] views = fromFile("small.txt");
        assertEquals(20, views.length); // correct number of entries

        View[] expected = new View[]{
                new View("A", "v4", LocalDateTime.of(2024, 4, 15, 2, 35, 0)),
                new View("A", "v6", LocalDateTime.of(2024, 4, 15, 14, 34, 0)),
                new View("C", "v6", LocalDateTime.of(2024, 4, 15, 19, 56, 0)),
                new View("D", "v1", LocalDateTime.of(2024, 4, 15, 11, 54, 0)),
                new View("B", "v6", LocalDateTime.of(2024, 4, 15, 8, 44, 0)),
                new View("C", "v5", LocalDateTime.of(2024, 4, 15, 6, 45, 0)),
                new View("B", "v5", LocalDateTime.of(2010, 8, 7, 3, 43, 47)),
                new View("A", "v2", LocalDateTime.of(2009, 6, 29, 23, 17, 15)),
                new View("A", "v1", LocalDateTime.of(2024, 4, 15, 21, 14, 0)),
                new View("D", "v5", LocalDateTime.of(2013, 9, 10, 16, 38, 1)),
                new View("B", "v3", LocalDateTime.of(2024, 4, 15, 3, 5, 0)),
                new View("C", "v5", LocalDateTime.of(2024, 4, 15, 8, 51, 0)),
                new View("B", "v6", LocalDateTime.of(2024, 4, 15, 20, 44, 0)),
                new View("C", "v1", LocalDateTime.of(2024, 4, 15, 5, 44, 0)),
                new View("C", "v1", LocalDateTime.of(2024, 4, 15, 7, 55, 0)),
                new View("C", "v2", LocalDateTime.of(2024, 4, 15, 11, 22, 0)),
                new View("D", "v3", LocalDateTime.of(2024, 4, 15, 8, 9, 0)),
                new View("A", "v4", LocalDateTime.of(2024, 4, 15, 8, 29, 0)),
                new View("D", "v2", LocalDateTime.of(2024, 4, 15, 14, 37, 0)),
                new View("C", "v1", LocalDateTime.of(2008, 7, 18, 13, 2, 1)),
        };

        assertArrayEquals(expected, views);
    }

    @DisplayName("WHEN we call `firstVideoViews()` on the released `small.txt` data, THEN it "
            + "returns the Views for each video \"v1\"-\"v6\" with the earliest timestamps.")
    @Test
    void testFirstViewSmall() throws IOException {
        View[] views = fromFile("small.txt");
        View[] first = firstVideoViews(views);
        assertEquals(6, first.length); // correct number of entries

        // Note: Order of these elements is underspecified; sort to remove ambiguity
        first = DataUtilities.deduplicatingSort(first, BY_VIDEO_ID, KEEP_ALL);
        assertEquals(new View("C", "v1", LocalDateTime.of(2008, 7, 18, 13, 2, 1)), first[0]);
        assertEquals(new View("A", "v2", LocalDateTime.of(2009, 6, 29, 23, 17, 15)), first[1]);
        assertEquals(new View("B", "v3", LocalDateTime.of(2024, 4, 15, 3, 5)), first[2]);
        assertEquals(new View("A", "v4", LocalDateTime.of(2024, 4, 15, 2, 35)), first[3]);
        assertEquals(new View("B", "v5", LocalDateTime.of(2010, 8, 7, 3, 43, 47)), first[4]);
        assertEquals(new View("B", "v6", LocalDateTime.of(2024, 4, 15, 8, 44)), first[5]);
    }

    @DisplayName("WHEN we call `totalViews()` on the released `small.txt` data, passing in each "
            + "videoID \"v1\"-\"v6\", THEN it returns the correct count.")
    @Test
    void testCountInIntervalSmall() throws IOException {
        View[] views = fromFile("small.txt");
        assertEquals(5, totalViews(views, "v1"));
        assertEquals(3, totalViews(views, "v2"));
        assertEquals(2, totalViews(views, "v3"));
        assertEquals(2, totalViews(views, "v4"));
        assertEquals(4, totalViews(views, "v5"));
        assertEquals(4, totalViews(views, "v6"));
    }

    @DisplayName("WHEN we call `countDistinctUsersInTimeInterval()` on the released `small.txt` "
            + "data for a time interval within the range of the records, THEN it returns the "
            + "correct count, 2.")
    @Test
    void testCountDistinctUsersSmall() throws IOException {
        View[] views = fromFile("small.txt");
        assertEquals(2, countDistinctUsersInTimeInterval(views,
                LocalDateTime.of(2009, 1, 1, 0, 0), LocalDateTime.of(2012, 1, 1, 0, 0)));
    }

    @DisplayName("WHEN we call `lastKViewedByUser()` on the released `small.txt` data with k=2 and "
            + "user \"A\", THEN it returns the correct array of `View`s.")
    @Test
    void testLastKUsersSmall() throws IOException {
        View[] views = fromFile("small.txt");
        View[] lastK = lastKViewedByUser(views, "A", 2);
        assertEquals(2, lastK.length);
        lastK = deduplicatingSort(lastK, BY_VIDEO_ID, KEEP_ALL); // sort entries
        assertEquals(new View("A", "v1", LocalDateTime.of(2024, 4, 15, 21, 14)), lastK[0]);
        assertEquals(new View("A", "v6", LocalDateTime.of(2024, 4, 15, 14, 34)), lastK[1]);
    }

    @DisplayName("WHEN we call `mostObsessedViewer()` on the released `small.txt` data with the "
            + "videoID \"v5\", THEN it returns the correct userID, \"C\".")
    @Test
    void testMostObsessedViewerSmall() throws IOException {
        View[] views = fromFile("small.txt");
        String fan = mostObsessedViewer(views, "v5");
        assertEquals("C", fan);
    }
    // =========================================================================
    // countDistinctUsersInTimeInterval Tests
    // =========================================================================

    @DisplayName("WHEN multiple views in the interval belong to the same user, THEN the user is only counted once.")
    @Test
    void testCountDistinctUsersDuplicateInInterval() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.of(2026, 1, 1, 10, 0)),
                new View("A", "v2", LocalDateTime.of(2026, 1, 2, 10, 0)),
                new View("B", "v1", LocalDateTime.of(2026, 1, 3, 10, 0))
        };
        assertEquals(2, countDistinctUsersInTimeInterval(views,
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 4, 0, 0)));
    }

    @DisplayName("WHEN the time interval contains no views, THEN it returns 0.")
    @Test
    void testCountDistinctUsersEmptyInterval() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.of(2026, 1, 1, 10, 0))
        };
        assertEquals(0, countDistinctUsersInTimeInterval(views,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)));
    }

    // =========================================================================
    // lastKViewedByUser Tests
    // =========================================================================

    @DisplayName("WHEN the user has viewed fewer than k distinct videos, THEN it returns all distinct videos viewed.")
    @Test
    void testLastKUsersFewerThanK() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.of(2026, 1, 1, 10, 0)),
                new View("A", "v1", LocalDateTime.of(2026, 1, 2, 10, 0)), // Duplicate view of v1
                new View("A", "v2", LocalDateTime.of(2026, 1, 3, 10, 0))
        };
        View[] lastK = lastKViewedByUser(views, "A", 5);

        assertEquals(2, lastK.length);
        lastK = deduplicatingSort(lastK, BY_VIDEO_ID, KEEP_ALL);

        // It should keep the latest timestamp for v1
        assertEquals(new View("A", "v1", LocalDateTime.of(2026, 1, 2, 10, 0)), lastK[0]);
        assertEquals(new View("A", "v2", LocalDateTime.of(2026, 1, 3, 10, 0)), lastK[1]);
    }

    @DisplayName("WHEN the user has viewed more than k distinct videos, THEN it strictly returns the latest k.")
    @Test
    void testLastKUsersMoreThanK() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.of(2026, 1, 1, 10, 0)),
                new View("A", "v2", LocalDateTime.of(2026, 1, 2, 10, 0)),
                new View("A", "v3", LocalDateTime.of(2026, 1, 3, 10, 0)),
                new View("A", "v4", LocalDateTime.of(2026, 1, 4, 10, 0))
        };
        View[] lastK = lastKViewedByUser(views, "A", 2);

        assertEquals(2, lastK.length);
        lastK = deduplicatingSort(lastK, BY_VIDEO_ID, KEEP_ALL);

        // Should only contain the two most recently watched videos (v3 and v4)
        assertEquals(new View("A", "v3", LocalDateTime.of(2026, 1, 3, 10, 0)), lastK[0]);
        assertEquals(new View("A", "v4", LocalDateTime.of(2026, 1, 4, 10, 0)), lastK[1]);
    }

    // =========================================================================
    // mostObsessedViewer Tests
    // =========================================================================

    @DisplayName("WHEN the queried videoID has no views, THEN mostObsessedViewer returns null.")
    @Test
    void testMostObsessedViewerNoViews() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.of(2026, 1, 1, 10, 0))
        };
        assertNull(mostObsessedViewer(views, "v99"));
    }

    @DisplayName("WHEN multiple users have viewed a video, THEN it returns the user with the highest view count.")
    @Test
    void testMostObsessedViewerClearWinner() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.of(2026, 1, 1, 10, 0)),
                new View("B", "v1", LocalDateTime.of(2026, 1, 2, 10, 0)),
                new View("B", "v1", LocalDateTime.of(2026, 1, 3, 10, 0)),
                new View("C", "v1", LocalDateTime.of(2026, 1, 4, 10, 0))
        };
        assertEquals("B", mostObsessedViewer(views, "v1"));
    }
}
