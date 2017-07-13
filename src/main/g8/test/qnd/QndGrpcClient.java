package qnd;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.github.ddth.commons.utils.MapUtils;
import com.google.protobuf.Empty;

import grpc.GrpcApiUtils;
import grpc.def.ApiServiceProto.PApiAuth;
import grpc.def.ApiServiceProto.PApiContext;
import grpc.def.ApiServiceProto.PApiParams;
import grpc.def.ApiServiceProto.PApiResult;
import grpc.def.ApiServiceProto.PDataEncodingType;
import grpc.def.PApiServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import play.libs.Json;

public class QndGrpcClient {

    private static String toString(PApiResult result) {
        System.out.println("=== Size: " + result.toByteArray().length);
        ToStringBuilder tsb = new ToStringBuilder(result, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("status", result.getStatus());
        tsb.append("message", result.getMessage());
        tsb.append("type", result.getDataType());
        tsb.append("data", GrpcApiUtils.decodeToJson(result.getDataType(), result.getResultData()));
        tsb.append("debug", GrpcApiUtils.decodeToJson(result.getDataType(), result.getDebugData()));
        return tsb.toString();
    }

    private static void testClientBlocking() throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", 9095)
                // Channels are secure by default (via SSL/TLS). For the example
                // we disable TLS to avoid
                // needing certificates.
                .usePlaintext(true).build();

        try {
            PApiServiceGrpc.PApiServiceBlockingStub stub = PApiServiceGrpc.newBlockingStub(channel);
            {
                Empty result = stub.ping(Empty.getDefaultInstance());
                System.err.println("ping: " + result);
            }
            {
                PApiAuth apiAuth = PApiAuth.newBuilder().setApiKey("apiKey")
                        .setAccessToken("accessToken").build();
                PApiResult result = stub.check(apiAuth);
                System.err.println("check: " + toString(result));
            }
            {
                PApiAuth apiAuth = PApiAuth.newBuilder().setApiKey("apiKey")
                        .setAccessToken("accessToken").build();
                Map<Object, Object> data = MapUtils.createMap("t", System.currentTimeMillis(), "n",
                        "Thanh Nguyen", "e", "btnguyen2k@gmail.com", "system",
                        System.getProperties(), "env", System.getenv());
                PApiParams apiParams = PApiParams.newBuilder()
                        .setParamsData(GrpcApiUtils.encodeFromJson(null, Json.toJson(data)))
                        .setExpectedReturnDataType(PDataEncodingType.JSON_STRING).build();
                PApiContext context = PApiContext.newBuilder().setApiAuth(apiAuth)
                        .setApiName("echo").setApiParams(apiParams).build();
                PApiResult result = stub.callApi(context);
                System.err.println("callApi(echo): " + toString(result));
            }
            {
                PApiAuth apiAuth = PApiAuth.newBuilder().setApiKey("apiKey")
                        .setAccessToken("accessToken").build();
                Map<Object, Object> data = MapUtils.createMap("t", System.currentTimeMillis(), "n",
                        "Thanh Nguyen", "e", "btnguyen2k@gmail.com", "system",
                        System.getProperties(), "env", System.getenv());
                PApiParams apiParams = PApiParams.newBuilder()
                        .setParamsData(GrpcApiUtils.encodeFromJson(null, Json.toJson(data)))
                        .setExpectedReturnDataType(PDataEncodingType.JSON_GZIP).build();
                PApiContext context = PApiContext.newBuilder().setApiAuth(apiAuth)
                        .setApiName("echo").setApiParams(apiParams).build();
                PApiResult result = stub.callApi(context);
                System.err.println("callApi(echo): " + toString(result));
            }
        } finally {
            channel.shutdown();
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            testClientBlocking();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
