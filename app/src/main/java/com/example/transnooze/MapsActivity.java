package com.example.transnooze;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private GeofencingClient geofencingClient;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private static int geofenceNumber = 0;
    private FloatingActionButton checkFab;
    private TextView hint;
    private RadioButton button500m;
    private RadioButton button1km;
    private RadioButton button2km;
    private RadioGroup distanceRadios;
    String TAG = "placeautocomplete";
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private final int UPDATE_INTERVAL =  1000;
    private final int FASTEST_INTERVAL = 900;
    private static float GEOFENCE_RADIUS = 500.0f;
    private Circle geoFenceLimits;
    private static double LAT;
    private static double LONG;
    private Marker geoFenceMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        createGoogleApi();

        Places.initialize(getApplicationContext(), "AIzaSyCN15Tb6TbvsMibYwpmveCen_lbnga3_dc");
        PlacesClient placesClient = Places.createClient(this);





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final int REQUEST_LOCATION = 2;
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                    REQUEST_LOCATION );
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        mapFragment.getMapAsync(this);
        geofencingClient = LocationServices.getGeofencingClient(this);

        final TextView txtView = findViewById(R.id.txtView);

        checkFab = findViewById(R.id.checkFab);
        checkFab.setEnabled(false);
        checkFab.setAlpha(0.25f);
        checkFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Geofence geofence = new Geofence.Builder()
                        .setRequestId("GeoFence" + geofenceNumber) // Geofence ID
                        .setCircularRegion( LAT, LONG, GEOFENCE_RADIUS) // defining fence region
                        .setExpirationDuration(NEVER_EXPIRE) // expiring date
                        // Transition types that it should look for
                        .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER)
                        .build();

                GeofencingRequest request = new GeofencingRequest.Builder()
                        // Notification to trigger when the Geofence is created
                        .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                        .addGeofence( geofence ) // add a Geofence
                        .build();

                Intent mapToName = new Intent(getBaseContext(), EnterNameActivity.class);
                startActivity(mapToName);

            }
        });

        hint = findViewById(R.id.hint);

        button500m = findViewById(R.id.button500m);
        button1km = findViewById(R.id.button1km);
        button2km = findViewById(R.id.button2km);
        distanceRadios = findViewById(R.id.distanceRadios);
        distanceRadios.setEnabled(false);
        distanceRadios.setAlpha(0.25f);

        button500m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GEOFENCE_RADIUS = 500.0f;
                if (LAT != 0 && LONG != 0) {
                    if (geoFenceLimits != null) {
                        geoFenceLimits.remove();
                    }
                    CircleOptions circleOptions = new CircleOptions()
                            .center(geoFenceMarker.getPosition())
                            .strokeColor(Color.parseColor("#ffa500"))
                            .fillColor(Color.parseColor("#80ffa500"))
                            .radius(GEOFENCE_RADIUS);
                    geoFenceLimits = mMap.addCircle(circleOptions);

                }
            }
        });

        button1km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GEOFENCE_RADIUS = 1000.0f;
                if (LAT != 0 && LONG != 0) {
                    if (geoFenceLimits != null) {
                        geoFenceLimits.remove();
                    }
                    CircleOptions circleOptions = new CircleOptions()
                            .center(geoFenceMarker.getPosition())
                            .strokeColor(Color.parseColor("#ffa500"))
                            .fillColor(Color.parseColor("#80ffa500"))
                            .radius(GEOFENCE_RADIUS);
                    geoFenceLimits = mMap.addCircle(circleOptions);
                }
            }
        });

        button2km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GEOFENCE_RADIUS = 2000.0f;
                if (LAT != 0 && LONG != 0) {
                    if (geoFenceLimits != null) {
                        geoFenceLimits.remove();
                    }
                    CircleOptions circleOptions = new CircleOptions()
                            .center(geoFenceMarker.getPosition())
                            .strokeColor(Color.parseColor("#ffa500"))
                            .fillColor(Color.parseColor("#80ffa500"))
                            .radius(GEOFENCE_RADIUS);
                    geoFenceLimits = mMap.addCircle(circleOptions);
                }
            }
        });




        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                txtView.setText(place.getName()+","+place.getId());
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ',' + place.getLatLng());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17);
                mMap.animateCamera(cameraUpdate);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });






    }

    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "Can't find style. Error: ", e);
        }


    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        geoFenceMarker = mMap.addMarker(markerOptions);
        checkFab.setEnabled(true);
        distanceRadios.setEnabled(true);

        checkFab.setAlpha(1f);
        distanceRadios.setAlpha(1f);
        LAT = latLng.latitude;
        LONG = latLng.longitude;

        if (geoFenceLimits != null) {
            geoFenceLimits.remove();
        }
        CircleOptions circleOptions = new CircleOptions()
                .center(geoFenceMarker.getPosition())
                .strokeColor(Color.parseColor("#ffa500"))
                .fillColor(Color.parseColor("#80ffa500"))
                .radius(GEOFENCE_RADIUS);
        geoFenceLimits = mMap.addCircle(circleOptions);


        AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnimation.setDuration(1000);
        hint.setAlpha(0f);

        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0);

        if (addresses.get(0).getFeatureName() != null) {
            address = addresses.get(0).getAddressLine(0);
        }

        Log.e("test", address);


    }






    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
