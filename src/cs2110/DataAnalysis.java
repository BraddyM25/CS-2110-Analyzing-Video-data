package cs2110;

import java.time.LocalDateTime;

import static cs2110.DataUtilities.*;
import static cs2110.DataUtilities.DedupPolicy.*;
import static cs2110.DataUtilities.SearchPolicy.*;

/**
 * Methods utilizing tools from `DataUtilities` to enable interesting queries on `View` array data.
 */
public class DataAnalysis {

    /**
     * Returns an array comprising the first view recorded for each video.
     */
    static View[] firstVideoViews(View[] views) {
        View[] byTime = deduplicatingSort(views, BY_TIMESTAMP, KEEP_ALL);
        return deduplicatingSort(byTime, BY_VIDEO_ID, KEEP_FIRST);
    }

    /**
     * Returns the total number of views that the video with the given `videoID` has had.
     */
    static int totalViews(View[] views, String videoID) {
        View[] byVidID = deduplicatingSort(views, BY_VIDEO_ID, KEEP_ALL);
        View key = new View(null, videoID,
                null); // can't search for videoID directly; stick in "dummy" View object
        int first = binarySearch(byVidID, key, BY_VIDEO_ID, LEFT);
        int last = binarySearch(byVidID, key, BY_VIDEO_ID, RIGHT);
        return last - first;
    }

    /**
     * Returns the number of distinct users who viewed at least one video at a timestamp `t` with
     * `start <= t <= end`.
     */
    @SuppressWarnings("SameParameterValue")
    static int countDistinctUsersInTimeInterval(View[] views, LocalDateTime start,
            LocalDateTime end) {
        // TODO 5: Implement this method according to its specifications. Your definition must use
        //  the `binarySearch()`, `copyOfRange()`, and/or `deduplicatingSort()` methods of the
        //  `DataUtilities` class to manipulate the array data. You may not directly access the
        //  array contents. Label each line of with its worst-case runtime complexity.
        View[] byTime = deduplicatingSort(views, BY_TIMESTAMP, KEEP_ALL); // O(N log N)

        View startKey = new View(null, null, start); // O(1)
        View endKey = new View(null, null, end); // O(1)

        int startIndex = binarySearch(byTime, startKey, BY_TIMESTAMP, LEFT); // O(log N)
        int endIndex = binarySearch(byTime, endKey, BY_TIMESTAMP, RIGHT); // O(log N)

        if (endIndex < startIndex) { // O(1)
            endIndex = startIndex; // O(1)
        }

        View[] inRange = copyOfRange(byTime, startIndex, endIndex); // O(N)
        View[] uniqueUsers = deduplicatingSort(inRange, BY_USER_ID, KEEP_FIRST); // O(N log N)

        return uniqueUsers.length; // O(1)
    }

    /**
     * Returns an array of length `k` containing the Views of the last `k` distinct videos that the
     * given `userID` has watched (in any order). If that video has been watched more than once by
     * the user, then the View corresponding to the latest watch is included. More formally (to
     * account for possible ties), this method returns an array of `k` Views such that (1) the user
     * of each View has the given `userID`, (2) the `videoID`s of these Views are distinct, and (3)
     * for each videoID `v1` in this array, if this user viewed `v2` strictly after `v1`, then a
     * view of `v2` will also be present in the array. If `userID` has viewed fewer than `k`
     * distinct videos, then a shorter array containing their latest View of each video is
     * returned.
     */
    @SuppressWarnings("SameParameterValue")
    static View[] lastKViewedByUser(View[] views, String userID, int k) {
        // TODO 6: Implement this method according to its specifications. Your definition must use
        //  the `binarySearch()`, `copyOfRange()`, and/or `deduplicatingSort()` methods of the
        //  `DataUtilities` class to manipulate the array data. You may not directly access the array
        //  contents. Label each line of your definition with its worst-case runtime complexity.
        View[] byUser = deduplicatingSort(views, BY_USER_ID, KEEP_ALL); // O(N log N)
        View userKey = new View(userID, null, null); // O(1)

        int firstUser = binarySearch(byUser, userKey, BY_USER_ID, LEFT); // O(log N)
        int lastUser = binarySearch(byUser, userKey, BY_USER_ID, RIGHT); // O(log N)
        View[] userViews = copyOfRange(byUser, firstUser, lastUser); // O(N)

        View[] userByTime = deduplicatingSort(userViews, BY_TIMESTAMP, KEEP_ALL); // O(N log N)
        View[] uniqueVideosLatest = deduplicatingSort(userByTime, BY_VIDEO_ID, KEEP_LAST); // O(N log N)
        View[] uniqueChronological = deduplicatingSort(uniqueVideosLatest, BY_TIMESTAMP, KEEP_ALL); // O(N log N)

        int resultLength = Math.min(k, uniqueChronological.length); // O(1)
        int startIndex = uniqueChronological.length - resultLength; // O(1)

        return copyOfRange(uniqueChronological, startIndex, uniqueChronological.length); // O(N)
    }

    /**
     * Returns the `userID` of an individual who has the most recorded views of the video with the
     * given `videoID` in the `views` array. Returns `null` if there are no recorded views for that
     * video. The contents of `views` are not modified by this method.
     */
    @SuppressWarnings("SameParameterValue")
    static String mostObsessedViewer(View[] views, String videoID) {
        // TODO 7: Implement this method according to its specifications. Make sure to add a comment
        //  documenting the invariant of each loop that you write. Your definition must have a
        //  worst-case runtime complexity of `O(N + M log M)`, where `N = views.length` and `M` is
        //  the number of entries of `views` with the given `videoID`.
        int matchCount = 0;
        // The loop invariant here is that matchCount equals the number of
        // views in views[0...iChange)
        // that match the key or target for the videoID.
        for (int iChange = 0; iChange < views.length; iChange++) {
            if (views[iChange].videoID().equals(videoID)) {
                matchCount++;
            }
        }

        if (matchCount == 0) {
            return null;
        }

        View[] filtered = new View[matchCount];
        int writeIchange = 0;

        // the loop invariant here is when filtered[0...writeIChange) contains
        // all the views[0...iChange) that match the videoID.
        for (int iChange = 0; iChange < views.length; iChange++) {
            if (views[iChange].videoID().equals(videoID)) {
                filtered[writeIchange++] = views[iChange];
            }
        }

        View[] sortedByUser = deduplicatingSort(filtered, BY_USER_ID, KEEP_ALL);

        String maxUser = sortedByUser[0].userID();
        int maxObsess = 1;

        String currentUser = sortedByUser[0].userID();
        int currentObsess = 1;
        // The invariant here is when the maxUser
        // holds the ID of the user with the longest occurrence in the array
        // sortedByUser[0..iChange).
        // maxObsess holds the length of that users occurrence.
        // currentUser holds the ID of the user at sortedByUser[iChange-1], and
        // currentObsess is their current running Occurrence.
        for (int iChange = 1; iChange < sortedByUser.length; iChange++) {
            if (sortedByUser[iChange].userID().equals(currentUser)) {
                currentObsess++;
            } else {
                if (currentObsess > maxObsess) {
                    maxObsess = currentObsess;
                    maxUser = currentUser;
                }
                currentUser = sortedByUser[iChange].userID();
                currentObsess = 1;
            }
        }

        if (currentObsess > maxObsess) {
            maxUser = currentUser;
        }

        return maxUser;
    }
}