package net.theneophyte.weatheralerts.products;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds the polygon derived from some weather alert types (mainly severe warnings).
 * @author Matt Sutter
 *
 */
public class AlertPolygon {
	
	private final int MIN_VERTS = 3;

	private final ArrayList<LatLng> mVertices;
	private final LatLngBox mBounds;
	
	/**
	 * Builds a polygon from a list of points.
	 * @param points
	 */
	private AlertPolygon(List<LatLng> points){
		if (points.size() < MIN_VERTS){
			throw new IllegalArgumentException("An AlertPolygon must have at least 3 vertices.");
		}
		
		mVertices = new ArrayList<LatLng>(points);
		mBounds = LatLngBox.maxBounds(points);
	}
	
	/**
	 * Builds a polygon from a string containing lat/long pairs
	 * @param points
	 */
	public AlertPolygon(String points){
		this(parsePoints(points));
	}
	
	protected LatLngBox getBoundingBox(){
		return mBounds;
	}
	
	/**
	 * Parses lat/long pairs out of a string.
	 * @param points
	 * @return
	 */
	private static List<LatLng> parsePoints(String points){
		ArrayList<LatLng> verts = new ArrayList<LatLng>();
		
		final String[] latlngs = points.split("\\s");
		for (String latlng : latlngs){
			verts.add(new LatLng(latlng));
		}
		
		return verts;
	}
}
