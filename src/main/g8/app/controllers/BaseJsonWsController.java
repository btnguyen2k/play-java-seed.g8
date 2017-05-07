package controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.JsonNode;

import akka.util.ByteString;
import play.libs.Json;
import play.mvc.Http.RawBuffer;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import utils.AppConstants;
import utils.RequestEntiryTooLarge;

/**
 * Base class for Json-based webservice controllers.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class BaseJsonWsController extends BaseController {

    /**
     * Parse the request's content as {@link JsonNode}.
     * 
     * <p>
     * Post size limit is defined by {@code play.http.parser.maxDiskBuffer} in
     * {@code application.conf}
     * </p>
     * 
     * @return
     * @throws IOException
     */
    protected JsonNode parseRequestContent() throws IOException, RequestEntiryTooLarge {
        return parseRequestContent(Integer.MAX_VALUE);
    }

    /**
     * Parse the request's content as {@link JsonNode}.
     * 
     * <p>
     * Post size limit is
     * {@code min(maxPostSize, play.http.parser.maxDiskBuffer in application.conf)}
     * </p>
     * 
     * @param maxPostSize
     * @return
     * @throws IOException
     */
    protected JsonNode parseRequestContent(long maxPostSize)
            throws IOException, RequestEntiryTooLarge {
        RequestBody requestBody = request().body();
        JsonNode jsonNode = requestBody.asJson();
        if (jsonNode != null) {
            long postSize = jsonNode.toString().getBytes(AppConstants.UTF8).length;
            if (postSize > maxPostSize) {
                throw new RequestEntiryTooLarge(postSize);
            }
            return jsonNode;
        }

        String requestContent = null;
        RawBuffer rawBuffer = requestBody.asRaw();
        if (rawBuffer != null) {
            ByteString buffer = rawBuffer.asBytes();
            if (buffer != null) {
                long postSize = buffer.size();
                if (postSize > maxPostSize) {
                    throw new RequestEntiryTooLarge(postSize);
                }
                requestContent = buffer.decodeString(AppConstants.UTF8);
            } else {
                File bufferFile = rawBuffer.asFile();
                long postSize = bufferFile.length();
                if (postSize > maxPostSize) {
                    throw new RequestEntiryTooLarge(postSize);
                }
                byte[] buff = FileUtils.readFileToByteArray(bufferFile);
                requestContent = buff != null ? new String(buff, AppConstants.UTF8) : null;
            }
        } else {
            requestContent = requestBody.asText();
            long postSize = requestContent.getBytes(AppConstants.UTF8).length;
            if (postSize > maxPostSize) {
                throw new RequestEntiryTooLarge(postSize);
            }
        }

        return requestContent != null ? Json.parse(requestContent) : null;
    }

    @SuppressWarnings("serial")
    public Result doResponse(int status, String message, Object data) {
        Map<String, Object> result = new HashMap<String, Object>() {
            {
                put("status", status);
                if (message != null) {
                    put("msg", message);
                }
                if (data != null) {
                    put("data", data);
                }
            }
        };
        return ok(Json.toJson(result)).as("application/json; charset=UTF-8");
    }

    public Result doResponse(int status, String message) {
        return doResponse(status, message, null);
    }

}
