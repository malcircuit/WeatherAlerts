package net.theneophyte.weatheralerts;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.theneophyte.weatheralerts.products.Alert;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.format.Time;
import android.util.Xml;

/**
 * Class for parsing {@link Alert}s out of the NWS alerts feed.
 * @author Matt Sutter
 *
 */
public class AlertsParser implements CapConstants{
	
	private final XmlPullParser parser;

	/**
	 * Constructor
	 */
	public AlertsParser(){
		parser = Xml.newPullParser();
	}
	
	/**
	 * Sets the input to the parser.
	 * @param xmlFile - Alerts XML file.
	 * @throws XmlPullParserException If the input is not a valid XML file.
	 */
	public void setInput(InputStream xmlFile) throws XmlPullParserException{
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(xmlFile, null);
	}

	/**
	 * Parses a {@link List} of {@link Alert}s out of the XML file.
	 * @return {@link List} containing all of the weather alerts in the feed.
	 * @throws XmlPullParserException If the XML entry for an alert is not valid.
	 * @throws IOException
	 */
	public List<Alert> getAlerts() throws XmlPullParserException, IOException{
		List<Alert> alerts = new ArrayList<Alert>();

		parser.require(XmlPullParser.START_DOCUMENT, null, null);
		parser.next();
		while (!parser.getName().equals(ATOM_FEED)){
			skipTag(parser);
		}
		
		parser.require(XmlPullParser.START_TAG, null, ATOM_FEED);
		while (parser.next() != XmlPullParser.END_TAG){
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			final String tagName = parser.getName();

			if (tagName.equals(ALERT_ENTRY)){
				alerts.add(Alert.readAlert(parser));
			}
			else{
				skipTag(parser);
			}
		}
		
		return alerts;
	}
	
	/**
	 * Skips a tag (any tags it wraps, recursively) in the XML.
	 * @param parser - XML parser for which you want to skip a tag.
	 * @throws XmlPullParserException If the XML is not valid.
	 * @throws IOException
	 */
	public static void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException{
		if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	}
	
	/**
	 * Turns a date string from the alerts XML into Unix time.
	 * @param dateString
	 * @return Date as milliseconds.
	 */
	public static long parseDate(String dateString){
		final Time date = new Time(Time.TIMEZONE_UTC);
    	date.parse3339(dateString);
    	return date.toMillis(true);
	}
}
