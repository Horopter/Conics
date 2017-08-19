/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Horopter.Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * @author SantoshKumar
 */
public class ClientStub {

    String Username;
    String MachineID;
    int port;
    String RemoteIPAddress;
    String downloadLocation;
    JFrame frame;
    ImageIcon imageIcon;
    JPanel panel;
    JTextField downloadLocationField, NameField, portField, MachineIdField, ipAddressField;
    JButton openFilesButton,Connect;
    ClientGUI clientGUI;
    Thread clientGUIThread;

    public ClientStub() {
        Username = "Santosh";
        MachineID = "TuringMachine";
        port = 5027;
        RemoteIPAddress = "localhost";
        downloadLocation = "C:/Client/exam";
    }

    private void prepareGUI() {
        frame = new JFrame("Exam App Worker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 400));
        frame.setIconImage(new ImageIcon(ClientStub.class.getResource("exam.png")).getImage());
        frame.setResizable(false);
        panel = new JPanel();
        panel.setBackground(Color.CYAN);
        panel.setLayout(null);
        JLabel ClientName = new JLabel("Enter User name : ");
        ClientName.setBounds(20, 20, 200, 30);
        NameField = new JTextField();
        NameField.setBounds(250, 20, 300, 30);
        panel.add(ClientName);
        panel.add(NameField);

        JLabel MachineName = new JLabel("Enter Machine Id : ");
        MachineName.setBounds(20, 60, 200, 30);
        MachineIdField = new JTextField();
        MachineIdField.setBounds(250, 60, 300, 30);
        panel.add(MachineName);
        panel.add(MachineIdField);

        JLabel portNumber = new JLabel("Enter port number : ");
        portNumber.setBounds(20, 100, 200, 30);
        portField = new JTextField();
        portField.setBounds(250, 100, 150, 30);
        portField.setText("5027");
        panel.add(portNumber);
        panel.add(portField);

        JLabel ipAddress = new JLabel("Enter IP Address of the host : ");
        ipAddress.setBounds(20, 140, 200, 30);
        ipAddressField = new JTextField();
        ipAddressField.setBounds(250, 140, 150, 30);
        panel.add(ipAddress);
        panel.add(ipAddressField);

        JLabel setDownloadLocation = new JLabel("Choose Download Location : ");
        setDownloadLocation.setBounds(20, 180, 200, 30);
        downloadLocationField = new JTextField();
        downloadLocationField.setBounds(250, 180, 300, 30);
        downloadLocationField.setEnabled(false);
        panel.add(setDownloadLocation);
        panel.add(downloadLocationField);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Download Directory");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileChooser.setAcceptAllFileFilterUsed(false);

        openFilesButton = new JButton("Select Folder");
        openFilesButton.setBounds(580, 180, 150, 30);
        openFilesButton.addActionListener((ActionEvent e) -> {
            fileChooser.showOpenDialog(new JFrame());
            File file = fileChooser.getSelectedFile();
            downloadLocationField.setText(file.getAbsolutePath());
        });

        panel.add(openFilesButton);

        Connect = new JButton("Connect");
        Connect.setBounds(350, 220, 150, 30);
        Connect.addActionListener((ActionEvent e) -> {
            Connect.setEnabled(false);
            downloadLocation = downloadLocationField.getText();
            RemoteIPAddress = ipAddressField.getText();
            Username = NameField.getText();
            MachineID = MachineIdField.getText();
            String remotePort = portField.getText();
            try {
                port = Integer.parseInt(remotePort);
                if ("".equals(Username)) {
                    JOptionPane.showMessageDialog(new JFrame(), "Please enter a valid Username.");
                } else if ("".equals(MachineID)) {
                    JOptionPane.showMessageDialog(new JFrame(), "Please enter a valid Machine ID.");
                } else if ("".equals(RemoteIPAddress)) {
                    JOptionPane.showMessageDialog(new JFrame(), "Please enter a valid file IP Address.");
                } else if ("".equals(port)) {
                    JOptionPane.showMessageDialog(new JFrame(), "Please enter a valid port number.");
                } else if ("".equals(downloadLocation)) {
                    JOptionPane.showMessageDialog(new JFrame(), "Please choose a valid file download location.");
                } else  {
                    clientGUIThread = new Thread() {
                        @Override
                        public void run() {
                            clientGUI = new ClientGUI(Username, MachineID, RemoteIPAddress, port, downloadLocation,new ImageIcon(ClientStub.class.getResource("exam.png")));
                        }
                    };
                    clientGUIThread.start();
                    TimeUnit.SECONDS.sleep(1);
                    Connect.setEnabled(true);
                    frame.dispose();
                }
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Please enter a valid port number.");
            } catch (HeadlessException | InterruptedException i) {
                i.printStackTrace();
            }
        });
        panel.add(Connect);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

    }

    public static void main(String args[]) {
        ClientStub clientStub = new ClientStub();
        try {
            clientStub.prepareGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
