package net.theneophyte.weatheralerts.products;

import java.util.ArrayList;
import java.util.List;

import net.theneophyte.weatheralerts.R;
import android.content.Context;
import android.database.Cursor;

// TODO Documentation
public class CountyQuery extends DatabaseQuery{
	
	private static final int RES_ID = R.raw.counties;
	
	private static final String SITE_TABLE = "counties";
	
	private static final String 
			STATE = "state",
			CWA = "cwa",
			COUNTY_NAME = "county_name",
			FIPS = "fips",
			ADJ = "adjacent",
			CENT_LAT = "cent_lat",
			CENT_LONG = "cent_long",
			MAX_LAT = "lat_max",
			MIN_LAT = "lat_min",
			MAX_LONG = "long_max",
			MIN_LONG = "long_min";
	
	public CountyQuery(Context context) {
		super(context, SITE_TABLE, RES_ID);
	}
	
	public int[] countiesNearPoint(LatLng point){
		final int[] boundingCounties = boundingCounties(point);
		
		final ArrayList<Integer> adjacent = new ArrayList<Integer>();
		
		for (int i = 0; i < boundingCounties.length; i++){
			final int[] adj = queryAdjacent(boundingCounties[i]);
			for (int county : adj){
				if (!adjacent.contains(county)){
					adjacent.add(county);
				}
			}
		}
		
		final Double[] distances = new Double[adjacent.size()];
		
		for (int i = 0; i < adjacent.size(); i++){
			distances[i] = distanceToCentroid(adjacent.get(i), point);
		}
		
		for (int swapIndex = 0; swapIndex < adjacent.size() - 1; swapIndex++){
			for (int checkIndex = swapIndex + 1; checkIndex < adjacent.size(); checkIndex++){
				if (distances[checkIndex] < distances[swapIndex]){
					swap(adjacent, swapIndex, checkIndex);
					swap(distances, swapIndex, checkIndex);
				}
			}
		}
	
		final int[] counties = new int[adjacent.size()];
		
		for (int i = 0; i < counties.length; i++){
			counties[i] = adjacent.get(i);
		}
		
		return counties;
	}
	
	private void swap(List<Integer> list, int indexA, int indexB){
		final int buffer = list.get(indexA);
		list.set(indexA, list.get(indexB));
		list.set(indexB, buffer);
	}
	
	private void swap(Object[] array, int indexA, int indexB){
		final Object buffer = array[indexA];
		array[indexA] = array[indexB];
		array[indexB] = buffer;
	}
	
	private int[] boundingCounties(LatLng point){
		final String[] columns = {FIPS};
		final String where = "where " + MAX_LONG + " >= ? and " + MIN_LONG + " <= ? and " + MAX_LAT + " >= ?  and " + MIN_LAT + " <= ?";
		final String[] selArgs = {
				Double.toString(point.longitude),
				Double.toString(point.longitude), 
				Double.toString(point.latitude), 
				Double.toString(point.latitude)
				};
		
		final Cursor nearby = query(false, columns, where, selArgs);
		final int count = nearby.getCount();
		final int fipsIndex = nearby.getColumnIndex(FIPS);
		final int[] counties = new int[count];
		
		nearby.moveToFirst();
		for (int i = 0; i < count; i++){
			counties[i] = nearby.getInt(fipsIndex);
		}
		
		return counties;
	}
	
	private int[] queryAdjacent(int fips){
		// TODO make this work for int[] fips
		final String[] columns = {ADJ};
		final String where = "where " + FIPS + " == ?";
		final String[] selArgs = {Integer.toString(fips)};
		
		final Cursor result = query(true, columns, where, selArgs);
		
		result.moveToFirst();
		if(result.getCount() != 1){
			throw new IllegalArgumentException("There is no county with the FIPS code " + fips);
		}
		else{
			final String[] adj = result.getString(result.getColumnIndex(ADJ)).split(",");
			final int[] counties = new int[adj.length];
			for (int i = 0; i < adj.length; i++){
				counties[i] = Integer.parseInt(adj[i]);
			}
			
			return counties;
		}
	}
	
	private Double distanceToCentroid(int fips, LatLng point){
		final String[] columns = {CENT_LAT, CENT_LONG};
		final String where = "where " + FIPS + " == ?";
		final String[] selArgs = {Integer.toString(fips)};
		
		final Cursor result = query(false, columns, where, selArgs);
		final int count = result.getCount();
		final int centLatIndex = result.getColumnIndex(CENT_LAT);
		final int centLongIndex = result.getColumnIndex(CENT_LONG);
		
		result.moveToFirst();
		Double distance = Double.MAX_VALUE;
		for (int i = 0; i < count; i++){
			final LatLng nextCentroid = new LatLng(result.getDouble(centLatIndex), result.getDouble(centLongIndex));
			final Double nextDistance = distance(point, nextCentroid);
			
			if (!(nextCentroid.isNull()) && nextDistance < distance){
				distance = nextDistance;
			}
			
			result.moveToNext();
		}
		
		return distance;
	}
	
	private static Double distance(LatLng pointA, LatLng pointB){
		return Math.sqrt(Math.pow(pointB.latitude - pointA.latitude, 2) + Math.pow(pointB.longitude - pointA.longitude, 2));
	}
}
