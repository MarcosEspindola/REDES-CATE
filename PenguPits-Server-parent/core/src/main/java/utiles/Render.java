package utiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.some_example_name.Main;

public class Render {
	public static SpriteBatch batch;
	public static Main app;
	
	public static void limpiarPantalla() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
    	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}
