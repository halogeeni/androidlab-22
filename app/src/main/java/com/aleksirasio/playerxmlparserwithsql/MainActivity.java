package com.aleksirasio.playerxmlparserwithsql;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivity";

    private final int LOADER_ID = 0x01;
    private static final int CREATE_PLAYER = 0;
    private static final String[] PROJECTION = {
        PlayerContract.PlayerEntry._ID,
                PlayerContract.PlayerEntry.COLUMN_NAME_FULLNAME,
                PlayerContract.PlayerEntry.COLUMN_NAME_NUMBER
    };

    private PlayerDatabaseHelper dbHelper;
    private SimpleCursorAdapter mAdapter;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new PlayerDatabaseHelper(this);
        dbHelper.initDb();
        dbHelper.close();

        mAdapter = new SimpleCursorAdapter(this,
                R.layout.list_item,
                null,
                new String[] { PlayerContract.PlayerEntry.COLUMN_NAME_NUMBER, PlayerContract.PlayerEntry.COLUMN_NAME_FULLNAME },
                new int[] { R.id.listItemNumber , R.id.listItemName }, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        ListView mListView = (ListView) findViewById(R.id.playerList);
        mListView.setAdapter(mAdapter);

        serviceIntent = new Intent(this, PlayerService.class);
        startService(serviceIntent);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
    }

    // create new player activity invocation
    public void createNewPlayer(View view) {
        Intent i = new Intent(this, CreatePlayerActivity.class);
        startActivityForResult(i, CREATE_PLAYER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (CREATE_PLAYER): {
                if (resultCode == Activity.RESULT_OK) {
                    Player p = (Player) data.getSerializableExtra("newPlayer");
                    ContentValues values = new ContentValues();
                    values.put(PlayerContract.PlayerEntry.COLUMN_NAME_FULLNAME, p.getName());
                    values.put(PlayerContract.PlayerEntry.COLUMN_NAME_NUMBER, p.getNumber());
                    getContentResolver().insert(PlayerContentProvider.CONTENT_URI, values);
                    getContentResolver().notifyChange(PlayerContentProvider.CONTENT_URI, null);
                }
                break;
            }
        }
    }

    // cursorloader methods

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, PlayerContentProvider.CONTENT_URI, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.notifyDataSetChanged();
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}
