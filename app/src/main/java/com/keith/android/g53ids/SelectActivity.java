package com.keith.android.g53ids;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.keith.android.g53ids.database.DBHelper;
import com.keith.android.g53ids.gps.GPSTracker;

import org.mapsforge.core.graphics.Bitmap;
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
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;

public class SelectActivity extends ActionBarActivity{

    private static final String MAPFILE = "malaysia_singapore_brunei.map";
    private static final String TAG = "MainActivity";
    static final int SEARCH_REQUEST = 1;
    private ProgressDialog progressDialog;
    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;
    private GPSTracker gps;

    //Activity Life cycle - Start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(getApplication());
        setContentView(R.layout.activity_main);
        initMapView();
//        showCurrentLocation(getInitialPosition());
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
    public void onDestroy(){
        super.onDestroy();
        destroyLayers();
        this.tileCache.destroy();
        this.mapView.getModel().mapViewPosition.destroy();
        this.mapView.destroy();
        AndroidResourceBitmap.clearResourceBitmaps();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            if(mapView.getLayerManager().getLayers().size()>1) {
                Intent intent = new Intent();
                LatLong coordinates = mapView.getLayerManager().getLayers().get(1).getPosition();
                intent.putExtra("latitude", coordinates.latitude);
                intent.putExtra("longitude", coordinates.longitude);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            }
            else{
                Toast.makeText(this, "No coordinates selected", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    //Activity Lifecycle - End

    //Map functions - Start
    private void destroyLayers(){
        for(Layer layer : mapView.getLayerManager().getLayers()){
            mapView.getLayerManager().getLayers().remove(layer);
            layer.onDestroy();
        }
    }

    public void initMapView(){
        createMap();
        createTileCache();
        createLayer();
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
        this.mapView.getModel().mapViewPosition.setCenter(new LatLong(2.943332,101.875841));
    }

    private void createLayer(){
        this.tileRendererLayer = new TileRendererLayer(tileCache,
                this.mapView.getModel().mapViewPosition,
                false, true, AndroidGraphicFactory.INSTANCE){
            @Override
            public boolean onLongPress(LatLong tapLatLong, Point layerXY, Point tapXY){
                return onMapTap(tapLatLong, layerXY, tapXY);
            }
        };
        tileRendererLayer.setMapFile(getMapFile());
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);
    }

    private void createTileCache(){
        this.tileCache = AndroidUtil.createTileCache(getApplicationContext(),
                "mapcache", mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());
    }

    private boolean onMapTap(LatLong tapLatLong, Point layerXY, Point tapXY){
        Layers layers = mapView.getLayerManager().getLayers();
        if(layers.size()>1){
            layers.remove(1);
        }
        Marker marker = createMarker(tapLatLong, R.drawable.marker_red);
        layers.add(marker);
        return true;
    }

    private Marker createMarker( LatLong p, int resource )
    {
        Drawable drawable = getResources().getDrawable(resource);
        Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        return new Marker(p, bitmap, -bitmap.getHeight(), -bitmap.getWidth() / 2);
    }
}
