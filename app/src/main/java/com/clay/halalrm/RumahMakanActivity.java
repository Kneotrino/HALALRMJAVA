package com.clay.halalrm;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clay.halalrm.fragment.DaftarMenuFragment;
import com.clay.halalrm.fragment.InfoFragment;
import com.clay.halalrm.fragment.MainFragment;
import com.clay.halalrm.fragment.dummy.DummyContent;
import com.clay.halalrm.model.DaftarMenu;
import com.clay.halalrm.model.RumahMakan;

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

    private void switchToInfo() {
        btnAddMenu.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = RumahMakanActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.RumahMakanFrame, InfoFragment.newInstance(AdminMode,idRM));
        fragmentTransaction.commit();
    }


    private void switchToMenu() {

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

    Button btnAddMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rumah_makan);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        btnAddMenu = findViewById(R.id.btnAddMenu);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        SetupView();
        switchToInfo();
//        setTitle();
    }

    private void SetupView() {

        idRM = getIntent().getLongExtra("key",0l);
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
                                            txtNamaMenu.getText().toString(),
                                            txtHargaMenu.getText().toString(),
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

        txtNamaMenu.setText(item.getHarga());
        txtHargaMenu.setText(item.getNama());


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
                            item.setHarga(txtNamaMenu.getText().toString());
                            item.setNama(txtHargaMenu.getText().toString());
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
