package qnd;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.commons.utils.ThriftUtils;

import play.libs.Json;
import thrift.ThriftApiUtils;
import thrift.def.TApiAuth;
import thrift.def.TApiParams;
import thrift.def.TApiResult;
import thrift.def.TApiService;
import thrift.def.TDataEncodingType;

public class QndThriftClient {

    private static String toString(TApiResult result) throws TException {
        System.out.println("=== Size: " + ThriftUtils.toBytes(result).length);
        ToStringBuilder tsb = new ToStringBuilder(result, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("status", result.status);
        tsb.append("message", result.message);
        tsb.append("type", result.dataType);
        tsb.append("data", ThriftApiUtils.decodeToJson(result.dataType, result.getResultData()));
        tsb.append("debug", ThriftApiUtils.decodeToJson(result.dataType, result.getDebugData()));
        return tsb.toString();
    }

    private static void testClient(TApiService.Client client) throws TException {
        client.ping();

        TApiAuth apiAuth = new TApiAuth();
        apiAuth.setApiKey("apiKey").setAccessToken("accessToken");
        {
            System.out.println("check: " + toString(client.check(apiAuth)));
        }
        {
            Map<Object, Object> data = MapUtils.createMap("t", System.currentTimeMillis(), "n",
                    "Thanh Nguyen", "e", "btnguyen2k@gmail.com", "system", System.getProperties(),
                    "env", System.getenv());
            TApiParams apiParams = new TApiParams()
                    .setParamsData(ThriftApiUtils.encodeFromJson(null, Json.toJson(data)))
                    .setExpectedReturnDataType(TDataEncodingType.JSON_STRING);
            System.out.println(
                    "callApi(echo): " + toString(client.callApi(apiAuth, "echo", apiParams)));
        }
        {
            Map<Object, Object> data = MapUtils.createMap("t", System.currentTimeMillis(), "n",
                    "Thanh Nguyen", "e", "btnguyen2k@gmail.com", "system", System.getProperties(),
                    "env", System.getenv());
            TApiParams apiParams = new TApiParams()
                    .setParamsData(ThriftApiUtils.encodeFromJson(null, Json.toJson(data)))
                    .setExpectedReturnDataType(TDataEncodingType.JSON_GZIP);
            System.out.println(
                    "callApi(echo): " + toString(client.callApi(apiAuth, "echo", apiParams)));
        }
    }

    private static void testClient() throws Exception {
        TTransport transport = new TFramedTransport(new TSocket("127.0.0.1", 9090));
        try {
            transport.open();
            TProtocol protocol = new TCompactProtocol(transport);
            TApiService.Client client = new TApiService.Client(protocol);
            testClient(client);
        } finally {
            transport.close();
        }
    }

    private static void testClientSsl() throws Exception {
        TSSLTransportParameters params = new TSSLTransportParameters();
        params.setTrustStore("conf/keys/client.truststore", "pl2yt3mpl2t3");
        TTransport transport = TSSLTransportFactory.getClientSocket("127.0.0.1", 9093, 10000,
                params);
        try {
            transport.open();
            TProtocol protocol = new TCompactProtocol(transport);
            TApiService.Client client = new TApiService.Client(protocol);
            testClient(client);
        } finally {
            transport.close();
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            testClient();
            testClientSsl();
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            // e.printStackTrace();
        }
    }
}
