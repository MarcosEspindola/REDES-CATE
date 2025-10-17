package elementos;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import utiles.Render;

public class Pengu extends Actor {
	
	// --- CONSTANTES COMPARTIDAS (No necesitan ser duplicadas) ---
	private Animation<TextureRegion> animation;
	private float tiempo;
	private TextureRegion[] regionsMovimiento;
	private TextureRegion frameActual;
	private Texture imagen;
	
	// --- CONSTANTES COMPARTIDAS (No necesitan ser duplicadas) ---
	private final float GRAVEDAD = -0.8f; 
	private final float IMPULSO_SALTO = 15f; 
	private final float ALTURA_PISO = 120; // Para mayor claridad
	private final float ALTURA_TECHO = 600; // Para mayor claridad
	   
	    
		// --- VARIABLES DE ESTADO INDEPENDIENTES ---
		
		// Orientación (Flip del Sprite)
		private boolean mirandoDerecha1 = true; 
		private boolean mirandoDerecha2 = false; 
		
		// Salto y Gravedad (Velocidad Vertical)
		private float velocidadY1 = 0; 
		private float velocidadY2 = 0; 
		
		// Doble Salto (Contador)
		private int saltosRestantes1 = 2; 
		private int saltosRestantes2 = 2; 
		
		private Rectangle colision1; 
	    private Rectangle colision2;
	
	public float alto,ancho;
	public float x, y ,x2, y2;
	int vel = 2, salto = 17;
	
	
	public Pengu(float x, float y,int pj){
		
		if(pj==1) { 
			imagen = new Texture(Gdx.files.internal("pengu/pengu1spr.png"));
			colision1 = new Rectangle(x, y, 100, 120);
			this.x = x;
			this.y = y;
		}
		if(pj==2) { 
			imagen = new Texture(Gdx.files.internal("pengu/pengu2spr.png"));
			colision2 = new Rectangle(x, y, 100, 120);
			this.x2 = x;
			this.y2 = y;
		}
		
		TextureRegion[][] tmp = TextureRegion.split(imagen, imagen.getWidth()/5, imagen.getHeight());
		regionsMovimiento = new TextureRegion[5];
		
		for(int i=0; i<5;i++) regionsMovimiento[i]= tmp[0][i];
		
		animation = new Animation<TextureRegion>(0.1f,regionsMovimiento);
		
	}
	
	public void actualizar(int pj) {
	    
		boolean mirandoDerecha;
	    float velocidadY;
	    int saltosRestantes;
	    float currentX, currentY;
	    int keyMoveRight, keyMoveLeft, keyJump;

	    if (pj == 1) {
	        mirandoDerecha = this.mirandoDerecha1;
	        velocidadY = this.velocidadY1;
	        saltosRestantes = this.saltosRestantes1;
	        currentX = this.x; currentY = this.y; // <--- Usa x, y
	        colision1.setPosition(currentX, currentY);
	        keyMoveRight = Keys.D; keyMoveLeft = Keys.A; keyJump = Keys.W;
	    } else { // pj == 2
	        mirandoDerecha = this.mirandoDerecha2;
	        velocidadY = this.velocidadY2;
	        saltosRestantes = this.saltosRestantes2;
	        currentX = this.x2; currentY = this.y2; // <--- Usa x2, y2
	        colision2.setPosition(currentX, currentY);
	        keyMoveRight = Keys.L; keyMoveLeft = Keys.J; keyJump = Keys.I;
	    }

	    // Detección de movimiento para controlar el avance del tiempo de animación
	    boolean moviendoseHorizontalmente = Gdx.input.isKeyPressed(keyMoveRight) || Gdx.input.isKeyPressed(keyMoveLeft);
	    boolean estaMoviendose = moviendoseHorizontalmente || (velocidadY != 0);


	    // ----------------------------------------------------
	    // 2. CONTROL DE TIEMPO DE ANIMACIÓN (Congelamiento)
	    // ----------------------------------------------------
	    // Solo avanza el tiempo si el personaje está en movimiento o en el aire.
	    if (estaMoviendose) {
	        tiempo += Gdx.graphics.getDeltaTime();
	    }
	    
	    // ----------------------------------------------------
	    // 3. LÓGICA DE MOVIMIENTO Y SALTO (Unificada)
	    // ----------------------------------------------------
	    
	    // --- Movimiento Horizontal y Orientación ---
	    if (Gdx.input.isKeyPressed(keyMoveRight)) {
	        mirandoDerecha = true;
	        currentX += vel;
	        if (currentX > 1100) currentX = 1100;

	    } else if (Gdx.input.isKeyPressed(keyMoveLeft)) {
	        mirandoDerecha = false;
	        currentX -= vel;
	        if (currentX < 0) currentX = 0;
	    }

	    // --- Salto/Doble Salto y Gravedad ---
	    if (Gdx.input.isKeyJustPressed(keyJump)) { 
	        if (saltosRestantes > 0) {
	            velocidadY = IMPULSO_SALTO;
	            saltosRestantes--;
	        }
	    }

	    // Aplicar Gravedad
	    velocidadY += GRAVEDAD;

	    // Aplicar Movimiento Vertical
	    currentY += velocidadY;

	    // Detección de Suelo y Reset de Saltos
	    if (currentY <= ALTURA_PISO) {
	        currentY = ALTURA_PISO;
	        velocidadY = 0;
	        saltosRestantes = 2;
	    }

	    // Restricción de Techo
	    if (currentY > ALTURA_TECHO) {
	        currentY = ALTURA_TECHO;
	        if (velocidadY > 0) {
	            velocidadY = 0;
	        }
	    }

	    // ----------------------------------------------------
	    // 4. GUARDAR ESTADOS GLOBALES (Actualiza las variables de clase)
	    // ----------------------------------------------------
	    
	    if (pj == 1) {
	        setX(currentX); setY(currentY);
	        this.mirandoDerecha1 = mirandoDerecha;
	        this.velocidadY1 = velocidadY;
	        this.saltosRestantes1 = saltosRestantes;
	    } else {
	        setX2(currentX); setY2(currentY);
	        this.mirandoDerecha2 = mirandoDerecha;
	        this.velocidadY2 = velocidadY;
	        this.saltosRestantes2 = saltosRestantes;
	    }

	    // ----------------------------------------------------
	    // 5. DIBUJADO Y FLIP
	    // ----------------------------------------------------

	    // Obtener el frame: Si 'tiempo' está congelado, devuelve el último frame.
	    TextureRegion frameActual = animation.getKeyFrame(tiempo, true);
	    
	    // Invertir el sprite horizontalmente (FLIP)
	    if (!mirandoDerecha && !frameActual.isFlipX()) {
	        frameActual.flip(true, false); 
	    } else if (mirandoDerecha && frameActual.isFlipX()) {
	        frameActual.flip(true, false);
	    }
	    
	    // Dibuja SIEMPRE el frameActual (el sprite estático ahora es el frame congelado)
	    Render.batch.draw(frameActual, currentX, currentY);
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
	
	
	
	
}
