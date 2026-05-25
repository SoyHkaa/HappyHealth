package com.xidne.happyhealth

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class HomeFragment : Fragment() {

    private lateinit var chipGroupSintomas: ChipGroup
    private lateinit var etSintomaPersonalizado: EditText
    private lateinit var tvDiagnostico: TextView
    private var doctorSeleccionado: String = ""
    private val listaSintomas = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        chipGroupSintomas = view.findViewById(R.id.chipGroupSintomas)
        etSintomaPersonalizado = view.findViewById(R.id.etSintomaPersonalizado)
        tvDiagnostico = view.findViewById(R.id.tvDiagnostico)
        val btnReservar = view.findViewById<Button>(R.id.btnReservarCita)

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

        // Logica de Doctores
        view.findViewById<CardView>(R.id.docChris).setOnClickListener {
            mostrarInfoDoctor("Dr. Chris Frazie", "Pediatra experto en enfermedades respiratorias. 10 años de experiencia.")
        }
        view.findViewById<CardView>(R.id.docViola).setOnClickListener {
            mostrarInfoDoctor("Dr. Viola Dunn", "Pediatra especializada en atención temprana y nutrición. 8 años de experiencia.")
        }
        view.findViewById<CardView>(R.id.docKatherine).setOnClickListener {
            mostrarInfoDoctor("Katherine Rojas", "Médico general especializada en medicina familiar integral.")
        }
        view.findViewById<CardView>(R.id.docKevin).setOnClickListener {
            mostrarInfoDoctor("Kevin Benalcazar", "Especialista en Cardiología y prevención de enfermedades coronarias.")
        }
        view.findViewById<CardView>(R.id.docManuela).setOnClickListener {
            mostrarInfoDoctor("Manuela Beltrán", "Atención médica general y chequeos preventivos para todas las edades.")
        }
        view.findViewById<CardView>(R.id.docSofia).setOnClickListener {
            mostrarInfoDoctor("Sofía López", "Especialista en medicina interna y diagnóstico de condiciones crónicas.")
        }
        view.findViewById<CardView>(R.id.docArturo).setOnClickListener {
            mostrarInfoDoctor("Arturo Vidal", "Cardiólogo experto en rehabilitación cardiovascular y deportiva.")
        }

        //Envio de datos a ReserveActivity
        btnReservar.setOnClickListener {
            val todosMisSintomas = listaSintomas.joinToString(", ")
            val sintomaManual = etSintomaPersonalizado.text.toString()

            if (listaSintomas.isEmpty() && sintomaManual.isBlank()) {
                Toast.makeText(context, "Por favor indica al menos un síntoma", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (doctorSeleccionado.isEmpty()) {
                Toast.makeText(context, "Por favor selecciona un doctor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sintomasFinales = if (sintomaManual.isNotBlank()) {
                if (todosMisSintomas.isNotEmpty()) "$todosMisSintomas, $sintomaManual" else sintomaManual
            } else {
                todosMisSintomas
            }

            val intent = Intent(requireContext(), ReserveActivity::class.java)
            intent.putExtra("SINTOMAS_PREVIOS", sintomasFinales)
            intent.putExtra("DOCTOR_PREVIO", doctorSeleccionado)
            startActivity(intent)
        }

        return view
    }

    private fun mostrarInfoDoctor(nombreDoctor: String, descripcion: String) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Información del Especialista")
        dialog.setMessage("👨‍⚕️ $nombreDoctor\n\n$descripcion")
        
        dialog.setPositiveButton("Seleccionar este doctor") { _, _ ->
            doctorSeleccionado = nombreDoctor
            Toast.makeText(context, "Has seleccionado a $nombreDoctor", Toast.LENGTH_SHORT).show()
            actualizarDiagnostico()
        }
        
        dialog.setNegativeButton("Volver", null)
        dialog.show()
    }

    private fun actualizarDiagnostico() {
        listaSintomas.clear()

        for (i in 0 until chipGroupSintomas.childCount) {
            val chip = chipGroupSintomas.getChildAt(i) as Chip
            if (chip.isChecked && chip.text != "Otro") {
                listaSintomas.add(chip.text.toString())
            }
        }

        if (listaSintomas.isEmpty() && doctorSeleccionado.isEmpty()) {
            tvDiagnostico.text = "Selecciona síntomas y un doctor para ver la evaluación."
            return
        }

        val sintomasTexto = if (listaSintomas.isNotEmpty()) listaSintomas.joinToString(", ") else "Por detallar"
        val doctorTexto = if (doctorSeleccionado.isNotEmpty()) doctorSeleccionado else "Ninguno aún"

        tvDiagnostico.text = "Evaluación Preliminar:\nSíntomas: $sintomasTexto\n Doctor: $doctorTexto"
    }
}