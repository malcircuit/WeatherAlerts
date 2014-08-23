package net.theneophyte.weatheralerts.products;

import java.io.IOException;

import net.theneophyte.weatheralerts.CapConstants;
import net.theneophyte.weatheralerts.AlertsParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Class for enclosing all of the useful information parsed from a particular weather alert from the NWS alerts XML feed. 
 * @author Matt Sutter
 *
 */
public class Alert implements CapConstants{
	
	private static final int 
			ALERT_FIELDS = 21,
			URL = 0,
			UPDATED = 1,
			PUBLISHED = 2,
			AUTHOR = 3,
			TITLE = 4,
			FIPS = 5,
			SUMMARY = 6,
			EVENT = 7,
			EFFECTIVE = 8,
			EXPIRES = 9,
			STATUS = 10,
			MSG_TYPE = 11,
			CATEGORY = 12,
			URGENCY = 13,
			SEVERITY = 14,
			CERTAINTY = 15,
			AREA_DESC = 16,
			POLYGON = 17,
			UGC = 18,
			VTEC = 19;
	
	public final String 
			mURL,
			mAuthor,
			mTitle,
			mSummary,
			mEvent,
			mStatus,
			mMsgType,
			mCategory,
			mUrgency,
			mSeverity,
			mCertainty;
	
	public final String[]
			mUGC,
			mAreaDesc;
	
	public final long 
			mExpirationDate,
			mEffectiveDate,
			mUpdated,
			mPublished;
	
	public final AlertPolygon mPolygon;
	public final int[] mFIPS;
	public final VTEC mVTEC;
	
	private Alert(String[] alertValues){
		mURL = alertValues[URL];
		mUpdated = AlertsParser.parseDate(alertValues[UPDATED]);
		mPublished = AlertsParser.parseDate(alertValues[PUBLISHED]);
		mAuthor = alertValues[AUTHOR];
		mTitle = alertValues[TITLE];
		mSummary = alertValues[SUMMARY];
		mEvent = alertValues[EVENT];
		mEffectiveDate = AlertsParser.parseDate(alertValues[EFFECTIVE]);
		mExpirationDate = AlertsParser.parseDate(alertValues[EXPIRES]);
		mStatus = alertValues[STATUS];
		mMsgType = alertValues[MSG_TYPE];
		mCategory = alertValues[CATEGORY];
		mUrgency = alertValues[URGENCY];
		mSeverity = alertValues[SEVERITY];
		mCertainty = alertValues[CERTAINTY];
		mAreaDesc = alertValues[AREA_DESC].split(";\\s");
		
		if (!alertValues[POLYGON].isEmpty()){
			mPolygon = new AlertPolygon(alertValues[POLYGON]);
		}
		else{
			mPolygon = null;
		}
		
		mFIPS = parseFips(alertValues[FIPS]);
		mUGC = alertValues[UGC].split("\\s");
		
		if (!alertValues[VTEC].isEmpty()){
			mVTEC = new VTEC(alertValues[VTEC]);
		}
		else{
			mVTEC = null;
		}
	}

	/**
	 * Parses FIPS codes out the string from an alert entry.
	 * @param fipsValues String containing FIPS codes separated by spaces.
	 * @return int array containing the FIPS codes
	 */
	private final int[]	parseFips(String fipsValues){
		final String[] values = fipsValues.split("\\s");
		final int[] fips = new int[values.length];
		for (int i = 0; i < values.length; i++){
			fips[i] = Integer.parseInt(values[i]);
		}
		
		return fips;
	}

	/**
	 * Builds a new {@link Alert} by parsing the information from the alerts feed.
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static Alert readAlert(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, ALERT_ENTRY);

		final String[] alertValues = new String[ALERT_FIELDS];

		while (parser.next() != XmlPullParser.END_TAG){
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			final String tagName = parser.getName();

			if (tagName.equals(ALERT_ID)){
				alertValues[URL] = parser.nextText();
			}
			else if (tagName.equals(ALERT_UPDATED)){
				alertValues[UPDATED] = parser.nextText();
			}
			else if (tagName.equals(ALERT_PUBLISHED)){
				alertValues[PUBLISHED] = parser.nextText();
			}
			else if (tagName.equals(ALERT_AUTHOR)){
				alertValues[AUTHOR] = readAuthor(parser);
			}
			else if (tagName.equals(ALERT_TITLE)){
				alertValues[TITLE] = parser.nextText();
			}
			else if (tagName.equals(ALERT_SUMMARY)){
				alertValues[SUMMARY] = parser.nextText();
			}
			else if (tagName.equals(ALERT_EVENT)){
				alertValues[EVENT] = parser.nextText();
			}
			else if (tagName.equals(ALERT_EFFECTIVE_DATE)){
				alertValues[EFFECTIVE] = parser.nextText();
			}
			else if (tagName.equals(ALERT_EXPIRATION_DATE)){
				alertValues[EXPIRES] = parser.nextText();
			}
			else if (tagName.equals(ALERT_STATUS)){
				alertValues[STATUS] = parser.nextText();
			}
			else if(tagName.equals(ALERT_MSG_TYPE)){
				alertValues[MSG_TYPE] = parser.nextText();
			}
			else if (tagName.equals(ALERT_CATEGORY)){
				alertValues[CATEGORY] = parser.nextText();
			}
			else if (tagName.equals(ALERT_URGENCY)){
				alertValues[URGENCY] = parser.nextText();
			}
			else if (tagName.equals(ALERT_SEVERITY)){
				alertValues[SEVERITY] = parser.nextText();
			}
			else if (tagName.equals(ALERT_CERTAINTY)){
				alertValues[CERTAINTY] = parser.nextText();
			}
			else if (tagName.equals(ALERT_AREA_DESC)){
				alertValues[AREA_DESC] = parser.nextText();
			}
			else if (tagName.equals(ALERT_GEOCODE)){
				readGeocodes(parser, alertValues);
			}
			else if (tagName.equals(ALERT_POLYGON)){
				alertValues[POLYGON] = parser.nextText();
			}
			else if (tagName.equals(CAP_PARAMETER)){
				alertValues[VTEC] = readVTEC(parser);
			}
			else{
				AlertsParser.skipTag(parser);
			}
		}

		return new Alert(alertValues);
	}

	/**
	 * Finds the next start tag in the XML file.
	 * @param parser 
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void nextStartTag(XmlPullParser parser) throws XmlPullParserException, IOException{
		int type = parser.getEventType();
		
		if (type == XmlPullParser.START_TAG){
			type = parser.nextToken();
		}
		
		while (type != XmlPullParser.START_TAG){
			type = parser.nextToken();
		}
	}
	
	/**
	 * Finds the next end tag in the XML file.
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void nextEndTag(XmlPullParser parser) throws XmlPullParserException, IOException{
		int type = parser.getEventType();

		if (type == XmlPullParser.END_TAG){
			type = parser.nextToken();
		}
		
		while (type != XmlPullParser.END_TAG){
			type = parser.nextToken();
		}
	}

	/**
	 * Parses the value contained in the "author" tag.
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static String readAuthor(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, ALERT_AUTHOR);
		
		nextStartTag(parser);
		parser.require(XmlPullParser.START_TAG, null, AUTHOR_NAME);
		
		final String author = parser.nextText();
		
		parser.require(XmlPullParser.END_TAG, null, AUTHOR_NAME);
		nextEndTag(parser);
		parser.require(XmlPullParser.END_TAG, null, ALERT_AUTHOR);
		
		return author;
	}

	/**
	 * Parses the values contained in the "cap:geocode" tag.
	 * @param parser
	 * @param alertValues
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readGeocodes(XmlPullParser parser, String[] alertValues) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, ALERT_GEOCODE);
		
		nextStartTag(parser);
		parser.require(XmlPullParser.START_TAG, null, CAP_PARAMETER_VALUENAME);
		if (!parser.nextText().equals(ALERT_FIPS6)){
			throw new XmlPullParserException("Not FIPS6");
		}
		parser.require(XmlPullParser.END_TAG, null, CAP_PARAMETER_VALUENAME);
		
		nextStartTag(parser);
		parser.require(XmlPullParser.START_TAG, null, CAP_PARAMETER_VALUE);
		alertValues[FIPS] = parser.nextText();
		parser.require(XmlPullParser.END_TAG, null, CAP_PARAMETER_VALUE);
		
		nextStartTag(parser);
		parser.require(XmlPullParser.START_TAG, null, CAP_PARAMETER_VALUENAME);
		if (!parser.nextText().equals(ALERT_UGC)){
			throw new XmlPullParserException("Not UGC");
		}
		parser.require(XmlPullParser.END_TAG, null, CAP_PARAMETER_VALUENAME);
		nextStartTag(parser);
		parser.require(XmlPullParser.START_TAG, null, CAP_PARAMETER_VALUE);
		alertValues[UGC] = parser.nextText();
		parser.require(XmlPullParser.END_TAG, null, CAP_PARAMETER_VALUE);
		
		nextEndTag(parser);
		parser.require(XmlPullParser.END_TAG, null, ALERT_GEOCODE);
	}

	/**
	 * Parses the VTEC string contained in the "cap:parameter" tag.
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static String readVTEC(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, CAP_PARAMETER);

		nextStartTag(parser);
		parser.require(XmlPullParser.START_TAG, null, CAP_PARAMETER_VALUENAME);
		if (!parser.nextText().equals(ALERT_VTEC)){
			throw new XmlPullParserException("Not VTEC");
		}
		parser.require(XmlPullParser.END_TAG, null, CAP_PARAMETER_VALUENAME);

		nextStartTag(parser);
		parser.require(XmlPullParser.START_TAG, null, CAP_PARAMETER_VALUE);
		String vtec = parser.nextText();
		parser.require(XmlPullParser.END_TAG, null, CAP_PARAMETER_VALUE);
		nextEndTag(parser);
		parser.require(XmlPullParser.END_TAG, null, CAP_PARAMETER);

		return vtec;
	}

	@Override
	public String toString(){
		return mEvent;
	}
}
