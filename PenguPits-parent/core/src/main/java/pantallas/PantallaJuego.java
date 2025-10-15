package pantallas;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import elementos.Imagen;
import elementos.Pengu;
import escenas.Hud;
import utiles.Config;
import utiles.Render;

public class PantallaJuego implements Screen {
	Imagen fondo;
	Hud hud;
	Pengu pengu,pengu2;
	int vida2 = 0;
	int vida = 0;
	int piso = 120;
	int posX = 100;
	
	public void show() {
		
		fondo = new Imagen("FondoJuego1.png");
		fondo.setSize(Config.ANCHO, Config.ALTO);
		hud = new Hud();
		pengu = new Pengu(100, piso,1);
		pengu2 = new Pengu(1000, piso,2);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla();
		Render.batch.begin();
			fondo.dibujar();
			hud.actualizarHud(vida,vida2);
			hud.dibujarHud();
			pengu2.actualizar(2);
			pengu.actualizar(1);
			
	    
		Render.batch.end();
		
		if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){vida++;}
		if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)){vida2++;}
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
