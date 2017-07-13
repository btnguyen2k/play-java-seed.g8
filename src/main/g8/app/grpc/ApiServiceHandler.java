package grpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.MapUtils;
import com.google.inject.Provider;
import com.google.protobuf.Empty;

import api.ApiAuth;
import api.ApiContext;
import api.ApiDispatcher;
import api.ApiParams;
import api.ApiResult;
import grpc.def.ApiServiceProto.PApiAuth;
import grpc.def.ApiServiceProto.PApiContext;
import grpc.def.ApiServiceProto.PApiParams;
import grpc.def.ApiServiceProto.PApiResult;
import grpc.def.ApiServiceProto.PDataEncodingType;
import grpc.def.PApiServiceGrpc.PApiServiceImplBase;
import io.grpc.stub.StreamObserver;
import modules.registry.IRegistry;
import play.Logger;
import utils.AppConstants;

public class ApiServiceHandler extends PApiServiceImplBase {

    private Provider<IRegistry> registry;

    public ApiServiceHandler(Provider<IRegistry> registry) {
        this.registry = registry;
    }

    private static ApiParams parseParams(PApiParams _apiParams) {
        JsonNode paramNode = GrpcApiUtils.decodeToJson(_apiParams.getDataType() != null
                ? _apiParams.getDataType() : PDataEncodingType.JSON_STRING,
                _apiParams.getParamsData());
        ApiParams apiParams = new ApiParams(paramNode);
        return apiParams;
    }

    private static ApiAuth buildAuth(PApiAuth _apiAuth) {
        return new ApiAuth(_apiAuth.getApiKey(), _apiAuth.getAccessToken());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ping(Empty request, StreamObserver<Empty> responseObserver) {
        Empty result = Empty.getDefaultInstance();
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }

    private static PApiResult doResponse(ApiResult _apiResult, PDataEncodingType dataType) {
        PApiResult.Builder apiResultBuilder = PApiResult.newBuilder();
        apiResultBuilder.setStatus(_apiResult.status).setMessage(_apiResult.message);
        if (dataType == null) {
            dataType = PDataEncodingType.JSON_STRING;
        }
        apiResultBuilder.setDataType(dataType);
        apiResultBuilder
                .setResultData(GrpcApiUtils.encodeFromJson(dataType, _apiResult.getDataAsJson()));
        apiResultBuilder.setDebugData(
                GrpcApiUtils.encodeFromJson(dataType, _apiResult.getDebugDataAsJson()));

        return apiResultBuilder.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check(PApiAuth request, StreamObserver<PApiResult> responseObserver) {
        long t = System.currentTimeMillis();
        PApiResult result = doResponse(ApiResult.resultOk()
                .clone(MapUtils.createMap("t", t, "d", System.currentTimeMillis() - t)), null);
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void callApi(PApiContext request, StreamObserver<PApiResult> responseObserver) {
        long t = System.currentTimeMillis();
        PApiResult result = null;
        try {
            PApiParams _apiParams = request.getApiParams();
            ApiParams apiParams = parseParams(_apiParams);
            ApiContext apiContext = ApiContext.newContext(AppConstants.API_GATEWAY_GRPC,
                    request.getApiName());
            ApiAuth apiAuth = buildAuth(request.getApiAuth());
            ApiResult apiResult = getApiDispatcher().callApi(apiContext, apiAuth, apiParams);
            PDataEncodingType dataType = _apiParams.getExpectedReturnDataType();
            if (dataType == null) {
                dataType = _apiParams.getDataType();
            }
            result = doResponse(apiResult != null ? apiResult : ApiResult.RESULT_UNKNOWN_ERROR,
                    dataType);
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
            result = doResponse(new ApiResult(ApiResult.STATUS_ERROR_SERVER, e.getMessage())
                    .setDebugData(MapUtils.createMap("t", t, "d", System.currentTimeMillis() - t)),
                    null);
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
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
