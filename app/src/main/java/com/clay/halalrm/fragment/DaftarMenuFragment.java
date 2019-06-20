package com.clay.halalrm.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clay.halalrm.R;
import com.clay.halalrm.fragment.adapter.DaftarMenuAdapter;
import com.clay.halalrm.model.DaftarMenu;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DaftarMenuFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    boolean AdminMode;
    long idRM;

    public DaftarMenuFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DaftarMenuFragment newInstance(int columnCount) {
        DaftarMenuFragment fragment = new DaftarMenuFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public static DaftarMenuFragment newInstance(boolean AdminMode,long idRM) {
        DaftarMenuFragment fragment = new DaftarMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_COLUMN_COUNT, 1);
        bundle.putBoolean("admin", AdminMode);
        bundle.putLong("key", idRM);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("getArguments() = " + getArguments());
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            AdminMode = getArguments().getBoolean("admin",false);
            idRM = getArguments().getLong("key");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daftarmenu_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            System.out.println("idRM = " + idRM);
            List<DaftarMenu> list = Select.from(DaftarMenu.class)
                .where(
                        Condition.prop("Rumah_Makan_ID").eq(idRM),
                        Condition.prop("NAMA").notEq("0")
                ).list();
            System.out.println("list.size() = " + list.size());
            recyclerView.setAdapter(new DaftarMenuAdapter(list, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DaftarMenu item);
    }
}
