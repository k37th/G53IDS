package com.keith.android.g53ids;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keith.android.g53ids.database.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapsforge.core.model.LatLong;

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
                    return TagsFragment.newInstance(position + 1, poi_id);
                case 2:
                    return CommentFragment.newInstance(position + 1, poi_id);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Information";
                case 1:
                    return "Tags";
                case 2:
                    return "Comments";

            }
            return null;
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

    /**
     *Fragment for details
     */
    public static class TagsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_POI_ID = "poi_id";
        private static final String TAG = "TagFragment";
        private String poi_id;
        private TagAdapter adapter;
        private ArrayList<Tag> mTags;
        private EditText tagName;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static TagsFragment newInstance(int sectionNumber, String id) {
            TagsFragment fragment = new TagsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_POI_ID, id);
            fragment.setArguments(args);
            return fragment;
        }

        public TagsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstance){
            super.onCreate(savedInstance);
            Bundle bundle = getArguments();
            poi_id = bundle.getString(ARG_POI_ID);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mTags = DBHelper.getInstance(getActivity()).getTags(poi_id);
            for( Tag x : mTags){
                Log.d(TAG, x.displayTag());
            }
            View rootView = inflater.inflate(R.layout.fragment_tags, container, false);
//            LinearLayout tagBoard = (LinearLayout)rootView.findViewById(R.id.tag_board);
//            LinearLayout templateRow = (LinearLayout)inflater.inflate(R.layout.btn_row_template, null);
//            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);

//            ArrayList<LinearLayout> rows = new ArrayList<>();
//            int j=0;
//            for(int i=0; i<mTags.size(); i++){
//            for(int i=0; i<5; i++){
//                if(j==0){
//                    rows.add((LinearLayout)inflater.inflate(R.layout.btn_row_template,null));
//                    j++;
//                }
//                else if(i>0){
//                    if( (i-1) % 3 == 0){
//                        rows.add((LinearLayout)inflater.inflate(R.layout.btn_row_template,null));
//                        j++;
//                    }
//                }
//                Button btn = new Button(getActivity());
//                btn.setLayoutParams(param);
//                btn.setText(mTags.get(i).getName());
//                btn.setText("Button "+ i);
//                rows.get(j-1).addView(btn);
//            }

//            for (LinearLayout row : rows){
//                tagBoard.addView(row);
//            }

            GridView tagView = (GridView)rootView.findViewById(R.id.tagGrid);
            adapter = new TagAdapter(mTags);
            tagView.setAdapter(adapter);

            tagName = (EditText)rootView.findViewById(R.id.tag_text);
            Button submit = (Button)rootView.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newTag = tagName.getText().toString();
                    Log.d(TAG, newTag);
                    postSubmitBehaviour(tagName);
                    postTag(newTag);
                }
            });
            return rootView;
        }

        public class TagAdapter extends ArrayAdapter<Tag>{
            public TagAdapter(ArrayList<Tag> tags){
                super(getActivity(),0,tags);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                if(convertView == null){
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.button_template, null);
                }

                Tag tag = getItem(position);
                Button btn = (Button)convertView.findViewById(R.id.tag_button);
                btn.setTag(tag.getId());
                btn.setText(tag.getName());
                btn.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Button tagBtn = (Button) v.findViewById(R.id.tag_button);
                        final String btnId = (String) tagBtn.getTag();
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                "The button id is: "+btnId, Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//                        dialog.setTitle("Confirm delete Tag");
                        dialog.setMessage("Are you sure you want to delete this tag?");
                        dialog.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(TAG,"Yes clicked");
                                        deleteTag(btnId);
                                        dialog.dismiss();
                                    }
                                });
                        dialog.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(TAG,"No clicked");
                                        dialog.dismiss();
                                    }
                                });
                        dialog.show();
                        return true;
                    }
                });
                return convertView;
            }
        }

        public void postSubmitBehaviour(EditText view){
            view.setText("");
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(tagName.getWindowToken(), 0);
        }


        public void postTag(String newTag){
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("poi", poi_id);
            params.put("tag", newTag);
            client.post("http://g53ids-env.elasticbeanstalk.com/insertNewTag.php", params, new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    String response = new String(responseBody);
                    if(response.equals("true")) {
                        Toast.makeText(getActivity(), "Tag added!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getActivity(), "Tag add failed!", Toast.LENGTH_LONG).show();
                    }
                    Log.d(TAG, "Tag added successfully");

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    failureAction(statusCode);
                    Log.d(TAG, "Failed to add tag");
                }
            });
        }

        public void deleteTag(String tagId){
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("id", tagId);
            client.post("http://g53ids-env.elasticbeanstalk.com/deleteTag.php", params, new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    String response = new String(responseBody);
                    if(response.equals("true")) {
                        Toast.makeText(getActivity(), "Tag deleted!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getActivity(), "Failed to delete!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    failureAction(statusCode);
                    Log.d(TAG, "Failed to delete tag");
                }
            });
        }

        public void failureAction(int statusCode){
            if (statusCode == 404) {
                Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
            } else if (statusCode == 500) {
                Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet]",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CommentFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String TAG = "CommentFragment";
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_POI_ID = "poi_id";
        private String poi_id;
        private ArrayList<Comment> mComments = new ArrayList<>();
        private ProgressDialog syncDialog;
        private SwipeRefreshLayout swipeView;
        private CommentAdapter adapter;
        private EditText commentText;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static CommentFragment newInstance(int sectionNumber, String id) {
            CommentFragment fragment = new CommentFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_POI_ID, id);
            fragment.setArguments(args);
            return fragment;
        }

        public CommentFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstance){
            super.onCreate(savedInstance);
            Log.d(TAG, "Create is called");
            initSyncDialog();
//            mComments = new ArrayList<>();
            Bundle bundle = getArguments();
            poi_id = bundle.getString(ARG_POI_ID);
//            retrieveComments(bundle.getString(ARG_POI_ID));
            adapter = new CommentAdapter(mComments);
            setListAdapter(adapter);
        }

        @Override
        public void onStart(){
            super.onStart();
            Log.d(TAG, "Start is called");
        }

        @Override
        public void onResume(){
            super.onResume();
            Log.d(TAG, "Resume is called");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_comment, container, false);
            commentText = (EditText)rootView.findViewById(R.id.comment_text);
            swipeView = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe);
            swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
//                    swipeView.setRefreshing(true);
//                    Toast.makeText(getActivity(),"Refresh started",Toast.LENGTH_SHORT).show();
//                    (new Handler()).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            swipeView.setRefreshing(false);
//                            Toast.makeText(getActivity(),"Refresh completed",Toast.LENGTH_SHORT).show();
//                        }
//                    }, 3000);
                    retrieveComments(poi_id);
                }
            });
            Button submit = (Button)rootView.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = commentText.getText().toString();
                    Log.d(TAG, text);
                    postSubmitBehaviour(commentText);
                    postComment(text);
                }
            });
            if(mComments.isEmpty()) {
                retrieveComments(poi_id);
            }
            return rootView;
        }

        public void initSyncDialog(){
            syncDialog = new ProgressDialog(getActivity());
            syncDialog.setMessage("Retrieving Comments");
            syncDialog.setCancelable(false);
        }

        private class CommentAdapter extends ArrayAdapter<Comment> {
            public CommentAdapter(ArrayList<Comment> comments){
                super(getActivity(),0,comments);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                if(convertView == null){
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_comment, null);
                }

                Comment comment = getItem(position);
                TextView date = (TextView)convertView.findViewById(R.id.comment_list_date);
                TextView text = (TextView)convertView.findViewById(R.id.comment_list_text);
                date.setText(comment.getdate());
                text.setText(comment.getText());
                return convertView;
            }
        }

        public void postSubmitBehaviour(EditText view){
            view.setText("");
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
        }

        private void retrieveComments(String id){
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("id",id);
//            syncDialog.show();
            swipeView.setRefreshing(true);
            client.post("http://g53ids-env.elasticbeanstalk.com/retrieveAllComments.php", params, new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    clearComments();
                    processComments(new String(responseBody));
                    Log.d(TAG, "Success in retrieving comments");
                    adapter.notifyDataSetChanged();
//                    syncDialog.dismiss();
                    swipeView.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    failureAction(statusCode);
                    Log.d(TAG, "Failure in retrieving comments");
//                    syncDialog.dismiss();
                    swipeView.setRefreshing(false);
                }
            });

        }

        public void failureAction(int statusCode){
            if (statusCode == 404) {
                Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
            } else if (statusCode == 500) {
                Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet]",
                        Toast.LENGTH_LONG).show();
            }
        }

        public void processComments(String responseBody){
            try{
                JSONArray arr = new JSONArray(responseBody);
                if(arr.length() != 0){
                    for(int i=0; i<arr.length();i++){
                        JSONObject object = (JSONObject)arr.get(i);
                        Comment c = new Comment(object.get("Date").toString(), object.get("Text").toString());
                        mComments.add(c);
                    }
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        private void clearComments(){
            mComments.clear();
        }

        public void postComment(String text){
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("id", poi_id);
            params.put("text", text);
            client.post("http://g53ids-env.elasticbeanstalk.com/insertNewComment.php", params, new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    String response = new String(responseBody);
                    if(response.equals("true")) {
                        Toast.makeText(getActivity(), "Comment posted!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getActivity(), "Comment not posted!", Toast.LENGTH_LONG).show();
                    }
                    Log.d(TAG, "Comment posted successfully");

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    failureAction(statusCode);
                    Log.d(TAG, "Failed to post comment");
                }
            });
        }
    }
}
