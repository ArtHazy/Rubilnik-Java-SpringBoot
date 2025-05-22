package org.rubilnik.room_service;

import org.rubilnik.core.Room;
import org.rubilnik.core.quiz.Quiz;
import org.rubilnik.core.users.Host;
import org.rubilnik.core.users.Player;
import org.rubilnik.core.users.User;
import org.rubilnik.room_service.App.UserValidationInfo;
import org.rubilnik.room_service.WS_BinaryHandler.EventMessageBody;
import org.springframework.web.socket.WebSocketSession;


public class WS_EventHandler {

    void reveal(WebSocketSession session) throws WebSocketEventException{
        var user = WS_BinaryHandler.userConnections.get1(session);
        if (!(user instanceof Host)) throw new WebSocketEventException("User is not a Host");
        var host = (Host)user;
        var question = host.getRoom().getCurrentQuestion();
        if (question==null) throw new WebSocketEventException("No question is currently available");
        var revealedChoices = question.getChoices();
        WS_BinaryHandler.messageTo_WS(host.getRoom().getUsers(), WS_ReplyFactory.onReveal(revealedChoices));
    }

    

    static class JoinRequest_Data {
        public String name;
        public String roomId;
        public UserValidationInfo validation;
    }
    void join(WebSocketSession session, EventMessageBody body) throws WebSocketEventException{
        var data = WS_BinaryHandler.objectMapper_Json.convertValue(body.data,JoinRequest_Data.class);
        var room = Room.getRoom(data.roomId);
        if (room==null) throw new WebSocketEventException("No room available with such id");
        User player;
        if (App.validateUser(data.validation)){
            player = App.getUser(data.validation).joinRoom(room);
        } else {
            player = new Player(data.name,room);
        }
        WS_BinaryHandler.userConnections.put(session, player);
        
        // message to users
        WS_BinaryHandler.messageToUser_WS(player, WS_ReplyFactory.onJoin(player.getId(), player.getName(), room.getUsers()));
        var otherUsers = room.getUsers();
        otherUsers.remove(player);
        WS_BinaryHandler.messageTo_WS(otherUsers, WS_ReplyFactory.onJoined(player.getId(), player.getName(), room.getUsers()));
    }

    static class CreateRequest_Data{
        public UserValidationInfo validation;
        public Quiz quiz;
        public Long questionId;
    }
    void create(WebSocketSession session, EventMessageBody body) throws WebSocketEventException{
        var bodyData = WS_BinaryHandler.objectMapper_Json.convertValue(body.data, CreateRequest_Data.class);
        if ( !App.validateUser(bodyData.validation) ) throw new WebSocketEventException("User validation failed");
        var user = App.getUser(bodyData.validation);
        var host = user.createRoom(bodyData.quiz);
        WS_BinaryHandler.userConnections.put(session, host);
        var msg = WS_ReplyFactory.onCreate(host.getRoom().getUsers());
        WS_BinaryHandler.messageToUser_WS(host, msg);
    }
    
    void bark(WebSocketSession session) throws WebSocketEventException{
        User user = null;
        user = WS_BinaryHandler.userConnections.get1(session);
        var room = user.getRoom();
        if (room == null) throw new WebSocketEventException("User doesn't have a Room");
        WS_BinaryHandler.messageTo_WS( room.getUsers(), WS_ReplyFactory.onBark(user.getId(), user.getName()) );
    }

    private Host _start_next_end_shared(WebSocketSession session) throws WebSocketEventException{
        var user = WS_BinaryHandler.userConnections.get1(session);
        if (!(user instanceof Host)) throw new WebSocketEventException("User is not a Host");
        var host = (Host) user;
        var room = host.getRoom();
        if (room == null) throw new WebSocketEventException("User doesn't have a Room");
        return host;
    }

    static class StartRequest_Data {
        public long questionId;
    }
    void start(WebSocketSession session, EventMessageBody body) throws WebSocketEventException{
        var bodyData = WS_BinaryHandler.objectMapper_Json.convertValue(body.data, StartRequest_Data.class);
        var host = _start_next_end_shared(session);
        var room = host.getRoom();
        var question = host.startRoom(bodyData.questionId);
        WS_BinaryHandler.messageTo_WS(room.getUsers(), WS_ReplyFactory.onStart(question, room.getQuiz().getQuestions().indexOf(room.getCurrentQuestion()), room.getQuiz().getQuestions().size()) );
    }
    static class NextRequest_Data {
        public long questionId;
    }
    void next(WebSocketSession session, EventMessageBody body) throws WebSocketEventException{
        var bodyData = WS_BinaryHandler.objectMapper_Json.convertValue(body.data, NextRequest_Data.class);
        var host = _start_next_end_shared(session);
        var room = host.getRoom();
        var question = host.nextQuestion(bodyData.questionId);
        WS_BinaryHandler.messageTo_WS(room.getUsers(), WS_ReplyFactory.onNext(question, room.getQuiz().getQuestions().indexOf(room.getCurrentQuestion()), room.getQuiz().getQuestions().size()));  
    }
    void end(WebSocketSession session) throws WebSocketEventException{
        var host = _start_next_end_shared(session);
        var room = host.getRoom();
        var scores = host.endRoom();
        WS_BinaryHandler.messageTo_WS(room.getUsers(), WS_ReplyFactory.onEnd(scores));
    }

    static class ChoiceRequest_Data{
        public int questionInd, choiceInd;
    }
    void choice(WebSocketSession session, EventMessageBody body) throws WebSocketEventException{
        var data = WS_BinaryHandler.objectMapper_Json.convertValue(body.data, ChoiceRequest_Data.class);
        var user = WS_BinaryHandler.userConnections.get1(session);
        var room = user.getRoom();
        if (!(user instanceof Player)) throw new WebSocketEventException("User is not a Player");

        var player = (Player) user;
        var question = room.getQuiz().getQuestions().get(data.questionInd);
        player.choose(question, data.choiceInd);
        
        WS_BinaryHandler.messageToUser_WS(room.getHost(), WS_ReplyFactory.onChoice(user.getId(), user.getEmail(), data.questionInd, data.choiceInd));
    }
}