package red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException; 
import pantallas.PantallaJuego;
import pantallas.PantallaFin;
import utiles.Global;

// HiloCliente es como un cartero que trabaja en un hilo (Thread) separado
// para que el juego principal no se congele mientras espera mensajes del servidor.
public class HiloCliente extends Thread {
	
	private DatagramSocket conexion; // El "teléfono" para enviar y recibir datos (UDP).
	private InetAddress ipServer;      // La dirección IP de la "casa" del servidor.
	private int puerto = 9015;         // El número de "puerta" (puerto) al que envía mensajes.
	private boolean fin = false;       // Bandera para saber cuándo debe dejar de funcionar.
	private PantallaJuego juego;       // Referencia a la pantalla del juego para actualizar cosas.
	
	// Constructor: Se ejecuta al crear el HiloCliente.
	public HiloCliente() {
		try {
			// Intentamos encontrar la dirección del servidor (aquí es 'localhost' o 127.0.0.1).
			ipServer = InetAddress.getByName("127.0.0.1"); 
			conexion = new DatagramSocket(); // Abrimos la conexión.
            
            // Le decimos al teléfono que si no llega un mensaje en 0.5 segundos, siga. 
            // Esto evita que se bloquee la espera de mensajes.
            conexion.setSoTimeout(500); 
            
		} catch (SocketException e) {
		    e.printStackTrace();
		} catch (UnknownHostException e) { 
		    e.printStackTrace();
		}
		// Enviamos el primer mensaje para decirle al servidor que nos conectamos.
		enviarMensaje("Conexion:CLIENTE");
	}
	
    /**
     * Permite que la PantallaJuego se registre para recibir actualizaciones.
     */
    public void setPantallaJuego(PantallaJuego juego) {
        this.juego = juego;
    }
    
	// Método para enviar mensajes (comandos) al servidor.
	public void enviarMensaje(String msg) {
		byte[] data = msg.getBytes();
		// Creamos el paquete con el mensaje, la dirección del servidor y el puerto.
		DatagramPacket dp = new DatagramPacket(data, data.length, ipServer, puerto);
		try {
			conexion.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	// El método 'run' es el trabajo principal del cartero.
	public void run() {
		do{
			byte[] data = new byte [1024]; 
			DatagramPacket dp = new DatagramPacket(data, data.length);
			
			try {
				// Esperamos a recibir un paquete del servidor.
				conexion.receive(dp);
			} catch (IOException e) {
				// Si hubo un timeout (el servidor no envió nada), lo ignoramos y seguimos.
				if (!fin && !(e instanceof java.net.SocketTimeoutException)) { 
                    e.printStackTrace();
                }
			}
            
            // Si recibimos algo real, lo procesamos.
            if (dp.getLength() > 0) {
			    procesarMensaje(dp);	
            }
		}while(!fin); // Repetimos mientras 'fin' sea falso.
        
        // Cuando terminamos, cerramos la conexión de forma limpia.
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
	}
	
	// Método que analiza el mensaje recibido del servidor.
	private void procesarMensaje(DatagramPacket dp) {
	    String msg = new String(dp.getData(), 0, dp.getLength()).trim(); 
	    String[] partes = msg.split(":"); // Dividimos el mensaje por los dos puntos (:)
	    
	    System.out.println("Cliente recibió: " + msg);
	    
	    if(partes[0].equals("OK")) {
	        // El servidor nos asigna nuestro ID (Jugador 1 o 2).
	        ipServer = dp.getAddress(); 
	        if (partes.length > 1 && juego != null) {
	            try {
	                juego.setJugadorID(Integer.parseInt(partes[1])); 
	            } catch (NumberFormatException e) {
	                System.err.println("Error al obtener ID del servidor.");
	            }
	        }
	    } else if (msg.equals("Empieza")) {
	        // La bandera 'Global.empieza' se activa para iniciar el juego.
	        Global.empieza = true; 
	    } else if (msg.startsWith("ESTADO:")) {
	        // Recibimos la posición de los jugadores, balas y vidas.
	        if (juego != null) {
	            final String estado = msg; 
	            // CRÍTICO: Ejecutamos el cambio de pantalla en el hilo principal de LibGDX
	            com.badlogic.gdx.Gdx.app.postRunnable(new Runnable() {
	                @Override
	                public void run() {
	                    juego.actualizarEstadoServidor(estado);
	                    
	                    // Si la vida de cualquier jugador llega a cero, cambiamos a la pantalla de fin.
	                    if (juego.vida <= 0 || juego.vida2 <= 0) { 
	                        int ganador = (juego.vida > 0) ? 1 : 2; 
	                        
	                        // Cambia la pantalla a PantallaFin
	                        ((com.badlogic.gdx.Game)com.badlogic.gdx.Gdx.app.getApplicationListener()).setScreen(new PantallaFin(ganador)); 
	                    }
	                }
	            });
	        }
	    } else if (msg.equals("INICIO_PARTIDA")) {
	        // Si el servidor confirma el reinicio, volvemos a la pantalla de juego.
	        com.badlogic.gdx.Gdx.app.postRunnable(new Runnable() {
	            @Override
	            public void run() {
	                // Cargamos una nueva instancia de la PantallaJuego.
	                ((com.badlogic.gdx.Game)com.badlogic.gdx.Gdx.app.getApplicationListener()).setScreen(new PantallaJuego()); 
	            }
	        });
	    } else if (partes[0].equals("GANADOR")) {
	        // Este es un mensaje redundante (el estado ya maneja el fin de juego).
	        System.out.println("Fin del Juego detectado por mensaje GANADOR.");
	    }
	}
    
    // Método llamado para detener este hilo de forma segura.
    public void detener() {
        this.fin = true;
    }
}