package org.rubilnik.room_service;

import java.util.Set;

import org.rubilnik.core.users.Host;
import org.rubilnik.core.users.User;
import org.rubilnik.core.BiMap;
import org.rubilnik.core.users.Player;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WS_BinaryHandler extends BinaryWebSocketHandler {
    static BiMap<WebSocketSession, User> userConnections = new BiMap<WebSocketSession, User>();
    
    static ObjectMapper objectMapper_Json = new ObjectMapper();

    WS_EventHandler eventHandler = new WS_EventHandler();

    static void messageTo_WS(Set<User> users, String message){
        for (User user : users){
            messageToUser_WS(user, message);
        }
    }
    static void messageToUser_WS(User user, String message){
        var text_message = new TextMessage(message);
        try {
            var session = userConnections.get2(user);
            session.sendMessage(text_message);
        } catch (Exception e) {
            // TODO 
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try {
            User user = userConnections.get1(session);
            if (user instanceof Player && user.getId().length()==5) user.clearID(); // if Quest
            if (user instanceof Host){
                for (Player player : user.getRoom().getPlayers()) {
                    userConnections.remove2(player).close();
                }
            }
            var room = user.leaveRoom();
            messageTo_WS(room.getUsers(), WS_ReplyFactory.onLeft(user.getId(), user.getName(), room.getUsers()) );
        } catch (Exception e) { System.out.println(e.getMessage());}

        userConnections.remove1(session);

        super.afterConnectionClosed(session, status);
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WS connection established");
        super.afterConnectionEstablished(session);
    }

    static class EventMessageBody{
        public String event;
        public Object data; // LinkedHashMap // mapper.convertValue(data, SOME.class);
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            var body = parseEventMessageBody(message);
            switch(body.event){
                case "create": eventHandler.create(session, body); break;
                case "join": eventHandler.join(session,body); break;
                case "bark": eventHandler.bark(session); break;
                case "choice": eventHandler.choice(session, body); break;
                case "start": eventHandler.start(session); break;
                case "next": eventHandler.next(session); break;
                case "end": eventHandler.end(session); break;
                case "reveal": eventHandler.reveal(session); break;
                default: throw new WebSocketEventException("Invalid event:"+" '"+body.event+"'."+" Can't handle this event");
            }
        } catch (WebSocketEventException e) { handleWebSocketEventException(session, e); }
    }

    EventMessageBody parseEventMessageBody(TextMessage message) throws WebSocketEventException{
        var payload = message.getPayload();
        System.out.println("Got payload: " + payload);
        System.out.println("");
        try {
            return objectMapper_Json.readValue(payload, EventMessageBody.class);
        } catch (JsonProcessingException e) {
            throw new WebSocketEventException("JsonProcessingException: incorrect json message body");
        }
    }

    void handleWebSocketEventException(WebSocketSession session, WebSocketEventException e){
        try {
            System.err.println(e.getMessage());
            session.sendMessage(new TextMessage(WS_ReplyFactory.onError(e.getMessage())));
        } catch (Exception ex) { 
            System.err.println(ex.getMessage());
        }
    }
}