package com.example.yash.weatherapi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class weather extends AppCompatActivity {

    private String TAG = "MainActivity";
    //private ListView listView;
    TextView tempshow,city__name;
    private ProgressDialog progressDialog;
    ImageView emoji;

    private static String url;
    String maintemp,city_name;
    float temp_int;

    //ArrayList<HashMap<String,String >> cityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Intent intent = getIntent();
        String city_id = intent.getStringExtra("city");
        city_name = intent.getStringExtra("name");
        url = "http://api.openweathermap.org/data/2.5/weather?id="+ city_id + "&appid=9dbafc249e0e40479e01f13dc3d49fef";
        //cityList = new ArrayList<>();
        //listView = (ListView)findViewById(R.id.listView);
        tempshow = (TextView)findViewById(R.id.temp_show);
        city__name = (TextView)findViewById(R.id.city_name);
        emoji = (ImageView)findViewById(R.id.emoji);

        new weather.GetContacts().execute();

    }
    //onPreExecute--> doInBackground--> onPostExecute
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(weather.this);
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            com.example.yash.weatherapi.HttpHandler httpHandler = new com.example.yash.weatherapi.HttpHandler();

            String jsonString = httpHandler.makeServiceCall(url);

            Log.e(TAG,"Response from url: " +jsonString);

            if(jsonString!=null){
                try{
                    JSONObject jsonObject = new JSONObject(jsonString);

                    JSONObject temp = jsonObject.getJSONObject("main");
                     maintemp = temp.getString("temp");
                    temp_int = Float.parseFloat(maintemp);
                    temp_int -= 273.15;
                    maintemp = String.format("%.1f", temp_int) + " C";
                    //maintemp = String.valueOf(temp_int + "C");

                } catch (final JSONException e) {
                    //e.printStackTrace();
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }
            else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            city__name.setText(city_name);
            tempshow.setText(maintemp);
            if(temp_int > 40){
                emoji.setImageResource(R.drawable.hot);
            }
            if(temp_int < 40 &&temp_int > 30){
                emoji.setImageResource(R.drawable.hoter);
            }
            if(temp_int < 30 &&temp_int > 10){
                emoji.setImageResource(R.drawable.smile);
            }
            if(temp_int < 10){
                emoji.setImageResource(R.drawable.chill);
            }
        }
    }
}
