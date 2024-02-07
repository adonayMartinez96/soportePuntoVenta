package Kernel.components;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Components {


    private JFrame frame;

    public Components(JFrame frame, int width, int height){
        this.frame = frame;
        this.frame.setSize(width, height);
    }


    public void setTitle(String title){
        this.frame.setTitle(title);
    }

    public void setDefaultCloseOperation(int operation){
        this.frame.setDefaultCloseOperation(operation);
    }

    public void setLocationRelativeTo(Component c){
        this.frame.setLocationRelativeTo(c);
    }

    public void jPanel(GridLayout c){
        JPanel jpanel = new JPanel();
        jpanel.setLayout(c);
        this.frame.add(jpanel);
    }


    public void jTextField(String text){
        JTextField nameField = new JTextField(text);
    }
}
