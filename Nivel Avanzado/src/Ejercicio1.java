import java.util.Scanner;

public class Ejercicio1 {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		boolean granjero = false, lobo = false, gallina = false, saco = false;
		
		System.out.println("-------------------------------------------------");
		System.out.println("Si todos son 'true' se gana el juego");
		System.out.println("-------------------------------------------------");
		
		do {
			System.out.println(" ");
			System.out.println("granjero: " + granjero + " / lobo: " + lobo + " / gallina: " + gallina + " / saco: " + saco); 
			System.out.println(" ");
			System.out.print("Elija quien cruza primero con el granjero (saco/gallina/lobo/solo): ");
			String cruza = sc.next();
			
			if(cruza.equals("lobo") && lobo == granjero) {
				lobo = !lobo;
			} else if(cruza.equals("gallina") && gallina == granjero) {
				gallina = !gallina;
			} else if(cruza.equals("saco") && saco == granjero) {
				saco = !saco;
			} else if(!cruza.equals("solo")) {
				System.out.println("El objeto se encuentra del otro lado por lo que no puede cruzar con el granjero");
				System.out.println("Intente de nuevo");
				continue;
			}
			
			granjero = !granjero;

			if(lobo == gallina && granjero == !lobo) {
				System.out.println("Perdiste, el lobo se comio a la gallina");
				break;
			}
			
			if(saco == gallina && granjero == !gallina) {
				System.out.println("Perdiste, la gallina se comio el saco");
				break;
			}
			
		} while(granjero != true || lobo !=true || gallina != true || saco != true); 
		
		sc.close();
		
		System.out.println("ganaste");
		
	}

}
