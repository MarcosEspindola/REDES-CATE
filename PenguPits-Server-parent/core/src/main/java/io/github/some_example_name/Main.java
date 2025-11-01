package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


import utiles.Global;
import utiles.Render;
import pantallas.PantallaMenu;

public class Main extends Game{
	
    @Override
    public void create() {
    	Render.app = this;
    	Render.batch = new SpriteBatch();
    	
        // *** CARGA DE RECURSOS GLOBALES EN EL HILO PRINCIPAL ***
    	Global.TEXTURA_FONDO_MENU = new Texture("FondoMenu.png");
        Global.TEXTURA_LOGO = new Texture("Logo.png");
        Global.TEXTURA_BALA = new Texture("bala.png"); 
        
    	this.setScreen(new PantallaMenu()); 
    }

    @Override
    public void render() {
    	super.render();
    }
    
    @Override
    public void dispose() {
        super.dispose();
        
        if (Render.batch != null) {
            Render.batch.dispose();
        }
        
        // LIBERAR TODAS LAS TEXTURAS GLOBALES CARGADAS
       if (Global.TEXTURA_FONDO_MENU != null) Global.TEXTURA_FONDO_MENU.dispose();
       if (Global.TEXTURA_LOGO != null) Global.TEXTURA_LOGO.dispose();
       if (Global.TEXTURA_BALA != null) Global.TEXTURA_BALA.dispose();
    }
}

	