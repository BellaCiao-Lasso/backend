package com.restaurante.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Slf4j
@Configuration
public class H2ServerConfig {

    @Bean(destroyMethod = "stop")
    public Server h2TcpServer() {
        try {
            Server server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092", "-ifNotExists");
            server.start();
            log.info("H2 TCP Server iniciado exitosamente en puerto 9092");
            return server;
        } catch (SQLException e) {
            log.warn("H2 TCP Server en puerto 9092 no se inició (posiblemente ya está en ejecución): {}", e.getMessage());
            return null;
        }
    }
}
