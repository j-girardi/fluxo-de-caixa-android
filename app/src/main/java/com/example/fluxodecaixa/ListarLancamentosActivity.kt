package com.example.fluxodecaixa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fluxodecaixa.adapter.AdapterLista
import com.example.fluxodecaixa.database.DatabaseHandler
import com.example.fluxodecaixa.databinding.ActivityListarLancamentosBinding

class ListarLancamentosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListarLancamentosBinding
    private lateinit var dbHandler: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListarLancamentosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHandler = DatabaseHandler(this)

    }

    override fun onStart() {
        super.onStart()

        val registros = dbHandler.listar()
        val adapter = AdapterLista(
            this,
            registros
        )

        binding.listView.adapter = adapter
    }
}