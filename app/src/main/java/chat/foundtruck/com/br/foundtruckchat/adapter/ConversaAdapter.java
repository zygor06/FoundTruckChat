package chat.foundtruck.com.br.foundtruckchat.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import chat.foundtruck.com.br.foundtruckchat.R;
import chat.foundtruck.com.br.foundtruckchat.helper.Preferencias;
import chat.foundtruck.com.br.foundtruckchat.model.Conversa;

/**
 * Created by Gollum on 20/11/2017.
 */

public class ConversaAdapter extends ArrayAdapter<Conversa> {

    private ArrayList<Conversa> conversas;
    private Context context;


    public ConversaAdapter(@NonNull Context c, @NonNull ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.conversas = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = null;

        if(conversas != null){

            Preferencias preferencias = new Preferencias(context);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_conversa, parent, false);

            Typeface signature = ResourcesCompat.getFont(context, R.font.signage_regular);
            Typeface menu = ResourcesCompat.getFont(context, R.font.menu_regular);

            TextView nomeConversa = (TextView) view.findViewById(R.id.tv_nomeConversa);
            TextView ultimaMensagem = (TextView) view.findViewById(R.id.tv_ultimaMensagem);

            nomeConversa.setTypeface(signature);
            nomeConversa.setTextSize(20);
            ultimaMensagem.setTypeface(menu);
            ultimaMensagem.setTextSize(20);

            Conversa conversa = conversas.get(position);

            nomeConversa.setText(conversa.getNome());

            ultimaMensagem.setText(conversa.getMensagem());

        }

        return view;
    }
}
