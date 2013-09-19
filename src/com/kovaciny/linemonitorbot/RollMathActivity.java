package com.kovaciny.linemonitorbot;

import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.primexmodel.Roll;

public class RollMathActivity extends FragmentActivity implements TabListener {
    
    double DIAMETER_SAFETY_FACTOR = .1875; //also having them use the ordered, not average gauge

    
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     * Only public for testing.
     */
    public ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle args) {
        super.onCreate(args);
        
        //Share extras across fragments with SharedPreferences
        Bundle extras = getIntent().getExtras();
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        
        editor.putInt("RollMath.coreType", extras.getInt("coreType"));
        editor.putInt("RollMath.linearFeet", extras.getInt("linearFeet"));
        editor.putFloat("RollMath.width", extras.getFloat("width"));

        editor.commit();
        
        
        setContentView(R.layout.activity_roll_math);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2); 
        
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setCustomView(mSectionsPagerAdapter.getPageLabel(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            Fragment fragment = null;
            Bundle args = new Bundle();
            switch(position) {
                case (0): fragment = new RollMathDiameterFragment();
                break;
                
                case (1): fragment = new RollMathWeightFragment();
                break;
                
                case (2): fragment = new RollMathFeetFragment();
                break;
            }
          
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        public int getPageLabel(int position) {
            switch (position) {
                case 0:
                    return R.layout.tab_bar_icon_diameter;
                case 1:
                    return R.layout.tab_bar_icon_weight;
                case 2:
                    return R.layout.tab_bar_icon_linear_feet;
            }
            return 0;
        }
    }
        /*
         * 
         */
        public double calculateRollDiameter(int coreType, int linearFeet, double orderedGauge) {
            double areaOfPlastic = Double.valueOf(linearFeet) * HelperFunction.INCHES_PER_FOOT * orderedGauge;
            double areaOfRoll = Roll.getCoreArea(coreType) + areaOfPlastic;
            double radiusOfRoll = Math.sqrt(areaOfRoll / Math.PI);
            double diameterOfRoll = 2 * radiusOfRoll;
            return diameterOfRoll + DIAMETER_SAFETY_FACTOR;
        }
        
        /*
         * 
         */
        public double calculateRollDiameter(int coreType, double rollWidth, double rollGrossWeight, double materialDensity) {
            double rollNetWeight = rollGrossWeight - Roll.getCoreWeight(coreType, rollWidth);
            double rollNetVolume = rollNetWeight / materialDensity;
            double coreVolume = Math.PI * Math.pow(Roll.getCoreOutsideRadius(coreType), 2d) * rollWidth;
            double rollTotalVolume = rollNetVolume + coreVolume;
            double rollRadius = Math.sqrt( rollTotalVolume / Math.PI / rollWidth);
            return (rollRadius * 2d) + DIAMETER_SAFETY_FACTOR;
            //diam from weight and width only, and factor, not gauge.
                        //net weight = a cube width * (rO - rI)
                        //volume = (volume of a cylinder - volume of a inner cylinder)
                        //volume = mass/density
                    //TODO how about a reference weight version?
                    //or a gauge factor version
        }
        
        /*
         * 
         */
        public double calculateMaterialDensity(double linearFootWeight, double gauge, double rollWidth) {
            double linearFootVolume = gauge * rollWidth * HelperFunction.INCHES_PER_FOOT;
            return linearFootWeight / linearFootVolume;
            
        }

        /*
         * 
         */
        public double calculateLinearFeet(double rollNetWeight, double referenceNetWeight, int referenceLinearFeet) {
            return Double.valueOf(referenceLinearFeet) * rollNetWeight / referenceNetWeight;
        }
        
        /*
         * 
         */
        public double calculateLinearFeet(int coreType, double width, double rollGrossWeight, double linearFootWeight) {
            double plasticWeight = rollGrossWeight - Roll.getCoreWeight(coreType, width);
            return plasticWeight / linearFootWeight;
        }
        
        /*
         * 
         */
        public double calculateLinearFeet(int coreType, double rollRadius, double orderedGauge) {
            double rollArea = Math.PI * Math.pow(rollRadius, 2d);
            double coreArea = Roll.getCoreArea(coreType);
            double plasticArea = rollArea - coreArea;
            return plasticArea / 12 / orderedGauge;
        }
        
        /*
         * 
         */
        public double calculateRollNetWeight(int linearFeet, double footWeight) {
            return Double.valueOf(linearFeet) * footWeight;
        }
        
        /*
         * 
         */
        public double calculateRollNetWeight(int linearFeet, double referenceNetWeight, int referenceLinearFeet) {
            return Double.valueOf(linearFeet) / Double.valueOf(referenceLinearFeet) * referenceNetWeight;
        }
}
