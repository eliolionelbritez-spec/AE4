package com.example.persistencia;

import java.util.ArrayList;
import java.util.List;

import com.example.dominio.catalogo.OfertaComercial;
import com.example.dominio.pagos.Factura;

/** Guarda facturas en un archivo de texto. El formato de línea es un detalle de esta capa. */
public class RepositorioFacturasArchivo implements Repositorio<Factura> {
    private final GestorArchivo archivo;

    public RepositorioFacturasArchivo(String ruta) { this.archivo = new GestorArchivo(ruta); }

    @Override
    public void guardar(List<? extends Factura> facturas) throws PersistenciaException {
        List<String> lineas = new ArrayList<>();
        for (Factura f : facturas) lineas.add(aLinea(f));
        archivo.escribir(lineas);
    }

    @Override
    public List<String> leerRegistros() throws PersistenciaException { return archivo.leer(); }

    private String aLinea(Factura f) {
        StringBuilder items = new StringBuilder();
        for (OfertaComercial i : f.getItems()) {
            if (items.length() > 0) items.append("|");
            items.append(i.getCodigo()).append(":").append(i.getNombre());
        }
        return f.getNumero() + ";" + f.getFecha() + ";" + f.getCliente().getNombre() + ";"
                + f.getEmpleado().getNombre() + ";" + f.calcularTotal() + ";"
                + f.getPago().getFormaPago() + ";" + f.getPago().getEstado() + ";" + items;
    }
}
