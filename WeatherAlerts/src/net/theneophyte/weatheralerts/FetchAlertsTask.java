package net.theneophyte.weatheralerts;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.theneophyte.weatheralerts.products.Alert;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

public abstract class FetchAlertsTask extends AsyncTask<Integer, Void, List<Alert>>{
	
	private final String ALERTS_URL = "http://alerts.weather.gov/cap/us.atom";
	
	public FetchAlertsTask(){
		super();
	}

	@Override
	protected List<Alert> doInBackground(Integer ... fips) {
		URL alertsUrl = null;
		HttpURLConnection conn = null;
		InputStream xmlFile = null;
		
		try {
			alertsUrl = new URL(ALERTS_URL);
			conn = (HttpURLConnection) alertsUrl.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			xmlFile = conn.getInputStream();

			AlertsParser parser = new AlertsParser();
			
			parser.setInput(xmlFile);
			return parser.getAlerts();
			
		} catch (MalformedURLException e) {
			// TODO We shouldn't ever get here because the URL is static and can't ever be malformed.
			e.printStackTrace();
		} catch (IOException e) {
			/* TODO What happens if there is an input stream failure?  
			 * Why would there be a stream failure?  The file should be in RAM, right?  
			 * If the RAM is corrupted we're screwed anyway.
			 */
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO The xml file we got is probably no good.  Redownload the file?
			e.printStackTrace();
		} finally { 
			if (conn != null){
				conn.disconnect();
			}
			
			if (xmlFile != null){
				try {
					xmlFile.close();
				} catch (IOException e) {
					// TODO I don't know why this would fail, so I'm not sure what to do about it.
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(List<Alert> alerts){
		if (alerts != null){
			postExecute(alerts);
		}
	}

	// TODO Do something with the list of warnings
	abstract void postExecute(List<Alert> alerts);
	
}
