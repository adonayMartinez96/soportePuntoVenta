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

                if (extractedValue instanceof List<?>) {
                    // Si es una lista, agregamos todos los elementos a la lista resultante
                    for (Object nestedValue : (List<?>) extractedValue) {
                        if (targetType.isInstance(nestedValue)) {
                            extractedValues.add((T) nestedValue);
                        }
                    }
                } else {
                    // Si no es una lista, simplemente lo agregamos a la lista resultante
                    if (targetType.isInstance(extractedValue)) {
                        extractedValues.add((T) extractedValue);
                    }
                }
            }
        }

        return extractedValues;
    }
}
