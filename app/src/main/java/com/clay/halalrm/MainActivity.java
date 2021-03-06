package com.clay.halalrm;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.gson.Gson;
import com.orm.SugarContext;
import com.orm.SugarDb;

import java.io.ByteArrayOutputStream;
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
        SugarContext.terminate();
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
//        showFloatingActionButton(fab);

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
//        unregisterReceiver(receiver);
    }

    private void showPlacePicker() {
        Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(myIntent);

    }


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

    int back = 0;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            back++;
            makeSnakeBar(null,"Tekan sekali lagi untuk keluar");
            if (back % 2 == 0)
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
        final MenuItem menuItem = item;


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {

            if  (isAdmin()){

                FragmentTransaction fragmentTransaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.FrameFragment,new MainFragment());
                fragmentTransaction.commit();
                setViewMain();

                makeSnakeBar(null,"Berhasil keluar Admin Mode");
                navUserSub.setText("User Mode");
                hideFloatingActionButton(fab);
                setAdmin(false);
                menuItem.setTitle("Admin Mode");

            }
            else {
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
                                System.out.println("login = " + login);

                                if (login)  {
                                    navUserSub.setText("Admin Mode");
                                    dialog.dismiss();
                                    setAdmin(true);
                                    makeSnakeBar(null,"Berhasil Masuk Sebagai Admin");
                                    menuItem.setTitle("Admin Logout");

                                    FragmentTransaction fragmentTransaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.FrameFragment,new RumahMakanFragment());
                                    fragmentTransaction.commit();
                                    setViewDataAll();


                                }
                                else {
                                    txtPassword.setError("Salah password");
                                    txtUsername.setError("Salah username");
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
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

        int id;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentData = null;

        System.out.println("Admin = " + Admin);
        if (id == R.id.nav_all) {
            fragmentData = new RumahMakanFragment();
            setViewDataAll();
        } else if (id == R.id.nav_main) {
            fragmentData = new MainFragment();
            setViewMain();
        } else if (id == R.id.nav_jawa) {
            fragmentData = MapFragment.newInstance("RMjawa.json",isAdmin());
            setViewMain();
        } else if (id == R.id.nav_padang) {
            fragmentData = MapFragment.newInstance("RMpadang.json",isAdmin());
            setViewMain();
        } else if (id == R.id.nav_madura) {
            fragmentData = MapFragment.newInstance("RMmadura.json",isAdmin());
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
        }
        else {
            hideFloatingActionButton(fab);
        }
    }


    private void setViewMain() {
        if (isAdmin())
            showAddRumahMakan();
        else
            hideAddRumahMakan();
    }

    private void hideAddRumahMakan() {
        hideFloatingActionButton(fab);
    }

    private void showAddRumahMakan() {
        showFloatingActionButton(fab);
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
    public void onListFragmentInteraction(final RumahMakan item) {
        final Intent myIntent = new Intent(MainActivity.this, RumahMakanActivity.class);
        myIntent.putExtra("admin",isAdmin());
        myIntent.putExtra("key", item.getId());
        myIntent.putExtra("lat", lat);
        myIntent.putExtra("lng", lng);

        if  (!Admin)
            startActivity(myIntent);
        else {
            String[] colors = {"Lihat", "Edit", "Hapus", "Batal"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pilih Aksi :"+item.getName());
            builder.setItems(colors, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    System.out.println("which = " + which);
                    switch (which) {
                        case 0:
                            System.out.println("Lihat");
                            System.out.println("item = " + item);
                            startActivity(myIntent);
                            break;
                        case 1:
                            System.out.println("Edit");
                            EditRumahMakan(item);
                            break;
                        case 2:
                            System.out.println("Hapus");
                            HapusRumahMakan(item);
                            break;
                        case 3:
                            System.out.println("Batal");
                            break;
                    }

                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    private void EditRumahMakan(RumahMakan item) {
        System.out.println("Edit.item = " + item);
        FormRumahMakan(item);
        ResetDataFragment();
    }

    private void ResetDataFragment() {

        FragmentTransaction fragmentTransaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.FrameFragment,new RumahMakanFragment());
        fragmentTransaction.commit();

    }

    private void HapusRumahMakan(final RumahMakan item) {
        System.out.println("Hapus.item = " + item);

        AlertDialog HapusDialog =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Hapus")
                .setMessage("Anda Yakin untuk Hapus")

                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        item.delete();
                        dialog.dismiss();
                        ResetDataFragment();

                    }

                })



                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        HapusDialog.show();
    }

    private void FormRumahMakan(final RumahMakan item) {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View prompt = li.inflate(R.layout.form_rumah_makan, null);

        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(prompt)
                .setTitle("Admin Mode")
                .setMessage("Masukkan data Rumah Makan")
                .setPositiveButton("Simpan", null) //Set to null. We override the onclick
                .setNegativeButton("Batal", null)
                .create();


        final EditText RumahMakanNama = prompt.findViewById(R.id.RumahMakanNama);
        final EditText RumahMakanRating = prompt.findViewById(R.id.RumahMakanRating);
        final EditText RumahMakanKode = prompt.findViewById(R.id.RumahMakanKode);
        final EditText RumahMakanAlamat = prompt.findViewById(R.id.RumahMakanAlamat);

        final List<EditText> editTextList = new LinkedList<>();
        editTextList.add(RumahMakanNama);
        editTextList.add(RumahMakanAlamat);
        editTextList.add(RumahMakanKode);

        final Spinner spinner = prompt.findViewById(R.id.spinner1);

        RumahMakanAlamat.setText(item.getFormatted_address());
        RumahMakanNama.setText(item.getName());
        RumahMakanKode.setText(item.getCompound_code());
        RumahMakanRating.setText(item.getRating().toString());

        switch (item.getGlobal_code()) {
            case "RMjawa.json":
                spinner.setSelection(1);
                break;
            case "RMmadura.json":
                spinner.setSelection(0);
                break;
            case "RMpadang.json":
                spinner.setSelection(2);
                break;
        }


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnAdd = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnAdd .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (checkEditText(editTextList)) {

                            item.setFormatted_address(RumahMakanAlamat.getText().toString());
                            item.setName(RumahMakanNama.getText().toString());
                            item.setCompound_code(RumahMakanKode.getText().toString());
                            double value = 0d;
                            try {
                                value = Double.parseDouble(RumahMakanRating.getText().toString());
                            }
                            catch (Exception E ){

                            }
                            item.setRating(value);
                            final int selectedItemPosition = spinner.getSelectedItemPosition();

                            System.out.println("spinner = " + spinner.getSelectedItem());
                            switch (selectedItemPosition) {
                                case 0:
                                    item.setGlobal_code("RMjawa.json");
                                    break;
                                case 1:
                                    item.setGlobal_code("RMmadura.json");
                                    break;
                                case 2:
                                    item.setGlobal_code("RMpadang.json");
                                    break;
                            }
                            item.save();
                            System.out.println("rumahMakan = " + item);
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Berhasil Edit Rumah Makan", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        dialog.show();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println("uri = " + uri);
    }
    private boolean checkEditText(List<EditText> editTextList) {

        for (EditText editText: editTextList) {
            if (TextUtils.isEmpty(editText.getText().toString())) {
                editText.setError("Harus isi");
                return false;
            }
        }
        return true;
    }

}
