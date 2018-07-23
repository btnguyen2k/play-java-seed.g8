package qnd;

import com.github.ddth.commons.jsonrpc.HttpJsonRpcClient;
import com.github.ddth.commons.jsonrpc.RequestResponse;
import com.github.ddth.commons.utils.MapUtils;

import java.util.Map;

public class QndApiHttp {

    private final static Map<String, Object> headers = MapUtils
            .createMap("X-App-Id", "test", "X-Access-Token", "test");

    public static void main(String[] args) {
        try (HttpJsonRpcClient client = new HttpJsonRpcClient()) {
            client.init();

            {
                RequestResponse rr = client
                        .doGet("http://localhost:9000/samplesApi/api/info", null, null);
                System.out.println("Response status: " + rr.getResponseStatus());
                System.out.println("RPC status     : " + rr.getRpcStatus());
                System.out.println("Response data  : " + new String(rr.getResponseData()));
            }
            {
                RequestResponse rr = client
                        .doGet("http://localhost:9000/samplesApi/api/info", headers, null);
                System.out.println("Response status: " + rr.getResponseStatus());
                System.out.println("RPC status     : " + rr.getRpcStatus());
                System.out.println("Response data  : " + new String(rr.getResponseData()));
            }
        }
    }
}
