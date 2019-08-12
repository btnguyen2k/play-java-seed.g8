package utils;

/**
 * Application's common constants.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class AppConstants {
    public final static String API_HTTP_HEADER_APP_ID = "X-App-Id";
    public final static String API_HTTP_HEADER_ACCESS_TOKEN = "X-Access-Token";
    public final static String API_CONF_HTTP_MAX_BODY_SIZE = "api.http.maxBodySize";
    public final static int DEFAULT_API_CONF_HTTP_MAX_BODY_SIZE = 16 * 1024;

    public final static String API_CONF_GRPC_MAX_INBOUND_METADATA_SIZE = "api.grpc.maxInboundMetadataSize";
    public final static int DEFAULT_API_CONF_GRPC_MAX_INBOUND_METADATA_SIZE = 8 * 1024;
    public final static String API_CONF_GRPC_MAX_MSG_SIZE = "api.grpc.maxMessageSize";
    public final static int DEFAULT_API_CONF_GRPC_MAX_MSG_SIZE = 64 * 1024;

    public final static String API_CONF_THRIFT_CLIENT_TIMEOUT = "api.thrift.clientTimeout";
    public final static int DEFAULT_API_CONF_THRIFT_CLIENT_TIMEOUT = 10000;
    public final static String API_CONF_THRIFT_MAX_FRAME_SIZE = "api.thrift.maxFrameSize";
    public final static int DEFAULT_API_CONF_THRIFT_MAX_FRAME_SIZE = 64 * 1024;
    public final static String API_CONF_THRIFT_MAX_READ_BUFFER_SIZE = "api.thrift.maxReadBufferSize";
    public final static int DEFAULT_API_CONF_THRIFT_MAX_READ_BUFFER_SIZE = 16 * 1024 * 1024;
    public final static String API_CONF_THRIFT_NUM_SELECTOR_THREADS = "api.thrift.selectorThreads";
    public final static int DEFAULT_API_CONF_THRIFT_NUM_SELECTOR_THREADS = 0;
    public final static String API_CONF_THRIFT_NUM_WORKER_THREADS = "api.thrift.workerThreads";
    public final static int DEFAULT_API_CONF_THRIFT_NUM_WORKER_THREADS = 0;
    public final static String API_CONF_THRIFT_QUEUE_SIZE_PER_THREAD = "api.thrift.queueSizePerThread";
    public final static int DEFAULT_API_CONF_THRIFT_QUEUE_SIZE_PER_THREAD = 1000;
    public final static String API_CONF_THRIFT_COMPACT_MODE = "api.thrift.compactMode";
    public final static boolean DEFAULT_API_CONF_THRIFT_COMPACT_MODE = true;

    public final static String THREAD_POOL_DEFAULT = "default-dispatcher";
    public final static String THREAD_POOL_DB = "db-dispatcher";
    public final static String THREAD_POOL_WORKER = "worker-dispatcher";

    public final static String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
    public final static String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";

    public final static String API_GATEWAY_WEB = "WEB";
    public final static String API_GATEWAY_THRIFT = "THRIFT";
    public final static String API_GATEWAY_THRIFT_OVER_HTTP = "THRIFT_OVER_HTTP";
    public final static String API_GATEWAY_GRPC = "GRPC";

    public final static String CLUSTER_ROLE_MASTER = "master";
}
