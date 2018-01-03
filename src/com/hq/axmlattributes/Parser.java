package com.hq.axmlattributes;

import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author bilux (i.bilux@gmail.com)
 */
public class Parser {

    public static int startTag = 0x00100102;

    public static HashMap<String, String> getManifestHeaderAttributes(byte[] binaryXml) throws IOException {
        int numbStrings = littleEndianWord(binaryXml, 4 * 4);
        int stringIndexTableOffset = 0x24;
        int stringTableOffset = stringIndexTableOffset + numbStrings * 4;
        int xmlTagOffset = littleEndianWord(binaryXml, 3 * 4);
        for (int i = xmlTagOffset; i < binaryXml.length - 4; i += 4) {
            if (littleEndianWord(binaryXml, i) == startTag) {
                xmlTagOffset = i;
                break;
            }
        }
        int offset = xmlTagOffset;

        while (offset < binaryXml.length) {
            int tag0 = littleEndianWord(binaryXml, offset);
            int nameStringIndex = littleEndianWord(binaryXml, offset + 5 * 4);

            if (tag0 == startTag) {
                int numbAttrs = littleEndianWord(binaryXml, offset + 7 * 4);
                offset += 9 * 4;

                HashMap<String, String> attributes = new HashMap<>();
                for (int i = 0; i < numbAttrs; i++) {
                    int attributeNameStringIndex = littleEndianWord(binaryXml, offset + 1 * 4);
                    int attributeValueStringIndex = littleEndianWord(binaryXml, offset + 2 * 4);
                    int attributeResourceId = littleEndianWord(binaryXml, offset + 4 * 4);
                    offset += 5 * 4;

                    String attributeName = getString(binaryXml, stringIndexTableOffset, stringTableOffset, attributeNameStringIndex);
                    String attributeValue;
                    if (attributeValueStringIndex != -1) {
                        attributeValue = getString(binaryXml, stringIndexTableOffset, stringTableOffset, attributeValueStringIndex);
                    } else {
                        attributeValue = "" + attributeResourceId;
                    }
                    attributes.put(attributeName, attributeValue);
                }
                return attributes;
            } else {
                // we only need the first <manifest> start tag
                break;
            }
        }
        return new HashMap<>(0);
    }

    public static String getString(byte[] bytes, int stringIndexTableOffset, int stringTableOffset, int stringIndex) {
        if (stringIndex < 0) {
            return null;
        }
        int stringOffset = stringTableOffset + littleEndianWord(bytes, stringIndexTableOffset + stringIndex * 4);
        return getStringAt(bytes, stringOffset);
    }

    public static String getStringAt(byte[] bytes, int stringOffset) {
        int length = bytes[stringOffset + 1] << 8 & 0xff00 | bytes[stringOffset] & 0xff;
        byte[] chars = new byte[length];
        for (int i = 0; i < length; i++) {
            chars[i] = bytes[stringOffset + 2 + i * 2];
        }
        return new String(chars);
    }

    // Return the little endian 32-bit word from the byte array at offset
    public static int littleEndianWord(byte[] bytes, int offset) {
        return bytes[offset + 3]
                << 24 & 0xff000000
                | bytes[offset + 2]
                << 16 & 0xff0000
                | bytes[offset + 1]
                << 8 & 0xff00
                | bytes[offset] & 0xFF;
    }
}
