package org.rubilnik.core.users;

import org.rubilnik.core.IdManager;
import org.rubilnik.core.Room;
import org.rubilnik.core.quiz.Question;

public class Player extends User {
    private static IdManager idManager = new IdManager(5, null);

    public Player(User user){
        super(user);
    }
    public Player(String name, Room room){
        room.joinPlayer(this);
        id = idManager.getFreeId();
        this.name = name;
    }
    @Override
    public void clearID() {
        idManager.deleteId(id);
        id = null;
    }
    public void choose(Question question, int choiceInd) throws RuntimeException{
        checkRoomForNull();
        room.registerPlayerChoice(this, question, choiceInd);
    }
    public void choose(int choiceInd) throws RuntimeException{
        checkRoomForNull();
        room.registerPlayerChoice(this, room.getCurrentQuestion(), choiceInd);
    }
    
}