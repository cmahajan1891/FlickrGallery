package com.example.cmahajan.flickrgallery.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.cmahajan.flickrgallery.models.FlickerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// Let for the time being not declare it as singleton class.
public class FlickrItemsFetcher {

    private static final String API_KEY = "7b85e389607020e3b5a12c5a40e260db";
    private static final String FLICKR_URL = "https://api.flickr.com/services/rest/";
    private static final String TAG = "FlickrItemsFetcher";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }


    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }


    private void parseItems(List<FlickerItem> items, JSONObject jsonBody, PersistanceManager persistanceManager)
            throws JSONException {

        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            FlickerItem item = new FlickerItem();
            item.setId(photoJsonObject.getString("id"));

            try {
                String url = constructThumbnailUrl(photoJsonObject);
                if (url != null) {
                    item.setUrl(url);
                } else {
                    continue;
                }
            } catch (JSONException je) {
                Log.e(TAG, "Failed to fetch photo URL", je);
                continue;
            }

            persistanceManager.storeItems(item.getId(), item.getUrl());
            items.add(item);
        }
    }

    private String constructThumbnailUrl(JSONObject photoJsonObject) throws JSONException {
        String url = null;
        if (photoJsonObject != null) {
            url = new String("https://farm" + photoJsonObject.getString("farm")
                    + ".staticflickr.com/" + photoJsonObject.getString("server")
                    + "/" + photoJsonObject.getString("id")
                    + "_" + photoJsonObject.getString("secret") + "_t.jpg");
        }

        return url;
    }

    public List<FlickerItem> fetchItems(PersistanceManager persistanceManager) {

        List<FlickerItem> items = new ArrayList<>();

        try {
            String url = Uri.parse(FLICKR_URL)
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .build().toString();

            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody, persistanceManager);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);

        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);

        }

        return items;
    }


}
