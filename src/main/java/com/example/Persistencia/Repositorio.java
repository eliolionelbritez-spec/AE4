package com.example.persistencia;

import java.util.List;

/**
 * Contrato de persistencia. Los servicios dependen de esta interfaz y no de cómo se guardan
 * los datos (archivo de texto hoy; base de datos o JSON mañana) sin tocar la lógica del negocio.
 */
public interface Repositorio<T> {

    /** Guarda la colección completa, reemplazando lo almacenado antes. */
    void guardar(List<? extends T> datos) throws PersistenciaException;

    /** Devuelve los registros almacenados, uno por línea. */
    List<String> leerRegistros() throws PersistenciaException;
}
