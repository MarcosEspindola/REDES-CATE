
public class Ejercicio3 {
	public static void main(String[] args) {
        boolean rojo = false; // Dado: el frasco rojo NO es venenoso
        int soluciones = 0;

        for (int an = 0; an < 2; an++)        // anaranjado
        for (int am = 0; am < 2; am++)        // amarillo
        for (int ve = 0; ve < 2; ve++)        // verde
        for (int az = 0; az < 2; az++)        // azul
        for (int vi = 0; vi < 2; vi++) {      // violeta
            boolean anaranjado = an == 1;
            boolean amarillo   = am == 1;
            boolean verde      = ve == 1;
            boolean azul       = az == 1;
            boolean violeta    = vi == 1;

            // Reglas:
            if (!(violeta ^ azul)) continue;        // (a) uno con veneno y el otro no
            if (!(rojo ^ amarillo)) continue;       // (b) uno con veneno y el otro no
            if (!(azul ^ anaranjado)) continue;     // (c) uno con veneno y el otro no
            if (violeta && amarillo) continue;      // (d) no ambos venenosos
            // (e) rojo y anaranjado: hay uno sin veneno -> se cumple porque rojo=false
            if (verde && azul) continue;            // (f) no ambos venenosos

            // Si llegó aquí, es solución
            soluciones++;
            System.out.println("Solución #" + soluciones + ":");
            System.out.println(" - rojo: "        + (rojo ? "VENENO" : "no veneno"));
            System.out.println(" - anaranjado: "  + (anaranjado ? "VENENO" : "no veneno"));
            System.out.println(" - amarillo: "    + (amarillo ? "VENENO" : "no veneno"));
            System.out.println(" - verde: "       + (verde ? "VENENO" : "no veneno"));
            System.out.println(" - azul: "        + (azul ? "VENENO" : "no veneno"));
            System.out.println(" - violeta: "     + (violeta ? "VENENO" : "no veneno"));
            System.out.println();
        }

        if (soluciones == 0) System.out.println("No hay soluciones.");
    }

	}

