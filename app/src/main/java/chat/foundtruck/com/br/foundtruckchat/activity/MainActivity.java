package chat.foundtruck.com.br.foundtruckchat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import chat.foundtruck.com.br.foundtruckchat.R;
import chat.foundtruck.com.br.foundtruckchat.adapter.TabAdapter;
import chat.foundtruck.com.br.foundtruckchat.config.ConfiguracaoFireBase;
import chat.foundtruck.com.br.foundtruckchat.helper.Base64Custom;
import chat.foundtruck.com.br.foundtruckchat.helper.Preferencias;
import chat.foundtruck.com.br.foundtruckchat.helper.SlidingTabLayout;
import chat.foundtruck.com.br.foundtruckchat.model.Contato;
import chat.foundtruck.com.br.foundtruckchat.model.Usuario;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth authenticator;
    private Preferencias preferencias;
    private Toolbar toolbar;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    private String identificadorContato;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authenticator = ConfiguracaoFireBase.getFirebaseAuth();

        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("FoundTruck Chat");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        slidingTabLayout =findViewById(R.id.stl_tabs);
        viewPager = findViewById(R.id.vp_pagina);

        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));

        //Configurar adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);

    }

    private void logout(){
        authenticator = ConfiguracaoFireBase.getFirebaseAuth();
        authenticator.signOut();
        preferencias = new Preferencias(MainActivity.this);
        preferencias.limparUsuarioPreferencias();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void abrirCadastroContato(){

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //Configurações
        alert.setTitle("Novo contato");
        alert.setMessage("E-mail do usuário");
        alert.setCancelable(false);
        final EditText editText = new EditText(MainActivity.this);
        alert.setView(editText);


        //Configura botoes
        alert.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String emailContato = editText.getText().toString();

                if(emailContato.isEmpty()){
                    Toast.makeText(MainActivity.this, "Preencha o e-mail", Toast.LENGTH_SHORT).show();
                }
                else{

                    identificadorContato = Base64Custom.codificar64(emailContato);

                    //Recuperar firebase

                    reference = ConfiguracaoFireBase.getFirebase();
                    reference = reference.child("usuarios").child(identificadorContato);

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue() != null){

                                //Recuperar dados do contato a ser adicionado
                                Usuario usuarioContato = dataSnapshot.getValue(Usuario.class);


                                //Recuperar identificador usuario logado (base 64)
                                Preferencias preferencia = new Preferencias(MainActivity.this);
                                String identificadorUsuario = preferencia.getIdentificador();

                                reference = ConfiguracaoFireBase.getFirebase();
                                reference = reference.child("contatos")
                                        .child(identificadorUsuario)
                                        .child(identificadorContato);

                                Contato contato = new Contato();
                                contato.setIdentificador(identificadorContato);
                                contato.setEmail(usuarioContato.getEmail());
                                contato.setNome(usuarioContato.getNome());

                                reference.setValue(contato);

                                Toast.makeText(MainActivity.this, "Usuário inserido na lista de contatos", Toast.LENGTH_SHORT).show();

                            }else{

                                Toast.makeText(MainActivity.this, "Usuário não possui cadastro", Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.create();
        alert.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sair:
                logout();
                return true;
            case R.id.item_add:
                abrirCadastroContato();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
