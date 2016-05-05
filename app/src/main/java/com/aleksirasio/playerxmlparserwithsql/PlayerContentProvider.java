package com.aleksirasio.playerxmlparserwithsql;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

public class PlayerContentProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.aleksirasio.playerxmlparserwithsql.PlayerContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/players" );

    /** Constants to identify the requested operation */
    private static final int PLAYERS = 1;

    private static final UriMatcher uriMatcher ;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "players", PLAYERS);
    }

    /** This content provider does the database operations by this object */
    private PlayerDatabaseHelper mPlayerDb;

    /** A callback method which is invoked when the content provider is starting up */
    @Override
    public boolean onCreate() {
        mPlayerDb = new PlayerDatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if(uriMatcher.match(uri) == PLAYERS){
            Cursor cursor = mPlayerDb.getPlayerCursor();
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        } else{
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO not used in this lab
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        try {
            long id = mPlayerDb.insertPlayer(values);
            Uri returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(returnUri, null);
            return returnUri;
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO not used in this lab
        return 0;
    }

}
