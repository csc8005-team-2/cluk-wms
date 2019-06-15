package com.csc8005.googlemap1.Direction;


import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionFinder {
    private static final String DIRECTION_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyDKi8K5yLXoaTKNBMwG_kOF7NuWkOM9FFQ";
    private DirectionFinderListener listener;
    private String origin;
    private String destination;

    public DirectionFinder(DirectionFinderListener listener, String origin, String destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }

    /**
     * Executing the direction request and sow it on the map.
     * will be implemented in the main activity class to show direction for giving locations.
     * @throws UnsupportedEncodingException
     */
    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadJSONData().execute(getDirectionUrl());
    }

    /**
     * Fetching the URL as API-direction request format.
     * @return API-direction request format.
     * @throws UnsupportedEncodingException
     */

    private String getDirectionUrl() throws UnsupportedEncodingException {
        String dOrigin = URLEncoder.encode(origin, "utf-8");
        String dDestination = URLEncoder.encode(destination, "utf-8");

        return DIRECTION_URL + "origin=" + dOrigin + "&destination=" + dDestination + "&key=" + GOOGLE_API_KEY;
    }

    private class DownloadJSONData extends AsyncTask<String, Void, String> {

        /**
         * Once direction API is requested, this method will output the HTTP URL and parse it into
         * JSON data.
         * @param params
         * @return Full URL Direction API if been requested, null if not.
         */
        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * This method will pass the URL output to parseJSon method.
         * @param s to hold the direction URL that has been requested.
         */
        @Override
        protected void onPostExecute(String s) {
            try {
                parseJSon(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is to take all the output from Google Direction API as JASON and converted
     * it into an Object-oriented language (JAVA) and add it to Routes Array.
     * @param data holding all JSON data as (JSONObject, JSONArray)
     * @throws JSONException
     */
    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Routes> routes = new ArrayList<Routes>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        // looping throw the route array in JSON data to save the input int the (routes)
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Routes route = new Routes();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray  jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            routes.add(route); // adding the info to the array
        }

        // will be called in main activity to show direction for giving locations
        listener.onDirectionFinderSuccess(routes);
    }

    /**
     * Open Source method that took uncoding information from JSON data of optimizing route for
     * giving journey.
     *
     * @return list that hold latitude and longitude of start and end location.
     */

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;

            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded; // direction best route
    }
}
