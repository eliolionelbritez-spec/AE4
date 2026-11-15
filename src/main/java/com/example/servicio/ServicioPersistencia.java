package com.example.servicio;

import java.util.List;

import com.example.dominio.gestion.Empresa;
import com.example.dominio.actores.Cliente;
import com.example.dominio.pagos.Factura;
import com.example.persistencia.PersistenciaException;
import com.example.persistencia.Repositorio;

/** Guardar y recuperar datos. Depende de la interfaz Repositorio, no de una tecnología concreta. */
public class ServicioPersistencia {
    private final Empresa empresa;
    private final Repositorio<Cliente> repoClientes;
    private final Repositorio<Factura> repoFacturas;

    public ServicioPersistencia(Empresa empresa, Repositorio<Cliente> repoClientes,
                                Repositorio<Factura> repoFacturas) {
        this.empresa = empresa;
        this.repoClientes = repoClientes;
        this.repoFacturas = repoFacturas;
    }

    public void guardarFacturas() throws PersistenciaException { repoFacturas.guardar(empresa.getFacturas()); }
    public void guardarClientes() throws PersistenciaException { repoClientes.guardar(empresa.getClientes()); }

    public void guardarTodo() throws PersistenciaException {
        guardarFacturas();
        guardarClientes();
    }

    public List<String> leerFacturas() throws PersistenciaException { return repoFacturas.leerRegistros(); }
    public List<String> leerClientes() throws PersistenciaException { return repoClientes.leerRegistros(); }
}
