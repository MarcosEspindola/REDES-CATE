package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture; // Necesario para cargar recursos
import com.badlogic.gdx.utils.Array;
import elementos.Bala;
import elementos.Imagen;
import elementos.Pengu;
import escenas.Hud;
import red.HiloCliente; // Usamos HiloCliente
import utiles.Config;
import utiles.Global;
import utiles.Render;
import java.util.HashMap;
import java.util.Map;


public class PantallaJuego implements Screen {
	Imagen fondo;
	Hud hud;
	Pengu pengu,pengu2;
	
	// Variables de red y estado
	private HiloCliente hc; // El hilo del cliente para comunicación
    private int jugadorID = 0; // ID asignado por el servidor (1 o 2)

	// Balas gestionadas por el cliente
	private Array<Bala> balas = new Array<>(); 
	private Map<Integer, Bala> balasMapa = new HashMap<>(); // Para acceso rápido por ID
    
    // Recurso de fondo de pantalla (para poder liberarlo)
    private Texture tFondo;

	Config t;
	int vida2 = 5; 
	int vida = 5; 
	int piso = 120;
	
	public void show() {
        // 1. Carga de Fondo (usando el constructor Imagen(Texture))
        tFondo = new Texture("FondoJuego1.png");
		fondo = new Imagen(tFondo);
		fondo.setSize(Config.ANCHO, Config.ALTO);
		
		hud = new Hud();
		pengu = new Pengu(100, piso,1);
		pengu2 = new Pengu(1000, piso,2);
		
        // Balas no necesitan inicializarse aquí, se inicializan con el estado de red.
		
		t = new Config();
		t.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
		
        // 2. INICIALIZAR HILO CLIENTE (La inicialización de HiloServer ahora va en PantallaServidorLauncher)
		hc = new HiloCliente();
		hc.setPantallaJuego(this);
		hc.start();
		
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla();
        
        // 1. Manejo de Entrada (Solo si el juego ha empezado)
        manejarInput();

        // 2. DIBUJADO: UN SOLO BEGIN/END
		Render.batch.begin();
		
		if(!Global.empieza) {
			fondo.dibujar();
			t.dibujarTexto("Esperando Jugadores (ID: " + jugadorID + ")", 100, 100);
		} else {
            fondo.dibujar();
            hud.actualizarHud(vida,vida2);
            hud.dibujarHud();

            // Dibujar y actualizar los pingüinos (la posición viene de la red)
            pengu.actualizar(1); 
            pengu2.actualizar(2);

            // Dibujar las balas
            for (Bala b : balas) {
                b.actualizar(); 
                b.dibujarBala();
            }
        }
        // El batch termina una sola vez.
		Render.batch.end();
	}
	
    // ----------------------------------------------------------------------
    // LÓGICA DE INPUT (CLIENTE A SERVIDOR)
    // ----------------------------------------------------------------------
    private void manejarInput() {
	    if (hc == null || jugadorID == 0 || !Global.empieza) return;

        Pengu jugadorLocal = (jugadorID == 1) ? pengu : pengu2;

	    // --- 1. Movimiento Horizontal ---
	    boolean movDerecha = Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);
	    boolean movIzquierda = Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
	    
	    if (movDerecha) {
	        hc.enviarMensaje("MOV_D_INICIO");
	        jugadorLocal.setMirandoDerecha(true, jugadorID);
	    } else if (movIzquierda) {
	        hc.enviarMensaje("MOV_A_INICIO");
	        jugadorLocal.setMirandoDerecha(false, jugadorID);
	    } else {
	        hc.enviarMensaje("MOV_FIN");
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
	// LÓGICA DE ESTADO (SERVIDOR A CLIENTE)
	// ----------------------------------------------------------------------
    public void setJugadorID(int id) {
        this.jugadorID = id;
    }

    public void actualizarEstadoServidor(String estadoMsg) {
        
        String[] partes = estadoMsg.substring("ESTADO:".length()).split(":");
        
        if (partes.length < 5) return; 

        try {
            // A. Posición y Vida
            String[] p1Pos = partes[0].split(",");
            pengu.setX(Float.parseFloat(p1Pos[0]));
            pengu.setY(Float.parseFloat(p1Pos[1]));
            
            String[] p2Pos = partes[1].split(",");
            pengu2.setX2(Float.parseFloat(p2Pos[0])); 
            pengu2.setY2(Float.parseFloat(p2Pos[1]));
            
            String[] vidas = partes[2].split(",");
            this.vida = Integer.parseInt(vidas[0]); 
            this.vida2 = Integer.parseInt(vidas[1]); 
            
            // B. Balas
            String balasData = partes[4]; 
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
            
            // C. Eliminación
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
            System.err.println("Error al parsear mensaje de estado: " + e.getMessage());
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
		// Detener el hilo cliente y cerrar el socket
		if (hc != null) hc.detener();
		
		// Liberar objetos de balas
		for (Bala b : balas) {
		    b.dispose();
		}
		
		// Liberar recurso del fondo de juego
		if (tFondo != null) {
			tFondo.dispose();
		}
	}
}