import java.util.Scanner;
public class Ejercicio16 {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		int hh=01,mm=10,ss=25,ssU=0,mm2=0,hh2=0;
		
		
		System.out.println("Ingresa Segundos:");
		ssU= sc.nextInt();
		
		ssU+=ss;
			
		if (ssU>=3600){
			
			hh2= ssU/3600;
			ssU = ssU%3600;	
		}
		if (ssU>=60){
			
			mm2 = ssU/60;
			ssU = ssU%60;	
		}
		
		mm2+=mm;
		hh2+=hh;
		
		System.out.println(hh2);
		System.out.println(mm2);
		System.out.println(ssU);
		
		
		
			
	}

}
