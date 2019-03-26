package com.csc8005.googlemap1;

import android.Manifest;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.Build;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;

import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.PolylineOptions;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.widget.Toast;
import android.content.pm.PackageManager;
import java.sql.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    // Google GoogleMap
    private GoogleMap mMap;
    //Google ApiClient
    private GoogleApiClient googleApiClient;
    // Google LocationRequest
    private LocationRequest locationRequest;
    // GoogleMap user Location.
    private Location destination;
    // GoogleMap detucting current Location.
    private Marker userCurrentLocation;
    //DEFAULT_ZOOM for camera movments.
    private static final float DEFAULT_ZOOM = 10f;
    // App's constants references: Refernce to USER_REQUEST_LOACATION.
    private static final int USER_REQUEST_LOACATION = 9000;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9001;
    // Resturants location on map.
    private static final LatLng warehose           = new LatLng(54.875796, -1.5896747);
    private static final LatLng SeatonBurnServices = new LatLng(55.066538, -1.6359047);
    private static final LatLng AlnwickTownCentre  = new LatLng(55.4128398, -1.711825);
    private static final LatLng NewtonAycliffe     = new LatLng(54.6123511, -1.5824388);
    private static final LatLng ThirskTownCentre   = new LatLng(54.2322091, -1.3451253);
    private static final LatLng WhitbyTownCentre   = new LatLng(54.4851519, -0.6174552);


    /**
     * Check for user permission whent been accepted.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CheckUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapViewSettings();
        ourLocationsMarker();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            BuildApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    /**
     * This method is called whenever the device is connected to the map.
     *
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "LOCATION SERVICES CONECCTED");

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000).setFastestInterval(1000).setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // context
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    /**
     * This method is called whenever the user is moving, to update the current location on the map.
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        destination = location;
        if (userCurrentLocation != null)
        {
            // Check if there is any marker in the map then remove it to update the location for user's current location
            userCurrentLocation.remove();
        }

        //Showing Current Location Marker on Map
        LatLng userPoints = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(userPoints).title("Your current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        userCurrentLocation = mMap.addMarker(markerOptions);

        // Move the camera to user location
        moveCamera(userPoints, DEFAULT_ZOOM);

        // Start location updates.
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,
                    this);
        }
    }

    /**
     * Handling permission results if the user accept PERMISSION_GRANTED the map will show current
     * location, otherwise map will shows the Warehouse location as default.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case USER_REQUEST_LOACATION:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) ;
                        {
                            BuildApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    DefaultLocation();
                }
                return;
        }
    }

    /**
     * To build googleApiClient whenever is needed.
     */
    protected synchronized void BuildApiClient() {
        //Initializing googleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //Here where we connect the new googleApiClient to the map.
        googleApiClient.connect();
    }

    /**
     * Method to aske the user's choice of location permission.
     *
     * @return true if user granted.
     */
    public boolean CheckUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, USER_REQUEST_LOACATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, USER_REQUEST_LOACATION);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Shows the Warehouse location if user doesn't accept location permission.
     */
    private void DefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                "showing default location", Toast.LENGTH_SHORT).show();
        // Move the camera to defualt location
        moveCamera(warehose, DEFAULT_ZOOM);
        mMap.addMarker(new MarkerOptions().position(warehose).title("Marker in Warehouse"));
        mMap.addCircle(new CircleOptions().center(warehose).radius(500).strokeWidth(3f).strokeColor(Color.RED)
                .fillColor(Color.argb(70, 150, 50, 50)));

    }

    /**
     * Animated the camera movment.
     *
     * @param latLng
     * @param zoom
     */
    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    public void ourLocationsMarker() {
        /*mMap.addPolyline(
                new PolylineOptions().add(warehose).add(SeatonBurnServices)
                        .add(AlnwickTownCentre).add(NewtonAycliffe)
                        .add(ThirskTownCentre).add(WhitbyTownCentre)
                        .width(5f).color(Color.RED));*/

        mMap.addMarker(new MarkerOptions().position(warehose).title("Marker in Warehouse"));
        mMap.addMarker(new MarkerOptions().position(SeatonBurnServices).title("Marker in Seaton Burn Services"));
        mMap.addMarker(new MarkerOptions().position(AlnwickTownCentre).title("Marker in Alnwick Town Centre "));
        mMap.addMarker(new MarkerOptions().position(NewtonAycliffe).title("Marker in Newton Aycliffe"));
        mMap.addMarker(new MarkerOptions().position(ThirskTownCentre).title("Marker in Thirsk Town Centre"));
        mMap.addMarker(new MarkerOptions().position(WhitbyTownCentre).title("Marker in Whitby Town Centre"));
    }

    /**
     * Method to enable services view in the map.
     */
    public void MapViewSettings() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setBuildingsEnabled(true);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "LOCATION SERVICES DISCONNECTED");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "LOCATION SERVICES FAILED WITH CODE:" + connectionResult.getErrorCode());
        }
    }

    private class MyTask extends AsyncTask<String, String, Connection> {
        Connection connection = null;

        @Override
        protected Connection doInBackground(String... strings) {
            try {
                String userName = "csc8005_team02";
                String password = "HogsGet(Text";
                String url = "jdbc:mysql://homepages.cs.ncl.ac.uk/csc8005_team02";
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection(url, userName, password);
                System.out.println("Database connection established");
            } catch (Exception e) {
                System.err.println("Cannot connect to database server");
                System.err.println(e.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                        System.out.println("Database connection terminated");
                    } catch (Exception e) { /* ignore close errors */ }
                }
            }
            return connection;
        }
    }
}
