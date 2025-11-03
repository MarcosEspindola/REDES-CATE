package red;

import java.rmi.UnknownHostException;
import java.net.InetAddress;

// DireccionRed: Un objeto que guarda la dirección única de un cliente conectado.
// El servidor usa esto como una "dirección postal" para enviar mensajes.
public class DireccionRed{
	
	private InetAddress ip; // La dirección IP (la "calle" de la casa del cliente).
	private int puerto;     // El número de puerto (la "puerta" específica para el juego).
	
	// Constructor: Se ejecuta cuando el servidor acepta una nueva conexión.
	public DireccionRed(InetAddress ip, int puerto) {
	
		this.ip = ip;
		this.puerto = puerto;
	}
	
	// Devuelve la dirección IP del cliente.
	public InetAddress getIp(){
		return ip;	
	}
	
	// Devuelve el puerto del cliente.
	public int getPuerto(){
		return puerto;	
	}
}