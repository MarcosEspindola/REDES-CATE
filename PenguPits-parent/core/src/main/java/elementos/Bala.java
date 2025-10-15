package elementos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Bala extends Actor{

	public Imagen spr;
	public float x, y;
	int vel = 20;
	public Bala(float x, float y){
	spr = new Imagen("bala.png");
	spr.setSize(100, 100);
	spr.setPosition(x, y);
	
	this.x =x;
	this.y = y;


}
	
	public void dibujarBala() {
		spr.dibujar();
	}
	
	public void actualizar() {
		setX(getX()+10);
		setY(getY());
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
