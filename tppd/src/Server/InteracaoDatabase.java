package Server;

import Dados.Ficheiro;

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
    private String ip;
    private String url;
    private Connection connection;
    int identificador;

    Statement stmt;
    String query;

    public InteracaoDatabase(String ip, int id) {
        this.ip = ip;
        this.identificador = id;
        DB_NAME = "tppd"+identificador;
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection("jdbc:mysql://"+ip+TIMEZONE,DB_USER,DB_PASS);
            stmt = connection.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS "+DB_NAME);
            System.out.println("Base de Dados do Servidor Criada...");
            createT_MSG();
            createT_Ficheiro();
            createT_Canal();
            createT_User();
            System.out.println("Tabelas Criadas...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean createT_Canal(){
        try{
            query = "CREATE TABLE IF NOT EXISTS "+DB_NAME+".canal ("+
                    "id INT NOT NULL AUTO_INCREMENT,"+
                    "nome VARCHAR(15) DEFAULT NULL,"+
                    "descricao VARCHAR(200) DEFAULT NULL,"+
                    "username VARCHAR(15) DEFAULT NULL,"+
                    "password VARCHAR(15) DEFAULT NULL,"+
                    "PRIMARY KEY (id))";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            return true;
        }catch(Exception e){
            System.out.println(e);
        }
        return false;
    }
    public boolean createT_Ficheiro(){
        try{
            query = "CREATE TABLE IF NOT EXISTS "+DB_NAME+".ficheiro ("+
                    "id INT NOT NULL AUTO_INCREMENT,"+
                    "username VARCHAR(15) DEFAULT NULL,"+
                    "caminho VARCHAR(200) DEFAULT NULL,"+
                    "tamanho BIGINT DEFAULT NULL,"+
                    "tipo INT DEFAULT NULL,"+
                    "PRIMARY KEY (id))";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            return true;
        }catch(Exception e){
            System.out.println(e);
        }
        return false;
    }
    public boolean createT_MSG(){
        try{
            query = "CREATE TABLE IF NOT EXISTS "+DB_NAME+".msg ("+
                    "id INT NOT NULL AUTO_INCREMENT,"+
                    "texto VARCHAR(1024) DEFAULT NULL,"+
                    "id_chanel INT DEFAULT NULL,"+
                    "id_ficheiro INT DEFAULT NULL,"+
                    "username VARCHAR(15) DEFAULT NULL,"+
                    "envia VARCHAR(20) DEFAULT NULL,"+
                    "recebe VARCHAR(20) DEFAULT NULL,"+
                    "PRIMARY KEY (id))";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            return true;
        }catch(Exception e){
            System.out.println(e);
        }
        return false;
    }
    public boolean createT_Server(){
        try{
            query = "CREATE TABLE IF NOT EXISTS "+DB_NAME+".server (" +
                    "id int NOT NULL AUTO_INCREMENT," +
                    "ip VARCHAR(20) DEFAULT NULL," +
                    "udp_port INT DEFAULT NULL," +
                    "tcp_port INT DEFAULT NULL," +
                    "PRIMARY KEY (id))";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            return true;
        }catch(Exception e){
            System.out.println(e);
        }
        return false;
    }
    public boolean createT_User(){
        try{
            query = "CREATE TABLE IF NOT EXISTS "+DB_NAME+".user (" +
                    "id INT NOT NULL AUTO_INCREMENT," +
                    "nome VARCHAR(30) DEFAULT NULL," +
                    "username VARCHAR(15) DEFAULT NULL," +
                    "password VARCHAR(15) DEFAULT NULL," +
                    "ip VARCHAR(20) DEFAULT NULL," +
                    "udp_port INT DEFAULT NULL," +
                    "tcp_port INT DEFAULT NULL," +
                    "ativo TINYINT DEFAULT '0'," +
                    "imagem VARCHAR(150) DEFAULT NULL,"+
                    "PRIMARY KEY (id))";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            return true;
        }catch(Exception e){
            System.out.println(e);
        }
        return false;
    }
    public boolean create_Users(){
        try{
            query = "INSERT INTO "+DB_NAME+".user VALUES " +
                    "(1,'andre joao','andre123','andre123','127.0.0.1',3636,3636,0,NULL)," +
                    "(2,'andre sousa','andre321','andre321','127.0.0.1',3737,3737,0,NULL);";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            return true;
        }catch(Exception e){
            System.out.println(e);
        }
        return false;
    }

    public boolean setDesligado(String username){
        try{
            String query ="update "+DB_NAME+".user set ligado ='"+0+"' where username = '"+username+"'";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            return true;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    public void setLigado(String username){
        try{
            String query ="update "+DB_NAME+".user set ligado ='"+1+"' where username = '"+username+"'";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public List<String> getConnectedUsers(){
        List<String> list = new ArrayList<>();
        String query ="select * from "+DB_NAME+".user where ligado = '1'";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                list.add(rs.getString("username"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public boolean isConnected(String user){
        List<String> users = getConnectedUsers();
        return users.contains(user);
    }

    public Ficheiro getFileInfo(String nome){
        String query ="select * from mydb.ficheiro where nome ='"+nome+"'";
        List<String> downloads = new ArrayList<>();
        try{
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            Ficheiro f = new Ficheiro();
            while(rs.next()){
                f.setNome(rs.getString("nome"));
                f.setTamanho(rs.getLong("tamanho"));
            }
            return f;
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
}