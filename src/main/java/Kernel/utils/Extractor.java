package Kernel.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Extractor {

    @SuppressWarnings("unchecked")
    public static <T> List<T> extract(List<Map<String, Object>> resultsList, String key, Class<?> targetType) {
        List<T> extractedValues = new ArrayList<>();

        for (Map<String, Object> result : resultsList) {
            if (result.containsKey(key)) {
                Object extractedValue = result.get(key);
                if (targetType.isInstance(extractedValue)) {
                    extractedValues.add((T) extractedValue);
                }
            }
        }

        return extractedValues;
    }
}
