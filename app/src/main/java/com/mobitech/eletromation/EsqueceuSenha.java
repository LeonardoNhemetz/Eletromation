package com.mobitech.eletromation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.mobitech.eletromation.Cliente.CriarPedidoCliente;
import com.mobitech.eletromation.Email.GmailSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

public class EsqueceuSenha extends AppCompatActivity {

    TextView txtEsqueceuSenhaEmail,txtCodigo;
    Button btnEnviarCodigo,btnRecuperarSenha;
    public static String EmailPara = "",Text = "";
    ProgressDialog pd;
    private RequestQueue mRequest;
    private VolleyRP volley;
    private static final String url= "http://iurdcom.ddns.net:1024/eletromation/esqueceusenha.php";
    private static final String url2= "http://iurdcom.ddns.net:1024/eletromation/verificarcodigo.php";
    public static String codigo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_esqueceu_senha );

        txtEsqueceuSenhaEmail = (TextView) findViewById( R.id.txtEsqueceuSenhaEmail );
        txtCodigo = (TextView) findViewById( R.id.txtCodigo );
        btnEnviarCodigo = (Button) findViewById( R.id.btnEnviarCodigo );
        btnRecuperarSenha = (Button) findViewById( R.id.btnRecuperarSenha );

        volley = VolleyRP.getInstance( this );
        mRequest = volley.getRequestQueue();



        btnEnviarCodigo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String alphabet = "abcdefghijklmnopqrstuvwxyz1234567890";
                final int N = alphabet.length();
                Random rd = new Random();
                int iLength = 5;
                StringBuilder sb = new StringBuilder(iLength);
                for (int i = 0; i < iLength; i++) {
                    sb.append(alphabet.charAt(rd.nextInt(N)));
                }
                codigo = sb.toString();
                pd = new ProgressDialog(EsqueceuSenha.this);
                pd.setTitle("Enviando E-Mail");
                pd.setMessage("Espere...");
                pd.show();

                EmailPara = txtEsqueceuSenhaEmail.getText().toString();
                Text = "Digite o codigo a seguir no aplicativo para recuperar sua senha: "+codigo;

                CadastrarSenhaPerdida( EmailPara,codigo );



            }
        } );

        btnRecuperarSenha.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                HashMap<String, String> hashMapToken = new HashMap<>();
                hashMapToken.put( "email", EmailPara );
                hashMapToken.put( "codigo", txtCodigo.getText().toString() );

                JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, url2, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject dados) {

                        try
                        {

                            String estado = dados.getString("resultado");
                            Log.i( "ESTADOOOOOOOOOOO",estado );
                            if(estado.equals( "ON" ))
                            {
                                Intent i = new Intent(EsqueceuSenha.this,MudarSenha.class);
                                startActivity( i );
                                pd.dismiss();
                                finish();
                            }
                            else
                            {
                                Toast.makeText( EsqueceuSenha.this, estado, Toast.LENGTH_LONG ).show();
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
                        Toast.makeText( EsqueceuSenha.this, "Ocorreu algum erro", Toast.LENGTH_SHORT ).show();
                        Log.i( "COEEEEEEEEEEEEEEE", String.valueOf( error ) );


                    }
                } );
                VolleyRP.addToQueue( solicitar, mRequest, EsqueceuSenha.this, volley );
            }
        } );
    }

    private void CadastrarSenhaPerdida(String email,String codigo)
    {
        HashMap<String, String> hashMapToken = new HashMap<>();
        hashMapToken.put( "email", email );
        hashMapToken.put( "codigo", codigo );

        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, url, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados) {

                Log.i( "DADOSSSSSS",dados.toString() );
                try
                {
                    String estado = dados.getString("resultado");
                    if(estado.equals( "Email enviado com sucesso" ))
                    {
                        EnviarEmailCodigo enviarEmailCodigo = new EnviarEmailCodigo();
                        enviarEmailCodigo.execute( Text,EmailPara,EmailPara );
                        Toast.makeText( EsqueceuSenha.this, estado, Toast.LENGTH_LONG ).show();
                        pd.dismiss();
                    }
                    else
                    {
                        Toast.makeText( EsqueceuSenha.this, estado, Toast.LENGTH_LONG ).show();
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
                Toast.makeText( EsqueceuSenha.this, "Ocorreu algum erro", Toast.LENGTH_SHORT ).show();
                Log.i( "COEEEEEEEEEEEEEEE", String.valueOf( error ) );



            }
        } );
        VolleyRP.addToQueue( solicitar, mRequest, this, volley );

    }




}
