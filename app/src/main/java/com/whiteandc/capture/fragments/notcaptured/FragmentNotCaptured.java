package com.whiteandc.capture.fragments.notcaptured;

import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;
import com.whiteandc.capture.R;
import com.whiteandc.capture.fragments.BasicFragment;
import com.whiteandc.capture.data.Monument;
import com.whiteandc.capture.data.MonumentList;
import com.whiteandc.capture.util.StringUtil;


public class FragmentNotCaptured extends BasicFragment implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final String CLASS = "FragmentNotCaptured";
    public static final int CROP_INDEX = 250;
    private Monument monument;
    private ViewPagerAdapter adapter;
    private int currentPicture = 0;
    private TextView description;
    private TextView unlock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(CLASS, "monumentActivity.getCurrentMonumentId(): "+monumentActivity.getCurrentMonumentId());

        // This is a fail safe method. It shouldn't be necessary
        if(monumentActivity.getCurrentMonumentId() == null){
            monumentActivity.setCurrentMonumentId(MonumentList.getList().get(0).getName());
            monumentActivity.switchToListAdapter();
            Log.w(CLASS, "Empty current monumentId, this may be due to an error during start activity for result");
        }
        monument = MonumentList.getMonument(monumentActivity.getCurrentMonumentId());
        View rootView = inflater.inflate(R.layout.fragment_monument_not_captured, container, false);

        monumentActivity.setFullScreen(false);
        monumentActivity.setToolBarVisibility(true);
        monumentActivity.setHomeButtonVisibility(true);

        adapter = new ViewPagerAdapter(monumentActivity, monument.getPhotos());
        adapter.notifyDataSetChanged();
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.photo_pager);
        viewPager.setAdapter(adapter);

        description = (TextView) rootView.findViewById(R.id.description);
        unlock = (TextView) rootView.findViewById(R.id.unlock);
        setCapturedStatus();

        //Bind the title indicator to the adapter
        CirclePageIndicator circleIndicator = (CirclePageIndicator)rootView.findViewById(R.id.indicator);
        circleIndicator.setViewPager(viewPager);
        circleIndicator.setOnPageChangeListener(this);
        rootView.findViewById(R.id.camera_fab).setOnClickListener(this);

        monumentActivity.setToolbarTitle(monument.getName());



        return rootView;
    }

    public void setCapturedStatus() {
        Log.i(CLASS, "monument.isCaptured(): " + monument.isCaptured());
        if(monument.isCaptured()){
            description.setText(monument.getDescription());
            unlock.setVisibility(View.GONE);
        }else{
            unlock.setVisibility(View.VISIBLE);
            String monumentDescription = monument.getDescription();
            description.setText(StringUtil.cropString(monumentDescription, CROP_INDEX)+"...");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        setCapturedStatus();
    }

    @Override
    public void onClick(View v) {
        monumentActivity.startCameraActivity(currentPicture);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        currentPicture = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
