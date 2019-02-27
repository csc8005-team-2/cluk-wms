//pakage name here

import android.os.Build;  
import android.support.v4.app.FragmentActivity;  
import android.os.Bundle;  
  
import com.google.android.gms.common.api.GoogleApiClient;  
import com.google.android.gms.maps.CameraUpdateFactory;  
import com.google.android.gms.maps.GoogleMap;  
import com.google.android.gms.maps.OnMapReadyCallback;  
import com.google.android.gms.maps.SupportMapFragment;  
import com.google.android.gms.maps.model.BitmapDescriptorFactory;  
import com.google.android.gms.maps.model.LatLng;  
import com.google.android.gms.maps.model.Marker;  
import com.google.android.gms.maps.model.MarkerOptions;  
import com.google.android.gms.location.LocationServices;  
  
import android.location.Location;  
import android.Manifest;  
import android.content.pm.PackageManager;  
import android.support.v4.content.ContextCompat;  
import com.google.android.gms.common.ConnectionResult;  
import com.google.android.gms.location.LocationListener;  
import com.google.android.gms.location.LocationRequest;  

public class driversMaps extends FragmentActivity implements OnMapReadyCallback, LocationListener,GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener {  

private GoogleMap map;  
private Location driverLastLocation;  
private Marker driverCurrLocation;  
private GoogleApiClient dGoogleApiClient;  
private LocationRequest driverLocationRequest;  


@Override  
public void onLocationChanged(Location location) 
    {  
  
        driverLastLocation = location;  
        if (driverCurrLocation != null) 
        // first check driver's location then updated.
          { 
            driverCurrLocation.remove();  // remove last location. 
          }  
        //Place new current location point.  
        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());  
        MarkerOptions markerOptions = new MarkerOptions();  
        markerOptions.position(point);  
        markerOptions.title("This is Your Current Location");  
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));  
        mCurrLocationMarker = map.addMarker(markerOptions);  
  
        //move map camera  
        map.moveCamera(CameraUpdateFactory.newLatLng(point));  
        map.animateCamera(CameraUpdateFactory.zoomTo(9));  
  
        //stop location updates  
        if (dGoogleApiClient != null)
          {  
            LocationServices.FusedLocationApi.removeLocationUpdates(dGoogleApiClient, this);  
          }  
  
    }  
