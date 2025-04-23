package org.rubilnik.room_service.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Aspect
@Component
public class WS_EventHandler_LoggerAspect {

    // @Autowired
    // WebSocketSession session; 
    // @Autowired
    // EventMessageBody body;

    // @Before("execution(* org.rubilnik.room_service.WS_EventHandler.*(..)) && args(session, body)")
    // public void log(WebSocketSession session, EventMessageBody body){
    //     System.out.println(body.event+" event received from session:"+session.getId());
    // }
    // @Before("execution(* org.rubilnik.room_service.WS_EventHandler.*(..)) && args(session)")
    // public void log(WebSocketSession session){
    //     System.out.println(body.event+" event received from session:"+session.getId());
    // }

    @Before("execution(* org.rubilnik.room_service.WS_EventHandler.*(..)) && args(session,..)")
    public void log(JoinPoint joinPoint, WebSocketSession session) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println(methodName+" event received from session: "+session.getId());
    }
}