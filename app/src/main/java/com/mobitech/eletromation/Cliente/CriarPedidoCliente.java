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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CriarPedidoCliente extends AppCompatActivity {

    private String[] TipoDeServico = new String[]{"Automação","Civil","Climatização","Elétrico","Pequenos Reparos",
            "Pintura","Refrigeração"};
    //private String[] Precos = new String[]{"R$ 10,00","R$ 20,00","R$ 30,00","R$ 40,00","R$ 50,00"};
    private Spinner sp;
    private EditText txtEstimativa,txtData,txtData2,txtData3,txtInfo;
    RecyclerView recViewFoto;
    private Button btnEnviar,btnFoto,btnAudio;
    private static String TipoServico,Horario,Horario2 = "null",Horario3 = "null",Info,Data,Data2,Data3,Text;
    private static String EmailPara = "leonardonhemetz@hotmail.com";
    //private static String EmailPara = "thiagorodriguesgj@gmail.com";

    private static String subject = "Pedido";
    private RadioGroup RadioGroup,RadioGroup2,RadioGroup3;
    private RadioButton RadioButtonManha,RadioButtonTarde;
    ProgressDialog pd;
    ProgressDialog pd2;
    private RequestQueue mRequest;
    private VolleyRP volley;
    private static final String url= "http://iurdcom.ddns.net:1024/eletromation/login_cadastrarpedido.php";
    public static final String urlInfo = "http://iurdcom.ddns.net:1024/eletromation/login_getclienteinfo.php";
    private StorageReference mStorage;
    private static final int GALLERY_INTENT = 2;
    private static final String urlJSON = "http://iurdcom.ddns.net:1024/eletromation/teste.php";
    public static int ProximoPedido;
    private static final int RESULT_LOAD_IMAGE1 = 1;
    private List<String> fileNameList;
    private List<String> fileDoneList;
    private UploadListAdapter uploadListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_criar_pedido_cliente );


        pd2 = new ProgressDialog( this );

        volley = VolleyRP.getInstance( this );
        mRequest = volley.getRequestQueue();

        GetProximoPedido();

        recViewFoto = (RecyclerView) findViewById( R.id.recViewFoto );

        fileNameList = new ArrayList<>(  );
        fileDoneList = new ArrayList<>(  );
        uploadListAdapter = new UploadListAdapter( fileNameList,fileDoneList );

        recViewFoto.setLayoutManager( new LinearLayoutManager( this ) );
        recViewFoto.setHasFixedSize( true );
        recViewFoto.setAdapter( uploadListAdapter );


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, TipoDeServico  );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        sp = (Spinner) findViewById( R.id.spinner );
        sp.setAdapter( adapter );

        txtData = (EditText) findViewById( R.id.txtData );
        txtData2 = (EditText) findViewById( R.id.txtData2 );
        txtData3 = (EditText) findViewById( R.id.txtData3 );
        txtInfo = (EditText) findViewById( R.id.txtInfoPedido );



        final SendEmailTask sendEmailTask = new SendEmailTask();



        sp.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
               //txtEstimativa.setText( Precos[position] );
                //Preco = Precos[position];
                TipoServico = TipoDeServico[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );

        RadioGroup = (RadioGroup) findViewById( R.id.RadioGroup );
        RadioGroup.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById( checkedId );
                Horario = radioButton.getText().toString();

            }
        } );
        RadioGroup2 = (RadioGroup) findViewById( R.id.RadioGroup2 );
        RadioGroup2.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById( checkedId );
                Horario2 = radioButton.getText().toString();
            }

        } );
        RadioGroup3 = (RadioGroup) findViewById( R.id.RadioGroup3 );
        RadioGroup3.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById( checkedId );
                Horario3 = radioButton.getText().toString();
            }
        } );

        mStorage = FirebaseStorage.getInstance().getReference();

        btnFoto = (Button) findViewById( R.id.btnFoto );
        btnFoto.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType( "image/*" );
                intent.putExtra( Intent.EXTRA_ALLOW_MULTIPLE,true );
                intent.setAction( Intent.ACTION_GET_CONTENT );
                startActivityForResult(intent.createChooser( intent,"Selecione até 3 fotos" ),RESULT_LOAD_IMAGE1);



            }
        } );

        btnAudio = (Button) findViewById( R.id.btnAudio );
            btnAudio.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( CriarPedidoCliente.this,Audio.class );
                    startActivity( intent );

                }
            } );

        btnEnviar = (Button) findViewById( R.id.btnEnviar );
        btnEnviar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClienteInfo clienteInfo = new ClienteInfo();

                Data = txtData.getText().toString();
                if(!txtInfo.getText().toString().equals( "" )) Info = txtInfo.getText().toString();
                else Info = "NULL";

                if(Horario.equals( "" )) Toast.makeText( CriarPedidoCliente.this, "Você não selecionou o Horario", Toast.LENGTH_SHORT ).show();
                else if(Data.equals( "" )) Toast.makeText( CriarPedidoCliente.this, "Você não inseriu a Data", Toast.LENGTH_SHORT ).show();

                else
                {
                    if(!txtData2.getText().toString().equals( "" ) && !Horario2.equals( "null" ) && !txtData3.getText().toString().equals( "" ) && !Horario3.equals( "null" ))
                    {
                        //Todos os campos preenchidos de data e horario
                        Data2 = txtData2.getText().toString();
                        Data3 = txtData3.getText().toString();
                        Text = "Tipo do Serviço: "+TipoServico+"\n\n " +
                                "Data e horario desejado pelo cliente: "+Data+" / "+Horario+"\n Segunda data e horario: "+Data2+"  "+Horario2+"\n Terceira data e horario: "+Data3+" / "+Horario3+"\n\n" +
                                "Nome: "+clienteInfo.getNome_cliente()+"\n Email: "+clienteInfo.getEmail_cliente()+"\n Contato: "+clienteInfo.getCelular_cliente()+"\n\n" +
                                "Informações adicionais: \n" +Info;

                        pd = new ProgressDialog(CriarPedidoCliente.this);
                        pd.setTitle("Enviando pedido");
                        pd.setMessage("Espere...");
                        pd.show();

                        if (clienteInfo.getDocumento_cliente().equals( "" )) clienteInfo.setDocumento_cliente( "null" );

                        sendEmailTask.execute(  );
                        CadastrarPedido( TipoServico,Data,Data2,Data3,Horario,Horario2,Horario3,clienteInfo.getNome_cliente(),clienteInfo.getEmail_cliente(),
                                clienteInfo.getCelular_cliente(),Info,clienteInfo.getDocumento_cliente());
                    }
                    else if(!txtData2.getText().toString().equals( "" ) && !Horario2.equals( "null" ) && txtData3.getText().toString().equals( "" ) && Horario3.equals( "null" ))
                    {
                        //Somente data 1 e 2 e horario 1 e 2 preenchidos
                        Data3 = "null";
                        Data2 = txtData2.getText().toString();
                        Text = "Tipo do Serviço: "+TipoServico+"\n\n " +
                                "Data e horario desejado pelo cliente: "+Data+" / "+Horario+"\n Segunda data e horario: "+Data2+"  "+Horario2+"\n\n" +
                                "Nome: "+clienteInfo.getNome_cliente()+"\n Email: "+clienteInfo.getEmail_cliente()+"\n Contato: "+clienteInfo.getCelular_cliente()+"\n\n" +
                                "Informações adicionais: \n" +Info;

                        pd = new ProgressDialog(CriarPedidoCliente.this);
                        pd.setTitle("Enviando pedido");
                        pd.setMessage("Espere...");
                        pd.show();

                        if (clienteInfo.getDocumento_cliente().equals( "" )) clienteInfo.setDocumento_cliente( "null" );

                        sendEmailTask.execute(  );
                        CadastrarPedido( TipoServico,Data,Data2,Data3,Horario,Horario2,Horario3,clienteInfo.getNome_cliente(),clienteInfo.getEmail_cliente(),
                                clienteInfo.getCelular_cliente(),Info,clienteInfo.getDocumento_cliente());
                    }
                    else if(txtData2.getText().toString().equals( "" ) && Horario2.equals( "null" ) && !txtData3.getText().toString().equals( "" ) && !Horario3.equals( "null" ))
                    {
                        //Somente data 1 e 3 e horario 1 e 3 preenchidos
                        Data2 = "null";
                        Data3 = txtData3.getText().toString();
                        Text = "Tipo do Serviço: "+TipoServico+"\n\n " +
                                "Data e horario desejado pelo cliente: "+Data+" / "+Horario+"\n Segunda data e horario: "+Data3+"  "+Horario3+"\n\n" +
                                "Nome: "+clienteInfo.getNome_cliente()+"\n Email: "+clienteInfo.getEmail_cliente()+"\n Contato: "+clienteInfo.getCelular_cliente()+"\n\n" +
                                "Informações adicionais: \n" +Info;

                        pd = new ProgressDialog(CriarPedidoCliente.this);
                        pd.setTitle("Enviando pedido");
                        pd.setMessage("Espere...");
                        pd.show();

                        if (clienteInfo.getDocumento_cliente().equals( "" )) clienteInfo.setDocumento_cliente( "null" );

                        sendEmailTask.execute(  );
                        CadastrarPedido( TipoServico,Data,Data2,Data3,Horario,Horario2,Horario3,clienteInfo.getNome_cliente(),clienteInfo.getEmail_cliente(),
                                clienteInfo.getCelular_cliente(),Info,clienteInfo.getDocumento_cliente());
                    }
                    else
                    {
                        //Somente data 1 e horario 1 preenchidos
                        Data2 = "null";
                        Data3 = "null";
                        Text = "Tipo do Serviço: "+TipoServico+"\n\n " +
                                "Data e horario desejado pelo cliente: "+Data+" / "+Horario+"\n\n " +
                                "Nome: "+clienteInfo.getNome_cliente()+"\n Email: "+clienteInfo.getEmail_cliente()+"\n Contato: "+clienteInfo.getCelular_cliente()+"\n\n" +
                                "Informações adicionais: \n" +Info;

                        pd = new ProgressDialog(CriarPedidoCliente.this);
                        pd.setTitle("Enviando pedido");
                        pd.setMessage("Espere...");
                        pd.show();

                        if (clienteInfo.getDocumento_cliente().equals( "" )) clienteInfo.setDocumento_cliente( "null" );

                        sendEmailTask.execute(  );
                        CadastrarPedido( TipoServico,Data,Data2,Data3,Horario,Horario2,Horario3,clienteInfo.getNome_cliente(),clienteInfo.getEmail_cliente(),
                                clienteInfo.getCelular_cliente(),Info,clienteInfo.getDocumento_cliente());
                    }


                }
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
                Toast.makeText(CriarPedidoCliente.this, "Ocorreu algum erro",Toast.LENGTH_LONG).show();
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
                sender.sendMail(subject,
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


    private void CadastrarPedido(String TipoServico, String Data, String Data2, String Data3, String Horario, String Horario2, String Horario3, String Nome, String Email, String Contato, String Info, String Doc) {

        HashMap<String, String> hashMapToken = new HashMap<>();
        hashMapToken.put( "tipo_servico_pedido", TipoServico );
        hashMapToken.put( "data_pedido", Data );
        hashMapToken.put( "data2_pedido", Data2 );
        hashMapToken.put( "data3_pedido", Data3 );
        hashMapToken.put( "hora_pedido", Horario );
        hashMapToken.put( "hora2_pedido", Horario2 );
        hashMapToken.put( "hora3_pedido", Horario3 );
        hashMapToken.put( "nome_cliente", Nome );
        hashMapToken.put( "email_cliente", Email );
        hashMapToken.put( "celular_cliente", Contato );
        hashMapToken.put( "info_pedido", Info );
        hashMapToken.put( "documento_cliente", Doc );


        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, url, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados) {

                Toast.makeText( CriarPedidoCliente.this, "Pedido cadastrado", Toast.LENGTH_SHORT ).show();
                pd.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText( CriarPedidoCliente.this, "Ocorreu algum erro", Toast.LENGTH_SHORT ).show();
                Log.i( "COEEEEEEEEEEEEEEE", String.valueOf( error ) );


            }
        } );
        VolleyRP.addToQueue( solicitar, mRequest, this, volley );
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




}
