package elementos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import utiles.Global; // Usamos recursos que están disponibles para todo el juego.

// Bala: Lo que el jugador ve volar en la pantalla.
public class Bala extends Actor{

	public Imagen spr; // El dibujo (sprite) de la bala.
	public float x, y; // Posición actual (actualizada por la red).
	// La velocidad ya no importa aquí, el servidor es quien decide dónde está la bala.
	private final float VELOCIDAD = 15f; 
	private boolean direccionDerecha; 
	private Rectangle colisionBala; // El área invisible que usamos para detectar golpes.
	private int idBala; // Su número de identificación único (para sincronizarla con el servidor).
    
	
	// Constructor: Se ejecuta cuando el cliente recibe una nueva bala de la red.
	public Bala(int idBala, float x, float y, boolean direccionDerecha){ 
	
        this.idBala = idBala;
        
        // Creamos la imagen usando la textura que se cargó al inicio del juego.
        this.spr = new Imagen(Global.TEXTURA_BALA); 
        
        this.spr.setSize(50, 50); // Le damos un tamaño.
        
        colisionBala = new Rectangle(x, y, 30, 30); // Creamos el área de colisión.
        this.x =x;
        this.y = y;
        this.direccionDerecha = direccionDerecha;
        spr.setPosition(x, y); // Colocamos la imagen en la posición inicial.


    }
	
	// Dibuja la bala en la pantalla.
	public void dibujarBala() {
		spr.dibujar();
	}
	
    // Este método se llama en cada frame para mover el área de colisión 
    // a donde la red puso la imagen (x, y).
	public void actualizar() {
		colisionBala.setPosition(x, y);
	}
	
	public Rectangle getColision() {
        return colisionBala;
    }
	
	public boolean debeEliminarse() {
		// La decisión de eliminarla la toma el servidor, no el cliente.
		return false; 
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public int getIdBala() {
        return idBala;
    }
	
	// Mueve la bala horizontalmente (llamado por la red).
	public void setX(float x){
		this.x = x;
		spr.setPosition(x, y); // Mueve la imagen al nuevo X.
	}
	
	// Mueve la bala verticalmente (llamado por la red).
	public void setY(float y){
		this.y = y;
		spr.setPosition(x, y); // Mueve la imagen al nuevo Y.
	}
	
    // Método de limpieza: Solo libera el contenedor (Imagen), no la textura compartida.
	public void dispose() {
	    if (spr != null) {
	        spr.dispose(); 
	    }
	}
}