package net.theneophyte.weatheralerts;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class CAPParser{
	
	private static final String CAP_NAMESPACE = "cap";
	
	private XmlPullParser parser;

	protected void setXML(InputStream xml_file) throws XmlPullParserException, IOException{
		try{
			parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			parser.setInput(xml_file, null);
		}
		finally{
			xml_file.close();
		}
	}
}
