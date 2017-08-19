/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Horopter.Client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.util.Arrays;
import java.util.TreeSet;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.filechooser.FileView;
import javax.swing.text.View;

/**
 *
 * @author SantoshKumar
 */
public class ClientGUI implements ActionListener, MouseListener {

    public String Username;
    Socket clientSocket;
    InputStream serverDownloadStream;
    OutputStream serverUploadStream;
    BufferedInputStream bufferedInputStream;
    PrintWriter printWriter;
    JList<String> filesList;
    JFrame frame;
    JLabel title, subTitle, statusField, filesOnServer;
    JTextArea textArea;
    JScrollPane scrollPaneForTextArea;
    JButton uploadButton, downloadButton;
    JProgressBar progressBar;
    JFileChooser fileChooser;
    JButton openFilesButton, clearArea;
    JScrollPane scroll;
    Font titleFont, subtitleFont, statusFont;
    String[] fileNames = new String[1000]; //bottleneck on number of simultaneous file downloads
    String folderName;
    String fileName, filePlaceholder, path;
    String hostAddress;
    int portNumber;
    int dataCounter;
    int size = 65535;
    int fileCount;
    int progress = 0;
    String MachineId;
    TreeSet<String> selectedFilesList;
    Timer timer = new Timer(2000, this);
    ImageIcon icon;
    JRadioButton EnableUploadOnly, EnableDownloadOnly;
    ButtonGroup transferMode;

    @SuppressWarnings("LeakingThisInConstructor")
    public ClientGUI(String username, String machineName, String _hostAddress, int _portNumber, String _folderName, ImageIcon imageIcon) {
        Username = username;
        MachineId = machineName;
        folderName = _folderName;
        hostAddress = _hostAddress;
        portNumber = _portNumber;
        selectedFilesList = new TreeSet<>();
        icon = imageIcon;
        prepareGUI();
    }

    private void prepareGUI() {
        timer.start();
        frame = new JFrame("Exam App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(50, 50, 1000, 700);
        frame.setIconImage(icon.getImage());
        frame.getContentPane().setBackground(Color.WHITE);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);

        titleFont = new Font("Arial Narrow", Font.BOLD, 40);
        title = new JLabel("File Transfer Client");
        title.setFont(titleFont);
        title.setBounds(335, 30, 500, 30);
        frame.add(title);

        subtitleFont = new Font("Helvetica", Font.ITALIC, 18);
        subTitle = new JLabel("List of files to be transferred : ");
        subTitle.setFont(subtitleFont);
        subTitle.setBounds(150, 140, 500, 30);
        frame.add(subTitle);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBounds(170, 190, 500, 50);
        scrollPaneForTextArea = new JScrollPane(textArea);
        scrollPaneForTextArea.setBounds(160, 180, 520, 70);
        frame.add(scrollPaneForTextArea);

        uploadButton = new JButton("Upload");
        uploadButton.setBounds(200, 520, 100, 30);
        frame.add(uploadButton);

        downloadButton = new JButton("Download");
        downloadButton.setBounds(680, 520, 100, 30);
        frame.add(downloadButton);

        statusFont = new Font("Arial", Font.PLAIN, 12);
        statusField = new JLabel("");
        statusField.setHorizontalAlignment(JLabel.CENTER);
        statusField.setFont(statusFont);
        statusField.setBounds(250, 570, 500, 30);
        frame.add(statusField);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        progressBar.setBounds(240, 600, 500, 30);
        progressBar.setVisible(false);
        frame.add(progressBar);

        fileChooser = new JFileChooser(folderName);
        fileChooser.setFileView(new FileView() {
            @Override
            public Boolean isTraversable(File f) {
                return new File(folderName).equals(f);
            }
        });

        clearArea = new JButton("Clear");
        clearArea.setBounds(700, 220, 150, 30);
        clearArea.addActionListener(this);
        frame.add(clearArea);

        fileChooser.setMultiSelectionEnabled(false);
        disableButton(fileChooser, "FileChooser.homeFolderIcon");
        disableButton(fileChooser, "FileChooser.upFolderIcon");
        disableButton(fileChooser, "FileChooser.newFolderIcon");
        openFilesButton = new JButton("Select Files ...");
        openFilesButton.setBounds(700, 180, 150, 30);
        openFilesButton.addActionListener(this);
        frame.add(openFilesButton);

        uploadButton.addActionListener(this);
        downloadButton.addActionListener(this);

        filesOnServer = new JLabel("Files in the Server Directory :");
        filesOnServer.setBounds(150, 240, 400, 50);
        frame.add(filesOnServer);
        
        scroll = new JScrollPane();
        scroll.setBounds(325, 270, 400, 200);
        frame.add(scroll);

        EnableUploadOnly = new JRadioButton("Upload Mode", true);
        openFilesButton.setEnabled(true);
        downloadButton.setEnabled(false);
        uploadButton.setEnabled(true);
        Component myview1 = scroll.getViewport().getView();
        if(myview1!=null)
            myview1.setEnabled(false);
        EnableDownloadOnly = new JRadioButton("Download Mode");

        EnableUploadOnly.setMnemonic(KeyEvent.VK_U);
        EnableDownloadOnly.setMnemonic(KeyEvent.VK_D);

        EnableUploadOnly.addItemListener((ItemEvent e) -> {
            int State = e.getStateChange();
            if (State == 1) {
                openFilesButton.setEnabled(true);
                downloadButton.setEnabled(false);
                uploadButton.setEnabled(true);
                Component myview3 = scroll.getViewport().getView();        
                if(myview3!=null)            
                    myview3.setEnabled(false);
            } else {
                openFilesButton.setEnabled(false);
                downloadButton.setEnabled(true);
                uploadButton.setEnabled(false);
                Component myview2 = scroll.getViewport().getView();        
                if(myview2!=null)            
                    myview2.setEnabled(true);
            }
        });

        EnableDownloadOnly.addItemListener((ItemEvent e) -> {
            int State = e.getStateChange();
            if (State == 1) {
                openFilesButton.setEnabled(false);
                downloadButton.setEnabled(true);
                uploadButton.setEnabled(false);
                Component myview4 = scroll.getViewport().getView();        
                if(myview4!=null)            
                    myview4.setEnabled(true);
            } else {
                openFilesButton.setEnabled(true);
                downloadButton.setEnabled(false);
                uploadButton.setEnabled(true);
                Component myview5 = scroll.getViewport().getView();        
                if(myview5!=null)            
                    myview5.setEnabled(false);
            }
        });

        EnableUploadOnly.setBounds(355, 85, 200, 30);
        EnableDownloadOnly.setBounds(570, 85, 200, 30);

        frame.add(EnableUploadOnly);
        frame.add(EnableDownloadOnly);

        transferMode = new ButtonGroup();
        transferMode.add(EnableUploadOnly);
        transferMode.add(EnableDownloadOnly);

        
        askDetails();

        frame.setVisible(true);
    }

    public final void setClient() throws IOException {
        clientSocket = new Socket(hostAddress, portNumber);
        serverDownloadStream = clientSocket.getInputStream();
        printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        serverUploadStream = clientSocket.getOutputStream();
    }

    public final void closeAll() throws IOException {
        serverDownloadStream.close();
        serverUploadStream.close();
        printWriter.close();
        clientSocket.close();
    }

    //disabling components
    public static void disableButton(final Container c, final String iconString) {
        int len = c.getComponentCount();
        for (int i = 0; i < len; i++) {
            Component comp = c.getComponent(i);
            if (comp instanceof JButton) {
                JButton b = (JButton) comp;
                Icon icon = b.getIcon();
                if (icon != null
                        && icon == UIManager.getIcon(iconString)) {
                    b.setEnabled(false);
                }
            } else if (comp instanceof Container) {
                disableButton((Container) comp, iconString);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent click) {
        if (click.getClickCount() == 2) {//select on double click
            String selectedItem = (String) filesList.getSelectedValue();
            selectedFilesList.add(selectedItem);
            String resultString = "";
            String[] files = selectedFilesList.toArray(new String[selectedFilesList.size()]);
            for (int i = 0; i < files.length; i++) {
                if (i == 0) {
                    resultString = files[0];
                } else {
                    resultString += "|" + files[i];
                }
            }
            textArea.setText(resultString);
            frame.revalidate();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public void onDownloadButtonClicked() {
        try {
            setClient();
            File directory = new File(folderName);
            if (!directory.exists()) {
                directory.mkdir();
            }
            printWriter.println(Username);
            printWriter.println(MachineId);
            printWriter.println("transferFile");

            fileName = textArea.getText();
            String[] FileNamesList = fileName.split("\\|");
            if (FileNamesList.length > 1) {
                printWriter.println("multiple");
                filePlaceholder = encodeFilename(fileName);
                printWriter.println(filePlaceholder);
                fileName = "zipmodule.zip";
                printWriter.println(encodeFilename(fileName));
            } else {
                printWriter.println("single");
                filePlaceholder = encodeFilename(fileName);
                printWriter.println(filePlaceholder);
            }

            ObjectInputStream objectInputStream = new ObjectInputStream(serverDownloadStream);
            String resultString = (String) objectInputStream.readObject();
            if (resultString.equals("Success")) {
                File file = new File(directory, fileName);
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
                    Long filesize = (Long) objectInputStream.readObject();
                    receivePackets(filesize, dataOutputStream, file);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    statusField.setText("Unexpected Server Response : " + e.getMessage());
                    frame.revalidate();
                }
            } else {
                statusField.setText("Requested file could not be found on the server.");
                frame.revalidate();
            }
            closeAll();
            askDetails();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            statusField.setText("Remote Server refused to connect. Reopen the application and retry again.");
            frame.revalidate();
        }
    }

    public String encodeFilename(String name) {
        return "|" + name + "|";
    }

    public void onUploadButtonClicked() {
        try {
            setClient();
            fileName = textArea.getText();
            FileInputStream fileInputStream;
            BufferedInputStream _bufferedInputStream;
            boolean fileExists;

            path = folderName + "/" + fileName;
            File uploadFile = new File(path);
            fileExists = uploadFile.exists();
            if (!fileExists) {
                statusField.setText("Requested file could not be found.");
            } else {
                fileInputStream = new FileInputStream(path);
                _bufferedInputStream = new BufferedInputStream(fileInputStream);
                printWriter.println(Username);
                printWriter.println(MachineId);
                printWriter.println("transferFile");
                printWriter.println("single");
                printWriter.println(fileName);
                printWriter.println(uploadFile.length());
                statusField.setText("Uploading your file now --- ");
                frame.revalidate();

                sendPackets(_bufferedInputStream, serverUploadStream, uploadFile.length());
                statusField.setText("Completed.");
                frame.revalidate();

                boolean exists = false;
                for (int i = 0; i < fileCount; i++) {
                    if (fileNames[i].equals(fileName)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    fileNames[fileCount] = fileName;
                    fileCount++;
                }

                String[] temp_names = new String[fileCount];
                System.arraycopy(fileNames, 0, temp_names, 0, fileCount);

                Arrays.sort(temp_names);
                filesList.setListData(temp_names);

                _bufferedInputStream.close();
                fileInputStream.close();

            }
            closeAll();
            askDetails();
        } catch (IOException e) {
            statusField.setText("Remote Server refused to connect. Reopen the application and retry again.");
            frame.revalidate();
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == uploadButton) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    progress = 0;
                    progressBar.setVisible(true);
                    progressBar.setValue(0);
                    uploadButton.setEnabled(false);
                    downloadButton.setEnabled(false);
                    onUploadButtonClicked();
                }
            };
            thread.start();
        } else if (event.getSource() == downloadButton) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    progress = 0;
                    progressBar.setVisible(true);
                    progressBar.setValue(0);
                    uploadButton.setEnabled(false);
                    downloadButton.setEnabled(false);
                    onDownloadButtonClicked();
                }
            };
            thread.start();
        } else if (event.getSource() == timer) {
            if (progress == 0 && progressBar.isVisible() == true && !"Remote Server refused to connect. Reopen the application and retry again.".equals(statusField.getText())) {
                statusField.setText(" Counting, assembling and zipping your file(s). This might take a while. ");
            }
            frame.revalidate();
            frame.repaint();
        } else if (event.getSource() == openFilesButton) {
            onOpenFilesButtonClicked();
        } else if (event.getSource() == clearArea) {
            selectedFilesList = new TreeSet<>();
            textArea.setText("");
        }

    }

    public void onOpenFilesButtonClicked() {
        fileChooser.showOpenDialog(new JFrame());
        File file = fileChooser.getSelectedFile();
        textArea.setText(file.getName());
    }

    public static void commandRejected() {
        System.out.println("Couldn't connect. Sorry.");
    }

    private void sendPackets(BufferedInputStream in, OutputStream out, Long filesize) throws IOException {
        progress = 0;
        int packetCounter;
        do {
            byte[] data = new byte[size];
            packetCounter = in.read(data, 0, data.length);
            if (packetCounter != -1) {
                out.write(data, 0, packetCounter);
                progress += packetCounter;
                statusField.setText("Progress : " + progress + " bytes transferred out of " + filesize + " bytes.");
                progressBar.setValue(new BigDecimal(progress)
                        .multiply(new BigDecimal(100))
                        .divide(new BigDecimal(filesize)
                                .setScale(0, RoundingMode.FLOOR), 2, RoundingMode.HALF_UP)
                        .setScale(0, RoundingMode.FLOOR).intValue());
            }
            out.flush();
        } while (packetCounter != -1);
        statusField.setText("Completed.");
        progressBar.setValue(100);
        uploadButton.setEnabled(EnableUploadOnly.isSelected());
        downloadButton.setEnabled(EnableDownloadOnly.isSelected());
        textArea.setText("");
        frame.revalidate();
    }

    public void receivePackets(Long filesize, DataOutputStream dataOutputStream, File file) throws IOException {
        progress = 0;
        boolean isFinished = false;
        byte[] data = new byte[size];
        while (!isFinished) {
            dataCounter = serverDownloadStream.read(data, 0, data.length);
            progress = progress + dataCounter;
            if (dataCounter == -1) {
                isFinished = true;
                if (!file.exists()) {
                    file.createNewFile();
                }
            } else {
                dataOutputStream.write(data, 0, dataCounter);
                dataOutputStream.flush();
            }
            statusField.setText("Progress : " + progress + " bytes transferred out of " + filesize + " bytes.");
            if (filesize == 0) {
                progressBar.setValue(100);
            } else {
                progressBar.setValue(new BigDecimal(progress)
                        .multiply(new BigDecimal(100))
                        .divide(new BigDecimal(filesize)
                                .setScale(0, RoundingMode.FLOOR), 2, RoundingMode.HALF_UP)
                        .setScale(0, RoundingMode.FLOOR).intValue());
            }
        }
        statusField.setText("Completed.");
        progressBar.setValue(100);
        uploadButton.setEnabled(EnableUploadOnly.isSelected());
        downloadButton.setEnabled(EnableDownloadOnly.isSelected());
        textArea.setText("");
        frame.revalidate();
    }

    private void askDetails() {
        try {
            setClient();
            printWriter.println(Username);
            printWriter.println(MachineId);
            printWriter.println("askDetails");
            ObjectInputStream objectInputStream = new ObjectInputStream(serverDownloadStream);
            fileCount = Integer.parseInt((String) objectInputStream.readObject());

            String[] temp_names = new String[fileCount];

            for (int i = 0; i < fileCount; i++) {
                String filename = (String) objectInputStream.readObject();
                fileNames[i] = filename;
                temp_names[i] = filename;
            }
            Arrays.sort(temp_names);
            filesList = new JList<>(temp_names);
            scroll.setViewportView(filesList);

            frame.add(scroll);
            filesList.addMouseListener(this);
            if(null!=objectInputStream)
            {
                objectInputStream.close();
            }
            closeAll();

        } catch (IOException | ClassNotFoundException | NumberFormatException e) {
            e.printStackTrace();
            statusField.setText("Remote Server refused to connect. Reopen the application and retry again.");
            frame.revalidate();
        }
    }
}
