package Kernel.Respository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.sql.PreparedStatement;


public class CustomPreparedStatement {

    Connection connection;
    String tableName;

    public CustomPreparedStatement(Connection connection, String tableName) {
        this.tableName = tableName;
        this.connection = connection;
    }

    public java.sql.PreparedStatement prepareInsertStatement(Insert insert) throws SQLException {
        java.sql.PreparedStatement preparedStatement = connection.prepareStatement(insert.query(), PreparedStatement.RETURN_GENERATED_KEYS);

        System.out.println(insert.query());

        List<Object> placeholders = insert.getColumnsWithPlaceHolderInsert();
        if (placeholders != null) {
            for (int i = 0; i < placeholders.size(); i++) {
                System.out.println((i + 1) + " -> " + placeholders.get(i));
                preparedStatement.setObject(i + 1, placeholders.get(i));
            }
        }
        /* Hacer la ejecucion aunque no tenga placeholders xd */
        

        return preparedStatement;
    }
    
}
