package com.whiteandc.capture;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.whiteandc.capture.data.Monument;
import com.whiteandc.capture.data.MonumentList;
import com.whiteandc.capture.data.MonumentLoader;
import com.whiteandc.capture.fragments.map.FragmentMap;
import com.whiteandc.capture.fragments.map.FragmentMapDetail;
import com.whiteandc.capture.fragments.list.FragmentCityList;
import com.whiteandc.capture.fragments.notcaptured.FragmentNotCaptured;
import com.whiteandc.capture.tabs.SlidingTabLayout;
import com.whiteandc.capture.tabs.ViewPagerAdapterTabs;

public class MonumentsActivity extends ActionBarActivity {

    private static final String CLASS = "MonumentsActivity";
    private static final int CAMERA_REQUEST_CODE = 10000;

    private Toolbar toolbar;
    private String currentMonumentId;
    private ViewPager pager;
    private ViewPagerAdapterTabs adapter;
    private SlidingTabLayout tabs;
    private OnBackPressedListener backPressedListener;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monuments);
        toolbarCreation();
        createViewPager();

        MonumentLoader.loadMonuments(getPreferences(MODE_PRIVATE), this);

        currentMonumentId = MonumentList.getList().get(0).getName();
        switchToListAdapter();
    }

    private void createViewPager() {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        CharSequence[] titles = new CharSequence[]{getResources().getString(R.string.tab_monuments), getResources().getString(R.string.tab_map)};
        adapter = new ViewPagerAdapterTabs(getFragmentManager(), titles, titles.length);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // Makes the Tabs Fixed

        // TODO Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accentColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    private void toolbarCreation() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.getMenu().clear();
        setSupportActionBar(toolbar);
    }

    public void setToolbarTitle(String cityName) {
        if (toolbar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            Assets.setFont1(toolbarTitle, this);
            toolbarTitle.setText(cityName);
        }
    }

    public void setToolBarVisibility(boolean visible) {
        int visibility = (visible) ? View.VISIBLE : View.GONE;
        if (toolbar != null) {
            toolbar.setVisibility(visibility);
        }
    }

    public void setFullScreen(boolean fullScreen) {
        if (fullScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    public void setHomeButtonVisibility(boolean visibility) {
        getSupportActionBar().setHomeButtonEnabled(visibility);
        getSupportActionBar().setDisplayHomeAsUpEnabled(visibility);
    }

    public void switchToListAdapter() {
        switchToAdapter(new FragmentCityList(), new FragmentMap());
    }

    public void switchToDetailAdapter() {
        switchToAdapter(new FragmentNotCaptured(), new FragmentMapDetail());
    }

    private void switchToAdapter(Fragment leftFragment, Fragment rightFragment){
        Log.i(CLASS, "switchToAdapter: " + leftFragment.getClass().getSimpleName());

        backPressedListener = adapter;
        adapter.deleteFragments();
        adapter.setFragments(leftFragment, rightFragment);
        final String leftTag =
                getResources().getString((leftFragment instanceof FragmentCityList)?
                        R.string.tab_monuments : R.string.tab_monument);

        adapter.setTitles(new CharSequence[]{leftTag, getResources().getString(R.string.tab_map)});
        tabs.updateTextTitles();
        pager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.monuments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            switchToListAdapter();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setCurrentMonumentId(String monumentId) {
        currentMonumentId = monumentId;
    }

    public String getCurrentMonumentId() {
        return currentMonumentId;
    }

    @Override
    public void onBackPressed() {
        backPressedListener.onBackPressed(this);
    }

    public void startCameraActivity(int currentPicture) {
        Monument monument = MonumentList.getMonument(currentMonumentId);
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(Assets.CURRENT_PICTURE, currentPicture);
        intent.putExtra(Assets.MONUMENT_ID, currentMonumentId);
        intent.putExtra(Assets.PICTURE_RESOURCE, monument.getPhotos()[currentPicture]);

        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(CLASS, "result: " + requestCode + "  " + resultCode);
        if(requestCode == CAMERA_REQUEST_CODE){
            currentMonumentId = data.getStringExtra(Assets.MONUMENT_ID);
            if(resultCode == RESULT_OK){
                Log.i(CLASS, "ok!");
                showCustomWebView();
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                Log.i(CLASS, "Set key "+ currentMonumentId+" to true");
                editor.putBoolean(currentMonumentId, true);
                editor.apply();
                Monument monument = MonumentList.getMonument(currentMonumentId);
                monument.setIsCaptured(true);
            }else if(resultCode == RESULT_CANCELED){
                Log.i(CLASS, "cancelled!");
            }
        }

    }

    private void showCustomWebView() {
        new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .title(R.string.congratulations)
                .titleColorRes(R.color.primaryColor)
                .content(getString(R.string.captured_msg) + " " + currentMonumentId)
                .positiveText(R.string.ok)
                .show();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public interface OnBackPressedListener {
        public void onBackPressed(MonumentsActivity monumentsActivity);
    }
}
