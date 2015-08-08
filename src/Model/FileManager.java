/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import generatedClasses.ChineseCheckers;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

 public abstract class FileManager {
    
    public static ArrayList<String> readLinesFromFile(String path) throws FileNotFoundException, IOException{
        ArrayList<String> lines = new ArrayList();
        
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        
        String line;
        while((line = bufferedReader.readLine()) != null){
            lines.add(line);
        }
        return lines;
    }
    
    public static ChineseCheckers loadGame(String path){
        ChineseCheckers cc = null; 
        try {
            JAXBContext jc = JAXBContext.newInstance(ChineseCheckers.class);
            Unmarshaller u = jc.createUnmarshaller();
 
            File f = new File(path);
            cc = (ChineseCheckers) u.unmarshal(f);

        } catch (JAXBException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);

        }
        return cc;
    }
    
    public static void saveGame(String path,ChineseCheckers savedGame){
        try {
            JAXBContext context = JAXBContext.newInstance(ChineseCheckers.class);
            
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
             File f = new File(path);
            m.marshal(savedGame, f);
        } catch (PropertyException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
