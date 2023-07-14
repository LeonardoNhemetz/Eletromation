package com.mobitech.eletromation.CriarConta;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mobitech.eletromation.MudarSenha;
import com.mobitech.eletromation.R;
import com.mobitech.eletromation.VolleyRP;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CriarContaCliente extends AppCompatActivity {

    EditText txtEmailCliente,txtSenhaCliente,txtNomeCliente,txtRazSoc,txtNomeFant,txtCnpj;

    Button btnCadastrarCliente;

    //private RadioGroup RadioGroupCliente;

    private RequestQueue mRequest;
    private VolleyRP volley;
    ProgressDialog pd;

    private static final String url = "http://iurdcom.ddns.net:1024/eletromation/login_cadastrarcliente.php";

    String staticTipoPessoaCliente = "PJ", CelularCliente = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_criar_conta_cliente );

        txtEmailCliente = (EditText) findViewById( R.id.txtEmailCliente );
        txtSenhaCliente = (EditText) findViewById( R.id.txtSenhaCliente );
        txtNomeCliente = (EditText) findViewById( R.id.txtNomeCliente );
        txtRazSoc = (EditText) findViewById( R.id.txtRazSoc );
        txtNomeFant = (EditText) findViewById( R.id.txtNomeFant );
        txtCnpj = (EditText) findViewById( R.id.txtCnpj );

        volley = VolleyRP.getInstance( this );
        mRequest = volley.getRequestQueue();

       /* RadioGroupCliente = (RadioGroup) findViewById( R.id.RadioGroup );
        RadioGroupCliente.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById( checkedId );

                if(radioButton.getText().toString().equals( "FÍSICA" ))
                {
                    staticTipoPessoaCliente = "PF";


                }
                else if(radioButton.getText().toString().equals( "JURÍDICA" ))
                {
                    staticTipoPessoaCliente = "PJ";
                }
            }
        } );*/



        btnCadastrarCliente = (Button) findViewById( R.id.btnCadastrarCliente );
        btnCadastrarCliente.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String EmailCliente = "",SenhaCliente = "",TipoPessoaCliente = "",NomeCliente = "",CelularCliente = "",
                        RazSoc = "",NomeFant = "",cnpj = "";

                EmailCliente = txtEmailCliente.getText().toString();
                SenhaCliente = txtSenhaCliente.getText().toString();
                TipoPessoaCliente = staticTipoPessoaCliente;
                NomeCliente = txtNomeCliente.getText().toString();
                EnviarSMSCadastroCliente enviarSMSCadastroCliente = new EnviarSMSCadastroCliente();
                CelularCliente = enviarSMSCadastroCliente.numeroCliente;
                RazSoc = txtRazSoc.getText().toString();
                NomeFant = txtNomeFant.getText().toString();
                cnpj = txtCnpj.getText().toString();





                if(EmailCliente.equals( "" ))Toast.makeText( CriarContaCliente.this, "Você esqueceu de digitar seu E-mail", Toast.LENGTH_SHORT ).show();
                if(SenhaCliente.equals( "" ))Toast.makeText( CriarContaCliente.this, "Você esqueceu de digitar sua senha", Toast.LENGTH_SHORT ).show();
                if(TipoPessoaCliente.equals( "" ))Toast.makeText( CriarContaCliente.this, "Você esqueceu de marcar o tipo de documento", Toast.LENGTH_SHORT ).show();
                if(NomeCliente.equals( "" ))Toast.makeText( CriarContaCliente.this, "Você esqueceu de digitar seu Nome", Toast.LENGTH_SHORT ).show();
                if(RazSoc.equals( "" ))Toast.makeText( CriarContaCliente.this, "Você esqueceu de digitar a razão social", Toast.LENGTH_SHORT ).show();
                if(NomeFant.equals( "" ))Toast.makeText( CriarContaCliente.this, "Você esqueceu de digitar o nome fantasia", Toast.LENGTH_SHORT ).show();
                if(cnpj.equals( "" ))Toast.makeText( CriarContaCliente.this, "Você esqueceu de digitar o cnpj", Toast.LENGTH_SHORT ).show();

                else CadastrarCliente( EmailCliente,SenhaCliente,TipoPessoaCliente,NomeCliente,CelularCliente,RazSoc,NomeFant,cnpj);

                pd = new ProgressDialog(CriarContaCliente.this);
                pd.setTitle("Cadastrando");
                pd.setMessage("Espere...");
                pd.show();



            }
        } );


    }

    private void CadastrarCliente(String EmailCliente,String SenhaCliente,String TipoPessoaCliente,
                                  String NomeCliente,String CelularCliente,String RazSoc, String NomeFant, String cnpj )
    {
        HashMap<String,String> hashMapToken = new HashMap<>(  );
        hashMapToken.put( "email_cliente",EmailCliente);
        hashMapToken.put( "senha_cliente",SenhaCliente);
        hashMapToken.put( "tipo_pessoa_cliente",TipoPessoaCliente);
        hashMapToken.put( "nome_cliente",NomeCliente);
        hashMapToken.put( "celular_cliente",CelularCliente);
        hashMapToken.put( "raz_social_cliente",RazSoc);
        hashMapToken.put( "nome_fantasia_cliente",NomeFant);
        hashMapToken.put( "documento_cliente",cnpj);


        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, url, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados) {

                try
                {
                    String estado = dados.getString("resultado");
                    if (estado.equals( "Cadastro ja existente!" ))
                    {
                        Toast.makeText( CriarContaCliente.this, "Cadastro já existente!", Toast.LENGTH_LONG ).show();
                        pd.dismiss();
                        finish();
                    }


                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
                finish();


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                pd.dismiss();

                Toast.makeText(CriarContaCliente.this, "Ocorreu algum erro",Toast.LENGTH_LONG).show();
                Log.i("COEEEEEEEEEEEEEEE",error.getMessage() );


            }
        });
        VolleyRP.addToQueue(solicitar,mRequest,this,volley);

    }

    public void VerificarPasswordCliente(JSONObject dados){
        try {
            String estado = dados.getString("resultado");
            if(estado.equals("CC")){
                JSONObject Jsondados = new JSONObject(dados.getString("dados"));
                Toast.makeText( this, Jsondados.toString(), Toast.LENGTH_SHORT ).show();

            }else{
                Toast.makeText(this,estado,Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {

        }
    }


}
