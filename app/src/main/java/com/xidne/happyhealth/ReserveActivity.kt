package com.xidne.happyhealth

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ReserveActivity : AppCompatActivity() {

    private val selectedCalendar = Calendar.getInstance()
    private lateinit var especialidad: Spinner
    private lateinit var doctorSpinner: Spinner
    private lateinit var cvSiguiente: CardView
    private lateinit var cvConfirmar: CardView
    private lateinit var btnSiguiente: Button
    private lateinit var btnConfirmar: Button
    private lateinit var etDescripcion: EditText
    private lateinit var etFecha: EditText

    private val reservaValidator = ReservaValidator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve)
        
        inicializarVariables()
        configurarSpinners()
        cargarDatosPrevios()

        btnSiguiente.setOnClickListener {
            cvSiguiente.visibility = View.GONE
            cvConfirmar.visibility = View.VISIBLE
        }

        btnConfirmar.setOnClickListener {
            procesarConfirmacionCita()
        }
    }

    private fun inicializarVariables() {
        especialidad = findViewById(R.id.especialidad)
        doctorSpinner = findViewById(R.id.doctorSpinner)
        cvSiguiente = findViewById(R.id.cvSiguiente)
        cvConfirmar = findViewById(R.id.cvConfirmar)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        etDescripcion = findViewById(R.id.etDescripcion)
        etFecha = findViewById(R.id.etFecha)
    }

    private fun configurarSpinners() {
        especialidad.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reservaValidator.obtenerEspecialidades())
        doctorSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reservaValidator.obtenerDoctores())
    }

    private fun cargarDatosPrevios() {
        val doctorRecibido = intent.getStringExtra("DOCTOR_PREVIO") ?: ""
        val sintomasRecibidos = intent.getStringExtra("SINTOMAS_PREVIOS") ?: ""

        if (sintomasRecibidos.isNotEmpty()) {
            etDescripcion.setText(sintomasRecibidos)
        }

        if (doctorRecibido.isNotEmpty()) {
            val posicionDoctor = reservaValidator.obtenerDoctores().indexOf(doctorRecibido)
            if (posicionDoctor >= 0) { 
                doctorSpinner.setSelection(posicionDoctor)
            }
        }
    }

    private fun procesarConfirmacionCita() {
        val descripcion = etDescripcion.text.toString()
        val especialidadSeleccionada = especialidad.selectedItem.toString()
        val doctorSeleccionado = doctorSpinner.selectedItem.toString()
        val fecha = etFecha.text.toString()

        if (!reservaValidator.validarDatosCita(descripcion, fecha)) {
            Toast.makeText(this, "Por favor, rellene todos los campos requeridos.", Toast.LENGTH_LONG).show()
            return
        }

        val cita = Cita(
            descripcion = descripcion, 
            especialidad = especialidadSeleccionada, 
            doctor = doctorSeleccionado, 
            fecha = fecha
        )
        
        guardarCitaEnBaseDeDatos(cita)
    }

    private fun guardarCitaEnBaseDeDatos(cita: Cita) {
        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(applicationContext).citaDao().insert(cita)
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Cita programada", Toast.LENGTH_SHORT).show()
                finish() // Cierra la actividad tras guardar
            }
        }
    }

    fun onCLickScheduleDate(v: View?) {
        val etScheduleDate = findViewById<EditText>(R.id.etFecha)
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)
        
        val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            selectedCalendar.set(y, m, d)
            etScheduleDate.setText("$y-${m + 1}-$d")
        }

        DatePickerDialog(this, listener, year, month, dayOfMonth).show()
    }
}