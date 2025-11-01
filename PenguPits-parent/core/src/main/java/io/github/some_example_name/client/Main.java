package io.github.some_example_name.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture; // Necesario para cargar Textura
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import utiles.Global;
import utiles.Render;
import pantallas.PantallaMenu;
// Importamos PantallaJuego si lo necesitas, pero no es crucial aquí

public class Main extends Game{
	
    @Override
    public void create() {
    	Render.app = this;
    	Render.batch = new SpriteBatch();
    	
        // *** CARGA COMPLETA DE RECURSOS GLOBALES ***
    	Global.TEXTURA_FONDO_MENU = new Texture("FondoMenu.png");
        Global.TEXTURA_LOGO = new Texture("Logo.png");
        // Cargamos la textura de la bala también al inicio
        // (Asumiendo que TEXTURA_BALA está declarado en Global)
        Global.TEXTURA_BALA = new Texture("bala.png"); 
        
        Global.TEXTURAS_HUD_DERECHA[0] = new Texture("vida/Dhud100.png");
        Global.TEXTURAS_HUD_DERECHA[1] = new Texture("vida/Dhud75.png");
        Global.TEXTURAS_HUD_DERECHA[2] = new Texture("vida/Dhud50.png");
        Global.TEXTURAS_HUD_DERECHA[3] = new Texture("vida/Dhud25.png");
        Global.TEXTURAS_HUD_DERECHA[4] = new Texture("vida/Dhud0.png");

        Global.TEXTURAS_HUD_IZQUIERDA[0] = new Texture("vida/Ihud100.png");
        Global.TEXTURAS_HUD_IZQUIERDA[1] = new Texture("vida/Ihud75.png");
        Global.TEXTURAS_HUD_IZQUIERDA[2] = new Texture("vida/Ihud50.png");
        Global.TEXTURAS_HUD_IZQUIERDA[3] = new Texture("vida/Ihud25.png");
        Global.TEXTURAS_HUD_IZQUIERDA[4] = new Texture("vida/Ihud0.png");
        
    	this.setScreen(new PantallaMenu());
    }

    @Override
    public void render() {
    	super.render();
    }
    
    @Override
    public void dispose() {
       Render.batch.dispose();
       
       // LIBERAR TODAS LAS TEXTURAS GLOBALES
       if (Global.TEXTURA_FONDO_MENU != null) Global.TEXTURA_FONDO_MENU.dispose();
       if (Global.TEXTURA_LOGO != null) Global.TEXTURA_LOGO.dispose();
       
       // El dispose de la bala lo gestionamos en PantallaJuego.dispose()
       // si la pantalla no está visible. Si la aplicación se cierra aquí,
       // debemos liberar también la textura de la bala:
       if (Global.TEXTURA_BALA != null) Global.TEXTURA_BALA.dispose();
    }
}

	