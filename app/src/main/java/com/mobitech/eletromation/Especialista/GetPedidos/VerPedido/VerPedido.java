package com.mobitech.eletromation.Especialista.GetPedidos.VerPedido;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobitech.eletromation.Cliente.MainCliente;
import com.mobitech.eletromation.Especialista.GetPedidos.PedidosAdapter;
import com.mobitech.eletromation.Especialista.GetPedidos.PedidosAtributos;
import com.mobitech.eletromation.Login;
import com.mobitech.eletromation.R;
import com.mobitech.eletromation.VolleyRP;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VerPedido extends AppCompatActivity {

    private List<PedidoAtributosCompletos> atributosListPedido;
    private PedidoCompletoAdapter adapter;
    private RecyclerView rv;
    private RecyclerView rvi;


    private RequestQueue mRequest;
    private VolleyRP volley;
    private static final String URL = "http://iurdcom.ddns.net:1024/eletromation/pedidos_getone.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_ver_pedido );

        atributosListPedido = new ArrayList<>(  );

        volley = VolleyRP.getInstance( this );
        mRequest = volley.getRequestQueue();

        rv = (RecyclerView) findViewById( R.id.RecyclerViewPedido );
        LinearLayoutManager lm = new LinearLayoutManager( this );
        rv.setLayoutManager( lm );

        adapter = new PedidoCompletoAdapter( atributosListPedido, this );
        rv.setAdapter( adapter );


        SolicitarJSONPedido();
    }



    public void SolicitarJSONPedido()
    {
        PedidosAtributos pedidosAtributos = new PedidosAtributos();
        HashMap<String,String> hashMapToken = new HashMap<>(  );
        hashMapToken.put( "id_pedido",pedidosAtributos.getIdpedido());

        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, URL, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados)
            {
                VerificarPedido( dados );
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(VerPedido.this, "Ocorreu algum erro",Toast.LENGTH_LONG).show();
                Log.i("COEEEEEEEEEEEEEEE", String.valueOf( error ) );


            }
        });
        VolleyRP.addToQueue(solicitar,mRequest,this,volley);
    }



    public void VerificarPedido(JSONObject dados){
        Log.i("COEEEEEEEEEEEEEEE", String.valueOf( dados ) );
        try {
            String estado = dados.getString("resultado");
            if(estado.equals("CC")){
                JSONObject Jsondados = new JSONObject(dados.getString("dados"));

                String id = Jsondados.getString("id_pedido");
                String status = ( Jsondados.getString("status_pedido") );
                String tipo = ( Jsondados.getString("tipo_servico_pedido") );
                String info = ( Jsondados.getString("info_pedido") );
                String data = ( Jsondados.getString("data_pedido") );
                String hora = ( Jsondados.getString("hora_pedido") );
                String nome = ( Jsondados.getString("nome_cliente") );
                String email = ( Jsondados.getString("email_cliente") );
                String endereco = ( Jsondados.getString("endereco_cliente") );
                String celular = ( Jsondados.getString("celular_cliente") );

                if(id == null) id = "NULO";
                if(status == null) status = "NULO";
                if(tipo == null) tipo = "NULO";
                if(info == null) info = "NULO";
                if(data == null) data = "NULO";
                if(hora == null) hora = "NULO";
                if(nome == null) nome = "NULO";
                if(email == null) email = "NULO";
                if(endereco == null) endereco = "NULO";
                if(celular == null) celular = "NULO";

                agregarPedido( id,status,tipo,info,data,hora,nome,email,endereco,celular );

            }
        } catch (JSONException e) {

        }
    }

    public void agregarPedido(String id, String status, String tipo, String info, String data, String hora, String nome,
                              String email, String endereco, String celular)
    {
        PedidoAtributosCompletos pedidosAtributosCompletos = new PedidoAtributosCompletos();
        pedidosAtributosCompletos.setId( id );
        pedidosAtributosCompletos.setStatus( status );
        pedidosAtributosCompletos.setTipo( tipo );
        pedidosAtributosCompletos.setInfo( info );
        pedidosAtributosCompletos.setData( data );
        pedidosAtributosCompletos.setHora( hora );
        pedidosAtributosCompletos.setNome( nome );
        pedidosAtributosCompletos.setEmail( email );
        pedidosAtributosCompletos.setEndereco( endereco );
        pedidosAtributosCompletos.setCelular( celular );


        atributosListPedido.add( pedidosAtributosCompletos );
        adapter.notifyDataSetChanged();
    }


}



