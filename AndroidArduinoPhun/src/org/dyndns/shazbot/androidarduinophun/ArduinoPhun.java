package org.dyndns.shazbot.androidarduinophun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.support.v4.app.NavUtils;

public class ArduinoPhun extends Activity implements OnClickListener {
	HttpClient client = new DefaultHttpClient();
	public static final String HOST = "http://10.0.0.10";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
               
        ToggleButton red = (ToggleButton)findViewById(R.id.red);
        red.setOnClickListener(this);

        ToggleButton green = (ToggleButton)findViewById(R.id.green);
        green.setOnClickListener(this);

        ToggleButton blue = (ToggleButton)findViewById(R.id.blue);
        blue.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
	public void onClick(View v) {
    	ToggleButton tb = (ToggleButton) v;
    	String ledColor = tb.getText().toString();
    	String params[] = { ledColor, "0" };
    	//Instantiate the asynctask, which we will use to perform an HTTP GET
    	postToArduino post = new postToArduino();
    	
    	//By default, the LED state is set to on, or '0' (params[1]).
    	if (tb.isChecked()) {
    		post.execute(params);
    	} else {
    		params[1] = "1";
    		post.execute(params);
    	}		
	}
    
    private class postToArduino extends AsyncTask<String, Integer, String> {
    	
		@Override
		protected String doInBackground(String... params) {
			String uri = "";
			String response = "";
			
			//Not sure if this is the right way to do this? (building the uri string)
			for (String param : params) {
				uri += "/" + param;
			}
	
			String url = HOST + uri;		
			HttpGet request = new HttpGet(url);
			
            try {           	
				HttpResponse execute = client.execute(request);
				InputStream content = execute.getEntity().getContent();
				
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String inputString = "";
				
				while ((inputString = buffer.readLine()) != null) {
					response += inputString;
				}
			} catch (ClientProtocolException e) {
				Log.e("ClientProtocolException", e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("IOException", e.toString());
				e.printStackTrace();
			}
            return response;
		}
		
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
		}
    }
}
