package chat.foundtruck.com.br.foundtruckchat.config;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public final class ConfiguracaoFireBase {

    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth firebaseAuth;

    public static DatabaseReference getFirebase(){

        if(referenciaFirebase == null){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }

        return referenciaFirebase;
    }

    public static FirebaseAuth getFirebaseAuth(){

        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }

        return firebaseAuth;

    }


}