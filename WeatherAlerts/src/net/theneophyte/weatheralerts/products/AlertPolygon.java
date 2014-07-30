package net.theneophyte.weatheralerts.products;

import java.util.ArrayList;
import java.util.List;

public class AlertPolygon {
	
	private final int MIN_VERTS = 3;

	private final ArrayList<LatLng> mVertices;
	private final LatLngBox mBounds;
	
	public AlertPolygon(List<LatLng> points){
		if (points.size() < MIN_VERTS){
			throw new IllegalArgumentException("An AlertPolygon must have at least 3 vertices.");
		}
		
		mVertices = new ArrayList<LatLng>(points);
		mBounds = LatLngBox.maxBounds(points);
	}
	
	public AlertPolygon(String points){
		this(parsePoints(points));
	}
	
	protected LatLngBox getBoundingBox(){
		return mBounds;
	}
	
	private static List<LatLng> parsePoints(String points){
		ArrayList<LatLng> verts = new ArrayList<LatLng>();
		
		final String[] latlngs = points.split("\\s");
		for (String latlng : latlngs){
			verts.add(new LatLng(latlng));
		}
		
		return verts;
	}
}
