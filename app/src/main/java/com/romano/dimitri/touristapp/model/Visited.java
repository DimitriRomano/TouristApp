package com.romano.dimitri.touristapp.model;

public class Visited {
    private int idPlace;
    private String pseudo;

    public Visited(){ }

    public Visited(int idPlace, String pseudo) {
        this.idPlace = idPlace;
        this.pseudo=pseudo;
    }

    public int getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(int idPlace) {
        this.idPlace = idPlace;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    @Override
    public String toString() {
        return "Visited{" +
                "idPlace=" + idPlace +
                ", pseudo='" + pseudo + '\'' +
                '}';
    }
}
