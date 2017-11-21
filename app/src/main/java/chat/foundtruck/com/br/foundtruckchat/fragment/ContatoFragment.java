package chat.foundtruck.com.br.foundtruckchat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import chat.foundtruck.com.br.foundtruckchat.R;
import chat.foundtruck.com.br.foundtruckchat.activity.ConversaActivity;
import chat.foundtruck.com.br.foundtruckchat.adapter.ContatoAdapter;
import chat.foundtruck.com.br.foundtruckchat.config.ConfiguracaoFireBase;
import chat.foundtruck.com.br.foundtruckchat.helper.Preferencias;
import chat.foundtruck.com.br.foundtruckchat.model.Contato;

public class ContatoFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contato> contatos;
    private DatabaseReference reference;
    private ValueEventListener eventListener;



    public ContatoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if(reference != null)
            reference.addValueEventListener(eventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(reference != null)
            reference.removeEventListener(eventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contatos = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_contato, container, false);
        listView = (ListView) view.findViewById(R.id.lv_contatos);
        /*adapter = new ArrayAdapter(
                getActivity(),
                R.layout.lista_contato ,
                contatos
        );*/

        adapter = new ContatoAdapter(getActivity(), contatos);

        listView.setAdapter(adapter);

        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();
        if(identificadorUsuarioLogado != null){
            reference = ConfiguracaoFireBase.getFirebase().child("contatos").child(identificadorUsuarioLogado);
        }


        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Limpar lista
                contatos.clear();


                //Listar contatos
                for(DataSnapshot dados : dataSnapshot.getChildren()){

                    Contato contato = dados.getValue( Contato.class );
                    contatos.add(contato);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                //ENVIANDO DADOS

                Contato contato = contatos.get(position);

                intent.putExtra("nome", contato.getNome());
                intent.putExtra("email", contato.getEmail());

                startActivity(intent);

            }
        });

        return view;
    }
}
