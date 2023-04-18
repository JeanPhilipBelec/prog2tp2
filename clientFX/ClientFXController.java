package clientFX;

import server.models.Command;
import server.models.Course;
import server.models.RegistrationForm;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

/**
 *Le controleur du client, fait la communication entre le modele et la vue,
 *possede plusieur methode pour communiquer avec le serveur et modifier le modele et la vue en consequence
 *
 */
public class ClientFXController {

    private final int port = 1337;
    private ClientFXModele modele;
    private ClientFXVue vue;

    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;


    /**
     * Construteur du controleur
     * @param m le modele associer au controleur
     * @param v la vue associer au controleur
     */
    public ClientFXController(ClientFXModele m, ClientFXVue v){
        this.modele = m;
        this.vue = v;
        this.vue.getChargerButton().setOnAction((action) -> {
            this.charger();
        });

        this.vue.getInscriptionButton().setOnAction((action) -> {
            this.inscrire();
        });

        this.vue.getSessionCBox().setOnAction((action) -> {
                    this.updateSession();
        });



    }

    /**
     * Prend la selection de la session et demande au server la liste de cours, l'enregistre dans le ClientFXModele
     * et ensuite affiche la nouvelle liste de cours dans la ClientFXVue
     */
    private void charger(){
        if ((this.modele.sessionSelect) == null){
            return;
        }

        LinkedList<Course> liste = new LinkedList<>();

        String session = this.modele.sessionSelect;
        Command charger = new Command("CHARGER",session);

        try {


        Socket clientSocket = new Socket("127.0.0.1", this.port);

        this.outStream = new ObjectOutputStream(clientSocket.getOutputStream()) ;
        this.inStream = new ObjectInputStream(clientSocket.getInputStream());

        this.outStream.writeObject(charger);

        this.modele.coursesList = (LinkedList<Course>) this.inStream.readObject();


        updateVueCourses();


        }catch (IOException | ClassNotFoundException ex){
            this.modele.serverAnswer = "Echec de chargement, probleme avec le serveur";
            updateAnswerTextVue();
            return;
        }

    }

    /**
     * Met a jours la session dans le modele
     */
    private void updateSession(){
        this.modele.sessionSelect = this.vue.getSessionCBox().getValue().toString();
    }

    /**
     * Met a jours les cours afficher dans la vue;
     */
    private void updateVueCourses(){
        this.vue.setTableCours(this.modele.coursesList);
    }

    /**
     * Envoie les information pertinante au serveur pour faire une demande d'enregistrement a un cours
     */
    private void inscrire(){
        updateCours();
        if (this.modele.selectedCourse == null){
            this.modele.serverAnswer = "Veuillez selectionner un cours";
            updateAnswerTextVue();
            return;
        }

        updateNom();
        updatePrenom();
        updateEmail();
        updateMatricule();

        //verifie que le metricule est 6 charactere
        if (this.modele.matricule.length() != 6){
            this.modele.serverAnswer = "Matricule incorrect, doit contenir 6 chiffres";
            updateAnswerTextVue();
            return;
        }

        //Verifie que le matricule est numerique
        try{
            Integer.parseInt(this.modele.matricule);
        }catch (NumberFormatException ex){
            this.modele.serverAnswer = "Matricule incorrect, doit contenir 6 chiffres";
            updateAnswerTextVue();
            return;
        }

        Command inscrire = new Command("INSCRIRE", "");

        RegistrationForm form = new RegistrationForm(this.modele.prenom,this.modele.nom,this.modele.email,this.modele.matricule,this.modele.selectedCourse);

        //envoi de la commande "inscrire"
        try {
            Socket clientSocket = new Socket("127.0.0.1", this.port);

            this.outStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.inStream = new ObjectInputStream(clientSocket.getInputStream());

            outStream.writeObject(inscrire);

        }catch (IOException ex) {
            this.modele.serverAnswer = "Echec d'inscription probleme avec le serveur";
            updateAnswerTextVue();
            return;
        }


        //envoi du formulaire
        try {

            outStream.writeObject(form);
            String message = (String) inStream.readObject();
            this.modele.serverAnswer = message;
            updateAnswerTextVue();


        }catch (IOException | ClassNotFoundException ex) {
            this.modele.serverAnswer = "Echec d'inscription probleme avec le serveur";
            updateAnswerTextVue();
            return;
        }
    }

    /**
     * Met a jours le Email dans le modele
     */
    public void updateEmail(){
        this.modele.email = this.vue.getTextFieldEmail().getText();
    }

    /**
     * Met a jours le Nom dans le modele
     */
    public void updateNom(){
        this.modele.nom = this.vue.getTextFieldNom().getText();
    }

    /**
     * Met a jours le Prenom dans le modele
     */
    public void updatePrenom(){
        this.modele.prenom = this.vue.getTextFieldPrenom().getText();
    }

    /**
     * Met a jours le Matricule dans le modele
     */
    public void updateMatricule(){
        this.modele.matricule = this.vue.getTextFieldMatricule().getText();
    }

    /**
     * Met a jours le CoursSelectioner dans le modele
     */
    public void updateCours(){
        if (this.vue.getTableCours().getSelectionModel().getSelectedItem() != null) {
            this.modele.selectedCourse = (Course) this.vue.getTableCours().getSelectionModel().getSelectedItem();
        }
    }

    public void updateAnswerTextVue(){this.vue.getServerAnswer().setText(this.modele.serverAnswer);}
}