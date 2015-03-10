package com.keith.android.g53ids;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.keith.android.g53ids.database.DBHelper;

import org.w3c.dom.Comment;
import org.w3c.dom.Text;


public class DetailsActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private String poi_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        poi_id = intent.getStringExtra(MainActivity.POI_ID);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return DetailsFragment.newInstance(position+1, poi_id);
                case 1:
                    return CommentFragment.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Information";
                case 1:
                    return "Comments";

            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CommentFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static CommentFragment newInstance(int sectionNumber) {
            CommentFragment fragment = new CommentFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public CommentFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_details, container, false);
            return rootView;
        }
    }


    /**
     *Fragment for details
     */
    public static class DetailsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_POI_ID = "poi_id";
        POI p;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DetailsFragment newInstance(int sectionNumber, String id) {
            DetailsFragment fragment = new DetailsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_POI_ID, id);
            fragment.setArguments(args);
            return fragment;
        }

        public DetailsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle bundle = getArguments();
            p = DBHelper.getInstance(getActivity()).getPoi(bundle.getString(ARG_POI_ID));
            View rootView = inflater.inflate(R.layout.fragment_details, container, false);
            TextView id = (TextView)rootView.findViewById(R.id.name_of_poi);
            TextView type = (TextView)rootView.findViewById(R.id.type_of_poi);
            TextView contact = (TextView)rootView.findViewById(R.id.contact_of_poi);
            TextView open = (TextView)rootView.findViewById(R.id.open_time);
            TextView close = (TextView)rootView.findViewById(R.id.close_time);
            CheckBox monday = (CheckBox)rootView.findViewById(R.id.monday);
            CheckBox tuesday = (CheckBox)rootView.findViewById(R.id.tuesday);
            CheckBox wednesday = (CheckBox)rootView.findViewById(R.id.wednesday);
            CheckBox thursday = (CheckBox)rootView.findViewById(R.id.thursday);
            CheckBox friday = (CheckBox)rootView.findViewById(R.id.friday);
            CheckBox saturday = (CheckBox)rootView.findViewById(R.id.saturday);
            CheckBox sunday = (CheckBox)rootView.findViewById(R.id.sunday);
            id.setText(p.getName());
            type.setText(p.getType());
            contact.setText(p.getContact());
            open.setText("Opens at: " + p.getOpenTime());
            close.setText("Closes at: " + p.getCloseTime());
            monday.setChecked(p.getMonday() == 1);
            tuesday.setChecked(p.getTuesday() == 1);
            wednesday.setChecked(p.getWednesday() == 1);
            thursday.setChecked(p.getThursday() == 1);
            friday.setChecked(p.getFriday() == 1);
            saturday.setChecked(p.getSaturday() == 1);
            sunday.setChecked(p.getSunday() == 1);
            Button submit = (Button)rootView.findViewById(R.id.navigate);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("poiID", p.getId());
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            });
            return rootView;
        }
    }

}
