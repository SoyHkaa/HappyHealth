package com.xidne.happyhealth


class ReservaValidator {

    fun obtenerEspecialidades(): Array<String> {
        return arrayOf("General", "Cardiología", "Especialistas")
    }

    fun obtenerDoctores(): Array<String> {
        return arrayOf("Dr. Chris Frazie", "Dr. Viola Dunn", "Katherine Rojas", "Kevin Benalcazar", "Manuela Beltrán", "Sofía López", "Arturo Vidal")
    }

    fun validarDatosCita(descripcion: String, fecha: String): Boolean {
        return descripcion.isNotEmpty() && fecha.isNotEmpty()
    }
}