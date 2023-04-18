package clientFX;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import server.models.Course;

import java.util.Iterator;
import java.util.LinkedList;


public class ClientFXVue extends BorderPane {

    //titres des sections
    private final Label titreTableCours = new Label("Liste des cours");
    private final Label titreFormulaire = new Label("Formulaire d'inscription");


    // Section des champs de texte
    private final Label labelPrenom = new Label("Prenom:");
    private TextField textFieldPrenom = new TextField ();
    private HBox boxPrenom = new HBox(labelPrenom,textFieldPrenom);

    private final Label labelNom = new Label("Nom:");
    private TextField textFieldNom = new TextField ();
    private HBox boxNom = new HBox(labelNom,textFieldNom);


    private final Label labelEmail = new Label("Email:");
    private TextField textFieldEmail = new TextField ();
    private HBox boxEmail = new HBox(labelEmail,textFieldEmail);


    private final Label labelMatricule = new Label("Matricule:");
    private TextField textFieldMatricule = new TextField ();
    private HBox boxMatricule = new HBox(labelMatricule,textFieldMatricule);


    //Section de la table des cours a afficher
    private TableView tableCours = new TableView();

    private ComboBox sessionCBox = new ComboBox();



    //Les zones de texte de succes/erreur
    private Label serverAnswer = new Label();




    //Les boutons
    private Button inscriptionButton = new Button("envoyer");

    private Button chargerButton = new Button("charger");


    //Boites pour organiser les elements
    private HBox bottomLeftBox = new HBox(sessionCBox,chargerButton);

    private VBox leftSection = new VBox(titreTableCours,tableCours, bottomLeftBox);

    private VBox rightSection = new VBox(titreFormulaire,boxPrenom,boxNom,boxEmail,boxMatricule,inscriptionButton,serverAnswer);


    /**
     * Contruteur de la Vue, initialise les valeurs
     */
    public ClientFXVue() {
        this.sessionCBox.setPromptText("session");
        this.sessionCBox.getItems().add("Automne");
        this.sessionCBox.getItems().add("Hiver");
        this.sessionCBox.getItems().add("Ete");



        TableColumn<Course, String> codeCol = new TableColumn<>("Code");

        codeCol.setCellValueFactory(new PropertyValueFactory<Course, String>("Code"));

        TableColumn<Course, String> coursCol = new TableColumn<>("Cours");

        coursCol.setCellValueFactory(new PropertyValueFactory<Course, String>("Name"));

        tableCours.getColumns().addAll(codeCol, coursCol);






        this.setLeft(this.leftSection);
        this.setRight(this.rightSection);


        BorderPane.setAlignment(this.leftSection, Pos.CENTER_RIGHT);
        BorderPane.setAlignment(this.rightSection, Pos.CENTER_LEFT);

    }



    public void setTableCours(LinkedList<Course> liste){
        this.tableCours.setItems(observableCourses(liste));

    }
    public Button getChargerButton(){
        return chargerButton;
    }

    public Button getInscriptionButton() {
        return inscriptionButton;
    }

    public TextField getTextFieldEmail() {
        return textFieldEmail;
    }

    public TextField getTextFieldMatricule() {
        return textFieldMatricule;
    }

    public TextField getTextFieldNom() {
        return textFieldNom;
    }

    public TextField getTextFieldPrenom() {
        return textFieldPrenom;
    }

    public ComboBox getSessionCBox() {
        return sessionCBox;
    }

    public TableView getTableCours() {
        return tableCours;
    }

    public Label getServerAnswer() {
        return serverAnswer;
    }

    /**
     * Transforme une liste chainee de cours en liste observable pour la vue
     * @param coursesList La liste a ajouter a la liste observable
     * @return la liste a observer
     */
    private static ObservableList<Course> observableCourses(LinkedList<Course> coursesList){
        ObservableList<Course> courses = FXCollections.observableArrayList();
        Iterator<Course> iterator = coursesList.iterator();

        while (iterator.hasNext()){
            courses.add(iterator.next());
        }

        return courses;
    }


}