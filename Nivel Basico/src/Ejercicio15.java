import java.util.Scanner;
public class Ejercicio15 {

	public static void main(String[] args) {
		int s=0,h=0,m=0,ss;
		Scanner sc = new Scanner(System.in);
		System.out.println("Ingrese una cantidad de Segundos:");
		ss= sc.nextInt();
		
		if (ss>=3600) {
			h = ss/3600;
			ss = ss%3600;
		}
		if (ss>=60) {
			m = ss/60;
			ss = ss%60;
		}
		s=ss;
		
		System.out.println(h+":"+m+":"+s);
	}

}
