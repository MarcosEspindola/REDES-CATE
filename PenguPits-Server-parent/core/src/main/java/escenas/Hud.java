package escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import elementos.Imagen;

public class Hud {

    private Imagen[] DhudImages;
    private Imagen[] IhudImages; 
    private int actual1,actual2; 

    public Hud() {
       
        DhudImages = new Imagen[5];
        DhudImages[0] = new Imagen("vida/Dhud100.png");
        DhudImages[1] = new Imagen("vida/Dhud75.png");
        DhudImages[2] = new Imagen("vida/Dhud50.png");
        DhudImages[3] = new Imagen("vida/Dhud25.png");
        DhudImages[4] = new Imagen("vida/Dhud0.png");
        
        IhudImages = new Imagen[5];
        IhudImages[0] = new Imagen("vida/Ihud100.png");
        IhudImages[1] = new Imagen("vida/Ihud75.png");
        IhudImages[2] = new Imagen("vida/Ihud50.png");
        IhudImages[3] = new Imagen("vida/Ihud25.png");
        IhudImages[4] = new Imagen("vida/Ihud0.png");

       
        for (Imagen img : DhudImages) {
            img.setSize(500, 150);
            img.setPosition(20, 600); 
        }
        for (Imagen img : IhudImages) {
            img.setSize(500, 150);
            img.setPosition(780, 600); 
        }

       
        actual1 = 5;
        actual2 = 5;
    }

   
    public void dibujarHud() {
        DhudImages[actual1].dibujar();
        IhudImages[actual2].dibujar();
    }

    
    public void actualizarHud(int vida, int vida2) {
        
        // 1. Clampear la vida: Aseguramos que la vida no baje de 0 ni suba de 5 (si 5 es el máximo).
        int v1 = Math.max(0, Math.min(5, vida));
        int v2 = Math.max(0, Math.min(5, vida2));
        
        // 2. Determinar el índice
        // El índice 0 corresponde a la vida máxima (5).
        // El índice 4 corresponde a la vida mínima (0 o 1).
        
        // Jugador 1:
        if (v1 == 5) {
            actual1 = 0; // Dhud100.png
        } else if (v1 == 4) {
            actual1 = 1; // Dhud75.png
        } else if (v1 == 3) {
            actual1 = 2; // Dhud50.png
        } else if (v1 == 2) {
            actual1 = 3; // Dhud25.png
        } else { // v1 es 1 o 0
            actual1 = 4; // Dhud0.png (vida 1 o 0)
        }
        
        // Jugador 2:
        if (v2 == 5) {
            actual2 = 0; // Ihud100.png
        } else if (v2 == 4) {
            actual2 = 1; // Ihud75.png
        } else if (v2 == 3) {
            actual2 = 2; // Ihud50.png
        } else if (v2 == 2) {
            actual2 = 3; // Ihud25.png
        } else { // v2 es 1 o 0
            actual2 = 4; // Ihud0.png (vida 1 o 0)
        }
    }

   
}