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

                new View("A", "V", LocalDateTime.of(2026, 1, 1, 0, 0)),
                new View("B", "V", LocalDateTime.of(2026, 1, 2, 0, 0)),
                new View("C", "V", LocalDateTime.of(2026, 1, 3, 0, 0)),
                new View("D", "V", LocalDateTime.of(2026, 1, 4, 0, 0)),
                new View("E", "V", LocalDateTime.of(2026, 1, 5, 0, 0)),
                new View("F", "V", LocalDateTime.of(2026, 1, 6, 0, 0)),
                new View("G", "V", LocalDateTime.of(2026, 1, 7, 0, 0)),
        };
        View key = new View("C", "V", LocalDateTime.of(2026, 1, 8, 0, 0));
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
                new View("A", "V", LocalDateTime.of(2026, 1, 2, 0, 0)),
                new View("B", "V", LocalDateTime.of(2026, 1, 1, 0, 0)),
                new View("C", "V", LocalDateTime.of(2026, 1, 1, 0, 0)),
        };
        View[] sorted = deduplicatingSort(views, BY_TIMESTAMP, KEEP_FIRST);
        assertEquals(2, sorted.length);
        assertEquals("B", sorted[0].userID());
        assertEquals("A", sorted[1].userID());
    }

    // =========================================================================
    // merge() Tests
    // =========================================================================

    @DisplayName("WHEN merging with KEEP_FIRST, THEN duplicates from the right array are dropped in favor of the left.")
    @Test
    void testMergeKeepFirstDropsRightDuplicates() {
        // Left array has "A", Right array has "A" (different videoID so we can tell them apart)
        View[] views = new View[]{
                new View("A", "leftVideo", LocalDateTime.MIN),
                new View("B", "leftVideo", LocalDateTime.MIN),
                new View("A", "rightVideo", LocalDateTime.MIN),
                new View("C", "rightVideo", LocalDateTime.MIN)
        };
        View[] work = new View[2]; // Need space for the left array copy

        // Merge views[0..2) and views[2..4) using BY_USER_ID
        int k = merge(views, work, 0, 2, 2, 4, BY_USER_ID, KEEP_FIRST);

        assertEquals(3, k); // Should result in 3 elements: A, B, C
        assertEquals("leftVideo", views[0].videoID()); // Kept the left 'A'
        assertEquals("B", views[1].userID());
        assertEquals("C", views[2].userID());
    }

    @DisplayName("WHEN merging with KEEP_LAST, THEN duplicates from the left array are dropped in favor of the right.")
    @Test
    void testMergeKeepLastDropsLeftDuplicates() {
        View[] views = new View[]{
                new View("A", "leftVideo", LocalDateTime.MIN),
                new View("A", "rightVideo", LocalDateTime.MIN),
                new View("B", "rightVideo", LocalDateTime.MIN)
        };
        View[] work = new View[1];

        // Merge views[0..1) and views[1..3) using BY_USER_ID
        int k = merge(views, work, 0, 1, 1, 3, BY_USER_ID, KEEP_LAST);

        assertEquals(2, k); // Should result in 2 elements: A, B
        assertEquals("rightVideo", views[0].videoID()); // Kept the right 'A'
        assertEquals("B", views[1].userID());
    }

    @DisplayName("WHEN merging two arrays that are disjoint (not physically next to each other in the array), THEN it merges correctly.")
    @Test
    void testMergeNonContiguousRanges() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.MIN), // Left (0)
                new View("X", "vX", LocalDateTime.MIN), // Ignored garbage (1)
                new View("Y", "vY", LocalDateTime.MIN), // Ignored garbage (2)
                new View("B", "v2", LocalDateTime.MIN)  // Right (3)
        };
        View[] work = new View[1];

        // Merge views[0..1) and views[3..4)
        int k = merge(views, work, 0, 1, 3, 4, BY_USER_ID, KEEP_ALL);

        assertEquals(2, k);
        assertEquals("A", views[0].userID());
        assertEquals("B", views[1].userID());
    }

    @DisplayName("WHEN merging an empty left array with a right array, THEN the result is just the right array.")
    @Test
    void testMergeEmptyLeft() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.MIN),
                new View("B", "v2", LocalDateTime.MIN)
        };
        View[] work = new View[0];

        // Left is [0..0), Right is [0..2)
        int k = merge(views, work, 0, 0, 0, 2, BY_USER_ID, KEEP_ALL);

        assertEquals(2, k);
        assertEquals("A", views[0].userID());
        assertEquals("B", views[1].userID());
    }
    // =========================================================================
    // deduplicatingSort() Tests
    // =========================================================================

    @DisplayName("WHEN sorting an empty array, THEN it returns a new empty array.")
    @Test
    void testSortEmptyArray() {
        View[] views = new View[0];
        View[] sorted = deduplicatingSort(views, BY_USER_ID, KEEP_ALL);
        assertEquals(0, sorted.length);
        assertNotSame(views, sorted); // Must be a new array copy
    }

    @DisplayName("WHEN sorting an array with a single element, THEN it returns a new array with that element.")
    @Test
    void testSortSizeOne() {
        View[] views = new View[]{new View("A", "V", LocalDateTime.MIN)};
        View[] sorted = deduplicatingSort(views, BY_USER_ID, KEEP_ALL);
        assertEquals(1, sorted.length);
        assertEquals("A", sorted[0].userID());
        assertNotSame(views, sorted);
    }

    @DisplayName("WHEN sorting an array of identical elements with KEEP_FIRST, THEN it returns an array of size 1 containing the original first element.")
    @Test
    void testSortAllDuplicatesKeepFirst() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.MIN),
                new View("A", "v2", LocalDateTime.MIN),
                new View("A", "v3", LocalDateTime.MIN),
                new View("A", "v4", LocalDateTime.MIN)
        };

        View[] sorted = deduplicatingSort(views, BY_USER_ID, KEEP_FIRST);

        assertEquals(1, sorted.length);
        assertEquals("v1", sorted[0].videoID()); // The very first one should survive
    }

    @DisplayName("WHEN sorting an array of identical elements with KEEP_LAST, THEN it returns an array of size 1 containing the original last element.")
    @Test
    void testSortAllDuplicatesKeepLast() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.MIN),
                new View("A", "v2", LocalDateTime.MIN),
                new View("A", "v3", LocalDateTime.MIN),
                new View("A", "v4", LocalDateTime.MIN)
        };

        View[] sorted = deduplicatingSort(views, BY_USER_ID, KEEP_LAST);

        assertEquals(1, sorted.length);
        assertEquals("v4", sorted[0].videoID()); // The very last one should survive
    }

    @DisplayName("WHEN sorting an array in strictly reverse order, THEN it correctly sorts it ascending.")
    @Test
    void testSortReverseOrder() {
        View[] views = new View[]{
                new View("D", "v1", LocalDateTime.MIN),
                new View("C", "v2", LocalDateTime.MIN),
                new View("B", "v3", LocalDateTime.MIN),
                new View("A", "v4", LocalDateTime.MIN)
        };

        View[] sorted = deduplicatingSort(views, BY_USER_ID, KEEP_ALL);

        assertEquals(4, sorted.length);
        assertEquals("A", sorted[0].userID());
        assertEquals("B", sorted[1].userID());
        assertEquals("C", sorted[2].userID());
        assertEquals("D", sorted[3].userID());
    }

}
