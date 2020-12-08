package Dados;

import java.io.Serializable;

public class Login implements Serializable {
    static final long serialVersionUID=1L;
    private String username;
    private String password;

    public Login(){
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
