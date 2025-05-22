package org.rubilnik.core.users;

import java.util.List;
import java.util.Map.Entry;

import org.rubilnik.core.quiz.Question;


public class Host extends User {
    public Host(User user) {super(user);}


    public Question nextQuestion(long id){
        checkRoomForNull();
        try {
            return room.next(id);
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }
    public Question startRoom(long id) throws RuntimeException{
        checkRoomForNull();
        return room.start(id);
    }
    public List<Entry<Player, Integer>> endRoom() throws RuntimeException{
        checkRoomForNull();
        return room.end();
    }
}