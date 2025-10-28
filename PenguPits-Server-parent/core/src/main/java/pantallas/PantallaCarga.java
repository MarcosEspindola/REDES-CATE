package pantallas;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import elementos.Imagen;
import elementos.Pengu;
import utiles.Config;
import utiles.Recursos;
import utiles.Render;

public class PantallaCarga implements Screen{
	
	Imagen fondo;
	Pengu pengu;
	int cont =0;
 
	@Override
	public void show() {
		System.out.println("hola");
		fondo = new Imagen("fondo.png");
		fondo.setSize(Config.ANCHO, Config.ALTO);
		
		
	}


	

	@Override
	public void render(float delta) {
		
		Render.limpiarPantalla();
		Render.batch.begin();
	
		cont++;
		
		fondo.dibujar();
			
		Render.batch.end();
		
		if (Gdx.input.isKeyPressed(Keys.SPACE )|| Gdx.input.isKeyPressed(Keys.ENTER)){Render.app.setScreen(new PantallaMenu());} //
		
		}

	@Override
	public void resize(int width, int height) {
    
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
		
	}
}
