package com.clay.halalrm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clay.halalrm.fragment.MainFragment;
import com.clay.halalrm.fragment.RumahMakanFragment;
import com.clay.halalrm.fragment.dummy.DummyContent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , RumahMakanFragment.OnListFragmentInteractionListener
        , MainFragment.OnFragmentInteractionListener
{

        FloatingActionButton fab;
        TextView navUserMain,navUserSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        navUserMain = (TextView) headerView.findViewById(R.id.txtMain);
        navUserSub = (TextView) headerView.findViewById(R.id.txtSub);

        UserView();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

            final EditText txtPassword = (EditText) prompt.findViewById(R.id.txtPassword);
            final EditText txtUsername = (EditText) prompt.findViewById(R.id.txtUsername);

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button btnLogin = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
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
//            setPengunaView();
            // Handle the camera action
        } else if (id == R.id.nav_main) {
            fragmentData = new MainFragment();
            setViewMain();

        } else if (id == R.id.nav_jawa) {

        } else if (id == R.id.nav_padang) {

        } else if (id == R.id.nav_madura) {

        } else if (id == R.id.nav_exit) {
            System.exit(0);
        }

        FragmentTransaction fragmentTransaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.FrameFragment,fragmentData);
        fragmentTransaction.commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setViewDataAll() {
        showFloatingActionButton(fab);

    }

    private void setViewMain() {
        hideFloatingActionButton(fab);
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
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        System.out.println("item = " + item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println("uri = " + uri);
    }
}
