package com.mobitech.eletromation.Especialista.GetPedidos;

public class PedidosAtributos {
    String id,status,tipo,info,email;
    static String idpedido;
    static String EmailCliente;

    public PedidosAtributos()
    {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public static String getIdpedido() {
        return idpedido;
    }

    public static void setIdpedido(String idpedido) {
        PedidosAtributos.idpedido = idpedido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String getEmailCliente() {
        return EmailCliente;
    }

    public static void setEmailCliente(String emailCliente) {
        EmailCliente = emailCliente;
    }
}
