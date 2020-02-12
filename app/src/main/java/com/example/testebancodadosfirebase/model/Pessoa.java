package com.example.testebancodadosfirebase.model;

public class Pessoa {
    private String id;
    private String nome;
    private String email;
    private String idImage;


    public Pessoa(String id, String nome, String email, String idImage) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.idImage = idImage;
    }

    public String getIdImage() {
        return idImage;
    }

    public void setIdImage(String idImage) {
        this.idImage = idImage;
    }

    public Pessoa() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        return nome;
    }

}
