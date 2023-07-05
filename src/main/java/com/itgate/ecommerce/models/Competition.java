package com.itgate.ecommerce.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String date;
    private String lieu ;
    private String genre ;
    private String categoriecoureur;

    private String distance;
    private String pointdepart ;
    private String pointarrive ;

    private String nomcompetition;
    private String heure;

    private String confirmation;

   private ArrayList<Long> list = new ArrayList< > ();

//    @ManyToOne
//    @JoinColumn(name = "id_equipefederation")
//    private EquipeFederation equipeFederation;

    @ManyToOne
    @JoinColumn(name = "id_categorie")
    private CategorieVelo categorieVelo;



    @ManyToMany
    @JoinTable(name="COMP_ARBITRE",joinColumns = @JoinColumn (name ="COMP_ID"),
            inverseJoinColumns =@JoinColumn(name = "ARB_ID"))
    private Collection<Arbitre> arbitres;

    @OneToMany(mappedBy = "competition",cascade =CascadeType.REMOVE)
    private Collection<ResultatCompetition> resultatCompetition;





    public void addarbitre(Arbitre a){
        if(arbitres==null){
            arbitres=new ArrayList<>();
        }
        arbitres.add(a);
    }











    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }



    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }




    public String getNomcompetition() {
        return nomcompetition;
    }

    public void setNomcompetition(String nomcompetition) {
        this.nomcompetition = nomcompetition;
    }



    public CategorieVelo getCategorieVelo() {
        return categorieVelo;
    }

    public void setCategorieVelo(CategorieVelo categorieVelo) {
        this.categorieVelo = categorieVelo;
    }

    public Collection<Arbitre> getArbitres() {
        return arbitres;
    }

    public void setArbitres(Collection<Arbitre> arbitres) {
        this.arbitres = arbitres;
    }


@JsonIgnore
    public Collection<ResultatCompetition> getResultatCompetition() {
        return resultatCompetition;
    }

    public void setResultatCompetition(Collection<ResultatCompetition> resultatCompetition) {
        this.resultatCompetition = resultatCompetition;
    }



/*   public void addcoureur(Coureur c){
        if(coureurs==null){
            coureurs=new ArrayList<>();
        }
        coureurs.add(c);
    }*/

    public ArrayList<Long> getList() {
        return list;
    }

    public void setList(ArrayList<Long> list) {
        this.list = list;
    }



    public String getPointdepart() {
        return pointdepart;
    }

    public void setPointdepart(String pointdepart) {
        this.pointdepart = pointdepart;
    }

    public String getPointarrive() {
        return pointarrive;
    }

    public void setPointarrive(String pointarrive) {
        this.pointarrive = pointarrive;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCategoriecoureur() {
        return categoriecoureur;
    }

    public void setCategoriecoureur(String categoriecoureur) {
        this.categoriecoureur = categoriecoureur;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }
}
