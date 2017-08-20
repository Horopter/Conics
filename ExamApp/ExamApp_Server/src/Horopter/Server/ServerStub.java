/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Horopter.Server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 *
 * @author SantoshKumar
 */
public class ServerStub implements ActionListener {

    ServerSocket serverSocket;
    int port;

    JPanel panel1 = null, panel2 = null, panel3 = null;
    JTabbedPane tabbedPane = null;
    JTextField uploadLocation = null;
    JTextField portNumber = null;
    JButton openFilesButton;
    boolean isServerOn = false;
    Thread launchServiceThread;
    ArrayList<Socket> socketArray;
    String fileUploadLocation;
    ArrayList<FileThread> threadArray;
    boolean launch;
    Timer timer = new Timer(2000, this);
    JFrame frame;
    JScrollPane userListScroll, fileListScroll;

    private void launchSocketService() {
        socketArray = new ArrayList<>();
        threadArray = new ArrayList<>();
        try {
            int id = 1;
            serverSocket = new ServerSocket(port); //FTP port 21 usage may hinder other apps
            while (launch) {
                if (isServerOn) {
                    try {
                        Socket thisSocket = serverSocket.accept();
                        socketArray.add(thisSocket);
                        System.out.println("Client with ID " + id + " connected from " + thisSocket.getInetAddress().getHostName() + "...");
                        FileThread server = new FileThread(thisSocket, id, fileUploadLocation);
                        id = (id + 1) % Integer.MAX_VALUE;
                        server.start();
                        threadArray.add(server);
                    } catch (SocketException e) {
                        System.out.println("Manual close from Admin. Anywho this is the exception that's occurred." + e.getMessage());
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void terminateSocketService() {
        launch = false;
        if (threadArray != null) {
            try {
                Iterator<FileThread> iter = threadArray.iterator();
                while (iter.hasNext()) {
                    FileThread fileThread = iter.next();
                    fileThread.bufferedReader.close();
                    fileThread.threadSocket.close();
                    iter.remove();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        if (socketArray != null) {
            Iterator<Socket> iter = socketArray.iterator();
            while (iter.hasNext()) {
                Socket socket = iter.next();
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                iter.remove();
            }
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        openFilesButton.setEnabled(true);
        portNumber.setEnabled(true);
    }

    public static void main(String[] args) {

        ServerStub serverStub = new ServerStub();
        serverStub.timer.start();
        try {
            serverStub.prepareGUI("Control Options", "Show All Files", "Show Connections in this Session ", new ImageIcon(ServerStub.class.getResource("exam.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JPanel getTitlePanel(String title, JPanel titlePanel) {
        FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        layout.setHgap(10);
        layout.setVgap(10);
        titlePanel = new JPanel(layout);
        titlePanel.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        titlePanel.add(titleLbl);
        return titlePanel;
    }

    private void prepareGUI(String title1, String title2,
            String title3, ImageIcon imageIcon) {
        frame = new JFrame("Exam App Master");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setMinimumSize(new Dimension(700, 300));
        frame.setIconImage(imageIcon.getImage());
        tabbedPane = new JTabbedPane();
        frame.setResizable(false);

        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();

        panel1.setBackground(Color.CYAN);
        panel2.setBackground(Color.GREEN);
        panel3.setBackground(Color.YELLOW);

        JLabel setUploadLocation = new JLabel("Choose Upload Location : ");
        setUploadLocation.setBounds(20, 20, 200, 30);
        uploadLocation = new JTextField();
        uploadLocation.setEditable(false);

        JLabel setPortNumber = new JLabel("Choose port number : ");
        setPortNumber.setBounds(20, 80, 200, 30);
        portNumber = new JTextField();
        portNumber.setText("5027");

        JButton startServer = new JButton("Start Server");

        uploadLocation.setBounds(220, 20, 200, 30);
        portNumber.setBounds(220, 80, 200, 30);
        startServer.setBounds(220, 150, 200, 30);

        userListScroll = new JScrollPane();
        userListScroll.setBounds(20, 20, 500, 300);
        panel3.setLayout(new BorderLayout());
        panel3.add(userListScroll, BorderLayout.CENTER);

        fileListScroll = new JScrollPane();
        fileListScroll.setBounds(20, 20, 500, 300);
        panel2.setLayout(new BorderLayout());
        panel2.add(fileListScroll, BorderLayout.CENTER);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Upload Directory");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileChooser.setAcceptAllFileFilterUsed(false);

        openFilesButton = new JButton("Select Folder");
        openFilesButton.setBounds(440, 20, 150, 30);
        openFilesButton.addActionListener((ActionEvent e) -> {
            fileChooser.showOpenDialog(new JFrame());
            File file = fileChooser.getSelectedFile();
            uploadLocation.setText(file.getAbsolutePath());
        });

        startServer.addActionListener((ActionEvent e) -> {
            if (isServerOn) {
                terminateSocketService();
                startServer.setText("Start Server");
                isServerOn = !isServerOn;
            } else {
                String requiredDirectory = uploadLocation.getText();
                File directory = new File(requiredDirectory);
                if ("".equals(requiredDirectory) || !directory.exists() || !directory.isDirectory()) {
                    JOptionPane.showMessageDialog(new JFrame(), "Please choose a valid file upload location.");
                } else {
                    fileUploadLocation = requiredDirectory;

                    String result = portNumber.getText();
                    try {
                        port = Integer.parseInt(result);
                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(new JFrame(), "Enter a valid port number.");
                    }
                    launch = true;
                    isServerOn = !isServerOn;

                    refreshFileListPanel();

                    launchServiceThread = new Thread() {
                        @Override
                        public void run() {
                            openFilesButton.setEnabled(false);
                            portNumber.setEnabled(false);
                            launchSocketService();
                        }
                    };
                    launchServiceThread.start();
                    startServer.setText("Stop Server");
                }
            }

        });

        panel1.setLayout(null);
        panel1.add(setUploadLocation);
        panel1.add(uploadLocation);
        panel1.add(setPortNumber);
        panel1.add(portNumber);
        panel1.add(startServer);
        panel1.add(openFilesButton);

        tabbedPane.add(panel1);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(panel1), getTitlePanel(title1, panel1));
        tabbedPane.setBackgroundAt(tabbedPane.indexOfComponent(panel1), Color.CYAN);

        tabbedPane.add(panel2);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(panel2), getTitlePanel(title2, panel2));
        tabbedPane.setBackgroundAt(tabbedPane.indexOfComponent(panel2), Color.GREEN);

        tabbedPane.add(panel3);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(panel3), getTitlePanel(title3, panel3));
        tabbedPane.setBackgroundAt(tabbedPane.indexOfComponent(panel3), Color.YELLOW);

        frame.add(tabbedPane);

        // Display the window.
        frame.pack();

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, " Are you sure you want stop the server and Quit?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    terminateSocketService();
                    System.exit(0);
                }
            }
        });
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == timer) {
            HashSet<String> Usernames = new HashSet<>();
            if (threadArray != null && threadArray.size() > 0) {
                threadArray.forEach((thread) -> {
                    Usernames.add(thread.Username);
                });
                JList userList = new JList<>(Usernames.toArray());
                userListScroll.setViewportView(userList);
            }

            if (frame != null) {
                frame.revalidate();
                frame.repaint();
            }
        }
    }

    private void refreshFileListPanel() {
        if (uploadLocation != null && !"".equals(uploadLocation.getText())) {
            File directory = new File(uploadLocation.getText());
            String[] fileNames = directory.list();
            Arrays.sort(fileNames);
            JList filesList = new JList<>(fileNames);
            if (fileListScroll != null) {
                fileListScroll.setViewportView(filesList);
            }
        }
    }
}
