package pantallas;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import elementos.Imagen;
import elementos.Insecto;
import elementos.Pengu;
import escenas.Hud;
import utiles.Config;
import utiles.Render;

public class PantallaJuego implements Screen {
	Imagen fondo;
	Hud hud;
	Pengu pengu,pengu2;
	Insecto insecto,insecto2;
	int vida2 = 0;
	int vida = 0;
	int piso = 120;
	int posX = 100;
	
	public void show() {
		
		fondo = new Imagen("FondoJuego1.png");
		fondo.setSize(Config.ANCHO, Config.ALTO);
		hud = new Hud();
		pengu = new Pengu(100, piso,1);
		pengu2 = new Pengu(500, piso,2);
		insecto = new Insecto(250,piso,1);
		insecto2 = new Insecto(100,piso,2);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla();
		Render.batch.begin();
			fondo.dibujar();
			hud.actualizarHud(vida,vida2);
			hud.dibujarHud();
			pengu.actualizar(1);
			pengu2.actualizar(2);
			pengu.dibujar();
			pengu2.dibujar();
			insecto.animar(1);
			insecto2.animar(2);
			
	    
		Render.batch.end();
		
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){vida++;}
		if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){vida2++;}
		if(Gdx.input.isKeyPressed(Keys.SPACE)) {vida=0;vida2=0;}
	}
	

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
