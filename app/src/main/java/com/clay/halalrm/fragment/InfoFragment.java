package com.clay.halalrm.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.clay.halalrm.R;
import com.clay.halalrm.fragment.dummy.ModelObject;
import com.clay.halalrm.model.RumahMakan;
import com.clay.halalrm.tools.ImageSaver;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
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

        List<Bitmap> imageURL = new LinkedList<>();

        try {

                Bitmap bitmap1 = BitmapFactory.decodeFile(rumahMakan.getFoto1());
                imageURL.add(bitmap1);
                Bitmap bitmap2 = BitmapFactory.decodeFile(rumahMakan.getFoto2());
                imageURL.add(bitmap2);
                Bitmap bitmap3 = BitmapFactory.decodeFile(rumahMakan.getFoto3());
                imageURL.add(bitmap3);
        }
        catch (Exception e)
        {
//            e.printStackTrace();
        }

        System.out.println("imageURL.size() = " + imageURL.size());


        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vpRumahMakan);
        viewPager.setAdapter(new CustomPagerAdapter(getContext(), imageURL));
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
        private List<Bitmap> mImageURL;

        public CustomPagerAdapter(Context context, List<Bitmap> imageURL) {
            mContext = context;
            mImageURL = imageURL;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, final int position) {
            final ModelObject modelObject = ModelObject.values()[position];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);

            final ImageView imageView = layout.findViewById(modelObject.getImageViewId());
            collection.addView(layout);


            try {
                Bitmap bitmap = mImageURL.get(position);
                if (bitmap != null)
                {
                    imageView.setImageBitmap(mImageURL.get(position));
                }
            }
            catch (Exception E) {}

            layout.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    //this will log the page number that was click
                    Log.i("TAG", "This page was clicked: " + position);

                    if (mAdmin)
                        SimpanGambar(position);

                }
            });
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
            return mContext.getString(R.string.app_name);
        }
    }

    private void SimpanGambar(int position) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra("position", position);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), position);
    }




//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        System.out.println("requestCode = " + requestCode);
//        System.out.println("resultCode = " + resultCode);
////        if (resultCode != Activity.RESULT_OK) {
////            return;
////        }
////        if (requestCode == 1) {
////            final Bundle extras = data.getExtras();
////            System.out.println("extras = " + extras);
////
////            final int position = data.getIntExtra("position",1);
////            System.out.println("position = " + position);
////
////            String filename = rumahMakan.getName()+rumahMakan.getId()+ position+".jpg";
////            System.out.println("filename = " + filename);
////
////            if (extras != null) {
////                //Get image
////
//////                Bitmap newProfilePic = extras.getParcelable("data");
//////                      new ImageSaver(getApplicationContext())
//////                                .setFileName(rumahMakan.getName() + position +"_.jpg")
//////                                .setExternal(false)//image save in external directory or app folder default value is false
//////                                .setDirectory("RMgamabar")
//////                                .save(newProfilePic); //Bitmap from your code
////
////            }
////        }
//    }


}
