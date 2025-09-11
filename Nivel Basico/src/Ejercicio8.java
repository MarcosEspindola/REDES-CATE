import java.util.Scanner;

public class Ejercicio8 {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		
		System.out.print("Ingrese el radio del circulo: ");
		float radio = sc.nextFloat();
		
		sc.close();
		
		System.out.println("Radio: " + radio);
		
		float pi = 3.14159f;
		
		float diametro = 2 * radio;
		System.out.println("Diametro: " + diametro);
		
		float circunferencia = 2 * pi * radio;
		System.out.println("Circunferencia: " + circunferencia);
		
		float area = (float) (pi * Math.pow(radio, 2));
		System.out.println("Area: " + area);
		
	}

}
