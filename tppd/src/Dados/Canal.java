package Dados;

import java.io.Serializable;

public class Canal implements Serializable {
    private static long serialVersionUID=1L;

    private String nome;
    private String descricao;
    private String username;
    private String password;

    public Canal(){
    }

    public Canal(String nome, String descricao, String user, String password){
        this.nome=nome;//nome do canal
        this.descricao=descricao;//descricao do canal
        this.username=user;//username do utilizador que criou o canal
        this.password=password;//password do canal
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
