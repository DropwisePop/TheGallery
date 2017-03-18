package com.example.ben.thegallery.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.ben.thegallery.R;
import com.example.ben.thegallery.adapters.FullscreenPagerAdapter;
import com.example.ben.thegallery.data.GallerySettings;
import com.example.ben.thegallery.data.GalleryUtil;

import java.io.File;

/**
 * Created by dropwisepop on 3/10/2017.
 */
public class FullscreenActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, FullscreenPagerAdapter.AdapterCallbacks {

    //region Member Variables
    public static String KEY_PAGER_POSITION = "com.example.ben.thegallery.POSITION_RESULT";
    private static String BUNDLE_ADAPTER_POSITION = "I am a bad person";
    private static boolean sDestroyed = true;
    private int mStartPosition;
    private int mPreviousPosition;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private Cursor mCursor;
    private boolean mToolbarShown;
    private FullscreenPagerAdapter mFullscreenAdapter;
    //endregion

    //region Lifecycle Methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mToolbarShown = false;
        if (savedInstanceState != null) {
            mStartPosition = savedInstanceState.getInt(BUNDLE_ADAPTER_POSITION, 0);
            mPreviousPosition = mStartPosition;
            getSupportLoaderManager().restartLoader(GalleryUtil.MEDIASTORE_LOADER_ID, null, this);
        } else {
            getSupportLoaderManager().initLoader(GalleryUtil.MEDIASTORE_LOADER_ID, null, this);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        hideStatusBar();    //TODO: onSwipeUp() -> hideStatusBar()
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sDestroyed = true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY_PAGER_POSITION, mViewPager.getCurrentItem());
        setResult(RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(BUNDLE_ADAPTER_POSITION, mViewPager.getCurrentItem());
    }
    //endregion

    //region Loader Methods
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
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
    public void onLoadFinished(Loader loader, Cursor data) {
        mCursor = data;
        if (sDestroyed) {
            setupUI(data);
            sDestroyed = false;
        } else {
            mFullscreenAdapter.changeCursor(data);      //SHARED LINE
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mFullscreenAdapter.changeCursor(null);
        this.finish();
    }
    //endregion

    //region UI Methods
    private void setupUI(Cursor data) {

        mToolbar = (Toolbar) findViewById(R.id.fullscreen_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setBackgroundColor(GallerySettings.getToolbarColor());
        mToolbar.setTranslationY(mToolbar.getBottom());

        mViewPager = (ViewPager) findViewById(R.id.fullscreen_viewpager);

        mFullscreenAdapter = new FullscreenPagerAdapter(this);
        mViewPager.setAdapter(mFullscreenAdapter);
        mFullscreenAdapter.changeCursor(data);      //SHARED LINE

        Intent callingIntent = getIntent();
        mStartPosition = callingIntent.getIntExtra(ThumbnailActivity.EXTRA_PAGER_POSITION, 0);
        mPreviousPosition = mStartPosition;
        mViewPager.setCurrentItem(mStartPosition);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mPreviousPosition < position) {
                    GallerySettings.setDirectionAscending();
                } else if (position < mPreviousPosition) {
                    GallerySettings.setDirectionDescending();
                }
                mPreviousPosition = position;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("DEBUGGER-TAG", "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_fullscreen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_fullscreen_trash:
                //TODO: the following few lines, here and elsewhere, are id to getUriFromMediastore
                int dataIndex = mCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                mCursor.moveToPosition(position);
                String path = mCursor.getString(mViewPager.getCurrentItem());
                File toDelete = new File(path);
                if (toDelete.exists()){
                    if (toDelete.delete()){
                        Log.d("DEBUGGER-TAG", "WAS DELETED");
                    }
                    else{
                        Log.d("DEBUGGER-TAG", "WAS NOT DELETED");
                    }
                }
                mFullscreenAdapter.notifyDataSetChanged();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onImageClick() {
        int nextPage = mViewPager.getCurrentItem() + GallerySettings.getDirection();
        if (-1 < nextPage && nextPage < mFullscreenAdapter.getCount()) {
            mViewPager.setCurrentItem(nextPage, false);
        }
    }

    @Override
    public void onImageLongClick() {
        setToolbarShown(!mToolbarShown);
    }

    private void setToolbarShown(boolean showToolbar) {
        if (showToolbar) {
            mToolbar.animate().translationY(0);
            mToolbarShown = true;
        } else {
            mToolbar.animate().translationY(mToolbar.getBottom());
            mToolbarShown = false;
            hideStatusBar();
        }
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    //endregion
}
