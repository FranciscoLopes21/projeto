package com.example.crowdzero_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Session session;
    private int rCargo;
    ImageView c1, c2, c3, icon;
    Button bMap, bLogo;
    TextView tUser, tCargo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new Session(this);

        c1 = findViewById(R.id.btnPoints);
        c2 = findViewById(R.id.btnPerfil);
        c3 = findViewById(R.id.btnContactos);
        icon = findViewById(R.id.imageView4);
        bMap = findViewById(R.id.btnMapa);
        tUser = findViewById(R.id.txtUsername);
        tCargo = findViewById(R.id.txtCargo);
        bLogo = findViewById(R.id.btnLogout);

        String url ="https://crowdzeromapi.herokuapp.com/userData";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("HttpClient", "success! response: " + response.toString());
                        try {
                            JSONObject newData = new JSONObject(response);
                            JSONArray dataArray = newData.getJSONArray("user");

                            rCargo = (Integer) dataArray.getJSONObject(0).get("cargo");
                            tUser.setText((String) dataArray.getJSONObject(0).get("nome"));

                            if (rCargo<= 50){
                                tCargo.setText("Cidadão");
                                icon.setImageResource(R.drawable.avatar);
                            } else if (rCargo > 50 && rCargo <= 200) {
                                tCargo.setText("Agente Sanitário");
                                icon.setImageResource(R.drawable.avatar2);
                            } else if (rCargo > 200) {
                                tCargo.setText("Agente de Saúde");
                                icon.setImageResource(R.drawable.avatar3);
                            }
                        }catch(Error | JSONException error) {
                            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HttpClient", "error: " + error.toString());
            }
        })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("id", session.getId().toString());
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);

        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnPoints:
                        startActivity(new Intent(MainActivity.this, PontosActivity.class));
                        finish();
                        break;
                }
            }
        });

        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnPerfil:
                        startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                        break;
                }
            }
        });

        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnContactos:
                        startActivity(new Intent(MainActivity.this, ContactosActivity.class));
                        break;
                }
            }
        });

        bMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnMapa:
                        openMapa();
                        break;
                }
            }
        });

        bLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnLogout:
                        logout();
                        break;
                }
            }
        });

    }

    protected void openMapa(){
        startActivity(new Intent(MainActivity.this, MapaActivity.class));
        finish();
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.apply();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}