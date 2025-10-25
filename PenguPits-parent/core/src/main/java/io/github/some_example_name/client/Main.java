package io.github.some_example_name.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import utiles.Render;
import pantallas.PantallaCarga;
import pantallas.PantallaJuego;
import pantallas.PantallaMenu;

public class Main extends Game{
	
	
    
     public void create() {
    	Render.app = this;
    	Render.batch = new SpriteBatch();
    	this.setScreen(new PantallaMenu());
    	
    }

    @Override
    public void render() {
    	super.render();

       
    }
    
    public void update(){
    	
    	
    	
    }

    @Override
    public void dispose() {
       Render.batch.dispose();

    }
}

	