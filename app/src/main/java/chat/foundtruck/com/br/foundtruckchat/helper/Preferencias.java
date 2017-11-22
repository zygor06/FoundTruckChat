package chat.foundtruck.com.br.foundtruckchat.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Gollum on 20/11/2017.
 */

public class Preferencias {

    private Context contexto;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private final String CHAVE_IDENTIFICADOR = "identificador";
    private final String CHAVE_NOME = "nome";
    private final String NOME_ARQUIVO = "foundtruckchat.preferences";

    public Preferencias(Context contextoParametro){

        this.contexto = contextoParametro;
        this.preferences = contexto.getSharedPreferences(NOME_ARQUIVO, Context.MODE_PRIVATE);
        this.editor = preferences.edit();

    }

    public void salvarDados(String id, String nome){

        String identificador;

        if(id != null){
            identificador = Base64Custom.codificar64(id);
        }else{
            identificador = id;
        }

        this.editor.putString(CHAVE_IDENTIFICADOR, identificador);
        this.editor.putString(CHAVE_NOME, nome);
        this.editor.commit();
    }

    public HashMap<String, String> getDadosUsuario(){

        HashMap<String, String> dadosUsuario = new HashMap<>();
        dadosUsuario.put(CHAVE_IDENTIFICADOR, preferences.getString(CHAVE_IDENTIFICADOR, null));
        dadosUsuario.put(CHAVE_NOME, preferences.getString(CHAVE_NOME, null));

        return dadosUsuario;
    }

    public void limparUsuarioPreferencias(){
        salvarDados(null,null);
    }

    public String getIdentificador(){
        return preferences.getString(CHAVE_IDENTIFICADOR, "");
    }
    public String getNome(){
        return preferences.getString(CHAVE_NOME, null);
    }
}
