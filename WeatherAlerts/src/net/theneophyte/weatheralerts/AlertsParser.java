package net.theneophyte.weatheralerts;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.theneophyte.weatheralerts.products.Alert;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.format.Time;
import android.util.Xml;

public class AlertsParser implements CapConstants{
	
	private final XmlPullParser parser;

	public AlertsParser() throws XmlPullParserException{
		parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	}
	
	public void setInput(InputStream xmlFile) throws XmlPullParserException{
		parser.setInput(xmlFile, null);
	}

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
	
	public static long parseDate(String dateString){
		final Time date = new Time(Time.TIMEZONE_UTC);
    	date.parse3339(dateString);
    	return date.toMillis(true);
	}
}
