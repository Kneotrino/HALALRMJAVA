package com.clay.halalrm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.clay.halalrm.model.RumahMakan;
import com.clay.halalrm.tools.requestHandler;
import com.clay.informhalal.geoCode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.orm.SugarContext;

import java.util.LinkedList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SugarContext.init(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);

        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
     MarkerOptions markerOptions = null;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng kupang = new LatLng(-10.16572447010728,123.5985479298927);

        markerOptions = new MarkerOptions()
                .title("Tambah Rumah Makan")
                .position(kupang);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(kupang));//Moves the camera to users current longitude and latitude
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kupang,(float) 14));//Animates camera and zooms to preferred state on the user's current location.

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                Toast.makeText(getApplicationContext(), point.toString(), Toast.LENGTH_SHORT).show();
                mMap.clear();
                markerOptions.position(point);
                mMap.addMarker(markerOptions);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    addRumahMakan();
                    return false;
                }
            }
        );
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


    private void addRumahMakan() {
        LayoutInflater li = LayoutInflater.from(MapsActivity.this);
        View prompt = li.inflate(R.layout.form_rumah_makan, null);

        final AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this)
                .setView(prompt)
                .setTitle("Admin Mode")
                .setMessage("Masukkan Data Rumah Makan")
                .setPositiveButton("Simpan", null) //Set to null. We override the onclick
                .setNegativeButton("Batal", null)
                .create();


        final LatLng position = markerOptions.getPosition();

        final EditText RumahMakanNama = prompt.findViewById(R.id.RumahMakanNama);
        final EditText RumahMakanRating = prompt.findViewById(R.id.RumahMakanRating);
        final EditText RumahMakanKode = prompt.findViewById(R.id.RumahMakanKode);
        final EditText RumahMakanAlamat = prompt.findViewById(R.id.RumahMakanAlamat);

        final List<EditText> editTextList = new LinkedList<>();
        editTextList.add(RumahMakanNama);
        editTextList.add(RumahMakanAlamat);
        editTextList.add(RumahMakanKode);

        final Spinner spinner = prompt.findViewById(R.id.spinner1);


        RumahMakanAlamat.setText(getFormattedAddres(position.latitude,position.longitude));

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnAdd = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnAdd .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (checkEditText(editTextList)) {

                        RumahMakan rumahMakan = new RumahMakan();
                        rumahMakan.setFormatted_address(RumahMakanAlamat.getText().toString());
                        rumahMakan.setLng(position.longitude);
                        rumahMakan.setLat(position.latitude);
                        rumahMakan.setName(RumahMakanNama.getText().toString());
                        rumahMakan.setCompound_code(RumahMakanKode.getText().toString());
                        double value = 0d;
                        try {
                            value = Double.parseDouble(RumahMakanRating.getText().toString());
                        }
                        catch (Exception E ){

                        }
                            rumahMakan.setRating(value);
                        final int selectedItemPosition = spinner.getSelectedItemPosition();

                        System.out.println("spinner = " + spinner.getSelectedItem());
                        switch (selectedItemPosition) {
                            case 0:
                                rumahMakan.setGlobal_code("RMjawa.json");
                                break;
                            case 1:
                                rumahMakan.setGlobal_code("RMmadura.json");
                                break;
                            case 2:
                                rumahMakan.setGlobal_code("RMpadang.json");
                                break;
                        }
                        rumahMakan.save();
                        System.out.println("rumahMakan = " + rumahMakan);
                        dialog.dismiss();
                        finish();
                        Toast.makeText(getApplicationContext(), "Berhasil Tambahkan Rumah Makan", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private String getFormattedAddres(double latitude, double longitude) {
        final Uri.Builder builder = new Uri.Builder();
        String kunci = getResources().getString(R.string.google_maps_key);
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
            return address;
        }
        return "";
        ////        readingRest(this,string);
    }

}
