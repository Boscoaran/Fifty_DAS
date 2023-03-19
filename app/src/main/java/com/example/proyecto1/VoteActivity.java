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

        String[] options = bd.getTodaysOptions();

        Long clash_id = Long.parseLong(options[0]);
        String option1 = options[1];
        String option2 = options[2];

        Button btnOption1 = findViewById(R.id.btnOption1);
        btnOption1.setText(option1);
        Button btnOption2 = findViewById(R.id.btnOption2);
        btnOption2.setText(option2);
        Transition transitionButtons = new ChangeBounds().setInterpolator(new DecelerateInterpolator());
        transitionButtons.setDuration(2000);

        ImageView bottomRow = findViewById(R.id.imageView);
        TextView friends = findViewById(R.id.textFriends);


        Boolean userVote = bd.haVotado(clash_id, user);

        if (!userVote) {
            btnOption1.setOnClickListener(view -> {
                bd.vota(user, 1);
                float votes = bd.getVotePercent(clash_id);
                TransitionManager.beginDelayedTransition((ViewGroup) view.getParent(), transitionButtons);
                resizeBtns(votes, btnOption1, btnOption2);
                showTexts(bottomRow, friends);
                swipeUp(btnOption1, user);
                swipeUp(btnOption2, user);
            });
            btnOption2.setOnClickListener(view -> {
                bd.vota(user, 2);
                float votes = bd.getVotePercent(clash_id);
                TransitionManager.beginDelayedTransition((ViewGroup) view.getParent(), transitionButtons);
                resizeBtns(votes, btnOption1, btnOption2);
                showTexts(bottomRow, friends);
                swipeUp(btnOption1, user);
                swipeUp(btnOption2, user);
            });
        }else {
            float votes = bd.getVotePercent(clash_id);
            resizeBtns(votes, btnOption1, btnOption2);
            showTexts(bottomRow, friends);
            swipeUp(btnOption1, user);
            swipeUp(btnOption2, user);
        }
    }

    private void showTexts(ImageView row, TextView text){
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 1f,
                Animation.RELATIVE_TO_PARENT, 0f
        );
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        text.setVisibility(View.VISIBLE);
        row.setVisibility(View.VISIBLE);
        text.startAnimation(animation);
        row.setAnimation(animation);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void swipeUp(Button btn, String user){
        btn.setOnClickListener(null);
        btn.setOnTouchListener(new OnSwipeTouchListener(VoteActivity.this){
            public void onSwipeTop() {
                Intent intent = new Intent(VoteActivity.this, FriendsActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }
    private void resizeBtns(float newSizeO1, Button btn1, Button btn2){
        float percent01 = newSizeO1;
        float percent02 = 1-percent01;
        newSizeO1 = newSizeO1-0.05f;
        float newSizeO2 = 1-newSizeO1-0.05f;
        if (newSizeO1>0.9){
            newSizeO1 = 0.85f;
            newSizeO2 = 0.10f;
        } else if (newSizeO2>0.9) {
            newSizeO2 = 0.85f;
            newSizeO1 = 0.10f;
        }
        ConstraintLayout.LayoutParams lpBtn1 = (ConstraintLayout.LayoutParams) btn1.getLayoutParams();
        lpBtn1.matchConstraintPercentHeight = newSizeO1;
        btn1.setLayoutParams(lpBtn1);
        String btn1Text = btn1.getText()+"\n"+String.valueOf(Math.round(percent01 * 100));
        btn1.setText(btn1Text);
        ConstraintLayout.LayoutParams lpBtn2 = (ConstraintLayout.LayoutParams) btn2.getLayoutParams();
        lpBtn2.matchConstraintPercentHeight = 1-newSizeO2;
        btn2.setLayoutParams(lpBtn2);
        String btn2Text = btn2.getText()+"\n"+String.valueOf(Math.round(percent02 * 100));
        btn2.setText(btn2Text);
    }
}