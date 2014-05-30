package net.theneophyte.weatheralerts;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class DownloadXMLTask extends AsyncTask<String, Void, InputStream>{

	@Override
	protected InputStream doInBackground(String... urls) {
		try {
            return downloadURL(urls[0]);
        } 
		catch (IOException e) {
        }
		catch (URISyntaxException ue) {
        }
		return null;
	}
	
	/**
	 * Downloads the requested URL.
	 * @param url - URL of the file you want.
	 * @return Downloaded file as an {@link InputStream}.
	 * @throws IOException General HTTP connection failure.
	 * @throws URISyntaxException You gave it an improperly formatted URL.
	 */
	private InputStream downloadURL(String url) throws IOException, URISyntaxException {
		HttpClient client = new DefaultHttpClient();

		// Build a HTTP GET request packet.
		HttpGet request = new HttpGet();
		request.setURI(new URI(url));

		// Actually send the request.
		HttpResponse response = client.execute(request);
		
		return response.getEntity().getContent();
	}
}
