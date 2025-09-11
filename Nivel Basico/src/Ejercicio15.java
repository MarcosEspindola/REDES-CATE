import java.util.Scanner;
public class Ejercicio15 {

	public static void main(String[] args) {
		// 15) Crear un programa que lea un número entero n mayor a cero que identifica una
		//cantidad de segundos, y calcule e imprima el número de horas, minutos y segundos
		//contenidos en ella. Ejemplo: Para n = 15723 se debe imprimir 4 horas, 22 minutos y 3 segundos.
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
