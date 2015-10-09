package al.musi.lyricsfetcher;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class AZLyricsProvider extends Service {

    private String mTitle;
    private String mArtist;
    private String mLyrics;

    public static final String TAG = "AZLyricsProvider";

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mTitle = extras.getString("title");
            mArtist = extras.getString("artist");

            mLyrics = getActualContent();

            Log.v(TAG, "response: " + mLyrics);

            Intent intent1 = new Intent("lyricSearching");
            intent1.putExtra("message", mLyrics);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    protected String getActualContent() {
        if (mTitle.length() < 0 || mArtist.length() < 0) { return null; }

        String artist= mArtist.replaceAll(" ", "").replaceAll("\\W", "") + "/";
        String title = mTitle.replaceAll(" ", "").replaceAll("\\W", "") + ".html";

        String u = "http://www.azlyrics.com/lyrics/" + artist + title;
        Log.d(TAG, "url: " + u);
        final String url = u;
        String response = "ayy lmao";
        HttpConnection httpConnection = new HttpConnection(url);

        try {
            response = httpConnection.a();
            Log.v(TAG, "Fetching url: " + u);
            return parse(response, url);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return response;
    }

    private String parse(String response, String url) {
        //YOU STUPID
        //WHAT'S NINE PLUS TEN?
        String onlyLyrics = response.substring(
                response.indexOf("Sorry about that. -->")+21,
                response.length()-1);
        onlyLyrics = onlyLyrics.substring(0, onlyLyrics.indexOf("</div>"));
        onlyLyrics = onlyLyrics.replaceAll("\"", "").replaceAll("<br>", "\n");
        return onlyLyrics;
    }
}
