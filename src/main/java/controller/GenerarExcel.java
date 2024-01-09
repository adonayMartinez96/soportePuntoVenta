package controller;

import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class GenerarExcel {

    public void outputeExcel(DefaultTableModel tableModel, String nombreArchivo){

        Date fechaActual = new Date();
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String fechaHoraFormateada = fechaHoraActual.format(formato);
        String nombre = nombreArchivo+fechaHoraFormateada+".xlsx";
        System.out.println(nombre);

        String rutaCompleta = Paths.get(nombre).toString();
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Datos");
            // Crear un estilo de fuente en negrita
            Font font = workbook.createFont();
            font.setBold(true);
            // Aplicar el estilo de fuente al estilo de celda
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(font);


            // Crear la fila de encabezados
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(tableModel.getColumnName(col));
                cell.setCellStyle(headerCellStyle);
            }

            // Llenar los datos
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Cell cell = dataRow.createCell(j);
                    Object value = tableModel.getValueAt(i, j);
                    cell.setCellValue((value != null) ? value.toString() : "-");
                }
            }

            // Guardar el archivo Excel
            try (FileOutputStream outputStream = new FileOutputStream(nombre)) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
