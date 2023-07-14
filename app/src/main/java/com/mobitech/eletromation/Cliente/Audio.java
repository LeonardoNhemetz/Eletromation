package com.mobitech.eletromation.Cliente;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobitech.eletromation.ClienteInfo;
import com.mobitech.eletromation.Login;
import com.mobitech.eletromation.R;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Audio extends AppCompatActivity {
    private double finalTime = 0;
    Button btnGravar,btnParar,btnEnviar;
    String pathSave = "";
    MediaRecorder mRecorder;
    String mFileName = null;

    int MAX_DURATION = 360000;

    StorageReference mStorage;
    ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_audio );

        mStorage = FirebaseStorage.getInstance().getReference();

        btnGravar = (Button) findViewById( R.id.btnGravar );
        btnParar = (Button) findViewById( R.id.btnParar );
        btnEnviar = (Button) findViewById( R.id.btnEnviar );
        btnParar.setEnabled( false );
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/record_audio.3gp";



        btnParar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                stopRecording();

            }
        } );


        btnGravar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startRecording();
                Toast.makeText( Audio.this, "GRAVANDO...", Toast.LENGTH_LONG ).show();
                btnGravar.setEnabled( false );
                btnParar.setEnabled( true );
            }
        } );

        btnEnviar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAudio();
            }
        } );

    }

    private void startRecording()
    {
        btnParar.setEnabled( true );
        mRecorder = new MediaRecorder();
        mRecorder.setMaxDuration( MAX_DURATION );
        mRecorder.setAudioSource( MediaRecorder.AudioSource.MIC );
        mRecorder.setOutputFormat( MediaRecorder.OutputFormat.THREE_GPP );
        mRecorder.setOutputFile( mFileName );
        mRecorder.setAudioEncoder( MediaRecorder.AudioEncoder.AMR_NB );



        try
        {
            mRecorder.prepare();
        } catch (IOException e)
        {

        }

        mRecorder.start();
    }

    private void stopRecording()
    {
        Toast.makeText( Audio.this, "OUVINDO...", Toast.LENGTH_LONG ).show();
        mRecorder.stop();

        mRecorder.release();

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {

            mediaPlayer.setDataSource( mFileName );
            mediaPlayer.prepare();
            mediaPlayer.start();
            btnParar.setEnabled( false );



        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void uploadAudio()
    {
        pd.setMessage( "Enviando Audio..." );
        pd.show();

        ClienteInfo clienteInfo = new ClienteInfo();
        CriarPedidoCliente criarPedidoCliente = new CriarPedidoCliente();

        StorageReference filepath = mStorage.child("Arquivos de Pedidos").child( clienteInfo.getEmail_cliente() ).child( "Audio do Pedido: "+criarPedidoCliente.ProximoPedido );
        Uri uri = Uri.fromFile( new File( mFileName ) );

        filepath.putFile( uri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                pd.dismiss();
                Toast.makeText( Audio.this, "GRAVADO E ENVIADO", Toast.LENGTH_SHORT ).show();
                finish();


            }
        } );
    }





}
