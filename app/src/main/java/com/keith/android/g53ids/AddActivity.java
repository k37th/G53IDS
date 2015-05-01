package com.keith.android.g53ids;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.graphhopper.util.StopWatch;
import com.keith.android.g53ids.database.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapsforge.core.model.LatLong;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;


public class AddActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "AddActivity";
    static final int SELECT_COORDINATES_REQUEST = 1;
    private EditText poiName;
    private EditText poiContact;
    private Spinner poiGroup;
    private EditText poiOpenHour;
    private EditText poiCloseHour;
    private CheckBox poiMondayOpen;
    private CheckBox poiTuesdayOpen;
    private CheckBox poiWednesdayOpen;
    private CheckBox poiThursdayOpen;
    private CheckBox poiFridayOpen;
    private CheckBox poiSaturdayOpen;
    private CheckBox poiSundayOpen;
    private TextView poiLatitude;
    private TextView poiLongitude;
    private ProgressDialog progressDialog;
    NumberFormat formatter = new DecimalFormat("#0.00000000");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initView();
        initSpinner();
        initProgressDialog();
        initUseCurrentCoordinatesButton();
        initSelectCoordinatesButton();
        initSubmitButton();
    }

    public void initView(){
        poiName = (EditText)findViewById(R.id.poi_name);
        poiGroup = (Spinner) findViewById(R.id.spinner);
        poiContact = (EditText)findViewById(R.id.contact);
        poiOpenHour = (EditText)findViewById(R.id.open_time);
        poiCloseHour = (EditText)findViewById(R.id.close_time);
        poiMondayOpen = (CheckBox)findViewById(R.id.monday);
        poiTuesdayOpen = (CheckBox)findViewById(R.id.tuesday);
        poiWednesdayOpen = (CheckBox)findViewById(R.id.wednesday);
        poiThursdayOpen = (CheckBox)findViewById(R.id.thursday);
        poiFridayOpen = (CheckBox)findViewById(R.id.friday);
        poiSaturdayOpen = (CheckBox)findViewById(R.id.saturday);
        poiSundayOpen = (CheckBox)findViewById(R.id.sunday);
        poiLatitude = (TextView)findViewById(R.id.poi_latitude);
        poiLongitude = (TextView)findViewById(R.id.poi_longitude);
        addOnTextChangeListener();
    }

    private void addOnTextChangeListener(){
        EditText array[] = {poiName,poiContact,poiOpenHour,poiCloseHour};
        for(int i=0; i<array.length;i++){
            final EditText field = array[i];
            field.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    field.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    public void initSpinner(){
        poiGroup.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.grouping_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        poiGroup.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SELECT_COORDINATES_REQUEST){
            if(resultCode == RESULT_OK){
//                NumberFormat formatter = new DecimalFormat("#0.00000000");
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude",0);
//                poiLatitude.setText(Double.toString(latitude));
//                poiLongitude.setText(Double.toString(longitude));
                poiLatitude.setText(formatter.format(latitude));
                poiLongitude.setText(formatter.format(longitude));
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
//        Log.d(TAG,"An item was selected");
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding new POI");
        progressDialog.setCancelable(false);
    }

    public void initSubmitButton(){
        Button submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectivityState.getInstance(AddActivity.this.getApplicationContext()).isInternetAvailable()) {
                    EditText fieldEditText[] = {poiName, poiContact, poiOpenHour, poiCloseHour};
                    TextView fieldTextView[] = {poiLatitude, poiLongitude};
                    CheckBox fieldDays[] = {poiMondayOpen, poiTuesdayOpen, poiWednesdayOpen, poiThursdayOpen, poiFridayOpen, poiSaturdayOpen, poiSundayOpen};
//                if(!gotEmptyInputs(fieldEditText) && !gotEmptyInputs(fieldTextView) && oneDaySelected()){
//                    getInputValues(fieldEditText);
//                    getInputValues(fieldTextView);
//                    getDaysValues(fieldDays);
//                    Log.d(TAG, poiGroup.getSelectedItem().toString());
//                    addNewPoi();
//                }

                    if (checkCorrectFormat(fieldEditText) && oneDaySelected() && !gotEmptyInputs(fieldTextView)) {
//                    addNewPoi();
                        Log.d(TAG, "Validation complete");
                        getSimilarPoi();
                    }
                }
                else{
                    Toast.makeText(AddActivity.this.getApplicationContext(),
                            "Internet connectivity required",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void initSelectCoordinatesButton(){
        Button selectCoordinates = (Button)findViewById(R.id.select_coordinates);
        selectCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, SelectActivity.class);
                startActivityForResult(intent, SELECT_COORDINATES_REQUEST);
            }
        });
    }

    public void initUseCurrentCoordinatesButton(){
        Button currentCoordinates = (Button)findViewById(R.id.current_coordinates);
        currentCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UserLocation.getInstance().nullLocation()) {
                    LatLong l = UserLocation.getInstance().getLocation();
//                    poiLatitude.setText(Double.toString(l.latitude));
//                    poiLongitude.setText(Double.toString(l.longitude));
                    double latitude = l.latitude;
                    double longitude = l.longitude;
                    poiLatitude.setText(formatter.format(latitude));
                    poiLongitude.setText(formatter.format(longitude));
                }
                else{
                    Toast.makeText(getApplicationContext(),"Failed to retrieve current location",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean gotEmptyInputs(EditText[] fields){
        for(int i=0; i < fields.length; i++){
            if(checkEmptyValue(fields[i])){
                return true;
            }
        }
        return false;
    }

    public void showAlertDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Oops!")
                .setMessage("There is a similar entry recorded in the database")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                    }
                }).show();
    }

    public boolean gotEmptyInputs(TextView[] fields){
        for(int i=0; i < fields.length; i++){
            if(checkEmptyValue(fields[i])){
                return true;
            }
        }
        return false;
    }

    public boolean oneDaySelected(){
        CheckBox [] dayList = {poiMondayOpen,poiTuesdayOpen,poiWednesdayOpen,
                poiThursdayOpen,poiFridayOpen,poiSaturdayOpen,poiSundayOpen};
        for(int i=0; i<dayList.length;i++){
            if(dayList[i].isChecked()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkEmptyValue(EditText txt){
        if(txt.getText().toString().trim().length() == 0){
            txt.setError("This field is empty");
            txt.requestFocus();
            return true;
        }
        else{
            return false;
        }
    }

    public boolean checkEmptyValue(TextView txt){
        if(txt.getText().toString().length() == 0){
            txt.setError("This field is empty");
            return true;
        }
        else{
            return false;
        }
    }

    public void getInputValues(EditText[] fields){
        for(int i=0; i < fields.length; i++){
                Log.d(TAG,"Value of field is "+ fields[i].getText().toString());
            }
    }

    public String getInputValue(EditText field){
        return field.getText().toString().trim();
    }


    public void getInputValues(TextView[] fields){
        for(int i=0; i < fields.length; i++){
            Log.d(TAG,"Value of field is "+ fields[i].getText().toString());
        }
    }

    public void getDaysValues(CheckBox[] fields){
        for(int i=0; i< fields.length; i++){
            Log.d(TAG, "Day is open: " + ((fields[i].isChecked()) ? 1 : 0));
        }
    }

    public boolean checkCorrectFormat(EditText[] fields){
        String nameRegex = "^[A-Za-z0-9 ]{5,30}";
        String contactRegex = "^\\d{2,3}[-,+]\\d{7,8}$";
        String timeRegex = "^[012]\\d{1}:\\d{2}$";

        for(int i=0; i<fields.length; i++){
            if( i == 0) {
                if (!getInputValue(fields[i]).matches(nameRegex)) {
                    fields[i].setError("Incorrect format\nOnly accepts min 5 and max 25 characters");
                    return false;
                } else {
                    fields[i].setError(null);
                    Log.d(TAG, "Name accepted");
                }
            }
            else if(i == 1) {
                if (!getInputValue(fields[i]).matches(contactRegex)) {
                    fields[i].setError("Incorrect format\nOnly accepts xxx-xxxxxxx");
                    return false;
                } else {
                    fields[i].setError(null);
                    Log.d(TAG, "Contact accepted");
                }
            }
            else if(i == 2 || i == 3) {
                if (!getInputValue(fields[i]).matches(timeRegex)) {
                    fields[i].setError("Incorrect format\nOnly accepts HH:MM");
                    return false;
                } else {
                    fields[i].setError(null);
                    Log.d(TAG, "Time accepted");
                }
            }
        }

        return true;
    }

    public void addNewPoi(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("name", poiName.getText().toString().trim());
        params.put("type", poiGroup.getSelectedItem().toString().trim());
        params.put("contact", poiContact.getText().toString().trim());
        params.put("openTime", poiOpenHour.getText().toString().trim() + ":00");
        params.put("closeTime", poiCloseHour.getText().toString().trim() + ":00");
        params.put("monday", ((poiMondayOpen.isChecked()) ? "1" : "0"));
        params.put("tuesday", ((poiTuesdayOpen.isChecked()) ? "1" : "0"));
        params.put("wednesday", ((poiWednesdayOpen.isChecked()) ? "1" : "0"));
        params.put("thursday", ((poiThursdayOpen.isChecked()) ? "1" : "0"));
        params.put("friday", ((poiFridayOpen.isChecked()) ? "1" : "0"));
        params.put("saturday", ((poiSaturdayOpen.isChecked()) ? "1" : "0"));
        params.put("sunday", ((poiSundayOpen.isChecked()) ? "1" : "0"));
        params.put("latitude", poiLatitude.getText().toString());
        params.put("longitude", poiLongitude.getText().toString());
        float time;
        StopWatch sw = new StopWatch().start();
        client.post("http://g53ids-env.elasticbeanstalk.com/insertNewPoi.php", params, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                String response = new String(responseBody);
                if(response.equals("false")){
                    Toast.makeText(AddActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(AddActivity.this, "New Poi added", Toast.LENGTH_SHORT).show();
                    finish();
                }
//                Log.d(TAG, response);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                failureAction(statusCode);
                progressDialog.dismiss();
            }
        });
        time = sw.stop().getSeconds();
        Log.d(TAG, "Time take for adding poi: "+time);
    }

    public void getSimilarPoi(){
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        final String newPoiName = poiName.getText().toString().trim();
        params.put("type", poiGroup.getSelectedItem().toString().trim());
        params.put("lat", poiLatitude.getText().toString());
        params.put("lon", poiLongitude.getText().toString());

        client.post("http://g53ids-env.elasticbeanstalk.com/retrieveSimilarLocations.php", params, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                String response = new String(responseBody);
                Log.d(TAG, response);
                if(!processSimilarLocations(newPoiName,response)){
                    Log.d(TAG, "Similar poi detected");
                    progressDialog.dismiss();
                    showAlertDialog();
//                    Toast.makeText(getApplicationContext(),"There is a similar entry in the database", Toast.LENGTH_LONG).show();
                }
                else{
                    Log.d(TAG, "New poi will be added");
                    addNewPoi();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                progressDialog.dismiss();
                failureAction(statusCode);
            }
        });
    }

    public void failureAction(int statusCode){
        if (statusCode == 404) {
            Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
        } else if (statusCode == 500) {
            Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet]",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean processSimilarLocations(String newName, String data){
        try{
            JSONArray arr = new JSONArray(data);
            if(arr.length() != 0){
                loop:
                for(int i=0; i<arr.length();i++){
                    JSONObject object = (JSONObject)arr.get(i);
                    String name = object.get("Name").toString();
                    if(isDuplicate(newName,name)){
                        return false;
                    }
                }
                return true;
            }
            else{
                return true;
            }

        }catch(JSONException e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean isDuplicate(String newName, String name){
        double similarityPercentile = compareStrings(newName, name);
        Log.d(TAG, "New: "+newName +", Current: "+ name + " Similarity: "+ similarityPercentile);
        return similarityPercentile > 70;
    }

    private String[] letterPairs(String str) {
        int numPairs = str.length()-1;
        String[] pairs = new String[numPairs];
        for (int i=0; i<numPairs; i++) {
            pairs[i] = str.substring(i,i+2);
        }
        return pairs;
    }

    /** @return an ArrayList of 2-character Strings. */
    private ArrayList wordLetterPairs(String str) {
        ArrayList allPairs = new ArrayList();
        // Tokenize the string and put the tokens/words into an array
        String[] words = str.split("\\s");
        // For each word
        for (int w=0; w < words.length; w++) {
            // Find the pairs of characters
            String[] pairsInWord = letterPairs(words[w]);
            for (int p=0; p < pairsInWord.length; p++) {
                allPairs.add(pairsInWord[p]);
            }
        }
        return allPairs;
    }

    /** @return lexical similarity value in the range [0,1] */
    public double compareStrings(String str1, String str2) {
        ArrayList pairs1 = wordLetterPairs(str1.toUpperCase());
        ArrayList pairs2 = wordLetterPairs(str2.toUpperCase());
        int intersection = 0;
        int union = pairs1.size() + pairs2.size();
        for (int i=0; i<pairs1.size(); i++) {
            Object pair1=pairs1.get(i);
            for(int j=0; j<pairs2.size(); j++) {
                Object pair2=pairs2.get(j);
                if (pair1.equals(pair2)) {
                    intersection++;
                    pairs2.remove(j);
                    break;
                }
            }
        }
        return ((2.0*intersection)/union)*100;
    }
}
