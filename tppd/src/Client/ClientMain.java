package Client;

import Dados.*;
import Server.ServerServiceInterface;

import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientMain extends UnicastRemoteObject implements ClientInterface{

    private static Login login = null;

    ClientMain() throws RemoteException{    }

    public static void main(String[] args) throws UnknownHostException, RemoteException {
        ServerServiceInterface remoteFileService;
        ClientMain clim = null;

        String objectUrl;

        if(args.length != 2){
            System.err.println("Sintaxe: java Client <IP_SERVIDOR> <UDP_PORT_SERVIDOR>");
            return;
        } System.out.println("Server IP is "+args[0]+" and Server UDP Port is " + args[1] + "\n");

        objectUrl = "rmi://"+args[0]+"/GetRemoteService";

        try {
            remoteFileService = (ServerServiceInterface) Naming.lookup(objectUrl);

            ClienteComm cli = new ClienteComm(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
            cli.start();

            //COMECA O MENU DO CLIENTE
            clim = new ClientMain();
            Scanner scan1 = new Scanner(System.in);

            int escolha;

            do {
                System.out.println("Selecione uma das opções:\n" +
                        "1-Login\n" +
                        "2-Registar\n" +
                        "3-Sair\n" +
                        "4-Registar ");
                escolha = scan1.nextInt();
                switch (escolha) {
                    case 1:
                        cli.login(fazlogin(cli));
                        if(cli.autenticado)
                            System.out.println("Utilizador autenticado com sucesso!\n");
                        else
                            break;
                    case 2:
                        cli.registo(regista(cli));
                        if(cli.isAutenticado())
                            System.out.println("Utilizador registado com sucesso!\n");
                        else
                            break;
                    case 3:
                        if(cli.autenticado)
                            cli.sai();
                        System.exit(0);
                    case 4:
                        Utilizador aux = regista(cli);
                        if(remoteFileService.registaUserRmi(aux, clim)){
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
            }while(!cli.autenticado);

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
                        cli.enviamensagem(getmensagem(cli));
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
                        if(remoteFileService.enviaMsgTodosRmi(getmensagem2(cli),clim)){
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

    public static Msgt getmensagem2(ClienteComm cli){
        Scanner scanner = new Scanner(System.in);
        Msgt msg = new Msgt();
        String envia,mensagem;
        msg.setEnvia(cli.getUtilizador().getUsername());
        try {
            System.out.println("Escreva a mensagem:\n");
            mensagem= scanner.nextLine();
            msg.setTexto(mensagem);
        } catch (InputMismatchException e){
            System.out.println("Gerou um erro a declarar os portos.");
        } catch (Exception e){
            System.out.println("Gerou um erro a declarar qualquer coisa.");
        }
        return msg;
    }

    public static Msg getmensagem(ClienteComm cli){
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

    public static Login fazlogin(ClienteComm cli){
        Scanner scan = new Scanner(System.in);
        scan.nextLine();
        System.out.print("Introduza o seu username: ");
        login.setUsername(scan.nextLine());
        System.out.print("Introduza a sua password: ");
        login.setPassword(scan.nextLine());

        cli.getDatabase();
        return login;
    }

    public static Utilizador regista(ClienteComm cli){
        int portoTCP = cli.getTcp_port_server();
        int portoUDP = cli.getUdp_port_server();
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

        return util;
    }

    @Override
    public boolean registaruser(Utilizador user) throws RemoteException {
        return false;
    }

    @Override
    public boolean enviaMsgTodos(Msgt msg) throws RemoteException {
        return false;
    }
}