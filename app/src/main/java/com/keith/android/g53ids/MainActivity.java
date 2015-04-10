package com.keith.android.g53ids;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.support.v7.widget.PopupMenu;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;
import com.keith.android.g53ids.database.DBHelper;
import com.keith.android.g53ids.gps.FallBackLocationTracker;
import com.keith.android.g53ids.gps.GPSTracker;
//import com.keith.android.g53ids.gps.LocationReceiver;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.graphics.AndroidResourceBitmap;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Circle;
import org.mapsforge.map.layer.overlay.FixedPixelCircle;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;


public class MainActivity extends ActionBarActivity{
    public final static String POI_ID = "poiId";
    private static final String MAPFILE = "malaysia_singapore_brunei.map";
    private static final String TAG = "MainActivity";

    static final int SEARCH_REQUEST = 1;
    static final int ROUTE_REQUEST = 2;
    private ProgressDialog progressDialog;
    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;
    private GraphHopper hopper;
    private File mapsFolder = new File(Environment.getExternalStorageDirectory(),"malaysia_singapore_brunei-gh");
    private BroadcastReceiver broadcastReceiver = new LocationReceiver();
//    private GPSTracker gps;
    private FallBackLocationTracker tracker;

    private BroadcastReceiver listenerReceiver;
    private AlarmManager am;
    private PendingIntent pi;
    //Activity Life cycle - Start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(getApplication());
        setContentView(R.layout.activity_main);
//        initBroadcastReceiver();
        initGPS();
        initMapView();
        initCenterButton();
        initProgressDialog();
        showCurrentLocation(getInitialPosition());
        loadGraphStorage();
    }

    @Override
    protected void onStart(){
        super.onStart();
        initBroadcastReceiver();
        initListenerReceiver();
//        showCurrentLocation(getInitialPosition());
    }

    @Override
    protected void onStop(){
        super.onStop();
        deInitBroadcastReceiver();
        deInitListenerReceiver();
        AndroidResourceBitmap.clearResourceBitmaps();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        tracker.stop();
//        gps.stopUsingGPS();
//        deInitBroadcastReceiver();
        destroyLayers();
        this.tileCache.destroy();
        this.mapView.getModel().mapViewPosition.destroy();
        this.mapView.destroy();
//        AndroidResourceBitmap.clearResourceBitmaps();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Toast.makeText(this, DBHelper.getInstance(this).getLastSyncDate(),Toast.LENGTH_LONG).show();
            return true;
        }
        else if(id == R.id.action_sync){
            syncRemoteDatabase();
            return true;
        }
        else if(id == R.id.action_about){
            return true;
        }
        else if(id == R.id.search_action){
            Intent intent = new Intent(this,SearchActivity.class);
            startActivityForResult(intent, SEARCH_REQUEST);
            return true;
        }
        else if(id == R.id.add_action){
            Intent intent = new Intent(this,AddActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Activity Lifecycle - End

    //Map functions - Start
    private void destroyLayers(){
        for(Layer layer : mapView.getLayerManager().getLayers()){
            mapView.getLayerManager().getLayers().remove(layer);
            layer.onDestroy();
        }
    }

    public void initGPS(){
//        gps = new GPSTracker(MainActivity.this);
        tracker = new FallBackLocationTracker(MainActivity.this);
        tracker.start();
    }

    public void initMapView(){
        createMap();
        createTileCache();
        createLayer();
    }

    private File getMapFile(){
//        return new File(Environment.getExternalStorageDirectory(), "/malaysia_singapore_brunei-gh/"+ MAPFILE);
        return new File(mapsFolder, MAPFILE );
    }

    private void createMap(){
        mapView = (MapView)findViewById(R.id.mapView);
        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(false);
//        this.mapView.getModel().mapViewPosition.setMapLimit(new BoundingBox(2.9074, 101.8045, 2.978, 101.9149));
        this.mapView.getModel().mapViewPosition.setMapLimit(new BoundingBox(2.711, 100.974, 3.707, 102.431));
        this.mapView.getModel().mapViewPosition.setZoomLevel((byte) 16);
    }

    private void createLayer(){
        this.tileRendererLayer = new TileRendererLayer(tileCache,
                this.mapView.getModel().mapViewPosition,
                false, true, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setMapFile(getMapFile());
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);
    }

    private void createTileCache(){
        this.tileCache = AndroidUtil.createTileCache(getApplicationContext(),
                "mapcache", mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());
    }

    void loadGraphStorage(){

        logUser("loading graph");
        new GHAsyncTask<Void, Void, Path>()
        {
            protected Path saveDoInBackground( Void... v ) throws Exception
            {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                tmpHopp.setCHShortcuts("fastest");
//                tmpHopp.load(new File(mapsFolder, "malaysia_singapore_brunei").getAbsolutePath());
                tmpHopp.load(new File(Environment.getExternalStorageDirectory(), "/malaysia_singapore_brunei/").getAbsolutePath());
                Log.d(TAG,"found graph " + tmpHopp.getGraph().toString() + ", nodes:" + tmpHopp.getGraph().getNodes());
                hopper = tmpHopp;
                return null;
            }

            protected void onPostExecute( Path o )
            {
                if (hasError())
                {
                    logUser("An error happened while creating graph:"
                            + getErrorMessage());
                    Log.d(TAG,"An error happened while creating graph:"
                            + getErrorMessage());
                } else
                {
                    logUser("Finished loading graph.");
                }

            }
        }.execute();
    }
//    final double fromLat, final double fromLon
    public void calcPath( final LatLong fromLoc,
                          final double toLat, final double toLon )
    {

//        log("calculating path ...");
        new AsyncTask<Void, Void, GHResponse>()
        {
            float time;

            protected GHResponse doInBackground( Void... v )
            {
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLoc.latitude, fromLoc.longitude, toLat, toLon).
                        setAlgorithm("dijkstrabi").
                        putHint("instructions", false).
                        putHint("douglas.minprecision", 1);
                GHResponse resp = hopper.route(req);
                time = sw.stop().getSeconds();
                return resp;
            }

            protected void onPostExecute( GHResponse resp )
            {
                if (!resp.hasErrors())
                {
//                    log("from:" + fromLat + "," + fromLon + " to:" + toLat + ","
//                            + toLon + " found path with distance:" + resp.getDistance()
//                            / 1000f + ", nodes:" + resp.getPoints().getSize() + ", time:"
//                            + time + " " + resp.getDebugInfo());
//                    logUser("the route is " + (int) (resp.getDistance() / 100) / 10f
//                            + "km long, time:" + resp.getMillis() / 60000f + "min, debug:" + time);

                    mapView.getLayerManager().getLayers().add(createPolyline(resp));
                    Log.d(TAG, "Size of layers" + mapView.getLayerManager().getLayers().size());
                    //mapView.redraw();
                } else
                {
                    logUser("Error:" + resp.getErrors());
                }
//                shortestPathRunning = false;
            }
        }.execute();
    }

    private Polyline createPolyline( GHResponse response )
    {
        Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
        paintStroke.setStyle(Style.STROKE);
        paintStroke.setColor(Color.BLUE);
        paintStroke.setDashPathEffect(new float[]
                {
                        25, 15
                });
        paintStroke.setStrokeWidth(8);

        // TODO: new mapsforge version wants an mapsforge-paint, not an android paint.
        // This doesn't seem to support transparceny
        //paintStroke.setAlpha(128);
        Polyline line = new Polyline((org.mapsforge.core.graphics.Paint) paintStroke, AndroidGraphicFactory.INSTANCE);
        List<LatLong> geoPoints = line.getLatLongs();
        PointList tmp = response.getPoints();
        for (int i = 0; i < response.getPoints().getSize(); i++)
        {
            geoPoints.add(new LatLong(tmp.getLatitude(i), tmp.getLongitude(i)));
        }

        return line;
    }

    private void logUser( String str ){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    private LatLong getInitialPosition(){
//        if(gps.canGetLocation()){
//            return new LatLong(gps.getLatitude(),gps.getLongitude());
//        }
        if(tracker.hasLocation() && tracker.getLocation() != null){
            Location currentLocation = tracker.getLocation();
            return new LatLong(currentLocation.getLatitude(),currentLocation.getLongitude());
        }
        else if(tracker.hasPossiblyStaleLocation() && tracker.getPossiblyStaleLocation()!= null){
            Location currentLocation = tracker.getPossiblyStaleLocation();
            return new LatLong(currentLocation.getLatitude(),currentLocation.getLongitude());
        }
        else
            return new LatLong(2.943332,101.875841);
    }

//    private void showCurrentLocation(LatLong coordinates){
//        Circle circle = new Circle(coordinates, 20, Utils.createPaint(
//                AndroidGraphicFactory.INSTANCE.createColor(Color.GREEN), 0,
//                Style.FILL), null);
//        this.mapView.getModel().mapViewPosition.setCenter(coordinates);
//        this.mapView.getLayerManager().getLayers().add(circle);
//    }

    private void showCurrentLocation(LatLong coordinates){
        FixedPixelCircle circle = new FixedPixelCircle(coordinates, 18, Utils.createPaint(
                AndroidGraphicFactory.INSTANCE.createColor(Color.GREEN), 0,
                Style.FILL), null);
        this.mapView.getModel().mapViewPosition.setCenter(coordinates);
        this.mapView.getLayerManager().getLayers().add(circle);
    }

    private void updateLocation(LatLong coordinates){
        FixedPixelCircle circle = (FixedPixelCircle) mapView.getLayerManager().getLayers().get(1);
        circle.setLatLong(coordinates);
        circle.requestRedraw();
    }
//    private void updateLocation(LatLong coordinates){
//        Circle circle = (Circle) mapView.getLayerManager().getLayers().get(1);
//        circle.setLatLong(coordinates);
//        circle.requestRedraw();
//    }

    public void displayMarker(String id,LatLong coordinates){
        PoiMarker marker = createTappableMarker(this,R.drawable.marker_red, coordinates, id);
        mapView.getLayerManager().getLayers().add(marker);
        mapView.getModel().mapViewPosition.animateTo(coordinates);
    }

    public PoiMarker createTappableMarker(Context c, int resourceIdentifier,
                                       LatLong latLong, String id) {
        Drawable drawable = c.getResources().getDrawable(resourceIdentifier);
        Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        bitmap.incrementRefCount();
//        return new Marker(latLong, bitmap, 0, -bitmap.getHeight() / 2) {
        return new PoiMarker(latLong, bitmap, 0, -bitmap.getHeight() /2, id) {
            @Override
            public boolean onTap(LatLong geoPoint, Point viewPosition,
                                 Point tapPoint) {
                if (contains(viewPosition, tapPoint)) {
                    Log.w("Tap", "The Marker was touched with onTap: "
                            + this.getLatLong().toString());
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra(POI_ID, this.getId());
                    startActivityForResult(intent, ROUTE_REQUEST);
                    return true;
                }
                return false;
            }
        };
    }

    public void removeAdditionalLayers(){
        while(mapView.getLayerManager().getLayers().size() > 2){
            Layer extra = mapView.getLayerManager().getLayers().get(2);
            mapView.getLayerManager().getLayers().remove(extra);
            extra.onDestroy();
        }
    }

    //Map functions - End

    public void initCenterButton(){
        ImageButton center = (ImageButton)(findViewById(R.id.center));
        center.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                mapView.getModel().mapViewPosition.animateTo(getCurrentPosition());
            }
        });
    }

    public void initListenerReceiver(){
        am = (AlarmManager)(this.getSystemService(Context.ALARM_SERVICE));
        IntentFilter filter = new IntentFilter("LOCATIONLISTENER");
        listenerReceiver = new ListenerReceiver();
        registerReceiver(listenerReceiver,filter);
        pi = PendingIntent.getBroadcast(this,0,new Intent("LOCATIONLISTENER"),0);
        startAlarm();
    }

    public void startAlarm(){
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30000,pi);
    }

    public void deInitListenerReceiver(){
        am.cancel(pi);
        unregisterReceiver(this.listenerReceiver);
    }

    public class ListenerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context c, Intent i){
            Log.d(TAG,"Received broadcast from listenerReceiver");
        }
    }

    public void initBroadcastReceiver(){
        IntentFilter filter = new IntentFilter("LOCATIONCHANGED");
        this.registerReceiver(broadcastReceiver,filter);
    }

    public void deInitBroadcastReceiver(){
        this.unregisterReceiver(this.broadcastReceiver);
    }

//    public LatLong getCurrentPosition(){
//        Circle circle = (Circle)mapView.getLayerManager().getLayers().get(1);
//        if(circle != null)
//            return circle.getPosition();
//        else
//            return new LatLong(2.943332,101.875841);
//    }

    public LatLong getCurrentPosition(){
        FixedPixelCircle circle = (FixedPixelCircle)mapView.getLayerManager().getLayers().get(1);
        if(circle != null)
            return circle.getPosition();
        else
            return new LatLong(2.943332,101.875841);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SEARCH_REQUEST){
            if(resultCode == RESULT_OK){
                removeAdditionalLayers();
                String id = data.getStringExtra("poiID");
//                Toast.makeText(this,id,Toast.LENGTH_SHORT).show();
                POI p = DBHelper.getInstance(this).getPoi(id);
                displayMarker(p.getId(),p.getCoordinates());
            }
        }
        else if(requestCode == ROUTE_REQUEST){
            if(resultCode == RESULT_OK){
                String id = data.getStringExtra("poiID");
                Toast.makeText(this,id,Toast.LENGTH_SHORT).show();
                calcPath(getCurrentPosition(),2.945219,101.874778);
            }
        }
    }

    public class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            Log.d(TAG, "Receive broadcast");
            LatLong coordinates = new LatLong(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude",0));
            Log.d(TAG,"Latitude:"+ coordinates.latitude + " Longitude:"+ coordinates.longitude);
            updateLocation(coordinates);
            UserLocation.getInstance().setLocation(coordinates);
        }
    }

    public void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Syncing with online database");
        progressDialog.setCancelable(false);
    }

    public void syncRemoteDatabase(){
        AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams params = new RequestParams();
        String date = DBHelper.getInstance(this).getLastSyncDate();
        if( date == null){
            Log.d(TAG, "Date is null");
        }
        else {
            Log.d(TAG, date);
        }
        params.put("date",date);
        progressDialog.show();
        client.post("http://g53ids-env.elasticbeanstalk.com/syncDatabase.php", params, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
//                Log.d(TAG,new String(responseBody));
                saveNewData(new String(responseBody));
                getSyncDatetime();
                syncTagTable(params);
//                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                progressDialog.dismiss();
                failureAction(statusCode);
                Log.d(TAG, "Failure in getting data");
            }
        });
    }

    public void saveNewData(String responseBody){
        try{
            JSONArray arr = new JSONArray(responseBody);
            if(arr.length() != 0){
                for(int i=0; i<arr.length();i++){
                    JSONObject object = (JSONObject)arr.get(i);
                    POI p = new POI(
                            object.get("Id").toString(),
                            object.get("Name").toString(),
                            object.get("Type").toString(),
                            Double.parseDouble(object.get("Rating").toString()),
                            object.get("Contact").toString(),
                            object.get("OpenTime").toString(),
                            object.get("CloseTime").toString(),
                            Integer.parseInt(object.get("Monday").toString()),
                            Integer.parseInt(object.get("Tuesday").toString()),
                            Integer.parseInt(object.get("Wednesday").toString()),
                            Integer.parseInt(object.get("Thursday").toString()),
                            Integer.parseInt(object.get("Friday").toString()),
                            Integer.parseInt(object.get("Saturday").toString()),
                            Integer.parseInt(object.get("Sunday").toString()),
                            Integer.parseInt(object.get("Status").toString()),
                            new LatLong(
                                    Double.parseDouble(object.get("Latitude").toString()),
                                    Double.parseDouble(object.get("Longitude").toString())
                                    )
                            );
                    DBHelper.getInstance(this).insertPOI(p);

                }
                Toast.makeText(this,"Poi table updated", Toast.LENGTH_LONG).show();
            }

        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void getSyncDatetime(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.post("http://g53ids-env.elasticbeanstalk.com/getDatetime.php", params, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
               saveSyncDatetime(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                failureAction(statusCode);
                Log.d(TAG, "Failure in getting time");
            }
        });
    }

    public void saveSyncDatetime(String syncDatetime){
        DBHelper.getInstance(this).updateSyncDatetime(syncDatetime);
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

    public void syncTagTable(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//        params.put("date",date);
        client.post("http://g53ids-env.elasticbeanstalk.com/syncTagDatabase.php", params, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                Log.d(TAG, new String(responseBody));
                updateTagTable(new String(responseBody));
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                failureAction(statusCode);
                Log.d(TAG, "Failure in syncing tag");
                progressDialog.dismiss();
            }
        });
    }

    public void updateTagTable(String responseBody){
        try{
            JSONArray arr = new JSONArray(responseBody);
            if(arr.length() != 0){
                for(int i=0; i<arr.length();i++){
                    JSONObject object = (JSONObject)arr.get(i);
                    Tag t = new Tag(
                            object.get("Id").toString(),
                            object.get("Name").toString(),
                            object.get("Poi").toString(),
                            Integer.parseInt(object.get("Flag").toString())
                    );
                    DBHelper.getInstance(this).insertTag(t);
                }
            Toast.makeText(this,"Tag table updated", Toast.LENGTH_LONG).show();
            }

        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}
