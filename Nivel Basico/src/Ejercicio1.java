import java.util.Scanner;

public class Ejercicio1 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.print("ingrese el peso: ");
		int peso = sc.nextInt();
		
		sc.close();
		
		float pl = (peso * 17) / 100;
		
		System.out.println("El peso lunar es: " + pl);
	}

}
