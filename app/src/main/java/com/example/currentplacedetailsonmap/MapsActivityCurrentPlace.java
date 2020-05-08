package com.example.currentplacedetailsonmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.maps.model.TileOverlay;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.model.PlaceLikelihood;
//import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
//import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
//import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.sql.SQLRecoverableException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static java.lang.Thread.sleep;
import static java.util.Locale.getDefault;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapsActivityCurrentPlace extends AppCompatActivity
        implements OnMapReadyCallback {
    BluetoothAdapter BTAdapter;
    private static final Handler handler = new Handler();
    private static final Handler handler1 = new Handler();

    private static final String TAG = MapsActivityCurrentPlace.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    double latitude;
    double longitude;
    Integer c=0;
    String place;
    TileOverlay overlay;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static RecyclerView recyclerView;
    private static RecyclerView.Adapter mAdapter;
    private static RecyclerView.LayoutManager layoutManager;
    // The entry point to the Places API.
    //---private PlacesClient mPlacesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 1;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private List[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private static ArrayList<Details> f_db;

    ////////////////////////////////////////////////////////////////////////////////
    //////////// Important Values for your localization and bluetooth //////////////
    ////////////////////////////////////////////////////////////////////////////////
    // Selected current place
    private LatLng markerLatLng;
    private String markerSnippet;
    private String markerPlaceName;

    // New Bluetooth Devices Number
    private int btDevicesCount;

    Geocoder geocoder;
    List<Address> addresses;
    private RecyclerView rv;
    private RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager mLayoutManager;
    Activity_places_list obj;

    ////////////////////////////////////////////////////////////////////////////////
    Activity_places_list activity_places_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        obj = new Activity_places_list();
        f_db = obj.fetch_db();

        geocoder = new Geocoder(this, getDefault());
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);

       handler.postDelayed(device_runnable, 1000);
       // handler1.postDelayed(place_runnable, 2000);
        // Retrieve the content view that renders the map.

        // Construct a PlacesClient
        //-Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        //-mPlacesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //News update
        final Button button1 = findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent serverIntent = new Intent(MapsActivityCurrentPlace.this, news.class);
                startActivity(serverIntent);

            }
        });

        // Timeline
        Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent serverIntent = new Intent(MapsActivityCurrentPlace.this, Activity_places_list.class);
                startActivity(serverIntent);
            }
        });

        //Heatmap
        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMap.clear();
                addHeatMap();
                overlay.clearTileCache();
            }
        });

        //Display score
        Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                double score = obj.score_computation(f_db);
                Integer bt_count = obj.bt_count;
                Integer place_count = obj.place_count;
                b.putDouble("score", score);
                b.putInt("dev_ct", bt_count);
                b.putInt("place_ct", place_count);
                Intent serverIntent = new Intent(MapsActivityCurrentPlace.this, show_score.class);
                serverIntent.putExtras(b);
                startActivity(serverIntent);
            }
        });
        updateLocationUI();
    }

    //Show HeatMap
    public void addHeatMap() {

        List<LatLng> list = null;

        // Get the data: latitude/longitude positions of fetched data
        list = getLatLon(f_db);
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder().data(list).build();
        // Add a tile overlay to the map, using the heat map tile provider.
        overlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }

    //Get lat lon from fetched data
    private ArrayList<LatLng> getLatLon(ArrayList<Details> data) {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        for (Details d : data) {
            double lat = d.getLat();
            double lng = d.getLon();
            list.add(new LatLng(lat, lng));
        }
        return list;
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     *
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     *
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            getDeviceLocation();

           // device_count();
        } else if (item.getItemId() == R.id.nearby_devices) {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
        }
        else if(item.getItemId() == R.id.upload)
        {
            LoadAndSaveData();
            Context context = getApplicationContext();
            CharSequence text = "Upload Successful";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        return true;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    ////////////////////////////////////////////////////////////////////////////
                    //////////////////////   WRITE YOUR CODE HERE ! ////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////
                    // Example:
                    btDevicesCount = data.getExtras()
                            .getInt(DeviceListActivity.EXTRA_DEVICE_COUNT);
                    Log.d(TAG, "Device number:" + btDevicesCount);
                    // You can change the address to the number of certain type of devices, or
                    // other variables you want to use. Remember to change the corresponding
                    // name at DeviceListActivity.java.
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    ////////////////////////////////////////////////////////////////////////////
                    //////////////////////   WRITE YOUR CODE HERE ! ////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////
                    // Example:
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                }
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;


        // TEST
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }


    /**
     * Gets the current location of the device, and positions the map's camera.
     */


    private void LoadAndSaveData() {
        if (mLocationPermissionGranted) {
            final Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override

                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // get location details
                        mLastKnownLocation = task.getResult();

                        if (mLastKnownLocation != null) {
                            latitude = mLastKnownLocation.getLatitude();
                            longitude = mLastKnownLocation.getLongitude();
                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                Log.i("address", addresses.get(0).getLocality());
                                place = addresses.get(0).getAddressLine(0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            //get the current time
                            Long timestamp = mLastKnownLocation.getTime();
                            Date dateTime = new Date(timestamp);
                            Log.v(TAG, "---Raw Date---" + dateTime);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            String date = sdf.format(timestamp);


                            //get the bluetooth count
                            Details det = new Details(place, latitude, longitude, btDevicesCount, date);
                            Log.i(TAG,"here");
                            //store into the database
                            if (place != null && btDevicesCount >0) {
                                Log.d(TAG,"Aishwarya Rajasekaran");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
                                ref.child(date).setValue(det);
                                // final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd ");
                            }
                        }
                    }
                }
            });
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                final Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            Long timestamp = mLastKnownLocation.getTime();

                            if (mLastKnownLocation != null) {
                                latitude = mLastKnownLocation.getLatitude();
                                longitude = mLastKnownLocation.getLongitude();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(latitude, longitude), DEFAULT_ZOOM));
                                Log.i("Latitude", String.valueOf(latitude));
                                Log.i("Longitude", String.valueOf(longitude));
                                try {
                                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    Log.i("address", addresses.get(0).getLocality());
                                    place = addresses.get(0).getAddressLine(0);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(place));
                                //Store in the data base

                                Date dateTime = new Date(timestamp);
                                Log.v(TAG, "---Raw Date---" + dateTime);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                String date = sdf.format(timestamp);


                                //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
                                //Details det = new Details(place, latitude, longitude, btDevicesCount, date);
                                // final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd ");
                                //ref.child(date).setValue(det);
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    //---------------------
    /*
    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.

    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                    Place.Field.LAT_LNG);

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.newInstance(placeFields);

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final Task<FindCurrentPlaceResponse> placeResult =
                    mPlacesClient.findCurrentPlace(request);
            placeResult.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FindCurrentPlaceResponse likelyPlaces = task.getResult();

                        // Set the count, handling cases where less than 5 entries are returned.
                        int count;
                        if (likelyPlaces.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                            count = likelyPlaces.getPlaceLikelihoods().size();
                        } else {
                            count = M_MAX_ENTRIES;
                        }

                        int i = 0;
                        mLikelyPlaceNames = new String[count];
                        mLikelyPlaceAddresses = new String[count];
                        mLikelyPlaceAttributions = new List[count];
                        mLikelyPlaceLatLngs = new LatLng[count];

                        for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                            // Build a list of likely places to show the user.
                            mLikelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                            mLikelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                            mLikelyPlaceAttributions[i] = placeLikelihood.getPlace()
                                    .getAttributions();
                            mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                            i++;
                            if (i > (count - 1)) {
                                break;
                            }
                        }

                        // Show a dialog offering the user the list of likely places, and add a
                        // marker at the selected place.
                        MapsActivityCurrentPlace.this.openPlacesDialog();
                    } else {
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                }
            });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.

    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                markerLatLng = mLikelyPlaceLatLngs[which];
                markerSnippet = mLikelyPlaceAddresses[which];
                markerPlaceName = mLikelyPlaceNames[which];

                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(markerPlaceName)
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }
*/

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private Integer device_count() {

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        btDevicesCount=0;
        //if bt is not enables,  ask user for permission
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_ENABLE_BT);
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        BTAdapter.startDiscovery();
        Log.i(TAG,"Count"+btDevicesCount);
        return btDevicesCount;
    }

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (device.getName() != null) {
                        BluetoothClass bluetoothClass = device.getBluetoothClass();
                        //check if the new device is a phone and if yes then add it to a list.
                        if (bluetoothClass.getDeviceClass() == BluetoothClass.Device.PHONE_SMART) {
                            Log.i("Filtering:phone devices", device.getName());
                            btDevicesCount++;

                        } else {Log.i("Filtering: non phone", device.getName()); }
                    }
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "finished discovery");
                BTAdapter.startDiscovery();
                btDevicesCount=0;

            }
        }


    };


//    private final Runnable device_runnable = new Runnable() {
//        @Override
//        public void run() {
//            device_count();
//        }
//    };

    private final Runnable device_runnable = new Runnable() {
        @Override
        public void run() {
            device_count();
        }
    };

}

////////////////////////////////////////////////////////////////////////////////////
/* public ArrayList<Details> query(){

       Log.i(TAG,"inside query_main");
       DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
       DatabaseReference tripsRef = rootRef.child("user");
       ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //fetched_list.clear();

            for(DataSnapshot ds : dataSnapshot.getChildren()) {

                Details d  = ds.getValue(Details.class);
                String lat = ds.child("lat").getValue().toString();
                String longi = ds.child("lon").getValue().toString();
                String placee = ds.child("place").getValue().toString();
                String no_dev = ds.child("no_devices").getValue().toString();
                Log.i("TAG", d.getPlace()+"/"+d.getLat() + " / " + d.getLon()  + " / " + d.getNo_devices());
                //Log.i("TAG", ds.getKey()+"/"+lat + " / " + longi  + " / " + placee);
                fetched_list.add(d);
                Log.i("TAG", "added success"+fetched_list.size());

            }
            }
           @Override
           public void onCancelled(DatabaseError databaseError) {}
    };

       tripsRef.addListenerForSingleValueEvent(valueEventListener);
        Log.i(TAG,"lets check the size inside query"+fetched_list.size());
      return fetched_list;
    }
*/