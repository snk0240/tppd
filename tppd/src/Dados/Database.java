package Dados;

import java.io.Serializable;
import java.util.*;

public class Database implements Serializable {
    private static long serialVersionUID=1L;

    private List<String> users;
    private Map<String,List<Canal>> canais;
    private Map<String,List<Msg>> msgs;
    private Map<String,List<Ficheiro>> ficheiros;

    public Database(){
        users = new ArrayList<>();
        this.canais = new HashMap<>();
        this.msgs = new HashMap<>();
        this.ficheiros = new HashMap<>();
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public Map<String, List<Canal>> getCanais() {
        return canais;
    }

    public void setCanais(Map<String, List<Canal>> canais) {
        this.canais = canais;
    }

    public Map<String, List<Msg>> getMsgs() {
        return msgs;
    }

    public void setMsgs(Map<String, List<Msg>> msgs) {
        this.msgs = msgs;
    }

    public Map<String, List<Ficheiro>> getFicheiros() {
        return ficheiros;
    }

    public void setFicheiros(Map<String, List<Ficheiro>> ficheiros) {
        this.ficheiros = ficheiros;
    }
}

