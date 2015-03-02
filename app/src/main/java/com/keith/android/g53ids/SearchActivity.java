package com.keith.android.g53ids;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.keith.android.g53ids.database.DBHelper;

import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity{
    private ResultAdapter adapter;
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onNewIntent(Intent intent){
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            processQuery(query);
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
            nameTextView.setTag(p.getId());
            return convertView;
        }
    }

    public void processQuery(String query){
        ResultList.getInstance().clear();
        ArrayList<POI>poiList = DBHelper.getInstance(this).getPois(query);
        if(poiList.isEmpty()) {
            ResultList.getInstance().add(new POI("0","No results found",new LatLong(0,0)));
        }
        else {
            for (POI x : poiList) {
                ResultList.getInstance().add(x);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
