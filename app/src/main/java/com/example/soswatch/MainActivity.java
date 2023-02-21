package com.example.soswatch;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.soswatch.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private ActivityMainBinding binding;

    private static final int RC_SIGN_IN = 9001;
    private  String id = "";
    SharedPreferences.Editor myEditPref;
    private GoogleSignInClient mSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        TODO
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        myEditPref = sharedPref.edit();

        GoogleSignInOptions options =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .build();

        mSignInClient = GoogleSignIn.getClient(this, options);

        id = sharedPref.getString("token","");
        if(!id.equals("")) {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            intent.putExtra("token",id);
            startActivity(intent);
        }

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = mSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);

//                mSignInClient.signOut();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...).
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (signInResult.isSuccess()) {
                GoogleSignInAccount acct = signInResult.getSignInAccount();

                // Get account information.
                String fullName = acct.getDisplayName();
                String givenName = acct.getGivenName();
                String idToken = acct.getIdToken();
                String familyName = acct.getFamilyName();
                id = acct.getId();
                String email = acct.getEmail();//110022754939632693383

                Log.d("gsign:",fullName+" ,"+givenName+" , "+familyName+" ,"+idToken+" , "+id+" , "+email);

                storeBeachUser(fullName, email, familyName, id);
            }
        }
    }

    private void storeBeachUser(String name, String email, String familyName, String id) {
        String url = "https://app.arslaan.link/api/googleSignIn";

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        Map<String, String> params = new HashMap();
        params.put("token",id);
        params.put("name",name);
        params.put("familyName",familyName);
        params.put("email",email);

        JSONObject parameters = new JSONObject(params);

        RequestQueue req = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    String r_msg = obj.getString("msg");
                    boolean success = obj.getBoolean("success");
                    Toast.makeText(MainActivity.this, r_msg, Toast.LENGTH_SHORT).show();
                    if(success) {
                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        intent.putExtra("token",id);
                        startActivity(intent);

                        myEditPref.putString("token", id);
                    }

                    Log.d("ars", response.toString());

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
}