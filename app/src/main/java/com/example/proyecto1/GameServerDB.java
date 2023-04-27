package com.example.proyecto1;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GameServerDB {

    private static GameServerDB mDB;

    private GameServerDB() {

    }

    public static synchronized GameServerDB getDB(){
        if (mDB == null){
            mDB = new GameServerDB();
        }
        return mDB;
    }

    public void gestion_usuarios(Context ctx, String action, String username, String name, String password, OnResponseListener listener) throws IOException {
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jaranguren008/WEB/gestion_usuarios.php";
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("action", action);
            jsonBody.put("username", username);
            jsonBody.put("name", name);
            jsonBody.put("password", password);
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta
                        try {
                            listener.onResponse(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar el error
                listener.onError(error);
            }
        });
        queue.add(request);
    }

    public void gestion_votos(Context ctx, String action, String username, int voto, String enfrentamiento, OnResponseListener listener) throws IOException {
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jaranguren008/WEB/gestion_votos.php";
        RequestQueue queue = Volley.newRequestQueue(ctx);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("action", action);
            jsonBody.put("username", username);
            jsonBody.put("voto", voto);
            jsonBody.put("enfrentamiento", enfrentamiento);
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta
                        try {
                            listener.onResponse(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar el error
                listener.onError(error);
            }
        });
        queue.add(request);
    }

    public void gestion_amigos(Context ctx, String action, String amigo, String username, OnResponseListener listener) throws IOException {
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jaranguren008/WEB/gestion_amigos.php";
        RequestQueue queue = Volley.newRequestQueue(ctx);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("action", action);
            jsonBody.put("user_amigo", amigo);
            jsonBody.put("user", username);
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta
                        try {
                            listener.onResponse(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar el error
                listener.onError(error);
            }
        });
        queue.add(request);
    }

    public interface OnResponseListener{
        void onResponse(JSONObject response) throws JSONException;
        void onError(VolleyError error);
    }
}
