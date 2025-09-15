import java.util.Scanner;

public class Ejercicio4 {

    // --- Parser mínimo ---
    static class P {
        String s; int i=-1, ch;
        P(String t){ s=t.replaceAll("\\s+",""); next(); }
        void next(){ ch=(++i<s.length())?s.charAt(i):-1; }
        boolean eat(int c){ if(ch==c){ next(); return true; } return false; }

        double parse(){ double x=expr(); if(ch!=-1) throw new RuntimeException("Error cerca de '"+(char)ch+"'"); return x; }

        // expr = term { (+|-) term }
        double expr(){
            double x=term();
            while(true){
                if(eat('+')) x+=term();
                else if(eat('-')) x-=term();
                else return x;
            }
        }

        // term = pow { (*|/) pow }
        double term(){
            double x=pow();
            while(true){
                if(eat('*')) x*=pow();
                else if(eat('/')) x/=pow();
                else return x;
            }
        }

        // pow = unary [ ^ pow ]   (derecha)
        double pow(){
            double a=unary();
            if(eat('^')) return Math.pow(a, pow());
            return a;
        }

        // unary = (+|-) unary | primary
        double unary(){
            if(eat('+')) return +unary();
            if(eat('-')) return -unary();
            return primary();
        }

        // primary = número | (expr) | sqrt(expr) | root(expr,expr)
        double primary(){
            if(eat('(')){
                double x=expr();
                if(!eat(')')) throw new RuntimeException("Falta ')'");
                return x;
            }
            if(isAlpha(ch)){                 // funciones
                String f=ident();
                if(!eat('(')) throw new RuntimeException("Falta '('");
                double a=expr();
                if(f.equals("sqrt")){
                    if(!eat(')')) throw new RuntimeException("Falta ')'");
                    return Math.sqrt(a);
                } else if(f.equals("root")){
                    if(!eat(',')) throw new RuntimeException("Falta ','");
                    double n=expr();
                    if(!eat(')')) throw new RuntimeException("Falta ')'");
                    return Math.pow(a, 1.0/n);
                } else throw new RuntimeException("Función: "+f);
            }
            if(isDigit(ch) || ch=='.'){     // número simple
                int st=i;
                while(isDigit(ch) || ch=='.') next();
                return Double.parseDouble(s.substring(st,i));
            }
            throw new RuntimeException("Token inesperado");
        }

        String ident(){ int st=i; while(isAlphaNum(ch)) next(); return s.substring(st,i); }
        boolean isDigit(int c){ return c>='0'&&c<='9'; }
        boolean isAlpha(int c){ return (c>='a'&&c<='z')||(c>='A'&&c<='Z'); }
        boolean isAlphaNum(int c){ return isAlpha(c)||isDigit(c); }
    }

    // --- Programa principal ---
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        System.out.println("Calculadora: + - * / ^, sqrt(x), root(x,n). Vacío para salir.");
        while(true){
            System.out.print("> ");
            String line=sc.nextLine();
            if(line==null || line.trim().isEmpty()) break;
            try{
                double r=new P(line).parse();
                // imprime entero sin .0 cuando aplica
                if (Math.abs(r - Math.rint(r)) < 1e-12) System.out.println("= " + (long)Math.rint(r));
                else System.out.println("= " + r);
            }catch(Exception e){
                System.out.println("Expresión inválida: " + e.getMessage());
            }
        }
    }
}

