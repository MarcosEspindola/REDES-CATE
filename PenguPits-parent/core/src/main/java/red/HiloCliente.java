package red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException; 

import pantallas.PantallaJuego;
import utiles.Global;

public class HiloCliente extends Thread {
	private DatagramSocket conexion;
	private InetAddress ipServer;
	// CORRECCIÓN: Sincronizar puerto con HiloServer (9013)
	private int puerto = 9007; 
	private boolean fin = false;
	private PantallaJuego juego; // Referencia a la pantalla
	
	public HiloCliente() {
		try {
			ipServer = InetAddress.getByName("127.0.0.1"); 
			conexion = new DatagramSocket();
            
            // CORRECCIÓN: Establecer un timeout bajo para evitar el bloqueo indefinido.
            conexion.setSoTimeout(500); 
            
		} catch (SocketException e) {
		    e.printStackTrace();
		} catch (UnknownHostException e) { 
		    e.printStackTrace();
		}
		enviarMensaje("Conexion:CLIENTE");
	}
	
    /**
     * Permite a PantallaJuego asignar la referencia para las actualizaciones
     */
    public void setPantallaJuego(PantallaJuego juego) {
        this.juego = juego;
    }
    
	public void enviarMensaje(String msg) {
		byte[] data = msg.getBytes();
		DatagramPacket dp = new DatagramPacket(data, data.length, ipServer, puerto);
		try {
			conexion.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		do{
			byte[] data = new byte [1024]; 
			DatagramPacket dp = new DatagramPacket(data, data.length);
			
			try {
				conexion.receive(dp);
			} catch (IOException e) {
				// El timeout (SocketTimeoutException) es esperado, no hacemos nada.
				if (!fin && !(e instanceof java.net.SocketTimeoutException)) { 
                    e.printStackTrace();
                }
			}
            
            // Solo procesamos si recibimos datos (el timeout hace que dp.getLength() sea 0)
            if (dp.getLength() > 0) {
			    procesarMensaje(dp);	
            }
		}while(!fin);
        
        // Cierre limpio de la conexión
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
	}
	
	private void procesarMensaje(DatagramPacket dp) {
	    String msg = new String(dp.getData(), 0, dp.getLength()).trim(); 
	    String[] partes = msg.split(":");
	    
	    System.out.println("Cliente recibió: " + msg);
	    
	    if(partes[0].equals("OK")) {
	        // Asignación de ID al inicio de la conexión
	        ipServer = dp.getAddress(); 
	        if (partes.length > 1 && juego != null) {
	            try {
	                juego.setJugadorID(Integer.parseInt(partes[1])); 
	            } catch (NumberFormatException e) {
	                System.err.println("Error al obtener ID del servidor.");
	            }
	        }
	    } else if (msg.equals("Empieza")) {
	        Global.empieza = true; 
	    } else if (msg.startsWith("ESTADO:")) {
	        // *** CORRECCIÓN CRÍTICA: Mover la actualización del estado al hilo de renderizado ***
	        if (juego != null) {
	            final String estado = msg; // Necesitamos una variable final para usar en el Runnable

	            // Ejecutamos actualizarEstadoServidor en el hilo seguro de LibGDX
	            com.badlogic.gdx.Gdx.app.postRunnable(new Runnable() {
	                @Override
	                public void run() {
	                    juego.actualizarEstadoServidor(estado);
	                }
	            });
	        }
	    } else if (partes[0].equals("GANADOR")) {
	        // El manejo del fin de juego también debe ser seguro
	        if (juego != null && partes.length > 1) {
	            final int idGanador = Integer.parseInt(partes[1]);

	            com.badlogic.gdx.Gdx.app.postRunnable(new Runnable() {
	                @Override
	                public void run() {
	                    System.out.println("Fin del Juego. El ganador es el Jugador " + idGanador);
	                    // Lógica de PantallaFin o resultado va aquí
	                }
	            });
	        }
	    }
	}
    
    public void detener() {
        this.fin = true;
        // La conexión se cierra en el finally/al final del run
    }
}