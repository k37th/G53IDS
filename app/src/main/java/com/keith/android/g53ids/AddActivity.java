package com.keith.android.g53ids;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class AddActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "AddActivity";
    private EditText poiName;
    private EditText poiContact;
    private Spinner poiGroup;
    private EditText poiOpenHour;
    private EditText poiCloseHour;
    private boolean poiMondayOpen;
    private boolean poiTuesdayOpen;
    private boolean poiWednesdayOpen;
    private boolean poiThursdayOpen;
    private boolean poiFridayOpen;
    private boolean poiSaturdayOpen;
    private boolean poiSundayOpen;
    private TextView poiLatitude;
    private TextView poiLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initView();
        initSpinner();
        initSubmitButton();
    }

    public void initView(){
        poiName = (EditText)findViewById(R.id.poi_name);
        poiGroup = (Spinner) findViewById(R.id.spinner);
        poiContact = (EditText)findViewById(R.id.contact);
        poiOpenHour = (EditText)findViewById(R.id.open_time);
        poiCloseHour = (EditText)findViewById(R.id.close_time);
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

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.d(TAG,"An item was selected");
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
                if(!gotEmptyInputs(fieldEditText) && !gotEmptyInputs(fieldTextView)){
                    getInputValues(fieldEditText);
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

    public boolean checkEmptyValue(EditText txt){
        if(txt.getText().toString().length() == 0){
            txt.setError("This field is empty");
            txt.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(txt,InputMethodManager.SHOW_IMPLICIT);
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
            if(i==2){
                Log.d(TAG,"Value of field is "+ Integer.parseInt(fields[i].getText().toString()));
            }
            else if(i == 5 || i == 6){
                Log.d(TAG,"Value of field is "+ Double.parseDouble(fields[i].getText().toString()));
            }
            else{
                Log.d(TAG,"Value of field is "+ fields[i].getText().toString());
            }
        }
    }
}
