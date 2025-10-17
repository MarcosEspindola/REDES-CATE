package elementos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Bala extends Actor{

	public Imagen spr;
	public float x, y;
	private final float VELOCIDAD = 15f; //
	private boolean direccionDerecha; //
	private Rectangle colisionBala;

	
	public Bala(float x, float y, boolean direccionDerecha){//
	
	spr = new Imagen("bala.png");
	spr.setSize(50, 50);
	
	colisionBala = new Rectangle(x, y, 30, 30);
	this.x =x;
	this.y = y;
	this.direccionDerecha = direccionDerecha; //
	spr.setPosition(x, y);


}
	
	public void dibujarBala() {
		spr.dibujar();
	}
	
	public void actualizar() {
		if (direccionDerecha) {
			setX(getX() + VELOCIDAD); // Mover a la derecha
		} else {
			setX(getX() - VELOCIDAD); // Mover a la izquierda
		}
		colisionBala.setPosition(x, y);
	}
	
	public Rectangle getColision() {
        return colisionBala;
    }
	
	public boolean debeEliminarse() {
		
		return x < -50 || x > 1330; 
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public void setX(float x){
		this.x = x;
		spr.setPosition(x, y);
	}
	
	public void setY(float y){
		this.y = y;
		spr.setPosition(x, y);
	}
}
