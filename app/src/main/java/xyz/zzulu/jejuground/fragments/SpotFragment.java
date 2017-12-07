package xyz.zzulu.jejuground.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.kakao.oreum.tamra.Tamra;
import com.kakao.oreum.tamra.base.NearbySpots;
import com.kakao.oreum.tamra.base.Region;
import com.kakao.oreum.tamra.base.Spot;
import com.kakao.oreum.tamra.base.TamraObserver;
import com.kakao.usermgmt.response.model.UserProfile;


import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import xyz.zzulu.jejuground.R;
import xyz.zzulu.jejuground.fragments.dummy.SpotContent;
import xyz.zzulu.jejuground.fragments.dummy.SpotContent.SpotItem;

import static xyz.zzulu.jejuground.activities.MainActivity.serverIP;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SpotFragment extends Fragment implements RecyclerView.OnClickListener {

    private static final String LOG_TAG = "SpotFragment";

    private RecyclerView.Adapter rvAdapter;

    RequestQueue queue;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SpotFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SpotFragment newInstance(int columnCount) {
        SpotFragment fragment = new SpotFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }


            rvAdapter = new MySpotRecyclerViewAdapter(getActivity(), SpotContent.ITEMS, mListener);
            recyclerView.setAdapter(rvAdapter);

            queue = Volley.newRequestQueue(getActivity());

            String url = serverIP + "/spots";

            JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    SpotContent.ITEMS.clear();

                    for(int i = 0; i < response.length() ; i++) {
                        // Marker

                        MapPOIItem marker = new MapPOIItem();
                        try {
                            JSONObject spot = response.getJSONObject(i);
                            SpotContent.addItem(new SpotItem(spot.getString("region"), spot.getInt("spot_id"), spot.getString("name"), spot.getBoolean("indoor"), spot.getDouble("lat")	,spot.getDouble("lng"), spot.getBoolean("visited") ));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    rvAdapter.notifyDataSetChanged();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization: Token token", String.valueOf(UserProfile.loadFromCache().getId()));

                    return headers;
                }
            };
            queue.add(request);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;

            StartMonitoring();

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        StopMonitoring();
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity(), "asdf", Toast.LENGTH_LONG).show();
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
        void onListFragmentInteraction(ImageView iv, SpotItem item);
    }

    private void StartMonitoring() {
        Tamra.addObserver(tamraObserver);
    }

    private void StopMonitoring() {
        Tamra.removeObserver(tamraObserver);
    }

    private final TamraObserver tamraObserver = new TamraObserver() {
        @Override
        public void didEnter(Region region) {

            Log.d("GlobalApplication", "---- didEnter ----" + region.name() + " 진입");

        }

        @Override
        public void didExit(Region region) {

            Log.d("GlobalApplication", "---- didExit ----" + region.name() + " 이탈");

        }

        @Override
        public void ranged(final NearbySpots spots) {
            spots.orderBy(Spot.ACCURACY_ORDER).foreach(spot -> {
                Log.d("GlobalApplication", "---- spot detected ---- 근처에 / " + spot.id() + " / " + spot.description() + " 발견");
                Toast.makeText(getActivity(),spot.region() + " / " + spot.description(),Toast.LENGTH_SHORT).show();
            });
        }
    };
}
