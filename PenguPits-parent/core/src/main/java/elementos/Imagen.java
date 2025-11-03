package elementos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import utiles.Render;

// Imagen: Una clase simple para manejar los dibujos de LibGDX (Texturas y Sprites).
public class Imagen {
	private Texture t; // El archivo de imagen cargado en memoria.
	private Sprite s;  // La versión que podemos mover, rotar y dibujar.
	
    // Constructor: Recibe un archivo de imagen ya cargado (Texture).
	public Imagen(Texture texture) {
		this.t = texture; 
		s = new Sprite(t);
	}
	
	// Dibuja el sprite en la pantalla.
	public void dibujar(){
		s.draw(Render.batch);
	} 
	
	// Define el ancho y el alto del dibujo.
	public void setSize(float ancho, float alto) {
		s.setSize(ancho,alto);
	}
	
	// Coloca el dibujo en una posición específica.
	public void setPosition(float x,float y) {
		s.setPosition(x, y);
		
	}
    
    // Método de limpieza: No hace nada, ya que la Textura (el archivo de imagen) 
    // es compartida por muchas clases y se libera solo una vez en Main.
    public void dispose() {
        // No hace nada para evitar liberar recursos compartidos.
    }
}