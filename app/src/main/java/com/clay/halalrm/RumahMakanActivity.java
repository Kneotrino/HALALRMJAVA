package com.clay.halalrm;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clay.halalrm.fragment.DaftarMenuFragment;
import com.clay.halalrm.fragment.InfoFragment;
import com.clay.halalrm.model.DaftarMenu;
import com.clay.halalrm.model.RumahMakan;
import com.clay.halalrm.tools.ImageSaver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class RumahMakanActivity extends AppCompatActivity
    implements InfoFragment.OnFragmentInteractionListener, DaftarMenuFragment.OnListFragmentInteractionListener
{
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToInfo();
                    break;
                case R.id.navigation_dashboard:
                    switchToMenu();
                    break;
            }
            return false;
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("requestCode = " + requestCode);
        String filename = rumahMakan.getPlace_id()+rumahMakan.getId()+requestCode+".jpg";
        System.out.println("filename = " + filename);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 0) {

            final Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                final ImageSaver saver = new ImageSaver(getApplicationContext())
                        .setFileName(filename)
                        .setExternal(false)//image save in external directory or app folder default value is false
                        .setDirectory("RMgambar");
                saver.save(bitmap);
                rumahMakan.setFoto1(saver.getSaved().getPath());
                rumahMakan.save();
                switchToInfo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 1) {

            final Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                final ImageSaver saver = new ImageSaver(getApplicationContext())
                        .setFileName(filename)
                        .setExternal(false)//image save in external directory or app folder default value is false
                        .setDirectory("RMgambar");
                saver.save(bitmap);
                rumahMakan.setFoto2(saver.getSaved().getPath());
                rumahMakan.save();
                switchToInfo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 2) {

            final Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                final ImageSaver saver = new ImageSaver(getApplicationContext())
                        .setFileName(filename)
                        .setExternal(false)//image save in external directory or app folder default value is false
                        .setDirectory("RMgambar");
                saver.save(bitmap);
                rumahMakan.setFoto3(saver.getSaved().getPath());
                rumahMakan.save();
                switchToInfo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    private void switchToInfo() {
        btnAddMenu.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = RumahMakanActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.RumahMakanFrame,
                InfoFragment.newInstance(
                        AdminMode,
                        idRM,
                        lat,
                        lng
                )

        );
        fragmentTransaction.commit();
    }


    private void switchToMenu() {
        btnAddMenu.setVisibility(View.GONE);
        if (!AdminMode)
            btnAddMenu.setVisibility(View.GONE);
        else
            btnAddMenu.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = RumahMakanActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.RumahMakanFrame,DaftarMenuFragment.newInstance(AdminMode,idRM));
        fragmentTransaction.commit();
    }

    boolean AdminMode;
    RumahMakan rumahMakan;
    long idRM;
    double lat;
    double lng;

    protected void onDestroy() {
        super.onDestroy();
//        SugarContext.terminate();
    }

    Button btnAddMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rumah_makan);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        btnAddMenu = findViewById(R.id.btnAddMenu);
        SetupView();
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        switchToInfo();
    }

    private void SetupView() {

        final Bundle bundleExtra = getIntent().getExtras();
        System.out.println("bundleExtra = " + bundleExtra);

        idRM = getIntent().getLongExtra("key",0l);
        lat = getIntent().getDoubleExtra("lat",-10.162353);
        lng = getIntent().getDoubleExtra("lng",123.5915637);
        AdminMode = getIntent().getBooleanExtra("admin",false);

        System.out.println("AdminMode = " + AdminMode);
        rumahMakan = RumahMakan.findById(RumahMakan.class,idRM);
        setTitle(rumahMakan.getName());

        btnAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMenu();
            }
        });
    }

    private void AddMenu() {
        LayoutInflater li = LayoutInflater.from(RumahMakanActivity.this);
        View prompt = li.inflate(R.layout.form_add_menu, null);

        final EditText txtNamaMenu = prompt.findViewById(R.id.txtNamaMenu);
        final EditText txtHargaMenu = prompt.findViewById(R.id.txtHargaMenu);
        final List<EditText> editTextList = new LinkedList<>();
        editTextList.add(txtNamaMenu);
        editTextList.add(txtNamaMenu);


        final AlertDialog dialog = new AlertDialog.Builder(RumahMakanActivity.this)
                .setView(prompt)
                .setTitle("Admin Mode Input Data Penguna")
                .setMessage("Masukan Data Penguna Baru")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button btnSimpan = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button btnBatal = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);


                btnBatal.setText("Batal");
                btnSimpan.setText("Tambah");
                btnSimpan.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        boolean inputCheck = checkEditText(editTextList);
                        System.out.println("inputCheck = " + inputCheck);
                        if (inputCheck)    {
                            DaftarMenu daftarMenu =
                                    new DaftarMenu(
                                            txtHargaMenu.getText().toString(),
                                            txtNamaMenu.getText().toString(),
                                            rumahMakan.getId()
                                    );
                            System.out.println("Add.daftarMenu = " + daftarMenu);
                            daftarMenu.save();
                            dialog.dismiss();
                            switchToMenu();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private boolean checkEditText(List<EditText> editTextList) {

        for (EditText editText: editTextList) {
            if (TextUtils.isEmpty(editText.getText().toString())) {
                editText.setError("Harus isi");
                return false;
            }
            if ( editText.getText().toString().length() < 4) {
                editText.setError("Input minimal 4 karakter");
                return false;
            }
        }
        return true;
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DaftarMenu item) {
        System.out.println("item = " + item);
        if (AdminMode)
            EditMenu(item);
    }

    private void EditMenu(final DaftarMenu item) {
        LayoutInflater li = LayoutInflater.from(RumahMakanActivity.this);
        View prompt = li.inflate(R.layout.form_add_menu, null);

        final EditText txtNamaMenu = prompt.findViewById(R.id.txtNamaMenu);
        final EditText txtHargaMenu = prompt.findViewById(R.id.txtHargaMenu);
        final List<EditText> editTextList = new LinkedList<>();
        editTextList.add(txtNamaMenu);
        editTextList.add(txtNamaMenu);

        txtNamaMenu.setText(item.getNama());
        txtHargaMenu.setText(item.getHarga());


        final AlertDialog dialog = new AlertDialog.Builder(RumahMakanActivity.this)
                .setView(prompt)
                .setTitle("Admin Mode Input Data Penguna")
                .setMessage("Masukan Data Penguna Baru")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton("Batal", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button btnSimpan = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button btnBatal = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);


                btnBatal.setText("Hapus");
                btnSimpan.setText("Edit");

                btnSimpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean inputCheck = checkEditText(editTextList);
                        System.out.println("inputCheck = " + inputCheck);
                        if (inputCheck)    {
                            item.setNama(txtNamaMenu.getText().toString());
                            item.setHarga(txtHargaMenu.getText().toString());
                            System.out.println("Update.daftarMenu = " + item);
                            item.save();
                            dialog.dismiss();
                            switchToMenu();
                        }
                    }
                });
                btnBatal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            System.out.println("delete.daftarMenu = " + item);
                            item.delete();
                            dialog.dismiss();
                            switchToMenu();
                    }
                });

            }
        });
        dialog.show();

    }
}
