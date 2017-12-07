package xyz.zzulu.jejuground.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kakao.usermgmt.response.model.UserProfile;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import xyz.zzulu.jejuground.R;

import static xyz.zzulu.jejuground.activities.MainActivity.serverIP;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements MapView.POIItemEventListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String LOG_TAG = "MapFragment";

    RequestQueue queue;

//    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
//    private String mParam1;

    private OnFragmentInteractionListener mListener;

    MapView mapView;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Set Parameters from bundle
            // param1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        ViewGroup mapViewContainer = (ViewGroup) view.findViewById(R.id.map_view);

        mapView = new MapView(getActivity());
        mapView.setPOIItemEventListener(this);

        queue = Volley.newRequestQueue(getActivity());

        String url = serverIP + "/flags";

        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i = 0; i < response.length() ; i++) {
                    // Marker

                    MapPOIItem marker = new MapPOIItem();
                    try {
                        JSONObject flag = response.getJSONObject(i);
                        marker.setTag(flag.getInt("id")); // id
                        marker.setItemName(flag.getString("name")); // description
                        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(flag.getDouble("lat"), flag.getDouble("lng"))); // lat, lng
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

                    mapView.addPOIItem(marker);
                }

                try {
                    if(response.length() > 0){
                        JSONObject lastObj = response.getJSONObject(response.length()-1);
                        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lastObj.getDouble("lat"), lastObj.getDouble("lng")), 4, false);
                    } else {
                        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.3616666, 126.5291666), 8, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

        mapViewContainer.addView(mapView);

//        mapView.setCurrentLocationEventListener(this);
//        mapView.setCurrentLocationRadius(0);
//        mapView.setDefaultCurrentLocationMarker();
//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);


        return view;
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

//    @Override
//    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
//
//    }
//
//    @Override
//    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
//
//    }
//
//    @Override
//    public void onCurrentLocationUpdateFailed(MapView mapView) {
//
//    }
//
//    @Override
//    public void onCurrentLocationUpdateCancelled(MapView mapView) {
//
//    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        mapView.removePOIItem(mapPOIItem);

        queue = Volley.newRequestQueue(getActivity());

        String url = serverIP + "/flags/" + mapPOIItem.getTag();

        StringRequest request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

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
}
