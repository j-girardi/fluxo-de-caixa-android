package com.example.fluxodecaixa.adapter

import android.R.color
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.fluxodecaixa.database.DatabaseHandler
import com.example.fluxodecaixa.entity.Transacao
import java.text.NumberFormat

class AdapterLista( var context: Context, var cursor: Cursor ): BaseAdapter() {
    override fun getCount(): Int {
        return cursor.count
    }

    override fun getItem(position: Int): Any {
        cursor.moveToPosition(position)
        val transacao = Transacao(
            cursor.getInt(DatabaseHandler.ID),
            cursor.getString(DatabaseHandler.TIPO),
            cursor.getString(DatabaseHandler.DETALHE),
            cursor.getDouble(DatabaseHandler.VALOR),
            cursor.getString(DatabaseHandler.DATA)
        )
        return transacao
    }

    override fun getItemId(position: Int): Long {
        cursor.moveToPosition(position)
        return cursor.getInt(DatabaseHandler.ID).toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val v = inflater.inflate(android.R.layout.simple_list_item_2, null)

        val texto1 = v.findViewById<TextView>(android.R.id.text1)
        val texto2 = v.findViewById<TextView>(android.R.id.text2)

        cursor.moveToPosition(position)
        val valor = cursor.getDouble(DatabaseHandler.VALOR)
        val tipo = cursor.getString(DatabaseHandler.TIPO)
        val textoValor = tipo + " - " + NumberFormat.getCurrencyInstance().format(valor)
        val textoDescricao = cursor.getString(DatabaseHandler.DETALHE) + " - " + cursor.getString(DatabaseHandler.DATA)

        texto1.text = textoValor
        if(tipo == "Entrada"){
            texto1.setTextColor(context.resources.getColor(color.holo_green_dark))
        }else{
            texto1.setTextColor(context.resources.getColor(color.holo_red_dark))

        }

        texto2.text = textoDescricao

        return v
    }

}