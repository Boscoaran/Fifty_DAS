package com.example.proyecto1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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

public class VoteActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votar);

        GameBD bd = GameBD.getmDB(this);

        Intent intent = getIntent();
        String user = intent.getStringExtra("user");

        String[] options = bd.getTodaysOptions();//obtener opciones de hoy

        Long clash_id = Long.parseLong(options[0]);
        String option1 = options[1];//opcion arriba
        String option2 = options[2];//opcion abajo

        Button btnOption1 = findViewById(R.id.btnOption1);
        btnOption1.setText(option1);
        Button btnOption2 = findViewById(R.id.btnOption2);
        btnOption2.setText(option2);
        Transition transitionButtons = new ChangeBounds().setInterpolator(new DecelerateInterpolator());//transicion para cambiar de tamaño
        transitionButtons.setDuration(2000);

        ImageView bottomRow = findViewById(R.id.imageView);
        TextView friends = findViewById(R.id.textFriends);


        Boolean userVote = bd.haVotado(clash_id, user); //ver si el usuario ha votado para saber si hay que cargar el voto o permitirle votar

        if (!userVote) {
            btnOption1.setOnClickListener(view -> {
                bd.vota(user, 1);//el usuario vota la opción 1
                float votes = bd.getVotePercent(clash_id);//obtener porcentaje de votos
                TransitionManager.beginDelayedTransition((ViewGroup) view.getParent(), transitionButtons);//transicion
                resizeBtns(votes, btnOption1, btnOption2);//cambiar tamaño
                showTexts(bottomRow, friends);//cambiar textos
                swipeUp(btnOption1, user);//activar pantalla friends
                swipeUp(btnOption2, user);
            });
            btnOption2.setOnClickListener(view -> {
                bd.vota(user, 2);//el usuario vota la opción 2
                float votes = bd.getVotePercent(clash_id);//obtener porcentaje de votos
                TransitionManager.beginDelayedTransition((ViewGroup) view.getParent(), transitionButtons);//transicion
                resizeBtns(votes, btnOption1, btnOption2);//cambiar tamaño
                showTexts(bottomRow, friends);//cambiar textos
                swipeUp(btnOption1, user);//activar pantalla friends
                swipeUp(btnOption2, user);
            });
        }else {
            float votes = bd.getVotePercent(clash_id);//obtener porcentaje de votos
            resizeBtns(votes, btnOption1, btnOption2);//cambiar tamaño, no hay transicion porque se ven con distinto tamaño directamente
            showTexts(bottomRow, friends);//cambiar texto
            swipeUp(btnOption1, user);//activar friends
            swipeUp(btnOption2, user);
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