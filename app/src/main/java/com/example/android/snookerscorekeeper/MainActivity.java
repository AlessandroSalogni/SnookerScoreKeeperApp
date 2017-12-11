package com.example.android.snookerscorekeeper;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static final String PLAYER_1 = "p1";
    public static final String PLAYER_2 = "p2";

    public enum Ball {
        RED(1), YELLOW(2), GREEN(3), BROWN(4), BLUE(5), PINK(6), BLACK(7);

        int point;

        Ball(int point) {
            this.point = point;
        }

        public int getPoint() {
            return point;
        }
    }

    public enum Round {PLAYER_1, PLAYER_2}

    private Round round;
    private Ball potBall;
    private int redBall = 15;
    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeRound();
    }

    public void redBallShot(View v) {
        potBall = Ball.RED;

        if(!isWrongShot()) {
            redBall--;
            display(redBall, (TextView) findViewById(R.id.red_ball_btn));
            addScore(potBall.getPoint(), getScoreTextView(), getScoreBallTextView("red"));
        }
        else
            changeRound();
    }

    public void colourBallShot(View v) {
        String colourBall = null;

        switch(v.getId()) {
            case R.id.yellow_ball_btn:
                colourBall = "yellow";
                potBall = Ball.YELLOW;
                break;
            case R.id.green_ball_btn:
                colourBall = "green";
                potBall = Ball.GREEN;
                break;
            case R.id.brown_ball_btn:
                colourBall = "brown";
                potBall = Ball.BROWN;
                break;
            case R.id.blue_ball_btn:
                colourBall = "blue";
                potBall = Ball.BLUE;
                break;
            case R.id.pink_ball_btn:
                colourBall = "pink";
                potBall = Ball.PINK;
                break;
            case R.id.black_ball_btn:
                colourBall = "black";
                potBall = Ball.BLACK;
                break;
        }

        if(!isWrongShot())
            addScore(potBall.getPoint(), getScoreTextView(), getScoreBallTextView(colourBall));
        else
            changeRound();
    }

    private void addScore(int point, TextView scoreTextView, TextView scoreBallTextView) {
        int score = (getCurrentPlayer().equals(PLAYER_1)) ? (scorePlayer1 += point) : (scorePlayer2 += point);

        display(score, scoreTextView);
        display(Integer.parseInt(scoreBallTextView.getText().toString()) + 1, scoreBallTextView);
    }

    private void display(int number, TextView v) {
        v.setText(String.valueOf(number));
    }

    private boolean isWrongShot() {
        return (new Random()).nextInt(6) == 0;
    }

    private void changeRound() {
        currentPlayerCancelDecoretor();

        if(getCurrentPlayer().equals(PLAYER_1))
            round = Round.PLAYER_2;
        else
            round = Round.PLAYER_1;

        currentPlayerDecoretor();
    }

    private void currentPlayerDecoretor() {
        currentPlayerModifyStyle("#bbdefb", Typeface.DEFAULT_BOLD);
    }

    private void currentPlayerCancelDecoretor() {
        currentPlayerModifyStyle("#ffffff", Typeface.DEFAULT);
    }

    private void currentPlayerModifyStyle(String colour, Typeface style) {
        TextView playerText = findViewById(getResources().getIdentifier(getCurrentPlayer() + "_text", "id", getPackageName()));

        playerText.setTextColor(Color.parseColor(colour));
        playerText.setTypeface(style);
    }

    private TextView getScoreTextView() {
        return (TextView) findViewById(getResources().getIdentifier(getCurrentPlayer() + "_score", "id", getPackageName()));
    }

    private TextView getScoreBallTextView(String colourBall) {
        return (TextView) findViewById(getResources().getIdentifier(colourBall + "_ball_" + getCurrentPlayer(), "id", getPackageName()));
    }

    private String getCurrentPlayer() {
        return (round == Round.PLAYER_1) ? PLAYER_1 : PLAYER_2;
    }
}
