package com.example.ben.thegallery.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ben.thegallery.R;

/**
 * Created by dropwisepop on 3/10/2017.
 */

public class FullscreenPagerAdapter extends PagerAdapter {

    //region Member Variables
    public interface AdapterCallbacks {
        void onImageClick();
        void onImageLongClick();
    }

    private AdapterCallbacks mCallbacks;
    private Context mContext;
    private Cursor mCursor;
    //endregion

    //region Constructors
    public FullscreenPagerAdapter(Context context) {
        mContext = context;
        mCallbacks = (AdapterCallbacks) context;
    }
    //endregion

    //region Overridden Methods
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.pageitem_imageview, container, false);

        final ImageView imageView = (ImageView) view.findViewById(R.id.fullscreen_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onImageClick();
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCallbacks.onImageLongClick();
                return true;
            }
        });

        Glide.with(mContext)
                .load(getUriFromMediaStore(position))
                .fitCenter()
                .into(imageView);

        container.addView(view);
        return view;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return ((mCursor == null) ? 0 : mCursor.getCount());
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    //endregion

    //region MediaStore Related Methods
    private Uri getUriFromMediaStore(int position) {
        int dataIndex = mCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        mCursor.moveToPosition(position);

        String dataString = mCursor.getString(dataIndex);
        return Uri.parse("file://" + dataString);
    }

    //sets mCursor to passed in value, closes old cursors
    public void changeCursor(Cursor cursor) {
        Log.d("DEBUGGER-TAG", "FSPA changeCursor()");
        Cursor oldCursor = swapCursor(cursor);
        if (oldCursor != null) {     //swapCursor returns null if they're the same;
            oldCursor.close();
        }
    }

    //returns null if they're the same
    private Cursor swapCursor(Cursor cursor) {
        Log.d("DEBUGGER-TAG", "FSPA swapCursor()");
        if (mCursor == cursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;
        if (cursor != null) {                //when the cursor is changed and is not first cursor; SUPPOSEDLY
            int x = mCursor.getCount();
            this.notifyDataSetChanged();
            Log.d("DEBUGGER-TAG", "FSPA swapCursor(), cursor not null, cursor count..." + mCursor.getCount());
            Log.d("DEBUGGER-TAG", "FSPA swapCursor(), cursor not null, FSPA count...  " + getCount());
        } else {
            Log.d("DEBUGGER-TAG", "cursor was null");
        }
        return oldCursor;
    }

    //endregion

}

