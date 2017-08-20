/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Horopter.Server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author SantoshKumar
 */
public interface FileThreadInterface {

    void receivePackets(Long filesize, InputStream inputStream, DataOutputStream dataOutputStream, File file) throws IOException;

    @SuppressWarnings(value = "null")
    void run();
    
}
