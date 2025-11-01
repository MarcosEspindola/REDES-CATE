package utiles;

import com.badlogic.gdx.graphics.Texture; // ¡CRÍTICO! Necesario para declarar variables de tipo Texture

public class Global {

	public static int puntos1=0, puntos2=0, puntosFin = 2;
	public static boolean terminaJuego = false;
	public static boolean empieza = false;
    
    // RECURSOS COMPARTIDOS
	public static Texture TEXTURA_BALA;
	public static Texture TEXTURA_FONDO_MENU; 
    public static Texture TEXTURA_LOGO;       

    // ARRAYS PARA LAS TEXTURAS DEL HUD (5 estados de vida para cada jugador)
    public static Texture[] TEXTURAS_HUD_DERECHA = new Texture[5]; 
    public static Texture[] TEXTURAS_HUD_IZQUIERDA = new Texture[5];
	
}
