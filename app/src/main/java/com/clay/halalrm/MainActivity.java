package com.clay.halalrm;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clay.halalrm.fragment.MainFragment;
import com.clay.halalrm.fragment.MapFragment;
import com.clay.halalrm.fragment.RumahMakanFragment;
import com.clay.halalrm.model.DaftarMenu;
import com.clay.halalrm.model.RumahMakan;
import com.clay.halalrm.tools.MyUtils;
import com.clay.halalrm.tools.SessionHelper;
import com.clay.halalrm.tools.requestHandler;
import com.clay.informhalal.LocationReceiver;
import com.clay.informhalal.dataMenu;
import com.clay.informhalal.geoCode;
import com.clay.informhalal.googlePlace;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.gson.Gson;
import com.orm.SugarContext;
import com.orm.SugarDb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.com.safety.locationlistenerhelper.core.LocationTracker;
import br.com.safety.locationlistenerhelper.core.SettingsLocationTracker;

//import com.google.android.gms.location.places.Place;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , RumahMakanFragment.OnListFragmentInteractionListener
        , MainFragment.OnFragmentInteractionListener
        , MapFragment.OnFragmentInteractionListener
{

    FloatingActionButton fab;
    TextView navUserMain, navUserSub, navAcc, navLng, navLat,navAdd;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTracker.stopLocationService(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SugarContext.init(this);

        if (SessionHelper.getInstance(this).getAppFirstTime()) {
            Log.d("MainApp", "First session");
            SugarDb db = new SugarDb(this);
            db.onCreate(db.getDB());
            InputData();
            SessionHelper.getInstance(this).setAppFirstTime(false);
        } else {
            Log.d("MainApp", "Not First session");

            String kunci = getResources().getString(R.string.google_maps_key);
            Places.initialize(this,kunci);

//            RumahMakan.deleteAll(RumahMakan.class);
//            DaftarMenu.deleteAll(DaftarMenu.class);
//            InputData();
        }

        kunci = getResources().getString(R.string.google_maps_key);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlacePicker();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        navUserMain = headerView.findViewById(R.id.txtMain);
        navUserSub = headerView.findViewById(R.id.txtSub);
        navAcc = headerView.findViewById(R.id.txtAcc);
        navLat = headerView.findViewById(R.id.txtLat);
        navLng = headerView.findViewById(R.id.txtLng);
        navAdd = headerView.findViewById(R.id.txtAdd);


        UserView();
        showFloatingActionButton(fab);

        final boolean checkLocationPermission = checkLocationPermission();


        LocationReceiver receiver = new LocationReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                if (intent.getAction() == "my.action") {
                    Location extra = (Location) intent.getParcelableExtra(SettingsLocationTracker.LOCATION_MESSAGE);
//                    Log.d("Location LL: ", "Latitude: " + extra.getLatitude() + "\nLongitude:" + extra.getLongitude());
//                    Log.d("Location AR: ", "Accuracy: " + extra.getAccuracy() + "\nAltitude:" + extra.getAltitude());
                    navAcc.setText("Accuracy: " + extra.getAccuracy());
                    navLat.setText("Latitude: " + extra.getLatitude());
                    navLng.setText("Longitude:" + extra.getLongitude());

                    if (extra.getAccuracy() < 20d)
                        getFormattedAddres(extra.getLatitude(),extra.getLongitude());
//                    navUserMain.setText("Latitude: " + extra.getLatitude() + "\nLongitude:" + extra.getLongitude());
//                    navUserSub.setText("Accuracy: " + extra.getAccuracy() + "Altitude:" + extra.getAltitude());
                    lat = extra.getLatitude();
                    lng = extra.getLongitude();
                }
            }
        };
        registerReceiver(receiver, new IntentFilter("my.action"));
    }

    private void showPlacePicker() {
        Intent myIntent = new Intent(MainActivity.this, RumahMakanActivity.class);
        myIntent.putExtra("admin",isAdmin());
        myIntent.putExtra("key", 1l);
        myIntent.putExtra("lat", lat);
        myIntent.putExtra("lng", lng);
        startActivity(myIntent);

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case PLACE_PICKER_REQUEST:
//                    Place place = PlacePicker.getPlace(this, data);
//                    String placeName = String.format("Place: %s", place.getName());
//                    double latitude = place.getLatLng().latitude;
//                    System.out.println("latitude = " + latitude);
//                    double longitude = place.getLatLng().longitude;
//                    System.out.println("longitude = " + longitude);
//                    LatLng coordinate = new LatLng(latitude, longitude);
//            }
//        }
//    }

//    private void showPlacePicker() {
//
//        PingPlacePicker.IntentBuilder builder = new PingPlacePicker.IntentBuilder();
//        builder.setAndroidApiKey(kunci)
//                .setGeolocationApiKey(kunci);
//        try {
//            Intent placeIntent = builder.build(MainActivity.this);
//            startActivityForResult(placeIntent, PLACE_PICKER_REQUEST);
//        }
//        catch (Exception ex) {
//            // Google Play services is not available...
//        }
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if ((requestCode == PLACE_PICKER_REQUEST) && (resultCode == RESULT_OK)) {
//            Place place = PingPlacePicker.Companion.getPlace(data);
//            if (place != null) {
//                Toast.makeText(this, "You selected the place: " + place.getName(), Toast.LENGTH_SHORT).show();
//                System.out.println("place.getLatLng() = " + place.getLatLng());
//            }
//        }
//    }

    private final static int PLACE_PICKER_REQUEST = 111;
//    private void openPlacePicker() {
//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//        try {
//            // for activty
//            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
//            // for fragment
//            //startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }
//    }

        String kunci;
    private void getFormattedAddres(double latitude, double longitude) {
        final Uri.Builder builder = new Uri.Builder();

        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("geocode")
                .appendPath("json")
                .appendQueryParameter("latlng", latitude+","+longitude)
                .appendQueryParameter("key", kunci);
        final String string = builder.build().toString();
//        System.out.println("string = " + string);
        final String rest = requestHandler.INSTANCE.readingRest(this, string);
//        System.out.println("rest = " + rest);

        final Gson gson = new Gson();
        final geoCode formatted_address = gson.fromJson(rest, geoCode.class);
        Log.d("data.status", formatted_address.getStatus());
        if (formatted_address.getStatus().equals("OK"))
        {
            final String address = formatted_address.getResults().get(0).getFormatted_address();
            System.out.println("address = " + address);
            navAdd.setText(address);

        }
        else
        {
            navAdd.setText("");
        }
        ////        readingRest(this,string);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Ijin Lokasi")
                        .setMessage("Tolong beri aplikasi ijin pada GPS")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationTracker.onRequestPermission(requestCode,permissions,grantResults);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    private LocationTracker locationTracker = null;
    @Override
    protected void onStart() {
        super.onStart();
        locationTracker = new LocationTracker("my.action")
                .setInterval(50000)
                .setGps(true)
                .setNetWork(false)
                .start(this, this);
    }
    //    override fun onStart() {
//    super.onStart()
//    locationTracker = LocationTracker("my.action")
//            .setInterval(50000)
//            .setGps(true)
//            .setNetWork(false)
//            .start(baseContext, this)
//}    
    Double lat;
    Double lng;

    private void InputData() {
        List<String> DATA_ALL = new LinkedList<>();
//        DATA_ALL.add("RMjawa.json");
        DATA_ALL.add("RMmadura.json");
//        DATA_ALL.add("RMpadang.json");
        for (String s: DATA_ALL) {
            SiapkanData(s);
        }
    }

    private void SiapkanData(String s) {
        String padangData = MyUtils.loadJSONFromAsset(this, s);
        Gson gson = new Gson();
        googlePlace DataPadang = gson.fromJson(padangData, googlePlace.class);
        List<googlePlace.Result> resultsPadang = DataPadang.getResults();
        for (googlePlace.Result result: resultsPadang ) {
            RumahMakan RM = new RumahMakan();
            RM.setName(result.getName());
            RM.setReference(result.getReference());
            RM.setCompound_code(result.getPlus_code().getCompound_code());
            RM.setGlobal_code(s);
            RM.setFormatted_address(result.getFormatted_address());
            RM.setRating(result.getRating());
            RM.setPlace_id(result.getPlace_id());
            RM.setUser_ratings_total(result.getUser_ratings_total());
            RM.setLat(result.getGeometry().getLocation().getLat());
            RM.setLng(result.getGeometry().getLocation().getLng());


//            getBitmapAsByteArray()
//            Bitmap icon1 = BitmapFactory.decodeResource(this.getResources(),
//                    R.drawable.rm_teluk_bayur1);
//            final byte[] bitmap1 = getBitmapAsByteArray(icon1);
//            RM.setImage1(bitmap1);

//            Bitmap icon2 = BitmapFactory.decodeResource(this.getResources(),
//                    R.drawable.halal);
//            final byte[] bitmap2 = getBitmapAsByteArray(icon2);
//            RM.setImage2(bitmap2);
//
//            Bitmap icon3 = BitmapFactory.decodeResource(this.getResources(),
//                    R.drawable.logo);
//            final byte[] bitmap3 = getBitmapAsByteArray(icon3);
//            RM.setImage3(bitmap3);

            String DaftarMenu = MyUtils.loadJSONFromAsset(this, RM.getReference());
            dataMenu dataMenu = gson.fromJson(DaftarMenu, dataMenu.class);
            List<com.clay.informhalal.dataMenu.Result> results = dataMenu.getResults();
            RM.save();
            System.out.println("RM = " + RM);
            for (com.clay.informhalal.dataMenu.Result r: results) {
                com.clay.halalrm.model.DaftarMenu daftarMenu =
                        new DaftarMenu(r.getHarga(),r.getMenu(),RM.getId());
                daftarMenu.save();
            }
        }
    }



    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }


    private void UserView() {
        hideFloatingActionButton(fab);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentData = new MainFragment();
        FragmentTransaction fragmentTransaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.FrameFragment,fragmentData);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {

            LayoutInflater li = LayoutInflater.from(MainActivity.this);
            View prompt = li.inflate(R.layout.form_login, null);

            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setView(prompt)
                    .setTitle("Admin Mode")
                    .setMessage("Masukkan Username dan Password")
                    .setPositiveButton("Login", null) //Set to null. We override the onclick
                    .setNegativeButton("Batal", null)
                    .create();

            final EditText txtPassword = prompt.findViewById(R.id.txtPassword);
            final EditText txtUsername = prompt.findViewById(R.id.txtUsername);

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button btnLogin = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    btnLogin .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            String pass = txtPassword.getText().toString();
                            String user = txtUsername.getText().toString();
                            boolean login = Login(user, pass);

                            if (login)  {
                                navUserSub.setText("Admin Mode");
                                dialog.dismiss();
                            }
                            else {
                                txtPassword.setError("Salah password");
                                txtUsername.setError("Salah username");

                            }
                            makeSnakeBar(null,"Berhasil Masuk Sebagai Admin");
                            setAdmin(login);
                        }
                    });
                }
            });
            dialog.show();

            return true;
        }
        if (id == R.id.action_logout) {

            if (!isAdmin())
                makeSnakeBar(null,"Anda Bukan Admin");
            else{
                makeSnakeBar(null,"Berhasil keluar Admin Mode");
                navUserSub.setText("Admin Mode");
                hideFloatingActionButton(fab);
                setAdmin(false);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean Login(String user, String pass) {
        ArrayList<String> dataUser = new ArrayList<String>();
        dataUser.add("admin:admin");
        String key = user + ":" + pass;
        System.out.println("key = " + key);
        for(String s : dataUser)
            if(s.equals(key))
                return true;
        return false;
    }

    boolean Admin;

    public boolean isAdmin() {
        return Admin;
    }

    public void setAdmin(boolean admin) {
        Admin = admin;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentData = null;

        if (id == R.id.nav_all) {
            fragmentData = new RumahMakanFragment();
            setViewDataAll();
        } else if (id == R.id.nav_main) {
            fragmentData = new MainFragment();
            setViewMain();
        } else if (id == R.id.nav_jawa) {
            fragmentData = MapFragment.newInstance("RMjawa.json","");
            setViewMain();
        } else if (id == R.id.nav_padang) {
            fragmentData = MapFragment.newInstance("RMpadang.json","");
            setViewMain();
        } else if (id == R.id.nav_madura) {
            fragmentData = MapFragment.newInstance("RMmadura.json","");
            setViewMain();
        } else if (id == R.id.nav_exit) {
            System.exit(0);
        }

        FragmentTransaction fragmentTransaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.FrameFragment,fragmentData);
        fragmentTransaction.commit();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setViewDataAll() {

        if (isAdmin())
        {
            showFloatingActionButton(fab);
            fab.setImageResource(android.R.drawable.ic_menu_add);

        }
        else {
            hideFloatingActionButton(fab);
        }



    }


    private void setViewMain() {
        hideFloatingActionButton(fab);
        
        if (isAdmin())
            showAddRumahMakan();
        else
            hideAddRumahMakan();
    }

    private void hideAddRumahMakan() {
    }

    private void showAddRumahMakan() {
    }

    private void hideFloatingActionButton(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setAutoHideEnabled(false);
        }

        fab.hide();
    }

    private void showFloatingActionButton(FloatingActionButton fab) {
        fab.show();
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setAutoHideEnabled(true);
        }
    }

    private void makeSnakeBar(View view,String txt){

        if (view == null)
            view = findViewById(fab.getId());
        Snackbar.make(view, txt, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    @Override
    public void onListFragmentInteraction(RumahMakan item) {
        System.out.println("item = " + item);
        System.out.println("lat = " + lat);
        System.out.println("lng = " + lng);

        Intent myIntent = new Intent(MainActivity.this, RumahMakanActivity.class);
        myIntent.putExtra("admin",isAdmin());
        myIntent.putExtra("key", item.getId());
        myIntent.putExtra("lat", lat);
        myIntent.putExtra("lng", lng);
        startActivity(myIntent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println("uri = " + uri);
    }

}
