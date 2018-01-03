package com.hq.axmlattributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author bilux (i.bilux@gmail.com)
 */

public class ApkTool {

    // get apk information such as app name, version and icon.
    public static String getPackage(String filePath) throws IOException, ParserConfigurationException, SAXException {
        String xml = "";
        try (ZipFile zip = new ZipFile(filePath)) {
            ZipEntry amz;
            amz = zip.getEntry("AndroidManifest.xml");
            try (InputStream amis = zip.getInputStream(amz)) {
                int BUFFER_SIZE = (int) (amz.getSize() > 51200 ? 51200 : amz.getSize());
                byte[] buf = new byte[BUFFER_SIZE];
                int bytesRead = amis.read(buf);
                HashMap<String, String> attributes = Parser.getManifestHeaderAttributes(buf);
                Iterator it = attributes.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) it.next();
                    xml += pair.getKey() + " = " + pair.getValue() + System.lineSeparator();
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        }

        return xml;
    }
}
