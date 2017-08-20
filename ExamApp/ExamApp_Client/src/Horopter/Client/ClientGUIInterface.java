/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Horopter.Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author SantoshKumar
 */
public interface ClientGUIInterface extends ActionListener, MouseListener {

    void actionPerformed(ActionEvent event);

    void closeAll() throws IOException;

    String encodeFilename(String name);

    void mouseClicked(MouseEvent click);

    void mouseEntered(MouseEvent e);

    void mouseExited(MouseEvent e);

    void mousePressed(MouseEvent e);

    void mouseReleased(MouseEvent e);

    void onDownloadButtonClicked();

    void onOpenFilesButtonClicked();

    void onUploadButtonClicked();

    void receivePackets(Long filesize, DataOutputStream dataOutputStream, File file) throws IOException;

    void setClient() throws IOException;
    
}
