package com.aleksirasio.playerxmlparserwithsql;

import java.io.Serializable;
import java.util.Observable;

public class Player implements Serializable {

    private final String name;
    private final int number;

    public Player(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return (name + " - " + number);
    }
}
