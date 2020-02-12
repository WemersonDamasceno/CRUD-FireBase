package com.example.testebancodadosfirebase.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.testebancodadosfirebase.R;
import com.example.testebancodadosfirebase.model.Pessoa;

import java.util.ArrayList;

public class PessoaAdapter extends ArrayAdapter<Pessoa> {
    private final Context context;
    private final ArrayList<Pessoa> elementos;

    public PessoaAdapter(@NonNull Context context, int resource, Context context1, ArrayList<Pessoa> elementos) {
        super(context, R.layout.list_item, elementos);
        this.context = context1;
        this.elementos = elementos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        TextView nomePessoa = (TextView) rowView.findViewById(R.id.editNome);
        TextView emailPessoa = (TextView) rowView.findViewById(R.id.editEmail);

        nomePessoa.setText(elementos.get(position).getNome());
        emailPessoa.setText(elementos.get(position).getEmail());


        return rowView;
    }


}
