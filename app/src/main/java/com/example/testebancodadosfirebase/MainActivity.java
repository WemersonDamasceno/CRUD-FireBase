package com.example.testebancodadosfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.testebancodadosfirebase.DAO.PessoaDAO;
import com.example.testebancodadosfirebase.model.Pessoa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    PessoaDAO pessoaDAO;

    final int GALERIA = 1;
    final int CAMERA = 3;
    final int PERMISSAO_REQUEST = 2;
    //imagens
    Button btGaleria;
    Uri selectedImage;
    Button btCamera;
    ImageView imgView;
    ImageView imgViewList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //imagem BD
    FirebaseStorage storage;
    StorageReference storageReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        listViewDados = findViewById(R.id.listViewDados);
        pessoaDAO = new PessoaDAO();


        //imagens
        btCamera = findViewById(R.id.btCamera);
        btGaleria = findViewById(R.id.btGaleria);
        imgView = findViewById(R.id.imgView);
        imgViewList = findViewById(R.id.imgViewList);


        //Inicializando o banco de dados
        inicializandoFirebase();
        pegarDadosDataBase();
        //inicializando o firebase storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //permissao do usuario
        usuarioPermission();


        listPessoas = new ArrayList<Pessoa>();

        listViewDados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelected = (Pessoa) parent.getItemAtPosition(position);
                editNome.setText(pessoaSelected.getNome());
                editEmail.setText(pessoaSelected.getEmail());
            }
        });


        btGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALERIA);
            }
        });

        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAMERA);
                }
            }
        });


    }

    private void usuarioPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSAO_REQUEST);
            }
        }
    }

    private void pegarDadosDataBase() {
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

    public void inicializandoFirebase() {
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

            pessoaDAO.salvarPessoaBD(pessoa, MainActivity.this);
            //databaseReference.child("Pessoa").child(pessoa.getId()).setValue(pessoa);

            //salvar imagem no banco de dados FireBase;
            if (selectedImage != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                StorageReference ref = storageReference.child("images/" + pessoa.getId());
                ref.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setTitle("Uploaded " + (int) progress + "%");
                    }
                });

            }




            limparCampos();


        } else if (id == R.id.menu_atualizar) {
            Pessoa p = new Pessoa();
            p.setId(pessoaSelected.getId());
            p.setNome(editNome.getText().toString());
            p.setEmail(editEmail.getText().toString());

            //testeDAO
            pessoaDAO.atualizarPessoaBD(p, MainActivity.this);

            //salvar no banco de dados
            //databaseReference.child("Pessoa").child(p.getId()).setValue(p);
            limparCampos();
        } else if (id == R.id.menu_deletar) {
            Pessoa p = new Pessoa();
            p.setId(pessoaSelected.getId());

            //teste PessoaDAO
            pessoaDAO.removerPessoaBD(p, MainActivity.this);

            //remover do banco de dados
            //databaseReference.child("Pessoa").child(p.getId()).removeValue();
            limparCampos();
        }


        return true;
    }

    private void limparCampos() {
        editEmail.setText("");
        editNome.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //acessar galeria
        if (resultCode == RESULT_OK && requestCode == GALERIA) {
            selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            assert selectedImage != null;
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            assert c != null;
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            imgView.setImageBitmap(thumbnail);
        }
        //acessar camera
        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgView.setImageBitmap(imageBitmap);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSAO_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // A permissão foi concedida. Pode continuar
            } else {
                // A permissão foi negada. Precisa ver o que deve ser desabilitado
            }
            return;
        }
    }


}
