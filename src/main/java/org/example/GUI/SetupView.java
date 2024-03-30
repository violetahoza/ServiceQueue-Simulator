package org.example.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.commons.lang3.math.NumberUtils;
import org.example.Logic.SimulationManager;
import org.example.Model.LogEvents;
import org.example.Model.LogEvents.*;
import static org.example.Model.LogEvents.*;

public class SetupView extends JFrame implements ActionListener {
    JLabel clientsLabel = new JLabel("Insert the number of clients: ");
    JLabel queueLabel = new JLabel("Insert the number of queues: ");
    JLabel simLabel = new JLabel("Insert the maximum time of the simulation: ");
    JLabel minArriv = new JLabel("Insert the minimum arrival time: ");
    JLabel minServ = new JLabel("Insert the minimum service time: ");
    JLabel maxArriv = new JLabel("Insert the maximum arrival time: ");
    JLabel maxServ = new JLabel("Insert the maximum service time: ");
    public JTextField clientstf = new JTextField(), queuetf = new JTextField(), tmaxtf = new JTextField(), tminArrival = new JTextField(), tmaxArrival = new JTextField(), tminService = new JTextField(), tmaxService = new JTextField();
    JButton submit = new JButton("Submit");

    public SetupView(String name){
       super(name);
       this.prepareGUI();
    }

    public void prepareGUI()
    {
        this.setSize(700, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(220, 200, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        panel.setLayout(new GridLayout(8, 2, 5, 5));
        this.add(panel);

        addRow(panel, clientsLabel, clientstf);
        addRow(panel, queueLabel, queuetf);
        addRow(panel, simLabel, tmaxtf);
        addRow(panel, minArriv, tminArrival);
        addRow(panel, maxArriv, tmaxArrival);
        addRow(panel, minServ, tminService);
        addRow(panel, maxServ, tmaxService);

        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitPanel.setBackground(new Color(220, 200, 250));
        customizeButton(submit);
        submitPanel.add(submit);
        panel.add(submitPanel);
    }
    private void customizeButton(JButton button) {
        button.addActionListener(this);
        button.setForeground(new Color(128, 0, 128));
        button.setBackground(new Color(247, 216, 247));
        button.setFont(new Font("Serif", Font.BOLD, 14));
    }
    private void customizeLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(128, 0, 128));
    }
    private void customizeTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.BOLD, 16));
        textField.setForeground(new Color(128, 0, 128));
        textField.setBackground(new Color(247, 216, 247));
    }
    private void addRow(JPanel panel, JLabel label, JTextField textField) {
        customizeLabel(label);
        customizeTextField(textField);
        panel.add(label);
        panel.add(textField);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean ok = true;
        if(e.getSource() == submit)
        {
            String c = clientstf.getText(), q = queuetf.getText(), tmax = tmaxtf.getText();
            String arrivmin = tminArrival.getText(), arrivmax = tmaxArrival.getText();
            String servicemax = tmaxService.getText(), servicemin = tminService.getText();

            if(c.isEmpty() || q.isEmpty() || tmax.isEmpty() || arrivmax.isEmpty() || arrivmin.isEmpty() || servicemax.isEmpty() || servicemin.isEmpty())
            {
                System.out.println("Please complete all the fields!");
                showErrorDialog("Please complete all the fields!");
                ok = false;
            }
            if(!NumberUtils.isNumber(c) || !NumberUtils.isNumber(q) || !NumberUtils.isNumber(tmax) || !NumberUtils.isNumber(arrivmax) || !NumberUtils.isNumber(arrivmin) || !NumberUtils.isNumber(servicemax) || !NumberUtils.isNumber(servicemin))
            {
                ok = false;
                showErrorDialog("All the inputs must be numbers!");
                throw new IllegalArgumentException("All the inputs must be numbers!");
            }
            else{
                clientsNr.set(Integer.parseInt(clientstf.getText()));
                queuesNr.set(Integer.parseInt(queuetf.getText()));
                tMax.set(Integer.parseInt(tmaxtf.getText()));
                maxArrival.set(Integer.parseInt(tmaxArrival.getText()));
                minArrival.set(Integer.parseInt(tminArrival.getText()));
                maxService.set(Integer.parseInt(tmaxService.getText()));
                minService.set(Integer.parseInt(tminService.getText()));
            }
            if(tMax.get() < 0 || maxService.get() < 0 || maxArrival.get() < 0 || minArrival.get() < 0 || minService.get() < 0 || queuesNr.get() < 0 || clientsNr.get() < 0)
            {
                ok = false;
                showErrorDialog("All the values must be positive!");
                throw new IllegalArgumentException("All the values must be positive!");
            }
            if(maxArrival.get() < minArrival.get()){
                ok = false;
                showErrorDialog("Min arrival cannot be greater than max arrival!");
                throw new IllegalArgumentException("Min arrival cannot be greater than max arrival!");
            }
            if(maxService.get() < minService.get()){
                ok = false;
                showErrorDialog("Min service cannot be greater than max service!");
                throw new IllegalArgumentException("Min service cannot be greater than max service!");
            }
            if(maxArrival.get() > tMax.get()){
                ok = false;
                showErrorDialog("Max arrival time can't be greater than max simulation time!");
                throw new IllegalArgumentException("Max arrival time can't be greater than max simulation time!");
            }
            if(ok == true) {
                System.out.println("the inputs are ok");
                //Thread simulationControl = new Thread(new SimulationManager(new SimulationView("Queue management simulation")));
                //this.dispose();
                //simulationControl.start();
            }
        }
    }
    public static void showErrorDialog(String message) {
        Font font = new Font("Times New Roman", Font.BOLD, 14);
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.messageForeground", Color.RED);
        JOptionPane.showMessageDialog(null, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}
