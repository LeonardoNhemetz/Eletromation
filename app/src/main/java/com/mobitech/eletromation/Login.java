package com.mobitech.eletromation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mobitech.eletromation.Cliente.ClientePJ.MainClientePJ;
import com.mobitech.eletromation.CriarConta.EnviarSMSCadastroCliente;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    EditText txtLogin,txtSenha;
    Button btnEntrarComoCliente,btnCriarConta;
    //TextView lblespecialista;
    TextView lblEsqueceuSenha;
    private static final String urlCliente = "http://iurdcom.ddns.net:1024/eletromation/login_getcliente.php";
    private static final String urlEspecialista = "http://iurdcom.ddns.net:1024/eletromation/login_getespecialista.php";
    private RequestQueue mRequest;
    private VolleyRP volley;
    private String USER = "";
    private String PASSWORD = "";
    ProgressDialog pd;

    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );


        txtLogin = (EditText) findViewById( R.id.txtLogin );
        txtSenha = (EditText) findViewById( R.id.txtSenha );
        btnEntrarComoCliente = (Button) findViewById( R.id.btnEntrarComoCliente );
        btnCriarConta = (Button) findViewById( R.id.btnCriarConta );


        //lblespecialista = (TextView) findViewById( R.id.lblespecialista );
        lblEsqueceuSenha = (TextView) findViewById( R.id.lblEsqueceuSenha );

        volley = VolleyRP.getInstance( this );
        mRequest = volley.getRequestQueue();

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        //SpannableString ss = new SpannableString("Entrar como especialista");
        //ss.setSpan(new CustomClickableSpan(), 0, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //lblespecialista.setText(ss);
        //lblespecialista.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString ss = new SpannableString("Esqueci minha senha");
        ss.setSpan(new ClickableSpan(), 0, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        lblEsqueceuSenha.setText(ss);
        lblEsqueceuSenha.setMovementMethod(LinkMovementMethod.getInstance());


        SharedPreferences sharedPreferences =  getSharedPreferences("LoginCOM",MODE_PRIVATE);
        String Login = sharedPreferences.getString("Login","");
        String Senha = sharedPreferences.getString("Senha","");

        txtLogin.setText(Login);
        txtSenha.setText(Senha);



        btnEntrarComoCliente.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                USER = txtLogin.getText().toString().toLowerCase();
                PASSWORD = txtSenha.getText().toString();

                USER = USER.replaceAll( " ","" );
                PASSWORD = PASSWORD.replaceAll( " ","" );

                SharedPreferences sharedPreferences =  getSharedPreferences("LoginCOM",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Login",USER);
                editor.putString("Senha",PASSWORD);
                editor.commit();

                pd = new ProgressDialog(Login.this);
                pd.setTitle("Verificando Login");
                pd.setMessage("Espere...");
                pd.show();

                LogarCliente();
            }
        } );


        btnCriarConta.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( Login.this,EnviarSMSCadastroCliente.class );
                startActivity( i );

            }
        } );

    }
    private class ClickableSpan extends android.text.style.ClickableSpan
    {

        @Override
        public void onClick(View widget) {

            Intent i = new Intent( Login.this,EsqueceuSenha.class );
            startActivity( i );

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.BLUE);//cor do texto
            ds.setUnderlineText(false); //remove sublinhado
        }
    }

   /*private class CustomClickableSpan extends ClickableSpan {
        @Override
        public void onClick(View textView) {
            USER = txtLogin.getText().toString().toLowerCase();
            PASSWORD = txtSenha.getText().toString().toLowerCase();

            SharedPreferences sharedPreferences =  getSharedPreferences("LoginCOM",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Login",USER);
            editor.putString("Senha",PASSWORD);
            editor.commit();

            pd = new ProgressDialog(Login.this);
            pd.setTitle("Verificando Login");
            pd.setMessage("Espere...");
            pd.show();

            LogarEspecialista();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.BLUE);//cor do texto
            ds.setUnderlineText(false); //remove sublinhado
        }
    }*/


    private void LogarCliente()
    {
        HashMap<String,String> hashMapToken = new HashMap<>(  );
        hashMapToken.put( "login",USER);
        hashMapToken.put( "senha",PASSWORD);

        Log.i("COEEEEEEEEEEEEEE",PASSWORD);

        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, urlCliente, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados) {

                VerificarPasswordCliente( dados );
                Log.i("COEEEEEEEEEEEEEEE", String.valueOf( dados ) );

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(Login.this, "Ocorreu algum erro",Toast.LENGTH_LONG).show();
                Log.i("COEEEEEEEEEEEEEEE", String.valueOf( error ) );


            }
        });
        VolleyRP.addToQueue(solicitar,mRequest,this,volley);

    }
    public void VerificarPasswordCliente(JSONObject dados){
        try {
            String estado = dados.getString("resultado");
            if(estado.equals("CC")){
                ClienteInfo clienteInfo = new ClienteInfo();
                JSONObject Jsondados = new JSONObject(dados.getString("dados"));
                clienteInfo.setEmail_cliente( Jsondados.getString("email_cliente") );
                clienteInfo.setSenha_cliente( Jsondados.getString("senha_cliente") );
                clienteInfo.setTipo_pessoa_cliente( Jsondados.getString( "tipo_pessoa_cliente" ) );
                clienteInfo.setDocumento_cliente( Jsondados.getString( "documento_cliente" ) );
                clienteInfo.setNome_cliente( Jsondados.getString( "nome_cliente" ) );
                clienteInfo.setRaz_social_cliente( Jsondados.getString( "raz_social_cliente" ) );
                clienteInfo.setNome_fantasia_cliente( Jsondados.getString( "nome_fantasia_cliente" ) );
                clienteInfo.setCep_cliente( Jsondados.getString( "cep_cliente" ) );
                clienteInfo.setEndereco_cliente( Jsondados.getString( "endereco_cliente" ) );
                clienteInfo.setCelular_cliente( Jsondados.getString( "celular_cliente" ) );
                clienteInfo.setTelefone1_cliente( Jsondados.getString( "telefone1_cliente" ) );
                clienteInfo.setTelefone2_cliente( Jsondados.getString( "telefone2_cliente" ) );
                String verificacao_cliente = Jsondados.getString( "verificacao_cliente" );
                clienteInfo.setMaster_cliente( Jsondados.getString( "master_cliente" ) );

                if (clienteInfo.getDocumento_cliente().equals("")) clienteInfo.setDocumento_cliente( "null" );



                /*if(clienteInfo.getEmail_cliente().equals(USER) && clienteInfo.getSenha_cliente().equals(PASSWORD) && clienteInfo.getTipo_pessoa_cliente().equals("PF")){

                    Intent i = new Intent( this,MainCliente.class );
                    startActivity( i );
                    pd.dismiss();


                }*/
                if(clienteInfo.getEmail_cliente().equals(USER) && clienteInfo.getSenha_cliente().equals(PASSWORD)
                        && clienteInfo.getTipo_pessoa_cliente().equals("PJ") && verificacao_cliente.equals( "ON" ))
                {
                    Intent i = new Intent( this,MainClientePJ.class );
                    startActivity( i );
                    pd.dismiss();
                }
                else if(verificacao_cliente.equals( "OFF" ))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Já estamos validando seus dados");
                    builder.setMessage("Seu cadastro ainda não foi verificado, aguarde nosso contato");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    alerta = builder.create();
                    alerta.show();
                    pd.dismiss();

                }
            }else{
                Toast.makeText(this,estado,Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        } catch (JSONException e) {

        }
    }

    /*private void LogarEspecialista()
    {
        HashMap<String,String> hashMapToken = new HashMap<>(  );
        hashMapToken.put( "login",USER);
        hashMapToken.put( "senha",PASSWORD);

        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, urlEspecialista, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados) {

                VerificarPasswordEspecialista( dados );
                Log.i("COEEEEEEEEEEEEEEE", String.valueOf( dados ) );

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(Login.this, "Ocorreu algum erro",Toast.LENGTH_LONG).show();
                Log.i("COEEEEEEEEEEEEEEE", String.valueOf( error ) );


            }
        });
        VolleyRP.addToQueue(solicitar,mRequest,this,volley);

    }

    public void VerificarPasswordEspecialista(JSONObject dados){
        try {

            String estado = dados.getString("resultado");
            if(estado.equals("CC")){
                JSONObject Jsondados = new JSONObject(dados.getString("dados"));
                String login = Jsondados.getString("email_especialista");
                String senha = Jsondados.getString("senha_especialista");
                String verificacao = Jsondados.getString( "verificacao_especialista" );

                if(login.equals(USER) && senha.equals(PASSWORD) && verificacao.equals( "ON" ))
                {
                    Intent i = new Intent( this,MainEspecialista.class );
                    startActivity( i );
                    pd.dismiss();

                }
                else if (!login.equals( USER ) || !senha.equals( PASSWORD ) && verificacao.equals( "ON" ))
                {
                    pd.dismiss();
                    Toast.makeText( this, "Login ou senha incorretos", Toast.LENGTH_SHORT ).show();
                }
                else if (login.equals(USER) && senha.equals(PASSWORD) && verificacao.equals( "OFF" ))
                {
                    pd.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Você ainda não pode se logar até que seu cadastro esteja verificado. Por favor, aguarde contato de um de nossos representantes");
                    builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    } );
                    alerta = builder.create();
                    alerta.show();

                }
            }
        } catch (JSONException e) {

        }
    }*/


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


}
