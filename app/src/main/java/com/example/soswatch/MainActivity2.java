package com.example.soswatch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.soswatch.databinding.ActivityMain2Binding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends Activity {

    private TextView mTextView;
    private Button sendAlrtBtn;
    private ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        mTextView = binding.text;

        sendAlrtBtn = binding.sendAlert;
        sendAlrtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://app.arslaan.link/api/beachAlert";
//                String url = "http://localhost/misc_app/public/api/beachAlert";

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity2.this);

                Map<String, String> params = new HashMap();
                params.put("token","12345");
                params.put("latitude","7777777");
                params.put("longitude","999999");

                JSONObject parameters = new JSONObject(params);

                RequestQueue req = Volley.newRequestQueue(MainActivity2.this);
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(String.valueOf(response));
                            String r_msg = obj.getString("msg");
                            Log.d("ars", response.toString());
                            Toast.makeText(MainActivity2.this, r_msg, Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }}) {};
                requestQueue.add(stringRequest);
                stringRequest.setShouldCache(false);
            }
        });
    }
}