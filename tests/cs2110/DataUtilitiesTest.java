package cs2110;

import static cs2110.DataUtilities.*;
import static cs2110.DataUtilities.DedupPolicy.*;
import static cs2110.DataUtilities.SearchPolicy.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DataUtilitiesTest {
    /* Note: These tests are meant to serve as basic correctness checks and examples
     * of how to set up unit tests for these methods. They do NOT come close to offering
     * good coverage of the `DataUtilities` class. We encourage you to do additional
     * testing to gain confidence in the correctness of your submission.
     */

    @DisplayName("WHEN one of the `views` records has the target userID, THEN `binarySearch()` "
            + "with the BY_USER_ID Comparator and LEFT search policy returns the index of that "
            + "view.")
    @Test
    public void testBinarySearchFindsUniqueMatch() {
        View[] views = new View[]{

            new View("A", "V", LocalDateTime.of(2026,1,1,0,0)),
            new View("B", "V", LocalDateTime.of(2026,1,2,0,0)),
            new View("C", "V", LocalDateTime.of(2026,1,3,0,0)),
            new View("D", "V", LocalDateTime.of(2026,1,4,0,0)),
            new View("E", "V", LocalDateTime.of(2026,1,5,0,0)),
            new View("F", "V", LocalDateTime.of(2026,1,6,0,0)),
            new View("G", "V", LocalDateTime.of(2026,1,7,0,0)),
        };
        View key = new View("C", "V", LocalDateTime.of(2026,1,8,0,0));
        assertEquals(2, binarySearch(views, key, BY_USER_ID, LEFT));
    }

    @DisplayName("WHEN the userID of the `key` is alphabetically after the userIDs of all of the "
            + "`view`s, THEN `binarySearch()` with the BY_USER_ID Comparator returns the length "
            + "of the array.")
    @Test
    public void testBinarySearchNotPresent() {
        View[] views = new View[]{
                new View("A", "V", LocalDateTime.now()),
                new View("B", "V", LocalDateTime.now()),
                new View("C", "V", LocalDateTime.now()),
                new View("D", "V", LocalDateTime.now()),
                new View("E", "V", LocalDateTime.now()),
                new View("F", "V", LocalDateTime.now())
        };
        View key = new View("G", "V", LocalDateTime.now());
        assertEquals(6, binarySearch(views, key, BY_USER_ID, LEFT));
        assertEquals(6, binarySearch(views, key, BY_USER_ID, RIGHT));
    }

    /**
     * Asserts that `views[l..r)` is sorted according to `cmp`
     */
    @SuppressWarnings("SameParameterValue")
    void assertSorted(View[] views, int l, int r, Comparator<View> cmp) {
        for (int i = l; i < r - 1; i++) {
            assertTrue(cmp.compare(views[i], views[i + 1]) <= 0);
        }
    }

    @DisplayName("WHEN we merge on timestamps using the KEEP_ALL deduplication policy AND the "
            + "records are interleaved between the subarrays and have unique timestamps, THEN the "
            + "merged subarray is correctly sorted.")
    @Test
    void testMergeInterleavedUnique() {
        View[] views = new View[]{
                new View("A", "V", LocalDateTime.of(2025, 1, 1, 0, 0)),
                new View("B", "V", LocalDateTime.of(2025, 1, 4, 0, 0)),
                new View("C", "V", LocalDateTime.of(2025, 1, 6, 0, 0)),
                new View("D", "V", LocalDateTime.of(2025, 1, 2, 0, 0)),
                new View("E", "V", LocalDateTime.of(2025, 1, 3, 0, 0)),
                new View("F", "V", LocalDateTime.of(2025, 1, 5, 0, 0)),
                new View("G", "V", LocalDateTime.of(2025, 1, 7, 0, 0)),
        };
        View[] work = new View[3];
        merge(views, work, 0, 3, 3, 7, BY_TIMESTAMP, KEEP_ALL);
        assertSorted(views, 0, 7, BY_TIMESTAMP);
    }

    @DisplayName("WHEN we call `deduplicatingSort()` with KEEP_FIRST on two equivalent and one "
            + "distinct records, THEN the output contains the correct two elements in the correct "
            + "order.")
    @Test
    void testSortThreeEquivalentPairKeepFirst() {
        View[] views = new View[]{
                new View("A", "V", LocalDateTime.of(2026,1,2,0,0)),
                new View("B", "V", LocalDateTime.of(2026,1,1,0,0)),
                new View("C", "V", LocalDateTime.of(2026,1,1,0,0)),
        };
        View[] sorted = deduplicatingSort(views, BY_TIMESTAMP, KEEP_FIRST);
        assertEquals(2, sorted.length);
        assertEquals("B", sorted[0].userID());
        assertEquals("A", sorted[1].userID());
    }
}
