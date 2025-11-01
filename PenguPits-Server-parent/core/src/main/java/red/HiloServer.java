package red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.ArrayList; 
import java.util.Locale;

import utiles.Global; // Asumiendo que esta clase existe

// NOTA: Asume que 'DireccionRed' y 'BalaServer' son clases auxiliares correctas.

public class HiloServer extends Thread {
	private DatagramSocket conexion;
	private boolean fin = false;
	
    // Estructura para almacenar las direcciones de los clientes
	private DireccionRed[] clientes = new DireccionRed[2]; 
	private int cantClientes = 0;
    
    // --- ESTADO DEL JUEGO GESTIONADO POR EL SERVIDOR ---
    private List<BalaServer> balas = new ArrayList<>(); 
    private int nextBalaID = 1; 
    
    // Posición de los Jugadores
    private float p1X = 100, p1Y = 120; 
    private float p2X = 1000, p2Y = 120; 
    
    // Vidas
    private int p1Vida = 5;
    private int p2Vida = 5;
    
    // Mirando Derecha
    private boolean p1MirandoDerecha = true;
    private boolean p2MirandoDerecha = false;
    
    // Constantes para simulación de física en el servidor
    private final float VELOCIDAD_MOVIMIENTO = 100; // Velocidad de movimiento horizontal
    private final float IMPULSO_SALTO = 200; 
    private final float GRAVEDAD = -120;  
    private final float POSICION_PISO = 120;
    
    // Variables para simular la física (CRÍTICO: Velocidad X añadida)
    private float p1VelocidadY = 0;
    private float p2VelocidadY = 0;
    private float p1VelocidadX = 0; 
    private float p2VelocidadX = 0; 
    
    // Límite del mapa
    private final float LIMITE_DERECHO = 1100;
    private final float LIMITE_IZQUIERDO = 0;
 
    // -----------------------------------------------------------------
	
	public HiloServer() {
		// Puerto 9013 (Sincronizado con HiloCliente.java)
		try {
			conexion = new DatagramSocket(9007); 
			// CORRECCIÓN: Permite la reutilización rápida de la dirección (puerto)
			conexion.setReuseAddress(true); 
		} catch (SocketException e) {
			e.printStackTrace();
            System.err.println("ERROR: El puerto 9017 está en uso. Detenga el proceso anterior o use ServidorApp.");
		}
	}
    
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
	    
	    // Tasa de Simulación de la Física (MÁS IMPORTANTE QUE EL RENDERING)
	    // 60 Ticks por segundo: la física se actualizará cada 16.67ms.
	    final float S_POR_TICK = 1f / 60f; // 0.01667 segundos por tick
	    
	    float acumuladorTiempo = 0;
	    long lastTime = System.currentTimeMillis();
	    
	    do {
	        long now = System.currentTimeMillis();
	        // Delta (tiempo real transcurrido entre este frame y el anterior)
	        float delta = (now - lastTime) / 1000f; // Delta en segundos
	        lastTime = now;
	        
	        // El acumulador guarda el tiempo que ha pasado desde el último tick de física
	        acumuladorTiempo += delta;
	        
	        // -----------------------------------------------------------------
	        // 1. LECTURA DE COMANDOS (ALTA FRECUENCIA / NO BLOQUEANTE)
	        // -----------------------------------------------------------------
	        byte[] data = new byte [1024];
	        DatagramPacket dp = new DatagramPacket(data, data.length);
	        
	        try {
	            // setSoTimeout(1) asegura que solo esperamos 1ms, priorizando la lectura
	            conexion.setSoTimeout(1); 
	            conexion.receive(dp); 
	            
	            if (dp.getLength() > 0) {
	                procesarMensaje(dp); // El comando se procesa INMEDIATAMENTE
	            }
	        } catch (IOException e) {
	            // El timeout (SocketTimeoutException) es esperado, no lo imprimimos.
	            if (!fin && !(e instanceof java.net.SocketTimeoutException)) {
	                e.printStackTrace();
	            }
	        }
	        
	        // -----------------------------------------------------------------
	        // 2. BUCLE DE PASO DE TIEMPO FIJO (Fixed Timestep)
	        // -----------------------------------------------------------------
	        
	        // Mientras el tiempo acumulado sea mayor que el tiempo de un tick de física
	        while (acumuladorTiempo >= S_POR_TICK) {
	            
	            // A. APLICAR FÍSICA Y JUEGO (Usando el S_POR_TICK fijo)
	            aplicarFisica(S_POR_TICK); 
	            actualizarBalas(S_POR_TICK); 
	            
	            // B. SINCRONIZAR ESTADO (Enviar las nuevas posiciones)
	            sincronizarEstado();
	            
	            // C. Consumir el tiempo
	            acumuladorTiempo -= S_POR_TICK;
	        }
	        
	        // -----------------------------------------------------------------
	        // 3. CÓDIGO FINAL (CEDER CPU)
	        // -----------------------------------------------------------------
	        
	        // Como eliminamos el Thread.sleep(16), agregamos un micro-sleep de 1ms.
	        // Esto evita que el bucle consuma el 100% de la CPU al ceder el control
	        // brevemente a otros hilos/procesos del sistema operativo.
	        try {
	            Thread.sleep(1); 
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }

	    } while (!fin);
	    
	    if (conexion != null && !conexion.isClosed()) {
	        conexion.close();
	    }
	}
    
    // --- CORRECCIÓN DE LA FÍSICA Y MOVIMIENTO ---
    private void aplicarFisica(float delta) {
        
        // J1: Aplicar velocidad, gravedad y límites
        p1X += p1VelocidadX * delta; // <--- Aplica la velocidad horizontal
        p1Y += p1VelocidadY * delta; 
        p1VelocidadY += GRAVEDAD * delta;
        
        // Comprobar límites X
        if (p1X < LIMITE_IZQUIERDO) p1X = LIMITE_IZQUIERDO;
        if (p1X > LIMITE_DERECHO) p1X = LIMITE_DERECHO;

        // Comprobar límites Y (Piso)
        if (p1Y <= POSICION_PISO) {
            p1Y = POSICION_PISO;
            p1VelocidadY = 0;
        } 
        
        // J2: Aplicar velocidad, gravedad y límites
        p2X += p2VelocidadX * delta; // <--- Aplica la velocidad horizontal
        p2Y += p2VelocidadY * delta;
        p2VelocidadY += GRAVEDAD * delta;
        
        // Comprobar límites X
        if (p2X < LIMITE_IZQUIERDO) p2X = LIMITE_IZQUIERDO;
        if (p2X > LIMITE_DERECHO) p2X = LIMITE_DERECHO;

        // Comprobar límites Y (Piso)
        if (p2Y <= POSICION_PISO) {
            p2Y = POSICION_PISO;
            p2VelocidadY = 0;
        }
    }
	
    private void procesarMensaje(DatagramPacket dp) {
        String msg = new String(dp.getData(), 0, dp.getLength()).trim();
        String[] partes = msg.split(":");
        InetAddress remitenteIP = dp.getAddress();
        int remitentePuerto = dp.getPort();
        
        // Determinar el ID del jugador
        int jugadorID = 0;
        if (clientes[0] != null && clientes[0].getIp().equals(remitenteIP) && clientes[0].getPuerto() == remitentePuerto) {
            jugadorID = 1;
        } else if (clientes[1] != null && clientes[1].getIp().equals(remitenteIP) && clientes[1].getPuerto() == remitentePuerto) {
            jugadorID = 2;
        }
        
        // Manejo de Conexión Inicial y Comandos de Juego
        if(partes[0].equalsIgnoreCase("conexion")){
            if(cantClientes < clientes.length) { 
                clientes[cantClientes] = new DireccionRed(dp.getAddress(), dp.getPort());
                enviarMensaje("OK:" + (cantClientes + 1), clientes[cantClientes].getIp(), clientes[cantClientes].getPuerto());
                cantClientes++; 
                if (cantClientes == clientes.length) {
                    Global.empieza = true;
                    for (int i = 0; i < clientes.length; i++) {
                        enviarMensaje("Empieza", clientes[i].getIp(), clientes[i].getPuerto());
                    }
                }
            } else {
                 System.out.println("Cliente rechazado: Sala llena.");
            }
        } 
        else if (Global.empieza && jugadorID != 0) { 
            
            String comando = partes[0];
            String direccion = (partes.length > 1) ? partes[1] : ""; 

            switch(comando) {
                case "MOV_D_INICIO":
                    setVelocidadHorizontal(jugadorID, VELOCIDAD_MOVIMIENTO);
                    // ¡CRÍTICO! Sincronizar la dirección del jugador
                    if (jugadorID == 1) p1MirandoDerecha = true;
                    else p2MirandoDerecha = true;
                    break;
                case "MOV_A_INICIO":
                    setVelocidadHorizontal(jugadorID, -VELOCIDAD_MOVIMIENTO);
                    // ¡CRÍTICO! Sincronizar la dirección del jugador
                    if (jugadorID == 1) p1MirandoDerecha = false;
                    else p2MirandoDerecha = false;
                    break;
                case "MOV_FIN": // Comando para detener el movimiento
                    setVelocidadHorizontal(jugadorID, 0);
                    break;
                case "SALTAR":
                    manejarSalto(jugadorID);
                    break;
                case "DISPARAR":
                    manejarDisparo(jugadorID, direccion); 
                    break;
                case "RESET":
                    p1Vida = 5;
                    p2Vida = 5;
                    break;
            }
        }
    }
    
    // --- NUEVO MÉTODO PARA ESTABLECER LA VELOCIDAD ---
    private void setVelocidadHorizontal(int jugadorID, float velocidad) {
        if (jugadorID == 1) {
            p1VelocidadX = velocidad;
        } else {
            p2VelocidadX = velocidad;
        }
    }
    
    private void manejarSalto(int jugadorID) {
        float y = (jugadorID == 1) ? p1Y : p2Y;
        float velY = (jugadorID == 1) ? p1VelocidadY : p2VelocidadY;
        
        if (y <= POSICION_PISO) {
            velY = IMPULSO_SALTO;
            if (jugadorID == 1) { p1VelocidadY = velY; } else { p2VelocidadY = velY; }
        }
    }
    
    private void manejarDisparo(int jugadorID, String direccion) {
        if (p1Vida <= 0 || p2Vida <= 0) return;
        
        // 1. Calcular posición inicial 
        float startX = (jugadorID == 1) ? p1X : p2X;
        float startY = (jugadorID == 1) ? p1Y : p2Y;
        
        boolean isDerecha = direccion.equals("DERECHA");
        
        if (isDerecha) {
            startX += 80; 
        } else {
            startX -= 20; 
        }
        startY += 100;
        
        // 2. Crear la bala lógica
        BalaServer nuevaBala = new BalaServer(startX, startY, isDerecha, nextBalaID++, jugadorID);
        balas.add(nuevaBala);
    }
    
    private void actualizarBalas(float delta) {
        for (int i = balas.size() - 1; i >= 0; i--) {
            BalaServer balaActual = balas.get(i);
            balaActual.actualizar(delta);
            
            // 1. VERIFICACIÓN DE COLISIÓN Y LÍMITES
            boolean golpea = false;
            
            // Colisión con J1 (si J2 disparó)
            if (balaActual.getIdJugadorDano() == 2) {
                if (balaActual.getX() >= p1X && balaActual.getX() <= p1X + 100 &&
                    balaActual.getY() >= p1Y && balaActual.getY() <= p1Y + 210) {
                    p1Vida--;
                    golpea = true;
                }
            }
            
            // Colisión con J2 (si J1 disparó)
            if (balaActual.getIdJugadorDano() == 1) {
                if (balaActual.getX() >= p2X && balaActual.getX() <= p2X + 100 &&
                    balaActual.getY() >= p2Y && balaActual.getY() <= p2Y + 210) {
                    p2Vida--;
                    golpea = true;
                }
            }
            
            // 2. Eliminación
            if (balaActual.debeEliminarse() || golpea) {
                balas.remove(i);
            }
        }
    }

    private void sincronizarEstado() {
        // Serializar las balas: ID,X,Y,DERECHA;ID,X,Y,DERECHA;...
        StringBuilder balasString = new StringBuilder();
        for (BalaServer b : balas) {
            String dir = b.isDerecha() ? "D" : "I";
            // Formato: ID,X,Y,D/I;
            balasString.append(String.format(Locale.US, "%d,%.2f,%.2f,%s;", b.getIdBala(), b.getX(), b.getY(), dir));
        }

        // --- NUEVOS DATOS DE DIRECCIÓN ---
        // Determinamos si cada jugador está mirando a la derecha (D) o a la izquierda (I)
        String dir1 = p1MirandoDerecha ? "D" : "I";
        String dir2 = p2MirandoDerecha ? "D" : "I";

        // Formato final (¡CRÍTICO! El cliente debe esperar este nuevo formato):
        // ESTADO:P1X,P1Y:P2X,P2Y:V1,V2:D1,D2:BALAS:DATA
        String estadoMsg = String.format(Locale.US, 
                                        "ESTADO:%.2f,%.2f:%.2f,%.2f:%d,%d:%s,%s:%s", 
                                        p1X, p1Y, p2X, p2Y, p1Vida, p2Vida, dir1, dir2, balasString.toString());
        
        // Envío del mensaje de estado a todos los clientes
        for (int i = 0; i < cantClientes; i++) {
            enviarMensaje(estadoMsg, clientes[i].getIp(), clientes[i].getPuerto());
        }
        
        // Lógica de Fin de Juego (el servidor es el único que la maneja)
        if (p1Vida <= 0) {
            for (int i = 0; i < clientes.length; i++) {
                enviarMensaje("GANADOR:2", clientes[i].getIp(), clientes[i].getPuerto());
            }
        } else if (p2Vida <= 0) {
            for (int i = 0; i < clientes.length; i++) {
                enviarMensaje("GANADOR:1", clientes[i].getIp(), clientes[i].getPuerto());
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