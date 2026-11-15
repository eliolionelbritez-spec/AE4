package com.example.persistencia;

import java.util.ArrayList;
import java.util.List;

import com.example.dominio.actores.Cliente;

/** Guarda clientes en un archivo de texto. El formato de línea es un detalle de esta capa. */
public class RepositorioClientesArchivo implements Repositorio<Cliente> {
    private final GestorArchivo archivo;

    public RepositorioClientesArchivo(String ruta) { this.archivo = new GestorArchivo(ruta); }

    @Override
    public void guardar(List<? extends Cliente> clientes) throws PersistenciaException {
        List<String> lineas = new ArrayList<>();
        for (Cliente c : clientes) lineas.add(aLinea(c));
        archivo.escribir(lineas);
    }

    @Override
    public List<String> leerRegistros() throws PersistenciaException { return archivo.leer(); }

    private String aLinea(Cliente c) {
        return c.getDni() + ";" + c.getNombre() + ";" + c.getCategoria() + ";"
                + c.getLimiteCredito() + ";" + c.getHistorialCompras().size();
    }
}
