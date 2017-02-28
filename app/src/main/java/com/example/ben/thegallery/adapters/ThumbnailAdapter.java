package com.example.ben.thegallery.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
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

/**
 * Created by dropwisepop on 2/24/2017.
 */
public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {

    //--------------------------------------------------------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView mImageView;

        public ViewHolder(View v){
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.thumbnail_imageview);
        }

        public ImageView getImageView() {
            return mImageView;
        }
    }
    //--------------------------------------------------------------------------------



    public static String DEBUGGER_TAG = "DEBUGGER-TAG";
    private Activity mActivity;
    private Cursor mCursor;

    public ThumbnailAdapter(Activity activity){
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_imageview, parent, false);

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

    private Uri getUriFromMediaStore(int position){
        int dataIndex = mCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        mCursor.moveToPosition(position);

        String dataString = mCursor.getString(dataIndex);
        return Uri.parse("file://" + dataString);
    }

    //sets mCursor to passed in value, closes old cursors
    public void changeCursor(Cursor cursor){
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

}
