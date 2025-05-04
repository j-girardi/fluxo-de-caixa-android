package com.example.fluxodecaixa.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fluxodecaixa.entity.Transacao

class DatabaseHandler( context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "dbfile.sqlite"
        const val TABLE_NAME = "transacao"
        const val COL_ID = "_id"
        const val COL_TIPO = "tipo"
        const val COL_DETALHE = "detalhe"
        const val COL_VALOR = "valor"
        const val COL_DATA = "data"
        const val ID = 0
        const val TIPO = 1
        const val DETALHE = 2
        const val VALOR = 3
        const val DATA = 4
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TIPO TEXT,
                $COL_DETALHE TEXT,
                $COL_VALOR REAL,
                $COL_DATA TEXT
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
            put(COL_DATA, transacao.data)
        }

        return db.insert(TABLE_NAME, null, values)
    }

    fun listar(): Cursor {
        val banco: SQLiteDatabase = this.writableDatabase

        val registros = banco.query(
            "transacao",
            null,
            null,
            null,
            null,
            null,
            null
        )

        return registros
    }

    fun getTodasTransacoes(): List<Transacao> {
        val lista = mutableListOf<Transacao>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_DATA DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val transacao = Transacao(
                    _id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPO)),
                    detalhe = cursor.getString(cursor.getColumnIndexOrThrow(COL_DETALHE)),
                    valor = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_VALOR)),
                    data = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATA))
                )
                lista.add(transacao)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }

    fun saldo(): Triple<Double, Double, Double> {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT $COL_TIPO, SUM($COL_VALOR) 
            FROM $TABLE_NAME 
            GROUP BY $COL_TIPO
        """,
            null)

        var saldo = 0.0
        var somaEntradas = 0.0
        var somaSaidas = 0.0
        if(cursor.moveToFirst()){
            do {
                if (cursor.getString(0) == "Entrada") {
                    somaEntradas = cursor.getDouble(1)
                } else {
                    somaSaidas = cursor.getDouble(1)
                }
            } while (cursor.moveToNext())
        }

        saldo = somaEntradas - somaSaidas
        cursor.close()
        return Triple(saldo, somaEntradas, somaSaidas)
    }

}