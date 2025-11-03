package red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.ArrayList; 
import java.util.Locale;

import utiles.Global; // Clase con banderas y variables de estado global.

// HiloServer es el "cerebro" y "juez" del juego. Corre sin parar en un hilo separado
// para calcular la física y sincronizar a todos los jugadores.
public class HiloServer extends Thread {
	private DatagramSocket conexion; // El "teléfono" para enviar y recibir datos (UDP).
	private boolean fin = false;    // Bandera para detener el servidor de forma segura.
	
    // Estructura para saber dónde encontrar a los dos clientes (su IP y Puerto).
	private DireccionRed[] clientes = new DireccionRed[2]; 
	private int cantClientes = 0; // Contador de jugadores conectados.
    
    // --- ESTADO DEL JUEGO GESTIONADO POR EL SERVIDOR ---
    private List<BalaServer> balas = new ArrayList<>(); // Todas las balas activas en el juego.
    private int nextBalaID = 1; // Contador para dar un ID único a cada bala nueva.
    
    // Posición y Vidas de los Jugadores
    private float p1X = 100, p1Y = 120; 
    private float p2X = 1000, p2Y = 120; 
    private int p1Vida = 5;
    private int p2Vida = 5;
    
    // Dirección del sprite (necesario para el cliente)
    private boolean p1MirandoDerecha = true;
    private boolean p2MirandoDerecha = false;
    
    // Constantes de la Física
    private final float VELOCIDAD_MOVIMIENTO = 100; // Velocidad horizontal al caminar.
    private final float IMPULSO_SALTO = 200;       // Fuerza que recibe el pingüino al saltar.
    private final float GRAVEDAD = -120;           // La fuerza que siempre tira hacia abajo.
    private final float POSICION_PISO = 120;       // Altura del suelo.
    
    // Variables de Física Aplicada (cambian constantemente)
    private float p1VelocidadY = 0; // Velocidad vertical (subir/bajar).
    private float p2VelocidadY = 0;
    private float p1VelocidadX = 0; // Velocidad horizontal (caminar).
    private float p2VelocidadX = 0; 
    
    // Límites del área de juego
    private final float LIMITE_DERECHO = 1100;
    private final float LIMITE_IZQUIERDO = 0;
    
    private int votosReinicio = 0; // Contador para la opción "Jugar de nuevo".
 
    // -----------------------------------------------------------------
	
	// Constructor: Configura el socket del servidor.
	public HiloServer() {
		try {
			// Abre el puerto para escuchar (9012).
			conexion = new DatagramSocket(9015); 
			// Permite que el puerto se libere inmediatamente al cerrar, evitando errores.
			conexion.setReuseAddress(true); 
		} catch (SocketException e) {
			e.printStackTrace();
            System.err.println("ERROR: El puerto 9017 está en uso. Detenga el proceso anterior o use ServidorApp.");
		}
	}
    
	// Envía un mensaje a una dirección y puerto específicos.
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
	// El bucle principal del servidor (el juez).
	public void run() {
	    if (conexion == null) {
	        System.err.println("ERROR: El socket del servidor no se pudo crear. Terminando HiloServer.");
	        return; 
	    }
	    
	    // Definimos que la física se calcula 60 veces por segundo (1/60s).
	    final float S_POR_TICK = 1f / 60f; 
	    
	    float acumuladorTiempo = 0;
	    long lastTime = System.currentTimeMillis();
	    
	    do {
	        long now = System.currentTimeMillis();
	        // Calculamos el tiempo real que pasó desde la última vez que revisamos.
	        float delta = (now - lastTime) / 1000f; 
	        lastTime = now;
	        
	        acumuladorTiempo += delta;
	        
	        // -----------------------------------------------------------------
	        // 1. LECTURA DE COMANDOS
	        // -----------------------------------------------------------------
	        byte[] data = new byte [1024];
	        DatagramPacket dp = new DatagramPacket(data, data.length);
	        
	        try {
	            // Esperamos solo 1ms para recibir un comando del cliente antes de seguir.
	            conexion.setSoTimeout(1); 
	            conexion.receive(dp); 
	            
	            if (dp.getLength() > 0) {
	                procesarMensaje(dp); // Analizamos el comando inmediatamente.
	            }
	        } catch (IOException e) {
	            // El tiempo de espera ha expirado, no es un error real.
	        }
	        
	        // -----------------------------------------------------------------
	        // 2. BUCLE DE FÍSICA Y SINCRONIZACIÓN (Paso de Tiempo Fijo)
	        // -----------------------------------------------------------------
	        
	        // Esto asegura que la física se calcule 60 veces por segundo, sin importar el lag.
	        while (acumuladorTiempo >= S_POR_TICK) {
	            
	            // A. Aplicamos las reglas de física y movemos todos los objetos.
	            aplicarFisica(S_POR_TICK); 
	            actualizarBalas(S_POR_TICK); 
	            
	            // B. Enviamos el nuevo estado (posiciones y vidas) a todos los clientes.
	            sincronizarEstado();
	            
	            // C. Restamos el tiempo del tick que acabamos de procesar.
	            acumuladorTiempo -= S_POR_TICK;
	        }
	        
	        // -----------------------------------------------------------------
	        // 3. CEDER CPU
	        // -----------------------------------------------------------------
	        
	        // Pequeña pausa para evitar usar el 100% de la CPU.
	        try {
	            Thread.sleep(1); 
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }

	    } while (!fin); // El servidor corre hasta que alguien llame a detener().
	    
	    if (conexion != null && !conexion.isClosed()) {
	        conexion.close();
	    }
	}
    
    // --- LÓGICA DE FÍSICA ---
    private void aplicarFisica(float delta) {
        
        // J1: Aplicar velocidad, gravedad y límites
        p1X += p1VelocidadX * delta; // Mueve horizontalmente
        p1Y += p1VelocidadY * delta; // Mueve verticalmente
        p1VelocidadY += GRAVEDAD * delta; // Aplica gravedad constante
        
        // Comprobar límites X (para que no salga del mapa)
        if (p1X < LIMITE_IZQUIERDO) p1X = LIMITE_IZQUIERDO;
        if (p1X > LIMITE_DERECHO) p1X = LIMITE_DERECHO;

        // Comprobar límites Y (si toca el piso, se detiene la caída)
        if (p1Y <= POSICION_PISO) {
            p1Y = POSICION_PISO;
            p1VelocidadY = 0;
        } 
        
        // J2: Aplicar velocidad, gravedad y límites (igual que J1)
        p2X += p2VelocidadX * delta; 
        p2Y += p2VelocidadY * delta;
        p2VelocidadY += GRAVEDAD * delta;
        
        if (p2X < LIMITE_IZQUIERDO) p2X = LIMITE_IZQUIERDO;
        if (p2X > LIMITE_DERECHO) p2X = LIMITE_DERECHO;

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
        
        // 1. Identificar al jugador que envió el mensaje.
        int jugadorID = 0;
        if (clientes[0] != null && clientes[0].getIp().equals(remitenteIP) && clientes[0].getPuerto() == remitentePuerto) {
            jugadorID = 1;
        } else if (clientes[1] != null && clientes[1].getIp().equals(remitenteIP) && clientes[1].getPuerto() == remitentePuerto) {
            jugadorID = 2;
        }
        
        // 2. Manejar Conexión o Comandos
        if(partes[0].equalsIgnoreCase("conexion")){
            if(cantClientes < clientes.length) { 
                // Acepta un nuevo cliente, le da su ID (1 o 2) y lo saluda.
                clientes[cantClientes] = new DireccionRed(dp.getAddress(), dp.getPort());
                enviarMensaje("OK:" + (cantClientes + 1), clientes[cantClientes].getIp(), clientes[cantClientes].getPuerto());
                cantClientes++; 
                if (cantClientes == clientes.length) {
                    // Si ya hay 2 clientes, inicia el juego para todos.
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
            
            // Analiza el comando de juego enviado por el cliente.
            String comando = partes[0];
            String direccion = (partes.length > 1) ? partes[1] : ""; 

            switch(comando) {
                case "MOV_D_INICIO":
                    setVelocidadHorizontal(jugadorID, VELOCIDAD_MOVIMIENTO);
                    if (jugadorID == 1) p1MirandoDerecha = true;
                    else p2MirandoDerecha = true;
                    break;
                case "MOV_A_INICIO":
                    setVelocidadHorizontal(jugadorID, -VELOCIDAD_MOVIMIENTO);
                    if (jugadorID == 1) p1MirandoDerecha = false;
                    else p2MirandoDerecha = false;
                    break;
                case "MOV_FIN": 
                    setVelocidadHorizontal(jugadorID, 0); // Frena el movimiento horizontal.
                    break;
                case "SALTAR":
                    manejarSalto(jugadorID);
                    break;
                case "DISPARAR":
                    manejarDisparo(jugadorID, direccion); // Llama a la lógica para crear una bala.
                    break;
                case "RESET":
                    p1Vida = 5; // Resetea las vidas (usado para debugging).
                    p2Vida = 5;
                    break;
            }
        }
    }
    
    // --- MÉTODOS AUXILIARES ---
    
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
        
        // Solo permite saltar si está tocando el piso.
        if (y <= POSICION_PISO) {
            velY = IMPULSO_SALTO;
            if (jugadorID == 1) { p1VelocidadY = velY; } else { p2VelocidadY = velY; }
        }
    }
    
    private void manejarDisparo(int jugadorID, String direccion) {
        if (p1Vida <= 0 || p2Vida <= 0) return;
        
        // 1. Calcular dónde debe aparecer la bala.
        float startX = (jugadorID == 1) ? p1X : p2X;
        float startY = (jugadorID == 1) ? p1Y : p2Y;
        
        boolean isDerecha = direccion.equals("DERECHA");
        
        if (isDerecha) {
            startX += 80; 
        } else {
            startX -= 20; 
        }
        startY += 100;
        
        // 2. Crea la bala y le da un ID único.
        BalaServer nuevaBala = new BalaServer(startX, startY, isDerecha, nextBalaID++, jugadorID);
        balas.add(nuevaBala);
    }
    
    // Lógica para mover las balas y comprobar si golpearon a alguien.
    private void actualizarBalas(float delta) {
        for (int i = balas.size() - 1; i >= 0; i--) {
            BalaServer balaActual = balas.get(i);
            balaActual.actualizar(delta);
            
            boolean golpea = false;
            
            // Colisión con J1
            if (balaActual.getIdJugadorDano() == 2) { 
                // Comprobación simple de colisión de área.
                if (balaActual.getX() >= p1X && balaActual.getX() <= p1X + 100 &&
                    balaActual.getY() >= p1Y && balaActual.getY() <= p1Y + 210) {
                    p1Vida--;
                    golpea = true;
                }
            }
            
            // Colisión con J2
            if (balaActual.getIdJugadorDano() == 1) {
                if (balaActual.getX() >= p2X && balaActual.getX() <= p2X + 100 &&
                    balaActual.getY() >= p2Y && balaActual.getY() <= p2Y + 210) {
                    p2Vida--;
                    golpea = true;
                }
            }
            
            // 2. Eliminación (si salió del mapa o golpeó a un jugador)
            if (balaActual.debeEliminarse() || golpea) {
                balas.remove(i);
            }
        }
    }

    // Crea el mensaje que contiene todas las posiciones y estados para los clientes.
    private void sincronizarEstado() {
        // 1. Serializar las balas: Las convierte de objetos a una cadena de texto.
    	StringBuilder balasString = new StringBuilder();
        for (BalaServer b : balas) {
            String dir = b.isDerecha() ? "D" : "I";
            // Formato de la bala: ID,X,Y,DIRECCIÓN;
            balasString.append(String.format(Locale.US, "%d,%.2f,%.2f,%s;", b.getId(), b.getX(), b.getY(), dir));
            
        }

        // 2. Dirección de los pingüinos (para que el cliente haga el "flip").
        String dir1 = p1MirandoDerecha ? "D" : "I";
        String dir2 = p2MirandoDerecha ? "D" : "I";

        // 3. Formato del mensaje completo (ESTADO:P1:P2:Vidas:Direcciones:BALAS:Data)
        String estadoMsg = String.format(Locale.US, 
                                        "ESTADO:%.2f,%.2f:%.2f,%.2f:%d,%d:%s,%s:%s", 
                                        p1X, p1Y, p2X, p2Y, p1Vida, p2Vida, dir1, dir2, balasString.toString());
        
        // 4. Envío: Manda el estado a todos los clientes.
        for (int i = 0; i < cantClientes; i++) {
            enviarMensaje(estadoMsg, clientes[i].getIp(), clientes[i].getPuerto());
        }
        
        // 5. Lógica de Fin de Juego: Si alguien se queda sin vida, avisa que hay un ganador.
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