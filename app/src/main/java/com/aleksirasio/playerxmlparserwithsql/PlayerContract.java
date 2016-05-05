package com.aleksirasio.playerxmlparserwithsql;

import android.provider.BaseColumns;

public final class PlayerContract {
    // empty constructor preventing accidental instantiation
    public PlayerContract() {}

    // Inner class that defines the table contents
    public static abstract class PlayerEntry implements BaseColumns {
        public static final String TABLE_NAME = "player";
        //public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_FULLNAME = "name";
        public static final String COLUMN_NAME_NUMBER = "number";
    }
}
