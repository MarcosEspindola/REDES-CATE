package red;

// BalaServer hereda de RedAbstracta
public class BalaServer extends RedAbstracta { 

    private final float VELOCIDAD = 400f; 
    private boolean direccionDerecha;
    private int idJugadorDano; 

    /**
     * Constructor de la bala del lado del servidor.
     */
    public BalaServer(float x, float y, boolean direccionDerecha, int idBala, int idJugadorDano) {
       
       // CRÍTICO: Llamamos al constructor público de RedAbstracta.
       super(x, y, idBala); 
       
       this.direccionDerecha = direccionDerecha;
       this.idJugadorDano = idJugadorDano;
    }

    // ----------------------------------------------------------------------
    // IMPLEMENTACIÓN DEL MÉTODO ABSTRACTO
    // ----------------------------------------------------------------------
    
    @Override
    public void actualizar(float delta) {
       // Lógica de movimiento
       if (direccionDerecha) {
           this.x += VELOCIDAD * delta;
       } else {
           this.x -= VELOCIDAD * delta;
       }
    }

    // ----------------------------------------------------------------------
    // GETTERS PROPIOS
    // ----------------------------------------------------------------------

    public boolean debeEliminarse() {
       return x < -50 || x > 1330; 
    }

    public boolean isDerecha() {
       return direccionDerecha;
    }

    public int getIdJugadorDano() {
       return idJugadorDano;
    }
    
    // NOTA: Para obtener el ID de la bala, se usa getId() heredado de RedAbstracta.
}