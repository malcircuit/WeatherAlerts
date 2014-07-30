package net.theneophyte.weatheralerts.products;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SiteListHelper extends SQLiteOpenHelper {
	
	private static final String NAME = "sites.sqlite";
	private static final int INITIAL_VERSION = 0;
	private static final int DATABASE_VERSION = INITIAL_VERSION;

	public static final String SITE_TABLE = "sites";
	
	public static final String 
			SITE_URL = "url",
			SITE_STATE_SHORT = "state_short",
			SITE_STATE_LONG = "state_long",
			SITE_REGION = "region",
			SITE_CITY = "city",
			SITE_ID = "site_id",
			SITE_LAT = "latitude",
			SITE_LONG = "longitude";
	
	public SiteListHelper(Context context) {
		super(context, NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < INITIAL_VERSION){
			
		}
	}
}
