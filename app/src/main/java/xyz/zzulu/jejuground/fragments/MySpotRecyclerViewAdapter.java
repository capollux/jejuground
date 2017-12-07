package xyz.zzulu.jejuground.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.zzulu.jejuground.R;
import xyz.zzulu.jejuground.fragments.SpotFragment.OnListFragmentInteractionListener;
import xyz.zzulu.jejuground.fragments.dummy.SpotContent.SpotItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SpotItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySpotRecyclerViewAdapter extends RecyclerView.Adapter<MySpotRecyclerViewAdapter.ViewHolder> {

    private Context mContext;

    private final List<SpotItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MySpotRecyclerViewAdapter(Context context, List<SpotItem> items, OnListFragmentInteractionListener listener) {
        mContext = context;
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_spot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mRegionView.setText(mValues.get(position).region);
        holder.mDescriptionView.setText(mValues.get(position).description);
        if(holder.mItem.visited){
            holder.mImageView.setImageDrawable(ContextCompat.getDrawable( mContext, R.drawable.visited_spot ));
        } else {
            holder.mImageView.setImageDrawable(ContextCompat.getDrawable( mContext, R.drawable.not_visited_spot ));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mImageView, holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mRegionView;
        public final TextView mDescriptionView;
        public final ImageView mImageView;
        public SpotItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.flag);
            mRegionView = (TextView) view.findViewById(R.id.region);
            mDescriptionView = (TextView) view.findViewById(R.id.description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescriptionView.getText() + "'";
        }
    }

}
