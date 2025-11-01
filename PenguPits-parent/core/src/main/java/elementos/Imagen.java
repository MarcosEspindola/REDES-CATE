package elementos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import utiles.Render;

public class Imagen {
	private Texture t;
	private Sprite s;
	
    // *** CONSTRUCTOR CORREGIDO: ACEPTA UN OBJETO TEXTURE ***
	public Imagen(Texture texture) {
		this.t = texture; // Asigna la textura ya cargada
		s = new Sprite(t);
	}
	
	public void dibujar(){
		s.draw(Render.batch);
	} 
	
	public void setSize(float ancho, float alto) {
		s.setSize(ancho,alto);
	}
	

	public void setPosition(float x,float y) {
		s.setPosition(x, y);
		
	}
    
    // El método dispose YA NO debe liberar la textura, 
    // pues esta es compartida (Global.TEXTURA_BALA).
    // Para LibGDX, solo liberamos recursos que no compartimos.
    // Sin embargo, si quieres que 'Imagen' sea liberable, simplemente no llamas
    // a t.dispose() aquí. 
    // Para esta implementación, dejaremos este método vacío para no causar problemas:
    public void dispose() {
        // No hace nada. La liberación se hace en PantallaJuego.dispose()
    }
}

	