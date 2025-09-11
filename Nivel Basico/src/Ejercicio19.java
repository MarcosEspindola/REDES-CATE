import java.util.Scanner;

public class Ejercicio19 {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		float km, ms;
		
		System.out.print("Ingresa Los KM/H: ");
		km = sc.nextFloat();
		ms = km/3.6f;
		System.out.println("Los M/S son:"+ms);
	}

}
