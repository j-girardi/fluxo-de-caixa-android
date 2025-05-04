package com.example.fluxodecaixa.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fluxodecaixa.entity.Transacao
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatabaseHandler( context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "dbfile.sqlite"
        const val TABLE_NAME = "transacao"
        const val COL_ID = "_id"
        const val COL_TIPO = "tipo"
        const val COL_DETALHE = "detalhe"
        const val COL_VALOR = "valor"
        const val COL_DATA_HORA = "data"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TIPO TEXT,
                $COL_DETALHE TEXT,
                $COL_VALOR REAL,
                $COL_DATA_HORA TEXT
            )
        """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL( "DROP TABLE IF EXISTS ${TABLE_NAME}" )
        onCreate( db )
    }

    fun incluir(transacao : Transacao ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_TIPO, transacao.tipo)
            put(COL_DETALHE, transacao.detalhe)
            put(COL_VALOR, transacao.valor)
            put(COL_DATA_HORA, transacao.data)
        }

        return db.insert(TABLE_NAME, null, values)
    }

    fun alterar(transacao : Transacao ) {
        val db = this.writableDatabase

        val registro = ContentValues().apply {
            put("tipo", transacao.tipo)
            put("detalhe", transacao.detalhe)
            put("valor", transacao.valor)
            put("data", transacao.data)
        }

        db.update(
            "transacao",
            registro,
            "_id=${transacao._id}",
            null
        )
    }

    fun excluir( id : Int ): Int {
        val db = this.writableDatabase
        return db.delete( TABLE_NAME, "$COL_ID = ?", arrayOf( id.toString() ) )
    }

    fun getTodasTransacoes(): List<Transacao> {
        val lista = mutableListOf<Transacao>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_DATA_HORA DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val transacao = Transacao(
                    _id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPO)),
                    detalhe = cursor.getString(cursor.getColumnIndexOrThrow(COL_DETALHE)),
                    valor = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_VALOR)),
                    data = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATA_HORA))
                )
                lista.add(transacao)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }
}