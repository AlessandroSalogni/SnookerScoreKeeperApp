package com.example.android.snookerscorekeeper;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static final String CURRENT_PLAYER = "current player";
    public static final String CURRENT_POT_BALL = "current pot ball";
    public static final String COLOUR_SEQUENCE = "colour_sequence";
    public static final String NEXT_BALL_COLOUR_SEQUENCE = "next ball colour sequence";
    public static final String CURRENT_PLAYER_COLOUR = "current player colour";

    public static final int MAX_RED_BALL = 15;
    public static final int START_SCORE = 0;
    public static final int FOUL = -4;

    public enum Ball {
        RED(1, "red"), YELLOW(2, "yellow"), GREEN(3, "green"), BROWN(4, "brown"), BLUE(5, "blue"), PINK(6, "pink"), BLACK(7, "black");

        int point;
        String name;

        Ball(int point, String name) {
            this.point = point;
            this.name = name;
        }

        public int getPoint() {
            return point;
        }

        public String getName() {
            return name;
        }

        public static Ball nextColourBall(Ball ball) {
            switch(ball) {
                case RED:
                    return YELLOW;
                case YELLOW:
                    return GREEN;
                case GREEN:
                    return BROWN;
                case BROWN:
                    return BLUE;
                case BLUE:
                    return PINK;
                case PINK:
                    return BLACK;
                default:
                    return null;
            }
        }
    }

    public enum Round {PLAYER_1("p1"), PLAYER_2("p2");
        String player;

        Round(String player) {
            this.player = player;
        }

        public String getPlayer() {
            return player;
        }
    }

    private Round currentPlayer;
    private Ball potBall;

    private int redBall;
    private int scorePlayer1;
    private int scorePlayer2;
    private int currentPlayerColour;

    private Ball nextBallColourSequence;
    private boolean colourSequence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null)
            initializeState();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        int id;

        for (Round player : Round.values()) {
            id = getResources().getIdentifier(player.getPlayer() + "_score", "id", getPackageName());
            savedInstanceState.putCharSequence(String.valueOf(id), getScoreTextView(player.getPlayer()).getText());

            id = getResources().getIdentifier(player.getPlayer() + "_foul", "id", getPackageName());
            savedInstanceState.putCharSequence(String.valueOf(id), getFoulsTextView(player.getPlayer()).getText());

            for (Ball ball : Ball.values()) {
                id = getResources().getIdentifier(ball.getName() + "_ball_" + player.getPlayer(), "id", getPackageName());
                savedInstanceState.putCharSequence(String.valueOf(id), getScoreBallTextView(ball.getName(), player.getPlayer()).getText());
            }
        }

        for (Ball ball : Ball.values()) {
            id = getResources().getIdentifier(ball.getName() + "_ball_btn", "id", getPackageName());
            savedInstanceState.putCharSequence(String.valueOf(id), getBallBtnTextView(ball.getName()).getText());
        }

        savedInstanceState.putCharSequence(String.valueOf(R.id.match_story), ((TextView) findViewById(R.id.match_story)).getText());
        savedInstanceState.putSerializable(CURRENT_PLAYER, currentPlayer);
        savedInstanceState.putSerializable(CURRENT_POT_BALL, potBall);
        savedInstanceState.putBoolean(COLOUR_SEQUENCE, colourSequence);
        savedInstanceState.putSerializable(NEXT_BALL_COLOUR_SEQUENCE, nextBallColourSequence);
        savedInstanceState.putInt(CURRENT_PLAYER_COLOUR, currentPlayerColour);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int id;
        CharSequence memorizeString;

        for (Round player : Round.values()) {
            id = getResources().getIdentifier(player.getPlayer() + "_score", "id", getPackageName());
            memorizeString = savedInstanceState.getCharSequence(String.valueOf(id));
            getScoreTextView(player.getPlayer()).setText(memorizeString);

            if(player.getPlayer().equals("p1"))
                scorePlayer1 = Integer.parseInt(memorizeString.toString());
            else
                scorePlayer2 = Integer.parseInt(memorizeString.toString());

            id = getResources().getIdentifier(player.getPlayer() + "_foul", "id", getPackageName());
            memorizeString = savedInstanceState.getCharSequence(String.valueOf(id));
            getFoulsTextView(player.getPlayer()).setText(memorizeString);

            for (Ball ball : Ball.values()) {
                id = getResources().getIdentifier(ball.getName() + "_ball_" + player.getPlayer(), "id", getPackageName());
                memorizeString = savedInstanceState.getCharSequence(String.valueOf(id));
                getScoreBallTextView(ball.getName(), player.getPlayer()).setText(memorizeString);
            }
        }

        for (Ball ball : Ball.values()) {
            id = getResources().getIdentifier(ball.getName() + "_ball_btn", "id", getPackageName());
            memorizeString = savedInstanceState.getCharSequence(String.valueOf(id));
            getBallBtnTextView(ball.getName()).setText(memorizeString);

            if(ball.getName().equals("red"))
                redBall = Integer.parseInt(memorizeString.toString());
        }

        memorizeString = savedInstanceState.getCharSequence(String.valueOf(R.id.match_story));
        ((TextView) findViewById(R.id.match_story)).setText(memorizeString);

        currentPlayer = (Round) savedInstanceState.getSerializable(CURRENT_PLAYER);
        potBall = (Ball) savedInstanceState.getSerializable(CURRENT_POT_BALL);
        colourSequence = savedInstanceState.getBoolean(COLOUR_SEQUENCE);
        nextBallColourSequence = (Ball) savedInstanceState.getSerializable(NEXT_BALL_COLOUR_SEQUENCE);

        currentPlayerDecorator(currentPlayerColour = savedInstanceState.getInt(CURRENT_PLAYER_COLOUR));

    }

    public void wrongShot(View v) {
        displayWrongShotMatchStory();
        changeRound(false);
        activeColorSequence();
    }

    public void restartMatch(View v) {
        for(Round player : Round.values()) {
            display(Integer.parseInt(getString(R.string.start_value_score)), getScoreTextView(player.getPlayer()));
            display(Integer.parseInt(getString(R.string.start_value_score)), getFoulsTextView(player.getPlayer()));

            for(Ball ball : Ball.values())
                display(Integer.parseInt(getString(R.string.start_value_score)), getScoreBallTextView(ball.getName(), player.getPlayer()));
        }

        for(Ball ball : Ball.values()) {
            getBallBtnTextView(ball.getName()).setEnabled(true);

            if (!ball.getName().equals("red"))
                display(Integer.parseInt(getString(R.string.start_value_btn)), getBallBtnTextView(ball.getName()));
            else
                display(Integer.parseInt(getString(R.string.start_red_ball)), getBallBtnTextView(ball.getName()));
        }

        initializeState();
    }

    private void initializeState() {
        redBall = MAX_RED_BALL;
        scorePlayer1 = START_SCORE;
        scorePlayer2 = START_SCORE;
        nextBallColourSequence = null;
        colourSequence = false;
        currentPlayer = ((new Random()).nextInt(2) == 0) ? Round.PLAYER_1 : Round.PLAYER_2;
        potBall = null;

        playerStartStyle();
        displayStartMatchStory();
    }

    public void redBallShot(View v) {
        if(isFoulShot(Ball.RED.getName())) {
            manageFoul();
            return;
        }

        potBall = Ball.RED;
        redBall--;

        display(redBall, (TextView) findViewById(R.id.red_ball_btn));
        addScore(potBall.getPoint(), getScoreTextView(), getScoreBallTextView(potBall.getName()));
        displayPotBallMatchStory();

        if(redBall == 0)
            getBallBtnTextView(potBall.getName()).setEnabled(false);
    }

    public void colourBallShot(View v) {
        if(isFoulShot(Ball.BLACK.getName()) && !colourSequence) {
            manageFoul();
            return;
        }

        switch(v.getId()) {
            case R.id.yellow_ball_btn:
                potBall = Ball.YELLOW;
                break;
            case R.id.green_ball_btn:
                potBall = Ball.GREEN;
                break;
            case R.id.brown_ball_btn:
                potBall = Ball.BROWN;
                break;
            case R.id.blue_ball_btn:
                potBall = Ball.BLUE;
                break;
            case R.id.pink_ball_btn:
                potBall = Ball.PINK;
                break;
            case R.id.black_ball_btn:
                potBall = Ball.BLACK;
                break;
        }

        displayPotBallMatchStory();

        if(colourSequence) {
            if(nextBallColourSequence != potBall)
                manageFoul();
            else {
                display(0, getBallBtnTextView(potBall.getName()));
                getBallBtnTextView(potBall.getName()).setEnabled(false);

                addScore(potBall.getPoint(), getScoreTextView(), getScoreBallTextView(potBall.getName()));
                nextBallColourSequence = Ball.nextColourBall(potBall);
            }
        }
        else {
            addScore(potBall.getPoint(), getScoreTextView(), getScoreBallTextView(potBall.getName()));
            activeColorSequence();
        }
    }

    private void addScore(int point, TextView scoreTextView, TextView scoreBallTextView) {
        int score = (getCurrentPlayer().equals(Round.PLAYER_1.getPlayer())) ? (scorePlayer1 += point) : (scorePlayer2 += point);

        display(score, scoreTextView);

        if(scoreBallTextView != null)
            display(Integer.parseInt(scoreBallTextView.getText().toString()) + 1, scoreBallTextView);
    }

    private void display(int number, TextView v) {
        v.setText(String.valueOf(number));
    }

    private void activeColorSequence() {
        if (redBall == 0 && !colourSequence) {
            colourSequence = true;
            nextBallColourSequence = Ball.YELLOW;
        }
    }

    private boolean isFoulShot(String currentBall) {
        if(currentBall.equals(Ball.RED.getName()) && potBall == Ball.RED)
            return true;
        if(!currentBall.equals(Ball.RED.getName()) && potBall != Ball.RED)
            return true;

        return false;
    }

    private void manageFoul() {
        display(Integer.parseInt(getFoulsTextView().getText().toString()) + 1, getFoulsTextView());
        addScore(FOUL, getScoreTextView(), null);
        displayFoulMatchStory();
        changeRound(true);
    }

    private void changeRound(boolean changeForFoul) {
        potBall = null;
        currentPlayerCancelDecorator();

        if(getCurrentPlayer().equals(Round.PLAYER_1.getPlayer()))
            currentPlayer = Round.PLAYER_2;
        else
            currentPlayer = Round.PLAYER_1;

        if(changeForFoul)
            currentPlayerDecorator(currentPlayerColour = getResources().getColor(R.color.playerFoul));
        else
            currentPlayerDecorator(currentPlayerColour = getResources().getColor(R.color.playerWrongShot));
    }


    private void playerStartStyle() {
        playerModifyStyle(getResources().getColor(R.color.white), Typeface.DEFAULT, Round.PLAYER_1.getPlayer());
        playerModifyStyle(getResources().getColor(R.color.white), Typeface.DEFAULT, Round.PLAYER_2.getPlayer());
        currentPlayerDecorator(currentPlayerColour = getResources().getColor(R.color.playerStartColor));
    }

    private void currentPlayerDecorator(int colour) {
        playerModifyStyle(colour, Typeface.DEFAULT_BOLD, currentPlayer.getPlayer());
    }

    private void currentPlayerCancelDecorator() {
        playerModifyStyle(getResources().getColor(R.color.white), Typeface.DEFAULT, currentPlayer.getPlayer());
    }

    private void playerModifyStyle(int colour, Typeface style, String player) {
        TextView playerText = findViewById(getResources().getIdentifier(player + "_text", "id", getPackageName()));

        playerText.setTextColor(colour);
        playerText.setTypeface(style);
    }

    private TextView getFoulsTextView() {
        return (TextView) findViewById(getResources().getIdentifier(getCurrentPlayer() + "_fouls", "id", getPackageName()));
    }

    private TextView getFoulsTextView(String player) {
        return (TextView) findViewById(getResources().getIdentifier(player + "_fouls", "id", getPackageName()));
    }

    private TextView getScoreTextView() {
        return (TextView) findViewById(getResources().getIdentifier(getCurrentPlayer() + "_score", "id", getPackageName()));
    }

    private TextView getScoreTextView(String player) {
        return (TextView) findViewById(getResources().getIdentifier(player + "_score", "id", getPackageName()));
    }

    private TextView getScoreBallTextView(String colourBall) {
        return (TextView) findViewById(getResources().getIdentifier(colourBall + "_ball_" + getCurrentPlayer(), "id", getPackageName()));
    }

    private TextView getScoreBallTextView(String colourBall, String player) {
        return (TextView) findViewById(getResources().getIdentifier(colourBall + "_ball_" + player, "id", getPackageName()));
    }

    private TextView getBallBtnTextView(String colourBall) {
        return (TextView) findViewById(getResources().getIdentifier(colourBall + "_ball_btn", "id", getPackageName()));
    }

    private String getCurrentPlayer() {
        return currentPlayer.getPlayer();
    }

    private void displayStartMatchStory() {
        String player = (getCurrentPlayer().equals("p1")) ? "Player 1: " : "Player 2: ";
        displayMatchStory(player + " start match");
    }

    private void displayPotBallMatchStory() {
        displayMatchStory("pot " + potBall.getName() + " ball, +" + potBall.getPoint() + " point");
    }

    private void displayFoulMatchStory() {
        displayMatchStory("foul " + FOUL + " point, change player!!");
    }

    private void displayWrongShotMatchStory() {
        displayMatchStory("wrong shot, change player!!");
    }

    private void displayMatchStory(String story) {
        ((TextView) findViewById(R.id.match_story)).setText(story);
    }
}
