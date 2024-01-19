package decoder.core;

import com.google.common.collect.Sets;
import org.apache.commons.text.similarity.JaroWinklerDistance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductMatcher {

    public static String findBestMatch(String input, List<String> productNames) {
        String bestMatch = "";
        double maxOverallSimilarity = 0.0;

        for (String productName : productNames) {
            double overallSimilarity = calculateOverallSimilarity(input, productName);
            if (overallSimilarity > maxOverallSimilarity) {
                maxOverallSimilarity = overallSimilarity;
                bestMatch = productName;
            }
        }

        return bestMatch;
    }

    public static double calculateOverallSimilarity(String input, String productName) {
        JaroWinklerDistance jaroWinkler = new JaroWinklerDistance();
        Set<String> inputTokens = tokenizeAndNormalize(input);
        Set<String> productTokens = tokenizeAndNormalize(productName);
        double jaroWinklerSimilarity = jaroWinkler.apply(productName, input);
        double jaccardSimilarity = calculateJaccardSimilarity(inputTokens, productTokens);

        // ponderacion de las similitudes (se puede ajustar si es necesario)
        double weightedSimilarity = 0.7 * jaroWinklerSimilarity + 0.3 * jaccardSimilarity;

        return weightedSimilarity;
    }

    private static Set<String> tokenizeAndNormalize(String input) {
        return new HashSet<>(Arrays.asList(input.toLowerCase().split("\\s+")));
    }

    private static double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        Sets.SetView<String> intersection = Sets.intersection(set1, set2);
        int intersectionSize = intersection.size();
        int unionSize = set1.size() + set2.size() - intersectionSize;

        return (double) intersectionSize / unionSize;
    }


}
