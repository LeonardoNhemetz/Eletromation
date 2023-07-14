package com.mobitech.eletromation.CriarConta;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mobitech.eletromation.Login;
import com.mobitech.eletromation.R;
import com.mobitech.eletromation.VolleyRP;

import org.json.JSONObject;

import java.util.HashMap;

public class CriarContaEspecialista extends AppCompatActivity {

    EditText txtEmailEspecialista, txtSenhaEspecialista, txtDocumentoEspecialista, txtFuncaoEspecialista, txtNomeEspecialista, txtRazaoSocialEspecialista,
            txtNomeFantasiaEspecialista, txtCepEspecialista, txtEnderecoEspecialista;

    Button btnCadastrarEspecialista;
    ProgressDialog pd;

    private RequestQueue mRequest;
    private VolleyRP volley;

    private static final String url = "http://iurdcom.ddns.net:1024/eletromation/login_cadastrarespecialista.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_criar_conta_especialista );

        txtEmailEspecialista = (EditText) findViewById( R.id.txtEmailEspecialista );
        txtSenhaEspecialista = (EditText) findViewById( R.id.txtSenhaEspecialista );
        txtDocumentoEspecialista = (EditText) findViewById( R.id.txtDocumentoEspecialista );
        txtNomeEspecialista = (EditText) findViewById( R.id.txtNomeEspecialista );
        txtFuncaoEspecialista = (EditText) findViewById( R.id.txtFuncaoEspecialista );
        txtRazaoSocialEspecialista = (EditText) findViewById( R.id.txtRazaoSocialEspecialista );
        txtNomeFantasiaEspecialista = (EditText) findViewById( R.id.txtNomeFantasiaEspecialista );
        txtCepEspecialista = (EditText) findViewById( R.id.txtCepEspecialista );
        txtEnderecoEspecialista = (EditText) findViewById( R.id.txtEnderecoEspecialista );


        volley = VolleyRP.getInstance( this );
        mRequest = volley.getRequestQueue();

        btnCadastrarEspecialista = (Button) findViewById( R.id.btnCadastrarEspecialista );
        btnCadastrarEspecialista.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EmailEspecialista = "", SenhaEspecialista = "", DocumentoEspecialista = "", FuncaoEspecialista = "", NomeEspecialista = "",
                        RazaoSocialEspecialista = "", NomeFantasiaEspecialista = "", CepEspecialista = "",
                        EnderecoEspecialista = "", CelularEspecialista = "";

                EnviarSMSCadastroEspecialista enviarSMSCadastroEspecialista = new EnviarSMSCadastroEspecialista();

                EmailEspecialista = txtEmailEspecialista.getText().toString();
                SenhaEspecialista = txtSenhaEspecialista.getText().toString();
                DocumentoEspecialista = txtDocumentoEspecialista.getText().toString();
                NomeEspecialista = txtNomeEspecialista.getText().toString();
                FuncaoEspecialista = txtFuncaoEspecialista.getText().toString();
                RazaoSocialEspecialista = txtRazaoSocialEspecialista.getText().toString();
                NomeFantasiaEspecialista = txtNomeFantasiaEspecialista.getText().toString();
                CepEspecialista = txtCepEspecialista.getText().toString();
                EnderecoEspecialista = txtEnderecoEspecialista.getText().toString();
                CelularEspecialista = enviarSMSCadastroEspecialista.numeroEspecialista;


                if (EmailEspecialista.equals( "" ))
                    Toast.makeText( CriarContaEspecialista.this, "Você esqueceu de digitar seu E-mail", Toast.LENGTH_SHORT ).show();
                if (SenhaEspecialista.equals( "" ))
                    Toast.makeText( CriarContaEspecialista.this, "Você esqueceu de digitar sua senha", Toast.LENGTH_SHORT ).show();
                if (DocumentoEspecialista.equals( "" ))
                    Toast.makeText( CriarContaEspecialista.this, "Você esqueceu de marcar digitar o seu dcumento", Toast.LENGTH_SHORT ).show();
                if (NomeEspecialista.equals( "" ))
                    Toast.makeText( CriarContaEspecialista.this, "Você esqueceu de digitar seu Nome", Toast.LENGTH_SHORT ).show();
                if (RazaoSocialEspecialista.equals( "" ))
                    Toast.makeText( CriarContaEspecialista.this, "Você esqueceu de a Razão Social de sua empresa", Toast.LENGTH_SHORT ).show();
                if (NomeFantasiaEspecialista.equals( "" ))
                    Toast.makeText( CriarContaEspecialista.this, "Você esqueceu de digitar o Nome Fantasia de sua empresa", Toast.LENGTH_SHORT ).show();
                if (CepEspecialista.equals( "" ))
                    Toast.makeText( CriarContaEspecialista.this, "Você esqueceu de digitar seu CEP", Toast.LENGTH_SHORT ).show();
                if (EnderecoEspecialista.equals( "" ))
                    Toast.makeText( CriarContaEspecialista.this, "Você esqueceu de digitar seu Endereço", Toast.LENGTH_SHORT ).show();
                if (CelularEspecialista.equals( "" ))
                    Toast.makeText( CriarContaEspecialista.this, "Você esqueceu de digitar seu Celular", Toast.LENGTH_SHORT ).show();

                CadastrarEspecialista( EmailEspecialista, SenhaEspecialista, DocumentoEspecialista, FuncaoEspecialista, NomeEspecialista, RazaoSocialEspecialista, NomeFantasiaEspecialista,
                        CepEspecialista, EnderecoEspecialista, CelularEspecialista);
            }


        } );
    }

    private void CadastrarEspecialista(String EmailEspecialista, String SenhaEspecialista, String DocumentoEspecialista, String FuncaoEspecialista,
                                       String NomeEspecialista, String RazaoSocialEspecialista, String NomeFantasiaEspecialista,
                                       String CepEspecialista, String EnderecoEspecialista, String CelularEspecialista) {

        pd = new ProgressDialog(CriarContaEspecialista.this);
        pd.setMessage("Criando conta, espere...");
        pd.show();
        HashMap<String, String> hashMapToken = new HashMap<>();
        hashMapToken.put( "email_especialista", EmailEspecialista );
        hashMapToken.put( "senha_especialista", SenhaEspecialista );
        hashMapToken.put( "cnpj_especialista", DocumentoEspecialista );
        hashMapToken.put( "funcao_especialista", FuncaoEspecialista );
        hashMapToken.put( "nome_especialista", NomeEspecialista );
        hashMapToken.put( "raz_social_especialista", RazaoSocialEspecialista );
        hashMapToken.put( "nome_fantasia_especialista", NomeFantasiaEspecialista );
        hashMapToken.put( "cep_especialista", CepEspecialista );
        hashMapToken.put( "endereco_especialista", EnderecoEspecialista );
        hashMapToken.put( "celular_especialista", CelularEspecialista );


        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, url, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados) {

                pd.dismiss();
                Toast.makeText( CriarContaEspecialista.this, "Cadastrado com sucesso", Toast.LENGTH_LONG ).show();
                finish();


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(CriarContaEspecialista.this, "Ocorreu algum erro",Toast.LENGTH_LONG).show();
                Log.i("COEEEEEEEEEEEEEEE",error.getMessage() );


            }
        });
        VolleyRP.addToQueue(solicitar,mRequest,this,volley);
    }
}