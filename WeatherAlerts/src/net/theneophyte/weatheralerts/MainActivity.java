package net.theneophyte.weatheralerts;

import java.util.List;

import net.theneophyte.weatheralerts.products.Alert;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_text);
		final ListView list = (ListView) findViewById(R.id.alertsList);
		final ArrayAdapter<Alert> adapter = new ArrayAdapter<Alert>(getApplicationContext(), R.layout.alerts_text);
		list.setAdapter(adapter);				
		FetchAlertsTask alertsTask = new FetchAlertsTask(){

			@Override
			void postExecute(List<Alert> alerts) {
				adapter.clear();
				adapter.addAll(alerts);
			}
			
		};
		
		alertsTask.execute(0);

		if (savedInstanceState == null) {
//			setUpMapIfNeeded();
		}
	}

	private void setUpMapIfNeeded() {
	    if (mMap == null) {
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	        
	        if (mMap != null) {
	        	mMap.setMyLocationEnabled(true);
	        }
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
