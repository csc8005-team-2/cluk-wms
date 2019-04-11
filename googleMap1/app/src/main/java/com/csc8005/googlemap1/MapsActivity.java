package com.csc8005.googlemap1;

import android.Manifest;
import android.content.IntentSender;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.Build;

import com.csc8005.googlemap1.Direction.DirectionFinder;
import com.csc8005.googlemap1.Direction.DirectionFinderListener;
import com.csc8005.googlemap1.Direction.Routes;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;

import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main class that hold the activity of lunching Google Map. The Direction of giving destination
 * would be taken from JSON data as it's aimed to be used with GoogleMap-API.
 *
 * References that have been learned from are:
 * Google Developers: https://developers.google.com/android/
 * Android Developers: https://developer.android.com
 * Stackoverflow: https://stackoverflow.com
 * Open sources of implementing GoogleAPI from : https://github.com
 * As well as involving the student's background in JAVA.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, DirectionFinderListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    // Google GoogleMap
    private GoogleMap mMap;
    //Google ApiClient
    private GoogleApiClient googleApiClient;
    // Google LocationRequest
    private LocationRequest locationRequest;
    // GoogleMap detucting current Location.
    private Marker userCurrentLocationMarker;
    // To save all the addresses info and mark them out on map
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    //DEFAULT_ZOOM for camera movments.
    private static final float DEFAULT_ZOOM = 5f;
    // App's constants references: Refernce to USER_REQUEST_LOACATION.
    private static final int USER_REQUEST_LOACATION = 9000;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9001;
    // Restaurants location on map.
    private static final LatLng warehose           = new LatLng(54.875984, -1.587459);
    private static final LatLng SeatonBurnServices = new LatLng(55.066738, -1.633700);
    private static final LatLng AlnwickTownCentre  = new LatLng(55.413019, -1.709631);
    private static final LatLng NewtonAycliffe     = new LatLng(54.612528, -1.580213);
    private static final LatLng ThirskTownCentre   = new LatLng(54.232400, -1.342910);
    private static final LatLng WhitbyTownCentre   = new LatLng(54.485342, -0.615261);
    // Google map GUI style
    private Button btnFindPath;
    private EditText etStart;
    private EditText etEnd;


    /**
     * This method called when an Activity first call or launched, it's responsible to create the
     * map activity fragment.
     * When ever orientation(i.e. from horizontal to vertical) of activity gets changed or an Activity
     * gets forcefully terminated by any Operating System then savedInstanceState i.e.
     *
     * When the activity is lunched a pop up message will show to check for user location permission
     * if been accepted.
     *
     * @param savedInstanceState object of Bundle Class will save the state of an Activity and it's
     * responsible to save all the changes of the Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // call the check location condition
            CheckUserLocationPermission();
        }
        // implementing the GUI when user type in
        btnFindPath =  (Button)   findViewById(R.id.button);
        etStart     =  (EditText) findViewById(R.id.editText4);
        etEnd       =  (EditText) findViewById(R.id.editText5);

        // once user click the button
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        // clear the map fragment from all markers, fetching URL request and show the optimizing route
                mMap.clear();
                sendRequest();
            }
        });

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
        MapViewSettings();     // show views such as Compass
        RsturansLocationsMarker(); //  shows marker on restaurants locations

        // check if the user has accepted the location permission then shows his location on map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            BuildApiClient();
            mMap.setMyLocationEnabled(true); // user location shown and implement by onConnected method
        }

        // once user click on location marker
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
            // InfoWindow will show the location details
                View view = getLayoutInflater().inflate(R.layout.address_maps,null);
                TextView locality   = (TextView) view.findViewById(R.id.locality);
                TextView snippet    = (TextView) view.findViewById(R.id.snippet);

                LatLng latLng = marker.getPosition();
                locality.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());

                return view; // InfoWindow
            }
        });

    }

    /**
     * This method will be invoked asynchronously when the connect request has successfully completed.
     * After this callback, the application can make requests on other methods provided by the client
     * and expect that no user intervention is required to call methods that use account and scopes
     * provided to the client constructor.
     *
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location Services Connected");

         // get high accuracy of location, set to PRIORITY_HIGH_ACCURACY and setInterval(long) to 1 second.
        // This would be appropriate for mapping applications that are showing your location in real-time.
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000).setFastestInterval(1000) // in millisecond
        .setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // if location permission checked then shows the location on map in a second.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    /**
     * This method is called whenever the user is moving, to update the current location on the map.
     *
     * @param location to hold user location datafiles.
     */
    @Override
    public void onLocationChanged(Location location) {

        if (userCurrentLocationMarker != null)
        {
            // Check if there is any marker in the map then remove it to update the location for
           // user's current location
            userCurrentLocationMarker.remove();
        }

        // Showing updated Current Location Marker on Map
        LatLng userPoints = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(userPoints).title("Your current location");
        userCurrentLocationMarker = mMap.addMarker(markerOptions);

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
                    DefaultLocation();
                }
                return;
        }
    }

    /**
     * This method is been implemented once the user has searched for his journey to show a message
     * that the map searching for the best route.
     *
     * Any markers or polyline  will br removed according to updates in searching inputs.
     */
    @Override
    public void onDirectionFinderStart() {

        Toast.makeText(this, "Please, wait while finding your direction!", Toast.LENGTH_SHORT).show();

        // remove the marker from origin address when the user is searching for another address
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        // remove the marker from destination address when the user is searching for another address
        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        // remove the polyline from map when the user is searching for another address
        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    /**
     * This method is responsible of taking uesr's input and mark out its location, draw its optimzing
     * route and calculating the distance and time duration for user's journey.
     *
     * @param routes a list to hold the start and end addresses.
     */

    @Override
    public void onDirectionFinderSuccess(List<Routes> routes) {
        polylinePaths = new ArrayList<>();  // show the route direction
        originMarkers = new ArrayList<>(); // show star-location markers
        destinationMarkers = new ArrayList<>(); // show end-location markers

        for (Routes r : routes) {

            // point out startAddress info
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(r.startAddress)
                    .position(new LatLng(r.startLocation.latitude,r.startLocation.longitude));

            // add marker in destination point
            originMarkers.add(mMap.addMarker(markerOptions));

            // move the camera towards the start point
            moveCamera(r.startLocation, DEFAULT_ZOOM);

            // point out endAddress info
            MarkerOptions markerOptions1 = new MarkerOptions();
            markerOptions1.title(r.endAddress)
                    .position(new LatLng(r.endLocation.latitude,r.endLocation.longitude));

            //calculating distances in kilometers
            float result[] = new float[r.duration.value];
            Location.distanceBetween(r.startLocation.latitude,r.startLocation.longitude,
                    r.endLocation.latitude,r.endLocation.longitude,result);
            int distance = (int) result[0];

            // calculating duration time
            int durationTime = r.duration.value;
            markerOptions1.snippet("Distance = "+ distance/ 1000+ " Km"+"\n"+
            "Duration = "+ durationTime/3600+" Houres, "+ (durationTime/60)%60+ " minutes" ); // convert m to km

            // add marker in destination point
            destinationMarkers.add(mMap.addMarker(markerOptions1));

            // styling the polyline
            PolylineOptions polylineOptions = new PolylineOptions()
            .geodesic(true).color(Color.BLUE).width(5);

            // looping throw addresses to find the route and drawing it
            for (int i = 0; i < r.points.size(); i++)
                polylineOptions.add(r.points.get(i));
            polylinePaths.add(mMap.addPolyline(polylineOptions));

        }
    }

    /**
     *  This method is responsible to handel user's inputs of address details.
     *  Shows a message if null pointer checked.
     */
    private void sendRequest() {

        String origin = etStart.getText().toString(); // reading the input of start-location
        String destination = etEnd.getText().toString(); // reading the input of end-location

        // handling if null input, a message will shows to inform the user to type and address
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please, insert an origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please, insert a destination address!", Toast.LENGTH_SHORT).show();
            return;
        }
        // execute and fetch URL request for giving addresses
        try {
            new DirectionFinder(this, origin, destination).execute();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
     * Method to ask for user choice of location permission.
     * First condition will check for user permission, if not accepted then the second condition will
     * check if the user has had the message for permission request.
     *
     * @return true if user has seen the permission request and is been granted, false if not and
     * the DefaultLocation will be invoked.
     */
    public boolean CheckUserLocationPermission() {
        // check for user permission if return false then check for request message
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                // if return true then the user will see the request message
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, USER_REQUEST_LOACATION);
            } else {
                // if return false then show the request message to the user again
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
        // drawing a marker and circle around it
        mMap.addMarker(new MarkerOptions().position(warehose).title("Marker in Warehouse"));
        mMap.addCircle(new CircleOptions().center(warehose).radius(10).strokeWidth(3f).strokeColor(Color.RED)
                .fillColor(Color.argb(70, 150, 50, 50)));

    }

    /**
     * Animated the camera movement towards a giving location.
     *
     * @param latLng to hold latitude & latitude
     * @param zoom   to hold camera zoom
     */
    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
    }

    /**
     * This method is used to draw  markers for restaurants locations on the map.
     */

    public void RsturansLocationsMarker() {

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


    @Override
    public void onConnectionSuspended(int i) {
    }

    /**
     * Handling the internet connection failure.
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }
            catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location Services Failed:" + connectionResult.getErrorCode());
        }
    }
}
