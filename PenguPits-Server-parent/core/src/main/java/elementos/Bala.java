package elementos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import utiles.Global; // Importación necesaria para acceder a la textura global

public class Bala extends Actor{

	public Imagen spr;
	public float x, y;
	// NOTA: Esta VELOCIDAD ya no se usa, la velocidad la maneja BalaServer.
	private final float VELOCIDAD = 15f; 
	private boolean direccionDerecha; 
	private Rectangle colisionBala;
	private int idBala; // ID único del servidor
    // Se elimina la variable 'texture' ya que la gestiona la clase Global/Imagen.
	
	
	public Bala(int idBala, float x, float y, boolean direccionDerecha){ // Constructor con ID
	
	this.idBala = idBala;
	
	// *** CORRECCIÓN CLAVE: Usamos la textura pre-cargada de Global. ***
	this.spr = new Imagen(Global.TEXTURA_BALA); 
	
	this.spr.setSize(50, 50);
	
	colisionBala = new Rectangle(x, y, 30, 30);
	this.x =x;
	this.y = y;
	this.direccionDerecha = direccionDerecha;
	spr.setPosition(x, y);


}
	
	public void dibujarBala() {
		spr.dibujar();
	}
	
    // Este método solo actualiza la posición del hitbox (colisionBala)
    // después de que setX/setY ha sido llamado desde la red.
	public void actualizar() {
		colisionBala.setPosition(x, y);
	}
	
	public Rectangle getColision() {
        return colisionBala;
    }
	
	public boolean debeEliminarse() {
		// Esta función no se usa, la elimina el servidor.
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
	
	public void setX(float x){
		this.x = x;
		spr.setPosition(x, y);
	}
	
	public void setY(float y){
		this.y = y;
		spr.setPosition(x, y);
	}
	
    // El método dispose() es seguro porque la Textura se libera globalmente en PantallaJuego.dispose().
	public void dispose() {
	    // Solo liberamos el wrapper Imagen, NO la textura global (Global.TEXTURA_BALA).
	    if (spr != null) {
	        spr.dispose(); 
	    }
	    // No debe haber ninguna llamada a Global.TEXTURA_BALA.dispose() aquí.
	}
}