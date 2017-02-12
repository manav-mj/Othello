package com.hungryhackers.othello;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int n = 8;
    int flag = 0;

    MyPlayer players[][];
    LinearLayout rows[];
    LinearLayout boardLayout;
    TextView blackPlayerScore, whitePlayerScore, wPlayer, bPlayer;

    int scoreBlack = 2;
    int scoreWhite = 2;

    final int BLACK_PLAYER = 1;
    final int WHITE_PLAYER = 2;
    int playerTurn = 0;
    boolean gameOver = false;

    int iteration_X[] = {-1,-1,-1,0,1,1,1,0};
    int iteration_Y[] = {-1,0,1,1,1,0,-1,-1};

    HashMap<String, ArrayList<Float>> optionsAvailable = new HashMap<>();

    boolean outOfBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boardLayout = (LinearLayout) findViewById(R.id.board_layout);

        whitePlayerScore = (TextView) findViewById(R.id.white_score);
        blackPlayerScore = (TextView) findViewById(R.id.black_score);
        wPlayer = (TextView) findViewById(R.id.w_turn);
        bPlayer = (TextView) findViewById(R.id.b_turn);

        setUpBoard();
        setAvailableTiles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.replay){
            resetBoard();
        }
        return true;
    }

    private void setAvailableTiles() {
        optionsAvailable.clear();
        for (int i=0 ; i<n ; i++){
            for (int j=0 ; j<n ; j++){
                if (players[i][j].isBlack() && playerTurn == BLACK_PLAYER){
                    getNext(i,j,players[i][j].getPlayerStatus());
                }else if (players[i][j].isWhite() && playerTurn == WHITE_PLAYER) {
                    getNext(i,j,players[i][j].getPlayerStatus());
                }
            }
        }

        if (optionsAvailable.size() == 0 && !gameOver){
            if (playerTurn == BLACK_PLAYER){
                playerTurn = WHITE_PLAYER;
                flag ++;
                Toast.makeText(this, "Black Player has no moves left", Toast.LENGTH_SHORT).show();
                bPlayer.setVisibility(View.INVISIBLE);
                wPlayer.setVisibility(View.VISIBLE);
            }else {
                playerTurn = BLACK_PLAYER;
                flag ++;
                Toast.makeText(this, "White Player has no moves left", Toast.LENGTH_SHORT).show();
                bPlayer.setVisibility(View.VISIBLE);
                wPlayer.setVisibility(View.INVISIBLE);
            }
            if (flag == 2){
                gameOver = true;
                Toast.makeText(this, "No Players has any moves!! GAMEOVER", LENGTH_LONG);
                flag = 0;
            }else {
                setAvailableTiles();
            }
        }else {
            flag = 0;
            for (String coord : optionsAvailable.keySet()) {
                String[] s = coord.split("\\.");
                int a = Integer.parseInt(s[0]);
                int b = Integer.parseInt(s[1]);
                players[a][b].setAvailable(true);
                players[a][b].displayAvailable();
            }
        }
    }

    private void getNext(int x, int y, boolean playerStat) {
        for (int i=0 ; i<8 ; i++){
            ArrayList<Float> pathCoordinates = new ArrayList<>();
            outOfBound = false;
            int a = x + iteration_X[i];
            int b = y + iteration_Y[i];
            if ((a>=0 && a<n) && (b>=0 && b<n)) {
                if (players[a][b].getPlayerStatus() == !playerStat && !players[a][b].isEmpty()) {
                    pathCoordinates.add(encodeCoordinate(a,b));
                    int c = a + iteration_X[i];
                    int d = b + iteration_Y[i];
                    if ((c>=0 && c<n) && (d>=0 && d<n)) {
                        if ((players[c][d].getPlayerStatus() == players[a][b].getPlayerStatus()) && !players[c][d].isEmpty()) {
                            pathCoordinates.add(encodeCoordinate(c,d));
                        }
                        while ((players[c][d].getPlayerStatus() == players[a][b].getPlayerStatus()) && !players[c][d].isEmpty()) {
                            if ((c + iteration_X[i]>=0 && c + iteration_X[i]<n) && (d + iteration_Y[i]>=0 && d + iteration_Y[i]<n)) {
                                c = c + iteration_X[i];
                                d = d + iteration_Y[i];
                                pathCoordinates.add(encodeCoordinate(c,d));
                            }else {
                                outOfBound = true;
                                break;
                            }
                        }
                        if (outOfBound){
                            pathCoordinates.clear();
                        }
                        if (!outOfBound) {
                            float coordinate = encodeCoordinate(c,d);
                            pathCoordinates.add(coordinate);
                            String coord = String.valueOf(coordinate);
                            if (optionsAvailable.containsKey(coord)){
                                pathCoordinates.addAll(optionsAvailable.get(coord));
                            }
                            if (players[c][d].isEmpty()) {
                                optionsAvailable.put(coord, pathCoordinates);
                            }
                        }
                    }
                }
            }
        }
    }

    public void resetBoard(){
        playerTurn = BLACK_PLAYER;
        gameOver = false;

        for (int i=0 ; i<n ; i++){
            for (int j=0 ; j<n ; j++){
                players[i][j].resetValues();
                players[i][j].setImageBitmap(null);
            }
        }

        //Setting initial pawns
        int k = n/2 - 1;
        players[k][k].setWhite();
        players[k][k+1].setBlack();
        players[k+1][k].setBlack();
        players[k+1][k+1].setWhite();

        scoreBlack = 2;
        scoreWhite = 2;

        blackPlayerScore.setText("X " + Integer.toString(scoreBlack));
        whitePlayerScore.setText("X " + Integer.toString(scoreWhite));

        setAvailableTiles();

        bPlayer.setVisibility(View.VISIBLE);
        wPlayer.setVisibility(View.INVISIBLE);
    }

    private void setUpBoard() {

        players = new MyPlayer[n][n];
        rows = new LinearLayout[n];
        boardLayout.removeAllViews();

        playerTurn = BLACK_PLAYER;

        for (int i=0 ; i<n ; i++){
            rows[i] = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
            rows[i].setLayoutParams(params);
            rows[i].setOrientation(LinearLayout.HORIZONTAL);
            boardLayout.addView(rows[i]);
        }

        for (int i=0 ; i<n ; i++){
            for (int j=0 ; j<n ; j++){
                players[i][j] = new MyPlayer(this);
                players[i][j].x_coord = i;
                players[i][j].y_coord = j;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                players[i][j].setLayoutParams(params);
                players[i][j].setPadding(2,2,2,2);
                players[i][j].setOnClickListener(this);
                rows[i].addView(players[i][j]);

                //Setting the Background
                if ((i+j)%2 == 0){
                    players[i][j].setBackground(getDrawable(R.drawable.board_bg_dark));
                }else {
                    players[i][j].setBackground(getDrawable(R.drawable.board_bg_light));
                }
            }
        }

        //Setting initial pawns
        int k = n/2 - 1;
        players[k][k].setWhite();
        players[k][k+1].setBlack();
        players[k+1][k].setBlack();
        players[k+1][k+1].setWhite();
    }

    private void fillPath(int playerType, MyPlayer p) {
        String s = String.valueOf(encodeCoordinate(p.x_coord , p.y_coord));
        ArrayList<Float> tempPath = optionsAvailable.get(s);
        ArrayList<Float> temp = new ArrayList<>();

        for (float coordinate : tempPath) {
            int a = (int) coordinate;
            int b = fractionalPart(coordinate);
            if (!temp.contains(coordinate)) {
                temp.add(coordinate);
                if (playerType == BLACK_PLAYER) {
                    scoreBlack++;
                    if (players[a][b].isWhite()) {
                        scoreWhite--;
                    }
                    players[a][b].setBlack();
                } else if (playerType == WHITE_PLAYER) {
                    scoreWhite++;
                    if (players[a][b].isBlack()) {
                        scoreBlack--;
                    }
                    players[a][b].setWhite();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (gameOver) {
            return;
        }

        MyPlayer p = (MyPlayer)v;
        if (p.isAvailable()){
            if(playerTurn == BLACK_PLAYER){
                fillPath(BLACK_PLAYER, p);
                playerTurn = WHITE_PLAYER;
                bPlayer.setVisibility(View.INVISIBLE);
                wPlayer.setVisibility(View.VISIBLE);
            }else if (playerTurn == WHITE_PLAYER){
                fillPath(WHITE_PLAYER, p);
                playerTurn = BLACK_PLAYER;
                bPlayer.setVisibility(View.VISIBLE);
                wPlayer.setVisibility(View.INVISIBLE);
            }
            if (scoreBlack + scoreWhite == n*n){
                if (scoreBlack > scoreWhite){
                    Toast.makeText(this, "Black Player Wins !!", LENGTH_LONG).show();
                }else {
                    Toast.makeText(this, "White Player Wins !!", LENGTH_LONG).show();
                }
                gameOver = true;
            }
            clearPrevSuggestions();
            setAvailableTiles();
        }

        blackPlayerScore.setText("X " + Integer.toString(scoreBlack));
        whitePlayerScore.setText("X " + Integer.toString(scoreWhite));
    }

    private void clearPrevSuggestions() {

        for (String coord : optionsAvailable.keySet()){
            String[] s = coord.split("\\.");
            int a = Integer.parseInt(s[0]);
            int b = Integer.parseInt(s[1]);

            players[a][b].setAvailable(false);
            if (players[a][b].isEmpty()){
                players[a][b].setImageBitmap(null);
            }
        }
    }

    private int fractionalPart(float coordinate) {
        String temp = String.valueOf(coordinate);
        String[] sub = temp.split("\\.");
        return Integer.parseInt(sub[1]);
    }
    private Float encodeCoordinate(int a, int b) {
        return (float) a + (float) b / 1000;
    }
}
