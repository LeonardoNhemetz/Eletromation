package com.mobitech.eletromation.Cliente.ClientePJ;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobitech.eletromation.Especialista.GetPedidos.PedidosAdapter;
import com.mobitech.eletromation.Especialista.GetPedidos.PedidosAtributos;
import com.mobitech.eletromation.Especialista.GetPedidos.VerPedido.VerPedido;
import com.mobitech.eletromation.R;

import java.util.List;

public class AdapterPedidosPJ extends RecyclerView.Adapter<AdapterPedidosPJ.BuilderPedidosPJ> {

    private List<GetSetPedidosPJ> atributosList;
    private Context context;

    public AdapterPedidosPJ(List<GetSetPedidosPJ> atributosList, Context context) {
        this.atributosList = atributosList;
        this.context = context;
    }


    @Override
    public AdapterPedidosPJ.BuilderPedidosPJ onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_main_pj,parent,false);
        return new AdapterPedidosPJ.BuilderPedidosPJ( v );
    }

    @Override
    public void onBindViewHolder( AdapterPedidosPJ.BuilderPedidosPJ holder, final int position) {

        holder.id.setText( atributosList.get( position ).getId() );
        holder.status.setText( atributosList.get( position ).getStatus() );
        holder.info.setText( atributosList.get( position ).getInfo() );
        holder.email.setText( atributosList.get( position ).getEmail() );
        holder.nome.setText( atributosList.get( position ).getNome() );
        holder.tipo.setText( atributosList.get( position ).getTipo() );

        holder.cardView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    static class BuilderPedidosPJ extends RecyclerView.ViewHolder
    {

        CardView cardView;
        TextView id,status,nome,info,email,tipo;

        public BuilderPedidosPJ(View itemView) {
            super( itemView );
            cardView = (CardView) itemView.findViewById( R.id.card_view_verpedido );
            id = (TextView) itemView.findViewById( R.id.txtNumPedidoPJ );
            status = (TextView) itemView.findViewById( R.id.txtStatusPedidoPJ );
            info = (TextView) itemView.findViewById( R.id.txtInfoPedidoPJ );
            email = (TextView) itemView.findViewById( R.id.txtEmailPedidoPJ );
            nome = (TextView) itemView.findViewById( R.id.txtNomePedidoPJ );
            tipo = (TextView) itemView.findViewById( R.id.txtTipoPedidoPJ );
        }
    }
}
