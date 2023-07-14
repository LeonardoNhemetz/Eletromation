package com.mobitech.eletromation.Especialista.GetPedidos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mobitech.eletromation.R;
import com.mobitech.eletromation.VolleyRP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetPedidos extends AppCompatActivity {

    private List<PedidosAtributos> atributosList;
    private PedidosAdapter adapter;
    private RecyclerView rv;
    private RequestQueue mRequest;
    private VolleyRP volley;
    private static final String URL = "http://iurdcom.ddns.net:1024/eletromation/pedidos_getall.php";


    PedidosAtributos pedidosAtributos = new PedidosAtributos();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_get_pedidos );
        setTitle( "Pedidos" );

        atributosList = new ArrayList<>(  );

        volley = VolleyRP.getInstance( this );
        mRequest = volley.getRequestQueue();

        rv = (RecyclerView) findViewById( R.id.PedidoRecyclerView );
        LinearLayoutManager lm = new LinearLayoutManager( this );
        rv.setLayoutManager( lm );

        adapter = new PedidosAdapter( atributosList, this );
        rv.setAdapter( adapter );

        SolicitarJSON();
    }

    public void SolicitarJSON()
    {
        JsonObjectRequest solicitar = new JsonObjectRequest( URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                try {
                    String TodosOsDados = response.getString( "resultado" );
                    JSONArray jsonArray = new JSONArray( TodosOsDados );

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject js = jsonArray.getJSONObject( i );
                        String id = js.getString( "id_pedido" );
                        String tipo = js.getString( "tipo_servico_pedido" );
                        String status = js.getString( "status_pedido" );
                        String info = js.getString( "info_pedido" );
                        String email = js.getString( "email_cliente" );



                        agregarPedido(id,status,tipo,info,email );




                    }
                } catch (JSONException e) {
                    Toast.makeText( GetPedidos.this, "Erro no JSON", Toast.LENGTH_SHORT ).show();
                }
            }
        },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText( GetPedidos.this, "erro", Toast.LENGTH_SHORT ).show();

            }
        } );
        VolleyRP.addToQueue( solicitar,mRequest,this,volley );
    }

    public void agregarPedido(String id, String status, String tipo, String info, String email)
    {
        PedidosAtributos pedidosAtributos = new PedidosAtributos();
        pedidosAtributos.setId( id );
        pedidosAtributos.setStatus( status );
        pedidosAtributos.setTipo( tipo );
        pedidosAtributos.setInfo( info );
        pedidosAtributos.setEmail( email );


        atributosList.add( pedidosAtributos );
        adapter.notifyDataSetChanged();
    }
}
