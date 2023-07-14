package com.mobitech.eletromation.Cliente;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobitech.eletromation.ClienteInfo;
import com.mobitech.eletromation.Email.GmailSender;
import com.mobitech.eletromation.R;
import com.mobitech.eletromation.VolleyRP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CriarOcorrencia extends AppCompatActivity {

    private RequestQueue mRequest;
    private VolleyRP volley;

    private Button btnFotoOCO,btnAudioOCO,btnEnviarOCO;
    private EditText txtInfoOCO;
    private RecyclerView recViewFotoOCO;

    private ProgressDialog pd;
    private ProgressDialog pd2;

    private static String EmailPara = "leonardonhemetz@hotmail.com";
    //private static String EmailPara = "thiagorodriguesgj@gmail.com";

    private static final String url= "http://iurdcom.ddns.net:1024/eletromation/login_cadastrarocorrencia.php";
    private static final String urlJSON = "http://iurdcom.ddns.net:1024/eletromation/teste.php";
    private StorageReference mStorage;
    public static int ProximoPedido;
    private static final int RESULT_LOAD_IMAGE1 = 1;
    private List<String> fileNameList;
    private List<String> fileDoneList;
    private UploadListAdapter uploadListAdapter;
    private static String Info = "",Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_criar_ocorrencia );

        pd2 = new ProgressDialog( this );

        volley = VolleyRP.getInstance( this );
        mRequest = volley.getRequestQueue();

        btnFotoOCO = (Button) findViewById( R.id.btnFotoOCO );
        btnAudioOCO = (Button) findViewById( R.id.btnAudioOCO );
        btnEnviarOCO = (Button) findViewById( R.id.btnEnviarOCO );
        txtInfoOCO = (EditText) findViewById( R.id.txtInfoPedidoOCO );
        recViewFotoOCO = (RecyclerView) findViewById( R.id.recViewFotoOCO );

        fileNameList = new ArrayList<>(  );
        fileDoneList = new ArrayList<>(  );
        uploadListAdapter = new UploadListAdapter( fileNameList,fileDoneList );

        recViewFotoOCO.setLayoutManager( new LinearLayoutManager( this ) );
        recViewFotoOCO.setHasFixedSize( true );
        recViewFotoOCO.setAdapter( uploadListAdapter );


        mStorage = FirebaseStorage.getInstance().getReference();

        final SendEmailTask sendEmailTask = new SendEmailTask();

        GetProximoPedido();

        btnFotoOCO.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType( "image/*" );
                intent.putExtra( Intent.EXTRA_ALLOW_MULTIPLE,true );
                intent.setAction( Intent.ACTION_GET_CONTENT );
                startActivityForResult(intent.createChooser( intent,"Selecione até 3 fotos" ),RESULT_LOAD_IMAGE1);
            }
        } );

        btnAudioOCO.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( CriarOcorrencia.this,Audio.class );
                startActivity( intent );

            }
        } );

        btnEnviarOCO.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClienteInfo clienteInfo = new ClienteInfo();
                if(!txtInfoOCO.getText().toString().equals( "" )) Info = txtInfoOCO.getText().toString();
                else Info = "NULL";

                Text = "Uma nova ocorrencia foi registrada!\n\n" +
                        "Email: "+clienteInfo.getEmail_cliente()+"\n" +
                        "Nome: "+clienteInfo.getNome_cliente()+"\n" +
                        "Informação: "+Info;

                pd = new ProgressDialog(CriarOcorrencia.this);
                pd.setTitle("Enviando pedido");
                pd.setMessage("Espere...");
                pd.show();
                sendEmailTask.execute(  );

                CadastrarPedido( clienteInfo.getNome_cliente(), clienteInfo.getEmail_cliente(),clienteInfo.getCelular_cliente(),Info,clienteInfo.getDocumento_cliente() );

            }
        } );
    }

    private void GetProximoPedido()
    {
        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.GET, urlJSON,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados) {

                try{
                    String UltimoPedido = dados.getString("MAX(id_pedido)");
                    if(UltimoPedido == "null")
                    {
                        UltimoPedido = "0";
                        ProximoPedido = Integer.parseInt( UltimoPedido )+ 1;
                    }
                    else {
                        ProximoPedido = Integer.parseInt( UltimoPedido )+ 1;
                        Log.i("COEEEEEEEEEEEEEEE", String.valueOf( ProximoPedido ) );
                    }


                }catch (JSONException e)
                {

                }


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CriarOcorrencia.this, "Ocorreu algum erro",Toast.LENGTH_LONG).show();
                Log.i("COEEEEEEEEEEEEEEE", String.valueOf( error ) );


            }
        });
        VolleyRP.addToQueue(solicitar,mRequest,this,volley);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        ClienteInfo clienteInfo = new ClienteInfo();

        if(requestCode == RESULT_LOAD_IMAGE1 && resultCode == RESULT_OK)
        {
            if(data.getData() != null)
            {
                //Selecionado somente 1 foto

                Uri fileUri = data.getData();
                String fileName = getFileName( fileUri );
                fileNameList.add( fileName );

                uploadListAdapter.notifyDataSetChanged();

                pd2.setMessage( "Enviando Foto(s)...");
                pd2.show();


                StorageReference filepath = mStorage.child("Arquivos de Pedidos").child( clienteInfo.getEmail_cliente() ).child( "Foto do Pedido: "+ProximoPedido+ " (0)");
                filepath.putFile( fileUri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd2.dismiss();



                    }
                } );

            }
            else if (data.getClipData() != null)
            {
                //Selecionado mais de 1 foto
                int totalItemsSelected = data.getClipData().getItemCount();

                if (totalItemsSelected >= 3)
                {
                    for(int i = 0; i < totalItemsSelected; i++)
                    {
                        Uri fileUri = data.getClipData().getItemAt( i ).getUri();
                        String fileName = getFileName( fileUri );
                        fileNameList.add( fileName );

                        uploadListAdapter.notifyDataSetChanged();

                        pd2.setMessage( "Enviando Foto(s)...");
                        pd2.show();


                        StorageReference filepath = mStorage.child("Arquivos de Pedidos").child( clienteInfo.getEmail_cliente() ).child( "Foto do Pedido: "+ProximoPedido+ " ("+i+")");
                        filepath.putFile( fileUri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                pd2.dismiss();



                            }
                        } );

                    }


                }
                else Toast.makeText( this, "SELECIONE ATÉ 3 FOTOS", Toast.LENGTH_LONG ).show();

            }

           /* pd2.setMessage( "Enviando Foto..." );
            pd2.show();

            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("Arquivos de Pedidos").child( clienteInfo.getEmail_cliente() ).child( "Foto do Pedido: "+ProximoPedido );
            filepath.putFile( uri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText( CriarPedidoCliente.this, "ENVIADO COM SUCESSO", Toast.LENGTH_SHORT ).show();
                    pd2.dismiss();


                }
            } ); */

        }
    }

    public String getFileName (Uri uri)
    {
        String result = null;

        if(uri.getScheme().equals( "content" ))
        {
            Cursor cursor = getContentResolver().query( uri,null,null,null,null );
            try
            {
                if (cursor != null && cursor.moveToFirst())
                {
                    result = cursor.getString( cursor.getColumnIndex( OpenableColumns.DISPLAY_NAME ) );

                }
            }finally {
                cursor.close();
            }
        }
        if (result == null)
        {
            result = uri.getPath();
            int cut = result.lastIndexOf( '/' );
            if(cut != 1)
            {
                result = result.substring( cut + 1 );
            }
        }
        return result;
    }

    private void CadastrarPedido(String Nome, String Email, String Contato, String Info, String Doc) {

        HashMap<String, String> hashMapToken = new HashMap<>();
         hashMapToken.put( "nome_cliente", Nome );
        hashMapToken.put( "email_cliente", Email );
        hashMapToken.put( "celular_cliente", Contato );
        hashMapToken.put( "info_pedido", Info );
        hashMapToken.put( "documento_cliente", Doc );


        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, url, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados) {

                Toast.makeText( CriarOcorrencia.this, "Pedido cadastrado", Toast.LENGTH_SHORT ).show();
                pd.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText( CriarOcorrencia.this, "Ocorreu algum erro", Toast.LENGTH_SHORT ).show();
                Log.i( "COEEEEEEEEEEEEEEE", String.valueOf( error ) );


            }
        } );
        VolleyRP.addToQueue( solicitar, mRequest, this, volley );
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
                sender.sendMail("Ocorrência",
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
