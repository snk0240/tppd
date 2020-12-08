package Dados;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class Utilizador implements Serializable {
    static final long serialVersionUID = 1L;
    private String nome;//nome do utilizador
    private String username;//username do utilizador
    private String password;//password do utilizador
    private String caminhoReceber;//caminho para onde quer receber o ficheiro
    private String caminhoEnviar;//caminho para onde quer enviar o ficheiro
    private String ip;//endereço do utilizador em formato String
    private int portoUDP;//porto UDP do utilizador
    private int portoTCP;//porto TCP do utilizador

    public Utilizador(){
        try{
            ip = Inet4Address.getLocalHost().getHostAddress();
        }catch(UnknownHostException e){
            e.printStackTrace();
        }
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp(){
        return ip;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCaminhoReceber() {
        return caminhoReceber;
    }

    public void setCaminhoReceber(String caminhoReceber) {
        this.caminhoReceber = caminhoReceber;
    }

    public String getCaminhoEnviar() {
        return caminhoEnviar;
    }

    public void setCaminhoEnviar(String caminhoEnviar) {
        this.caminhoEnviar = caminhoEnviar;
    }

    public int getPortoUDP() {
        return portoUDP;
    }

    public void setPortoUDP(int portoUDP) {
        this.portoUDP = portoUDP;
    }

    public int getPortoTCP() {
        return portoTCP;
    }

    public void setPortoTCP(int portoTCP) {
        this.portoTCP = portoTCP;
    }

    public String toQuery(){
        return "('"+username+"','"+0+"','"+ip+"','"+nome+"','"+password+"','"+caminhoEnviar+"','"+caminhoReceber+"','"+portoTCP+"','"+portoUDP+"','"+1+"')";
    }
}
