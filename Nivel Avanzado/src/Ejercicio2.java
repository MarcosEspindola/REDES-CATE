import java.util.Arrays;

public class Ejercicio2 {
	
	    public static void main(String[] args) {
	        // Índices de trabajos
	        final int DIS = 0; // Diseñadora de moda
	        final int FLO = 1; // Florista
	        final int JAR = 2; // Jardinera
	        final int DIR = 3; // Directora de orquesta

	        String[] trabajos = {
	            "Diseñadora de moda",
	            "Florista",
	            "Jardinera",
	            "Directora de orquesta"
	        };

	        // tC, tL, tM, tN = trabajo de Clara, Luisa, María y Nélida (respectivamente)
	        for (int tC = 0; tC < 4; tC++) {
	            // (a) Clara es alérgica a las plantas -> no florista ni jardinera
	            if (tC == FLO || tC == JAR) continue;

	            for (int tL = 0; tL < 4; tL++) {
	                if (tL == tC) continue;
	                // (b) Luisa y la florista comparten dpto -> Luisa NO es florista
	                if (tL == FLO) continue;
	                // (c) Luisa solo rock -> no directora de orquesta
	                if (tL == DIR) continue;

	                for (int tM = 0; tM < 4; tM++) {
	                    if (tM == tC || tM == tL) continue;
	                    // (c) María solo rock -> no directora de orquesta
	                    if (tM == DIR) continue;

	                    for (int tN = 0; tN < 4; tN++) {
	                        if (tN == tC || tN == tL || tN == tM) continue;
	                        // (d) Jardinera, diseñadora y Nélida son 3 personas distintas
	                        //     => Nélida NO es jardinera ni diseñadora
	                        if (tN == JAR || tN == DIS) continue;

	                        // Derivado de (b)+(c)+(d): Nélida tampoco puede ser florista.
	                        // Si Nélida fuera florista, Luisa (que no puede ser directora ni florista)
	                        // tendría que ser diseñadora o jardinera y viviría con Nélida,
	                        // con lo que Nélida "conocería" a la diseñadora o jardinera,
	                        // contradiciendo (d).
	                        if (tN == FLO) continue;

	                        // Si llegamos aquí, es una solución válida
	                        System.out.println("Solución encontrada:");
	                        System.out.println(" - Clara:  " + trabajos[tC]);
	                        System.out.println(" - Luisa:  " + trabajos[tL]);
	                        System.out.println(" - María:  " + trabajos[tM]);
	                        System.out.println(" - Nélida: " + trabajos[tN]);
	                    }
	                }
	            }
	        }
	    }
	}



