package thrift;

import api.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.MapUtils;
import com.google.inject.Provider;
import modules.registry.IRegistry;
import org.apache.thrift.TException;
import play.Logger;
import thrift.def.*;
import utils.AppConstants;

/**
 * Handle API calls via Thrift.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ApiServiceHandler implements TApiService.Iface {

    private Provider<IRegistry> registry;

    public ApiServiceHandler(Provider<IRegistry> registry) {
        this.registry = registry;
    }

    private static ApiParams parseParams(TApiParams _apiParams) {
        JsonNode paramNode = ThriftApiUtils.decodeToJson(
                _apiParams.dataType != null ? _apiParams.dataType : TDataEncodingType.JSON_STRING,
                _apiParams.getParamsData());
        ApiParams apiParams = new ApiParams(paramNode);
        return apiParams;
    }

    private static ApiAuth buildAuth(TApiAuth _apiAuth) {
        return new ApiAuth(_apiAuth.getApiKey(), _apiAuth.getAccessToken());
    }

    private static TApiResult doResponse(ApiResult _apiResult, TDataEncodingType dataType) {
        TApiResult apiResult = new TApiResult();
        apiResult.setStatus(_apiResult.status).setMessage(_apiResult.message);
        if (dataType == null) {
            dataType = TDataEncodingType.JSON_STRING;
        }
        apiResult.setDataType(dataType);
        apiResult
                .setResultData(ThriftApiUtils.encodeFromJson(dataType, _apiResult.getDataAsJson()));
        apiResult.setDebugData(
                ThriftApiUtils.encodeFromJson(dataType, _apiResult.getDebugDataAsJson()));
        return apiResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() throws TException {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TApiResult check(TApiAuth apiAuth) throws TException {
        long t = System.currentTimeMillis();
        return doResponse(ApiResult.resultOk()
                .clone(MapUtils.createMap("t", t, "d", System.currentTimeMillis() - t)), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TApiResult callApi(TApiAuth _apiAuth, String _apiName, TApiParams _apiParams)
            throws TException {
        long t = System.currentTimeMillis();
        try {
            ApiParams apiParams = parseParams(_apiParams);
            ApiContext apiContext = ApiContext
                    .newContext(AppConstants.API_GATEWAY_THRIFT, _apiName);
            ApiAuth apiAuth = buildAuth(_apiAuth);
            ApiResult apiResult = getApiDispatcher().callApi(apiContext, apiAuth, apiParams);
            return doResponse(
                    apiResult != null ? apiResult : ApiResult.DEFAULT_RESULT_UNKNOWN_ERROR,
                    _apiParams.expectedReturnDataType != null
                            ? _apiParams.expectedReturnDataType
                            : _apiParams.dataType);
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
            return doResponse(new ApiResult(ApiResult.STATUS_ERROR_SERVER, e.getMessage())
                            .setDebugData(MapUtils.createMap("t", t, "d", System.currentTimeMillis() - t)),
                    null);
        }
    }

    private ApiDispatcher apiDispatcher;

    private ApiDispatcher getApiDispatcher() {
        if (apiDispatcher == null) {
            synchronized (this) {
                if (apiDispatcher == null) {
                    apiDispatcher = registry.get().getApiDispatcher();
                }
            }
        }
        return apiDispatcher;
    }

}
