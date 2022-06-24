package com.example.crowdzero_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private ImageView icon;
    TextInputLayout email, nome, pass, cont, caci;
    TextView name, points;
    int pontos;
    Session session;
    Button edit, save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        session = new Session(this);

        nome = (TextInputLayout) findViewById(R.id.txtName);
        email = (TextInputLayout) findViewById(R.id.txtEmail);
        pass = (TextInputLayout) findViewById(R.id.txtPassword);
        cont = (TextInputLayout) findViewById(R.id.txtContact);
        caci = (TextInputLayout) findViewById(R.id.txtCc);
        name = findViewById(R.id.txtUsername);
        points = findViewById(R.id.txtCargo);
        icon = findViewById(R.id.imageView4);
        edit = findViewById(R.id.btnEditar);
        save = findViewById(R.id.btnGuardar);

        popText();

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(PerfilActivity.this, MainActivity.class));
            }
        });
        findViewById(R.id.btnEditar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editar();
            }
        });
        findViewById(R.id.btnGuardar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PerfilActivity.this, MainActivity.class));
        finish();
    }

    private void popText() {
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
                            for (int i = 0; i < dataArray.length(); i++) {
                                pontos = (Integer) dataArray.getJSONObject(0).get("cargo");
                                nome.getEditText().setText((String) dataArray.getJSONObject(i).get("nome"));
                                name.setText((String) dataArray.getJSONObject(0).get("nome"));
                                email.getEditText().setText((String) dataArray.getJSONObject(i).get("email"));
                                pass.getEditText().setText((String) dataArray.getJSONObject(i).get("pass"));
                                cont.getEditText().setText(dataArray.getJSONObject(i).get("contacto").toString());
                                caci.getEditText().setText(dataArray.getJSONObject(i).get("cc").toString());
                                //google = String.valueOf(dataArray.getJSONObject(i).get("idgoogle").toString());

                                points.setText(String.valueOf(pontos));
                                if (pontos <= 50){
                                    points.setText("Cidadão");
                                    icon.setImageResource(R.drawable.avatar);
                                } else if (pontos > 50 && pontos <= 200) {
                                    points.setText("Agente Sanitário");
                                    icon.setImageResource(R.drawable.avatar2);
                                } else if (pontos > 200) {
                                    points.setText("Agente de Saúde");
                                    icon.setImageResource(R.drawable.avatar3);
                                }

                                //Toast.makeText( PerfilActivity.this, google.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }catch(Error | JSONException error) {
                            Toast.makeText(PerfilActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
    }

    private void editar() {
        email.setEnabled(true);
        nome.setEnabled(true);
        pass.setEnabled(true);
        cont.setEnabled(true);
        caci.setEnabled(true);

        edit.setVisibility(View.INVISIBLE);
        save.setVisibility(View.VISIBLE);
    }

    private void guardar() {
        String urlUpdU ="https://crowdzeromapi.herokuapp.com/updateUser/" + session.getId();
        RequestQueue queueUpU = Volley.newRequestQueue(this);

        String con, carcid;
        con = cont.getEditText().getText().toString();
        carcid = caci.getEditText().getText().toString();

        if (con.length() != 9 && !con.equals("")) {
            Toast.makeText(PerfilActivity.this, "Número do contacto incorreto", Toast.LENGTH_SHORT).show();
        } else if (carcid.length() != 9 || carcid.equals("")) {
            Toast.makeText(PerfilActivity.this, "Número do Cartão de Cidadão incorreto", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest reqUpdat = new StringRequest(Request.Method.POST, urlUpdU,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (response.equals("update success")){
                                    email.setEnabled(false);
                                    nome.setEnabled(false);
                                    pass.setEnabled(false);
                                    cont.setEnabled(false);
                                    caci.setEnabled(false);

                                    edit.setVisibility(View.VISIBLE);
                                    save.setVisibility(View.INVISIBLE);
                                    Toast.makeText(PerfilActivity.this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show();
                                }
                                else if (response.equals("Fail")){
                                    Toast.makeText(PerfilActivity.this, "Erro ao atualizar", Toast.LENGTH_SHORT).show();
                                }
                            }catch(Error error) {
                                Toast.makeText(PerfilActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                    params.put("nome", nome.getEditText().getText().toString());
                    params.put("email", email.getEditText().getText().toString());
                    params.put("pass", pass.getEditText().getText().toString());
                    params.put("contacto", cont.getEditText().getText().toString());
                    params.put("cc", caci.getEditText().getText().toString());
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };
            reqUpdat.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queueUpU.add(reqUpdat);
        }
    }
}