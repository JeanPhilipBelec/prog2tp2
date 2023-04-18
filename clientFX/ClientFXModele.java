package clientFX;


import server.models.Course;

import java.util.LinkedList;

/**
 * Modele pour le client,
 * Garde en memoire les information des cours a afficher et du formulaires a envoye
 */
public class ClientFXModele {


    /**
     * La session selectioner
     */
    public String sessionSelect;

    /**
     * les cours a afficher
     */
    public LinkedList<Course> coursesList;

    /**
     * Le cours selectioner
     */
    public Course selectedCourse;

    /**
     * le nom pour le formulaire
     */
    public String nom;

    /**
     * le prenom pour le formulaire
     */
    public String prenom;

    /**
     * le email pour le formulaire
     */
    public String email;

    /**
     * le matricule pour le formulaire
     */
    public String matricule;

    /**
     * La reponse du serveur
     */

    public String serverAnswer;
    /**
     * String qui est associé a la commande REGISTER
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * String qui est associé a la commande LOAD
     */
    public final static String LOAD_COMMAND = "CHARGER";

}
