package com.clay.halalrm.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.clay.halalrm.R;
import com.clay.halalrm.fragment.dummy.ModelObject;
import com.clay.halalrm.model.RumahMakan;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import static java.sql.DriverManager.println;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Boolean  mAdmin;
    private long     mKey;
    private RumahMakan rumahMakan;
    private double myLat,myLng;

    private OnFragmentInteractionListener mListener;

    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(
            boolean param1,
            long param2,
            double lat,
            double lng
    ) {

        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, param1);
        args.putLong(ARG_PARAM2, param2);
        args.putDouble("lat",lat);
        args.putDouble("lng",lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("getArguments() = " + getArguments());
        if (getArguments() != null) {
            mAdmin = getArguments().getBoolean(ARG_PARAM1,false);
            mKey = getArguments().getLong(ARG_PARAM2,0l);
            myLat = getArguments().getDouble("lat");
            myLng = getArguments().getDouble("lng");
            rumahMakan = RumahMakan.findById(RumahMakan.class,mKey);
        }
        System.out.println("InfoFragment.rumahMakan = " + rumahMakan);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        TextView textAlamat= (TextView) view.findViewById(R.id.textAlamat);
        TextView textRating= (TextView) view.findViewById(R.id.textRating);
        TextView textKode= (TextView) view.findViewById(R.id.textKode);
        TextView textNamaRM= (TextView) view.findViewById(R.id.textNamaRM);

        final Button buttonJalur = (Button) view.findViewById(R.id.buttonJalur);
        final Button buttonOpenMap = (Button) view.findViewById(R.id.buttonOpenMap);

        buttonOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMap();
            }
        });
        buttonJalur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenJalur();
            }
        });



        textAlamat.setText(rumahMakan.getFormatted_address());
        textRating.setText(rumahMakan.getRating().toString());
        textKode.setText(rumahMakan.getCompound_code());
        textNamaRM.setText(rumahMakan.getName());

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vpRumahMakan);
        viewPager.setAdapter(new CustomPagerAdapter(getContext()));
        return view;
    }

    private void OpenJalur() {

        final String format = String.format(
                Locale.ENGLISH,
                "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                myLat,
                myLng,
                rumahMakan.getLat(),
                rumahMakan.getLng()
        );
        System.out.println("format = " + format);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(format));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void OpenMap() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.google.com")
                .appendPath("maps")
                .appendPath("search")
                .appendPath("")
                .appendQueryParameter("api", "1")
                .appendQueryParameter("query", rumahMakan.getName());
//                .appendQueryParameter("query_place_id", intent.getStringExtra("id"));

        String key = builder.build().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(key));
        startActivity(intent);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class CustomPagerAdapter extends PagerAdapter {
        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            ModelObject modelObject = ModelObject.values()[position];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return ModelObject.values().length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            ModelObject customPagerEnum = ModelObject.values()[position];
            return mContext.getString(customPagerEnum.getTitleResId());
        }    }
}
