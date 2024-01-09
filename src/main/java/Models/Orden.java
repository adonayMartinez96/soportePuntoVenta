package Models;

public class Orden {
    private String orden;
    private String fechaIngreso;
    private String horaCerro;
    private String nombre;
    private String telefono;
    private String cuidad;
    private String direccion;
    private String departamento;
    private String valorDeclarado;
    private String borrado;
    private String anulada;

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getHoraCerro() {
        return horaCerro;
    }

    public void setHoraCerro(String horaCerro) {
        this.horaCerro = horaCerro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCuidad() {
        return cuidad;
    }

    public void setCuidad(String cuidad) {
        this.cuidad = cuidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getValorDeclarado() {
        return valorDeclarado;
    }

    public void setValorDeclarado(String valorDeclarado) {
        this.valorDeclarado = valorDeclarado;
    }

    public String getBorrado() {
        return borrado;
    }

    public void setBorrado(String borrado) {
        this.borrado = borrado;
    }

    public String getAnulada() {
        return anulada;
    }

    public void setAnulada(String anulada) {
        this.anulada = anulada;
    }

    public Orden(String orden, String fechaIngreso, String horaCerro, String nombre, String telefono, String cuidad, String direccion, String departamento, String valorDeclarado, String borrado, String anulada) {
        this.orden = orden;
        this.fechaIngreso = fechaIngreso;
        this.horaCerro = horaCerro;
        this.nombre = nombre;
        this.telefono = telefono;
        this.cuidad = cuidad;
        this.direccion = direccion;
        this.departamento = departamento;
        this.valorDeclarado = valorDeclarado;
        this.borrado = borrado;
        this.anulada = anulada;
    }

    public Orden() {
    }
}
