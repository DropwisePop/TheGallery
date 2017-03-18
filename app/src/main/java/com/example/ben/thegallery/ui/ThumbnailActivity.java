package com.example.ben.thegallery.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.ben.thegallery.R;
import com.example.ben.thegallery.adapters.ThumbnailAdapter;
import com.example.ben.thegallery.data.GallerySettings;
import com.example.ben.thegallery.data.GalleryUtil;

/**
 * The Activity managing the thumbnail bucket
 */
public class ThumbnailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    //region Member Variables
    private static String TAG = "DEBUGGER-TAG";
    private static final int REQUEST_CODE_READ_EXTERNAL = 0;
    private static final int REQUEST_CODE_PAGER_POSITION = 1;
    private static final int GOOD_THUMB_SIZE_IN_PIXELS = 480;
    public static final String EXTRA_PAGER_POSITION = "com.example.ben.thegallery.POSITION";

    private ThumbnailAdapter mThumbnailAdapter;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    //endregion

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbnails);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int spanCount = screenWidth / GOOD_THUMB_SIZE_IN_PIXELS;

        mRecyclerView = (RecyclerView) findViewById(R.id.thumbnail_recyclerview);
        mThumbnailAdapter = new ThumbnailAdapter(this);
        mRecyclerView.setAdapter(mThumbnailAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        mToolbar = (Toolbar) findViewById(R.id.thumb_toolbar);
        mToolbar.setBackgroundColor( GallerySettings.getToolbarColor() );
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        checkReadExternalStoragePermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(GalleryUtil.MEDIASTORE_LOADER_ID, null, this);
    }

    //endregion

    //region Methods for Permissions
    private void checkReadExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                getSupportLoaderManager().initLoader(GalleryUtil.MEDIASTORE_LOADER_ID, null, this);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_READ_EXTERNAL);
            }
        } else {
            getSupportLoaderManager().initLoader(GalleryUtil.MEDIASTORE_LOADER_ID, null, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_EXTERNAL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSupportLoaderManager().initLoader(GalleryUtil.MEDIASTORE_LOADER_ID, null, this);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //endregion

    //region Methods for Loaders
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
    //endregion

    //region UI Related Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_toolbar_main_on, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //TODO: cases
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //endregion

    //region Methods for Activity Interaction
    public void StartFullscreenActivity(int position) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.putExtra(EXTRA_PAGER_POSITION, position);
        startActivityForResult(intent, REQUEST_CODE_PAGER_POSITION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportLoaderManager().restartLoader(GalleryUtil.MEDIASTORE_LOADER_ID, null, this);
        if (requestCode == REQUEST_CODE_PAGER_POSITION) {
            if (resultCode == Activity.RESULT_OK) {
                int position = data.getIntExtra(FullscreenActivity.KEY_PAGER_POSITION, 0);
                mRecyclerView.getLayoutManager().scrollToPosition(position);
            }
        }
    }
    //endregion

}
