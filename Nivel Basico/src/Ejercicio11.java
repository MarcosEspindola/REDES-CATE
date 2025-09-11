import java.util.Scanner;

public class Ejercicio11 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Presione ENTER para realizar llamada");
		sc.next();
		
		long tiempoInicial = System.currentTimeMillis();
		
		System.out.println("Presione ENTER para finalizar llamada");
		sc.next();
		
		sc.close();
		
		long tiempoFinal = System.currentTimeMillis();
		
		long tiempoTotal = tiempoFinal - tiempoInicial;
		
		long segundos = (tiempoTotal/1000);
		
		long minutos = 0;
		
		if (segundos >= 60){
			minutos = segundos/60;
			segundos = segundos % 60;
		}
		
		float costo = 5;
		long tiempoSobrante;
		
		if (minutos >= 5) {
			tiempoSobrante = minutos - 5;
			costo += tiempoSobrante * 0.5;
		}
		
		
		
		System.out.println("Tiempo total en llamada: "+ minutos + " minutos " + segundos + " segundos");
		System.out.println("Costo total: " + costo);
	}
}
