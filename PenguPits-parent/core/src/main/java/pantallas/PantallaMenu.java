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
import utiles.Global;
import utiles.Render;

public class PantallaMenu implements Screen {
	
	Imagen fondo,logo; // Dibujos del fondo y el logo.
	Config t,t2,t3,TEST; // Objetos para dibujar texto (opciones y debug).
	int opc=1; // Opción seleccionada actualmente (1=Jugar, 2=Opciones, 3=Salir).
	ShapeRenderer sr; // Herramienta para dibujar figuras geométricas (los rectángulos de selección).
	
	
	@Override
	// Se ejecuta cuando se muestra la pantalla.
	public void show() {
		    // Usamos las texturas que se cargaron al inicio de la aplicación.
		    fondo = new Imagen(Global.TEXTURA_FONDO_MENU); 
		    fondo.setSize(Config.ANCHO, Config.ALTO);
		    
		    logo = new Imagen(Global.TEXTURA_LOGO);
		    logo.setSize(307, 204);
		    logo.setPosition(80, 550);
		    
		    // Inicializamos las herramientas para dibujar texto con estilo.
		    t = new Config(); t.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
			t2 = new Config(); t2.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
			t3 = new Config(); t3.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
		    
		    TEST = new Config();
		    TEST.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
            // Asigna la clase Entradas para leer el teclado y el ratón.
		    Gdx.input.setInputProcessor(new Entradas()); 
		    
		    sr = new ShapeRenderer(); // Inicializa la herramienta de dibujo de formas.


	}
 
	@Override
	// El bucle de dibujo y actualización constante.
	public void render(float delta) {
		Render.limpiarPantalla();
		Render.batch.begin(); // Comienza el dibujo de imágenes.
        
		// Dibuja el fondo, el logo y las opciones de texto.
		fondo.dibujar();
		logo.dibujar();
		t.dibujarTexto("Iniciar Partida",120, 285+160);
		t2.dibujarTexto("Opciones",120, 285+80);
		t3.dibujarTexto("Salir",120, 285);
        
        // Muestra las coordenadas del ratón (para debugging).
		TEST.dibujarTexto("CORDS : " + Entradas.mouseX + " CORDS Y: " + (Entradas.mouseY) ,120, 100);	
		Render.batch.end(); // Termina el dibujo de imágenes.
		
		// Dibuja los recuadros invisibles para las opciones (usando ShapeRenderer).
		sr.begin(ShapeType.Line);
			sr.setColor(Color.BLACK);
            // Estos rectángulos sirven como áreas de clic para el ratón
			sr.rect(120, 223, 110, 63); // Salir
			sr.rect(120, 300, 219, 66); // Opciones
			sr.rect(120, 385, 340, 62); // Iniciar Partida
			
		sr.end();
		
		// --- Lógica de Navegación por Teclado (Flechas Arriba/Abajo) ---
		
		if (Entradas.abajo==true){
			Entradas.abajo=false; // Resetea la pulsación.
			opc++;
			if (opc>3) { opc=1; } // Vuelve al inicio si pasa de 3.
		}
		
		if (Entradas.arriba==true){
			Entradas.arriba=false; // Resetea la pulsación.
			opc--;
			if (opc<1) { opc=3; } // Vuelve al final si pasa de 1.
		}
		
		// --- Resaltado de Opciones (Cambia el color del texto) ---
		
		if (opc==1) { // Iniciar Partida
			t.setColor(Color.CORAL);
			t2.setColor(Color.WHITE);
			t3.setColor(Color.WHITE);
		}
		if (opc==2) { // Opciones
			t.setColor(Color.WHITE);
			t2.setColor(Color.CORAL);
			t3.setColor(Color.WHITE);
		}
		if (opc==3) { // Salir
			t.setColor(Color.WHITE);
			t2.setColor(Color.WHITE);
			t3.setColor(Color.CORAL);
		}
		
		// --- Acciones por Teclado (ENTER) ---
		
		// Si se presiona ENTER y la opción es Iniciar Partida (opc==1)
		if (Gdx.input.isKeyPressed(Keys.ENTER) && opc==1){Render.app.setScreen(new PantallaJuego());}
		
		// Si se presiona ENTER y la opción es Salir (opc==3)
		if (Gdx.input.isKeyPressed(Keys.ENTER) && opc==3){Gdx.app.exit();}
		
		// --- Acciones por Ratón (Clic Izquierdo) ---
		
		// Si se hace clic izquierdo y la opción es Iniciar Partida (opc==1)
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && opc==1){
			// Verifica que el clic haya ocurrido dentro del rectángulo de "Iniciar Partida"
			if(Entradas.mouseX>120 && Entradas.mouseX<460 && Entradas.mouseY<449 && Entradas.mouseY>385) {
				Render.app.setScreen(new PantallaJuego());
			} 
		} 
		
		// Si se hace clic izquierdo y la opción es Salir (opc==3)
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && opc==3){
			// Verifica que el clic haya ocurrido dentro del rectángulo de "Salir"
			if(Entradas.mouseX>120 && Entradas.mouseX<230 && Entradas.mouseY<286 && Entradas.mouseY>223) {
				Gdx.app.exit();
			} 
		}
		
		// --- Selección de Opción al Pasar el Ratón ---

		// Si el ratón está sobre "Iniciar Partida"
		if(Entradas.mouseX>120 && Entradas.mouseX<460 && Entradas.mouseY<449 && Entradas.mouseY>385 ) {
			opc = 1;
		}
		
		// Si el ratón está sobre "Opciones"
		if(Entradas.mouseX>120 && Entradas.mouseX<339 && Entradas.mouseY<364 && Entradas.mouseY>300 ) {
			opc = 2;
		}
        
		// Si el ratón está sobre "Salir"
		if(Entradas.mouseX>120 && Entradas.mouseX<230 && Entradas.mouseY<286 && Entradas.mouseY>223) {
			opc = 3;
		}
	}
	// --- Métodos de ciclo de vida (no usados pero obligatorios) ---
	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void hide() {}
	@Override public void dispose() {}
}