package com.example.diceroller.Dice;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Random;

public class Dice implements Parcelable {

    private final int numberOfSides;

    private int luckySide;

    private int currentSide;
    public Dice(int numberOfSides) {
        this.numberOfSides = numberOfSides;
        this.currentSide = -1;

        // Roll internally to get a lucky side
        this.luckySide = new Random().nextInt(this.numberOfSides) + 1;
    }

    protected Dice(Parcel in) {
        numberOfSides = in.readInt();
        luckySide = in.readInt();
        currentSide = in.readInt();
    }

    public static final Creator<Dice> CREATOR = new Creator<Dice>() {
        @Override
        public Dice createFromParcel(Parcel in) {
            return new Dice(in);
        }

        @Override
        public Dice[] newArray(int size) {
            return new Dice[size];
        }
    };

    public int getNumberOfSides() {
        return numberOfSides;
    }

    public int getLuckySide() {
        return this.luckySide;
    }

    public void setLuckySide(int luckySide) {
        this.luckySide = luckySide;
    }

    public void setCurrentSide(int side) {
        this.currentSide = side;
    }

    public int getCurrentSide() {
        return this.currentSide;
    }

    public int roll() {
        this.currentSide = new Random().nextInt(numberOfSides) + 1;
        return this.currentSide;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(numberOfSides);
        dest.writeInt(luckySide);
        dest.writeInt(currentSide);
    }
}
