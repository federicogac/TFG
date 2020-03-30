package NSLVOrdJava;

import java.io.Serializable;

/**
 * @file FuzzyLinguisticVariableClass.java
 * @brief define the liguistic variable
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date diciembre 2015
 * @note Implement of linguistic variable 
 * @use FuzzyLinguisticTermClass
 */
public class FuzzyLinguisticVariableClass implements Serializable {
   
    private String name;            // nombre de la variable lingüística
    private int unit;               // define of measure to variable comparison
    private double numTermAutomatic; // Indica el número de terminos linguisticos que debe hacer de forma automatica
    private int variableType;         // indica si es consecuente (1) o antecedente (0) - o inactiva (-1)
    private int fuzzyLinguisticTermNum; //número de términos lingüisticos (etiquetas) que forman la variable
    private double infRange;       // rango inferior del universo de discurso (dominio)
    private double supRange;       // rango superior del universo de discurso (dominio)
    private int infRangeIsInf; // indica si el rango inferior es infinito (primera función de pertenencia tipo 'L', a=b=-inf)
    private int supRangeIsInf; // indica si el rango superior es infinito (última función de pertenencia tipo 'gamma', c=d=+inf)
    private FuzzyLinguisticTermClass[] fuzzyLinguisticTermList; //lista de términos lingüísticos (etiquetas)
    

    /** Default constructor */
    public FuzzyLinguisticVariableClass () { 
        name="Sin asignar";
        unit= 0;
        numTermAutomatic= 0;
        variableType= 1;
        fuzzyLinguisticTermNum= 0;
        infRange= supRange= 0;
        infRangeIsInf= supRangeIsInf= 0;
        fuzzyLinguisticTermList= null;      
    };
   
    /**
     * constructor
     * @param name name of liguistic variable
     * @param unit (not used in this version)
     * @param convFactor (not used in this version)
     * @param variableType indicate if the variable is antecedent or consequent
     * @param infRange inferior range of variable
     * @param supRange superior range of variable
     * @param infRangeIsInf 1 if inferior range of variable is infinite (a==b in first linguistic term)
     * @param supRangeIsInf 1 if superior range of variable is infinite (c==d in last linguistic term)
     * @param fuzzyLinguisticTermNum number of linguistic term of variable
     */
    public FuzzyLinguisticVariableClass (String name, int unit, double convFactor, int variableType,            
            double infRange, double supRange, int infRangeIsInf, int supRangeIsInf,
            int fuzzyLinguisticTermNum) { 
      
        // si el valor "numTermAutomatic" > 0 se establece como fuzzyLinguisticTermNum
        // y se asignan automáticamente los nombres
        if(numTermAutomatic > 0){          
          fuzzyLinguisticTermNum = (int) numTermAutomatic;
        }      
      
        this.name= name;
        this.unit= unit;
        this.numTermAutomatic= numTermAutomatic;
        this.variableType= variableType;
        this.fuzzyLinguisticTermNum= fuzzyLinguisticTermNum;
        this.infRange= infRange;
        this.supRange= supRange;
        this.infRangeIsInf= infRangeIsInf;
        this.supRangeIsInf= supRangeIsInf;        
        this.fuzzyLinguisticTermList= new FuzzyLinguisticTermClass[fuzzyLinguisticTermNum];
    };
   
    /**
     * contructor
     * @param name name of liguistic variable
     * @param unit (not used in this version)
     * @param numTermAutomatic (not used in this version)
     * @param variableType indicate if the variable is antecedent or consequent
     * @param infRange inferior range of variable
     * @param supRange superior range of variable
     * @param fuzzyLinguisticTermNum number of linguistic term of variable
     */
    public FuzzyLinguisticVariableClass (String name, int unit, double numTermAutomatic, int variableType, 
            double infRange, double supRange, int fuzzyLinguisticTermNum) { 

        if(numTermAutomatic > 0){
          fuzzyLinguisticTermNum = (int) numTermAutomatic;
        }      
      
        this.name= name;
        this.unit= unit;
        this.numTermAutomatic= numTermAutomatic;
        this.variableType= variableType;
        this.fuzzyLinguisticTermNum= fuzzyLinguisticTermNum;
        this.infRange= infRange;
        this.supRange= supRange;
        this.infRangeIsInf= this.supRangeIsInf= 0;
        this.fuzzyLinguisticTermList= new FuzzyLinguisticTermClass[fuzzyLinguisticTermNum];
    };
    
    /** Used for copy constructor
     * @param orig 
     */
    protected FuzzyLinguisticVariableClass(FuzzyLinguisticVariableClass orig){
      this.name= orig.name;
      this.unit= orig.unit;
      this.numTermAutomatic= orig.numTermAutomatic;
      this.variableType= orig.variableType;
      this.fuzzyLinguisticTermNum= orig.fuzzyLinguisticTermNum;
      this.infRange= orig.infRange;
      this.supRange= orig.supRange;
      this.infRangeIsInf= orig.infRangeIsInf;
      this.fuzzyLinguisticTermList= new FuzzyLinguisticTermClass[this.fuzzyLinguisticTermNum];
      for (int i=0; i < this.fuzzyLinguisticTermNum; i++){
        this.fuzzyLinguisticTermList[i]= orig.fuzzyLinguisticTermList[i].copy();
      }
    }
    
    /** copy constructor
     * @return 
     */
    public FuzzyLinguisticVariableClass copy(){
      return new FuzzyLinguisticVariableClass(this);
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

    /**
    * Set the value of unit
    * @param newVar the new value of unit
    */
    public void setUnit ( int newVar ) {
        unit = newVar;
    }

    /**
    * Get the value of unit
    * @return the value of unit
    */
    public int getUnit ( ) {
        return unit;
    }

    /**
    * Set the value of convFactor
    * @param newVar the new value of convFactor
    */
    public void setNumTermAutomatic ( double newVar ) {
        numTermAutomatic = newVar;
    }

    /**
    * Get the value of convFactor
    * @return the value of convFactor
    */
    public double getNumTermAutomatic ( ) {
        return numTermAutomatic;
    }
    
    /**
    * Set the value of variableType
    * @param newVar the new value of variableType
    */
    public void setVariableType ( int newVar ) {
        variableType = newVar;
    }

    /**
    * Get the value of variableType
    * @return the value of variableType
    */
    public int getVariableType ( ) {
        return variableType;
    }


    /**
    * Set the value of fuzzyLinguisticTermNum
    * @param newVar the new value of fuzzyLinguisticTermNum
    */
    public void setFuzzyLinguisticTermNum ( int newVar ) {
        fuzzyLinguisticTermNum = newVar;
    }

    /**
    * Get the value of fuzzyLinguisticTermNum
    * @return the value of fuzzyLinguisticTermNum
    */
    public int getFuzzyLinguisticTermNum ( ) {
        return fuzzyLinguisticTermNum;
    }

    /**
    * Set the value of infRange
    * @param newVar the new value of infRange
    */
    public void setInfRange ( double newVar ) {
        infRange = newVar;
    }

    /**
    * Get the value of infRange
    * @return the value of infRange
    */
    public double getInfRange ( ) {
        return infRange;
    }

    /**
    * Set the value of supRange
    * @param newVar the new value of supRange
    */
    public void setSupRange ( double newVar ) {
        supRange = newVar;
    }

    /**
    * Get the value of supRange
    * @return the value of supRange
    */
    public double getSupRange ( ) {
        return supRange;
    }

    /**
    * Set the value of infRangeIsInf
    * @param newVar the new value of infRangeIsInf
    */
    public void setInfRangeIsInf ( int newVar ) {
        infRangeIsInf = newVar;
    }

    /**
    * Get the value of infRangeIsInf
    * @return the value of infRangeIsInf
    */
    public int getInfRangeIsInf ( ) {
        return infRangeIsInf;
    }

    /**
    * Set the value of supRangeIsInf
    * @param newVar the new value of supRangeIsInf
    */
    public void setSupRangeIsInf ( int newVar ) {
        supRangeIsInf = newVar;
    }

    /**
    * Get the value of supRangeIsInf
    * @return the value of supRangeIsInf
    */
    public int getSupRangeIsInf ( ) {
        return supRangeIsInf;
    }

    /**
    * Set the value of fuzzyLinguisticTermList
    * @param newVar the new value of fuzzyLinguisticTermList
    */
    public void setFuzzyLinguisticTermList ( FuzzyLinguisticTermClass[] newVar ) {
        fuzzyLinguisticTermList = newVar;
    }

    /**
    * Get the value of fuzzyLinguisticTermList
    * @return the value of fuzzyLinguisticTermList
    */
    public FuzzyLinguisticTermClass[] getFuzzyLinguisticTermList ( ) {
        return fuzzyLinguisticTermList;
    }

    /**
    * Set the value of fuzzyLinguisticTerm at position i
    * @param newVar the new value of fuzzyLinguisticTerm at position i
    * @param i index of linguistic variable to set
    * @return -1 if no valid position, i otherwise
    */
    public int setFuzzyLinguisticTermList ( FuzzyLinguisticTermClass newVar, int i ) {
        if (i < fuzzyLinguisticTermNum){
            fuzzyLinguisticTermList[i] = newVar;
            return i;
        }        
        else{
            return -1;
        }
    }

    /**
    * Get the value of fuzzyLinguisticTerm at position i
    * @param i index of linguistic variable to set
    * @return null if no valid position, fuzzyLinguisticTerm at position i otherwise
    */
    public FuzzyLinguisticTermClass getFuzzyLinguisticTermList (int i) {
        if (i < fuzzyLinguisticTermNum){
            return fuzzyLinguisticTermList[i];
        }
        else{
            return null;
        }
            
    }
    
/* ......................................................................... */

    /**
     * Eval the "degree of membership" of x in this linguistic variable. (anterior "adaptacion")
     * The membership function is the trampf function defined in this linguistic variable
     * RETURN THE MAX DEGREE OF MEMBERSHIP IN ALL LINGUISTIC TERM
     * @param x input to eval
     * @return the MAX degree of membership of x in all linguistic term (labels), -1 if ERROR
     */
    public double maxAdaptation(double x){
        double mayor, nuevo;
        
        if (this.fuzzyLinguisticTermNum == 0){ // el dominio no está creado
            return -1;
        }
        
        mayor= this.fuzzyLinguisticTermList[0].adaptation(x);
        for (int i=0; i < this.fuzzyLinguisticTermNum; i++){
            nuevo = this.fuzzyLinguisticTermList[i].adaptation(x);
            
            if (nuevo > mayor){
                mayor= nuevo;
            }
        }
        
        return mayor;
    }    
    
    /**
     * Eval the membership degree of "x" to "label" divided by the mayor membership degree of "x" (anterior "adaptacion")
     * @param x input to eval
     * @param label 
     * @return normalized membership degree to mayor membership degree, -1 if ERROR
     */
    public double normalizedAdaptation(double x, int label){        
        double valorMaxAdaptation;
        if (this.fuzzyLinguisticTermNum == 0){ // el dominio no está creado
            return -1;
        }
        if (label >= this.fuzzyLinguisticTermNum){  // se ha indicado una etiqueta no válida
            return -1;
        }

        valorMaxAdaptation= this.maxAdaptation(x);
        if (valorMaxAdaptation==0){ // evitar la división por 0
            return 0;
        }
        
        return this.fuzzyLinguisticTermList[label].adaptation(x) / valorMaxAdaptation;    
    }
    
    /**
     * Eval the membership degree of "x" to a "set of labels". The labels are represented
     * as a ordered vector of ceros and ones. Cero represents ausence of the label,
     * oe represents presence of the label (anterior "adaptacion")
     * Eval the MAX membership degree of x to a set of labels / maxAdaptation(x)
     * @param x input to eval
     * @param labels String of labels (1101001...) to eval the membership (0 -> no presence, 1 -> presence)
     * @return normalized membership degree to string of labels
     */
    public double normalizedAdaptationToLabels(double x, int[] labels){        
        double mayor, nuevo, valorMaxAdaptation;
        
        if (this.fuzzyLinguisticTermNum == 0){ // el dominio no está creado
            return -1;
        }
        
        if (labels.length > this.fuzzyLinguisticTermNum){ // se ha indicado una cadena demasiado larga (no válida)
            return -1;
        }
        
        mayor= 0;
        for (int i=0; i < this.fuzzyLinguisticTermNum && i < labels.length; i++){
            if (labels[i] == 1){
                nuevo= this.fuzzyLinguisticTermList[i].adaptation(x);
                if (nuevo > mayor){
                    mayor= nuevo;
                }                    
            }
        }
        valorMaxAdaptation= this.maxAdaptation(x);
        if (valorMaxAdaptation==0){ // evitar la división por 0
            return 0;
        }
        return mayor/valorMaxAdaptation;
    }
    
}
