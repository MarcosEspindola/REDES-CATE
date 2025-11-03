package io.github.some_example_name.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture; // Necesario para cargar Textura
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import utiles.Global;
import utiles.Render;
import pantallas.PantallaMenu;
// Importamos PantallaJuego si lo necesitas, pero no es crucial aquí

// Main: Es el punto de inicio de la aplicación (el motor del juego).
public class Main extends Game{
	
    @Override
    // El método 'create' se ejecuta una sola vez al arrancar el juego.
    public void create() {
    	Render.app = this;
    	Render.batch = new SpriteBatch(); // Prepara la herramienta para dibujar imágenes (SpriteBatch).
    	
        // *** CARGA COMPLETA DE RECURSOS GLOBALES ***
        // Aquí cargamos todas las imágenes necesarias de golpe en la memoria:
    	Global.TEXTURA_FONDO_MENU = new Texture("FondoMenu.png");
        Global.TEXTURA_LOGO = new Texture("Logo.png");
        Global.TEXTURA_BALA = new Texture("bala.png"); 
        
        // Cargamos los 5 estados de vida del HUD del Jugador DERECHO:
        Global.TEXTURAS_HUD_DERECHA[0] = new Texture("vida/Dhud100.png");
        Global.TEXTURAS_HUD_DERECHA[1] = new Texture("vida/Dhud75.png");
        Global.TEXTURAS_HUD_DERECHA[2] = new Texture("vida/Dhud50.png");
        Global.TEXTURAS_HUD_DERECHA[3] = new Texture("vida/Dhud25.png");
        Global.TEXTURAS_HUD_DERECHA[4] = new Texture("vida/Dhud0.png");

        // Cargamos los 5 estados de vida del HUD del Jugador IZQUIERDO:
        Global.TEXTURAS_HUD_IZQUIERDA[0] = new Texture("vida/Ihud100.png");
        Global.TEXTURAS_HUD_IZQUIERDA[1] = new Texture("vida/Ihud75.png");
        Global.TEXTURAS_HUD_IZQUIERDA[2] = new Texture("vida/Ihud50.png");
        Global.TEXTURAS_HUD_IZQUIERDA[3] = new Texture("vida/Ihud25.png");
        Global.TEXTURAS_HUD_IZQUIERDA[4] = new Texture("vida/Ihud0.png");
        
        // Una vez cargado todo, iniciamos el juego con la PantallaMenu.
    	this.setScreen(new PantallaMenu());
    }

    @Override
    // El método 'render' es el bucle principal. Llama a 'render()' de la pantalla actual.
    public void render() {
    	super.render();
    }
    
    @Override
    // El método 'dispose' se ejecuta cuando el juego se cierra para liberar memoria.
    public void dispose() {
       Render.batch.dispose();
       
       // LIBERAR TODAS LAS TEXTURAS GLOBALES (CRÍTICO para evitar fugas de memoria).
       if (Global.TEXTURA_FONDO_MENU != null) Global.TEXTURA_FONDO_MENU.dispose();
       if (Global.TEXTURA_LOGO != null) Global.TEXTURA_LOGO.dispose();
       if (Global.TEXTURA_BALA != null) Global.TEXTURA_BALA.dispose();
    }
}