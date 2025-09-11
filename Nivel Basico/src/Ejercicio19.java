import java.util.Scanner;

public class Ejercicio19 {

	public static void main(String[] args) {
		//19) Crear un programa que pida al usuario una velocidad en Km/h, la convierta a m/s e
		//informe por consola la conversión.
		Scanner sc = new Scanner(System.in);
		float km, ms;
		
		System.out.print("Ingresa Los KM/H: ");
		km = sc.nextFloat();
		ms = km/3.6f;
		System.out.println("Los M/S son:"+ms);
	}

}
