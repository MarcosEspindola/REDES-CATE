package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import elementos.Bala;
import elementos.Imagen;
import elementos.Pengu;
import escenas.Hud;
import red.HiloCliente;
import utiles.Config;
import utiles.Global;
import utiles.Render;
import java.util.HashMap;
import java.util.Map;

// PantallaJuego: Dibuja el escenario, lee las teclas y gestiona la red cliente.
public class PantallaJuego implements Screen {
    
    // Referencia estática: Guardamos el hilo de red aquí para que la PantallaFin pueda cerrarlo.
    private static HiloCliente hiloClienteEstatico; 
    
	Imagen fondo;
	Hud hud; // La interfaz de vida.
	Pengu pengu,pengu2; // Los dos personajes.
	
	private HiloCliente hc; // El hilo de red que envía y recibe datos.
    private int jugadorID = 0; // Nuestro ID (1 o 2), asignado por el servidor.

    // Vidas: Son públicas para que el HiloCliente pueda leerlas y decidir si terminar el juego.
	public int vida2 = 5; 
	public int vida = 5;  
    
	private Texture tFondo; 
	private Array<Bala> balas = new Array<>(); // Lista de balas que vemos en pantalla.
	private Map<Integer, Bala> balasMapa = new HashMap<>(); // Mapa auxiliar para buscar balas por su ID.
	
	Config t;
	int piso = 120;
	
	@Override
	public void show() {
		// 1. CARGA DE RECURSOS
		tFondo = new Texture("FondoJuego1.png");
		fondo = new Imagen(tFondo);
		fondo.setSize(Config.ANCHO, Config.ALTO);
		
		hud = new Hud();
		pengu = new Pengu(100, piso, 1); // Crea el personaje visual J1
		pengu2 = new Pengu(1000, piso, 2); // Crea el personaje visual J2
		
		t = new Config();
		t.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
		
		// 2. INICIALIZAR HILO CLIENTE Y CONECTAR
        
        // Si venimos de un reinicio, detenemos el hilo viejo para evitar errores.
        if (hiloClienteEstatico != null) {
            hiloClienteEstatico.detener(); 
            hiloClienteEstatico = null;
        }
        
		hc = new HiloCliente(); // Creamos la nueva conexión
		hc.setPantallaJuego(this); // Le decimos al hilo que actualice ESTA pantalla
		hc.start(); // Iniciamos el hilo de red.
        
        // Guardamos la referencia del hilo actual.
        hiloClienteEstatico = hc; 
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla();
		
	    manejarInput(); // Lee las teclas del jugador local.
		
		Render.batch.begin();
		
		if (!Global.empieza) {
			// Si el servidor no ha dicho "Empieza", solo mostramos el fondo y el texto de espera.
			fondo.dibujar();
			t.dibujarTexto("Esperando Jugadores (ID: " + jugadorID + ")", 100, 100);
		} else {
			// El juego ya empezó:
			fondo.dibujar();
			
			hud.actualizarHud(vida, vida2);
			hud.dibujarHud();
			
			pengu.actualizar(1); // Dibuja y anima J1
			pengu2.actualizar(2); // Dibuja y anima J2
			
			// Dibuja todas las balas recibidas de la red.
			for (Bala b : balas) {
			    b.actualizar(); // Mueve el hitbox de la bala.
			    b.dibujarBala();
			}
		}
		
		Render.batch.end();
	}
	
    // Permite que la PantallaFin acceda al hilo de red para detenerlo/reiniciarlo.
    public static HiloCliente getHiloClienteEstatico() {
        return hiloClienteEstatico;
    }
    
	/**
	 * Mapea las teclas de entrada del jugador local a comandos de red.
	 */
	private void manejarInput() {
	    // Solo procesa si ya tenemos ID y el juego comenzó.
	    if (hc == null || jugadorID == 0 || !Global.empieza) return; 

	    // Usamos la referencia a nuestro propio personaje (J1 o J2)
	    Pengu jugadorLocal = (jugadorID == 1) ? pengu : pengu2;

	    // --- 1. Movimiento Horizontal ---
	    boolean movDerecha = Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);
	    boolean movIzquierda = Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
	    
	    if (movDerecha) {
	        hc.enviarMensaje("MOV_D_INICIO"); // Manda comando para ir a la derecha
	        jugadorLocal.setMirandoDerecha(true, jugadorID);
	    } else if (movIzquierda) {
	        hc.enviarMensaje("MOV_A_INICIO"); // Manda comando para ir a la izquierda
	        jugadorLocal.setMirandoDerecha(false, jugadorID);
	    } else {
            // Envía el comando para detener el movimiento horizontal.
            // Nota: Esta lógica debería ser más fina para evitar spam de red.
            hc.enviarMensaje("MOV_FIN"); 
	    }

	    // --- 2. Salto ---
	    if (Gdx.input.isKeyJustPressed(Keys.W) || Gdx.input.isKeyJustPressed(Keys.UP)) {
	        hc.enviarMensaje("SALTAR"); // Manda el comando de salto
	    }
	    
	    // --- 3. Disparo ---
	    if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
	        // Envía el comando DISPARAR + la dirección en la que estamos mirando.
	        boolean mirandoDerecha = jugadorLocal.isMirandoDerecha(jugadorID);
	        String direccion = mirandoDerecha ? "DERECHA" : "IZQUIERDA";
	        hc.enviarMensaje("DISPARAR:" + direccion);
	    }
	}
	
	// ----------------------------------------------------------------------
	// MÉTODOS PARA SER LLAMADOS POR HILO CLIENTE (Actualización de la Red)
	// ----------------------------------------------------------------------

    public void setJugadorID(int id) {
        this.jugadorID = id; // Asigna el ID que nos dio el servidor.
    }
    
    /**
     * Procesa el mensaje ESTADO: y actualiza todos los elementos del juego.
     */
    public void actualizarEstadoServidor(String estadoMsg) {
        
        String[] partes = estadoMsg.substring("ESTADO:".length()).split(":");
        
        if (partes.length < 4) { 
            System.err.println("Mensaje de ESTADO incompleto.");
            return; 
        }

        try {
            // A. POSICIÓN Y VIDA
            String[] p1Pos = partes[0].split(",");
            pengu.setX(Float.parseFloat(p1Pos[0])); // Mueve J1
            pengu.setY(Float.parseFloat(p1Pos[1]));
            
            String[] p2Pos = partes[1].split(",");
            pengu2.setX2(Float.parseFloat(p2Pos[0])); // Mueve J2
            pengu2.setY2(Float.parseFloat(p2Pos[1]));
            
            String[] vidas = partes[2].split(",");
            this.vida = Integer.parseInt(vidas[0]); // Actualiza vida J1
            this.vida2 = Integer.parseInt(vidas[1]); // Actualiza vida J2
            
            // B. DIRECCIÓN
            String[] direcciones = partes[3].split(","); 
            pengu.setMirandoDerecha(direcciones[0].equals("D"), 1); // Gira J1
            pengu2.setMirandoDerecha(direcciones[1].equals("D"), 2); // Gira J2
            
            // C. BALAS
            String balasData = "";
            if (partes.length > 4) {
                 balasData = partes[4]; // Obtiene la cadena de datos de balas.
            }

            Map<Integer, Bala> nuevasBalasMapa = new HashMap<>();

            if (!balasData.isEmpty()) {
                String[] balasArray = balasData.split(";");
                for (String balaStr : balasArray) {
                    String[] balaInfo = balaStr.split(",");
                    if (balaInfo.length == 4) {
                        int idBala = Integer.parseInt(balaInfo[0]);
                        float x = Float.parseFloat(balaInfo[1]);
                        float y = Float.parseFloat(balaInfo[2]);
                        boolean dir = balaInfo[3].equals("D");
                        
                        Bala balaExistente = balasMapa.get(idBala);
                        
                        if (balaExistente == null) {
                            // Si es una bala nueva, la creamos y la añadimos a la lista de dibujo.
                            balaExistente = new Bala(idBala, x, y, dir);
                            balas.add(balaExistente);
                        }
                        
                        // Actualizamos su posición con los datos del servidor.
                        balaExistente.setX(x);
                        balaExistente.setY(y);
                        
                        nuevasBalasMapa.put(idBala, balaExistente);
                    }
                }
            }
            
            // D. ELIMINAR BALAS ANTIGUAS (que ya no están en el servidor)
            Array<Bala> aEliminar = new Array<>();
            for(Bala b : balas) {
                if (!nuevasBalasMapa.containsKey(b.getIdBala())) {
                    aEliminar.add(b);
                    b.dispose(); // Liberamos los recursos visuales de esa bala.
                }
            }
            balas.removeAll(aEliminar, true); // Las quitamos de la lista de dibujo.
            balasMapa = nuevasBalasMapa; // Actualizamos el mapa de balas activas.
            
        } catch (Exception e) {
            System.err.println("Error al parsear estado: " + e.getMessage());
        }
    }
	

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {
		// El HiloCliente NO se detiene aquí, ya que debe sobrevivir para el reinicio.
		
        // Liberar todos los objetos de balas.
		for (Bala b : balas) {
		    b.dispose();
		}
        
        // Liberar personajes.
        if (pengu != null) pengu.dispose();
        if (pengu2 != null) pengu2.dispose();
		
		// Liberar la textura del fondo de juego.
		if (tFondo != null) {
			tFondo.dispose();
		}
	}
}