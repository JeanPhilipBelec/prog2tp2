package client;

import server.models.Command;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class Client {
    private int port;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;

    public Client(int port){
        this.port = port;
    }

    /**
     * Fonction principale du cient, imprime les messages envoie les commandes et recoit les reponse du serveur
     */
    public void run() {

        System.out.println("***Bienvenue au portail d'inscription de cours de l'UDEM***");

        boolean repeat = true;
        LinkedList<Course> list = new LinkedList<>();

        while (repeat){
            printChoixSession();
            list = askCourse();
            printCourseList(list);
            repeat = askNewList();
        }

        RegistrationForm form = buildRegistrationForm(list);


        sendRegistration(form);

    }
    public LinkedList<Course> sendLoadCommand(){
        Command command = buildLoadCourseCommand();
        LinkedList <Course> list;
        try {
            Socket clientSocket = new Socket("127.0.0.1", this.port);
            this.outStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.inStream = new ObjectInputStream(clientSocket.getInputStream());

            this.outStream.writeObject(command);

            if ((list = (LinkedList<Course>) this.inStream.readObject()) != null) {
                this.outStream.close();
                this.inStream.close();
                return list;
            }
        }catch (IOException ex) {
            System.out.println("erreur connection au serveur");
            return null;
        } catch (ClassNotFoundException ex) {
            System.out.println("erreur connection au serveur");
            return null;
        }
        return null;

    }

    /**
     * Envoie une demande pour une liste de cours d'une session au serveur
     * @return la liste renvoyer par le serveur
     */
    public LinkedList<Course> askCourse(){

        LinkedList<Course> list;
        list = sendLoadCommand();
        return list;
        }


    /**
     * Bati un formulaire d'inscription a partir d'une lsite de cours en demandant les inforamtion a l'utilisateur
     * Verifie que le code de cour est dans la liste
     * @param courseList Liste de cours disponible
     * @return Le formulaire rempli
     */
    public RegistrationForm buildRegistrationForm(LinkedList<Course> courseList){
       if  (courseList.isEmpty()){
           return null;
       }

       Command nextCommand = new Command("","");
       nextCommand.setCommand("INSCRIRE");

       String prenom;
       String nom;
       String email;
       String matricule;
       String code;

       Scanner scanner = new Scanner(System.in);

       System.out.println("veuillez saisir votre prénom:");
       prenom = scanner.nextLine();
       System.out.println("veuillez saisir votre nom:");
       nom = scanner.nextLine();
       System.out.println("veuillez saisir votre email:");
       email = scanner.nextLine();
       System.out.println("veuillez saisir votre matricule:");
       matricule = scanner.nextLine();
       System.out.println("veuillez saisir le code du cours:");
       code = scanner.nextLine();


       boolean found = false;
       Iterator iterator;
       Course course = null;

       while (!found) {
           iterator = courseList.iterator();

           while (iterator.hasNext()) {

               course = (Course) iterator.next();
               if (code.equals( course.getCode()) ) {
                   found = true;
                   break;
               }
           }

           if (!found){
               System.out.println("Le code n'etait pas present dans la liste selectionnee");
               System.out.println("veuillez saisir le code du cours:");
               code = scanner.nextLine();
           }


       }
       RegistrationForm registration = new RegistrationForm(prenom, nom, email, matricule, course);

       return registration;
   }

    /**
     * Envoie un formulaire au serveur, ecrit la reponse du serveur dans la console
     * ou un message d'erreur en cas de probleme de connection
     * @param form Le formulaire a envoyer
     */
    public void sendRegistration(RegistrationForm form){

        try {
            Socket clientSocket = new Socket("127.0.0.1", this.port);

            this.outStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.inStream = new ObjectInputStream(clientSocket.getInputStream());

            this.outStream.writeObject("INSCRIRE");

            this.outStream.writeObject(form);
            System.out.println(this.inStream.readObject());
        }catch (IOException | ClassNotFoundException ex) {
            System.out.println("Erreur de connextion");
        }

    }

    /**
     * Ecrit les choix pour les sessions
     */
    public void printChoixSession(){
        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste de cours:");
        System.out.println("1.Automne");
        System.out.println("2.Hiver");
        System.out.println("3.Ete");
        System.out.println("Choix :");
    }

    /**
     * Imprime une liste de cours
     * @param list la liste de cours a imprimer
     */
    public void printCourseList(LinkedList<Course> list){

        Iterator<Course> iterator = list.iterator();
        int i = 1;
        System.out.println("Les cours offerts pendat la session d'" + list.getFirst().getSession() + " sont");
        while (iterator.hasNext()){

            String line = iterator.next().toString();
            String[] words = line.split(" ",3);
            line = i +". " + words[0] +" "+ words[1];
            i +=1 ;

            System.out.println(line);
        }
    }

    public boolean askNewList(){
        System.out.println("Choix:");
        System.out.println("1. Consulter les cours offerts pour une autre session");
        System.out.println("2. Inscription a un cours");
        System.out.println("Choix:");

        Scanner scanner = new Scanner(System.in);

        int choix;
        while (true) {
            choix = scanner.nextInt();
            switch (choix) {
                case 1:
                    return true;
                case 2:
                    return false;
            }
        }
    }

    /**
     * Contruit une CHARGER avec le choix de session entrer par l'utilisateur
     * @return La commande CHARGER
     */
    public Command buildLoadCourseCommand(){
        Command command = new Command("CHARGER", "");
        Scanner scanner = new Scanner(System.in);

        int choix;
        outer: while (true){
            choix = scanner.nextInt();
            switch(choix) {
                case 1:
                    command.setArguments("Automne");
                    break outer;
                case 2:
                    command.setArguments("Hiver");
                    break outer;
                case 3:
                    command.setArguments("Ete");
                    break outer;
        }

        }
        return command;
    }
}

