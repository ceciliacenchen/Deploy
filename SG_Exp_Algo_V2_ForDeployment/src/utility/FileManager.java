/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;
import datahandler.InputReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author cenchen.2012
 */
public class FileManager {

    public static void createFolder(String dir) {
        (new File(dir)).mkdirs();
    }

    public static void copyFileUsingJava7Files(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }

    public static void writeToFile(String dir, String data, boolean append) {
        try {
            File file = new File(dir);

            //if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            //true = append file
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), append);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close();
            //System.out.println(data);
            System.out.println("write with success: " + dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            //directory is empty, then delete it
            if (file.list().length == 0) {
                file.delete();
            } else {
                //list all the directory contents
                String files[] = file.list();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    delete(fileDelete);
                }
                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                }
            }
        } else {
            //if file, then delete it
            file.delete();
        }
    }

    public static String readFile(String filename) {
        return readFile(System.getProperty("user.dir"), filename);
    }

    public static String readFile(String path, String name) {
        InputReader reader = new InputReader(path, name);
        return reader.readAll();
    }
    public static void lookForFolder(String dir, String name, String ext, ArrayList<String> dirs) {
        File[] files = new File(dir).listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                lookForFiles(file.getPath(),name,ext, dirs); 
            } else {
            	if(file.getName().contains(name) && file.getName().endsWith(ext)) {
            		dirs.add(file.getPath());
//                    System.out.println("File: " + file.getName());
            	}
            }
        }
    }
    
    public static void lookForFiles(String dir, String name, String ext, ArrayList<String> dirs) {
        File[] files = new File(dir).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                lookForFiles(file.getPath(),name,ext, dirs); 
            } else {
            	if(file.getName().contains(name) && file.getName().endsWith(ext)) {
            		dirs.add(file.getPath());
//                    System.out.println("File: " + file.getName());
            	}
            }
        }
    }
}
