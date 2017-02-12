package com.hungryhackers.othello;

import android.content.Context;
import android.widget.ImageView;


public class MyPlayer extends ImageView {

    public boolean isBlack() {
        return black;
    }
    public boolean isWhite() {
        return white;
    }
    public boolean getPlayerStatus(){
        return black;
    }
    public void setBlack() {
        black = true;
        white = false;
        empty = false;
        this.setImageAlpha(255);
        this.setImageResource(R.drawable.player_black);
    }
    public void setWhite() {
        white = true;
        black = false;
        empty = false;
        this.setImageAlpha(255);
        this.setImageResource(R.drawable.player_white);
    }
    public boolean isAvailable() {
        return available;
    }
    public void displayAvailable(){
        this.setImageAlpha(100);
        this.setImageResource(R.drawable.option_available);
    }
    public void setAvailable(boolean available) {

        this.available = available;
    }
    public boolean isEmpty() {
        return empty;
    }
    public void resetValues(){
        black = false;
        white = false;
        available = false;
        empty = true;
    }

    private boolean black;
    private boolean white;
    private boolean available;
    private boolean empty;

    int x_coord, y_coord;

    public MyPlayer(Context context) {
        super(context);
        white = false;
        black = false;
        available = false;
        empty = true;
    }
}
