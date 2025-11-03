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

public class PantallaJuego implements Screen {
    
    // CRÍTICO: Referencia estática para que PantallaFin acceda al hilo de red
    private static HiloCliente hiloClienteEstatico; 
    
	Imagen fondo;
	Hud hud;
	Pengu pengu,pengu2; 
	
	private HiloCliente hc; 
    private int jugadorID = 0; 

    // Las vidas deben ser públicas para que HiloCliente pueda leerlas 
	public int vida2 = 5; 
	public int vida = 5;  
    
	private Texture tFondo; // Textura del fondo de juego
	private Array<Bala> balas = new Array<>(); 
	private Map<Integer, Bala> balasMapa = new HashMap<>(); 
	
	Config t;
	int piso = 120;
	
	@Override
	public void show() {
		// 1. CARGA DE RECURSOS
		tFondo = new Texture("FondoJuego1.png");
		fondo = new Imagen(tFondo);
		fondo.setSize(Config.ANCHO, Config.ALTO);
		
		hud = new Hud();
		pengu = new Pengu(100, piso, 1); 
		pengu2 = new Pengu(1000, piso, 2);
		
		t = new Config();
		t.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
		
		// ----------------------------------------------------------------------
		// *** CORRECCIÓN CRÍTICA PARA EL REINICIO DE PARTIDA ***
        // 1. Detener y limpiar cualquier hilo de red anterior
        if (hiloClienteEstatico != null) {
            System.out.println("Deteniendo hilo de red anterior para reinicio...");
            hiloClienteEstatico.detener(); // Asegurar que el socket se cierre
            hiloClienteEstatico = null;
        }
        
		// 2. INICIALIZAR NUEVO HILO CLIENTE Y CONECTAR
		hc = new HiloCliente(); 
		hc.setPantallaJuego(this); 
		hc.start();
        
        // ¡CRÍTICO! Guardar la referencia estática para PantallaFin
        hiloClienteEstatico = hc; 
        // ----------------------------------------------------------------------
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla();
		
	    manejarInput(); 
		
		Render.batch.begin();
		
		if (!Global.empieza) {
			fondo.dibujar();
			t.dibujarTexto("Esperando Jugadores (ID: " + jugadorID + ")", 100, 100);
		} else {
			fondo.dibujar();
			
			hud.actualizarHud(vida, vida2);
			hud.dibujarHud();
			
			pengu.actualizar(1); 
			pengu2.actualizar(2);
			
			// Dibujar las balas
			for (Bala b : balas) {
			    b.actualizar(); 
			    b.dibujarBala();
			}
		}
		
		Render.batch.end();
	}
	
    // Método estático para que PantallaFin acceda al hilo de red
    public static HiloCliente getHiloClienteEstatico() {
        return hiloClienteEstatico;
    }
    
	/**
	 * Mapea las teclas de entrada del jugador local a comandos de red.
	 */
	private void manejarInput() {
	    if (hc == null || jugadorID == 0 || !Global.empieza) return; 

	    Pengu jugadorLocal = (jugadorID == 1) ? pengu : pengu2;

	    // --- 1. Movimiento Horizontal ---
	    boolean movDerecha = Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);
	    boolean movIzquierda = Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
	    
	    // Evita enviar el comando MOV_FIN si ya se está enviando MOV_D o MOV_A
	    if (movDerecha) {
	        hc.enviarMensaje("MOV_D_INICIO");
	        jugadorLocal.setMirandoDerecha(true, jugadorID);
	    } else if (movIzquierda) {
	        hc.enviarMensaje("MOV_A_INICIO");
	        jugadorLocal.setMirandoDerecha(false, jugadorID);
	    } else {
            // Asegura que solo envíe MOV_FIN una vez cuando se suelta la tecla
            if (Gdx.input.isKeyJustPressed(Keys.D) || Gdx.input.isKeyJustPressed(Keys.RIGHT) || 
                Gdx.input.isKeyJustPressed(Keys.A) || Gdx.input.isKeyJustPressed(Keys.LEFT)) {
                // No enviamos MOV_FIN si se acaba de presionar una tecla de movimiento
            } else {
                // Aquí deberías tener una lógica para enviar MOV_FIN solo una vez al soltar
                // Por ahora lo dejamos simple para evitar sobrecarga de red:
                hc.enviarMensaje("MOV_FIN");
            }
	    }

	    // --- 2. Salto ---
	    if (Gdx.input.isKeyJustPressed(Keys.W) || Gdx.input.isKeyJustPressed(Keys.UP)) {
	        hc.enviarMensaje("SALTAR");
	    }
	    
	    // --- 3. Disparo ---
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
        
        String[] partes = estadoMsg.substring("ESTADO:".length()).split(":");
        
        if (partes.length < 4) { 
            System.err.println("Mensaje de ESTADO incompleto. (Partes insuficientes)");
            return; 
        }

        try {
            // A. POSICIÓN Y VIDA
            String[] p1Pos = partes[0].split(",");
            pengu.setX(Float.parseFloat(p1Pos[0]));
            pengu.setY(Float.parseFloat(p1Pos[1]));
            
            String[] p2Pos = partes[1].split(",");
            pengu2.setX2(Float.parseFloat(p2Pos[0])); 
            pengu2.setY2(Float.parseFloat(p2Pos[1]));
            
            String[] vidas = partes[2].split(",");
            this.vida = Integer.parseInt(vidas[0]); 
            this.vida2 = Integer.parseInt(vidas[1]); 
            
            // B. DIRECCIÓN (parte[3])
            String[] direcciones = partes[3].split(","); 
            pengu.setMirandoDerecha(direcciones[0].equals("D"), 1);
            pengu2.setMirandoDerecha(direcciones[1].equals("D"), 2);
            
            // C. BALAS (parte opcional: partes[4] si existe)
            String balasData = "";
            if (partes.length > 4) {
                 balasData = partes[4]; 
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
                            balaExistente = new Bala(idBala, x, y, dir);
                            balas.add(balaExistente);
                        }
                        
                        balaExistente.setX(x);
                        balaExistente.setY(y);
                        
                        nuevasBalasMapa.put(idBala, balaExistente);
                    }
                }
            }
            
            // D. ELIMINAR BALAS ANTIGUAS
            Array<Bala> aEliminar = new Array<>();
            for(Bala b : balas) {
                if (!nuevasBalasMapa.containsKey(b.getIdBala())) {
                    aEliminar.add(b);
                    b.dispose(); 
                }
            }
            balas.removeAll(aEliminar, true);
            balasMapa = nuevasBalasMapa; 
            
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
		// EL HiloCliente NO se detiene aquí, ya que debe sobrevivir para el reinicio.
		
        // Liberar objetos de balas
		for (Bala b : balas) {
		    b.dispose();
		}
        
        // Liberar pingüinos
        if (pengu != null) pengu.dispose();
        if (pengu2 != null) pengu2.dispose();
		
		// Liberar recurso del fondo de juego
		if (tFondo != null) {
			tFondo.dispose();
		}
	}
}