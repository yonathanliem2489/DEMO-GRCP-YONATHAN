package com.tiket.tix.train.libs.grpc.server.config;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.Resource;
import org.springframework.util.SocketUtils;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties("train.libs.grpc.server")
@Getter
@Setter
public class GRPCServerProperties {
    public static final int DEFAULT_GRPC_PORT = 6565;
    /**
     * gRPC server port
     */
    private Integer port = null;

    private SecurityProperties security;

    private NettyServerProperties nettyServer;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private volatile Integer runningPort = null;

    private int startUpPhase = SmartLifecycle.DEFAULT_PHASE;


    /**
     * In process server name.
     * If  the value is not empty, the embedded in-process server will be created and started.
     */
    private String inProcessServerName;

    /**
     * Enables server reflection using <a href="https://github.com/grpc/grpc-java/blob/master/documentation/server-reflection-tutorial.md">ProtoReflectionService</a>.
     * Available only from gRPC 1.3 or higher.
     */
    private boolean enableReflection = false;

    /**
     * Number of seconds to wait for preexisting calls to finish before shutting down.
     * A negative value is equivalent to an infinite grace period
     */
    private int shutdownGrace = 0;

    public Integer getRunningPort() {
        if (null == runningPort) {
            synchronized (this) {
                if (null == runningPort) {
                    runningPort = Optional.ofNullable(port)
                            .map(p -> 0 == p ? SocketUtils.findAvailableTcpPort() : p)
                            .orElse(DEFAULT_GRPC_PORT);
                }
            }
        }
        return runningPort;

    }

    @Getter
    @Setter
    public static class SecurityProperties {
        private Resource certChain;
        private Resource privateKey;
        private Auth auth;
        @Getter
        @Setter
        public static class Auth {
            private Integer interceptorOrder;
            private boolean failFast = true;
        }
    }

    @Getter
    @Setter
    public static class NettyServerProperties {
        private Integer flowControlWindow;
        private Integer initialFlowControlWindow;

        private Integer maxConcurrentCallsPerConnection;

        private Duration keepAliveTime;
        private Duration keepAliveTimeout;

        private Duration maxConnectionAge;
        private Duration maxConnectionAgeGrace;
        private Duration maxConnectionIdle;
        private Duration permitKeepAliveTime;

        private DataSize maxInboundMessageSize;
        private DataSize maxInboundMetadataSize;

        private Boolean permitKeepAliveWithoutCalls;
        /**
         *  grpc listen address. <P>If configured, takes precedence over {@code grpc.port} property value.</p>
         *  Supported format:
         *  <li>{@code host:port} (if port is less than 1, uses random value)
         *  <li>{@code host:}  (uses default grpc port, 6565 )
         */
        private InetSocketAddress primaryListenAddress;

        private List<InetSocketAddress> additionalListenAddresses;
    }

    @PostConstruct
    public void init(){
        Optional.ofNullable(nettyServer)
                .map(NettyServerProperties::getPrimaryListenAddress)
                .ifPresent(a->port = a.getPort());

    }
}
