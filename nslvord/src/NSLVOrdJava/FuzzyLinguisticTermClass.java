package NSLVOrdJava;

import java.io.Serializable;

/**
 * @file FuzzyLinguisticTermClass.java
 * @brief define the linguistic term (fuzzy label) of a fuzzy set
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date diciembre 2015
 * @note Implement of linguistic term (fuzzy label) of a fuzzy set
 */
public class FuzzyLinguisticTermClass implements Serializable{

    private static final double MISSING = -999999999; /// TENGO QUE REPASAR PARA QUÉ SE UTILIZA ESTE VALOR.......................
    private double a;       // primer valor que define la función de pertenencia del término linguístico (etiqueta)
    private double b;       // segundo valor que define la función de pertenencia del término linguístico (etiqueta)
    private double c;       // tercer valor que define la función de pertenencia del término linguístico (etiqueta)
    private double d;       // cuarto valor que define la función de pertenencia del término linguístico (etiqueta)
    private int abInf;  // indica si a=b=-inf (func. pertenencia tipo 'L')
    private int cdInf;  // indica si c=d=+inf (func. pertenencia tipo 'gamma')
    private String name;  // nombre del término lingüístico

    /** Default constructor */
    public FuzzyLinguisticTermClass () { 
        a= b= c= d= 0;
        abInf= cdInf= 0;
        name = "Creado, no usado";
    };
    
    /** constructor
     * @param a a point of the trapezoidal fuzzy membership function
     * @param b b point of the trapezoidal fuzzy membership function
     * @param d c point of the trapezoidal fuzzy membership function
     * @param c d point of the trapezoidal fuzzy membership function
     * @param cdInf 1 if points c and d are the same and indicate the infinite
     * @param abInf 1 if points a and b are the same and indicate the infinite
     * @param name name of the liguistic term
     */
    public FuzzyLinguisticTermClass (double a, double b, double c, double d,
            int abInf, int cdInf, String name) { 
        this.a= a;
        this.b= b;
        this.c= c;
        this.d= d;
        this.abInf= abInf;
        this.cdInf= cdInf;
        this.name= name;
    };
    
    /** constructor
     * @param a a point of the trapezoidal fuzzy membership function
     * @param b b point of the trapezoidal fuzzy membership function
     * @param d c point of the trapezoidal fuzzy membership function
     * @param c d point of the trapezoidal fuzzy membership function
     * @param name name of the liguistic term
     */
    public FuzzyLinguisticTermClass (double a, double b, double c, double d,
            String name) { 
        this.a= a;
        this.b= b;
        this.c= c;
        this.d= d;
        if (a==b && b==c && c!=d){
            abInf= 1;            
        }
        if (b==c && c==d && a!=b){
            cdInf= 1;
        }
        this.name= name;
    };

    /** Used for copy constructor
     * @param orig 
     */
    protected FuzzyLinguisticTermClass(FuzzyLinguisticTermClass orig){
      this.a= orig.a;
      this.b= orig.b;
      this.c= orig.c;
      this.d= orig.d;
      this.abInf= orig.abInf;
      this.cdInf= orig.cdInf;
      this.name= orig.name;
    }
    
    /** copy constructor
     * @return 
     */
    public FuzzyLinguisticTermClass copy(){
      return new FuzzyLinguisticTermClass(this);
    }

    /**
    * Set the value of a
    * @param newVar the new value of a
    */
    public void setA ( double newVar ) {
        a = newVar;
    }

    /**
    * Get the value of a
    * @return the value of a
    */
    public double getA ( ) {
        return a;
    }

    /**
    * Set the value of b
    * @param newVar the new value of b
    */
    public void setB ( double newVar ) {
        b = newVar;
    }

    /**
    * Get the value of b
    * @return the value of b
    */
    public double getB ( ) {
        return b;
    }

    /**
    * Set the value of c
    * @param newVar the new value of c
    */
    public void setC ( double newVar ) {
        c = newVar;
    }

    /**
    * Get the value of c
    * @return the value of c
    */
    public double getC ( ) {
        return c;
    }

    /**
    * Set the value of d
    * @param newVar the new value of d
    */
    public void setD ( double newVar ) {
        d = newVar;
    }

    /**
    * Get the value of d
    * @return the value of d
    */
    public double getD ( ) {
        return d;
    }

    /**
    * Set the value of abInf
    * @param newVar the new value of abInf
    */
    public void setAbInf ( int newVar ) {
        abInf = newVar;
    }

    /**
    * Get the value of abInf
    * @return the value of abInf
    */
    public int getAbInf ( ) {
        return abInf;
    }

    /**
    * Set the value of cdInf
    * @param newVar the new value of cdInf
    */
    public void setCdInf ( int newVar ) {
        cdInf = newVar;
    }

    /**
    * Get the value of cdInf
    * @return the value of cdInf
    */
    public int getCdInf ( ) {
        return cdInf;
    }

    /**
    * Set the value of name
    * @param newVar the new value of name
    */
    public void setName ( String newVar ) {
        name = newVar;
    }

    /**
    * Get the value of name
    * @return the value of name
    */
    public String getName ( ) {
        return name;
    }

/* ......................................................................... */
    
    /**
     * Eval the "degree of membership" of x in this linguisticTerm. (anterior "adaptacion")
     * The membership function is the trampf function defined in this linguisticTerm
     * @param x input to eval
     * @return degree of membership
     */
    public double adaptation(double x){
        
        if (x == MISSING){
            return 1;
        }
        if ((abInf == 1 && x < c) // comienza con infinito y estás en la parte de valor 1
            || (cdInf == 1 && x > b)){ //termina con infinito y estás en la parte de valor 1
            
            return 1;
        }
        if (x < a)          // estás a la izquierda de la función de pertenencia
            return 0;            
        else if (x < b)     // estás en la pendiente de la izda
            return (x-a) / (b-a);
        else if (x <= c)    // estás en el rango de valor 1
            return 1;
        else if (x < d)     // estás en la pendiente de la dcha
            return (d-x) / (d-c);
        else                // estás a la derecha de la función de pertenencia
            return 0;
        
    }
}
