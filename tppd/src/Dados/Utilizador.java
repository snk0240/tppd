package Dados;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class Utilizador implements Serializable {
    private static long serialVersionUID = 1L;

    private String nome;//nome do utilizador
    private String username;//username do utilizador
    private String password;//password do utilizador
    private String ip;//endereço do utilizador em formato String
    private int portoUDP;//porto UDP do utilizador
    private int portoTCP;//porto TCP do utilizador
    private boolean ativo;//0 inativo, 1 ativo
    private String imagem;//imagem do utilizador
    private int contador = 1;//id

    public Utilizador(){
        try{
            ip = Inet4Address.getLocalHost().getHostAddress();
        }catch(UnknownHostException e){
            e.printStackTrace();
        }
    }

    public Utilizador(String nome, String user, String pass, String ip, int portoUDP, int portoTCP, boolean ati, String image){
        this.nome=nome;
        this.username=user;
        this.password=pass;
        this.ip=ip;
        this.portoUDP=portoUDP;
        this.portoTCP=portoTCP;
        this.ativo=ati;
        this.imagem=image;
        this.contador++;
    }
    @Override
    public String toString() {
        return "Utilizador{" +
                "nome='" + nome + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", ip='" + ip + '\'' +
                ", portoUDP=" + portoUDP +
                ", portoTCP=" + portoTCP +
                ", ativo=" + ativo +
                ", imagem='" + imagem + '\'' +
                '}';
    }

    public String toDB(){
        return "("+portoTCP+",'"+nome+"','"+username+"','"+password+"','"+ip+"',"+portoUDP+","+portoTCP+","+contador+",'"+imagem+"')";
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
