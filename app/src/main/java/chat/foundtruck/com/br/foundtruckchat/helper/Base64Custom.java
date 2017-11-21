package chat.foundtruck.com.br.foundtruckchat.helper;

import android.util.Base64;

/**
 * Created by Gollum on 20/11/2017.
 */

public class Base64Custom {

    public static String codificar64(String s){
        return  Base64.encodeToString(s.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String decodificar64(String s){
        return  new String(Base64.decode(s.getBytes(), Base64.DEFAULT));
    }

}
