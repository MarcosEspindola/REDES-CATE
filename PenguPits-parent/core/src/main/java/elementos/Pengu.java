package elementos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import utiles.Render;

public class Pengu extends Actor {
	
	// --- CONSTANTES COMPARTIDAS ---
	private Animation<TextureRegion> animation;
	private float tiempo;
	private TextureRegion[] regionsMovimiento;
	private TextureRegion frameActual;
	private Texture imagen;
	
    // --- VARIABLES DE ESTADO LOCALES (CONTROLADAS POR EL SERVIDOR) ---
		
	// Orientación (Flip del Sprite)
	private boolean mirandoDerecha1 = true; 
	private boolean mirandoDerecha2 = false; 
    
    // Nueva variable para controlar la animación de movimiento
    private float lastX1, lastX2; 
    
    // Colisiones (solo se usan para lectura en el cliente, el server las calcula)
	private Rectangle colision1; 
    private Rectangle colision2;
	
	public float alto,ancho;
	public float x, y ,x2, y2;
	int vel = 2, salto = 17;
	
	
	public Pengu(float x, float y,int pj){
		
		if(pj==1) { 
			imagen = new Texture("pengu/pengu1spr.png");
			colision1 = new Rectangle(x, y, 210, 210);
			this.x = x;
			this.y = y;
            this.lastX1 = x;
		}
		if(pj==2) { 
			imagen = new Texture("pengu/pengu2spr.png");
			colision2 = new Rectangle(x, y, 210, 210);
			this.x2 = x;
			this.y2 = y;
            this.lastX2 = x;
		}
		
		TextureRegion[][] tmp = TextureRegion.split(imagen, imagen.getWidth()/5, imagen.getHeight());
		regionsMovimiento = new TextureRegion[5];
		
		for(int i=0; i<5;i++) regionsMovimiento[i]= tmp[0][i];
		
		animation = new Animation<TextureRegion>(0.1f, regionsMovimiento);
		
	}
	
	public void actualizar(int pj) {
	    
	    float currentX, currentY;
	    boolean mirandoDerecha;

	    if (pj == 1) {
	        currentX = this.x; currentY = this.y; 
	        mirandoDerecha = this.mirandoDerecha1;
	        colision1.setPosition(currentX, currentY);
	    } else { // pj == 2
	        currentX = this.x2; currentY = this.y2; 
	        mirandoDerecha = this.mirandoDerecha2;
	        colision2.setPosition(currentX, currentY);
	    }
	    
	    // --- 1. LÓGICA DE ANIMACIÓN ---
	    final float EPSILON = 0.01f;
	    final float ANIMATION_SPEED_FACTOR = 1.5f;
	    boolean isMoving = false;
	    
	    if (pj == 1) {
	        isMoving = (Math.abs(currentX - lastX1) > EPSILON); 
	        lastX1 = currentX; 
	    } else {
	        isMoving = (Math.abs(currentX - lastX2) > EPSILON);
	        lastX2 = currentX; 
	    }

	    // Solo avanza el tiempo de animación si hay movimiento
	    if (isMoving) {
	        // Multiplicar por el factor para que la animación se vea fluida
	        tiempo += com.badlogic.gdx.Gdx.graphics.getDeltaTime() * ANIMATION_SPEED_FACTOR;
	    } else {
	        // Al estar parado, reiniciamos el tiempo a 0.0f
	        tiempo = 0f; 
	    }
	    
	    // --------------------------------------------------------------------
	    // 2. DIBUJADO Y FLIP
	    // --------------------------------------------------------------------

	    TextureRegion frameActual = animation.getKeyFrame(tiempo, true);
	    
	    // Invertir el sprite horizontalmente (FLIP)
	    if (!mirandoDerecha && !frameActual.isFlipX()) {
	        frameActual.flip(true, false); 
	    } else if (mirandoDerecha && frameActual.isFlipX()) {
	        frameActual.flip(true, false);
	    }
	    
	    Render.batch.draw(frameActual, currentX, currentY);
	}
	
	// Método modificado para actualizar la orientación del pingüino
	public void setMirandoDerecha(boolean mirandoDerecha, int pj) {
        if (pj == 1) {
            this.mirandoDerecha1 = mirandoDerecha;
        } else {
            this.mirandoDerecha2 = mirandoDerecha;
        }
    }
    
	public Rectangle getColision(int pj) {
        return (pj == 1) ? colision1 : colision2;
    }
	
	public boolean isMirandoDerecha(int pj) {
	    if (pj == 1) {
	        return mirandoDerecha1;
	    } else {
	        return mirandoDerecha2;
	    }
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public float getX2(){
		return x2;
	}
	
	public float getY2(){
		return y2;
	}
	
	public void setX(float x){
		this.x = x;
	}
	 
	public void setY(float y){
		this.y = y;
	}
	
	public void setX2(float x2){
		this.x2 = x2;
	}
	
	public void setY2(float y2){
		this.y2 = y2;
	}

    @Override
    public void dispose() {
        // CORRECCIÓN: Liberar la textura del personaje al cerrar
        if (imagen != null) {
            imagen.dispose();
        }
    }
}
