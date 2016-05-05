package com.aleksirasio.playerxmlparserwithsql;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PlayerService extends Service {

    private final String TAG = "PlayerService";

    private Poller p;
    private int mLastPlayerCount = 0;
    private final String url = "http://users.metropolia.fi/~peterh/players.xml";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // no binding here
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        p = new Poller();
        p.execute();
    }

    private class Poller extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(url);
            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return getResources().getString(R.string.xml_error);
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null || isCancelled()){
                return;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    p = new Poller();
                    p.execute();
                }
            }, 1000);
        }
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        Log.d(TAG, "in loadXmlFromNetwork");

        InputStream stream = null;
        PlayerXmlParser playerXmlParser = new PlayerXmlParser();
        String result = "";

        try {
            // connect to the server & get stream
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            conn.getInputStream();
            stream = new BufferedInputStream(conn.getInputStream());

            List<Player> playerList = playerXmlParser.parse(stream);

            if (playerList.size() > mLastPlayerCount) {
                int newPlayers = playerList.size() - mLastPlayerCount;
                // insert new values
                ContentValues values = new ContentValues();
                for (int i = 0; i < newPlayers; i++) {
                    Player p = playerList.get(playerList.size() - i - 1);
                    values.put(PlayerContract.PlayerEntry.COLUMN_NAME_FULLNAME, p.getName());
                    values.put(PlayerContract.PlayerEntry.COLUMN_NAME_NUMBER, p.getNumber());
                    getContentResolver().insert(PlayerContentProvider.CONTENT_URI, values);
                }
                // update last poll player counter
                mLastPlayerCount = playerList.size();

                // notify user on new content

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_notify_chat)
                        .setContentTitle("Player XML Parser")
                        .setContentText("New players found");

                Intent resultIntent = new Intent(this, MainActivity.class);

                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
                mBuilder.setContentIntent(resultPendingIntent);
                mBuilder.setAutoCancel(true);

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return result;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        Log.d(TAG, "in downloadUrl");
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        return conn.getInputStream();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "in onDestroy()");
        super.onDestroy();
        p.cancel(true);
    }

}
