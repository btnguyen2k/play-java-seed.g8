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
import utils.RequestEntiryTooLargeException;

/**
 * Base class for Json-based web-service controllers.
 * 
 * <p>
 * This controller accepts request data in JSON format, and response to client also in JSON. All
 * responses have the following format:
 * </p>
 * 
 * <pre>
 * {
 *     "status" : "(int) status code",
 *     "message": "(string) status message",
 *     "data"   : "(mixed/optional) API's output data, each API/service defines its own output",
 *     "dedug"  : "(mixed/optional) debug information"
 * }
 * </pre>
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
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
    protected JsonNode parseRequestContent() throws IOException, RequestEntiryTooLargeException {
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
            throws IOException, RequestEntiryTooLargeException {
        RequestBody requestBody = request().body();
        JsonNode jsonNode = requestBody.asJson();
        if (jsonNode != null) {
            long postSize = jsonNode.toString().getBytes(AppConstants.UTF8).length;
            if (postSize > maxPostSize) {
                throw new RequestEntiryTooLargeException(postSize);
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
                    throw new RequestEntiryTooLargeException(postSize);
                }
                requestContent = buffer.decodeString(AppConstants.UTF8);
            } else {
                File bufferFile = rawBuffer.asFile();
                long postSize = bufferFile.length();
                if (postSize > maxPostSize) {
                    throw new RequestEntiryTooLargeException(postSize);
                }
                byte[] buff = FileUtils.readFileToByteArray(bufferFile);
                requestContent = buff != null ? new String(buff, AppConstants.UTF8) : null;
            }
        } else {
            requestContent = requestBody.asText();
            long postSize = requestContent.getBytes(AppConstants.UTF8).length;
            if (postSize > maxPostSize) {
                throw new RequestEntiryTooLargeException(postSize);
            }
        }

        return requestContent != null ? Json.parse(requestContent) : null;
    }

    /**
     * Response to client in JSON format.
     * 
     * @param status
     * @param message
     * @param data
     * @param debugData
     * @return
     * @since template-v0.1.2
     */
    @SuppressWarnings("serial")
    public Result doResponse(int status, String message, Object data, Object debugData) {
        Map<String, Object> result = new HashMap<String, Object>() {
            {
                put("status", status);
                if (message != null) {
                    put("msg", message);
                }
                if (data != null) {
                    put("data", data);
                }
                if (debugData != null) {
                    put("debug", debugData);
                }
            }
        };
        return ok(Json.toJson(result)).as(AppConstants.CONTENT_TYPE_JSON);
    }

    /**
     * Response to client in JSON format.
     * 
     * @param status
     * @param message
     * @param data
     * @return
     */
    public Result doResponse(int status, String message, Object data) {
        return doResponse(status, message, data, null);
    }

    /**
     * Resposne to client in JSON format.
     * 
     * @param status
     * @param message
     * @return
     */
    public Result doResponse(int status, String message) {
        return doResponse(status, message, null, null);
    }

}
