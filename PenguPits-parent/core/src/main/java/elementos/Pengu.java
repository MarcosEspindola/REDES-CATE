package elementos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import utiles.Render;

public class Pengu extends Actor {
	
	// --- ELEMENTOS DE ANIMACIÓN ---
	private Animation<TextureRegion> animation; // Objeto que gestiona la secuencia de frames.
	private float tiempo; // Contador para saber en qué punto de la animación estamos.
	private TextureRegion[] regionsMovimiento; // Los cuadros individuales de la animación.
	private TextureRegion frameActual; // El dibujo actual que se está mostrando.
	private Texture imagen; // El archivo de imagen principal que contiene todos los frames.
	
    // --- ESTADO Y POSICIONES (Actualizado por la red) ---
		
	// Indica si la imagen debe estar 'flipeada' (mirando a la derecha o izquierda).
	private boolean mirandoDerecha1 = true; 
	private boolean mirandoDerecha2 = false; 
    
    // Almacenamos la posición anterior (lastX) para saber si el pingüino se está moviendo.
    private float lastX1, lastX2; 
    
    // Las áreas de colisión (hitbox) del cliente, usadas para el dibujo, no para la lógica.
	private Rectangle colision1; 
    private Rectangle colision2;
	
	public float alto,ancho; // Tamaño del personaje.
    // Posiciones del Jugador 1 (x, y) y Jugador 2 (x2, y2).
	public float x, y ,x2, y2;
	int vel = 2, salto = 17; // Variables de velocidad no utilizadas (el servidor las maneja).
	
	
	// Constructor: Crea el pingüino y prepara sus animaciones.
	public Pengu(float x, float y,int pj){
		
		if(pj==1) { // Si somos el Jugador 1
			imagen = new Texture("pengu/pengu1spr.png");
			colision1 = new Rectangle(x, y, 210, 210);
			this.x = x;
			this.y = y;
            this.lastX1 = x;
		}
		if(pj==2) { // Si somos el Jugador 2
			imagen = new Texture("pengu/pengu2spr.png");
			colision2 = new Rectangle(x, y, 210, 210);
			this.x2 = x;
			this.y2 = y;
            this.lastX2 = x;
		}
		
		// Divide el archivo de imagen grande en 5 cuadros individuales (frames).
		TextureRegion[][] tmp = TextureRegion.split(imagen, imagen.getWidth()/5, imagen.getHeight());
		regionsMovimiento = new TextureRegion[5];
		
		for(int i=0; i<5;i++) regionsMovimiento[i]= tmp[0][i];
		
		// Crea la animación: 0.1f segundos por cuadro.
		animation = new Animation<TextureRegion>(0.1f, regionsMovimiento);
		
	}
	
	// Método principal llamado en cada frame para dibujar y decidir qué frame mostrar.
	public void actualizar(int pj) {
	    
	    float currentX, currentY;
	    boolean mirandoDerecha;

        // Determina qué conjunto de variables usar (J1 o J2)
	    if (pj == 1) {
	        currentX = this.x; currentY = this.y; 
	        mirandoDerecha = this.mirandoDerecha1;
	        colision1.setPosition(currentX, currentY);
	    } else { // pj == 2
	        currentX = this.x2; currentY = this.y2; 
	        mirandoDerecha = this.mirandoDerecha2;
	        colision2.setPosition(currentX, currentY);
	    }
	    
	    // --- 1. LÓGICA DE ANIMACIÓN (Decide si correr o quedarse quieto) ---
	    final float EPSILON = 0.01f;
	    final float ANIMATION_SPEED_FACTOR = 1.5f;
	    boolean isMoving = false;
	    
	    // Compara la posición actual con la última posición conocida.
	    if (pj == 1) {
	        isMoving = (Math.abs(currentX - lastX1) > EPSILON); 
	        lastX1 = currentX; 
	    } else {
	        isMoving = (Math.abs(currentX - lastX2) > EPSILON);
	        lastX2 = currentX; 
	    }

	    // Si se está moviendo, avanza el contador de la animación.
	    if (isMoving) {
	        tiempo += com.badlogic.gdx.Gdx.graphics.getDeltaTime() * ANIMATION_SPEED_FACTOR;
	    } else {
	        // Si está quieto, resetea el tiempo para que muestre el frame de 'parado'.
	        tiempo = 0f; 
	    }
	    
	    // --------------------------------------------------------------------
	    // 2. DIBUJADO Y FLIP (Cambio de orientación)
	    // --------------------------------------------------------------------

        // Obtiene el frame que le toca mostrar según el tiempo transcurrido.
	    TextureRegion frameActual = animation.getKeyFrame(tiempo, true);
	    
	    // Lógica para girar el sprite si está mirando en la dirección opuesta al dibujo original.
	    if (!mirandoDerecha && !frameActual.isFlipX()) {
	        frameActual.flip(true, false); 
	    } else if (mirandoDerecha && frameActual.isFlipX()) {
	        frameActual.flip(true, false);
	    }
	    
	    // Dibuja el frame actual en la posición (X, Y) que dictó el servidor.
	    Render.batch.draw(frameActual, currentX, currentY);
	}
	
	// Método llamado por la red para actualizar si el pingüino mira a la derecha o izquierda.
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
	
    // --- Métodos Setter/Getter (para que la red actualice la posición) ---
	public float getX(){ return x; }
	public float getY(){ return y; }
	public float getX2(){ return x2; }
	public float getY2(){ return y2; }
	
	public void setX(float x){ this.x = x; }
	public void setY(float y){ this.y = y; }
	
	public void setX2(float x2){ this.x2 = x2; }
	public void setY2(float y2){ this.y2 = y2; }

    @Override
    // Método de limpieza: Libera el archivo de imagen de la memoria cuando el juego termina.
    public void dispose() {
        if (imagen != null) {
            imagen.dispose();
        }
    }
}