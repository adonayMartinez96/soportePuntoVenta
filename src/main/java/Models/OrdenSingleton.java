package Models;

public class OrdenSingleton {

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
    private String pagada;

    private  static OrdenSingleton instancia = null;

    private OrdenSingleton() {
        this.orden = "";
        this.fechaIngreso = "";
        this. horaCerro = "";
        this.nombre = "";
        this.telefono = "";
        this.cuidad = "";
        this.direccion = "";
        this.departamento = "";
        this.valorDeclarado = "";
        this.direccion = "";
        this.borrado = "";
        this.anulada = "";
        this.pagada = "";
    }

    public static OrdenSingleton getInstancia() {
        if (instancia == null) {
            instancia = new OrdenSingleton();
        }
        return instancia;
    }

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

    public String getPagada() {
        return pagada;
    }

    public void setPagada(String pagada) {
        this.pagada = pagada;
    }

    public  void setInstancia(OrdenSingleton instancia) {
        OrdenSingleton.instancia = instancia;
    }


}
