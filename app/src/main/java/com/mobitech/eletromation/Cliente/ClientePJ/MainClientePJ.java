package com.mobitech.eletromation.Cliente.ClientePJ;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mobitech.eletromation.Cliente.CriarOcorrencia;
import com.mobitech.eletromation.Cliente.CriarPedidoCliente;
import com.mobitech.eletromation.ClienteInfo;
import com.mobitech.eletromation.R;
import com.mobitech.eletromation.VolleyRP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainClientePJ extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    RecyclerView recViewMainPJ;
    private RequestQueue mRequest;
    private VolleyRP volley;
    private List<GetSetPedidosPJ> atributosList;
    private AdapterPedidosPJ adapter;
    private static final String URL = "http://iurdcom.ddns.net:1024/eletromation/pedidos_getpj.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main_cliente_pj );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        setTitle( "Menu Principal" );

        atributosList = new ArrayList<>(  );



        recViewMainPJ = (RecyclerView) findViewById( R.id.recViewMainPJ );
        LinearLayoutManager lm = new LinearLayoutManager( this );
        recViewMainPJ.setLayoutManager( lm );
        adapter = new AdapterPedidosPJ( atributosList, this );
        recViewMainPJ.setAdapter( adapter );

        volley = VolleyRP.getInstance( this );
        mRequest = volley.getRequestQueue();




        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        ClienteInfo clienteInfo = new ClienteInfo();

        if (clienteInfo.getMaster_cliente().equals("1"))
        {
            Menu menuNav=navigationView.getMenu();
            MenuItem nav_CadastrarClientePJ = menuNav.findItem(R.id.nav_CadastrarClientePJ);
            nav_CadastrarClientePJ.setEnabled(true);
        }
        else
        {
            Menu menuNav=navigationView.getMenu();
            MenuItem nav_CadastrarClientePJ = menuNav.findItem(R.id.nav_CadastrarClientePJ);
            nav_CadastrarClientePJ.setEnabled(false);
        }


        SolicitarJSON();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main_cliente_pj, menu );
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        ClienteInfo clienteInfo = new ClienteInfo();

        if (id == R.id.nav_pedidoPJ)
        {
            Intent i = new Intent(this,CriarPedidoCliente.class);
            startActivity( i );
        }
        else if (id == R.id.nav_ocorrencia)
        {
            Intent i = new Intent(this,CriarOcorrencia.class);
            startActivity( i );
        }
        else if (id == R.id.nav_CadastrarClientePJ)
        {
            Intent i = new Intent(this,CriarOcorrencia.class);
            startActivity( i );
        }



        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    private void SolicitarJSON()
    {
        ClienteInfo clienteInfo = new ClienteInfo();
        String documento_cliente = clienteInfo.getDocumento_cliente();
        HashMap<String,String> hashMapToken = new HashMap<>(  );
        hashMapToken.put( "documento_cliente",documento_cliente);

        JsonObjectRequest solicitar = new JsonObjectRequest( Request.Method.POST, URL, new JSONObject( hashMapToken ), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject dados) {

                try
                {
                    String TodosOsDados = dados.getString( "resultado" );
                    JSONArray jsonArray = new JSONArray( TodosOsDados );

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject js = jsonArray.getJSONObject( i );
                        String id = js.getString( "id_pedido" );
                        String nome = js.getString( "nome_cliente" );
                        String status = js.getString( "status_pedido" );
                        String info = js.getString( "info_pedido" );
                        String email = js.getString( "email_cliente" );
                        String tipo = js.getString( "tipo_pedido" );

                        agregarPedido(id,status,nome,info,email,tipo );

                    }
                }
                catch (JSONException e)
                {

                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("COEEEEEEEEEEEEEEE", String.valueOf( error ) );


            }
        });
        VolleyRP.addToQueue(solicitar,mRequest,this,volley);
    }

    public void agregarPedido(String id, String status, String nome, String info, String email, String tipo)
    {
        GetSetPedidosPJ getSetPedidosPJ = new GetSetPedidosPJ();
        getSetPedidosPJ.setId( id );
        getSetPedidosPJ.setStatus( status );
        getSetPedidosPJ.setNome( nome );
        getSetPedidosPJ.setInfo( info );
        getSetPedidosPJ.setEmail( email );
        getSetPedidosPJ.setTipo( tipo );


        atributosList.add( getSetPedidosPJ );
        adapter.notifyDataSetChanged();
    }



    @Override
    protected void onResume() {
        super.onResume();
        SolicitarJSON();
    }


}
