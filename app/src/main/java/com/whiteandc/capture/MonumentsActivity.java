package com.whiteandc.capture;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.whiteandc.capture.data.Monument;
import com.whiteandc.capture.data.MonumentList;
import com.whiteandc.capture.data.MonumentLoader;
import com.whiteandc.capture.fragments.map.FragmentMap;
import com.whiteandc.capture.fragments.map.FragmentMapDetail;
import com.whiteandc.capture.fragments.list.FragmentCityList;
import com.whiteandc.capture.fragments.notcaptured.FragmentNotCaptured;
import com.whiteandc.capture.tabs.SlidingTabLayout;
import com.whiteandc.capture.tabs.ViewPagerAdapterTabs;

public class MonumentsActivity extends ActionBarActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String CLASS = "MonumentsActivity";
    private static final int CAMERA_REQUEST_CODE = 10000;
    private static final int REQUEST_RESOLVE_ERROR = 10010;
    private static final String DIALOG_ERROR = "dialog_error";

    private Toolbar toolbar;
    private String currentMonumentId;
    private ViewPager pager;
    private ViewPagerAdapterTabs adapter;
    private SlidingTabLayout tabs;
    private OnBackPressedListener backPressedListener;
    private LocationUpdateListener locationUpdateListener;

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private Location mLastLocation;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monuments);
        toolbarCreation();
        createViewPager();

        MonumentLoader.loadMonuments(getPreferences(MODE_PRIVATE), this);

        buildGoogleApiClient();

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
        final FragmentMapDetail mapDetail = new FragmentMapDetail();
        locationUpdateListener = mapDetail;
        switchToAdapter(new FragmentNotCaptured(), mapDetail);
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public Location getLastLocation(){
        return mLastLocation;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.i(CLASS, "mLastLocation: " + mLastLocation);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, createLocationRequest(), this);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }


    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(CLASS, "onLocationChanged: " + mLastLocation);
        mLastLocation = location;
        if(locationUpdateListener != null) {
            locationUpdateListener.onLocationChanged(mLastLocation);
        }
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MonumentsActivity)getActivity()).onDialogDismissed();
        }
    }

    public interface LocationUpdateListener {
        public void onLocationChanged(Location location);
    }

    public interface OnBackPressedListener {
        public void onBackPressed(MonumentsActivity monumentsActivity);
    }
}
