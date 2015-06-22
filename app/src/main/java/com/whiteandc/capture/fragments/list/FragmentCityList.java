package com.whiteandc.capture.fragments.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;

import com.whiteandc.capture.R;
import com.whiteandc.capture.data.Monument;
import com.whiteandc.capture.data.MonumentList;
import com.whiteandc.capture.fragments.BasicFragment;


public class FragmentCityList extends BasicFragment implements AdapterView.OnItemClickListener{

	private CityListAdapter adapter = null;
    private ListView list;
    private ViewTreeObserver vto;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_city_list, container, false);

        monumentActivity.setFullScreen(false);
        monumentActivity.setToolBarVisibility(true);
        monumentActivity.setHomeButtonVisibility(false);

        adapter = new CityListAdapter(this.getActivity());
	    list= (ListView) rootView.findViewById(R.id.city_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

        int capturedPercentage = MonumentList.calculatePercentage();
        monumentActivity.setToolbarTitle(MonumentList.getCityName() + " " + capturedPercentage + "%");

		return rootView;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("FragmentCityList", "onItemClick. position: "+position);
        Monument monument= MonumentList.getList().get(position);
        monumentActivity.setCurrentMonumentId(monument.getName());
        monumentActivity.switchToDetailAdapter();
    }
}
