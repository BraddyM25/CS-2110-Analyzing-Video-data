package cs2110;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;

import static cs2110.DataUtilities.SearchPolicy.*;
import static cs2110.DataUtilities.DedupPolicy.*;

/**
 * Utilities for deduplicating, sorting, and searching `View` array data.
 */
public class DataUtilities {

    /* ***************************************************************************************
     * Define the record, comparators, and policy enums used to store and process View data  *
     *****************************************************************************************/

    /**
     * Models a single view by the individual with the given `userID` of the video with the given
     * `videoID` at the given `timestamp`.
     */
    record View(String userID, String videoID, LocalDateTime timestamp) {

    }

    /**
     * Indicates which index of `arr` should be returned during a binary search for `key` under a
     * given Comparator `cmp`:
     * <p> LEFT : Return the index `i` such that `arr[..i)` are all deemed less than `key`
     * by `cmp` and `arr[i..]` are all deemed equivalent to or greater than `key`.
     * <p> RIGHT : Return the index `i` such that `arr[..i)` are all deemed less than or
     * equivalent to `key` and `arr[i..]` are all deemed greater than `key`.
     */
    enum SearchPolicy {LEFT, RIGHT}

    /**
     * Indicates how equivalent (per the given Comparator) entries are handled during sorting:
     * <p> KEEP_ALL : Preserve all entries; the relative order of equivalent entries is preserved.
     * <p> KEEP_FIRST : Preserve only the first (in the original order) occurrence of each set of
     * equivalent entries.
     * <p> KEEP_LAST : Preserve only the last (in the original order) occurrence of each set of
     * equivalent entries.
     */
    enum DedupPolicy {KEEP_ALL, KEEP_FIRST, KEEP_LAST}

    /**
     * A Comparator object that is used to compare Views by `timestamp`. The
     * `BY_TIMESTAMP.compare()` method guarantees O(1) worst-case runtime and space complexities.
     */
    static final Comparator<View> BY_TIMESTAMP = Comparator.comparing(View::timestamp);

    /**
     * A Comparator object that is used to compare Views by `userID`. The `BY_USER_ID.compare()`
     * method guarantees O(1) worst-case runtime and space complexities.
     */
    static final Comparator<View> BY_USER_ID = Comparator.comparing(View::userID);

    /**
     * A Comparator object that is used to compare Views by `videoID`. The `BY_VIDEO_ID.compare()`
     * method guarantees O(1) worst-case runtime and space complexities.
     */
    static final Comparator<View> BY_VIDEO_ID = Comparator.comparing(View::videoID);

    /* ***************************************************************************************
     * Data processing methods                                                               *
     *****************************************************************************************/

    /**
     * Performs a binary search on the given `views` array for the given `key`. Returns the index
     * `i` with `0 <= i <= views.length` consistent with the given SearchPolicy `policy` using the
     * given Comparator `cmp`. No modifications are made to the array `views` as a result of this
     * method. Requires that `views` is sorted according to `cmp`.
     */
    static int binarySearch(View[] views, View key, Comparator<View> cmp, SearchPolicy policy) {
        // TODO 1: Implement this method according to its specifications. Your implementation must
        //  be recursive, include no loops, and have O(log N) worst-case runtime and space
        //  complexities, where N = `views.length`. Consider delegating work to a helper method.
        return binaryHelper(views, key, cmp, policy, 0, views.length);
    }

    private static int binaryHelper(View[] views, View key, Comparator<View> cmp,
            SearchPolicy policy, int lowBound, int highBound) {
        // this is the base case to determine if as we recurse through the mid point
        // and target value the lowBound gets closer to the highBound of the array
        // as we make sub arrays.
        if (lowBound >= highBound) {
            return lowBound;
        }

        int mid = (lowBound + highBound) / 2;

        // compares the target to the mid point if it is less than then we return a sub string
        // to the left of the mid point. If it is greater than we return a sub array
        // to the right of the mid point.
        int comp = cmp.compare(key, views[mid]);

        if (comp > 0) {
            return binaryHelper(views, key, cmp, policy, mid + 1, highBound);

        } else if (comp < 0) {
            return binaryHelper(views, key, cmp, policy, lowBound, mid);

        } else {
            // this is the case where the mid point is equal to the target value.
            // in this case if the policy is to the left and we want to find the first occurrence
            // we return the left side of the sub array.
            if (policy == SearchPolicy.LEFT) {
                return binaryHelper(views, key, cmp, policy, lowBound, mid);
            } else {
                return binaryHelper(views, key, cmp, policy, mid + 1, highBound);
            }
        }
    }

    /**
     * Returns a reference to a *new* array that is a copy of the range `views[begin..end)`. The
     * length of the returned array is exactly `end - begin`. No modifications are made to the
     * `views` array as a result of this method. Requires `0 <= begin <= end <= views.length`. This
     * method guarantees O(`end - begin`) worst-case runtime and space complexities.
     */
    static View[] copyOfRange(View[] views, int begin, int end) {
        return Arrays.copyOfRange(views, begin, end);
    }

    /**
     * Returns a reference to *new* array comprising the sorted (and possibly deduplicated) entries
     * of the given `views` array. No modifications are made to the `views` array as a result of
     * this method. The entries of the returned array are sorted in ascending order (according to
     * the given Comparator `cmp`) and deduplicated (according to the given DedupPolicy `policy`).
     * The length of the returned array is chosen to exactly store its contents with no trailing
     * empty entries.
     */
    static View[] deduplicatingSort(View[] views, Comparator<View> cmp, DedupPolicy policy) {
        // TODO 4a: Call dedupMergeSortRecursive(), passing in a copy of the `views` array. Use its
        //  return value to obtain the return value for this method.

        View[] work = copyOfRange(views, 0, views.length);

        // empty array to work on that will be changed and used to merge
        View[] emptyArr = new View[views.length];

        int finalBound = dedupMergeSortRecursive(work, emptyArr, 0, views.length, cmp, policy);
        return copyOfRange(work, 0, finalBound);

    }

    /**
     * Uses the merge sort algorithm to recursively sort `views[begin..end)` in ascending order
     * (according to the given Comparator `cmp`) and deduplicate these entries according to the
     * given DedupPolicy `policy`. Stores the sorted (and possibly deduplicated) data in
     * `views[begin..k)` and returns `k`. No entries of `views` outside `views[begin..end)` are
     * modified as a result of this method. Requires that `work.length > (end - begin) / 2`, and `0
     * <= begin <= end <= views.length`. This method uses the `work` array to guarantee an
     * O(log(`end - begin`)) space complexity.
     */
    static int dedupMergeSortRecursive(View[] views, View[] work, int begin, int end,
            Comparator<View> cmp, DedupPolicy policy) {
        // TODO 4b: Implement recursive merge sort.
        int distance = end - begin;
        // base case if the range for boundaries is 1 or 0
        // just return the end boundary index.
        if (distance == 1 || distance == 0) {
            return end;
        }
        // finding midpoint of the current sub array we are on.
        int midIndex = (begin + end) / 2;

        int leftHalf = dedupMergeSortRecursive(views, work, begin, midIndex, cmp, policy);
        int rightHalf = dedupMergeSortRecursive(views, work, midIndex, end, cmp, policy);

        return merge(views, work, begin, leftHalf, midIndex, rightHalf, cmp, policy);


    }

    /**
     * Merges the sorted/deduplicated ranges `views[leftBegin..leftEnd)` and
     * `views[rightBegin..rightEnd)` in ascending order (according to the given Comparator `cmp`),
     * applying the given DedupPolicy `policy`. Stores the merged (and possibly deduplicated) data
     * in `views[leftBegin..k)` and returns `k`. No entries of `views` outside `views[leftBegin..k)`
     * are modified as a result of this method. Requires:
     * <p> `work.length >= leftEnd - leftBegin`
     * <p> `0 <= leftBegin < leftEnd <= rightBegin < rightEnd <= views.length`
     * <p> `views[leftBegin..leftEnd)` is sorted/deduplicated according to given `cmp`/`policy`
     * <p> `views[rightBegin..rightEnd)` is sorted/deduplicated according to given `cmp`/policy`
     */
    @SuppressWarnings("SameParameterValue")
    static int merge(View[] views, View[] work, int leftBegin, int leftEnd,
            int rightBegin, int rightEnd, Comparator<View> cmp, DedupPolicy policy) {
        // TODO 3: Implement this method according to its specifications.

        int leftLen = leftEnd - leftBegin;

        for (int iChange = 0; iChange < leftLen; iChange++) {
            work[iChange] = views[leftBegin + iChange];
        }

        int i = 0;
        int j = rightBegin;
        int k = leftBegin;

        while (i < leftLen && j < rightEnd) {
            int comp = cmp.compare(work[i], views[j]);
            if (comp < 0) {
                views[k++] = work[i++];
            } else if (comp > 0) {
                views[k++] = views[j++];
            } else {
                if (policy == DedupPolicy.KEEP_FIRST) {
                    views[k++] = work[i++];
                    j++;
                } else if (policy == DedupPolicy.KEEP_LAST) {
                    views[k++] = views[j++];
                    i++;
                } else {
                    views[k++] = work[i++];
                }
            }
        }

        while (i < leftLen) {
            views[k++] = work[i++];
        }
        while (j < rightEnd) {
            views[k++] = views[j++];
        }

        return k;
    }
}