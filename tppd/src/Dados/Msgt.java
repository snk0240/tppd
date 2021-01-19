package Dados;

import java.io.Serializable;

public class Msgt implements Serializable {
    private static long serialVersionUID = 1L;

    private String texto;
    private String envia;
    private int id_channel;
    private int id_ficheiro;
    private String username;

    public Msgt(){
    }

    public Msgt(String texto, String envia, String recebe, int id_1, int id_2, String user){
        this.texto=texto;//texto com mensagem
        this.envia=envia;//ip do que enviou
        this.id_channel=id_1;//-1 se for publico, id do canal se for para um canal
        this.id_ficheiro=id_2;//-1 se nao for ficheiro, id do ficheiro se for ficheiro
        this.username=user;//username que enviou a mensagem
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getEnvia() {
        return envia;
    }

    public void setEnvia(String envia) {
        this.envia = envia;
    }

    public int getId_channel() {
        return id_channel;
    }

    public void setId_channel(int id_channel) {
        this.id_channel = id_channel;
    }

    public int getId_ficheiro() {
        return id_ficheiro;
    }

    public void setId_ficheiro(int id_ficheiro) {
        this.id_ficheiro = id_ficheiro;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
