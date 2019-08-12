package controllers;

import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.recipes.apiservice.ApiAuth;
import com.github.ddth.recipes.apiservice.ApiContext;
import com.github.ddth.recipes.apiservice.ApiParams;
import com.github.ddth.recipes.apiservice.ApiResult;
import com.typesafe.config.Config;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Http.RawBuffer;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import utils.AppConfigUtils;
import utils.AppConstants;
import utils.RequestEntiryTooLargeException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Base class for Json-based web-service controllers.
 *
 * <p> This controller accepts request data in JSON format, and response to client also in JSON.
 * All responses have the following format: </p>
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
 * @see ApiContext
 * @see ApiParams
 * @see ApiResult
 * @since template-v0.1.0
 */
public class BaseJsonWsController extends BaseController {
    /**
     * Parse the request's body as {@link JsonNode}.
     *
     * @param request
     * @return
     * @throws IOException
     * @throws RequestEntiryTooLargeException
     * @since template-v2.7.r1
     */
    private JsonNode parseRequestBody(Http.Request request) throws IOException, RequestEntiryTooLargeException {
        String requestMethod = request.method().toUpperCase();
        if (!StringUtils.equals(requestMethod, "POST") && !StringUtils.equals(requestMethod, "PUT") && !StringUtils
                .equals(requestMethod, "PATCH")) {
            //only POST, PUT, PATCH has request body
            return null;
        }

        Config appConfig = getAppConfig();
        int maxApiBody = AppConfigUtils.getOrDefault(appConfig::getInt, AppConstants.API_CONF_HTTP_MAX_BODY_SIZE, 0);
        if (maxApiBody <= 0) {
            maxApiBody = AppConstants.DEFAULT_API_CONF_HTTP_MAX_BODY_SIZE;
        }

        RequestBody requestBody = request.body();

        //first: parse request's body as json
        JsonNode jsonNode = requestBody.asJson();
        if (jsonNode != null) {
            int postSize = jsonNode.toString().getBytes(StandardCharsets.UTF_8).length;
            if (postSize > maxApiBody) {
                throw new RequestEntiryTooLargeException(postSize);
            }
            return jsonNode;
        }

        //fallback: parse request's body from raw text content
        String requestContent = null;
        RawBuffer rawBuffer = requestBody.asRaw();
        if (rawBuffer != null) {
            ByteString buffer = rawBuffer.asBytes();
            if (buffer != null) {
                int postSize = buffer.size();
                if (postSize > maxApiBody) {
                    throw new RequestEntiryTooLargeException(postSize);
                }
                requestContent = buffer.decodeString(StandardCharsets.UTF_8);
            } else {
                File bufferFile = rawBuffer.asFile();
                if (bufferFile != null) {
                    long postSize = bufferFile.length();
                    if (postSize > maxApiBody) {
                        throw new RequestEntiryTooLargeException(postSize);
                    }
                    byte[] buff = FileUtils.readFileToByteArray(bufferFile);
                    requestContent = buff != null ? new String(buff, StandardCharsets.UTF_8) : null;
                }
            }
        } else {
            requestContent = requestBody.asText();
            if (requestContent != null) {
                int postSize = requestContent.getBytes(StandardCharsets.UTF_8).length;
                if (postSize > maxApiBody) {
                    throw new RequestEntiryTooLargeException(postSize);
                }
            }
        }

        return requestContent != null ? Json.parse(requestContent) : null;
    }

    /**
     * Parse the requests's body as {@link ApiParams}.
     *
     * @param request
     * @return
     * @throws IOException
     * @throws RequestEntiryTooLargeException
     * @since template-v2.7.r1
     */
    protected ApiParams parseRequest(Http.Request request) throws IOException, RequestEntiryTooLargeException {
        JsonNode json = parseRequestBody(request);
        ApiParams apiParams = new ApiParams(json);
        for (String key : request.queryString().keySet()) {
            apiParams.addParam(key, request.getQueryString(key));
        }
        return apiParams;
    }

    /**
     * Build {@link ApiAuth} instance from http request.
     *
     * @param request
     * @return
     * @since template-v2.7.r1
     */
    protected ApiAuth buildApiAuthFromRequest(Http.Request request) {
        String appId = request.header(AppConstants.API_HTTP_HEADER_APP_ID).orElse("");
        String accessToken = request.header(AppConstants.API_HTTP_HEADER_ACCESS_TOKEN).orElse("");
        return new ApiAuth(appId, accessToken);
    }

    /**
     * Perform API call via web-service.
     *
     * @param request
     * @param apiName
     * @return
     * @throws Exception
     * @since template-v2.7.r1
     */
    protected Result doApiCall(Http.Request request, String apiName) {
        return doApiCall(request, apiName, null);
    }

    /**
     * Perform API call via web-service.
     *
     * <p>Parameter population rules:</p>
     * <ul>
     *     <li>Parameter-map is first constructed from request body. Request body, hence, should be valid JSON-encoded.</li>
     *     <li>Parameter-map is then populated with parameters from URL query string.</li>
     *     <li>Finally parameter-map is populated with supplied {@code params}.</li>
     * </ul>
     *
     * @param request
     * @param apiName
     * @param params
     * @return
     * @throws Exception
     * @since template-v2.7.r1
     */
    protected Result doApiCall(Http.Request request, String apiName, Map<String, Object> params) {
        try {
            ApiParams apiParams = parseRequest(request);
            if (params != null) {
                params.forEach((k, v) -> apiParams.addParam(k, v));
            }
            ApiContext apiContext = ApiContext.newContext(AppConstants.API_GATEWAY_WEB, apiName);
            apiContext.setContextField("method", request().method());
            apiContext.setContextField("uri", request().uri());
            ApiAuth apiAuth = buildApiAuthFromRequest(request());
            ApiResult apiResult = getRegistry().getApiRouter().callApi(apiContext, apiAuth, apiParams);
            return doResponse(apiResult != null ? apiResult : ApiResult.DEFAULT_RESULT_UNKNOWN_ERROR.clone());
        } catch (Exception e) {
            return doResponse(new ApiResult(ApiResult.STATUS_ERROR_SERVER, "[" + e.getClass() + "] " + e.getMessage()));
        }
    }

    /**
     * Return API result to client in JSON format.
     *
     * @param apiResult
     * @return
     * @since template-v0.1.4
     */
    public Result doResponse(ApiResult apiResult) {
        return ok(apiResult.asJson()).as(AppConstants.CONTENT_TYPE_JSON);
    }
}
