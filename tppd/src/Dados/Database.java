package Dados;

import java.io.Serializable;
import java.util.*;

public class Database implements Serializable {
    private static long serialVersionUID=1L;

    private List<String> users;
    private Map<String,List<Msg>> msgs;

    public Database(){
        this.users = new ArrayList<>();
        this.msgs = new HashMap<>();
    }

    public List<String> getUsers() {
        return this.users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public Map<String, List<Msg>> getMsgs() {
        return this.msgs;
    }

    public void setMsgs(Map<String, List<Msg>> msgs) {
        this.msgs = msgs;
    }

    public List<Msg> getUserMsg(String user){
        if(users.contains(user)){
            return null;
        }
        else{
            return this.msgs.get(user);
        }
    }
}

