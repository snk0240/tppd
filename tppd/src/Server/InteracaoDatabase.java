package Server;

import java.sql.Connection;
import java.sql.DriverManager;

public class InteracaoDatabase {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://";
    private static final String DB_NAME = "/tppd";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root123";
    private static final String TIMEZONE = "?serverTimezone=UTC";
    private String ip;
    private String url;
    private Connection connection;

    public InteracaoDatabase(String ip) {
        this.ip = ip;
        url = DB_URL + ip + DB_NAME + TIMEZONE;
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(url, DB_USER, DB_PASS);
            System.out.println("Ligado a Base de Dados");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
