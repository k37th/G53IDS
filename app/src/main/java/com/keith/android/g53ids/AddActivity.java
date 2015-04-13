package com.keith.android.g53ids;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

import com.keith.android.g53ids.database.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.mapsforge.core.model.LatLong;

import java.text.DecimalFormat;
import java.text.NumberFormat;


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
    NumberFormat formatter = new DecimalFormat("#0.00000000");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initView();
        initSpinner();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

    public void initSubmitButton(){
        Button submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText fieldEditText[] = {poiName,poiContact,poiOpenHour,poiCloseHour};
                TextView fieldTextView[] = {poiLatitude,poiLongitude};
                CheckBox fieldDays[] = {poiMondayOpen, poiTuesdayOpen, poiWednesdayOpen, poiThursdayOpen, poiFridayOpen, poiSaturdayOpen, poiSundayOpen};
//                if(!gotEmptyInputs(fieldEditText) && !gotEmptyInputs(fieldTextView) && oneDaySelected()){
//                    getInputValues(fieldEditText);
//                    getInputValues(fieldTextView);
//                    getDaysValues(fieldDays);
//                    Log.d(TAG, poiGroup.getSelectedItem().toString());
//                    addNewPoi();
//                }

                if(checkCorrectFormat(fieldEditText) && oneDaySelected() && !gotEmptyInputs(fieldTextView)){
                    addNewPoi();
                    Log.d(TAG, "Validation complete");
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
                    fields[i].setError("Incorrect format");
                    return false;
                } else {
                    fields[i].setError(null);
                    Log.d(TAG, "Name accepted");
                }
            }
            else if(i == 1) {
                if (!getInputValue(fields[i]).matches(contactRegex)) {
                    fields[i].setError("Incorrect format");
                    return false;
                } else {
                    fields[i].setError(null);
                    Log.d(TAG, "Contact accepted");
                }
            }
            else if(i == 2 || i == 3) {
                if (!getInputValue(fields[i]).matches(timeRegex)) {
                    fields[i].setError("Incorrect format");
                    return false;
                } else {
                    fields[i].setError(null);
                    Log.d(TAG, "Time accepted");
                }
            }
        }

        return true;
    }

    public double getSimilarityPercentile(String newPoi, String existingPoi){
        char[] arr1 = newPoi.toCharArray();
        char[] arr2 = existingPoi.toCharArray();
        int count = 0;
        for(int i=0; i<arr1.length && i<arr2.length; i++){
            if(arr1[i] == arr2[i]){
                count++;
            }
        }
        if(arr1.length > arr2.length){
            return (count/arr1.length) * 100;
        }
        else{
            return (count/arr2.length) * 100;
        }
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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
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
            Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                    Toast.LENGTH_LONG).show();
        }
    }
}
