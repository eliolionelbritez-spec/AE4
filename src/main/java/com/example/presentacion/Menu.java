package com.example.presentacion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import com.example.dominio.catalogo.OfertaComercial;
import com.example.dominio.excepciones.DatoInvalidoException;
import com.example.dominio.excepciones.FacturaSinItemsException;
import com.example.dominio.excepciones.LimiteCreditoExcedidoException;
import com.example.dominio.excepciones.PagoExcedidoException;
import com.example.dominio.actores.Cliente;
import com.example.dominio.actores.Empleado;
import com.example.dominio.pagos.Factura;
import com.example.persistencia.DatosInexistentesException;
import com.example.persistencia.PersistenciaException;
import com.example.servicio.ServicioClientes;
import com.example.servicio.ServicioFacturacion;
import com.example.servicio.ServicioPersistencia;

/** Menú de consola: pide datos y delega cada opción en un servicio. No contiene lógica de negocio. */
public class Menu {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final ServicioClientes clientes;
    private final ServicioFacturacion facturas;
    private final ServicioPersistencia persistencia;
    private final Pantalla pantalla = new Pantalla();
    private final Scanner in = new Scanner(System.in);

    public Menu(ServicioClientes clientes, ServicioFacturacion facturas, ServicioPersistencia persistencia) {
        this.clientes = clientes;
        this.facturas = facturas;
        this.persistencia = persistencia;
    }

    public void iniciar() {
        int opcion;
        do {
            mostrarOpciones();
            opcion = leerInt("Opción: ");
            try {
                switch (opcion) {
                    case 1 -> registrarCliente();
                    case 2 -> pantalla.listarClientes(clientes.listar());
                    case 3 -> nuevaFactura();
                    case 4 -> facturas.listar().forEach(f -> { pantalla.mostrarFactura(f); System.out.println(); });
                    case 5 -> pantalla.resumenFacturas("Facturas ordenadas por monto", facturas.porMonto());
                    case 6 -> pantalla.resumenFacturas("Facturas ordenadas por fecha", facturas.porFecha());
                    case 7 -> pantalla.listarClientes(clientes.porCompras());
                    case 8 -> pantalla.listarClientes(clientes.porLimite());
                    case 9 -> { persistencia.guardarTodo(); System.out.println("Facturas y clientes guardados en sus archivos."); }
                    case 10 -> pantalla.listarRegistros(persistencia.leerFacturas());
                    case 11 -> pantalla.listarRegistros(persistencia.leerClientes());
                    case 0 -> System.out.println("Hasta luego.");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (DatoInvalidoException e) {
                System.out.println("[Dato inválido] " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("[Error] Se esperaba un número.");
            } catch (DatosInexistentesException e) {
                System.out.println("[Archivo inexistente] Todavía no se guardó " + e.getOrigen() + ". Usá primero la opción 9.");
            } catch (PersistenciaException e) {
                System.out.println("[Error de archivo] " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void mostrarOpciones() {
        System.out.println("\n===== MENÚ =====");
        System.out.println(" 1) Registrar cliente");
        System.out.println(" 2) Listar clientes");
        System.out.println(" 3) Nueva factura");
        System.out.println(" 4) Listar facturas (detalle)");
        System.out.println(" 5) Facturas por monto (Comparable)");
        System.out.println(" 6) Facturas por fecha (Comparator)");
        System.out.println(" 7) Clientes por cantidad de compras (Comparable)");
        System.out.println(" 8) Clientes por límite de crédito (Comparator)");
        System.out.println(" 9) Guardar facturas y clientes en archivos");
        System.out.println("10) Leer archivo de facturas");
        System.out.println("11) Leer archivo de clientes");
        System.out.println(" 0) Salir");
    }

    private void registrarCliente() {
        String nombre = leer("Nombre: ");
        String domicilio = leer("Domicilio: ");
        String dni = leer("DNI (7 u 8 dígitos): ");
        String telefono = leer("Teléfono: ");
        double limite = Double.parseDouble(leer("Límite de crédito: "));
        String categoria = leer("Categoría (regular/premium/corporativo): ");
        clientes.registrar(nombre, domicilio, dni, telefono, limite, categoria);
        System.out.println("Cliente registrado.");
    }

    private void nuevaFactura() {
        Cliente cliente = elegir("cliente", clientes.listar(), c -> c.getNombre() + " (" + c.getCategoria() + ")");
        if (cliente == null) return;
        Empleado empleado = elegir("empleado", facturas.empleadosDisponibles(), e -> e.getNombre() + " - " + e.getPuesto());
        if (empleado == null) return;

        List<OfertaComercial> catalogo = facturas.ofertasDisponibles();
        for (int i = 0; i < catalogo.size(); i++)
            System.out.println((i + 1) + ") " + catalogo.get(i).resumen() + " - $" + catalogo.get(i).calcularPrecioFinal());
        List<OfertaComercial> items = new ArrayList<>();
        int n;
        while ((n = leerInt("Agregar ítem (0 = terminar): ")) != 0) {
            if (n >= 1 && n <= catalogo.size()) items.add(catalogo.get(n - 1));
            else System.out.println("Ítem inexistente.");
        }

        String fecha = leer("Fecha dd-MM-yyyy (Enter = hoy): ");
        if (fecha.isEmpty()) fecha = LocalDate.now().format(FORMATO);
        double monto = Double.parseDouble(leer("Monto que paga ahora: "));
        String forma = leer("Forma de pago: ");

        try {
            Factura f = facturas.emitir(fecha, cliente, empleado, items, monto, forma);
            System.out.println("\nFactura emitida:");
            pantalla.mostrarFactura(f);
        } catch (FacturaSinItemsException | PagoExcedidoException | LimiteCreditoExcedidoException e) {
            System.out.println("[No se pudo emitir] " + e.getMessage());
        }
    }

    private <T> T elegir(String titulo, List<T> lista, Function<T, String> descripcion) {
        if (lista.isEmpty()) { System.out.println("No hay " + titulo + "s cargados."); return null; }
        for (int i = 0; i < lista.size(); i++) System.out.println((i + 1) + ") " + descripcion.apply(lista.get(i)));
        int n = leerInt("Elegí " + titulo + ": ");
        if (n < 1 || n > lista.size()) { System.out.println("Selección inválida."); return null; }
        return lista.get(n - 1);
    }

    private String leer(String mensaje) {
        System.out.print(mensaje);
        return in.hasNextLine() ? in.nextLine().trim() : "0"; // sin más entrada: cierra el menú
    }

    private int leerInt(String mensaje) {
        try { return Integer.parseInt(leer(mensaje)); }
        catch (NumberFormatException e) { return -1; }
    }
}
