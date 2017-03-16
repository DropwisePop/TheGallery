package com.example.ben.thegallery.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ben.thegallery.R;
import com.example.ben.thegallery.ui.ThumbnailActivity;

/**
 * Created by dropwisepop on 2/24/2017.
 */
public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {

    //region ViewHolder Class
    //--------------------------------------------------------------------------------
    class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView mImageView;

        ViewHolder(View v){
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.thumbnails_imageview);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.StartFullscreenActivity(getAdapterPosition());
                }
            });
        }

        ImageView getImageView() {
            return mImageView;
        }

    }
    //--------------------------------------------------------------------------------
    //endregion

    //region Member Variables
    private static String TAG = "DEBUGGER-TAG";
    private ThumbnailActivity mActivity;
    private Cursor mCursor;
    //endregion

    //region Constructors and Overridden Methods
    public ThumbnailAdapter(ThumbnailActivity activity){
        mActivity = activity;
        Log.d(TAG, "ThumbnailAdapter constructor");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_imageview, parent, false);

        int width = parent.getWidth() / 3;
        int height = parent.getHeight() / 4;
        v.setMinimumHeight(height);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mActivity)
                .load(getUriFromMediaStore(position))
                .centerCrop()
                .dontAnimate()
                .into(holder.getImageView());

    }

    @Override
    public int getItemCount() {
        return ( (mCursor == null) ? 0 : mCursor.getCount() );
    }
    //endregion

    //region MediaStore Related Methods
    private Uri getUriFromMediaStore(int position){
        int dataIndex = mCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        mCursor.moveToPosition(position);

        String dataString = mCursor.getString(dataIndex);
        return Uri.parse("file://" + dataString);
    }

    //sets mCursor to passed in value, closes old cursors
    public void changeCursor(Cursor cursor){
        Log.d(TAG, "ThumbnailAdapter changeCursor()");
        Cursor oldCursor = swapCursor(cursor);
        if (oldCursor != null){     //swapCursor returns null if they're the same;
            oldCursor.close();
        }
    }

    //returns null if they're the same
    private Cursor swapCursor(Cursor cursor){
        if (mCursor == cursor){
            return null;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;
        if (cursor != null){                //when the cursor is changed and is not first cursor
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    //endregion

}
