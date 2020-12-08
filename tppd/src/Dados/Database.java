package Dados;

import java.io.Serializable;
import java.util.*;

public class Database implements Serializable {
    private static long serialVersionUID=1L;

    private List<String> users;

    public Database(){
        users = new ArrayList<>();
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

}

