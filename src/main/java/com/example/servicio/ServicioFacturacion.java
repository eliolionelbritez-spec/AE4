package com.example.servicio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.dominio.catalogo.OfertaComercial;
import com.example.dominio.excepciones.FacturaSinItemsException;
import com.example.dominio.excepciones.LimiteCreditoExcedidoException;
import com.example.dominio.excepciones.PagoExcedidoException;
import com.example.dominio.gestion.Empresa;
import com.example.dominio.actores.Cliente;
import com.example.dominio.actores.Empleado;
import com.example.dominio.pagos.Factura;
import com.example.dominio.pagos.Pago;

/** Casos de uso de facturación: emitir (con su pago) y listar con distintos órdenes. */
public class ServicioFacturacion {
    private final Empresa empresa;

    public ServicioFacturacion(Empresa empresa) { this.empresa = empresa; }

    /**
     * Crea el pago, arma la factura, la emite y la registra. Numera factura y pago.
     * Propaga los errores de dominio a quien la invoque.
     */
    public Factura emitir(String fecha, Cliente cliente, Empleado empleado,
                          List<OfertaComercial> items, double montoPagado, String formaPago)
            throws FacturaSinItemsException, PagoExcedidoException, LimiteCreditoExcedidoException {
        int n = empresa.getFacturas().size() + 1;
        Pago pago = new Pago("R" + n, fecha, montoPagado, formaPago, "Pendiente");
        Factura factura = new Factura(String.format("F%03d", n), fecha, cliente, empleado,
                new ArrayList<>(items), pago);
        factura.emitir();
        empresa.agregarFactura(factura);
        return factura;
    }

    public List<Factura> listar() { return empresa.getFacturas(); }

    /** Orden natural (Comparable): por monto total. */
    public List<Factura> porMonto() {
        List<Factura> l = new ArrayList<>(empresa.getFacturas());
        Collections.sort(l);
        return l;
    }

    /** Orden alternativo (Comparator): por fecha. */
    public List<Factura> porFecha() {
        List<Factura> l = new ArrayList<>(empresa.getFacturas());
        Collections.sort(l, Factura.POR_FECHA);
        return l;
    }

    /** Insumos necesarios para facturar: qué se puede vender y quién puede gestionarlo. */
    public List<OfertaComercial> ofertasDisponibles() { return empresa.getCatalogo(); }
    public List<Empleado> empleadosDisponibles() { return empresa.getEmpleados(); }
}
