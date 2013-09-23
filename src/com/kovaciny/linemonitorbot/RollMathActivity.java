package com.kovaciny.linemonitorbot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.kovaciny.linemonitorbot.CoreTypeFragment.OnCoreTypeChangedListener;
import com.kovaciny.primexmodel.Roll;

public class RollMathActivity extends FragmentActivity implements TabListener, OnCoreTypeChangedListener {
    
    public static final double DIAMETER_SAFETY_FACTOR = .1875; //also having them use the ordered, not average gauge
    public static final int DIAMETER_FRAGMENT_POSITION = 0;
    public static final int WEIGHT_FRAGMENT_POSITION = 1;
    public static final int FEET_FRAGMENT_POSITION = 2;
    
    int mCoreType;
    double mWidth;
    
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
     * 
     * Only public for testing.
     */
    public ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle args) {
        super.onCreate(args);
        
        Bundle extras = getIntent().getExtras();
        mCoreType = extras.getInt("coreType");
        mWidth = Double.valueOf(extras.getFloat("width"));
        
        //Share extras across fragments with SharedPreferences
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);	//TODO don't use
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("RollMath.linearFeet", extras.getInt("linearFeet"));
        editor.putFloat("RollMath.width", extras.getFloat("width"));
        editor.putFloat("RollMath.footWeight", extras.getFloat("footWeight"));

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
                case DIAMETER_FRAGMENT_POSITION: 
                	fragment = new RollMathDiameterFragment();
                break;
                
                case (WEIGHT_FRAGMENT_POSITION): 
                	fragment = new RollMathWeightFragment();
                break;
                
                case (FEET_FRAGMENT_POSITION): 
                	fragment = new RollMathFeetFragment();
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
    
	public Fragment findFragmentByPosition(int pos) {
		String tag = "android:switcher:" + mViewPager.getId() + ":" + pos;
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
		return fragment;
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
        public double calculateRollNetWeight(int coreType, double rollWidth, double diameter, double materialDensity) {
            double rollVolume = Math.PI * Math.pow(diameter / 2, 2) * rollWidth;
            double coreVolume = Math.PI * Math.pow(Roll.getCoreOutsideRadius(coreType), 2d) * rollWidth;
            double netVolume = rollVolume - coreVolume;
            return netVolume * materialDensity;
        }
        
        /*
         * 
         */
        public double calculateRollNetWeight(int linearFeet, double referenceNetWeight, int referenceLinearFeet) {
            return Double.valueOf(linearFeet) / Double.valueOf(referenceLinearFeet) * referenceNetWeight;
        }
        
        public void onCoreTypeChanged(int checkedRadio, boolean heavyWallChecked) {
			switch (checkedRadio) {
			case R.id.radio_r3:
				mCoreType = heavyWallChecked ? Roll.CORE_TYPE_R3_HEAVY : Roll.CORE_TYPE_R3;
				break;
			case R.id.radio_r6:
				mCoreType = heavyWallChecked ? Roll.CORE_TYPE_R6_HEAVY : Roll.CORE_TYPE_R6;
				break;
			case R.id.radio_r8:
				mCoreType = Roll.CORE_TYPE_R8;
				break;
			default: throw new RuntimeException("invalid core type");
			}
			
			List<CoreTypeFragment> ctfs = new ArrayList<CoreTypeFragment>();
			ctfs.add((CoreTypeFragment) getSupportFragmentManager().findFragmentById(R.id.core_type_fragment_1));
			ctfs.add((CoreTypeFragment) getSupportFragmentManager().findFragmentById(R.id.core_type_fragment_2));
			ctfs.add((CoreTypeFragment) getSupportFragmentManager().findFragmentById(R.id.core_type_fragment_3));
			for (Iterator<CoreTypeFragment> i = ctfs.iterator(); i.hasNext(); ) {
			    CoreTypeFragment ctf = i.next();
			    if (ctf != null) {
			        ctf.initializeViews(mCoreType, getCoreWeight());
			    }
			}
        }
        
        public int getCoreType() {
        	 return mCoreType;
        }
        
        public double getCoreWeight() {
            return Roll.getCoreWeight(mCoreType, mWidth);
        }
}
