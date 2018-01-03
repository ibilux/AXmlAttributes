/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hq.axmlattributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author bilux (i.bilux@gmail.com)
 */
public class AXmlAttributes {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //String fileName = "/home/hq/temp/uni.apk";
        //String fileName = "/home/hq/AndroidManifest.xml";
        String fileName = args[0];
        String xml = "";
        try {
            if (fileName.endsWith(".apk")) {
                xml = ApkTool.getPackage(fileName);
            } else if (fileName.endsWith(".xml")) {
                File file = new File(fileName);
                FileInputStream fin = new FileInputStream(file);
                byte buf[] = new byte[(int) file.length()];
                fin.read(buf);
                HashMap<String, String> attributes = Parser.getManifestHeaderAttributes(buf);
                Iterator it = attributes.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) it.next();
                    xml += pair.getKey() + " = " + pair.getValue() + System.lineSeparator();
                    it.remove(); // avoids a ConcurrentModificationException
                }
                fin.close();
            } else {
                xml = "Non valide file.";
            }
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            xml = "Non valide file.";
        }
        System.out.println(xml);
    }

}
