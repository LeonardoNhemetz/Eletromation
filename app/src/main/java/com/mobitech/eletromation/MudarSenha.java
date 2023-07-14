package com.mobitech.eletromation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MudarSenha extends AppCompatActivity {

    TextView txtSenhaNova,txtConfirmarSenhaNova;
    Button btnEnviarSenha;
    private RequestQueue mRequest;
    private VolleyRP volley;
    private static final String url= "http://iurdcom.ddns.net:1024/eletromation/mudarsenha.php";
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mudar_senha );

        txtSenhaNova = (TextView) findViewById( R.id.txtSenhaNova );
        txtConfirmarSenhaNova = (TextView) findViewById( R.id.txtConfirmarSenhaNova );
        btnEnviarSenha = (Button) findViewById( R.id.btnEnviarSenha );

        volley = VolleyRP.getInstance( this );
        mRequest = volley.getRequestQueue();


        btnEnviarSenha.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtSenhaNova.getText().toString().equals( txtConfirmarSenhaNova.getText().toString() ))
                {
                    pd = new ProgressDialog(MudarSenha.this);
                    pd.setTitle("Mudando Senha");
                    pd.setMessage("Espere...");
                    pd.show();
                    EsqueceuSenha esqueceuSenha = new EsqueceuSenha();
                    HashMap<String, String> hashMapToken = new HashMap<>();
                    hashMapToken.put( "email", esqueceuSenha.EmailPara );
                    hashMapToken.put( "senha", txtSenhaNova.getText().toString());

                    JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, url, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject dados) {

                            try
                            {

                                String estado = dados.getString("resultado");
                                Log.i( "ESTADOOOOOOOOOOO",estado );
                                if(estado.equals( "ON" ))
                                {
                                    Toast.makeText( MudarSenha.this, "Senha trocada com sucesso", Toast.LENGTH_LONG ).show();
                                    pd.dismiss();
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText( MudarSenha.this, estado, Toast.LENGTH_LONG ).show();
                                    pd.dismiss();
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }



                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            Toast.makeText( MudarSenha.this, "Ocorreu algum erro", Toast.LENGTH_SHORT ).show();
                            Log.i( "COEEEEEEEEEEEEEEE", String.valueOf( error ) );


                        }
                    } );
                    VolleyRP.addToQueue( solicitar, mRequest, MudarSenha.this, volley );
                }
                else Toast.makeText( MudarSenha.this, "As senhas não coincidem", Toast.LENGTH_SHORT ).show();
            }


        } );
    }
}
