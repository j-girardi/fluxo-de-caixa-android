package com.example.fluxodecaixa

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fluxodecaixa.database.DatabaseHandler
import com.example.fluxodecaixa.databinding.ActivityMainBinding
import com.example.fluxodecaixa.entity.Transacao
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHandler : DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate( layoutInflater )
        setContentView( binding.main )

        setButtonsListeneres()
        atualizarSpinner(R.array.detalhes_entrada_array)

        dbHandler = DatabaseHandler( this )
    }

    private fun setButtonsListeneres() {
        binding.btLancar.setOnClickListener {
            btIncluirOnClick()
        }

        binding.btListar.setOnClickListener {
            btListarOnClick()
        }

        binding.btSaldo.setOnClickListener {
            btSaldoOnClick()
        }

        binding.etDataTransacao.setOnClickListener {
            val calendario = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendario.set(year, month, dayOfMonth)

                    val formatoBanco = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                    dataSelecionada = formatoBanco.format(calendario.time)

                    // Para exibir na tela
                    val formatoVisivel = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val dataFormatada = formatoVisivel.format(calendario.time)
                    binding.etDataTransacao.setText(dataFormatada)
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.radioGroupTipo.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioEntrada -> atualizarSpinner(R.array.detalhes_entrada_array)
                R.id.radioSaida -> atualizarSpinner(R.array.detalhes_saida_array)
            }
        }
    }

    private fun atualizarSpinner(arrayId : Int) {
        val adapter = ArrayAdapter.createFromResource(
            this,
            arrayId,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spDetalhe.adapter = adapter
    }

    private fun btIncluirOnClick() {
        val transacao = Transacao(
            0,
            binding.radioGroupTipo.checkedRadioButtonId.toString(),
            binding.spDetalhe.selectedItem.toString(),
            binding.etValor.editText?.text.toString().toDouble(),
            binding.etDataTransacao.text.toString()
        )

        dbHandler.incluir(transacao)

        Toast.makeText(this, "Registro inserido com sucesso!", Toast.LENGTH_LONG).show()
    }


    private fun btListarOnClick() {
        val transacoes = dbHandler.getTodasTransacoes()
    }

    private fun btDeletarOnClick(transacao: Transacao) {
        dbHandler.excluir(transacao._id)
    }

    private fun btSaldoOnClick() {
        TODO("Not yet implemented")
    }
}