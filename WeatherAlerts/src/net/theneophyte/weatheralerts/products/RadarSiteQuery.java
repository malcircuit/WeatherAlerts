package net.theneophyte.weatheralerts.products;

import net.theneophyte.weatheralerts.R;
import android.content.Context;

// TODO Documentation
public class RadarSiteQuery extends DatabaseQuery{
	
	private static final int RES_ID = R.raw.sites;
	
	private static final String SITE_TABLE = "sites";
	
	private static final String 
			SITE_URL = "url",
			SITE_STATE_SHORT = "state_short",
			SITE_STATE_LONG = "state_long",
			SITE_REGION = "region",
			SITE_CITY = "city",
			SITE_ID = "site_id",
			SITE_LAT = "latitude",
			SITE_LONG = "longitude";

	
	public RadarSiteQuery(Context context) {
		super(context, SITE_TABLE, RES_ID);
	}
}
