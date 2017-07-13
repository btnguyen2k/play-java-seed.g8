package grpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.protobuf.ByteString;

import grpc.def.ApiServiceProto.PDataEncodingType;
import utils.ApiUtils;
import utils.AppConstants;

/**
 * gRPC API utility class.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r2
 */
public class GrpcApiUtils {

    /**
     * Encode data from JSON to {@link ByteString}.
     * 
     * @param dataType
     * @param jsonNode
     * @return
     */
    public static ByteString encodeFromJson(PDataEncodingType dataType, JsonNode jsonNode) {
        byte[] data = jsonNode == null || jsonNode instanceof NullNode
                || jsonNode instanceof MissingNode ? null
                        : jsonNode.toString().getBytes(AppConstants.UTF8);
        if (data == null) {
            return ByteString.EMPTY;
        }
        if (dataType == null) {
            dataType = PDataEncodingType.JSON_STRING;
        }
        try {
            switch (dataType) {
            case JSON_STRING:
                return ByteString.copyFrom(data);
            case JSON_GZIP:
                return ByteString.copyFrom(ApiUtils.toGzip(data));
            default:
                throw new IllegalArgumentException("Unsupported data encoding type: " + dataType);
            }
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    /**
     * Decode data from a byte array to json.
     * 
     * @param dataType
     * @param data
     * @return
     */
    public static JsonNode decodeToJson(PDataEncodingType dataType, ByteString data) {
        if (data == null || data.isEmpty()) {
            return NullNode.instance;
        }
        if (dataType == null) {
            dataType = PDataEncodingType.JSON_STRING;
        }
        try {
            switch (dataType) {
            case JSON_STRING:
                return ApiUtils.fromJsonString(data.toByteArray());
            case JSON_GZIP:
                return ApiUtils.fromJsonGzip(data.toByteArray());
            default:
                throw new IllegalArgumentException("Unsupported data encoding type: " + dataType);
            }
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

}
