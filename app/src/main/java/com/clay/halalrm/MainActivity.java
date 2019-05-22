package com.clay.halalrm;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.net.Uri;
import com.clay.halalrm.fragment.MainFragment;
import com.clay.halalrm.fragment.MapFragment;
import com.clay.halalrm.fragment.RumahMakanFragment;
import com.clay.halalrm.model.DaftarMenu;
import com.clay.halalrm.model.RumahMakan;
import com.clay.halalrm.tools.MyUtils;
import com.clay.halalrm.tools.SessionHelper;
import com.clay.informhalal.LocationReceiver;
import com.clay.informhalal.dataMenu;
import com.clay.informhalal.googlePlace;
import com.google.gson.Gson;
import com.orm.SugarContext;
import com.orm.SugarDb;
import com.clay.halalrm.tools.requestHandler;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.com.safety.locationlistenerhelper.core.LocationTracker;
import br.com.safety.locationlistenerhelper.core.SettingsLocationTracker;

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

//            RumahMakan.deleteAll(RumahMakan.class);
//            DaftarMenu.deleteAll(DaftarMenu.class);
//            InputData();
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

        final boolean checkLocationPermission = checkLocationPermission();


        LocationReceiver receiver = new LocationReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                if (intent.getAction() == "my.action") {
                    Location extra = (Location) intent.getParcelableExtra(SettingsLocationTracker.LOCATION_MESSAGE);
                    Log.d("Location LL: ", "Latitude: " + extra.getLatitude() + "\nLongitude:" + extra.getLongitude());
                    Log.d("Location AR: ", "Accuracy: " + extra.getAccuracy() + "\nAltitude:" + extra.getAltitude());
                    navAcc.setText("Accuracy: " + extra.getAccuracy());
                    navLat.setText("Latitude: " + extra.getLatitude());
                    navLng.setText("Longitude:" + extra.getLongitude());

                    if (extra.getAccuracy() < 15d)
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

    private void getFormattedAddres(double latitude, double longitude) {
        final Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("geocode")
                .appendPath("json")
                .appendQueryParameter("latlng", latitude+","+longitude)
                .appendQueryParameter("key", "AIzaSyCFiHUsYy5b6G7_8ehKf7wIFNhTjjm22pg");
        final String string = builder.build().toString();
        System.out.println("string = " + string);
//        final String rest = requestHandler.INSTANCE.readingRest(this, string);
//        System.out.println("rest = " + rest);
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
        DATA_ALL.add("RMjawa.json");
        DATA_ALL.add("RMmadura.json");
        DATA_ALL.add("RMpadang.json");
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
        if (id == R.id.action_settings) {

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
                            setAdmin(login);
                        }
                    });
                }
            });
            dialog.show();

            return true;
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
        System.out.println("lat = " + lat);
        System.out.println("lng = " + lng);
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

        Intent myIntent = new Intent(MainActivity.this, RumahMakanActivity.class);
        myIntent.putExtra("admin",isAdmin());
        myIntent.putExtra("key", item.getId());
        startActivity(myIntent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println("uri = " + uri);
    }

}
