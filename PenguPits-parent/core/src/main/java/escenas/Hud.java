package escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import elementos.Imagen;
import utiles.Global; // Necesario para acceder a las texturas de vida pre-cargadas.

public class Hud {

    // Arrays para guardar las 5 imágenes posibles de la barra de vida de cada jugador (100%, 75%, etc.)
    private Imagen[] DhudImages; // Barras de vida del jugador Derecho.
    private Imagen[] IhudImages; // Barras de vida del jugador Izquierdo.
    private int actual1,actual2; // Índice de la imagen que se debe mostrar para J1 y J2 (0 a 4).

    // Constructor: Inicializa las imágenes y las coloca en su lugar.
    public Hud() {
       
        DhudImages = new Imagen[5];
        IhudImages = new Imagen[5];

        // Recorre los 5 estados de vida y crea un objeto Imagen para cada uno.
        for (int i = 0; i < 5; i++) {
            // Usa las texturas que se cargaron globalmente al inicio del juego.
            DhudImages[i] = new Imagen(Global.TEXTURAS_HUD_DERECHA[i]); 
            IhudImages[i] = new Imagen(Global.TEXTURAS_HUD_IZQUIERDA[i]);
            
            // Define el tamaño y la posición en la pantalla (la misma para los 5 estados).
            DhudImages[i].setSize(500, 150);
            DhudImages[i].setPosition(20, 600); 
            
            IhudImages[i].setSize(500, 150);
            IhudImages[i].setPosition(780, 600); 
        }

       
        actual1 = 0; // Estado inicial: 5 vidas (índice 0).
        actual2 = 0; // Estado inicial: 5 vidas (índice 0).
    }

   
    // Dibuja la barra de vida actual para cada jugador.
    public void dibujarHud() {
        DhudImages[actual1].dibujar();
        IhudImages[actual2].dibujar();
    }

    
    // Método para cambiar la barra de vida visible según la vida real.
    public void actualizarHud(int vida, int vida2) {
        
        // 1. Clampear la vida: Nos aseguramos de que los valores de vida estén entre 0 y 5.
        int v1 = Math.max(0, Math.min(5, vida));
        int v2 = Math.max(0, Math.min(5, vida2));
        
        // 2. Determinar el índice (0=Máximo, 4=Mínimo)
        
        // Lógica para Jugador 1:
        if (v1 == 5) {
            actual1 = 0; 
        } else if (v1 == 4) {
            actual1 = 1; 
        } else if (v1 == 3) {
            actual1 = 2; 
        } else if (v1 == 2) {
            actual1 = 3; 
        } else { // Si la vida es 1 o 0
            actual1 = 4; 
        }
        
        // Lógica para Jugador 2: (igual que J1)
        if (v2 == 5) {
            actual2 = 0; 
        } else if (v2 == 4) {
            actual2 = 1; 
        } else if (v2 == 3) {
            actual2 = 2; 
        } else if (v2 == 2) {
            actual2 = 3; 
        } else { // Si la vida es 1 o 0
            actual2 = 4; 
        }
    }

   
}