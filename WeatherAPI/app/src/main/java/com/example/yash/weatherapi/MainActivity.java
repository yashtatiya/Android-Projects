package com.example.yash.weatherapi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private ListView listView;
    private ProgressDialog progressDialog;

    private static String url = "https://weatherforcast.000webhostapp.com/city_list.json";

    ArrayList<HashMap<String,String >> cityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityList = new ArrayList<>();
        listView = (ListView)findViewById(R.id.listView);

        new GetContacts().execute();
    }
    //onPreExecute--> doInBackground--> onPostExecute
    private class GetContacts extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler httpHandler = new HttpHandler();

            String jsonString = httpHandler.makeServiceCall(url);

            Log.e(TAG,"Response from url: " +jsonString);

            if(jsonString!=null){
                try{
                    JSONObject jsonObject = new JSONObject(jsonString);

                    JSONArray city = jsonObject.getJSONArray("City");

                    for(int i=0; i<city.length();i++){
                        JSONObject c = city.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");
                        String country = c.getString("country");

                        //Phone node is json object
                        JSONObject coord = c.getJSONObject("coord");
                        String lon = coord.getString("lon");
                        String lat = coord.getString("lat");

                        // tmp hash map for single contact
                        HashMap<String, String> eachcity = new HashMap<>();

                        // adding each child node to HashMap key => value
                        eachcity.put("id", id);
                        eachcity.put("name", name);
                        eachcity.put("country", country);
                        eachcity.put("lon", lon);
                        eachcity.put("lat", lat);

                        // adding contact to contact list
                        cityList.add(eachcity);
                    }

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

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, cityList,
                    R.layout.list_item, new String[]{"name"}, new int[]{R.id.name});
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String city_id = cityList.get(position).get("id");
                    String city_name = cityList.get(position).get("name");
                    Intent intent = new Intent(MainActivity.this,weather.class);
                    intent.putExtra("city",city_id);
                    intent.putExtra("name",city_name);
                    startActivity(intent);
                }
            });
        }
    }
}