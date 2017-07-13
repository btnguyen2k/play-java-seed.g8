package grpc.def;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.5.0-SNAPSHOT)",
    comments = "Source: api_service.proto")
public final class PApiServiceGrpc {

  private PApiServiceGrpc() {}

  public static final String SERVICE_NAME = "PApiService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> METHOD_PING =
      io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.google.protobuf.Empty>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "PApiService", "ping"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.google.protobuf.Empty.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.google.protobuf.Empty.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<grpc.def.ApiServiceProto.PApiAuth,
      grpc.def.ApiServiceProto.PApiResult> METHOD_CHECK =
      io.grpc.MethodDescriptor.<grpc.def.ApiServiceProto.PApiAuth, grpc.def.ApiServiceProto.PApiResult>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "PApiService", "check"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              grpc.def.ApiServiceProto.PApiAuth.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              grpc.def.ApiServiceProto.PApiResult.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<grpc.def.ApiServiceProto.PApiContext,
      grpc.def.ApiServiceProto.PApiResult> METHOD_CALL_API =
      io.grpc.MethodDescriptor.<grpc.def.ApiServiceProto.PApiContext, grpc.def.ApiServiceProto.PApiResult>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "PApiService", "callApi"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              grpc.def.ApiServiceProto.PApiContext.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              grpc.def.ApiServiceProto.PApiResult.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PApiServiceStub newStub(io.grpc.Channel channel) {
    return new PApiServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PApiServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new PApiServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PApiServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new PApiServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class PApiServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void ping(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_PING, responseObserver);
    }

    /**
     */
    public void check(grpc.def.ApiServiceProto.PApiAuth request,
        io.grpc.stub.StreamObserver<grpc.def.ApiServiceProto.PApiResult> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CHECK, responseObserver);
    }

    /**
     */
    public void callApi(grpc.def.ApiServiceProto.PApiContext request,
        io.grpc.stub.StreamObserver<grpc.def.ApiServiceProto.PApiResult> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CALL_API, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_PING,
            asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                com.google.protobuf.Empty>(
                  this, METHODID_PING)))
          .addMethod(
            METHOD_CHECK,
            asyncUnaryCall(
              new MethodHandlers<
                grpc.def.ApiServiceProto.PApiAuth,
                grpc.def.ApiServiceProto.PApiResult>(
                  this, METHODID_CHECK)))
          .addMethod(
            METHOD_CALL_API,
            asyncUnaryCall(
              new MethodHandlers<
                grpc.def.ApiServiceProto.PApiContext,
                grpc.def.ApiServiceProto.PApiResult>(
                  this, METHODID_CALL_API)))
          .build();
    }
  }

  /**
   */
  public static final class PApiServiceStub extends io.grpc.stub.AbstractStub<PApiServiceStub> {
    private PApiServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PApiServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PApiServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PApiServiceStub(channel, callOptions);
    }

    /**
     */
    public void ping(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_PING, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void check(grpc.def.ApiServiceProto.PApiAuth request,
        io.grpc.stub.StreamObserver<grpc.def.ApiServiceProto.PApiResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CHECK, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void callApi(grpc.def.ApiServiceProto.PApiContext request,
        io.grpc.stub.StreamObserver<grpc.def.ApiServiceProto.PApiResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CALL_API, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class PApiServiceBlockingStub extends io.grpc.stub.AbstractStub<PApiServiceBlockingStub> {
    private PApiServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PApiServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PApiServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PApiServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty ping(com.google.protobuf.Empty request) {
      return blockingUnaryCall(
          getChannel(), METHOD_PING, getCallOptions(), request);
    }

    /**
     */
    public grpc.def.ApiServiceProto.PApiResult check(grpc.def.ApiServiceProto.PApiAuth request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CHECK, getCallOptions(), request);
    }

    /**
     */
    public grpc.def.ApiServiceProto.PApiResult callApi(grpc.def.ApiServiceProto.PApiContext request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CALL_API, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class PApiServiceFutureStub extends io.grpc.stub.AbstractStub<PApiServiceFutureStub> {
    private PApiServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PApiServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PApiServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PApiServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> ping(
        com.google.protobuf.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_PING, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.def.ApiServiceProto.PApiResult> check(
        grpc.def.ApiServiceProto.PApiAuth request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CHECK, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.def.ApiServiceProto.PApiResult> callApi(
        grpc.def.ApiServiceProto.PApiContext request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CALL_API, getCallOptions()), request);
    }
  }

  private static final int METHODID_PING = 0;
  private static final int METHODID_CHECK = 1;
  private static final int METHODID_CALL_API = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PApiServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PApiServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PING:
          serviceImpl.ping((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_CHECK:
          serviceImpl.check((grpc.def.ApiServiceProto.PApiAuth) request,
              (io.grpc.stub.StreamObserver<grpc.def.ApiServiceProto.PApiResult>) responseObserver);
          break;
        case METHODID_CALL_API:
          serviceImpl.callApi((grpc.def.ApiServiceProto.PApiContext) request,
              (io.grpc.stub.StreamObserver<grpc.def.ApiServiceProto.PApiResult>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class PApiServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return grpc.def.ApiServiceProto.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PApiServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PApiServiceDescriptorSupplier())
              .addMethod(METHOD_PING)
              .addMethod(METHOD_CHECK)
              .addMethod(METHOD_CALL_API)
              .build();
        }
      }
    }
    return result;
  }
}
