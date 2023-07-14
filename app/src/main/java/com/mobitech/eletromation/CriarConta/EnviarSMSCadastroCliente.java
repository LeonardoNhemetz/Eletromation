package com.mobitech.eletromation.CriarConta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mobitech.eletromation.Login;
import com.mobitech.eletromation.R;
import com.mobitech.eletromation.VolleyRP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class EnviarSMSCadastroCliente extends AppCompatActivity {

    EditText txtCelularCliente,txtConfirmarCodigo;
    Button btnConfirmarCelular,btnConfirmarCodigo;
    private RequestQueue mRequest;
    private VolleyRP volley;
    private static final String URL = "http://iurdcom.ddns.net:1024/eletromation/procurarCelular.php";

    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    public String verificationCode;
    public static String numeroCliente = "+55";
    public static String numeroClienteCompleto;
    ProgressDialog pd,pd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_enviar_sms_cadastro_cliente );

        txtCelularCliente = (EditText) findViewById( R.id.txtCelularCliente );
        btnConfirmarCelular = (Button) findViewById( R.id.btnConfirmarCelular );
        btnConfirmarCodigo = (Button) findViewById( R.id.btnConfirmarCodigo );
        btnConfirmarCodigo.setEnabled( false );
        txtConfirmarCodigo = (EditText) findViewById( R.id.txtConfirmarCodigo );
        txtConfirmarCodigo.setEnabled( false );

        volley = VolleyRP.getInstance( this );
        mRequest = volley.getRequestQueue();
        auth = FirebaseAuth.getInstance();


        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent( s, forceResendingToken );
                SmsReceiver.bindListener( new SmsListener() {
                    @Override
                    public void messageReceived(String messageText) {
                        txtConfirmarCodigo.setText( messageText );
                    }
                });

                verificationCode = s;
                Toast.makeText( EnviarSMSCadastroCliente.this, "Codigo enviado ao seu número. Em breve ele aparecerá na caixa de texto abaixo", Toast.LENGTH_LONG ).show();
                pd2.dismiss();
                txtConfirmarCodigo.setEnabled( true );
                btnConfirmarCodigo.setEnabled( true );
                txtCelularCliente.setEnabled( false );
                btnConfirmarCelular.setEnabled( false );
            }
        };






    }

    public void EnviarSms(View v)
    {
        String numero = txtCelularCliente.getText().toString();
        numero = numero.replaceAll("\\(","").replaceAll("\\)","").replace("-","");
        numeroCliente += numero;
        numeroClienteCompleto = numeroCliente;
        pd2 = new ProgressDialog(EnviarSMSCadastroCliente.this);
        pd2.setMessage("Espere...");
        pd2.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber( numeroClienteCompleto,120,TimeUnit.SECONDS,this,mCallback );

    }



    public void verify(View v)
    {
        String input_code = txtConfirmarCodigo.getText().toString();
        if(!verificationCode.equals( "" ))
        {
            verifyPhoneNumber(verificationCode,input_code);
        }

    }

    public void verifyPhoneNumber(String verifyCode,String inputCode)
    {
        pd = new ProgressDialog(EnviarSMSCadastroCliente.this);
        pd.setMessage("Espere...");
        pd.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential( verifyCode,inputCode );
        signWithPhone( credential );

    }

    public void signWithPhone(PhoneAuthCredential credential)
    {
        auth.signInWithCredential( credential )
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            pd.dismiss();
                            verificarCelular();

                        }
                        else {
                            pd.dismiss();
                            Toast.makeText( EnviarSMSCadastroCliente.this, "Verifique o codigo e tente novamente", Toast.LENGTH_LONG ).show();
                        }
                    }
                } );

    }

    private void verificarCelular()
    {
        HashMap<String,String> hashMapToken = new HashMap<>(  );
        Log.i( "NUMEROAQUIMEUMANO",numeroClienteCompleto );
        hashMapToken.put( "celular",numeroClienteCompleto);

        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, URL, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados) {

                try
                {
                    String TodosOsDados = dados.getString( "resultado" );
                    Log.i( "Coeeeeeeeeeeeee",TodosOsDados );


                    if(!TodosOsDados.equals( "false" ))
                    {
                        Toast.makeText( EnviarSMSCadastroCliente.this, "Este número ja está cadastrado", Toast.LENGTH_LONG ).show();
                        finish();
                    }
                    else
                    {
                        Intent i = new Intent( EnviarSMSCadastroCliente.this,CriarContaCliente.class );
                        startActivity( i );
                        finish();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();



            }
        });
        VolleyRP.addToQueue(solicitar,mRequest,this,volley);


    }


}
