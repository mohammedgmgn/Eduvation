package org.mat.eduvation.navigation_items;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.client.Firebase;

import org.mat.eduvation.R;
import org.mat.eduvation.adapters.scheduleAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class Schedule extends Fragment {


    public Schedule() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_schedule, container, false);

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.tab_layoutsch);
        tabLayout.addTab(tabLayout.newTab().setText("Day 1"));
        //  tabLayout.addTab(tabLayout.newTab().setText("Day 2"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Disablesecondtab(tabLayout);


        final ViewPager viewPager = (ViewPager) root.findViewById(R.id.pagersch);
        final scheduleAdapter adapter = new scheduleAdapter(getFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        return root ;
    }
    private void Disablesecondtab(TabLayout mytab){

        LinearLayout tabStrip = ((LinearLayout)mytab.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

    }

}
