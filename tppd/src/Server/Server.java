package Server;

public class Server{

    public static void main(String[] args) {
        //portos de escuta tcp e udp e maquina da sua BD
        InteracaoDatabase idb = new InteracaoDatabase("localhost:3306");
    }
}