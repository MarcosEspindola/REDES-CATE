package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import utiles.Render;
import pantallas.PantallaCarga;
import pantallas.PantallaJuego;
import pantallas.PantallaMenu;
import red.HiloServer;

public class Main extends Game{
	private HiloServer servidor; // Ahora guardará la referencia
	
    
     public void create() {
    	Render.app = this;
    	Render.batch = new SpriteBatch();
        
        // --- CORRECCIÓN CRÍTICA: INICIAR EL SERVIDOR ---
        servidor = new HiloServer(); // Inicializa el objeto HiloServer (con puerto 9003)
        servidor.start();            // Inicia el hilo de red
        // ------------------------------------------------
        
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
        super.dispose();
        if (servidor != null) {
            // Este código ahora se ejecutará, ya que 'servidor' no será nulo
            servidor.detener();; 
            try {
                servidor.join(); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
	