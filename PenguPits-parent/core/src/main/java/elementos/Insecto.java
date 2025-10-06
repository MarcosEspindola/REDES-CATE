package elementos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import utiles.Render;

public class Insecto {

	public int x,y,x2,y2;
	private Animation<TextureRegion> animation;
	private float tiempo;
	private TextureRegion[] regionsMovimiento;
	private TextureRegion frameActual;
	private Texture imagen;
	
	
	public Insecto (int x,int y, int pj) {
		this.x = x;
		this.y = y;
		if(pj==1) { 
			imagen = new Texture(Gdx.files.internal("pengu/pengu1spr.png"));
			this.x = x;
			this.y = y;
		}
		if(pj==2) { 
			imagen = new Texture(Gdx.files.internal("pengu/pengu2spr.png"));
			this.x2 = x;
			this.y2 = y;
		}
		
		TextureRegion[][] tmp = TextureRegion.split(imagen, imagen.getWidth()/5, imagen.getHeight());
		
		regionsMovimiento = new TextureRegion[5];
		for(int i=0; i<5;i++) regionsMovimiento[i]= tmp[0][i];
		animation = new Animation<TextureRegion>(0.1f,regionsMovimiento);
		tiempo = 0f;
		
	}
	
	public void animar(int pj) {
		if (pj == 1) {
		tiempo+= Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.D)) {
		frameActual = animation.getKeyFrame(tiempo,true);
		Render.batch.draw(frameActual,x,y);
		x+=2;}}
		
		if (pj == 2) {
			tiempo+= Gdx.graphics.getDeltaTime();
			if(Gdx.input.isKeyPressed(Keys.D)) {
			frameActual = animation.getKeyFrame(tiempo,true);
			Render.batch.draw(frameActual,x2,y2);
			x2+=2;
		}}
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}
	
	public void setX2(float x2){
		this.x = x2;
	}
	
	public void setY2(float y2){
		this.y = y2;
	}
}
