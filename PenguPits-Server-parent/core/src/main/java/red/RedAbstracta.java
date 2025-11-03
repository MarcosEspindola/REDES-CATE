package red;

/**
 * Clase base abstracta para todas las entidades lógicas (servidor)
 * que tienen una posición (x, y) y una lógica de actualización.
 */
public abstract class RedAbstracta { // <-- CRÍTICO: Debe ser 'abstract'
    
    protected float x;
    protected float y;
    protected int id; // ID de la entidad (e.g., ID de bala, ID de jugador)

    // *** CORRECCIÓN CRÍTICA: El constructor debe ser PUBLIC ***
    public RedAbstracta(float x, float y, int id) { 
        this.x = x;
        this.y = y;
        this.id = id;
    }

    // ----------------------------------------------------------------------
    // MÉTODO ABSTRACTO (Obliga a la implementación)
    // ----------------------------------------------------------------------
    
    /**
     * Define la lógica de actualización de posición, colisión o estado.
     */
    public abstract void actualizar(float delta);

    // ----------------------------------------------------------------------
    // MÉTODOS CONCRETOS (Implementación compartida)
    // ----------------------------------------------------------------------
    
    public float getX() { 
        return x; 
    }
    
    public float getY() { 
        return y; 
    }
    
    // Este método reemplaza al antiguo getIdBala()
    public int getId() { 
        return id; 
    }
}

