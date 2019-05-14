package com.clay.halalrm;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;

import com.clay.halalrm.fragment.DaftarMenuFragment;
import com.clay.halalrm.fragment.MainFragment;
import com.clay.halalrm.fragment.dummy.DummyContent;
import com.clay.halalrm.model.DaftarMenu;
import com.clay.halalrm.model.RumahMakan;

public class RumahMakanActivity extends AppCompatActivity
    implements MainFragment.OnFragmentInteractionListener, DaftarMenuFragment.OnListFragmentInteractionListener
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
        FragmentTransaction fragmentTransaction = RumahMakanActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.RumahMakanFrame,new MainFragment());
        fragmentTransaction.commit();
    }


    private void switchToMenu() {
        FragmentTransaction fragmentTransaction = RumahMakanActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.RumahMakanFrame,DaftarMenuFragment.newInstance(AdminMode,idRM));
        fragmentTransaction.commit();


    }

    boolean AdminMode;
    RumahMakan rumahMakan;
    long idRM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rumah_makan);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        switchToInfo();
        SetupView();
//        setTitle();
    }

    private void SetupView() {
        idRM = getIntent().getLongExtra("key",0l);
        AdminMode = getIntent().getBooleanExtra("admin",false);
        rumahMakan = RumahMakan.findById(RumahMakan.class,idRM);
        System.out.println("RumahMakanActivity = " + rumahMakan);
        setTitle(rumahMakan.getName());

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DaftarMenu item) {
        System.out.println("item = " + item);

    }
}
