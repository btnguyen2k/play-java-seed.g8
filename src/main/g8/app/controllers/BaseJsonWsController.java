package controllers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import akka.util.ByteString;
import api.ApiAuth;
import api.ApiContext;
import api.ApiParams;
import api.ApiResult;
import modules.registry.RegistryGlobal;
import play.Configuration;
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
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 * @see ApiContext
 * @see ApiParams
 * @see ApiResult
 */
public class BaseJsonWsController extends BaseController {

    /**
     * Parse the request's body as {@link JsonNode}.
     * 
     * @return
     * @throws IOException
     * @throws RequestEntiryTooLargeException
     * @since template-v0.1.4
     */
    private JsonNode parseRequestBody() throws IOException, RequestEntiryTooLargeException {
        if (!StringUtils.equalsIgnoreCase(request().method(), "POST")) {
            return null;
        }

        Configuration appConfg = RegistryGlobal.registry.getAppConfiguration();
        int maxApiBody = appConfg.getInt("api.parser.maxBodySize", 1024 * 16);

        RequestBody requestBody = request().body();
        JsonNode jsonNode = requestBody.asJson();
        if (jsonNode != null) {
            int postSize = jsonNode.toString().getBytes(AppConstants.UTF8).length;
            if (postSize > maxApiBody) {
                throw new RequestEntiryTooLargeException(postSize);
            }
            return jsonNode;
        }

        String requestContent = null;
        RawBuffer rawBuffer = requestBody.asRaw();
        if (rawBuffer != null) {
            ByteString buffer = rawBuffer.asBytes();
            if (buffer != null) {
                int postSize = buffer.size();
                if (postSize > maxApiBody) {
                    throw new RequestEntiryTooLargeException(postSize);
                }
                requestContent = buffer.decodeString(AppConstants.UTF8);
            } else {
                File bufferFile = rawBuffer.asFile();
                if (bufferFile != null) {
                    long postSize = bufferFile.length();
                    if (postSize > maxApiBody) {
                        throw new RequestEntiryTooLargeException(postSize);
                    }
                    byte[] buff = FileUtils.readFileToByteArray(bufferFile);
                    requestContent = buff != null ? new String(buff, AppConstants.UTF8) : null;
                }
            }
        } else {
            requestContent = requestBody.asText();
            if (requestContent != null) {
                int postSize = requestContent.getBytes(AppConstants.UTF8).length;
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
     * @return
     * @throws IOException
     * @throws RequestEntiryTooLargeException
     * @since template-v0.1.4
     */
    protected ApiParams parseRequest() throws IOException, RequestEntiryTooLargeException {
        JsonNode json = parseRequestBody();
        ApiParams apiParams = new ApiParams(json);
        for (String key : request().queryString().keySet()) {
            apiParams.addParam(key, request().getQueryString(key));
        }
        return apiParams;
    }

    /**
     * Perform API call via web-service.
     * 
     * @param apiName
     * @return
     * @throws Exception
     * @since template-v0.1.4
     */
    protected Result doApiCall(String apiName) throws Exception {
        try {
            ApiParams apiParams = parseRequest();
            ApiContext apiContext = ApiContext.newContext(AppConstants.API_GATEWAY_WEB, apiName);
            ApiAuth apiAuth = ApiAuth.buildFromHttpRequest(request());
            ApiResult apiResult = RegistryGlobal.registry.getApiDispatcher().callApi(apiContext,
                    apiAuth, apiParams);
            return doResponse(apiResult != null ? apiResult : ApiResult.RESULT_UNKNOWN_ERROR);
        } catch (Exception e) {
            return doResponse(new ApiResult(ApiResult.STATUS_ERROR_SERVER, e.getMessage()));
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
