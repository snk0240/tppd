package Server;

import Dados.Ficheiro;
import Dados.Utilizador;

import java.net.InetAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InteracaoDatabase {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root123";
    private static final String TIMEZONE = "?serverTimezone=UTC";
    private static String DB_NAME;
    private Connection connection;

    private Statement statement;
    private String query;
    private ResultSet rs;

    public InteracaoDatabase(String ip, int portTCP) {
        this.DB_NAME = "tppd" + portTCP;
        try {
            Class.forName(this.JDBC_DRIVER);
            this.connection = DriverManager.getConnection("jdbc:mysql://" + ip + TIMEZONE, DB_USER, DB_PASS);
            this.statement = this.connection.createStatement();
            this.statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + this.DB_NAME);
            System.out.println("Server Database Created...");
            createT_MSG();
            createT_Ficheiro();
            createT_Canal();
            createT_User();
            System.out.println("Tables Created...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void termina(){
        this.query = "SELECT * FROM " + this.DB_NAME + ".user;";
        try {
            this.statement = this.connection.createStatement();
            this.rs = this.statement.executeQuery(this.query);
            while (this.rs.next()) {
                if (this.rs.getString("ativo").equals(true)) {
                    setDesligado(this.rs.getString("username"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() throws SQLException {
        this.statement.close();
        this.connection.close();
    }

    public boolean isRegistered(String username) {
        this.query = "SELECT * FROM " + this.DB_NAME + ".user;";
        try {
            this.statement = this.connection.createStatement();
            this.rs = this.statement.executeQuery(this.query);
            while (this.rs.next()) {
                if (this.rs.getString("username").equals(username)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createT_Canal() {
        try {
            this.query = "CREATE TABLE IF NOT EXISTS " + this.DB_NAME + ".canal (" +
                    "id INT NOT NULL AUTO_INCREMENT," +
                    "nome VARCHAR(15) DEFAULT NULL," +
                    "descricao VARCHAR(200) DEFAULT NULL," +
                    "username VARCHAR(15) DEFAULT NULL," +
                    "password VARCHAR(15) DEFAULT NULL," +
                    "PRIMARY KEY (id))";
            this.statement = this.connection.createStatement();
            this.statement.executeUpdate(this.query);
            return true;
        } catch (Exception e) {
            System.err.println(e);
        }
        return false;
    }

    public boolean createT_Ficheiro() {
        try {
            this.query = "CREATE TABLE IF NOT EXISTS " + this.DB_NAME + ".ficheiro (" +
                    "id INT NOT NULL AUTO_INCREMENT," +
                    "username VARCHAR(15) DEFAULT NULL," +
                    "caminho VARCHAR(200) DEFAULT NULL," +
                    "tamanho BIGINT DEFAULT NULL," +
                    "tipo INT DEFAULT NULL," +
                    "PRIMARY KEY (id))";
            this.statement = this.connection.createStatement();
            this.statement.executeUpdate(this.query);
            return true;
        } catch (Exception e) {
            System.err.println(e);
        }
        return false;
    }

    public boolean createT_MSG() {
        try {
            this.query = "CREATE TABLE IF NOT EXISTS " + this.DB_NAME + ".msg (" +
                    "id INT NOT NULL AUTO_INCREMENT," +
                    "texto VARCHAR(1024) DEFAULT NULL," +
                    "id_chanel INT DEFAULT NULL," +
                    "id_ficheiro INT DEFAULT NULL," +
                    "username VARCHAR(15) DEFAULT NULL," +
                    "envia VARCHAR(20) DEFAULT NULL," +
                    "recebe VARCHAR(20) DEFAULT NULL," +
                    "PRIMARY KEY (id))";
            this.statement = this.connection.createStatement();
            this.statement.executeUpdate(this.query);
            return true;
        } catch (Exception e) {
            System.err.println(e);
        }
        return false;
    }

    public boolean createT_User() {
        try {
            this.query = "CREATE TABLE IF NOT EXISTS " + this.DB_NAME + ".user (" +
                    "id INT DEFAULT NULL," +
                    "nome VARCHAR(30) DEFAULT NULL," +
                    "username VARCHAR(15) DEFAULT NULL," +
                    "password VARCHAR(15) DEFAULT NULL," +
                    "ip VARCHAR(20) DEFAULT NULL," +
                    "udp_port INT DEFAULT NULL," +
                    "tcp_port INT DEFAULT NULL," +
                    "ativo TINYINT DEFAULT '0'," +
                    "imagem VARCHAR(150) DEFAULT NULL)";
            this.statement = this.connection.createStatement();
            this.statement.executeUpdate(this.query);
            return true;
        } catch (Exception e) {
            System.err.println(e);
        }
        return false;
    }

    public boolean setDesligado(String username) {
        try {
            this.query = "UPDATE "+this.DB_NAME+".user SET ativo = 0 WHERE username = a";
            this.statement = this.connection.createStatement();
            this.statement.executeUpdate(this.query);
            return true;
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    public void setLigado(String username) {
        try {
            this.query = "UPDATE "+this.DB_NAME+".user SET o ='" + 1 + "' where username='"+username+"'";
            this.statement = this.connection.createStatement();
            this.statement.executeUpdate(this.query);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public List<String> getConnectedUsers() {
        List<String> list = new ArrayList<>();
        this.query = "select * from " + this.DB_NAME + ".user where ligado = '1'";
        try {
            this.statement = this.connection.createStatement();
            this.rs = this.statement.executeQuery(this.query);
            while (this.rs.next()) {
                list.add(this.rs.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isConnected(String user) {
        List<String> users = getConnectedUsers();
        return users.contains(user);
    }

    public void register(Utilizador utilizador) {
        try {
            this.statement = this.connection.createStatement();
            this.query = "INSERT INTO " + this.DB_NAME + ".user(id,nome,username,password,ip,udp_port,tcp_port,ativo,imagem) value"
                    + utilizador.toDB();
            this.statement.executeUpdate(this.query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int selectPortoUdp(String username){
        String query = "select portoudp from "+DB_NAME+".user where username ='"+username+"'";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
                int porto = rs.getInt("udp_port");
                return porto;
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return 0;
    }

    public Ficheiro getFileInfo(String nome) {
        this.query = "select * from mydb.ficheiro where nome ='" + nome + "'";
        List<String> downloads = new ArrayList<>();
        try {
            this.statement = this.connection.createStatement();
            this.rs = this.statement.executeQuery(query);
            Ficheiro f = new Ficheiro();
            while (this.rs.next()) {
                f.setNome(this.rs.getString("nome"));
                f.setTamanho(this.rs.getLong("tamanho"));
            }
            return f;
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    public Utilizador login(String username, String password,String ip){
        String query = "SELECT * FROM "+this.DB_NAME+".user;";
        Statement statement;
        Utilizador utilizador=new Utilizador();
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String name,pass;
                pass =rs.getString("password");
                name =rs.getString("username");
                if (name.equals(username) && pass.equals(password)) {
                    String query2 = "update "+this.DB_NAME+".user set ligado ="+1+" where username = '"+username+"'";
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate(query2);
                    String query3 = "update "+this.DB_NAME+".user set ip='"+ip+"' where username = '"+username+"'";
                    Statement stmt2 = connection.createStatement();
                    stmt2.executeUpdate(query3);

                    utilizador.setPortoUDP(rs.getInt("portoudp"));
                    utilizador.setPortoTCP(rs.getInt("portotcp"));
                    utilizador.setUsername(name);
                    utilizador.setPassword(pass);
                    utilizador.setNome(rs.getString("nome"));
                    utilizador.setIp(ip);

                    return utilizador;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public InetAddress selectIp(String username){
        String query = "select ip from "+DB_NAME+".user where username ='"+username+"'";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
                InetAddress ip = InetAddress.getByName(rs.getString("ip"));
                return ip;
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

}