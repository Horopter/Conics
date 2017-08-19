/*
 * To change this license header, choose License Headers bufferedReader Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template bufferedReader the editor.
 */
package Horopter.Server;

import Horopter.Utilities.ZipUtility;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author SantoshKumar
 */
//<editor-fold defaultstate="collapsed" desc="File Upload and Download Thread.">
class FileThread extends Thread {
//</editor-fold>

    int dataCounter = 0;
    String name, sentinel;
    String filename;
    Socket threadSocket;
    public int threadId;
    String folderName;
    int fileCount;
    int size = 65535;
    BufferedReader bufferedReader;
    int progress = 0;
    public String Username;
    public String MachineId;

    public FileThread(Socket socket, int cnt, String folder) {
        threadSocket = socket;
        threadId = cnt;
        folderName = folder;
    }

    @Override
    @SuppressWarnings("null")
    public void run() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));
            OutputStream outputStream;
            ObjectOutputStream objectOutputStream;
            try (InputStream inputStream = threadSocket.getInputStream()) {
                outputStream = threadSocket.getOutputStream();
                objectOutputStream = null;
                Username = bufferedReader.readLine();
                MachineId = bufferedReader.readLine();
                System.out.println("They say their name is : " + Username + ". Say Hi!");
                String command = bufferedReader.readLine();
                if ("askDetails".equals(command)) {
                    askDetails(inputStream, outputStream, objectOutputStream);
                    bufferedReader.close();
                } else if ("transferFile".equals(command)) {
                    String mode = bufferedReader.readLine();
                    if ("single".equals(mode)) {
                        name = bufferedReader.readLine();
                    } else if ("multiple".equals(mode)) {
                        String list = bufferedReader.readLine();
                        name = bufferedReader.readLine();
                        String[] FileNamesList = decodeName(list).split("\\|");
                        System.out.println("Zipping your file.");
                        ZipUtility.zip(FileNamesList, decodeName(name), folderName);
                        System.out.println("Zipping complete.");
                    }
                    transferFile(inputStream, outputStream, objectOutputStream);
                    bufferedReader.close();
                }
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (null != objectOutputStream) {
                objectOutputStream.close();
            }
            if (null != threadSocket) {
                threadSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            //cleanUp
            File zipFile = new File(folderName, "zipmodule.zip");
            if (zipFile.exists()) {
                zipFile.delete();
            }
        }
    }

//<editor-fold defaultstate="expanded" desc="TCP connection to get only file details.">
    private void askDetails //</editor-fold>
            (InputStream inputStream, OutputStream outputStream, ObjectOutputStream objectOutputStream) throws IOException {
        File ff = new File(folderName);
        ArrayList<String> names = new ArrayList<>(Arrays.asList(ff.list()));
        objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(String.valueOf(names.size()));

        for (String Name : names) {
            objectOutputStream.writeObject(Name);
        }
        inputStream.close();
        outputStream.close();
    }

    private void importFiles(InputStream inputStream) throws IOException {
        System.out.println("Request to upload file " + name + " recieved from " + threadSocket.getInetAddress().getHostName() + "...");
        File folder = new File(folderName);
        if (!folder.exists()) //<editor-fold defaultstate="expanded" desc="if">
        {
            folder.mkdir();
        }
        //</editor-fold>
        name = getStorageFilename();
        File fc = new File(folder, name);
        long filesize = Integer.parseInt(bufferedReader.readLine());
        try (FileOutputStream fileOutputStream = new FileOutputStream(fc)) {
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            receivePackets(filesize, inputStream, dataOutputStream, fc);
        }
    }

    private void exportFiles(OutputStream outputStream, ObjectOutputStream objectOutputStream) throws IOException {
        int end = name.lastIndexOf("|");
        filename = name.substring(1, end);

        if (!"".equals(filename)) //<editor-fold defaultstate="expanded" desc="if">
        {
            FileInputStream file = null;
            BufferedInputStream bufferedInputStream = null;
            boolean fileExists;
            File downloadFile = new File(folderName, filename);
            fileExists = downloadFile.exists();

            if (fileExists) //<editor-fold defaultstate="expanded" desc="if">
            {
                file = new FileInputStream(downloadFile.getAbsolutePath());
                bufferedInputStream = new BufferedInputStream(file);
                objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.reset();
                objectOutputStream.writeObject("Success");
                objectOutputStream.writeObject(downloadFile.length());
                sendPackets(bufferedInputStream, outputStream);
                bufferedInputStream.close();
                file.close();
                objectOutputStream.close();
                outputStream.close();
            } //<editor-fold>            
            else //<editor-fold defaultstate="expanded" desc="else">
            {
                objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject("FileNotFound");
                //<editor-fold defaultstate="expanded" desc="if">
                if (bufferedInputStream != null && file != null) {
                    bufferedInputStream.close();
                    file.close();
                }
                //</editor-fold>
                objectOutputStream.close();
                outputStream.close();
            }
            //</editor-fold>
        }
    }

    private void transferFile(InputStream inputStream, OutputStream outputStream, ObjectOutputStream objectOutputStream) throws IOException {
        sentinel = name.substring(0, 1);
        if (sentinel.equals("|")) {
            exportFiles(outputStream, objectOutputStream);
        } else {
            importFiles(inputStream);
        }
    }

    private void sendPackets(BufferedInputStream bufferedReader, OutputStream outputStream) throws IOException {
        progress = 0;
        int packetCounter;
        do {
            byte[] data = new byte[size];
            packetCounter = bufferedReader.read(data, 0, data.length);
            if (packetCounter != -1) {
                outputStream.write(data, 0, packetCounter);
                progress += packetCounter;
            }
            outputStream.flush();
        } while (packetCounter != -1);
    }

    public void receivePackets(Long filesize, InputStream inputStream, DataOutputStream dataOutputStream, File file)
            throws IOException {
        progress = 0;
        boolean isFinished = false;
        byte[] data = new byte[size];
        while (!isFinished) {
            dataCounter = inputStream.read(data, 0, data.length);
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
        }
    }

    private String decodeName(String n) {
        return n.substring(1, n.length() - 1);
    }

    private String getStorageFilename() {
        return Username + "_" + MachineId + "_" + getRemoteIPAddress(threadSocket.getRemoteSocketAddress()).replaceAll("[^a-zA-Z0-9\\.]","_") + "." + name.split("\\.(?=[^\\.]+$)")[1];
    }

    private String getRemoteIPAddress(SocketAddress socketAddress) {

        if (socketAddress instanceof InetSocketAddress) {
            InetAddress inetAddress = ((InetSocketAddress) socketAddress).getAddress();
            if (inetAddress instanceof Inet4Address) {
                return (inetAddress.toString());
            } else if (inetAddress instanceof Inet6Address) {
                return (inetAddress.toString());
            } else {
                System.err.println("Not an IP address.");
            }
        } else {
            System.err.println("Not an internet protocol socket.");
        }
        return "error";
    }
}
