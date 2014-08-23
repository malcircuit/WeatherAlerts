package net.theneophyte.weatheralerts.products;

import java.util.List;


public class LatLngBox {
	
	public final double minLong, maxLong, maxLat, minLat;
	
	public LatLngBox(final double latTop, final double latBottom, final double longRight, final double longLeft){
		maxLat = latTop;
		minLat = latBottom;
		maxLong = longRight;
		minLong = longLeft;
	}
	
	public LatLngBox(LatLng northeast, LatLng southwest){
		maxLat = northeast.latitude;
		maxLong = northeast.longitude;
		minLat = southwest.latitude;
		minLong = southwest.longitude;
	}
	
	public static LatLngBox maxBounds(List<LatLng> boundary){
		double left = 0, right = 0, top = 0, bottom = 0;
		
		for(LatLng point : boundary){
			if (point.latitude < bottom){
				bottom = point.latitude;
			}
			
			if (point.latitude > top){
				top = point.latitude;
			}
			
			if (point.longitude > right){
				right = point.longitude;
			}
			
			if (point.longitude < left){
				left = point.longitude;
			}
		}
		
		return new LatLngBox(top, bottom, right, left);
	}

	public boolean contains(LatLngBox b){
		return false;
	}
	
	public boolean contains(LatLng point){
		if (point.latitude <= maxLat && point.latitude >= minLat){
			if (point.longitude < maxLong && point.longitude >= minLong){
				return true;
			}
		}
		
		return false;
	}

	public LatLng getNortheastCorner(){
		return new LatLng(maxLat, maxLong);
	}
	
	public LatLng getSouthwestCorner(){
		return new LatLng(minLat, minLong);
	}
	
}
