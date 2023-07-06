package com.itgate.ecommerce.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ResultatCompetition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String temps;
    private String classement;
    private String dossards;


    @ManyToOne
    @JoinColumn(name = "id_competition")
    private Competition competition;

    @ManyToOne
    @JoinColumn(name = "id_coureur")
    private Coureur coureur;




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Coureur getCoureur() {
        return coureur;
    }

    public void setCoureur(Coureur coureur) {
        this.coureur = coureur;
    }

    public String getTemps() {
        return temps;
    }

    public void setTemps(String temps) {
        this.temps = temps;
    }

    public String getClassement() {
        return classement;
    }

    public void setClassement(String classement) {
        this.classement = classement;
    }

    public String getDossards() {
        return dossards;
    }

    public void setDossards(String dossards) {
        this.dossards = dossards;
    }
}
