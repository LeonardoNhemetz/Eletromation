package com.mobitech.eletromation.CriarConta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mobitech.eletromation.R;

public class CriarContaEscolha extends AppCompatActivity {

    Button btnCadastrarComoCliente,btnCadastrarComoEspecialista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_criar_conta );

        btnCadastrarComoCliente = (Button) findViewById( R.id.btnCadastrarComoCliente );
        btnCadastrarComoEspecialista = (Button) findViewById( R.id.btnCadastrarComoEspecialista );

        btnCadastrarComoCliente.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent( CriarContaEscolha.this,EnviarSMSCadastroCliente.class );
                startActivity( i );
                finish();

            }
        } );

        btnCadastrarComoEspecialista.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent( CriarContaEscolha.this,EnviarSMSCadastroEspecialista.class );
                startActivity( i );
                finish();

            }
        } );
    }
}
