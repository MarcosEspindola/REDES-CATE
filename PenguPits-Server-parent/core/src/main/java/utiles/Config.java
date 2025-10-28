package utiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Config {
	public static final int ANCHO = 1280;
	public static final int ALTO = 768;
	public static final String NOMBRE = "PenguPits2.0 Server";
	public int x = 0, y=0;
	
	BitmapFont font;
	
	public void Texto(String rutaFuente,int dimension, Color color){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(rutaFuente));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = dimension;
        parameter.color = color;
        parameter.shadowColor = Color.BLACK;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;

        font = generator.generateFont(parameter);
        generator.dispose();
		
	}
	
	public void dibujarTexto(String texto,int x , int y) {
		font.draw(Render.batch, texto,x,y); 
	}
	
	public void setColor(Color color){
	   font.setColor(color);
	}
}
