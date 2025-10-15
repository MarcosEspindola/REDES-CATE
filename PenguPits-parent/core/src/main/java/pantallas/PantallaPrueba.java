package pantallas;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle; // <<-- AÑADIR
import com.badlogic.gdx.utils.Array;     // <<-- AÑADIR

import elementos.Imagen;
import elementos.Insecto;
import elementos.Pengu;
import escenas.Hud;
import utiles.Config;
import utiles.Render;

public class PantallaPrueba implements Screen {
	Imagen fondo;
	Hud hud;
	Pengu pengu,pengu2;
	Insecto insecto,insecto2;
	int vida2 = 0;
	int vida = 0;
	int piso = 120;
	int posX = 100;
	
	private Array<Rectangle> plataformas; // <<-- AÑADIR
	
	public void show() {
		
		fondo = new Imagen("FondoJuego1.png");
		fondo.setSize(Config.ANCHO, Config.ALTO);
		hud = new Hud();
		pengu = new Pengu(100, piso,1);
		pengu2 = new Pengu(500, piso,2);
		insecto = new Insecto(250,piso,1);
		insecto2 = new Insecto(100,piso,2);
		
		// Inicialización y creación de plataformas
		plataformas = new Array<Rectangle>();
		plataformas.add(new Rectangle(300, 250, 250, 30)); // Plataforma 1
		plataformas.add(new Rectangle(700, 350, 200, 30)); // Plataforma 2
		plataformas.add(new Rectangle(50, 450, 150, 30));  // Plataforma 3
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla();
		Render.batch.begin();
			fondo.dibujar();
			hud.actualizarHud(vida,vida2);
			hud.dibujarHud();
			
			// <<-- CORREGIDO Y SE PASAN LAS PLATAFORMAS -->>
			pengu.actualizar3(1, plataformas); 
			pengu2.actualizar3(2, plataformas);
			
	    
		Render.batch.end();
		
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){vida++;}
		if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){vida2++;}
		if(Gdx.input.isKeyPressed(Keys.SPACE)) {vida=0;vida2=0;}
	}
	

	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void hide() {}
	@Override public void dispose() {}

}