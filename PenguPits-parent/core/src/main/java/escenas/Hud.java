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

       
        actual1 = 0;
        actual2 = 0;
    }

   
    public void dibujarHud() {
        DhudImages[actual1].dibujar();
        IhudImages[actual2].dibujar();
    }

    
    public void actualizarHud(int vida,int vida2) {
       
        if (vida == 0) {
        	actual1 = 0; 
        } else if (vida == 1) {
        	actual1 = 1; 
        } else if (vida == 2) {
        	actual1 = 2; 
        } else if (vida == 3) {
        	actual1 = 3; 
        } else {
        	actual1 = 4;
        }
        
        if (vida2 == 0) {
        	actual2 = 0; 
        } else if (vida2 == 1) {
        	actual2 = 1; 
        } else if (vida2 == 2) {
        	actual2 = 2; 
        } else if (vida2 == 3) {
        	actual2 = 3; 
        } else {
        	actual2 = 4;
        }

    }

   
}