package com.example.crowdzero_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class RegistarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);

        findViewById(R.id.btnRegist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnRegist:
                        registar();
                        break;
                }
            }
        });
    }

    private void registar() {
        Toast.makeText(RegistarActivity.this, "cheguei", Toast.LENGTH_SHORT).show();
        String urlRegU ="https://crowdzeromapi.herokuapp.com/registar";
        RequestQueue queueRegU = Volley.newRequestQueue(this);

        String email = ((TextInputLayout)findViewById(R.id.txtRegemail)).getEditText().getText().toString();
        String password = ((TextInputLayout)findViewById(R.id.txtRegpass)).getEditText().getText().toString();
        String nome = ((TextInputLayout)findViewById(R.id.txtRegnome)).getEditText().getText().toString();
        String contacto = ((TextInputLayout)findViewById(R.id.txtRegcontacto)).getEditText().getText().toString();
        String cc = ((TextInputLayout)findViewById(R.id.txtRegcc)).getEditText().getText().toString();

        StringRequest reqRegist = new StringRequest(Request.Method.POST, urlRegU,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (!response.equals("Success")){
                                Toast.makeText(RegistarActivity.this, "Registado com sucesso", Toast.LENGTH_SHORT).show();
                            }
                            else if (!response.equals("Fail")){
                                Toast.makeText(RegistarActivity.this, "Erro ao registar", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Error error) {
                            Toast.makeText(RegistarActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("car", "0");
                params.put("nom", nome.toString());
                params.put("ema", email.toString());
                params.put("pas", password.toString());
                params.put("con", contacto.toString());
                params.put("cci", cc.toString());
                params.put("idg", "123");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        reqRegist.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queueRegU.add(reqRegist);

        startActivity(new Intent(RegistarActivity.this, LoginActivity.class));
        finish();
    }
}