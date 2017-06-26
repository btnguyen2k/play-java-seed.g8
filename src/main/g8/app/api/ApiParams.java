package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.ddth.commons.utils.JacksonUtils;

import play.libs.Json;

/**
 * Parameters passed to API.
 * 
 * <p>
 * Parameters sent by client to API must be a map {key -> value}
 * </p>
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ApiParams {

    private JsonNode params;

    public ApiParams() {
        params = Json.newObject();
    }

    public ApiParams(JsonNode jsonNode) {
        params = jsonNode != null ? jsonNode : Json.newObject();
    }

    public ApiParams(Object params) {
        this.params = params != null ? Json.toJson(params) : Json.newObject();
    }

    /**
     * Add a single parameter (existing one will be overridden).
     * 
     * @param name
     * @param value
     * @return
     */
    public ApiParams addParam(String name, Object value) {
        if (params instanceof ObjectNode) {
            JacksonUtils.setValue(params, name, value);
        }
        return this;
    }

    /**
     * Return all parameters.
     * 
     * @return
     */
    public JsonNode getAllParams() {
        return params;
    }

    /**
     * Get a parameter value.
     * 
     * @param name
     * @param clazz
     * @return
     */
    public <T> T getParam(String name, Class<T> clazz) {
        return JacksonUtils.getValue(params, name, clazz);
    }

    /**
     * Get a parameter value, if failed go to next parameter, and so on.
     * 
     * @param clazz
     * @param names
     * @return
     */
    public <T> T getParamOr(Class<T> clazz, String... names) {
        for (String name : names) {
            T result = getParam(name, clazz);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Get a parameter value.
     * 
     * @param name
     * @return
     */
    public JsonNode getParam(String name) {
        return params.get(name);
    }
}
