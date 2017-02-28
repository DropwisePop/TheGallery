package com.example.ben.thegallery.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import com.example.ben.thegallery.R;
import com.example.ben.thegallery.adapters.ThumbnailAdapter;

public class ThumbnailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int REQUEST_READ_EXTERNAL_STORAGE_RESULT = 0;
    private static final int MEDIASTORE_LOADER_ID =  0;
    private ThumbnailAdapter mThumbnailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbnail);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.thumbnail_framelayout);
        frameLayout.setBackgroundColor(Color.rgb(184, 184, 184));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.thumbnail_recyclerview);
        mThumbnailAdapter = new ThumbnailAdapter(this);
        recyclerView.setAdapter(mThumbnailAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        checkReadExternalStoragePermission();
    }

    private void checkReadExternalStoragePermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED){
                getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);
            }
            else{
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_EXTERNAL_STORAGE_RESULT);
            }
        }
        else{
            getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_READ_EXTERNAL_STORAGE_RESULT:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        //NOTE: super was called in default statement in CatGallery; this has changed here
        //...and I just changed it back
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        return new CursorLoader(
                this,
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mThumbnailAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mThumbnailAdapter.changeCursor(null);
    }
}
