package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture; // Importación necesaria para la carga global
import com.badlogic.gdx.utils.Array;
import elementos.Bala;
import elementos.Imagen;
import elementos.Pengu;
import escenas.Hud;
import red.HiloCliente; // Importación crítica
import utiles.Config;
import utiles.Global;
import utiles.Render;
import java.util.HashMap;
import java.util.Map;

public class PantallaJuego implements Screen {
	Imagen fondo;
	Hud hud;
	Pengu pengu,pengu2; // Jugador 1 y Jugador 2
	
	// Variables de red y estado
	private HiloCliente hc; // El hilo del cliente para comunicación
    private int jugadorID = 0; // ID asignado por el servidor (1 o 2)

	// Balas gestionadas por el cliente
	private Array<Bala> balas = new Array<>(); 
	private Map<Integer, Bala> balasMapa = new HashMap<>(); // Para acceso rápido por ID
	
	Config t;
	int vida2 = 5; // Vida de J2 (Actualizada por el servidor)
	int vida = 5;  // Vida de J1 (Actualizada por el servidor)
	int piso = 120;
	
	@Override
	public void show() {
		// 1. CARGA DE FONDOS (usando las texturas pre-cargadas en Main)
		// Nota: Global.TEXTURA_FONDO_MENU se usa para el fondo del menú. 
		// Asumo que tienes una textura para el fondo de juego (ej: Global.TEXTURA_FONDO_JUEGO)
		// Si 'FondoJuego1.png' no es global, debemos cargarlo aquí. 
		// Para simplicidad, carguemos el fondo aquí, ya que es un recurso de pantalla.
		Texture tFondo = new Texture("FondoJuego1.png");
		fondo = new Imagen(tFondo);
		fondo.setSize(Config.ANCHO, Config.ALTO);
		
		hud = new Hud();
		
		// 2. Inicialización de jugadores
		pengu = new Pengu(100, piso, 1); 
		pengu2 = new Pengu(1000, piso, 2);
		
		t = new Config();
		t.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
		
		// 3. INICIALIZAR HILO CLIENTE Y CONECTAR
		// *** IMPORTANTE: Se elimina HiloServer y la carga de Global.TEXTURA_BALA. ***
		hc = new HiloCliente(); 
		hc.setPantallaJuego(this); // Darle la referencia a esta pantalla
		hc.start();
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla();
		
		// 1. MANEJO DE ENTRADA Y ENVÍO DE COMANDOS
	    manejarInput(); 
		
		// 2. DIBUJADO Y ACTUALIZACIÓN VISUAL (TODO DENTRO DE UN SOLO BEGIN/END)
		Render.batch.begin();
		
		if (!Global.empieza) {
			// Lógica de espera (si el juego no ha empezado)
			fondo.dibujar();
			t.dibujarTexto("Esperando Jugadores (ID: " + jugadorID + ")", 100, 100);
		} else {
			// Lógica de juego
			fondo.dibujar();
			
			// Dibujar HUD
			hud.actualizarHud(vida, vida2);
			hud.dibujarHud();
			
			// Dibujar y actualizar la animación de los pingüinos 
			pengu.actualizar(1); 
			pengu2.actualizar(2);
			
			// Dibujar las balas
			for (Bala b : balas) { 
			    b.actualizar(); 
			    b.dibujarBala();
			}
		}
		
		// El batch termina una sola vez. (Corrección del error SpriteBatch.end/begin)
		Render.batch.end();
	}
	
	/**
	 * Mapea las teclas de entrada del jugador local a comandos de red.
	 */
	private void manejarInput() {
	    // Si no hay cliente o el juego no ha empezado, salir.
	    if (hc == null || jugadorID == 0 || !Global.empieza) return; 

	    Pengu jugadorLocal = (jugadorID == 1) ? pengu : pengu2;

	    // --- 1. Movimiento Horizontal ---
	    boolean movDerecha = Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);
	    boolean movIzquierda = Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
	    
	    // El cliente envía el comando CADA FRAME mientras la tecla esté presionada
	    if (movDerecha) {
	        hc.enviarMensaje("MOV_D_INICIO");
	        jugadorLocal.setMirandoDerecha(true, jugadorID);
	    } else if (movIzquierda) {
	        hc.enviarMensaje("MOV_A_INICIO");
	        jugadorLocal.setMirandoDerecha(false, jugadorID);
	    } else {
	        // Solo envía MOV_FIN si no hay teclas de movimiento presionadas
	        hc.enviarMensaje("MOV_FIN");
	    }

	    // --- 2. Salto (Solo cuando la tecla es presionada por primera vez: justPressed) ---
	    if (Gdx.input.isKeyJustPressed(Keys.W) || Gdx.input.isKeyJustPressed(Keys.UP)) {
	        hc.enviarMensaje("SALTAR");
	    }
	    
	    // --- 3. Disparo (Solo cuando la tecla es presionada por primera vez: justPressed) ---
	    if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
	        boolean mirandoDerecha = jugadorLocal.isMirandoDerecha(jugadorID);
	        String direccion = mirandoDerecha ? "DERECHA" : "IZQUIERDA";
	        hc.enviarMensaje("DISPARAR:" + direccion);
	    }
	}
	
	// ----------------------------------------------------------------------
	// MÉTODOS PARA SER LLAMADOS POR HILO CLIENTE
	// ----------------------------------------------------------------------

    public void setJugadorID(int id) {
        this.jugadorID = id;
    }
    
    /**
     * Procesa el mensaje ESTADO: y actualiza todos los elementos del juego.
     */
    public void actualizarEstadoServidor(String estadoMsg) {
        
        // 1. Limpiar el prefijo "ESTADO:"
        String[] partes = estadoMsg.substring("ESTADO:".length()).split(":");
        
        // CORRECCIÓN CLAVE: El mensaje debe tener al menos 4 partes de datos (P1, P2, Vidas, Direcciones).
        if (partes.length < 4) { 
            System.err.println("Mensaje de ESTADO incompleto. (Partes insuficientes)");
            return; 
        }

        try {
            // --- A. POSICIÓN Y VIDA ---
            
            // P1: X,Y (partes[0])
            String[] p1Pos = partes[0].split(",");
            pengu.setX(Float.parseFloat(p1Pos[0]));
            pengu.setY(Float.parseFloat(p1Pos[1]));
            
            // P2: X,Y (partes[1])
            String[] p2Pos = partes[1].split(",");
            pengu2.setX2(Float.parseFloat(p2Pos[0])); 
            pengu2.setY2(Float.parseFloat(p2Pos[1]));
            
            // Vidas (partes[2])
            String[] vidas = partes[2].split(",");
            this.vida = Integer.parseInt(vidas[0]); // Vida J1
            this.vida2 = Integer.parseInt(vidas[1]); // Vida J2
            
            // --- B. DIRECCIÓN DE LOS PERSONAJES (partes[3]) ---
            String[] direcciones = partes[3].split(","); 
            String dir1 = direcciones[0]; // "D" o "I"
            String dir2 = direcciones[1]; // "D" o "I"

            // Aplicar la dirección visual al Pengu (soluciona el problema del sprite flip)
            pengu.setMirandoDerecha(dir1.equals("D"), 1);
            pengu2.setMirandoDerecha(dir2.equals("D"), 2);
            
            // --- C. BALAS (parte opcional: partes[4] si existe) ---
            String balasData = "";
            // Verificamos si la parte de la data de balas existe (índice 4)
            if (partes.length > 4) {
                 balasData = partes[4]; // Ahora leemos la data de balas en partes[4]
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
                            // Nueva bala
                            balaExistente = new Bala(idBala, x, y, dir);
                            balas.add(balaExistente);
                        }
                        
                        // Actualizar posición de la bala existente
                        balaExistente.setX(x);
                        balaExistente.setY(y);
                        
                        nuevasBalasMapa.put(idBala, balaExistente);
                    }
                }
            }
            
            // --- D. ELIMINAR BALAS ANTIGUAS ---
            Array<Bala> aEliminar = new Array<>();
            for(Bala b : balas) {
                if (!nuevasBalasMapa.containsKey(b.getIdBala())) {
                    aEliminar.add(b);
                    b.dispose(); 
                }
            }
            balas.removeAll(aEliminar, true);
            balasMapa = nuevasBalasMapa; 
            
        } catch (NumberFormatException e) {
            System.err.println("Error al convertir números flotantes/enteros: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error desconocido al parsear estado: " + e.getMessage());
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
		// Detener el hilo cliente y cerrar el socket
		if (hc != null) hc.detener();
		
		// Liberar objetos de balas
		for (Bala b : balas) {
		    b.dispose();
		}
		
		// Liberar recurso del fondo de juego
		if (fondo != null && fondo.t != null) {
			fondo.t.dispose();
		}
	}
}