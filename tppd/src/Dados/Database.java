package Dados;

import java.io.Serializable;
import java.util.*;

public class Database implements Serializable {
    private static long serialVersionUID=1L;

    private List<String> users;
    private Map<String,List<Ficheiro>> uploads;
    private Map<String,List<Ficheiro>>downloads;
    private Map<String,List<Ficheiro>> ficheiros;

    public Database(){
        users = new ArrayList<>();
        uploads = new HashMap<>();
        downloads = new HashMap<>();
        ficheiros = new HashMap<>();
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public Map<String, List<Ficheiro>> getUploads() {
        return uploads;
    }

    public void setUploads(Map<String, List<Ficheiro>> uploads) {
        this.uploads = uploads;
    }

    public Map<String, List<Ficheiro>> getDownloads() {
        return downloads;
    }

    public void setDownloads(Map<String, List<Ficheiro>> downloads) {
        this.downloads = downloads;
    }

    public Map<String, List<Ficheiro>> getFicheiros() {
        return ficheiros;
    }

    public void setFicheiros(Map<String, List<Ficheiro>> ficheiros) {
        this.ficheiros = ficheiros;
    }

    public List<Ficheiro> getUserDownloads(String user){
        if(users.contains(user)){
            return null;
        }
        else{
            return downloads.get(user);
        }
    }

    public List<Ficheiro> getUserUploads(String user){
        if(user.contains(user)){
            return null;
        }
        else{
            return uploads.get(user);
        }
    }

    public List<Ficheiro> getUserFile(String user){
        if(users.contains(user)){
            return null;
        }
        else{
            return ficheiros.get(user);
        }
    }
}

