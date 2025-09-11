import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Scanner;
public class Ejercicio14 {



	    public static void main(String[] args) {
	        System.out.println("Ingrese un DD MM AAAA");
	    	Scanner sc = new Scanner(System.in);
	        
	        int d = sc.nextInt();
	        int m = sc.nextInt();
	        int a = sc.nextInt();
	        sc.close();

	        try {
	            LocalDate fecha = LocalDate.of(a, m, d); 
	            String mes = fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
	            System.out.printf("%d de %s de %d%n", d, mes, a);
	        } catch (Exception e) {
	            System.out.println("Fecha inválida. Usa el formato: dd mm aaaa.");
	        }
	    }
	
}
