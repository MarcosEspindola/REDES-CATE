package red;

// CLASE ABSTRACTA: Es un plano básico que no se usa directamente.
// Simplemente obliga a todas las clases 'hijas' a tener la misma estructura.
public abstract class RedAbstracta { 
    
    // Posición X (horizontal) y Y (vertical) en el mundo del juego.
    protected float x;
    protected float y;
    // Un número de identificación único (ID) para el objeto.
    protected int id; 

    // Constructor: Este es el código que se ejecuta al crear cualquier objeto.
    // Pide la posición inicial y su ID.
    public RedAbstracta(float x, float y, int id) { 
        this.x = x;
        this.y = y;
        this.id = id;
    }

    // ----------------------------------------------------------------------
    // MÉTODO ABSTRACTO: El Contrato
    // ----------------------------------------------------------------------
    
    /**
     * Actualizar es un método obligatorio que cada objeto debe tener.
     * Define cómo se mueve y calcula la lógica de colisión en el servidor.
     */
    public abstract void actualizar(float delta);

    // ----------------------------------------------------------------------
    // MÉTODOS COMPARTIDOS
    // ----------------------------------------------------------------------
    
    public float getX() { 
        return x; 
    }
    
    public float getY() { 
        return y; 
    }
    
    public int getId() { 
        return id; 
    }
}

