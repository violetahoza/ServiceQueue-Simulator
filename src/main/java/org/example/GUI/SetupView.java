package org.example.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.example.Logic.SimulationManager;

import static org.example.Logic.SimulationManager.*;

public class SetupView extends JFrame implements ActionListener {
    private JLabel clientsLabel = new JLabel("Insert the number of clients: ");
    private JLabel queueLabel = new JLabel("Insert the number of queues: ");
    private JLabel simLabel = new JLabel("Insert the maximum time of the simulation: ");
    private JLabel minArriv = new JLabel("Insert the minimum arrival time: ");
    private JLabel minServ = new JLabel("Insert the minimum service time: ");
    private JLabel maxArriv = new JLabel("Insert the maximum arrival time: ");
    private JLabel maxServ = new JLabel("Insert the maximum service time: ");
    private JLabel strategyLabel = new JLabel("Strategy");
    public JTextField clientstf = new JTextField(), queuetf = new JTextField(), tmaxtf = new JTextField(), tminArrival = new JTextField(), tmaxArrival = new JTextField(), tminService = new JTextField(), tmaxService = new JTextField();
    private JButton submit = new JButton("Submit");
    private JComboBox strategyBox;

    public SetupView(String name){
       super(name);
       this.prepareGUI();
    }

    public void prepareGUI()
    {
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(220, 200, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        panel.setLayout(new GridLayout(9, 2, 5, 5));
        this.add(panel);

        addRow(panel, clientsLabel, clientstf);
        addRow(panel, queueLabel, queuetf);
        addRow(panel, simLabel, tmaxtf);
        addRow(panel, minArriv, tminArrival);
        addRow(panel, maxArriv, tmaxArrival);
        addRow(panel, minServ, tminService);
        addRow(panel, maxServ, tmaxService);
        customizeLabel(strategyLabel);

        String[] options = {String.valueOf(Strategy.SHORTEST_QUEUE), String.valueOf(Strategy.SHORTEST_TIME)};
        strategyBox = new JComboBox<>(options);
        strategyBox.setFont(new Font("Arial", Font.BOLD, 16));
        strategyBox.setForeground(new Color(128, 0, 128));
        strategyBox.setBackground(new Color(247, 216, 247));
        panel.add(strategyLabel);
        panel.add(strategyBox);

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
    private void getStrategy() {
        String selectedStrategy = (String) strategyBox.getSelectedItem();
        if (selectedStrategy != null) {
            switch (selectedStrategy) {
                case "SHORTEST_TIME":
                    strategy = Strategy.SHORTEST_TIME;
                    break;
                case "SHORTEST_QUEUE":
                    strategy = Strategy.SHORTEST_QUEUE;
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        boolean ok = true;
        if(e.getSource() == submit)
        {
            String c = clientstf.getText(), q = queuetf.getText(), tmax = tmaxtf.getText();
            String arrivmin = tminArrival.getText(), arrivmax = tmaxArrival.getText();
            String servicemax = tmaxService.getText(), servicemin = tminService.getText();
            getStrategy();
            // validate the inputs
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
                clientsNr.set(Integer.parseInt(c));
                queuesNr.set(Integer.parseInt(q));
                simulationTime.set(Integer.parseInt(tmax));
                maxArrival.set(Integer.parseInt(arrivmax));
                minArrival.set(Integer.parseInt(arrivmin));
                maxService.set(Integer.parseInt(servicemax));
                minService.set(Integer.parseInt(servicemin));
            }
            if(simulationTime.get() < 0 || maxService.get() < 0 || maxArrival.get() < 0 || minArrival.get() < 0 || minService.get() < 0 || queuesNr.get() < 0 || clientsNr.get() < 0)
            {
                ok = false;
                showErrorDialog("All the values must be positive!");
                throw new IllegalArgumentException("All the values must be positive!");
            }
            if(maxArrival.get() > simulationTime.get()){
                ok = false;
                showErrorDialog("Max arrival time can't be greater than max simulation time!");
                throw new IllegalArgumentException("Max arrival time can't be greater than max simulation time!");
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
            if(ok == true) { // if the inputs are valid, start the simulation
                System.out.println("the inputs are ok");
                Thread simulationControl = new Thread(new SimulationManager(new SimulationView("Queue management simulation")));
                this.dispose();
                simulationControl.start();
            }
        }
    }
    public static void showErrorDialog(String message) {
        Font font = new Font("Times New Roman", Font.BOLD, 14);
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.messageForeground", Color.RED);
        JOptionPane.showMessageDialog(null, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
    public static void showMessageDialog(String message, String title) {
        Font font = new Font("Times New Roman", Font.BOLD, 14);
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.messageForeground", Color.RED);
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
