package net.theneophyte.weatheralerts.products;

import java.util.ArrayList;
import java.util.List;


public class LatLngBox {
	
	private final double mLeft, mRight, mTop, mBottom;
	
	public LatLngBox(final double top, final double bottom, final double right, final double left){
		mTop = top;
		mBottom = bottom;
		mRight = right;
		mLeft = left;
	}
	
	public LatLngBox(LatLng northeast, LatLng southwest){
		mTop = northeast.latitude;
		mRight = northeast.longitude;
		mBottom = southwest.latitude;
		mLeft = southwest.longitude;
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
		if (point.latitude <= mTop && point.latitude >= mBottom){
			if (point.longitude < mRight && point.longitude >= mLeft){
				return true;
			}
		}
		
		return false;
	}

	public LatLng getNortheastCorner(){
		return new LatLng(mTop, mRight);
	}
	
	public LatLng getSouthwestCorner(){
		return new LatLng(mBottom, mLeft);
	}
	
}
