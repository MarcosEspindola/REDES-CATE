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
import red.HiloCliente;
import utiles.Config;
import utiles.Global;
import utiles.Render;

public class PantallaJuego implements Screen {
	Imagen fondo;
	Hud hud;
	Pengu pengu,pengu2;
	Array<Bala> balas;
	Config t;
	
	private HiloCliente hc;
	private int miJugadorID = 0; // 0 = no asignado, 1 = J1, 2 = J2
	
	int vida2 = 5;
	int vida = 5;
	int piso = 122;
	
    // Referencia de la posición X anterior para calcular la dirección del movimiento
    private float lastReportedX1 = 100;
    private float lastReportedX2 = 1000;
    
    // Constructor (El Main debería llamar a este para pasar el HiloCliente)
    public PantallaJuego(HiloCliente cliente) {
        this.hc = cliente;
    }
    
    // Constructor sin argumentos (Usado si la inicialización se hace en show)
    public PantallaJuego() {}

	public void show() {
		fondo = new Imagen("FondoJuego1.png");
		fondo.setSize(Config.ANCHO, Config.ALTO);
		hud = new Hud();
		pengu = new Pengu(100, piso, 1);
		pengu2 = new Pengu(1000, piso, 2);
		balas = new Array<>();
		t = new Config();
		t.Texto("Thunder-BoldLC.otf", 80, Color.WHITE);
        
        // Inicializar y asignar el hilo cliente
        if (hc == null) {
            hc = new HiloCliente();
        }
        
        // CRÍTICO: Asignar esta pantalla al cliente para que pueda actualizarla
        hc.setPantallaJuego(this); 
        
        if (!hc.isAlive()) {
		    hc.start();
        }
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla();
		
		if (hc != null && Global.empieza) {
		    manejarInputRed(); // Enviar comandos al servidor
		}

		if(!Global.empieza) {
			Render.batch.begin();
			fondo.dibujar();
			t.dibujarTexto("Esperando Jugadores", 100, 100);
			Render.batch.end();
		} else {
		    Render.batch.begin();
		    fondo.dibujar();
		    hud.actualizarHud(vida,vida2);
		    hud.dibujarHud();
		    
		    // Los Pengu ahora solo se dibujan, el servidor controla la posición.
		    pengu.actualizar(1); 
		    pengu2.actualizar(2); 
		    
		    // La lógica de balas y colisión DEBE estar en el servidor.
		    
		    // Reinicio (solo para pruebas)
		    if(Gdx.input.isKeyPressed(Keys.T)) {
                if (hc != null) { hc.enviarMensaje("RESET:VIDA"); }
            }
            
		    Render.batch.end();
		}
	}
	
    /**
     * Envía las entradas del teclado como comandos al servidor.
     */
	private void manejarInputRed() {
        // Obtenemos la ID de la tecla para el jugador actual
        int keyMoveRight = (miJugadorID == 1) ? Keys.D : Keys.L;
        int keyMoveLeft = (miJugadorID == 1) ? Keys.A : Keys.J;
        int keyJump = (miJugadorID == 1) ? Keys.W : Keys.I;
        int keyFire = (miJugadorID == 1) ? Keys.SPACE : Keys.P;
        
        // Comando de Movimiento Horizontal
        if (Gdx.input.isKeyPressed(keyMoveRight)) {
            hc.enviarMensaje("MOV_D:1"); 
        } else if (Gdx.input.isKeyPressed(keyMoveLeft)) {
            hc.enviarMensaje("MOV_A:1"); 
        } 

        // Comando de Salto
        if (Gdx.input.isKeyJustPressed(keyJump)) { 
            hc.enviarMensaje("SALTAR:1"); 
        }
        
        // Comando de Disparo
		if(Gdx.input.isKeyJustPressed(keyFire)) {
		    hc.enviarMensaje("DISPARAR:1"); 
		}
	}
    
    /**
     * Método llamado por HiloCliente para actualizar el estado del juego.
     * Formato esperado: ESTADO:P1X,P1Y:P2X,P2Y:V1,V2
     */
    public void actualizarEstadoServidor(String estadoMsg) {
        String[] partes = estadoMsg.split(":"); 
        if (partes.length < 4) return; 

        try {
            // 1. Posiciones
            String[] p1Pos = partes[1].split(",");
            String[] p2Pos = partes[2].split(",");
            
            float newX1 = Float.parseFloat(p1Pos[0]);
            float newX2 = Float.parseFloat(p2Pos[0]);

            // 2. Vidas
            String[] vidas = partes[3].split(",");
            
            // --- APLICAR ESTADO ---
            
            // 3. Orientación (Flip) para J1
            if (newX1 > lastReportedX1) {
                pengu.setMirandoDerecha(true, 1);
            } else if (newX1 < lastReportedX1) {
                pengu.setMirandoDerecha(false, 1);
            }
            lastReportedX1 = newX1;
            
            // 4. Orientación (Flip) para J2
            if (newX2 > lastReportedX2) {
                pengu2.setMirandoDerecha(true, 2);
            } else if (newX2 < lastReportedX2) {
                pengu2.setMirandoDerecha(false, 2);
            }
            lastReportedX2 = newX2;

            // 5. Aplicar a los personajes (esto sobrescribe la posición local)
            pengu.setX(newX1); 
            pengu.setY(Float.parseFloat(p1Pos[1])); 
            pengu2.setX2(newX2); 
            pengu2.setY2(Float.parseFloat(p2Pos[1])); 
            
            vida = Integer.parseInt(vidas[0]);
            vida2 = Integer.parseInt(vidas[1]);

        } catch (Exception e) {
            System.err.println("Error al parsear ESTADO del servidor: " + e.getMessage());
        }
    }
    
    public void setJugadorID(int id) {
        this.miJugadorID = id;
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
        if (hc != null) {
            hc.detener();
        }
	}

}

