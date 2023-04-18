package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Cette classe implemente un serveur pour le systeme d'inscription qui communique avec des Clients.
 * Permet d'envoyer des listes de cours d'un fichier selon la session demander et de le envoyé au client.
 * Permet de recevoir des enregristrement et de les écrires dans un fichier texte.
 */
public class Server {

    /**
     * String qui est associé a la commande REGISTER
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";

    /**
     * String qui est associé a la commande LOAD
     */
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Construteur de serveur, prépare le port ainsi que la liste de Handlers
     *
     * @param port Le port sur lequel le serveur sera ouvert
     * @throws IOException En cas de probleme de connection avec le Client
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Ajoute un handler a la liste de Handlers du serveur.
     * @param h Le handler a ajouter
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }


    /**
     * Recoir une commande et la passe a tout les handlers du server
     * @param cmd Type de commande
     * @param arg Argument de la commande
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Boucle principale du serveur.
     * Attend une connection d'un client et ensuite etablie un objectInputStream ainsi qu'un objectOutputStream pour
     * communiquer avec le client.
     * Ensuite, attend une commande du client et
     * ferme la connection avec le client directement apres le traitement de la commande.
     * En cas d'erreur ecris la trace de l'erreur.
     *
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client.Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Attend qu'un objet soit envoyer au objectInputStream le converti en commande
     * et ensuite envoie la commande au alertHandlers
     *
     * @throws IOException En cas de probleme de connection avec le client
     * @throws ClassNotFoundException Si l'objet reçu n'est pas sérializable
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ( (line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Prend une ligne de commande et la separe en type de commande et en argument pour la commande
     *
     * @param line La ligne de commande a separer
     * @return Type de Commande , argument de la commande
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }


    /**
     * Ferme les streams ojectOutputStream, objectInputStream et déconecte le client du serveur et
     *
     * @throws IOException En cas de probleme de connection avec le client
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Handler, recoit une commande, vérifie si le type de la commande est une type existant et passe les arguments a la bonne fonction.
     * @param cmd Type de la commande
     * @param arg Argument de la commande
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {
        // TODO: implémenter cette méthode
        LinkedList<Course> listCourses = new LinkedList<>();
        Course currentCourse;

        try {
            Scanner scan = new Scanner(new File("src/main/java/server/data/cours.txt"));
            while (scan.hasNext()) {

                String line = scan.nextLine();
                String[] words;
                words = line.split("\t", 3);


                if ( words[2].equals(arg)){
                    currentCourse = new Course(words[1],words[0],words[2]);
                    listCourses.add(currentCourse);
                }

            }
            try {
                this.objectOutputStream.writeObject(listCourses);
            }catch (IOException ex){
                System.out.println("Erreur à l'ecriture de l'objet");
            }

            System.out.println("Done");
        } catch (IOException ex) {
            System.out.println("Erreur à l'ouverture du fichier");

        }
    }


    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        // TODO: implémenter cette méthode


        try {
            //On vas chercher la forme serialiser de lobjet

            RegistrationForm registration = (RegistrationForm) this.objectInputStream.readObject();

            //on contruit la ligne a ecrire dans le fichier des enregistrement

            String line = registration.getCourse().getSession() + "\t";
            line += registration.getCourse().getCode() + "\t";
            line += registration.getMatricule() + "\t";
            line += registration.getPrenom() + "\t";
            line += registration.getNom() + "\t";
            line += registration.getEmail();
            line += "\n";

            try {
                FileWriter fw = new FileWriter("src/main/java/server/data/inscription.txt",true);
                BufferedWriter writer = new BufferedWriter(fw);

                writer.append(line);
                writer.close();
                fw.close();
            } catch (IOException ex) {
                System.out.println("Erreur à l'écriture du fichier");
                this.objectOutputStream.writeObject("Erreur a l'inscription");
            }

            this.objectOutputStream.writeObject("Inscription reusite");

        }catch (ClassNotFoundException | ClassCastException ex){
            try {
                System.out.println("Erreur a la lecture de l'objet");

            this.objectOutputStream.writeObject("Erreur a l'inscription");
                System.out.println("Message d'erreur envoyer au Client");
            }catch(IOException  e){
                System.out.println("Erreur de communication");
            }
        }catch (IOException  ex){

            System.out.println("Erreur de communication");
        }

    }
}

