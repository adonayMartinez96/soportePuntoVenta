package Kernel.utils;

import java.util.List;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class StringSimilarityFinder {
    public static String findMostSimilarString(String input, List<String> strings) {
        String mostSimilar = null;
        int minDistance = Integer.MAX_VALUE;

        for (String candidate : strings) {
            int distance = LevenshteinDistance.getDefaultInstance().apply(
                StringSimilarityFinder.normalizeString(input), 
                StringSimilarityFinder.normalizeString(candidate));
            if (distance < minDistance) {
                minDistance = distance;
                mostSimilar = candidate;
            }
        }

        return mostSimilar;
    }


    public static String normalizeString(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }
}

