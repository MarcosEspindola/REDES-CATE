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
	// El puerto debe coincidir con el del servidor (9004)
	private int puerto = 9008; 
	private boolean fin = false;
	private PantallaJuego juego; // Referencia a la pantalla
	
	public HiloCliente() {
		try {
			// Usar la IP de loopback para pruebas en la misma máquina
			ipServer = InetAddress.getByName("127.0.0.1"); 
			conexion = new DatagramSocket();
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
				if (!fin) { e.printStackTrace(); }
			}
			procesarMensaje(dp);	
		}while(!fin);
	}
	
	private void procesarMensaje(DatagramPacket dp) {
		String msg = new String(dp.getData(), 0, dp.getLength()).trim(); 
		String[] partes = msg.split(":");
		
		System.out.println("Cliente recibió: " + msg);
		
		if(partes[0].equals("OK")) {
			// El servidor envía OK:[ID], ejemplo: OK:1
			ipServer = dp.getAddress(); // Aseguramos que la IP del servidor es correcta
            if (partes.length > 1 && juego != null) {
                try {
                    // Asigna el ID del jugador (1 o 2) en la PantallaJuego
                    juego.setJugadorID(Integer.parseInt(partes[1])); 
                } catch (NumberFormatException e) {
                    System.err.println("Error al obtener ID del servidor.");
                }
            }
		} else if (msg.equals("Empieza")) {
			Global.empieza = true; 
		} else if (msg.startsWith("ESTADO:")) {
            // Recibimos el estado del juego (posiciones, vidas, etc.)
            if (juego != null) {
                juego.actualizarEstadoServidor(msg);
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