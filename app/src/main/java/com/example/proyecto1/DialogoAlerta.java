package com.example.proyecto1;

import android.app.Dialog;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogoAlerta extends DialogFragment {

    public Dialog errorUsuarioExiste(){//Notificación por defecto
        if (getActivity()!=null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("ERROR");
            builder.setMessage(R.string.error_usuario_existe);
            builder.setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            return builder.create();
        }else{
        return null;
        }

    }
}
