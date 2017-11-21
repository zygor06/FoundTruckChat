package chat.foundtruck.com.br.foundtruckchat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import chat.foundtruck.com.br.foundtruckchat.R;
import chat.foundtruck.com.br.foundtruckchat.adapter.MensagemAdapter;
import chat.foundtruck.com.br.foundtruckchat.config.ConfiguracaoFireBase;
import chat.foundtruck.com.br.foundtruckchat.helper.Base64Custom;
import chat.foundtruck.com.br.foundtruckchat.helper.Preferencias;
import chat.foundtruck.com.br.foundtruckchat.model.Conversa;
import chat.foundtruck.com.br.foundtruckchat.model.Mensagem;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton btEnviar;
    private DatabaseReference firebase;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenerMensagem;

    //dados destinatário
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //dados remetente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        btEnviar = (ImageButton) findViewById(R.id.btn_enviar);
        listView = (ListView) findViewById(R.id.lv_conversas);

        final Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();

        Bundle extra = getIntent().getExtras();


        if(extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            idUsuarioDestinatario = Base64Custom.codificar64(extra.getString("email"));
        }

        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.vtr_left_arrow);
        setSupportActionBar(toolbar);


        //Monta lisview e adapter
        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(ConversaActivity.this, mensagens);
        /*adapter = new ArrayAdapter(
                ConversaActivity.this,
                android.R.layout.simple_list_item_1,
                mensagens
        );*/

        listView.setAdapter(adapter);

        //recupera mensagens
        firebase = ConfiguracaoFireBase.getFirebase()
                .child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mensagens.clear();

                for(DataSnapshot dado : dataSnapshot.getChildren()){

                    Mensagem mensagem = dado.getValue(Mensagem.class);
                    mensagens.add(mensagem);


                }

                adapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerMensagem);

        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoMensagem = editMensagem.getText().toString();

                if(textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this, "Digite uma mensagem para enviar", Toast.LENGTH_SHORT).show();
                }else{
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                    Boolean retornoRemetente = salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                    if(!retornoRemetente){
                        Toast.makeText(ConversaActivity.this, "Problema ao salvar mensagem, tente novamente", Toast.LENGTH_SHORT).show();
                    }else {

                        Boolean retornoDestinatario = salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                        if (!retornoDestinatario) {
                            Toast.makeText(ConversaActivity.this, "Problema ao enviar mensagem para o destinatário", Toast.LENGTH_SHORT).show();
                        }
                    }


                    //Salvar conversa

                    nomeUsuarioRemetente = preferencias.getNome();
                    Toast.makeText(ConversaActivity.this, nomeUsuarioRemetente, Toast.LENGTH_SHORT).show();
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(textoMensagem);
                    Boolean retornoConversaRemetente = salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, conversa);
                    if(!retornoConversaRemetente){
                        Toast.makeText(ConversaActivity.this, "Problema ao salvar conversa", Toast.LENGTH_SHORT).show();
                    }else{
                        Conversa conversa1 = new Conversa();
                        conversa1.setIdUsuario(idUsuarioRemetente);
                        conversa1.setNome(nomeUsuarioRemetente);
                        conversa1.setMensagem(textoMensagem);
                        Boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestinatario,idUsuarioRemetente, conversa1);
                        if(!retornoConversaDestinatario){
                            Toast.makeText(ConversaActivity.this, "Problema ao salvar conversa", Toast.LENGTH_SHORT).show();
                        }
                    }

                    editMensagem.setText("");
                }


            }
        });
    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try{

            firebase = ConfiguracaoFireBase.getFirebase().child("mensagens");
            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try{

            firebase = ConfiguracaoFireBase.getFirebase().child("conversas");
            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .setValue(conversa);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(adapter.getCount() - 1);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}
