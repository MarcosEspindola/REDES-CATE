package elementos;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import utiles.Entradas;
import utiles.Render;

public class Pengu {
	Imagen spr;
	
	public float alto,ancho;
	public float x, y ,x2, y2, tiempo;
	int vel = 2, salto = 17;
	
	
	public Pengu(float x, float y,int pj){
		
		if(pj==1) { 
			
			spr = new Imagen("pengu/pengu1.png");
			setX(x);
			setY(y);
		}else{
			spr = new Imagen("pengu/pengu2.png");
			setX2(x);
			setY2(y);
		}
		spr.setSize(200, 200);

		
	}
	
	
	
	public void dibujar(){
		spr.dibujar();
	}
	
	public void actualizar(int pj){
	
		if(pj==1){
		if(Gdx.input.isKeyPressed(Keys.D)) {
			setX(getX()+vel);
			if(getX()>1200)setX(1200);
		}
		
		if(Gdx.input.isKeyPressed(Keys.SPACE)) {
			setY(getY()+salto);
			if(getY2()>600)setY2(600);
		}
		
		if(Gdx.input.isKeyPressed(Keys.A)) {
			setX(getX()-vel);
			if(getX()<0)setX(0);
			
		}
		if(getY()>120) {setY(getY()-5);}
		
		}
		
		if(pj==2){
			if(Gdx.input.isKeyPressed(Keys.L)) {
				setX2(getX2()+vel);
				if(getX2()>1200)setX2(1200);
			}
			
			if(Gdx.input.isKeyPressed(Keys.J)) {
				setX2(getX2()-vel);
				if(getX2()<0)setX2(0);
				
			}
			if(Gdx.input.isKeyPressed(Keys.I)) {
				setY2(getY2()+salto);
				System.out.println(getY2());
				if(getY2()>600)setY2(600);
				
			}
			if(getY2()>120) setY2(getY2()-5);
			}
		
		
			
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public float getX2(){
		return x2;
	}
	
	public float getY2(){
		return y2;
	}
	
	public void setX(float x){
		this.x = x;
		spr.setPosition(x, y);
	}
	
	public void setY(float y){
		this.y = y;
		spr.setPosition(x, y);
	}
	
	public void setX2(float x2){
		this.x2 = x2;
		spr.setPosition(x2, y2);
	}
	
	public void setY2(float y2){
		this.y2 = y2;
		spr.setPosition(x2, y2);
	}
	
	
	
	
}
