package com.example.testebancodadosfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.testebancodadosfirebase.model.Pessoa;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    EditText editNome;
    EditText editEmail;
    ListView listViewDados;
    List<Pessoa> listPessoas;
    ArrayAdapter<Pessoa> arrayAdapter;
    Pessoa pessoaSelected;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        listViewDados = findViewById(R.id.listViewDados);

        //Inicializando o banco de dados
        inicializandoFirebase();
        eventoDataBase();

        listPessoas = new ArrayList<Pessoa>();

        listViewDados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelected = (Pessoa) parent.getItemAtPosition(position);
                editNome.setText(pessoaSelected.getNome());
                editEmail.setText(pessoaSelected.getEmail());
            }
        });


    }

    private void eventoDataBase() {
        databaseReference.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPessoas.clear();
                for (DataSnapshot obj : dataSnapshot.getChildren()) {
                    Pessoa p = obj.getValue(Pessoa.class);
                    listPessoas.add(p);
                }
                arrayAdapter = new ArrayAdapter<Pessoa>(MainActivity.this, android.R.layout.simple_list_item_1, listPessoas);
                listViewDados.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializandoFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_novo) {
            Pessoa pessoa = new Pessoa();
            pessoa.setId(UUID.randomUUID().toString());
            pessoa.setNome(editNome.getText().toString());
            pessoa.setEmail(editEmail.getText().toString());

            databaseReference.child("Pessoa").child(pessoa.getId()).setValue(pessoa);
            limparCampos();

        } else if (id == R.id.menu_atualizar) {
            Pessoa p = new Pessoa();
            p.setId(pessoaSelected.getId());
            p.setNome(editNome.getText().toString());
            p.setEmail(editEmail.getText().toString());
            //salvar no banco de dados
            databaseReference.child("Pessoa").child(p.getId()).setValue(p);
            limparCampos();
        } else if (id == R.id.menu_deletar) {
            Pessoa p = new Pessoa();
            p.setId(pessoaSelected.getId());
            //remover do banco de dados
            databaseReference.child("Pessoa").child(p.getId()).removeValue();
            limparCampos();
        }


        return true;
    }

    private void limparCampos() {
        editEmail.setText("");
        editNome.setText("");
    }
}
