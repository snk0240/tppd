package Dados;

import java.io.Serializable;

public class Ficheiro implements Serializable {
    private static long serialVersionUID=1L;

    private String username;
    private long tamanho;
    private String nome;

    public Ficheiro(){
    }

    public Ficheiro(String user, String caminho, long tam, int t, String n){
        this.username=user;//username do utilizador que enviou o ficheiro
        this.tamanho=tam;//tamanho em bytes do ficheiro
        this.nome=n;//nome do ficheiro
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTamanho() {
        return tamanho;
    }

    public void setTamanho(long tamanho) {
        this.tamanho = tamanho;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
