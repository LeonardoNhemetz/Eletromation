package com.mobitech.eletromation.CriarConta;

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
import com.mobitech.eletromation.Cliente.MainCliente;
import com.mobitech.eletromation.ClienteInfo;
import com.mobitech.eletromation.Email.GmailSender;
import com.mobitech.eletromation.Login;
import com.mobitech.eletromation.R;
import com.mobitech.eletromation.VolleyRP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CriarContaPJCliente extends AppCompatActivity {

    TextView txtEmailClientePJ,txtSenhaClientePJ,txtNomeClientePJ,txtCelularClientePJ;
    Button btnCadastrarClientePJ;
    private static final String urlget = "http://iurdcom.ddns.net:1024/eletromation/login_getcliente_porpj.php";
    private static final String urlinsert = "http://iurdcom.ddns.net:1024/eletromation/login_cadastrar_cliente_por_pj.php";
    ProgressDialog pd;
    private RequestQueue mRequest;
    private VolleyRP volley;
    public static String documento_cliente,raz_social_cliente,nome_fantasia_cliente,cep_cliente,endereco_cliente;
    private static String EmailPara = "leonardonhemetz@hotmail.com";
    //private static String EmailPara = "thiagorodriguesgj@gmail.com";
    static String Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_criar_conta_pjcliente );

        txtEmailClientePJ = (TextView) findViewById( R.id.txtEmailClientePJ );
        txtSenhaClientePJ = (TextView) findViewById( R.id.txtSenhaClientePJ );
        txtNomeClientePJ = (TextView) findViewById( R.id.txtNomeClientePJ );
        txtCelularClientePJ = (TextView) findViewById( R.id.txtCelularClientePJ );


        btnCadastrarClientePJ = (Button) findViewById( R.id.btnCadastrarClientePJ );

        volley = VolleyRP.getInstance( this );
        mRequest = volley.getRequestQueue();

        btnCadastrarClientePJ.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerificarContaPJ();
            }
        } );
    }

    public void VerificarContaPJ()
    {
        pd = new ProgressDialog(CriarContaPJCliente.this);
        pd.setTitle("Cadastrando");
        pd.setMessage("Espere...");
        pd.show();

        ClienteInfo clienteInfo = new ClienteInfo();
        HashMap<String,String> hashMapToken = new HashMap<>(  );
        hashMapToken.put( "login",clienteInfo.getEmail_cliente());
        hashMapToken.put( "senha",clienteInfo.getSenha_cliente());

        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, urlget, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados) {

                VerificarDados( dados );
                Log.i("COEEEEEEEEEEEEEEE", String.valueOf( dados ) );

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(CriarContaPJCliente.this, "Ocorreu algum erro",Toast.LENGTH_LONG).show();
                Log.i("COEEEEEEEEEEEEEEE", String.valueOf( error ) );


            }
        });
        VolleyRP.addToQueue(solicitar,mRequest,this,volley);
    }

    public void VerificarDados(JSONObject dados)
    {
        try {
            String estado = dados.getString("resultado");
            if(estado.equals("CC")){
                JSONObject Jsondados = new JSONObject(dados.getString("dados"));
                documento_cliente = Jsondados.getString( "documento_cliente" );
                raz_social_cliente = Jsondados.getString( "raz_social_cliente" );
                nome_fantasia_cliente = Jsondados.getString( "nome_fantasia_cliente" );
                cep_cliente = Jsondados.getString( "cep_cliente" );
                endereco_cliente = Jsondados.getString( "endereco_cliente" );

                String email = txtEmailClientePJ.getText().toString().toLowerCase();
                String senha = txtSenhaClientePJ.getText().toString();
                String nome = txtNomeClientePJ.getText().toString();
                String celular = txtCelularClientePJ.getText().toString();

                AdicionarCliente(email,senha,nome,celular,documento_cliente,raz_social_cliente,
                        nome_fantasia_cliente,cep_cliente,endereco_cliente);

                Text = "Uma nova conta de cliente PJ foi criada e precisa de aprovação! \n\n" +
                        "Email: "+email+"\n" +
                        "Nome: "+nome+"\n" +
                        "Celular: "+celular+"\n" +
                        "Razão social: "+raz_social_cliente+"\n" +
                        "Nome fantasia: "+nome_fantasia_cliente+"\n" +
                        "CEP: "+cep_cliente+"\n" +
                        "Endereço: "+endereco_cliente;

                SendEmailTask sendEmailTask = new SendEmailTask();
                sendEmailTask.execute(  );



            }

        } catch (JSONException e) {

        }
    }

    public void AdicionarCliente(String email,String senha,String nome,String celular,String documento_cliente,
                                 String raz_social_cliente,String nome_fantasia_cliente,String cep_cliente,String endereco_cliente)
    {

        HashMap<String,String> hashMapToken = new HashMap<>(  );
        hashMapToken.put( "email_cliente",email);
        hashMapToken.put( "senha_cliente",senha);
        hashMapToken.put( "nome_cliente",nome);
        hashMapToken.put( "celular_cliente",celular);
        hashMapToken.put( "documento_cliente",documento_cliente);
        hashMapToken.put( "raz_social_cliente",raz_social_cliente);
        hashMapToken.put( "nome_fantasia_cliente",nome_fantasia_cliente);
        hashMapToken.put( "cep_cliente",cep_cliente);
        hashMapToken.put( "endereco_cliente",endereco_cliente);

        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, urlinsert, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados) {

                Toast.makeText( CriarContaPJCliente.this, "Cadastrado com sucesso", Toast.LENGTH_LONG ).show();
                finish();
                pd.dismiss();


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(CriarContaPJCliente.this, "Ocorreu algum erro",Toast.LENGTH_LONG).show();
                Log.i("COEEEEEEEEEEEEEEE",error.getMessage() );
                finish();
                pd.dismiss();


            }
        });
        VolleyRP.addToQueue(solicitar,mRequest,this,volley);

    }

    class SendEmailTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                GmailSender sender = new GmailSender("exemplodoappeletromation@gmail.com", "eletromation2018");
                //subject, body, sender, to
                sender.sendMail("Criação de conta PJ",
                        Text,
                        EmailPara,EmailPara);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }



}
