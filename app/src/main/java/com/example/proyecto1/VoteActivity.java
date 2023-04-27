package com.example.proyecto1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class VoteActivity extends AppCompatActivity {

    private String opcion1;
    private String opcion2;
    private String enfrentamiento;
    private boolean user_vote;
    private float porcentaje_votos;

    private Context ctx = this;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votar);

        final GameServerDB bd = GameServerDB.getDB();

        Intent intent = getIntent();
        String user = intent.getStringExtra("user");

        try {
            bd.gestion_votos(this, "opciones_hoy", user, 0, "", new GameServerDB.OnResponseListener() {
                @Override
                public void onResponse(JSONObject response) throws JSONException {
                    opcion1 = response.getString("o1");
                    opcion2 = response.getString("o2");
                    enfrentamiento = response.getString("id");
                    String ha_votado = response.getString("ha_votado");
                    if ("0".equals(ha_votado)) {
                        user_vote = false;
                    } else if ("1".equals(ha_votado)){
                        user_vote = true;
                    }
                    Button btnOption1 = findViewById(R.id.btnOption1);
                    btnOption1.setText(opcion1);
                    Button btnOption2 = findViewById(R.id.btnOption2);
                    btnOption2.setText(opcion2);
                    Transition transitionButtons = new ChangeBounds().setInterpolator(new DecelerateInterpolator());//transicion para cambiar de tamaño
                    transitionButtons.setDuration(2000);

                    ImageView bottomRow = findViewById(R.id.imageView);
                    TextView friends = findViewById(R.id.textFriends);

                    if (!user_vote) {
                        btnOption1.setOnClickListener(view -> {
                            try {
                                bd.gestion_votos(ctx, "votar", user, 1, "", new GameServerDB.OnResponseListener() {
                                    @Override
                                    public void onResponse(JSONObject response) throws JSONException {
                                        Log.d("", "onResponse: "+response);
                                        int v1 = response.getInt("v1");
                                        int v2 = response.getInt("v2");
                                        int total = response.getInt("total");
                                        if (v1 == 0 && v2 != 0) {
                                            porcentaje_votos = 0;
                                        } else if (v1 != 0 && v2 == 0) {
                                            porcentaje_votos = 1;
                                        } else if (v1 == 0 && v2 == 0) {
                                            porcentaje_votos = 0.5f;
                                        } else {
                                            porcentaje_votos = (float) v1 / total;
                                        }
                                        TransitionManager.beginDelayedTransition((ViewGroup) view.getParent(), transitionButtons);//transicion
                                        resizeBtns(porcentaje_votos, btnOption1, btnOption2);//cambiar tamaño
                                        showTexts(bottomRow, friends);//cambiar textos
                                        swipeUp(btnOption1, user);//activar pantalla friends
                                        swipeUp(btnOption2, user);
                                    }
                                    @Override
                                    public void onError(VolleyError error){

                                    }
                                });//el usuario vota la opción 1
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        btnOption2.setOnClickListener(view -> {
                            try {
                                bd.gestion_votos(ctx, "votar", user, 2, "", new GameServerDB.OnResponseListener() {
                                    @Override
                                    public void onResponse(JSONObject response) throws JSONException {
                                        int v1 = response.getInt("v1");
                                        int v2 = response.getInt("v2");
                                        int total = response.getInt("total");
                                        if (v1 == 0 && v2 != 0) {
                                            porcentaje_votos = 0;
                                        } else if (v1 != 0 && v2 == 0) {
                                            porcentaje_votos = 1;
                                        } else if (v1 == 0 && v2 == 0) {
                                            porcentaje_votos = 0.5f;
                                        } else {
                                            porcentaje_votos = (float) v1 / total;
                                        }
                                        TransitionManager.beginDelayedTransition((ViewGroup) view.getParent(), transitionButtons);//transicion
                                        resizeBtns(porcentaje_votos, btnOption1, btnOption2);//cambiar tamaño
                                        showTexts(bottomRow, friends);//cambiar textos
                                        swipeUp(btnOption1, user);//activar pantalla friends
                                        swipeUp(btnOption2, user);
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                    }
                                });//el usuario vota la opción 2
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }else {
                        try {
                            bd.gestion_votos(ctx,"get_porcentaje",user, 0, enfrentamiento, new GameServerDB.OnResponseListener()
                            {
                                @Override
                                public void onResponse(JSONObject response) throws JSONException {
                                    int v1 = response.getInt("v1");
                                    int v2 = response.getInt("v2");
                                    int total = response.getInt("total");
                                    if (v1 == 0 && v2 != 0) {
                                        porcentaje_votos = 0;
                                    } else if (v1 != 0 && v2 == 0) {
                                        porcentaje_votos = 1;
                                    } else if (v1 == 0 && v2 == 0) {
                                        porcentaje_votos = 0.5f;
                                    } else {
                                        porcentaje_votos = (float) v1 / total;
                                    }
                                    Log.d("", "onResponse: "+porcentaje_votos);
                                    resizeBtns(porcentaje_votos, btnOption1, btnOption2);//cambiar tamaño, no hay transicion porque se ven con distinto tamaño directamente
                                    showTexts(bottomRow, friends);//cambiar texto
                                    swipeUp(btnOption1, user);//activar friends
                                    swipeUp(btnOption2, user);
                                }

                                @Override
                                public void onError(VolleyError error) {

                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void showTexts(ImageView row, TextView text){
        TranslateAnimation animation = new TranslateAnimation(//ajustes animación
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 1f,
                Animation.RELATIVE_TO_PARENT, 0f
        );
        animation.setDuration(3000);//duración animacion
        animation.setInterpolator(new DecelerateInterpolator());
        text.setVisibility(View.VISIBLE);
        row.setVisibility(View.VISIBLE);
        text.startAnimation(animation);
        row.setAnimation(animation);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void swipeUp(Button btn, String user){//desliza hacia arriba menu amigos
        btn.setOnClickListener(null);//desactivar boton
        btn.setOnTouchListener(new OnSwipeTouchListener(VoteActivity.this){
            public void onSwipeTop() {//nueva actividad
                Intent intent = new Intent(VoteActivity.this, FriendsActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }
    private void resizeBtns(float newSizeO1, Button btn1, Button btn2){
        //calcular porcenajes y tamaños
        float percent01 = newSizeO1;
        float percent02 = 1-percent01;
        newSizeO1 = newSizeO1-0.025f;
        float newSizeO2 = 1-newSizeO1-0.025f;
        if (newSizeO1>0.9){//evitar que uno de los dos botones quede muy pequeño
            newSizeO1 = 0.85f;
            newSizeO2 = 0.10f;
        } else if (newSizeO2>0.9) {
            newSizeO2 = 0.85f;
            newSizeO1 = 0.10f;
        }
        //cambiar contrains boton 1
        ConstraintLayout.LayoutParams lpBtn1 = (ConstraintLayout.LayoutParams) btn1.getLayoutParams();
        lpBtn1.matchConstraintPercentHeight = newSizeO1;
        btn1.setLayoutParams(lpBtn1);
        //cambiar texto boton 1
        String btn1Text = btn1.getText()+"\n"+String.valueOf(Math.round(percent01 * 100));
        btn1.setText(btn1Text);
        //cambiar contrains boton 2
        ConstraintLayout.LayoutParams lpBtn2 = (ConstraintLayout.LayoutParams) btn2.getLayoutParams();
        lpBtn2.matchConstraintPercentHeight = newSizeO2;
        btn2.setLayoutParams(lpBtn2);
        //cambiar texto boton 2
        String btn2Text = btn2.getText()+"\n"+String.valueOf(Math.round(percent02 * 100));
        btn2.setText(btn2Text);
    }
}