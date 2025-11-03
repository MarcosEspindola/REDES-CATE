package red;

// Hereda de RedAbstracta, por lo que hereda los atributos de posición (x, y, id) 
// y se compromete a implementar el método 'actualizar'.
public class BalaServer extends RedAbstracta { 

    private final float VELOCIDAD = 400f; // Qué tan rápido se mueve la bala.
    private boolean direccionDerecha;     // true si va a la derecha, false a la izquierda.
    private int idJugadorDano;           // ID del jugador que disparó (1 o 2).

    // Constructor: Se ejecuta cuando HiloServer crea una nueva bala.
    public BalaServer(float x, float y, boolean direccionDerecha, int idBala, int idJugadorDano) {
       
       // Llama a la clase base (RedAbstracta) para guardar la posición y el ID único.
       super(x, y, idBala); 
       
       this.direccionDerecha = direccionDerecha;
       this.idJugadorDano = idJugadorDano;
    }

    // ----------------------------------------------------------------------
    // LÓGICA DE MOVIMIENTO (Implementación del método obligatorio)
    // ----------------------------------------------------------------------
    
    @Override
    public void actualizar(float delta) {
       // Si va a la derecha, le suma la velocidad a 'x'.
       if (direccionDerecha) {
           this.x += VELOCIDAD * delta;
       // Si va a la izquierda, le resta la velocidad a 'x'.
       } else {
           this.x -= VELOCIDAD * delta;
       }
    }

    // ----------------------------------------------------------------------
    // GETTERS PROPIOS
    // ----------------------------------------------------------------------

    public boolean debeEliminarse() {
       // Devuelve 'true' si la bala se salió de la pantalla para que el servidor la borre.
       return x < -50 || x > 1330; 
    }

    public boolean isDerecha() {
       return direccionDerecha;
    }

    public int getIdJugadorDano() {
       return idJugadorDano;
    }
}