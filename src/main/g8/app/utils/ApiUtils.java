package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;

/**
 * API Utility Helper class.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.5.r2
 */
public class ApiUtils {
    /**
     * Compress data using Gzip.
     * 
     * @param data
     * @return
     * @throws IOException
     */
    public static byte[] toGzip(byte[] data) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzipOs = new GZIPOutputStream(baos)) {
                gzipOs.write(data);
                gzipOs.finish();
                return baos.toByteArray();
            }
        }
    }

    /**
     * Decode data from a JSON string.
     * 
     * @param data
     * @return
     */
    public static JsonNode fromJsonString(byte[] data) {
        return Json.parse(data);
    }

    /**
     * Decode data from a Gzipped-JSON string.
     * 
     * @param data
     * @return
     * @throws IOException
     */
    public static JsonNode fromJsonGzip(byte[] data) throws IOException {
        try (GZIPInputStream gzipIs = new GZIPInputStream(new ByteArrayInputStream(data))) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gzipIs.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                return fromJsonString(baos.toByteArray());
            }
        }
    }
}
