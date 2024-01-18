package Models;

public class ClientesDireccionesDomicilios {
    private int id;
    private int idCliente;
    private String direccion;
    private String referencia;
    private String identificador;


    public ClientesDireccionesDomicilios() {
    }


    public ClientesDireccionesDomicilios(int idCliente, String direccion, String referencia, String identificador) {
        this.idCliente = idCliente;
        this.direccion = direccion;
        this.referencia = referencia;
        this.identificador = identificador;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    // Método toString para facilitar la visualización de los datos
    @Override
    public String toString() {
        return "ClientesDireccionesDomicilios{" +
                "id=" + id +
                ", idCliente=" + idCliente +
                ", direccion='" + direccion + '\'' +
                ", referencia='" + referencia + '\'' +
                ", identificador='" + identificador + '\'' +
                '}';
    }
}
