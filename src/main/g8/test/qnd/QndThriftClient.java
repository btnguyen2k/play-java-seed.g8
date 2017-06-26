package qnd;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import thrift.def.TApiAuth;
import thrift.def.TApiParams;
import thrift.def.TApiService;

public class QndThriftClient {

    private static void testClient() throws Exception {
        TTransport transport = new TFramedTransport(new TSocket("127.0.0.1", 9090));
        transport.open();

        TProtocol protocol = new TCompactProtocol(transport);
        TApiService.Client client = new TApiService.Client(protocol);

        client.ping();

        TApiAuth apiAuth = new TApiAuth();
        apiAuth.setApiKey("apiKey").setAccessToken("accessToken");
        System.out.println(client.check(apiAuth));
        System.out.println(client.callApi(apiAuth, "echo", new TApiParams()));

        transport.close();
    }

    private static void testClientSsl() throws Exception {
        TSSLTransportParameters params = new TSSLTransportParameters();
        params.setTrustStore("conf/keys/client.truststore", "pl2yt3mpl2t3");
        TTransport transport = TSSLTransportFactory.getClientSocket("127.0.0.1", 9093, 10000, params);
        TProtocol protocol = new TCompactProtocol(transport);
        TApiService.Client client = new TApiService.Client(protocol);

        client.ping();

        TApiAuth apiAuth = new TApiAuth();
        apiAuth.setApiKey("apiKey").setAccessToken("accessToken");
        System.out.println(client.check(apiAuth));
        System.out.println(client.callApi(apiAuth, "echo", new TApiParams()));
    }

    public static void main(String[] args) throws Exception {
        try {
            testClient();
            testClientSsl();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
