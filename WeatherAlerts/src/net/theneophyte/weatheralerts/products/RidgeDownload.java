package net.theneophyte.weatheralerts.products;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import com.google.android.gms.maps.model.GroundOverlay;


public class RidgeDownload implements Callable<GroundOverlay>{

	private static final String URL_BASE = "http://radar.weather.gov/ridge/RadarImg/N0Z/";
	private static final String BASE_REFL_LONG = "N0Z";
	private static final String WORLD_FILE_FILENAME = "_N0Z_0.gfw";
	private static final String MOST_RECENT_IMAGE_FILENAME = "_N0Z_0.gif";
	private static final String IMAGE_FILENAME_EXTENSION = ".gif";
	private static final String ORDERED_LAST_MODIFIED = "?C=M;O=D";
	
	private final String mSiteId;
	private final String mDirectory;
	private final URL mDirectoryIndex;
	private final URL mMostRecentImage;
	private final URL mWorldFile;
	
	public RidgeDownload(String siteId){
		mSiteId = siteId.substring(0);
		mDirectory = URL_BASE + mSiteId;
		try {
			mDirectoryIndex = new URL(mDirectory + "/" + ORDERED_LAST_MODIFIED);
			mMostRecentImage = new URL(mDirectory + MOST_RECENT_IMAGE_FILENAME);
			mWorldFile = new URL(mDirectory + WORLD_FILE_FILENAME);
		} catch (MalformedURLException e) {
			throw new RuntimeException();
		}
		
	}
	
	public void downloadWorldFile(String siteId){
		
	}
	
	public void downloadMostRecentImage(String siteId){
		
	}

	public void downloadImage(String siteId, int index){
		
	}

	@Override
	public GroundOverlay call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
