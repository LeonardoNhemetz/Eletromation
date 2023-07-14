package com.mobitech.eletromation.Especialista.GetPedidos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mobitech.eletromation.Especialista.GetPedidos.VerPedido.VerPedido;
import com.mobitech.eletromation.R;

import java.util.List;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.BuilderPedidos> {

    private List<PedidosAtributos> atributosList;
    private Context context;

    public PedidosAdapter(List<PedidosAtributos> atributosList, Context context) {
        this.atributosList = atributosList;
        this.context = context;
    }


    @Override
    public PedidosAdapter.BuilderPedidos onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_getpedidos,parent,false);
        return new PedidosAdapter.BuilderPedidos( v );
    }

    @Override
    public void onBindViewHolder(PedidosAdapter.BuilderPedidos holder, final int position) {
        holder.id.setText( atributosList.get( position ).getId() );
        holder.status.setText( atributosList.get( position ).getStatus() );
        holder.tipo.setText( atributosList.get( position ).getTipo() );
        holder.info.setText( atributosList.get( position ).getInfo() );
        holder.txtEmail.setText( atributosList.get( position ).getEmail() );

        holder.cardView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //QUANDO CLICAR NO CARDVIEW
                PedidosAtributos pedidosAtributos = new PedidosAtributos();
                pedidosAtributos.setIdpedido(atributosList.get( position ).getId());
                pedidosAtributos.setEmailCliente( atributosList.get(position).getEmail() );
                Intent i = new Intent( context,VerPedido.class );
                context.startActivity( i );

            }
        } );


    }

    @Override
    public int getItemCount() {
        return atributosList.size();
    }

    static class BuilderPedidos extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView id,status,tipo,info,txtEmail;

        public BuilderPedidos(View itemView) {
            super( itemView );
            cardView = (CardView) itemView.findViewById( R.id.card_view_getpedidos );
            id = (TextView) itemView.findViewById( R.id.txtPedido );
            tipo = (TextView) itemView.findViewById( R.id.txtTipoPedidoPedido );
            status = (TextView) itemView.findViewById( R.id.txtStatusPedido );
            info = (TextView) itemView.findViewById( R.id.txtInfoPedido );
            txtEmail = (TextView) itemView.findViewById( R.id.txtEmail );
        }
    }
}
