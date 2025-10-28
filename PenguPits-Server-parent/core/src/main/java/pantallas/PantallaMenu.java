package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import elementos.Imagen;
import utiles.Config;
import utiles.Entradas;
import utiles.Render;

public class PantallaMenu implements Screen {
	
	Imagen fondo,logo;
	Config t,t2,t3,TEST;
	int opc=1;
	ShapeRenderer sr;
	
	
	@Override
	public void show() {
		fondo = new Imagen("FondoMenu.png");
		fondo.setSize(Config.ANCHO, Config.ALTO);
		logo = new Imagen("Logo.png");
		logo.setSize(307, 204);
		logo.setPosition(80, 550);
		
		t = new Config();
		t.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
		t2 = new Config();
		t2.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
		t3 = new Config();
		t3.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
		
		TEST = new Config();
		TEST.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
		Gdx.input.setInputProcessor(new Entradas());
		
		sr = new ShapeRenderer();
	
	}
 
	@Override
	public void render(float delta) {
		Render.limpiarPantalla();
		Render.batch.begin();
		fondo.dibujar();
		logo.dibujar();
		t.dibujarTexto("Iniciar Partida",120, 285+160);
		t2.dibujarTexto("Opciones",120, 285+80);
		t3.dibujarTexto("Salir",120, 285);
		TEST.dibujarTexto("CORDS : " + Entradas.mouseX + " CORDS Y: " + (Entradas.mouseY) ,120, 100);	
		Render.batch.end();
		
		sr.begin(ShapeType.Line);
			sr.setColor(Color.BLACK);
			sr.rect(120, 223, 110, 63);
			sr.setColor(Color.BLACK);
			sr.rect(120, 300, 219, 66);
			sr.setColor(Color.BLACK);
			sr.rect(120, 385, 340, 62);
			
		sr.end();
		
		if (Entradas.abajo==true){
			Entradas.abajo=false;
			opc++;
			
			if (opc>3) {
				opc=1;
			}
			
		}
		
		if (Entradas.arriba==true){
			Entradas.arriba=false;
			opc--;
			
			if (opc<1) {
				opc=3;
			}
			
		}
		
		if (opc==1) {
			t.setColor(Color.CORAL);
			t2.setColor(Color.WHITE);
			t3.setColor(Color.WHITE);
		}
		if (opc==2) {
			t.setColor(Color.WHITE);
			t2.setColor(Color.CORAL);
			t3.setColor(Color.WHITE);
		}
		if (opc==3) {
			t.setColor(Color.WHITE);
			t2.setColor(Color.WHITE);
			t3.setColor(Color.CORAL);
		}
		
		if (Gdx.input.isKeyPressed(Keys.ENTER) && opc==1){Render.app.setScreen(new PantallaJuego());}
		
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && opc==1){
			if(Entradas.mouseX>120 && Entradas.mouseX<460 && Entradas.mouseY<449 && Entradas.mouseY>385) {
				Render.app.setScreen(new PantallaJuego());
			} 
		} 
		
		if (Gdx.input.isKeyPressed(Keys.ENTER) && opc==3){Gdx.app.exit();}
		
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && opc==3){
			if(Entradas.mouseX>120 && Entradas.mouseX<230 && Entradas.mouseY<286 && Entradas.mouseY>223) {
				Gdx.app.exit();
			} 
		}
		

		if(Entradas.mouseX>120 && Entradas.mouseX<460 && Entradas.mouseY<449 && Entradas.mouseY>385 ) {
			opc = 1;
		}
		
		if(Entradas.mouseX>120 && Entradas.mouseX<339 && Entradas.mouseY<364 && Entradas.mouseY>300 ) {
			opc = 2;
		}
		if(Entradas.mouseX>120 && Entradas.mouseX<230 && Entradas.mouseY<286 && Entradas.mouseY>223) {
			opc = 3;
		}
	}
	@Override
	public void resize(int width, int height) {
		
		
	}

	@Override
	public void pause() {
		
		
	}

	@Override
	public void resume() {
		
		
	}

	@Override
	public void hide() {
		
		
	}

	@Override
	public void dispose() {
		
		
	}

}
