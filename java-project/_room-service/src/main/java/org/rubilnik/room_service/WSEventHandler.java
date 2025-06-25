package org.rubilnik.room_service;

import org.rubilnik.core.Room;
import org.rubilnik.core.quiz.Quiz;
import org.rubilnik.core.users.Host;
import org.rubilnik.core.users.Player;
import org.rubilnik.core.users.User;
import org.rubilnik.room_service.WSBinaryHandler.EventMessageBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.socket.WebSocketSession;

import java.util.NoSuchElementException;


public class WSEventHandler {

    static class CreateRequest_Data{
        public Quiz quiz;
        public Long questionId;
    }
    void create(WebSocketSession session, EventMessageBody body) throws WebSocketEventException{
        String userSession = (String) session.getAttributes().get("rubilnik-user-session");
        var user = App.resolveUser(userSession);
        var bodyData = WSBinaryHandler.objectMapper_Json.convertValue(body.data, CreateRequest_Data.class);

        var host = user.createRoom(bodyData.quiz);
        WSBinaryHandler.userConnections.put(session, host);
        var msg = WSReplyFactory.onCreate(host.getRoom().getUsers());
        WSBinaryHandler.messageToUser_WS(host, msg);
    }

    void reveal(WebSocketSession session) throws WebSocketEventException{
        var user = WSBinaryHandler.userConnections.get1(session);
        if (!(user instanceof Host)) throw new WebSocketEventException("User is not a Host");
        var host = (Host)user;
        var question = host.getRoom().getCurrentQuestion();
        if (question==null) throw new WebSocketEventException("No question is currently available");
        var revealedChoices = question.getChoices();
        WSBinaryHandler.messageTo_WS(host.getRoom().getUsers(), WSReplyFactory.onReveal(revealedChoices));
    }

    

    static class JoinRequest_Data {
        public String name;
        public String roomId;
//        public UserValidationInfo validation;
    }
    void join(WebSocketSession session, EventMessageBody body) throws WebSocketEventException{
        var data = WSBinaryHandler.objectMapper_Json.convertValue(body.data,JoinRequest_Data.class);
        var room = Room.getRoom(data.roomId);
        if (room==null) throw new WebSocketEventException("No room available with such id");
        User player;

        try { // try user validation
            String userSession = (String) session.getAttributes().get("rubilnik-user-session");
            var user = App.resolveUser(userSession);
            if (user!=null){
                player = user.joinRoom(room);
            } else { // else join as guest
                player = new Player(data.name,room);
            }

        } catch (HttpStatusCodeException e){ // else join as guest // TODO
            System.out.println(e.getMessage());
            player = new Player(data.name,room);
        }
        WSBinaryHandler.userConnections.put(session, player);
        
        // message to users
        WSBinaryHandler.messageToUser_WS(player, WSReplyFactory.onJoin(player.getId(), player.getName(), room.getUsers()));
        var otherUsers = room.getUsers();
        otherUsers.remove(player);
        WSBinaryHandler.messageTo_WS(otherUsers, WSReplyFactory.onJoined(player.getId(), player.getName(), room.getUsers()));
    }


    
    void bark(WebSocketSession session) throws WebSocketEventException{
        User user = null;
        user = WSBinaryHandler.userConnections.get1(session);
        var room = user.getRoom();
        if (room == null) throw new WebSocketEventException("User doesn't have a Room");
        WSBinaryHandler.messageTo_WS( room.getUsers(), WSReplyFactory.onBark(user.getId(), user.getName()) );
    }

    private Host _start_next_end_shared(WebSocketSession session) throws WebSocketEventException{
        var user = WSBinaryHandler.userConnections.get1(session);
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
        var bodyData = WSBinaryHandler.objectMapper_Json.convertValue(body.data, StartRequest_Data.class);
        var host = _start_next_end_shared(session);
        var room = host.getRoom();
        var question = host.startRoom(bodyData.questionId);
        WSBinaryHandler.messageTo_WS(room.getUsers(), WSReplyFactory.onStart(question, room.getQuiz().getQuestions().indexOf(room.getCurrentQuestion()), room.getQuiz().getQuestions().size()) );
    }
    static class NextRequest_Data {
        public long questionId;
    }
    void next(WebSocketSession session, EventMessageBody body) throws WebSocketEventException{
        var bodyData = WSBinaryHandler.objectMapper_Json.convertValue(body.data, NextRequest_Data.class);
        var host = _start_next_end_shared(session);
        var room = host.getRoom();
        var question = host.nextQuestion(bodyData.questionId);
        WSBinaryHandler.messageTo_WS(room.getUsers(), WSReplyFactory.onNext(question, room.getQuiz().getQuestions().indexOf(room.getCurrentQuestion()), room.getQuiz().getQuestions().size()));
    }
    void end(WebSocketSession session) throws WebSocketEventException{
        var host = _start_next_end_shared(session);
        var room = host.getRoom();
        var scores = host.endRoom();
        WSBinaryHandler.messageTo_WS(room.getUsers(), WSReplyFactory.onEnd(scores));
    }

    static class ChoiceRequest_Data{
        public int questionId, choiceInd;
    }
    void choice(WebSocketSession session, EventMessageBody body) throws WebSocketEventException{
        var data = WSBinaryHandler.objectMapper_Json.convertValue(body.data, ChoiceRequest_Data.class);
        var user = WSBinaryHandler.userConnections.get1(session);
        var room = user.getRoom();
        if (!(user instanceof Player)) throw new WebSocketEventException("User is not a Player");

        var player = (Player) user;
//        var question = room.getQuiz().getQuestions().get(data.questionId);
        var question = room.getQuiz().getQuestions().stream().filter(q->q.getId()==data.questionId).findAny().orElseThrow(()->new NoSuchElementException("Couldn't find question with such id"));
        player.choose(question, data.choiceInd);
        
        WSBinaryHandler.messageToUser_WS(room.getHost(), WSReplyFactory.onChoice(user.getId(), user.getEmail(), data.questionId, data.choiceInd));
    }
}