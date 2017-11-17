package api;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.util.HashMap;
import java.util.Map;

/**
 * Result from API call.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ApiResult implements Cloneable {

    public final static int STATUS_OK = 200;
    public final static int STATUS_ERROR_CLIENT = 400;
    public final static int STATUS_NO_PERMISSION = 403;
    public final static int STATUS_NOT_FOUND = 404;
    public final static int STATUS_DEPRECATED = 410;
    public final static int STATUS_ERROR_SERVER = 500;
    public final static String MSG_OK = "Ok";

    public final static ApiResult DEFAULT_RESULT_OK = new ApiResult(STATUS_OK, MSG_OK);
    public final static ApiResult DEFAULT_RESULT_API_DEPRECATED = new ApiResult(STATUS_DEPRECATED,
            "API is deprecated.");

    public final static ApiResult DEFAULT_RESULT_API_NOT_FOUND = new ApiResult(STATUS_ERROR_CLIENT,
            "API not found");
    public final static ApiResult DEFAULT_RESULT_NOT_FOUND = new ApiResult(STATUS_NOT_FOUND,
            "Item not found");
    public final static ApiResult DEFAULT_RESULT_ACCESS_DENIED = new ApiResult(STATUS_NO_PERMISSION,
            "Access denied");
    public final static ApiResult DEFAULT_RESULT_UNKNOWN_ERROR = new ApiResult(STATUS_ERROR_SERVER,
            "Unknown error while calling API");

    public static ApiResult resultOk() {
        return DEFAULT_RESULT_OK;
    }

    public static ApiResult resultOk(String message) {
        return new ApiResult(STATUS_OK, message);
    }

    public static ApiResult resultOk(String message, Object data) {
        return new ApiResult(STATUS_OK, message, data);
    }

    public static ApiResult resultOk(Object data) {
        return new ApiResult(STATUS_OK, MSG_OK, data);
    }

    public static ApiResult resultDeprecated() {
        return resultDeprecated(null);
    }

    public static ApiResult resultDeprecated(String newApi) {
        return newApi == null
                ? DEFAULT_RESULT_API_DEPRECATED
                : new ApiResult(STATUS_DEPRECATED,
                        "API is deprecated. Please migrate to new API [" + newApi + "].");
    }

    public final int status;
    public final String message;
    public final Object data;
    private Object debugData;

    public ApiResult(int status) {
        this(status, null, null);
    }

    public ApiResult(int status, String message) {
        this(status, message, null);
    }

    public ApiResult(int status, Object data) {
        this(status, null, data);
    }

    public ApiResult(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResult setDebugData(Object debugData) {
        synchronized (this) {
            this.debugData = debugData;
            this.map = null;
            this.jsonNode = null;
            return this;
        }
    }

    public JsonNode getDataAsJson() {
        return data != null ? Json.toJson(data) : null;
    }

    public Object getDebugData() {
        return debugData;
    }

    public JsonNode getDebugDataAsJson() {
        return debugData != null ? Json.toJson(debugData) : null;
    }

    private JsonNode jsonNode;
    private Map<String, Object> map;

    public Map<String, Object> asMap() {
        if (map == null) {
            synchronized (this) {
                if (map == null) {
                    map = new HashMap<>();
                    map.put("status", status);
                    if (message != null) {
                        map.put("msg", message);
                    }
                    if (data != null) {
                        map.put("data", data);
                    }
                    if (debugData != null) {
                        map.put("debug", debugData);
                    }
                }
            }
        }
        return map;
    }

    public JsonNode asJson() {
        if (jsonNode == null) {
            synchronized (this) {
                if (jsonNode == null) {
                    jsonNode = Json.toJson(asMap());
                }
            }
        }
        return jsonNode;
    }

    /*----------------------------------------------------------------------*/
    public ApiResult clone() {
        try {
            ApiResult clone = (ApiResult) super.clone();
            clone.map = null;
            clone.jsonNode = null;
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public ApiResult clone(Object debugData) {
        ApiResult clone = clone();
        clone.setDebugData(debugData);
        return clone;
    }

}
