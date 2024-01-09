package controller;

import com.toedter.calendar.JCalendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarioController2 extends JFrame {

    public void calendario(JTextField txt) {
        JCalendar calendar = new JCalendar();
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);

        Date selectedDate = calendar.getDate();
        Date selectedTime = (Date)timeSpinner.getValue();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(selectedDate);
        Calendar timeCalendar = Calendar.getInstance();

        timeCalendar.setTime(selectedTime);

        calendar1.set(11, timeCalendar.get(11));
        calendar1.set(12, timeCalendar.get(12));

        System.out.println(calendar1.getTime());
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.add(calendar, "North");
        panel1.add(timeSpinner, "South");

        JButton guardar_fecha = new JButton("Guardar Fecha");
        guardar_fecha.setPreferredSize(new Dimension(20, 40));

        guardar_fecha.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // El código que se ejecutará cuando se haga clic en el botón
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String fechaFormateada = sdf.format(calendar.getDate());

                SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
                String horaFormateada = sdfHora.format(timeSpinner.getValue());



                txt.setText(fechaFormateada+" "+ horaFormateada);
                JOptionPane.showMessageDialog(null, "¡Seleccionaste la hora!");
            }
        });


        panel1.add(guardar_fecha);
        this.setTitle("Soporte");

        this.add(panel1);
        this.setDefaultCloseOperation(2);
        this.pack();
        this.setVisible(true);
    }
}
