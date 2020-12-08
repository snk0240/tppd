package Client;

import Dados.*;

import java.io.IOException;
import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientMain {

    ComunicacaoComServidor comunicacaoComServidor;
    TransferenciaFicheiros transferenciaFicheiros;
    Scanner scanner;
    Login login;
    //Database database = new Database();

    public ClientMain(ComunicacaoComServidor comunicacaoComServidor, TransferenciaFicheiros transferenciaFicheiros) {

        this.comunicacaoComServidor = comunicacaoComServidor;
        this.transferenciaFicheiros = transferenciaFicheiros;

        scanner = new Scanner(System.in);
        login = new Login();
    }

    public void corre()
    {
        int escolha;
        comunicacaoComServidor.comecaThread();

        do {
            System.out.println(
                    "Selecione uma das opções:\n" +
                    "1-Login\n" +
                    "2-Registar\n" +
                    "3-Sair");
            escolha = scanner.nextInt();
            switch (escolha)
            {
                case 1:
                    comunicacaoComServidor.login(fazlogin());
                    if(comunicacaoComServidor.autenticado)
                        System.out.println("Utilizador autenticado com sucesso!\n");
                    else
                        //System.out.println("Login falhou! Tente outra vez...\n");
                        break;
                case 2:
                    comunicacaoComServidor.registo(regista());
                    if(comunicacaoComServidor.autenticado)
                        System.out.println("Utilizador registado com sucesso!\n");
                    else
                        //System.out.println("Registo falhou! Tente outra vez...\n");
                        break;
                case 3:
                    System.exit(0);
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while(!comunicacaoComServidor.autenticado);

        String str;
        boolean sair = false;
        do
        {
            System.out.println(
                    "Selecione uma das opções:\n" +
                    "1-Enviar Mensagem\n" +
                    "2-Listar Utilizadores\n" +
                    "3-Listar Ficheiros\n" +
                    "4-Escolher ficheiro para download\n" +
                    "5-Atualizar base de dados com nova informação\n" +
                    "6-Obter database\n" +
                    "7-Mostrar Database\n" +
                    "8-sair");
            escolha = scanner.nextInt();
            switch(escolha)
            {
                case 1:
                    comunicacaoComServidor.enviamensagem(getmensagem());
                    break;
                case 2:
                    comunicacaoComServidor.listautilizadores();
                    break;
                case 3:
                    comunicacaoComServidor.listaficheiros();
                    break;
                case 4:
                    comunicacaoComServidor.downloadficheiro(escolheficheiro());
                    break;
                case 5:
                    comunicacaoComServidor.updateDatabase();
                    break;
                case 6:
                    comunicacaoComServidor.getDatabase();
                    break;
                case 7:
                    //comunicacaoComServidor.mostraDatabase();
                    break;
                case 8:
                    System.exit(0);
                    break;
            }

        }while(!sair);
    }
    public Msg getmensagem(){
        Msg msg = new Msg();
        String destino,mensagem;

        msg.setSource(comunicacaoComServidor.utilizador.getUsername());
        try {
            System.out.println("Indique o utilizador para o qual quer enviar mensagem:\n");
            destino = scanner.nextLine();
            System.out.println("Escreva a mensagem:\n");
            mensagem= scanner.nextLine();
            msg.setDestino(destino);
            msg.setMsg(mensagem);
        } catch (InputMismatchException e){
            System.out.println("Gerou um erro a declarar os portos.");
        } catch (Exception e){
            System.out.println("Gerou um erro a declarar qualquer coisa.");
        }
        return msg;
    }
    public FileRequest escolheficheiro(){
        FileRequest fr = new FileRequest();
        String filename,source;

        fr.setUsernameRequest(comunicacaoComServidor.utilizador.getUsername());

        try {
            System.out.println("Indique o ficheiro  transferir:\n");
            filename = scanner.nextLine();

            System.out.println("Insira o user que tem o ficheiro:\n");
            source = scanner.nextLine();
            fr.setUsernameSource(source);
            fr.setFilename(filename);
        } catch (InputMismatchException e){
            System.out.println("Gerou um erro a declarar os portos.");
        } catch (Exception e){
            System.out.println("Gerou um erro a declarar qualquer coisa.");
        }
        return fr;
    }
    public Utilizador regista(){
        int portoTCP = 0, portoUDP = 0;
        String password, username, nome;
        Utilizador util = new Utilizador();

        password = username = nome = null;
        scanner.nextLine();
        try {
            System.out.println("Insira o nome:\n");
            nome = scanner.nextLine();

            username = scanner.nextLine();
            System.out.println("Insira o username:\n");
            username = scanner.nextLine();

            System.out.println("Insira a password:\n");
            password = scanner.nextLine();

            do {
                System.out.println("Insira o porto TCP:\n");
                portoTCP = scanner.nextInt();
            }while(portoTCP < 1023 || portoTCP > 49152);

            do {
                System.out.println("Insira o porto UDP:\n");
                portoUDP = scanner.nextInt();
            }while(portoUDP < 1023 || portoUDP > 49152);

        } catch (InputMismatchException e){
            System.out.println("Gerou um erro a declarar os portos.");
        } catch (Exception e){
            System.out.println("Gerou um erro a declarar qualquer coisa.");
        }

        util.setPortoTCP(portoTCP);
        util.setPortoUDP(portoUDP);
        util.setUsername(username);
        util.setPassword(password);
        util.setNome(nome);

        //comunicacaoComServidor.getDatabase();
        return util;
    }
    public Login fazlogin(){
        scanner.nextLine();
        System.out.print("Introduza o seu username: ");
        login.setUsername(scanner.nextLine());
        System.out.print("Introduza a sua password: ");
        login.setPassword(scanner.nextLine());

        return login;
    }

    public static void main(String[] args){
        Socket socketServidor;

        if(args.length != 2){
            System.err.println("Sintaxe: java Client <IP_SERVIDOR> <TCP_PORT_SERVIDOR>");
            return;
        }else{
            System.out.println("Server IP: "+args[0]+"\nServer TCP port: " + args[1] + "\n");
        }
        TransferenciaFicheiros transferenciaFicheiros = new TransferenciaFicheiros();
        ComunicacaoComServidor comunicacaoComServidor = new ComunicacaoComServidor(transferenciaFicheiros);

        try{
            byte[] data2 = new byte[1024];
            String texto = "Hello from udp client!";
            byte[] data1 = texto.getBytes();

            DatagramPacket sendingPacket = new DatagramPacket(data1,data1.length,InetAddress.getByName(args[0]),Integer.parseInt(args[1]));
            comunicacaoComServidor.datagramSocket.send(sendingPacket);

            DatagramPacket receivingPacket = new DatagramPacket(data2,data2.length);
            comunicacaoComServidor.datagramSocket.receive(receivingPacket);

            String receivedData = new String(receivingPacket.getData());
            System.out.println("Recebi: "+receivedData);

            InetAddress serverAddr = InetAddress.getByName(args[0]);
            //System.out.println(Integer.parseInt(receivedData));
            socketServidor = new Socket(serverAddr,5002);//5002 deveria ser Integer.parseInt(args[1]);

            System.out.println("\nSocket Servidor:");
            System.out.println("IP: "+socketServidor.getLocalAddress());
            System.out.println("PORTA: "+socketServidor.getPort());
            socketServidor.close();

            comunicacaoComServidor.datagramSocket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Client cliente = new Client(comunicacaoComServidor,transferenciaFicheiros);
        //cliente.corre();
    }
}