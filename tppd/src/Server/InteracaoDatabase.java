package Server;

import Dados.Ficheiro;
import Dados.Utilizador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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

    public Connection getConnection() {
        return this.connection;
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

    public boolean createT_Server() {
        try {
            this.query = "CREATE TABLE IF NOT EXISTS " + this.DB_NAME + ".server (" +
                    "id int NOT NULL AUTO_INCREMENT," +
                    "ip VARCHAR(20) DEFAULT NULL," +
                    "udp_port INT DEFAULT NULL," +
                    "tcp_port INT DEFAULT NULL," +
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
            this.query = "update " + this.DB_NAME + ".user set ligado ='" + 0 + "' where username = '" + username + "'";
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
            this.query = "update " + this.DB_NAME + ".user set ligado ='" + 1 + "' where username = '" + username + "'";
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
}