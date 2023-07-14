package com.mobitech.eletromation.Especialista.GetPedidos.VerPedido;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobitech.eletromation.Especialista.GetPedidos.GetPedidos;
import com.mobitech.eletromation.Especialista.GetPedidos.PedidosAdapter;
import com.mobitech.eletromation.Especialista.GetPedidos.PedidosAtributos;
import com.mobitech.eletromation.Login;
import com.mobitech.eletromation.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class PedidoCompletoAdapter extends RecyclerView.Adapter<PedidoCompletoAdapter.BuilderPedidos> {

    private List<PedidoAtributosCompletos> atributosList;
    private Context context;

    public PedidoCompletoAdapter(List<PedidoAtributosCompletos> atributosList, Context context) {
        this.atributosList = atributosList;
        this.context = context;
    }


    @Override
    public PedidoCompletoAdapter.BuilderPedidos onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_verpedido,parent,false);
        return new PedidoCompletoAdapter.BuilderPedidos( v );
    }

    @Override
    public void onBindViewHolder(final PedidoCompletoAdapter.BuilderPedidos holder, int position) {
        holder.id.setText( atributosList.get( position ).getId() );
        holder.status.setText( atributosList.get( position ).getStatus() );
        holder.tipo.setText( atributosList.get( position ).getTipo() );
        holder.info.setText( atributosList.get( position ).getInfo() );
        holder.data.setText( atributosList.get( position ).getData() );
        holder.hora.setText( atributosList.get( position ).getHora() );
        holder.nome.setText( atributosList.get( position ).getNome() );
        holder.email.setText( atributosList.get( position ).getEmail() );
        holder.endereco.setText( atributosList.get( position ).getEndereco() );
        holder.celular.setText( atributosList.get( position ).getCelular() );

        final PedidosAtributos pedidosAtributos = new PedidosAtributos();
        String url = "Arquivos de Pedidos/"+pedidosAtributos.getEmailCliente()+"/Foto do Pedido: "+pedidosAtributos.getIdpedido()+"";
        Log.i("AQQQQQQQQQQQQQQ",url);
        StorageReference fStorage;
        fStorage = FirebaseStorage.getInstance().getReference().child(url);

        fStorage.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(holder.IVfoto);

            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

                Toast.makeText( context, "Não há foto a ser exibida!", Toast.LENGTH_LONG ).show();

            }
        } );

        holder.btnRepAudio.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference aStorage;
                String url = "Arquivos de Pedidos/"+pedidosAtributos.getEmailCliente()+"/Audio do Pedido: "+pedidosAtributos.getIdpedido()+"";
                aStorage = FirebaseStorage.getInstance().getReference().child(url);
                holder.btnRepAudio.setText( "Procurando audio..." );

                aStorage.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        holder.btnRepAudio.setText( "Reproduzindo Audio..." );
                        PlaySong( uri );
                    }
                } ).addOnFailureListener( new OnFailureListener()
                {
                    @Override
                    public void onFailure(Exception e)
                    {
                        Log.i( "AUDIOAQQQQQQQQQQQQQQQ",e.toString() );
                        holder.btnRepAudio.setText( "Não há Audio Gravado" );
                    }
                } );

            }
        } );




    }

    @Override
    public int getItemCount() {
        return atributosList.size();
    }

    static class BuilderPedidos extends RecyclerView.ViewHolder
    {
        ImageView IVfoto;
        TextView id,status,tipo,info,estimativaDePreco,data,hora,nome,email,endereco,celular;
        Button btnRepAudio;

        public BuilderPedidos(View itemView) {
            super( itemView );

            id = (TextView) itemView.findViewById( R.id.txtPedidoPedido );
            tipo = (TextView) itemView.findViewById( R.id.txtTipoPedidoPedido );
            status = (TextView) itemView.findViewById( R.id.txtStatusPedidoPedido );
            info = (TextView) itemView.findViewById( R.id.txtInfoPedidoPedido );
            data = (TextView) itemView.findViewById( R.id.txtDataPedido );
            hora = (TextView) itemView.findViewById( R.id.txtHoraPedido );
            nome = (TextView) itemView.findViewById( R.id.txtNomeClientePedido );
            email = (TextView) itemView.findViewById( R.id.txtEmailClientePedido );
            endereco = (TextView) itemView.findViewById( R.id.txtEnderecoClientePedido );
            celular = (TextView) itemView.findViewById( R.id.txtCelularClientePedido );
            IVfoto = (ImageView) itemView.findViewById( R.id.IVfoto );
            btnRepAudio = (Button) itemView.findViewById( R.id.btnRepAudio );
        }
    }

    public void PlaySong(Uri uri)
    {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try
        {

            mediaPlayer.setDataSource( uri.toString() );
            mediaPlayer.setOnPreparedListener( new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();

                }
            } );


            mediaPlayer.prepare();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

}
