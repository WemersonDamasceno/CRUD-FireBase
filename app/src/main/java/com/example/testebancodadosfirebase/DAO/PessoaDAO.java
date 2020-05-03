package com.example.testebancodadosfirebase.DAO;


import android.content.Context;
import android.widget.Toast;

import com.example.testebancodadosfirebase.model.Pessoa;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class PessoaDAO extends Pessoa {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    //inicializando o banco de dados
    public void inicializandoFirebase(Context context) {
        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    //salvar no banco
    public boolean salvarPessoaBD(Pessoa p, Context context) {
        inicializandoFirebase(context);
        //salvando no banco
        databaseReference.child("Pessoa").child(p.getId()).setValue(p);
        Toast.makeText(context, "Pessoa Adicionada Com Sucesso!", Toast.LENGTH_LONG).show();
        return true;
    }

    //removendo do banco
    public boolean removerPessoaBD(Pessoa pessoa, Context context) {
        inicializandoFirebase(context);
        databaseReference.child("Pessoa").child(pessoa.getId()).removeValue();
        Toast.makeText(context, "Pessoa Removida Com Sucesso!", Toast.LENGTH_LONG).show();
        return true;
    }

    //atualizando obj no banco
    public boolean atualizarPessoaBD(Pessoa p, Context context) {
        inicializandoFirebase(context);
        databaseReference.child("Pessoa").child(p.getId()).setValue(p);
        Toast.makeText(context, "Pessoa Atualizada Com Sucesso!", Toast.LENGTH_LONG).show();
        return true;
    }


}
