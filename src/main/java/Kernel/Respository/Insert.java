package Kernel.Respository;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Insert {

    private String tableName;
    private LinkedHashMap<String, Object> columns;
    private List<Boolean> isDirectsInsertes;

    public Insert(String tableName) {
        this.tableName = tableName;
        this.columns = new LinkedHashMap<>();
        this.isDirectsInsertes = new ArrayList<>();
    }

    public void setColumn(String columnName, Object value) {
        this.setColumn(columnName, value, false);
    }

    public void setColumn(String columnName, Object value, boolean isNotDirectInsert) {
        this.columns.put(columnName, value);
        /* Se que esto es un desmadre pero XD, ahorita solo busco que funcione la verdad xd */
        this.isDirectsInsertes.add(!isNotDirectInsert);
    }

    public LinkedHashMap<String, Object> getColumns(){
        return this.columns;
    }

    
    public List<Object> getColumnsWithPlaceHolderInsert() {
        List<Object> result = new ArrayList<>();
        int count = 0; 
        for (Map.Entry<String, Object> entry : this.columns.entrySet()) {
            if (!this.isDirectsInsertes.get(count)) {
               result.add(entry.getValue());
            }
            count++;
        }
        return result;
    }
    

    public String query() {
        StringBuilder insertQuery = new StringBuilder("INSERT INTO ");
        insertQuery.append(tableName).append(" (");
    
        List<String> columnNames = new ArrayList<>(columns.keySet());
        StringBuilder columnsPart = new StringBuilder();
        for (String columnName : columnNames) {
            columnsPart.append(columnName).append(", ");
        }
        // Eliminar la coma y el espacio al final
        columnsPart.setLength(columnsPart.length() - 2);
        columnsPart.append(")");
    
        insertQuery.append(columnsPart.toString());
    
        insertQuery.append(" VALUES (");
        int count = 0;
        boolean firstValue = true;
        StringBuilder valuesPart = new StringBuilder();
        for (Map.Entry<String, Object> entry : this.columns.entrySet()) {
            Object value = entry.getValue();
            if (this.isDirectsInsertes.get(count)) {
                // Si es una inserción directa, agrega el valor directo
                if (!firstValue) {
                    valuesPart.append(", ");
                }
                valuesPart.append(getFormattedValue(value));  // Formatea el valor según su tipo
                firstValue = false;
            } else {
                // Si es un parámetro, agrega un marcador de posición o cadena vacía
                if (!firstValue) {
                    valuesPart.append(", ");
                }
                valuesPart.append(value != null && !value.toString().isEmpty() ? "?" : "''");  // Si es null o cadena vacía, se inserta como cadena vacía
                firstValue = false;
            }
            count++;
        }
        
        valuesPart.append(")");
        
        
        
    
        insertQuery.append(valuesPart.toString());
    
        String query = insertQuery.toString();
        return query;
    }

    private String getFormattedValue(Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof Number) {
            return value.toString();
        } else if(value == null || value.toString().isEmpty()) {
            return "'" + value.toString() + "'";
        }else if(this.normalizeString(value.toString()).startsWith("date") || this.normalizeString(value.toString()).startsWith("now")){
            return value.toString();
        }else{
            return "'" + value.toString() + "'";
        }
    }
    

    public String normalizeString(String key) {
        return Normalizer.normalize(key, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase();
    }
    
}
