package com.keith.android.g53ids;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.graphhopper.util.StopWatch;
import com.keith.android.g53ids.database.DBHelper;

import org.mapsforge.core.model.LatLong;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.keith.android.g53ids.R.drawable.*;


public class SearchActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener{
    private final static String TAG = "SearchActivity";
    private ResultAdapter adapter;
    private RadioGroup optionSearch;
    private RadioButton nameSearch;
    private RadioButton tagSearch;
    private Spinner availabilitySpinner;
    SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");
    private Calendar calendar;
    private int day;
    private int hour;
    Location l;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        adapter = new ResultAdapter(ResultList.getInstance());
        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView< ?> parent, View view, int position, long id) {
                TextView nameTV = (TextView) view.findViewById(R.id.poi_name);
                String poiID = (String)nameTV.getTag();
                if(poiID.equals("0")){

                }
                else{
                    Intent intent = new Intent();
                    intent.putExtra("poiID", poiID);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
        optionSearch = (RadioGroup) findViewById(R.id.search_option);
        optionSearch.check(R.id.search_name);
        nameSearch = (RadioButton) findViewById(R.id.search_name);
        tagSearch = (RadioButton) findViewById(R.id.search_tag);
        availabilitySpinner = (Spinner) findViewById(R.id.availability);
        initSpinner();
        optionSearch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(nameSearch.isChecked()){
                    Log.d(TAG, "Search by name initiated");
                }
                else if(tagSearch.isChecked()){
                    Log.d(TAG, "Search by tag initiated");
                }
            }
        });
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_WEEK); //Sunday starts with 1
        hour = calendar.get(Calendar.HOUR_OF_DAY); //10pm is 22
        l = new Location("Origin");
        LatLong loc = UserLocation.getInstance().getLocation();
        l.setLatitude(loc.latitude);
        l.setLongitude(loc.longitude);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent){
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
//            processQuery(query);
            new processSearch().execute(query);
        }
    }

    private class ResultAdapter extends ArrayAdapter<POI>{
        public ResultAdapter(ArrayList<POI> results) {super(getApplicationContext(),0,results);}
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.list_item, null);
            }
            POI p = getItem(position);
            TextView nameTextView = (TextView)convertView.findViewById(R.id.poi_name);
            nameTextView.setText(p.getName());
            TextView typeTextView = (TextView)convertView.findViewById(R.id.poi_type);
            View shopStatus = (View)convertView.findViewById(R.id.poi_status);
//            if(!p.getId().equals("0")){
                typeTextView.setText(p.getType());

            LatLong destLoc = p.getCoordinates();
            Location d = new Location("Destination");
            d.setLatitude(destLoc.latitude);
            d.setLongitude(destLoc.longitude);
            double distance = Math.round(l.distanceTo(d)/1000.0 * 100.0) / 100.0;
            TextView distanceTextView = (TextView)convertView.findViewById(R.id.poi_distance);
            distanceTextView.setText(Double.toString(distance) + "km");

                if(shopOpenDay(p) && shopOpenHours(p)) {
//                    convertView.setBackgroundColor(Color.GREEN);
                    shopStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_circle));
                }
                else {
//                    convertView.setBackgroundColor(Color.RED);
                    shopStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_circle));
                }

//            }
//            else{
//                typeTextView.setText("Changing your search options may return results");
//                convertView.setBackgroundColor(Color.WHITE);
//            }

            nameTextView.setTag(p.getId());
            return convertView;
        }
    }

    public void onRadioButtonClicked(View v){
        boolean checked = ((RadioButton)v).isChecked();
        if(!checked){
            switch(v.getId()){
                case R.id.search_name:
                    Log.d(TAG, "Search by name initiated");
                    break;
                case R.id.search_tag:
                    Log.d(TAG, "Search by tag initiated");
                    break;
            }
        }
    }

    public void initSpinner(){
        availabilitySpinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.availability_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        availabilitySpinner.setAdapter(adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.d(TAG,"An item was selected");
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private class processSearch extends AsyncTask<String, Void, ArrayList<POI>>{
        protected ArrayList<POI> doInBackground(String... query){
            Log.d(TAG, "Doing in background");
            ResultList.getInstance().clear();
            ArrayList<POI>poiList;
            boolean availableNow = availabilitySpinner.getSelectedItem().toString().equals("Open Now");
            if(optionSearch.getCheckedRadioButtonId() == nameSearch.getId()){
                float time;
                StopWatch sw = new StopWatch().start();
                Log.d(TAG, "Checked button is name ");
                poiList = DBHelper.getInstance(SearchActivity.this).getPois(availableNow, query[0]);
                time = sw.stop().getSeconds();
                Log.d(TAG, "Time take for search: "+time);
                return poiList;
            }
            else{
                float time;
                StopWatch sw = new StopWatch().start();
                Log.d(TAG, "Checked button is tag");
                poiList = DBHelper.getInstance(SearchActivity.this).getTagRelatedPois(availableNow, query[0]);
                time = sw.stop().getSeconds();
                Log.d(TAG, "Time take for search: "+time);
            }

//            ArrayList<POI>poiList = DBHelper.getInstance(SearchActivity.this).getPois(query[0]);
            return poiList;
        }

        protected void onPostExecute(ArrayList<POI> list){
            Log.d(TAG, "Post execution started");
            if(list.isEmpty()) {
//                ResultList.getInstance().add(new POI("0","No results found",new LatLong(0,0)));
                Toast.makeText(SearchActivity.this, "No results found", Toast.LENGTH_LONG).show();
            }
            else {
                for (POI x : list) {
                    ResultList.getInstance().add(x);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    public boolean shopOpenDay(POI p){
        switch (day){
            case 1:
                return p.getSunday() == 1;
            case 2:
                return p.getMonday() == 1;
            case 3:
                return p.getTuesday() == 1;
            case 4:
                return p.getWednesday() == 1;
            case 5:
                return p.getThursday() == 1;
            case 6:
                return p.getFriday() == 1;
            case 7:
                return p.getSaturday() == 1;
            default:
                return false;
        }
    }

    public boolean shopOpenHours(POI p){
        try {
            Date currentTime = parser.parse(hour+":00:00");
            Date openTime = parser.parse(p.getOpenTime());
            Date closeTime = parser.parse(p.getCloseTime());
            if(currentTime.after(openTime) && currentTime.before(closeTime)){
                Log.d(TAG, "Shop is open!");
                return true;
            }
            else{
                Log.d(TAG, "Shop is close!");
                return false;
            }
        }catch(ParseException e){
            Log.d(TAG, "Date parsing failed");
            return false;
        }
    }
}
