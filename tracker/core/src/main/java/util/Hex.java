package util;

import java.io.ByteArrayOutputStream;

/**
 * Useful class for conversion between bytes and HEX chars
 * @author Florian Chis
 */
public final class Hex {

    /**
     * Convert from byte[] to String where 2 chars = 1 byte
     * @param b data
     * @param offset offset
     * @param len length
     * @return String representation of byte array argument
     */
    public static String doHexCharsArray(byte[] b, int offset, int len) {
        buf = new StringBuffer();
        for (i = offset; i < len; i++) {
            buf.append(toHexChar((b[i] >> 4) & 0x0F)).append(toHexChar(b[i] & 0x0F));
        }
        return buf.toString();
    }
    private static StringBuffer buf;
    private static int i;

    /**
     * Convert from byte[] to String where 2 chars = 1 byte
     * @param b byte array data
     * @return String representation of b
     */
    public static String doHexCharsArray(byte[] b) {
        return doHexCharsArray(b, 0, b.length);
    }

    /**
     * Convert from String to byte[] where 1 byte = 2 chars
     * @param stream String as hex chars
     * @return byte array
     */
    public static byte[] doHexBytesArray(String stream) {
        baos.reset();
        j = 0;
        try {
            while (j < stream.length()) {
                b = toHexByte(stream.charAt(j)) << 4;
                j++;
                b |= toHexByte(stream.charAt(j));
                baos.write(b);
                j++;
            }
        } catch (Exception _) { //wrong length
        }
        return baos.toByteArray();
    }
    private static ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private static int b;
    private static int j;

    /**
     * Convert int to char
     * @param nr int
     * @return hex char
     */
    public static char toHexChar(int nr) {
        return hexDigit[(nr & 0xF)];
    }
    private static final char[] hexDigit = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * Convert from hex char to hex int
     * @param ch char
     * @return hex int
     */
    public static int toHexByte(char ch) {
        if (ch > 0x2F && ch < 0x3A) {
            return ch - 0x30;
        }
        if (ch > 0x40 && ch < 0x47) {
            return ch - 0x37;
        }
        if (ch > 0x60 && ch < 0x67) {
            return ch - 0x57;
        }
        return 0;
    }
}
