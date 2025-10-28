package red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Locale;

import utiles.Global;

public class HiloServer extends Thread {
	private DatagramSocket conexion;
	private boolean fin = false;
	
    // Estructura para almacenar las direcciones de los clientes
	private DireccionRed[] clientes = new DireccionRed[2];
	private int cantClientes = 0;
    
    // --- ESTADO DEL JUEGO GESTIONADO POR EL SERVIDOR ---
    // Posición del Jugador 1
    private float p1X = 100, p1Y = 120; 
    // Posición del Jugador 2
    private float p2X = 1000, p2Y = 120; 
    // Vidas
    private int p1Vida = 5;
    private int p2Vida = 5;
    
    // Constantes para simulación de física en el servidor
    private final float VELOCIDAD_MOVIMIENTO = 100; // Velocidad horizontal en píxeles/segundo
    private final float IMPULSO_SALTO = 600f; // Impulso vertical inicial
    private final float GRAVEDAD = -500f; // Aceleración de la gravedad (píxeles/segundo^2)
    private final float POSICION_PISO = 120;
    
    // Variables para simular la física (el servidor es el dueño de la lógica)
    private float p1VelocidadY = 0;
    private float p2VelocidadY = 0;
    
    // Límite del mapa
    private final float LIMITE_DERECHO = 1100;
    private final float LIMITE_IZQUIERDO = 0;
    
    // Frecuencia de sincronización (en milisegundos)
    private final long SYNC_INTERVAL_MS = 30; // Sincroniza cada 30ms (aprox. 33 FPS)
    // -----------------------------------------------------------------
	
	public HiloServer() {
		// Puerto 9007, debe coincidir con el cliente
		try {
			conexion = new DatagramSocket(9007); 
		} catch (SocketException e) {
			e.printStackTrace();
            System.err.println("ERROR: El puerto 9007 está en uso. Reinicie o cambie de puerto.");
		}
	}
    
    /**
     * Envía un mensaje de texto a una IP y Puerto específicos.
     */
	private void enviarMensaje(String msg, InetAddress ip, int puerto) {
		byte[] data = msg.getBytes();
		DatagramPacket dp = new DatagramPacket(data, data.length, ip, puerto);
		try {
            if (conexion != null) {
			    conexion.send(dp);
            } else {
                System.err.println("Error: Conexión nula, no se pudo enviar el mensaje.");
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
        if (conexion == null) {
            System.err.println("ERROR: El socket del servidor no se pudo crear. Terminando HiloServer.");
            return; 
        }
        
        long lastUpdate = System.currentTimeMillis();
        long lastSync = System.currentTimeMillis();
		
		do{
			long now = System.currentTimeMillis();
            // Calcular delta en segundos (CRÍTICO para la física)
            float delta = (now - lastUpdate) / 1000f; 
            lastUpdate = now;
            
            // 1. Aplicar lógica de simulación de física (Gravedad)
            aplicarGravedad(delta); 
            
            // 2. Procesar mensajes recibidos
			byte[] data = new byte [1024];
			DatagramPacket dp = new DatagramPacket(data, data.length);
			
			try {
                // Configuración de timeout para no bloquear eternamente
                conexion.setSoTimeout(10); 
				conexion.receive(dp); 
                
                // Si hubo datos, procesarlos
                if (dp.getLength() > 0) {
			        procesarMensaje(dp);	
                }
			} catch (IOException e) {
                // Si hay timeout, simplemente sigue.
                if (!fin && !(e instanceof java.net.SocketTimeoutException)) {
                    e.printStackTrace();
                }
			}
            
            // 3. Sincronizar estado a intervalos regulares (Mejora la fluidez, Problema 2 y 4)
            if (Global.empieza && (System.currentTimeMillis() - lastSync) >= SYNC_INTERVAL_MS) {
                sincronizarEstado();
                lastSync = System.currentTimeMillis();
            }
            
		}while(!fin);
        
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
	}
    
    /**
     * Aplica la gravedad y actualiza la posición Y de ambos pingüinos.
     * @param delta Tiempo transcurrido en segundos.
     */
    private void aplicarGravedad(float delta) {
        
        // J1: Aplicar velocidad y gravedad
        p1Y += p1VelocidadY * delta; 
        p1VelocidadY += GRAVEDAD * delta;
        
        // Detección de piso (Problema 4)
        if (p1Y <= POSICION_PISO) {
            p1Y = POSICION_PISO;
            p1VelocidadY = 0;
        } else if (p1Y > 600) { // Restricción de techo
             p1Y = 600;
             if (p1VelocidadY > 0) p1VelocidadY = 0; // Detener subida si toca el techo
        }
        
        // J2: Aplicar velocidad y gravedad
        p2Y += p2VelocidadY * delta;
        p2VelocidadY += GRAVEDAD * delta;
        
        // Detección de piso (Problema 4)
        if (p2Y <= POSICION_PISO) {
            p2Y = POSICION_PISO;
            p2VelocidadY = 0;
        } else if (p2Y > 600) { // Restricción de techo
             p2Y = 600;
             if (p2VelocidadY > 0) p2VelocidadY = 0; // Detener subida si toca el techo
        }
    }
	
	private void procesarMensaje(DatagramPacket dp) {
		String msg = new String(dp.getData(), 0, dp.getLength()).trim();
		String[] partes = msg.split(":");
		InetAddress remitenteIP = dp.getAddress();
		int remitentePuerto = dp.getPort();
        
        // 1. Determinar el ID del jugador
        int jugadorID = 0;
        if (clientes[0] != null && clientes[0].getIp().equals(remitenteIP) && clientes[0].getPuerto() == remitentePuerto) {
            jugadorID = 1;
        } else if (clientes[1] != null && clientes[1].getIp().equals(remitenteIP) && clientes[1].getPuerto() == remitentePuerto) {
            jugadorID = 2;
        }
		
		// 1. Manejo de Conexión Inicial
		if(partes[0].equalsIgnoreCase("conexion")){
            if(cantClientes < clientes.length) { 
                clientes[cantClientes] = new DireccionRed(dp.getAddress(), dp.getPort());
                
                // Envía el ID: OK:1 o OK:2
                enviarMensaje("OK:" + (cantClientes + 1), clientes[cantClientes].getIp(), clientes[cantClientes].getPuerto());
                cantClientes++; 

                if (cantClientes == clientes.length) {
                    Global.empieza = true;
                    System.out.println("Dos clientes conectados. Enviando 'Empieza'.");
                    for (int i = 0; i < clientes.length; i++) {
                        enviarMensaje("Empieza", clientes[i].getIp(), clientes[i].getPuerto());
                    }
                }
            } else {
                 System.out.println("Cliente rechazado: Sala llena.");
            }
		} 
		// 2. Manejo de Comandos de Juego (Solo si el juego empezó)
		else if (Global.empieza && jugadorID != 0 && partes.length >= 2) {
			
            String comando = partes[0];
            String valorStr = partes[1]; // No usado actualmente, pero útil para mensajes complejos
            
            switch(comando) {
                case "MOV_D": // Mover Derecha
                case "MOV_A": // Mover Izquierda
                    manejarMovimiento(jugadorID, comando);
                    break;
                case "SALTAR":
                    manejarSalto(jugadorID);
                    break;
                case "DISPARAR":
                    manejarDisparo(jugadorID);
                    break;
                case "RESET":
                    p1Vida = 5;
                    p2Vida = 5;
                    break;
            }
        }
	}
    
    /**
     * Aplica la lógica de movimiento (simulada) y lo restringe.
     */
    private void manejarMovimiento(int jugadorID, String comando) {
        float x = (jugadorID == 1) ? p1X : p2X;
        
        // El movimiento se calcula en píxeles/segundo * 0.05 (aproximadamente 3 veces por segundo)
        if (comando.equals("MOV_D")) {
            x += VELOCIDAD_MOVIMIENTO * 0.05f; 
        } else if (comando.equals("MOV_A")) {
            x -= VELOCIDAD_MOVIMIENTO * 0.05f;
        }

        // Restricciones de borde
        if (x < LIMITE_IZQUIERDO) x = LIMITE_IZQUIERDO;
        if (x > LIMITE_DERECHO) x = LIMITE_DERECHO;

        if (jugadorID == 1) { p1X = x; } else { p2X = x; }
    }
    
    /**
     * Inicia la velocidad Y si el pingüino está en el suelo.
     */
    private void manejarSalto(int jugadorID) {
        float y = (jugadorID == 1) ? p1Y : p2Y;
        float velY = (jugadorID == 1) ? p1VelocidadY : p2VelocidadY;
        
        // Solo permite el salto si está en el piso (Problema 4)
        if (y <= POSICION_PISO) {
            velY = IMPULSO_SALTO;
            if (jugadorID == 1) { p1VelocidadY = velY; } else { p2VelocidadY = velY; }
        }
    }
    
    /**
     * Simula la lógica de disparo y daño (la colisión y bala real irían aquí).
     */
    private void manejarDisparo(int jugadorID) {
        // --- SIMULACIÓN DE DAÑO PARA PRUEBAS ---
        if (p1Vida <= 0 || p2Vida <= 0) return; // No permitir disparar si el juego terminó
        
        if (jugadorID == 1) {
            p2Vida--;
            System.out.println("J1 disparó. Vida J2: " + p2Vida);
        } else if (jugadorID == 2) {
            p1Vida--;
            System.out.println("J2 disparó. Vida J1: " + p1Vida);
        }
    }
    
    /**
     * Envía el estado actual del juego (posiciones, vida) a todos los clientes.
     */
    private void sincronizarEstado() {
        // Formato: ESTADO:P1X,P1Y:P2X,P2Y:V1,V2
    	String estadoMsg = String.format(Locale.US, "ESTADO:%.2f,%.2f:%.2f,%.2f:%d,%d", 
                p1X, p1Y, p2X, p2Y, p1Vida, p2Vida);
        
        // Envío del mensaje de estado
        for (int i = 0; i < cantClientes; i++) {
            enviarMensaje(estadoMsg, clientes[i].getIp(), clientes[i].getPuerto());
        }
        
        // Lógica de Fin de Juego (el servidor es el único que la maneja)
        if (p1Vida <= 0) {
            for (int i = 0; i < cantClientes; i++) {
                enviarMensaje("GANADOR:2", clientes[i].getIp(), clientes[i].getPuerto()); // Gana J2
            }
        } else if (p2Vida <= 0) {
            for (int i = 0; i < cantClientes; i++) {
                enviarMensaje("GANADOR:1", clientes[i].getIp(), clientes[i].getPuerto()); // Gana J1
            }
        }
    }
	
	public void detener() {
	    this.fin = true; 
	    if (conexion != null && !conexion.isClosed()) {
	        conexion.close(); 
	    }
	}
}