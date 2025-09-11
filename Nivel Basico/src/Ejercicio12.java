import java.util.Scanner;

public class Ejercicio12 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.print ("Ingrese año: ");
		int año = sc.nextInt();
		
		sc.close();
		
		if(año % 400 == 0) {
			System.out.println("El año es bisiesto");
		} else if(año % 4 == 0) {
			if(año % 100 == 0) {
				System.out.println("El año no es bisiesto");
			} else {
				System.out.println("El año es bisiesto");
			}
			
		} else {
			System.out.println("El año no es bisiesto");
		}
	}

}
