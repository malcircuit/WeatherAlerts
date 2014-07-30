package net.theneophyte.weatheralerts;

public interface CapConstants {
	/** 
	 * ATOM Constants
	 */
	static final String 
		ATOM_FEED = "feed",
		FEED_UPDATED_TAG = "updated",
		ALERT_ID = "id",
		ALERT_UPDATED = "updated",
		ALERT_PUBLISHED = "published",
		ALERT_AUTHOR = "author", AUTHOR_NAME = "name",
		ALERT_ENTRY = "entry",
		ALERT_TITLE = "title",
		ALERT_URL = "link", URL_ATTRIBUTE = "href",
		ALERT_SUMMARY = "summary";
	
	/**
	 * CAP Constants
	 */
	static final String 
		CAP_NAMESPACE = "urn:oasis:names:tc:emergency:cap:1.1",
		CAP_PREFIX = "cap",
		ALERT_EVENT = CAP_PREFIX + ":event",
		ALERT_EFFECTIVE_DATE = CAP_PREFIX + ":effective",
		ALERT_EXPIRATION_DATE = CAP_PREFIX + ":expires",
		ALERT_STATUS = CAP_PREFIX + ":status",
		ALERT_MSG_TYPE = CAP_PREFIX + ":msgType",
		ALERT_CATEGORY = CAP_PREFIX + ":category",
		ALERT_URGENCY = CAP_PREFIX + ":urgency",
		ALERT_SEVERITY = CAP_PREFIX + ":severity",
		ALERT_CERTAINTY = CAP_PREFIX + ":certainty",
		ALERT_AREA_DESC = CAP_PREFIX + ":areaDesc",
		ALERT_POLYGON = CAP_PREFIX + ":polygon",
		ALERT_GEOCODE = CAP_PREFIX + ":geocode",
		ALERT_FIPS6 = "FIPS6",
		ALERT_UGC = "UGC",
		CAP_PARAMETER = CAP_PREFIX + ":parameter",
		CAP_PARAMETER_VALUENAME = "valueName",
		CAP_PARAMETER_VALUE = "value",
		ALERT_VTEC = "VTEC";
	
	static final String WARNING_URGENCY = "Immediate";
	static final String WARNING_SEVERE = "Severe";
	static final String WARNING_EXTREME = "Extreme";
}
