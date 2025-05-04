package com.example.fluxodecaixa

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fluxodecaixa.database.DatabaseHandler
import com.example.fluxodecaixa.databinding.ActivityMainBinding
import com.example.fluxodecaixa.entity.Transacao
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHandler : DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        val dataText = binding.etDataTransacao.text.toString()

        val formatoVisivel = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            val valor = binding.etValor.editText?.text.toString()
            if (valor.isEmpty()) {
                throw IllegalArgumentException("O valor não pode estar vazio.")
            }

            if (dataText.isEmpty()) {
                throw IllegalArgumentException("A data não pode estar vazia.")
            }
            val data: Date = formatoVisivel.parse(dataText) as Date

            val tipo = (findViewById<RadioButton>(binding.radioGroupTipo.checkedRadioButtonId)).text.toString()

            val transacao = Transacao(
                0,
                tipo,
                binding.spDetalhe.selectedItem.toString(),
                valor.toDouble(),
                dataText
            )

            dbHandler.incluir(transacao)

            Toast.makeText(this, "Registro inserido com sucesso!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            val mensagemErro = when (e) {
                is java.text.ParseException -> "Data inválida, favor inserir uma data no formato dd/MM/yyyy"
                is NumberFormatException -> "Valor inválido, favor inserir um valor numérico"
                is IllegalArgumentException -> e.message
                else -> "Erro ao inserir o registro"
            }

            Toast.makeText(this, mensagemErro, Toast.LENGTH_LONG).show()
            return
        }
    }

    private fun btListarOnClick() {
        val intent = Intent(this, ListarLancamentosActivity::class.java)
        startActivity(intent)
    }

    private fun btSaldoOnClick() {
        val saldo = dbHandler.saldo()
        val totalEntradas = saldo.second
        val totalSaidas = saldo.third

        val mensagem = "Entradas: R$ %.2f\nSaídas: R$ %.2f\n\nSaldo: R$ %.2f".format(
            totalEntradas, totalSaidas, saldo.first
        )
        AlertDialog.Builder(this)
            .setTitle("Saldo Atual")
            .setMessage(mensagem)
            .setPositiveButton("OK", null)
            .show()
    }
}