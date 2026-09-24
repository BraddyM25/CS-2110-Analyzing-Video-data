package cs2110;

import static cs2110.DataUtilities.*;
import static cs2110.DataUtilities.SearchPolicy.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BinarySearchTest {

    @DisplayName("when the target shows up multiple times in the middle of the array, "
            + "then LEFT policy returns the index of the very first one.")
    @Test
    public void testBinarySearchLeftPolicyMultipleMatches() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.MIN),
                new View("B", "v1", LocalDateTime.MIN),
                new View("B", "v1", LocalDateTime.MIN),
                new View("B", "v1", LocalDateTime.MIN),
                new View("C", "v1", LocalDateTime.MIN)
        };
        View key = new View("B", "v99", LocalDateTime.MAX);

        assertEquals(1, binarySearch(views, key, BY_USER_ID, LEFT));
    }

    @DisplayName("when the array is completely empty, "
            + "then binarySearch just returns 0 for both policies so it doesn't break.")
    @Test
    public void testBinarySearchEmptyArray() {
        View[] views = new View[0];
        View key = new View("A", "v1", LocalDateTime.MIN);

        assertEquals(0, binarySearch(views, key, BY_USER_ID, LEFT));
        assertEquals(0, binarySearch(views, key, BY_USER_ID, RIGHT));
    }

    @DisplayName("when the target shows up multiple times in the array, "
            + "then RIGHT policy returns the index that is right after the last match.")
    @Test
    public void testBinarySearchRightPolicyMultipleMatches() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.MIN),
                new View("B", "v1", LocalDateTime.MIN),
                new View("B", "v1", LocalDateTime.MIN),
                new View("B", "v1", LocalDateTime.MIN),
                new View("C", "v1", LocalDateTime.MIN)
        };
        View key = new View("B", "v99", LocalDateTime.MAX);

        assertEquals(4, binarySearch(views, key, BY_USER_ID, RIGHT));
    }

    @DisplayName("when the target is smaller than everything else in the array, "
            + "then both policies return index 0.")
    @Test
    public void testBinarySearchTargetSmallerThanAll() {
        View[] views = new View[]{
                new View("B", "v1", LocalDateTime.MIN),
                new View("C", "v2", LocalDateTime.MIN),
                new View("D", "v3", LocalDateTime.MIN)
        };
        View key = new View("A", "v99", LocalDateTime.MAX);

        assertEquals(0, binarySearch(views, key, BY_USER_ID, LEFT));
        assertEquals(0, binarySearch(views, key, BY_USER_ID, RIGHT));
    }

    @DisplayName("when the target is bigger than all the elements in the array, "
            + "THEN both policies just return the length of the array.")
    @Test
    public void testBinarySearchTargetLargerThanAll() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.MIN),
                new View("B", "v2", LocalDateTime.MIN),
                new View("C", "v3", LocalDateTime.MIN)
        };
        View key = new View("D", "v99", LocalDateTime.MAX);

        assertEquals(3, binarySearch(views, key, BY_USER_ID, LEFT));
        assertEquals(3, binarySearch(views, key, BY_USER_ID, RIGHT));
    }

    @DisplayName("when the target isn't actually in the array but it belongs somewhere in "
            + "the middle, THEN both policies still return the right insertion index.")
    @Test
    public void testBinarySearchTargetMissingInMiddle() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.MIN),
                new View("B", "v2", LocalDateTime.MIN),
                new View("D", "v3", LocalDateTime.MIN),
                new View("E", "v4", LocalDateTime.MIN)
        };

        View key = new View("C", "v99", LocalDateTime.MAX);

        assertEquals(2, binarySearch(views, key, BY_USER_ID, LEFT));
        assertEquals(2, binarySearch(views, key, BY_USER_ID, RIGHT));
    }

    @DisplayName("when the array only has a size of 1, THEN binary search correctly figures out "
            + "all the boundaries without throwing an error.")
    @Test
    public void testBinarySearchSizeOneArray() {
        View[] views = new View[]{
                new View("B", "v1", LocalDateTime.MIN)
        };

        View smallerKey = new View("A", "v1", LocalDateTime.MIN);
        View exactKey = new View("B", "v1", LocalDateTime.MIN);
        View largerKey = new View("C", "v1", LocalDateTime.MIN);

        assertEquals(0, binarySearch(views, smallerKey, BY_USER_ID, LEFT));
        assertEquals(0, binarySearch(views, exactKey, BY_USER_ID, LEFT));
        assertEquals(1, binarySearch(views, exactKey, BY_USER_ID, RIGHT));
        assertEquals(1, binarySearch(views, largerKey, BY_USER_ID, RIGHT));
    }

    @DisplayName("when the array has a bunch of duplicate targets right at the beginning, "
            + "then it correctly returns 0 for LEFT and skips past them for RIGHT.")
    @Test
    public void testDuplicatesAtBeginning() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.MIN),
                new View("A", "v2", LocalDateTime.MIN),
                new View("A", "v3", LocalDateTime.MIN),
                new View("B", "v4", LocalDateTime.MIN)
        };

        View key = new View("A", "v99", LocalDateTime.MAX);

        assertEquals(0, binarySearch(views, key, BY_USER_ID, LEFT));
        assertEquals(3, binarySearch(views, key, BY_USER_ID, RIGHT));
    }

    @DisplayName("when the duplicate targets are piled up at the very end of the array, "
            + "then the policy LEFT finds the first one and RIGHT returns the array length.")
    @Test
    public void testDuplicatesAtEnd() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.MIN),
                new View("B", "v2", LocalDateTime.MIN),
                new View("B", "v3", LocalDateTime.MIN),
                new View("B", "v4", LocalDateTime.MIN)
        };

        View key = new View("B", "v99", LocalDateTime.MAX);

        assertEquals(1, binarySearch(views, key, BY_USER_ID, LEFT));
        assertEquals(4, binarySearch(views, key, BY_USER_ID, RIGHT));
    }

    @DisplayName("when the whole array is just the exact same element repeating, "
            + "then the LEFT policy returns 0 and RIGHT returns the length of the array.")
    @Test
    public void testAllIdenticalElements() {
        View[] views = new View[]{
                new View("A", "v1", LocalDateTime.MIN),
                new View("A", "v2", LocalDateTime.MIN),
                new View("A", "v3", LocalDateTime.MIN),
                new View("A", "v4", LocalDateTime.MIN)
        };

        View key = new View("A", "v99", LocalDateTime.MAX);

        assertEquals(0, binarySearch(views, key, BY_USER_ID, LEFT));
        assertEquals(4, binarySearch(views, key, BY_USER_ID, RIGHT));
    }
}
