package org.rubilnik.room_service;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@ComponentScan("org.rubilnik.room_service")
@EnableWebSocket
@EnableAspectJAutoProxy
public class WSConfiguration implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(new WSBinaryHandler(), "/ws").setAllowedOrigins("*")
                .addInterceptors(new WSInterceptor());
    }

}