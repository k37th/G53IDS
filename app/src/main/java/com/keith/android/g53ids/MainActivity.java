package com.keith.android.g53ids;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.keith.android.g53ids.database.DBHelper;
import com.keith.android.g53ids.gps.GPSTracker;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
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
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;


public class MainActivity extends ActionBarActivity{
//    private PopupMenu popupMenu;
    private static final String MAPFILE = "malaysia_singapore_brunei.map";
    private static final String TAG = "MainActivity";
    static final int SEARCH_REQUEST = 1;
    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;
    private BroadcastReceiver broadcastReceiver = new LocationReceiver();
    private GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(getApplication());
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter("LOCATIONCHANGED");
        this.registerReceiver(broadcastReceiver,filter);
        initGPS();
        initMapView();
//        initActionButton();
        initCenterButton();
        showCurrentLocation(getInitialPosition());
    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    protected void onStop(){
        super.onStop();
//        mapView.getLayerManager().getLayers().remove(1);
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        gps.stopUsingGPS();
        this.unregisterReceiver(this.broadcastReceiver);
        destroyLayers();
        this.tileCache.destroy();
        this.mapView.getModel().mapViewPosition.destroy();
        this.mapView.destroy();
        AndroidResourceBitmap.clearResourceBitmaps();
    }

    private void destroyLayers(){
        for(Layer layer : mapView.getLayerManager().getLayers()){
            mapView.getLayerManager().getLayers().remove(layer);
            layer.onDestroy();
        }
    }

    public void initGPS(){
        gps = new GPSTracker(MainActivity.this);
    }

    public void initMapView(){
        createMap();
        createTileCache();
        createLayer();
    }

    public void initCenterButton(){
        Button center = (Button)(findViewById(R.id.center));
        center.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                mapView.getModel().mapViewPosition.animateTo(getCurrentPosition());
            }
        });
    }

    public LatLong getCurrentPosition(){
        Circle circle = (Circle)mapView.getLayerManager().getLayers().get(1);
        if(circle != null)
            return circle.getPosition();
        else
            return new LatLong(2.943332,101.875841);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SEARCH_REQUEST){
            if(resultCode == RESULT_OK){
                removeAdditionalMarker();
                String id = data.getStringExtra("poiID");
                Toast.makeText(this,id,Toast.LENGTH_SHORT).show();
                POI p = DBHelper.getInstance(this).getPoi(id);
                displayMarker(p.getCoordinates());
            }
        }
    }

    private File getMapFile(){
        return new File(Environment.getExternalStorageDirectory(), MAPFILE);
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

    private LatLong getInitialPosition(){
        if(gps.canGetLocation()){
            return new LatLong(gps.getLatitude(),gps.getLongitude());
        }
        else
            return new LatLong(2.943332,101.875841);
    }

    private void showCurrentLocation(LatLong coordinates){
        Circle circle = new Circle(coordinates, 20, Utils.createPaint(
                AndroidGraphicFactory.INSTANCE.createColor(Color.GREEN), 0,
                Style.FILL), null);
        this.mapView.getModel().mapViewPosition.setCenter(coordinates);
        this.mapView.getLayerManager().getLayers().add(circle);
    }

    private void updateLocation(LatLong coordinates){
        Circle circle = (Circle) mapView.getLayerManager().getLayers().get(1);
        circle.setLatLong(coordinates);
        circle.requestRedraw();
    }

    public class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            Log.d(TAG, "Receive broadcast");
            LatLong coordinates = new LatLong(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude",0));
            Log.d(TAG,"Latitude:"+ coordinates.latitude + " Longitude:"+ coordinates.longitude);
            updateLocation(coordinates);
        }
    }

    public void displayMarker(LatLong coordinates){
        Marker marker = createTappableMarker(this,R.drawable.marker_red, coordinates);
        mapView.getLayerManager().getLayers().add(marker);
        mapView.getModel().mapViewPosition.animateTo(coordinates);
    }

    public Marker createTappableMarker(Context c, int resourceIdentifier,
                                       LatLong latLong) {
        Drawable drawable = c.getResources().getDrawable(resourceIdentifier);
        Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        bitmap.incrementRefCount();
        return new Marker(latLong, bitmap, 0, -bitmap.getHeight() / 2) {
            @Override
            public boolean onTap(LatLong geoPoint, Point viewPosition,
                                 Point tapPoint) {
                if (contains(viewPosition, tapPoint)) {
                    Log.w("Tap", "The Marker was touched with onTap: "
                            + this.getLatLong().toString());
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        };
    }

    public void removeAdditionalMarker(){
        if(mapView.getLayerManager().getLayers().size() == 3){
            Layer extra = mapView.getLayerManager().getLayers().get(2);
            mapView.getLayerManager().getLayers().remove(extra);
            extra.onDestroy();
        }
    }


}
