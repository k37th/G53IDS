package com.keith.android.g53ids;

import org.mapsforge.map.layer.overlay.Marker;

/**
 * Created by Keith on 3/10/2015.
 */
public class PoiMarker extends Marker {
    private String poiId;
    private org.mapsforge.core.graphics.Bitmap bitmap;
    private int horizontalOffset;
    private org.mapsforge.core.model.LatLong latLong;
    private int verticalOffset;

    public PoiMarker(org.mapsforge.core.model.LatLong latLong, org.mapsforge.core.graphics.Bitmap bitmap, int horizontalOffset, int verticalOffset, String id) {
        super(latLong,bitmap,horizontalOffset,verticalOffset);
        poiId = id;
    }

    public String getId(){
        return poiId;
    }

}
