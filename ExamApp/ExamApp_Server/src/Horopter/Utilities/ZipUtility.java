/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Horopter.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author SantoshKumar
 *
 * codeSource :
 * https://stackoverflow.com/questions/16546992/how-to-create-a-zip-file-of-multiple-image-files
 *
 */
public class ZipUtility {

    public static void zip(String[] sourceFiles, String zipFile, String directory) {
        try {
            zipFile = directory + File.separatorChar + zipFile;
            // create byte buffer
            byte[] buffer = new byte[65535];

            int progress = 0;

            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);

            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            for (int i = 0; i < sourceFiles.length; i++) {
                sourceFiles[i] = directory + File.separatorChar + sourceFiles[i];
                File srcFile = new File(sourceFiles[i]);

                FileInputStream fileInputStream = new FileInputStream(srcFile);

                // begin writing a new ZIP entry, positions the stream to the start of the entry data
                zipOutputStream.putNextEntry(new ZipEntry(srcFile.getName()));

                int length;

                while ((length = fileInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, length);
                    progress += length;
                    System.out.println("Progress : " + progress);
                }

                zipOutputStream.closeEntry();

                // close the InputStream
                fileInputStream.close();

            }

            // close the ZipOutputStream
            zipOutputStream.close();

        } catch (IOException e) {
            System.out.println("Error creating zip file: " + e);
        }

    }

}
