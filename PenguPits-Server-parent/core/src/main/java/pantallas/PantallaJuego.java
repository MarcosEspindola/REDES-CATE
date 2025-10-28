package pantallas;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import elementos.Bala;
import elementos.Imagen;
import elementos.Pengu;
import escenas.Hud;
import red.HiloServer;
import utiles.Config;
import utiles.Global;
import utiles.Render;


public class PantallaJuego implements Screen {
	Imagen fondo;
	Hud hud;
	Pengu pengu,pengu2;
	Array<Bala> balas; 
	private HiloServer hs;
	Config t;
	int vida2 = 5;
	int vida = 5;
	int piso = 120;
	
	public void show() {
		fondo = new Imagen("FondoJuego1.png");
		fondo.setSize(Config.ANCHO, Config.ALTO);
		hud = new Hud();
		pengu = new Pengu(100, piso,1);
		pengu2 = new Pengu(1000, piso,2);
		balas = new Array<>();
		t = new Config();
		t.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
		hs = new HiloServer();
		hs.start();
		
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla();
		if(!Global.empieza) {
			Render.batch.begin();
			fondo.dibujar();
			t.dibujarTexto("Esperando Jugadores", 100, 100);
			Render.batch.end();
		}else{
		Render.batch.begin();
		fondo.dibujar();
		hud.actualizarHud(vida,vida2);
		hud.dibujarHud();
		pengu2.actualizar(2);
		pengu.actualizar(1);
			
			
			
	    
		
		
		if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){vida--;}
		if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)){vida2--;}
		if(Gdx.input.isKeyPressed(Keys.T)){vida=5;vida2=5;}
		
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
		    // -----------------------------------------------------------------
		    // LÓGICA DE DISPARO - JUGADOR 1 (TECLA ESPACIO)
		    // -----------------------------------------------------------------
		    float inicioX = pengu.getX() + 100; // Desplazamiento horizontal
		    float inicioY = pengu.getY() + 100; // Altura aproximada de la boca

		    // Obtener la dirección del Pengu 1
		    boolean direccion = pengu.isMirandoDerecha(1);
		    
		    // Si dispara a la izquierda, ajustamos el inicioX para que salga del cañón
		    if (!direccion) {
		        inicioX = pengu.getX() - 30; // Ajuste para la izquierda
		    }
		    
		    // Crear y añadir la bala
		    Bala nuevaBala = new Bala(inicioX, inicioY, direccion);
		    balas.add(nuevaBala);
		}

		if (Gdx.input.isKeyJustPressed(Keys.P)) {
		    // -----------------------------------------------------------------
		    // LÓGICA DE DISPARO - JUGADOR 2 (TECLA P)
		    // -----------------------------------------------------------------
		    float inicioX = pengu2.getX2() + 100; // Desplazamiento horizontal (usa X2)
		    float inicioY = pengu2.getY2() + 95; // Altura aproximada de la boca (usa Y2)

		    // Obtener la dirección del Pengu 2
		    boolean direccion = pengu2.isMirandoDerecha(2);
		    
		    // Si dispara a la izquierda, ajustamos el inicioX para que salga del cañón
		    if (!direccion) {
		        inicioX = pengu2.getX2() - 30; // Ajuste para la izquierda
		    }
		    
		    // Crear y añadir la bala
		    Bala nuevaBala = new Bala(inicioX, inicioY, direccion);
		    balas.add(nuevaBala);
		}


		for (int i = 0; i < balas.size; i++) {
		    Bala balaActual = balas.get(i);
		    
		    balaActual.actualizar();
		    balaActual.dibujarBala();
		    
		 // --- LÓGICA DE COLISIÓN (Añadir esto) ---
		    boolean golpea = false;
		    
		    // 1. Comprobar si golpea al PENGU 1 (jugador enemigo si J2 disparó)
		    // El método 'overlaps' comprueba si dos rectángulos se intersecan
		    if (balaActual.getColision().overlaps(pengu.getColision(1))) {
		        vida--; // Aumenta la vida del jugador 1 (¡si es un golpe enemigo, esto debe ser daño a vida1!)
		        golpea = true;
		    }
		    
		    // 2. Comprobar si golpea al PENGU 2 (jugador enemigo si J1 disparó)
		    if (balaActual.getColision().overlaps(pengu2.getColision(2))) {
		        vida2--; // Aumenta la vida del jugador 2 (¡si es un golpe enemigo, esto debe ser daño a vida2!)
		        golpea = true;
		    }

		    // --- LÓGICA DE ELIMINACIÓN ---
		    // La bala se elimina si salió de la pantalla O si golpeó a un pengu
		    if (balaActual.debeEliminarse() || golpea) {
		    	System.out.println("Bala eliminada");
		        balas.removeIndex(i); 
		        i--; 
		    }
		    
		    
		    
		  
		}
		if(vida2 == 1){System.out.println("El Jugador 1 ha Ganado");}
	    if(vida == 1){System.out.println("El Jugador 2 ha Ganado");}
		Render.batch.end();
			}
	}
	

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
