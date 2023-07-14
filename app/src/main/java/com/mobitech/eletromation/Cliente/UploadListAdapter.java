package com.mobitech.eletromation.Cliente;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobitech.eletromation.R;

import java.util.List;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {

    public List<String> fileNameList;
    public List<String> fileDoneList;

    public UploadListAdapter(List<String> fileNameList,List<String> fileDoneList)
    {
        this.fileDoneList = fileDoneList;
        this.fileNameList = fileNameList;
    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate( R.layout.rec_view_foto_pedido,parent,false );
        return new ViewHolder( v );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        String fileName = fileNameList.get(position);
        holder.fileNameView.setText( fileName );



    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder
    {
        View mView;

        public TextView fileNameView;
        public ImageView fileDoneview;


        public ViewHolder(View itemView)
        {
            super( itemView );

            mView = itemView;
            fileNameView = (TextView) mView.findViewById( R.id.txtFileName );
            fileDoneview = (ImageView) mView.findViewById( R.id.imageViewFoto );
        }
    }


}
