package org.rubilnik.core;

import java.util.*;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.rubilnik.core.quiz.*;
import org.rubilnik.core.users.Host;
import org.rubilnik.core.users.Player;
import org.rubilnik.core.users.User;

import java.util.Map.Entry;
import java.io.Serializable;

public class Room implements Serializable{
    // keeps track of the created rooms
    private static Map<String,Room> currentRooms = new HashMap<String,Room>();
    
    private static String STATUS_AWAIT = "await";
    private static String STATUS_PROGRESS = "progress";
    private static String STATUS_COMPLETE = "complete";

    public static Room getRoom(String id){
        return currentRooms.get(id);
    }

    public static String listRooms(){
        var sb = new StringBuilder();
        currentRooms.forEach((id, room)->{
            sb.append(room.toString());
        });
        return sb.toString();
    }

    
    private String id;
    private Host host;
    private Set<Player> players = new HashSet<>();
    private Quiz quiz;
    private String status;
    private Map<Player,Map<Question,Choice>> playersChoices = new HashMap<>();
    private Map<Player,Float> playersScores = new HashMap<>();
    private int currentQuestionIndex=-1;

    public Set<User> getUsers() {
        var users = new HashSet<User>(players);
        users.add(host);
        return users;
    }
    public Set<Player> getPlayers() {
        return players;
    }
    public Host getHost() {
        return host;
    }
    

    @Nullable
    public Question getCurrentQuestion() {
        try {
            return quiz.getQuestions().get(currentQuestionIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    
    public Float getPlayersScore(Player player) {
        return playersScores.get(player);
    }
    public Map<Question, Choice> getPlayersChoices(Player player) {
        return playersChoices.get(player);
    }
    public Quiz getQuiz() {
        return quiz;
    }

    public Room(Host host, Quiz quiz){
        this.id = host.getId();
        this.host = host;

        currentRooms.put(this.id, this);

        host.setRoom(this);
        this.quiz = quiz;
        this.status = STATUS_AWAIT;
    }
    public String getId() {
        return id;
    }

    String printChoices(){
        var sb = new StringBuilder();
        playersChoices.forEach((player, choices)->{
            sb.append(player.toString()).append(" : ").append("\n");
            choices.forEach((question, choice)->{
                sb.append("  ").append(question.getTitle()).append(" : ").append(choice.getTitle()).append(" : ").append(choice.getValue()).append("\n");
            });
        });
        return sb.toString();
    }
    String listScores(){
        var sb = new StringBuilder();
        playersScores.forEach((player, score)->{
            sb.append("  ").append(player.getId()).append(" ").append(player.getName()).append(" : ").append(score).append("\n");
        });
        return sb.toString();
    }

    @Override
    public String toString() {
        return super.toString()+"\n"+
                "id: "+this.id+"\n"+
                "host: "+this.host.toString()+"\n"+
                "status: "+this.status+"\n"+
                "quiz: "+this.quiz.toString()+"\n"+
                "player's choices: "+printChoices()+"\n";
    }

    public void joinPlayer(Player user){
        user.setRoom(this);
        players.add(user);
        if (user instanceof Player && !playersChoices.containsKey(user)){
            playersChoices.put((Player)user, new HashMap<Question,Choice>());
        }
        System.out.println(this.toString());
    }

    public void leaveUser(User xUser){
        xUser.setRoom(null);
        if (xUser instanceof Host){
            status = STATUS_AWAIT;
            currentRooms.remove(this.id);
        } else if (xUser instanceof Player){
            players.remove(xUser);
        }
        System.out.println(this.toString());
    }
    public Question start(long id) throws NoSuchElementException{
        this.status = STATUS_PROGRESS;
        var nextQuestion = this.quiz.getQuestions().stream().filter(question->question.getId()==id ).findAny().orElseThrow(()->new NoSuchElementException("Question with such id wasn't found"));
        this.currentQuestionIndex = 0;
        return nextQuestion;
    }
    public Question next(long id) throws NoSuchElementException{
        var nextQuestion = this.quiz.getQuestions().stream().filter(question->question.getId()==id ).findAny().orElseThrow(()->new NoSuchElementException("Question with such id wasn't found"));
        currentQuestionIndex+=1;
        return nextQuestion;
    }
    public List<Entry<Player, Float>> end(){
        this.status = STATUS_COMPLETE;
        this.currentQuestionIndex = -1;
        return calcScores().toList();
    }

    Stream<Entry<Player, Float>> calcScores(){ // Map<Player,Integer>
        this.playersChoices.forEach((Player player, Map<Question,Choice> choices)->{
            float score = 0;
            for (Map.Entry<Question,Choice> entry : choices.entrySet()){
                score += entry.getValue().getValue();
            }
            this.playersScores.put(player, score);
        });
        return playersScores.entrySet().stream().sorted(Map.Entry.<Player,Float>comparingByValue().reversed());
        // return this.playersScores;
    }

    public void registerPlayerChoice(Player player, Question question, int choiceInd){
        if (status.equals(STATUS_PROGRESS)){
            if (!playersChoices.containsKey(player)) playersChoices.put(player, new HashMap<Question,Choice>());
            playersChoices.get(player).put(question, question.getChoices().get(choiceInd));
        }
    }
}