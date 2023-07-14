package com.mobitech.eletromation.CriarConta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mobitech.eletromation.R;

import java.util.concurrent.TimeUnit;

public class EnviarSMSCadastroEspecialista extends AppCompatActivity {

    EditText txtCelularEspecialista,txtConfirmarCodigoEspecialista;
    Button btnConfirmarCelularEspecialista,btnConfirmarCodigoEspecialista;

    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    public String verificationCode;
    public static String numeroEspecialista = "+55";
    ProgressDialog pd,pd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_enviar_smscadastro_especialista );

        txtCelularEspecialista = (EditText) findViewById( R.id.txtCelularEspecialista );
        btnConfirmarCelularEspecialista = (Button) findViewById( R.id.btnConfirmarCelularEspecialista );
        btnConfirmarCodigoEspecialista = (Button) findViewById( R.id.btnConfirmarCodigoEspecialista );
        btnConfirmarCodigoEspecialista.setEnabled( false );
        txtConfirmarCodigoEspecialista = (EditText) findViewById( R.id.txtConfirmarCodigoEspecialista );
        txtConfirmarCodigoEspecialista.setEnabled( false );

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
                verificationCode = s;
                Toast.makeText( EnviarSMSCadastroEspecialista.this, "Codigo enviado ao seu número, verifique suas mensagens e digite-o na caixa abaixo", Toast.LENGTH_LONG ).show();
                pd2.dismiss();
                txtConfirmarCodigoEspecialista.setEnabled( true );
                btnConfirmarCodigoEspecialista.setEnabled( true );
                txtCelularEspecialista.setEnabled( false );
                btnConfirmarCelularEspecialista.setEnabled( false );
            }
        };
    }

    public void EnviarSms(View v)
    {
        String numero = txtCelularEspecialista.getText().toString();
        numero = numero.replaceAll("\\(","").replaceAll("\\)","").replace("-","");
        numeroEspecialista += numero;
        pd2 = new ProgressDialog(EnviarSMSCadastroEspecialista.this);
        pd2.setMessage("Espere...");
        pd2.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber( numeroEspecialista,120,TimeUnit.SECONDS,this,mCallback );

    }



    public void verify(View v)
    {
        String input_code = txtConfirmarCodigoEspecialista.getText().toString();
        if(!verificationCode.equals( "" ))
        {
            verifyPhoneNumber(verificationCode,input_code);
        }

    }

    public void verifyPhoneNumber(String verifyCode,String inputCode)
    {
        pd = new ProgressDialog(EnviarSMSCadastroEspecialista.this);
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
                            Toast.makeText( EnviarSMSCadastroEspecialista.this, "Digite seus dados", Toast.LENGTH_SHORT ).show();
                            Intent i = new Intent( EnviarSMSCadastroEspecialista.this,CriarContaEspecialista.class );
                            startActivity( i );
                            finish();
                        }
                        else {
                            pd.dismiss();
                            Toast.makeText( EnviarSMSCadastroEspecialista.this, "Verifique o codigo e tente novamente", Toast.LENGTH_LONG ).show();
                        }
                    }
                } );

    }
}
