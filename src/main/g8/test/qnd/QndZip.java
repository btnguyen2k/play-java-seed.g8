package qnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;

import utils.AppConstants;

public class QndZip {

    private static byte[] compress(byte[] data) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ZipOutputStream zipOs = new ZipOutputStream(baos)) {
                zipOs.setLevel(9);
                zipOs.putNextEntry(new ZipEntry(""));
                zipOs.write(data);
                zipOs.closeEntry();
                zipOs.finish();
                return baos.toByteArray();
            }
        }
    }

    private static byte[] uncompress(byte[] data) throws IOException {
        try (ZipInputStream zipIs = new ZipInputStream(new ByteArrayInputStream(data))) {
            zipIs.getNextEntry();
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zipIs.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                return baos.toByteArray();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String dataStr = StringUtils.repeat("Thanh Nguyen <btnguyen2k@gmail.com>\t", 100);
        System.out.println("Data Str          = " + dataStr);

        byte[] data = dataStr.getBytes(AppConstants.UTF8);
        System.out.println("Data              = " + data.length);

        byte[] compressedData = compress(data);
        System.out.println("Compressed Data   = " + compressedData.length);

        byte[] uncompressedData = uncompress(compressedData);
        System.out.println("Uncompressed Data = " + uncompressedData.length);
        System.out
                .println("Data Uncomressed  = " + new String(uncompressedData, AppConstants.UTF8));
    }
}
