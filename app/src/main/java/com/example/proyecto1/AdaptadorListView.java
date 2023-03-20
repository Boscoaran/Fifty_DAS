package com.example.proyecto1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdaptadorListView  extends BaseAdapter {

    private Context ctx;
    private LayoutInflater inflater;
    private String[] lFriend;
    private int[] imagenes;

    //LISTA PERSONALIZA
    public AdaptadorListView(Context pCtx, String[] pFriends){
        ctx = pCtx;
        lFriend = pFriends;
        //imagenes = pImagenes;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return lFriend.length;
    }

    @Override
    public Object getItem(int i) {
        return lFriend[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.lista_personalizada, null);
        TextView friend = (TextView) view.findViewById(R.id.friendName);

        //Se podrá añadir foto de perfil
        //ImageView img = (ImageView) view.findViewById(R.id.image);

        friend.setText(lFriend[i]);
        //img.setImageResource(imagenes[i]);

        return view;
    }
}
