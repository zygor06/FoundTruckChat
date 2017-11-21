package chat.foundtruck.com.br.foundtruckchat.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import android.support.v7.widget.Toolbar;

import chat.foundtruck.com.br.foundtruckchat.R;
import chat.foundtruck.com.br.foundtruckchat.config.ConfiguracaoFireBase;
import chat.foundtruck.com.br.foundtruckchat.helper.Base64Custom;
import chat.foundtruck.com.br.foundtruckchat.helper.Preferencias;
import chat.foundtruck.com.br.foundtruckchat.model.Usuario;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText cadastroNome;
    private EditText cadastroEmail;
    private EditText cadastroSenha;
    private Button btnCadastrar;
    private Usuario usuario;
    private Preferencias preferencias;
    //private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cadastroNome = (EditText) findViewById(R.id.editCadastroNome);
        cadastroEmail = (EditText) findViewById(R.id.editCadastroEmail);
        cadastroSenha = (EditText) findViewById(R.id.editCadastroSenha);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);

    }

    public void cadastrarUsuario(View view){

        usuario = new Usuario();

        if(verificaDados()){
            usuario.setNome(cadastroNome.getText().toString());
            usuario.setEmail(cadastroEmail.getText().toString());
            usuario.setSenha(cadastroSenha.getText().toString());

            firebaseAuth = ConfiguracaoFireBase.getFirebaseAuth();
            firebaseAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String identificadorUsuario = Base64Custom.codificar64(usuario.getEmail());
                        usuario.setId(identificadorUsuario);
                        usuario.salvar();

                        preferencias = new Preferencias(CadastroUsuarioActivity.this);
                        preferencias.salvarDados(cadastroEmail.getText().toString(), usuario.getNome());

                        abrirLoginUsuario();

                        Toast.makeText(CadastroUsuarioActivity.this, "Sucesso ao cadastrar usuário", Toast.LENGTH_LONG).show();
                    }else{

                        String errException = "";

                        try{
                            throw task.getException();
                        }catch(FirebaseAuthWeakPasswordException e){
                            errException ="Erro ao cadastrar. Senha fraca.";
                        }catch(FirebaseAuthInvalidCredentialsException e){
                            errException = "Erro ao cadastrar. Email inválido";
                        }catch(FirebaseAuthUserCollisionException e){
                            errException = "Erro ao cadastrar. Usuário já existe.";
                        } catch (Exception e) {
                            e.printStackTrace();
                            errException = "Erro ao cadastrar usuário";
                        }

                        Toast.makeText(CadastroUsuarioActivity.this, errException, Toast.LENGTH_LONG).show();

                    }
                }
            });

        }else{

            Toast.makeText(CadastroUsuarioActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_LONG).show();
        }
    }

    public boolean verificaDados(){
        if(cadastroNome.getText().toString().isEmpty() || cadastroNome.getText().toString().equals("")
                ||cadastroEmail.getText().toString().isEmpty() || cadastroEmail.getText().toString().equals("")
                ||cadastroSenha.getText().toString().isEmpty() || cadastroSenha.getText().toString().equals("")){
            return false;
        }else{
            return true;
        }
    }

    public void abrirLoginUsuario(){
        Intent intent = new Intent(CadastroUsuarioActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}