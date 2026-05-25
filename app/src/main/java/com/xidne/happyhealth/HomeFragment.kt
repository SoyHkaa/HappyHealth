package com.xidne.happyhealth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class HomeFragment : Fragment() {

    private lateinit var chipGroupSintomas: ChipGroup
    private lateinit var etSintomaPersonalizado: EditText
    private lateinit var tvDiagnostico: TextView
    private val listaSintomas = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        chipGroupSintomas = view.findViewById(R.id.chipGroupSintomas)
        etSintomaPersonalizado = view.findViewById(R.id.etSintomaPersonalizado)
        tvDiagnostico = view.findViewById(R.id.tvDiagnostico)

        // Logica de Sintomas
        for (i in 0 until chipGroupSintomas.childCount) {
            val chip = chipGroupSintomas.getChildAt(i) as Chip
            
            chip.setOnCheckedChangeListener { botonTouch, isChecked ->
                if (botonTouch.id == R.id.chipOtro) {
                    etSintomaPersonalizado.visibility = if (isChecked) View.VISIBLE else View.GONE
                }
                actualizarDiagnostico()
            }
        }

        return view
    }

    private fun actualizarDiagnostico() {
        listaSintomas.clear()

        for (i in 0 until chipGroupSintomas.childCount) {
            val chip = chipGroupSintomas.getChildAt(i) as Chip
            if (chip.isChecked && chip.text != "Otro") {
                listaSintomas.add(chip.text.toString())
            }
        }

        if (listaSintomas.isEmpty()) {
            tvDiagnostico.text = "Selecciona síntomas para ver la evaluación."
            return
        }

        val sintomasTexto = listaSintomas.joinToString(", ")
        tvDiagnostico.text = "Evaluación Preliminar:\nSíntomas: $sintomasTexto"
    }
}