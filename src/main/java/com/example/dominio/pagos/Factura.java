package com.example.dominio.pagos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.dominio.catalogo.OfertaComercial;
import com.example.dominio.excepciones.DatoInvalidoException;
import com.example.dominio.excepciones.FacturaSinItemsException;
import com.example.dominio.excepciones.LimiteCreditoExcedidoException;
import com.example.dominio.excepciones.PagoExcedidoException;
import com.example.dominio.actores.Cliente;
import com.example.dominio.actores.Empleado;
import com.example.dominio.interfaces.Valorizable;

/** Implementa dos contratos independientes: Comparable y Valorizable. */
public class Factura implements Comparable<Factura>, Valorizable {
    private String numero;
    private String fecha; // formato dd-MM-yyyy
    private Cliente cliente;
    private Empleado empleado;
    private ArrayList<OfertaComercial> items;
    private Pago pago;

    /** Criterio alternativo (Comparator): por fecha. */
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final Comparator<Factura> POR_FECHA =
            Comparator.comparing((Factura f) -> LocalDate.parse(f.getFecha(), FORMATO));

    public Factura(String numero, String fecha, Cliente cliente, Empleado empleado,
                   ArrayList<OfertaComercial> items, Pago pago)
            throws FacturaSinItemsException, PagoExcedidoException {
        if (items == null || items.isEmpty()) throw new FacturaSinItemsException(numero);
        try { LocalDate.parse(fecha, FORMATO); }
        catch (DateTimeParseException | NullPointerException e) {
            throw new DatoInvalidoException("Fecha inválida (dd-MM-yyyy): " + fecha);
        }
        this.numero = numero;
        this.fecha = fecha;
        this.cliente = cliente;
        this.empleado = empleado;
        this.items = items;
        this.pago = pago;
        double total = calcularTotal();
        if (pago.getMonto() > total) throw new PagoExcedidoException(pago.getMonto(), total);
    }

    /** Emite la factura: registra la compra en el cliente (puede exceder su crédito) y fija el estado del pago. */
    public void emitir() throws LimiteCreditoExcedidoException {
        double total = calcularTotal();
        cliente.registrarCompra("Factura " + numero, total, pago.getMonto());
        double pagado = pago.getMonto();
        pago.setEstado(pagado >= total ? "Completo" : (pagado > 0 ? "Parcial" : "Pendiente"));
    }

    public void agregarItem(OfertaComercial item) { items.add(item); }

    public double calcularTotal() {
        double total = 0;
        for (OfertaComercial item : items) total += item.calcularPrecioFinal();
        return total;
    }

    @Override
    public double obtenerValor() { return calcularTotal(); }

    /** Orden natural (Comparable): por monto total. */
    @Override
    public int compareTo(Factura otra) {
        return Double.compare(calcularTotal(), otra.calcularTotal());
    }



    public String getNumero() { return numero; }
    public String getFecha() { return fecha; }
    public Cliente getCliente() { return cliente; }
    public Empleado getEmpleado() { return empleado; }
    public List<OfertaComercial> getItems() { return Collections.unmodifiableList(items); }
    public Pago getPago() { return pago; }
}
