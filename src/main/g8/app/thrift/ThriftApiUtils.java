package thrift;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;

import play.libs.Json;
import thrift.def.TDataEncodingType;
import utils.AppConstants;

/**
 * Thrift API utility class.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ThriftApiUtils {

    /**
     * Encode data from json to byte array.
     * 
     * @param dataType
     * @param jsonNode
     * @return
     */
    public static byte[] encodeFromJson(TDataEncodingType dataType, JsonNode jsonNode) {
        byte[] data = jsonNode == null || jsonNode instanceof NullNode
                || jsonNode instanceof MissingNode ? null
                        : jsonNode.toString().getBytes(AppConstants.UTF8);
        if (data == null) {
            return null;
        }
        try {
            switch (dataType) {
            case JSON_STRING:
                return null;
            case JSON_GZIP:
                return toGzip(data);
            default:
                throw new IllegalArgumentException("Unsupported data encoding type: " + dataType);
            }
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    private static byte[] toGzip(byte[] data) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzipOs = new GZIPOutputStream(baos)) {
                gzipOs.write(data);
                gzipOs.finish();
                return baos.toByteArray();
            }
        }
    }

    /**
     * Decode data from a byte array to json.
     * 
     * @param dataType
     * @param data
     * @return
     */
    public static JsonNode decodeToJson(TDataEncodingType dataType, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            switch (dataType) {
            case JSON_STRING:
                return fromJsonString(data);
            case JSON_GZIP:
                return fromJsonGzip(data);
            default:
                throw new IllegalArgumentException("Unsupported data encoding type: " + dataType);
            }
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    private static JsonNode fromJsonString(byte[] data) {
        return Json.parse(data);
    }

    private static JsonNode fromJsonGzip(byte[] data) throws IOException {
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
