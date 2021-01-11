package Client;

import Dados.*;
import Server.GetServiceObserverInterface;
import Server.ServerServiceInterface;

import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientMain extends UnicastRemoteObject implements ClientInterface{

    private ClienteComm cli;
    private Login login;
    private String ip;
    private Database database = new Database();

    String objectUrl;

    ServerServiceInterface remoteServerService;
    static ClientMain clim = null;

    ClientMain(ClienteComm cli, String ip) throws RemoteException {
        this.cli = cli;
        this.login = new Login();
        objectUrl = "rmi://"+ip+"/GetRemoveService";
    }

    public Msgt getmensagem2(){
        Scanner scanner = new Scanner(System.in);
        Msgt msg = new Msgt();
        String destino,mensagem;
        msg.setEnvia(cli.getUtilizador().getUsername());
        try {
            System.out.println("Indique o utilizador para o qual quer enviar mensagem:\n");
            destino = scanner.nextLine();
            System.out.println("Escreva a mensagem:\n");
            mensagem= scanner.nextLine();
            msg.setRecebe(destino);
            msg.setTexto(mensagem);
        } catch (InputMismatchException e){
            System.out.println("Gerou um erro a declarar os portos.");
        } catch (Exception e){
            System.out.println("Gerou um erro a declarar qualquer coisa.");
        }
        return msg;
    }

    public Msg getmensagem(){
        Scanner scanner = new Scanner(System.in);
        Msg msg = new Msg();
        String destino,mensagem;
        msg.setEnvia(cli.getUtilizador().getUsername());
        try {
            System.out.println("Indique o utilizador para o qual quer enviar mensagem:\n");
            destino = scanner.nextLine();
            System.out.println("Escreva a mensagem:\n");
            mensagem= scanner.nextLine();
            msg.setRecebe(destino);
            msg.setTexto(mensagem);
        } catch (InputMismatchException e){
            System.out.println("Gerou um erro a declarar os portos.");
        } catch (Exception e){
            System.out.println("Gerou um erro a declarar qualquer coisa.");
        }
        return msg;
    }

    public Login fazlogin(){
        Scanner scan = new Scanner(System.in);
        scan.nextLine();
        System.out.print("Introduza o seu username: ");
        this.login.setUsername(scan.nextLine());
        System.out.print("Introduza a sua password: ");
        this.login.setPassword(scan.nextLine());

        cli.getDatabase();
        return this.login;
    }

    public Utilizador regista(){
        int portoTCP = this.cli.getTcp_port_server();
        int portoUDP = this.cli.getUdp_port_server();
        try {
            InetAddress ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String password, username, nome , imagem;
        Utilizador util = new Utilizador();
        Scanner scanner = new Scanner(System.in);

        password = username = nome = imagem = null;
        scanner.nextLine();
        try {
            username = scanner.nextLine();
            System.out.println("Insira o username:\n");
            username = scanner.nextLine();

            System.out.println("Insira a password:\n");
            password = scanner.nextLine();

            System.out.println("Insira o nome:\n");
            nome = scanner.nextLine();

            System.out.println("Insira o caminho da imagem:\n");
            imagem = scanner.nextLine();

        } catch (InputMismatchException e){
            System.err.println("Some port declaration error.");
        } catch (Exception e){
            System.err.println("Some declaration error.");
        }

        util.setPortoTCP(portoTCP);
        util.setPortoUDP(portoUDP);
        util.setUsername(username);
        util.setPassword(password);
        util.setImagem(imagem);
        util.setNome(nome);
        util.setAtivo(true);

        cli.getDatabase();
        return util;
    }

    public void comeca(){
        Scanner scan1 = new Scanner(System.in);
        int escolha;
        try {
            remoteServerService = (ServerServiceInterface) Naming.lookup(objectUrl);

            do {
                System.out.println("Selecione uma das opções:\n" +
                        "1-Login\n" +
                        "2-Registar\n" +
                        "3-Sair\n" +
                        "4-Registar> ");
                escolha = scan1.nextInt();
                switch (escolha) {
                    case 1:
                        this.cli.login(fazlogin());
                        if(this.cli.autenticado)
                            System.out.println("Utilizador autenticado com sucesso!\n");
                        else
                            break;
                    case 2:
                        this.cli.registo(regista());
                        if(this.cli.isAutenticado())
                            System.out.println("Utilizador registado com sucesso!\n");
                        else
                            break;
                    case 3:
                        if(this.cli.autenticado)
                            this.cli.sai();
                        System.exit(0);
                    case 4:
                        if(remoteServerService.registaUserRmi(regista(), clim)){
                            System.out.println("User registado com sucesso.");
                        }else {
                            System.out.println("User registado sem sucesso.");
                        }
                        break;
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while(!this.cli.autenticado);

            boolean sair = false;
            do
            {
                Scanner scan2 = new Scanner(System.in);
                System.out.println("Selecione uma das opções:\n" +
                        "1-Enviar Mensagem\n" +
                        "2-Listar Utilizadores\n" +
                        "3-Listar Ficheiros\n" +
                        "4-Escolher ficheiro para download\n" +
                        "5-Atualizar base de dados com nova informação\n" +
                        "6-Obter database\n" +
                        "7-Mostrar Database\n" +
                        "8-sair\n" +
                        "9-Msg para Todos");
                escolha = scan2.nextInt();
                switch(escolha) {
                    case 1:
                        cli.enviamensagem(getmensagem());
                        break;
                    case 2:
                        cli.listautilizadores();
                        break;
                    case 3:
                        //cli.listaficheiros();
                        break;
                    case 4:
                        //cli.downloadficheiro(escolheficheiro());
                        break;
                    case 5:
                        //cli.updateDatabase();
                        break;
                    case 6:
                        cli.getDatabase();
                        break;
                    case 7:
                        cli.mostraDatabase();
                        break;
                    case 8:
                        System.exit(0);
                        break;
                    case 9:
                        if(remoteServerService.enviaMsgTodosRmi(getmensagem2(),clim)){
                            System.out.println("Msg enviada com sucesso.");
                        }else {
                            System.out.println("Msg enviada sem sucesso.");
                        }
                        break;
                }
            }while(!sair);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException, RemoteException, MalformedURLException, NotBoundException {
        GetServiceObserverInterface remoteFileService;

        if(args.length != 2){
            System.err.println("Sintaxe: java Client <IP_SERVIDOR> <UDP_PORT_SERVIDOR>");
            return;
        } System.out.println("Server IP is "+args[0]+" and Server UDP Port is " + args[1] + "\n");

        ClienteComm cli = new ClienteComm(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
        cli.start();

        //COMECA O MENU DO CLIENTE
        clim = new ClientMain(cli, args[0]);
        clim.comeca();
    }

    @Override
    public boolean registaruser(Utilizador user) throws RemoteException {
        try {
            cli.registo(user);
        } catch (Exception e) {
            System.out.println("Excepcao ao registar user: " + e);
            return false;
        }
        return true;
    }

    @Override
    public boolean enviaMsgTodos(Msgt msg) throws RemoteException {
        try {
            cli.enviamensagemTodos(msg);
        } catch (Exception e) {
            System.out.println("Excepcao ao enviar msg: " + e);
            return false;
        }
        return true;
    }
}