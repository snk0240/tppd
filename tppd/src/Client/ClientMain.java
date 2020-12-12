package Client;

import Dados.Login;

import java.net.*;
import java.util.Scanner;

public class ClientMain {

    private ClienteComm cli;
    private TransferenciaFicheiros transferenciaFicheiros;
    private Login login;

    ClientMain(ClienteComm cli, TransferenciaFicheiros transferenciaFicheiros){
        this.cli = cli;
        this.transferenciaFicheiros = transferenciaFicheiros;
        login = new Login();
    }

    public Login fazlogin(){
        Scanner scan = new Scanner(System.in);
        scan.nextLine();
        System.out.print("Introduza o seu username: ");
        login.setUsername(scan.nextLine());
        System.out.print("Introduza a sua password: ");
        login.setPassword(scan.nextLine());
        return login;
    }

    public void comeca()
    {
        Scanner scan = new Scanner(System.in);
        int escolha;
        do {
            System.out.println("Selecione uma das opções:\n1-Login\n2-Registar\n3-Sair");
            escolha = scan.nextInt();
            switch (escolha)
            {
                case 1:
                    /*cli.login(fazlogin());
                    if(cli.autenticado)
                        System.out.println("Utilizador autenticado com sucesso!\n");
                    else
                        System.out.println("Login falhou! Tente outra vez...\n");*/
                        break;
                case 2:
                    /*cli.registo(regista());
                    if(cli.autenticado)
                        System.out.println("Utilizador registado com sucesso!\n");
                    else
                        //System.out.println("Registo falhou! Tente outra vez...\n");*/
                        break;
                case 3:
                    System.exit(0);
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while(/*!cli.autenticado*/escolha!=5);

        String str;
        boolean sair = false;
        do
        {
            System.out.println("Selecione uma das opções:\n1-Enviar Mensagem\n" +
                    "2-Listar Utilizadores\n3-Listar Ficheiros\n4-Escolher ficheiro para" +
                    "download\n5-Atualizar base de dados com nova informação\n6-Obter database\n7-Mostrar Database\n8-sair");
            escolha = scan.nextInt();
            switch(escolha)
            {
                case 1:
                    //cli.enviamensagem(getmensagem());
                    break;
                case 2:
                    //cli.listautilizadores();
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
                    //cli.getDatabase();
                    break;
                case 7:
                    //cli.mostraDatabase();
                    break;
                case 8:
                    System.exit(0);
                    break;
            }

        }while(!sair);
    }

    public static void main(String[] args) throws UnknownHostException {
        if(args.length != 2){
            System.err.println("Sintaxe: java Client <IP_SERVIDOR> <UDP_PORT_SERVIDOR>");
            return;
        } System.out.println("Server IP: "+args[0]+"\nServer UDP port: " + args[1] + "\n");

        TransferenciaFicheiros transferenciaFicheiros = new TransferenciaFicheiros();
        ClienteComm cli = new ClienteComm(InetAddress.getByName(args[0]),Integer.parseInt(args[1]),transferenciaFicheiros);
        cli.start();
        System.out.println("asdasd");
        //COMECA O MENU DO CLIENTE
        ClientMain cli_main = new ClientMain(cli,transferenciaFicheiros);
        cli_main.comeca();
    }
}