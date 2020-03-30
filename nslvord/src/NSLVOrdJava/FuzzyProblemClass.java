package NSLVOrdJava;

import java.util.Scanner;
import java.io.*;
import java.util.Locale;
import java.util.Arrays;

import keel.Dataset.*;

/**
 * @file fuzzyProblemClass.java
 * @brief define the fuzzy problem
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date diciembre 2015
 * @note Implement of fuzzy problem
 * @use FuzzyLinguisticVariableClass
 */
public class FuzzyProblemClass implements Serializable{
     private int fuzzyLinguisticVariableNum;          // número de conjuntos difusos que forman el problema
     private FuzzyLinguisticVariableClass[] fuzzyLinguisticVariableList;   // lista de conjuntos difusos que forman el problema
     private int consequentIndexOriginal;   // aquí se guardará el indice original para el consecuente, 
                                            // ya que para nosotros el consecuente siempre estará el último
     private int shift; // % de la mitad del tamaño de la etiqueta para el desplazamiento
     private int direction; // direction of shift
     private int homogeneousLabel; // 0 -> non homogeneousLabel (min % of examples per label), 1 -> homogeneous label (no min % of examples per label)
     
    
    /** Default constructor */
    public FuzzyProblemClass(){
        fuzzyLinguisticVariableNum=0;    // no hay lista de conjuntos difursos
        shift= -1;
        direction= 0;
        fuzzyLinguisticVariableList= null;
        homogeneousLabel=1;
    }
    
    /** Used for copy constructor
     * @param orig 
     */
    protected FuzzyProblemClass(FuzzyProblemClass orig){
      this.fuzzyLinguisticVariableNum= orig.fuzzyLinguisticVariableNum;
      this.fuzzyLinguisticVariableList= new FuzzyLinguisticVariableClass[this.fuzzyLinguisticVariableNum];
      for (int i= 0; i < this.fuzzyLinguisticVariableNum; i++){
        this.fuzzyLinguisticVariableList[i]= orig.fuzzyLinguisticVariableList[i].copy();
      }
      this.shift= orig.shift;
      this.direction= orig.direction;
      this.homogeneousLabel= orig.homogeneousLabel;
    }
    
    /** copy constructor
     * @return 
     */
    public FuzzyProblemClass copy(){
      return new FuzzyProblemClass(this);
    }

    /** Constructor
     * Structure of DomainFile
     * --------
     * numberOfLinguisticVariable 
     * nameOfLinguisticVariable
     * consequent (0=consequent, 1=consequent)
     * Unit (define of measure to variable comparison)
     * numberOfLinguisticTermsAutomatic (factor to be variable measure comparable)
     * InfRange (inferior range of linguistic variable)
     * SupRange (superior range of linguistic variable)
     * numberOfLinguisticTerms
     * FirstValueOfMembershipFunction 2ndValMembFunct 3rdValMembFunct 4thValMembFunct NameofLinguisticTerm
     * nameOfLinguisticVariable
     * consequent (0=consequent, 1=consequent)
     * Unit (define of measure to variable comparison)
     * cFactor (factor to be variable measure comparable)
     * InfRange (inferior range of linguistic variable)
     * SupRange (superior range of linguistic variable)
     * numberOfLinguisticTerms
     * FirstValueOfMembershipFunction 2ndValMembFunct 3rdValMembFunct 4thValMembFunct NameofLinguisticTerm
     * ...
     * --------
     * 
     * @param DomainFile File with domain definition
     * @deprecated used to create the fuzzy problem in format NSLV
     */
    public FuzzyProblemClass(String DomainFile){
        Scanner scanFile= null;
        int numVariable, numTerm;

        shift= 0;
        direction= 0;
                
        try{
            scanFile= new Scanner(new FileReader(DomainFile));
            scanFile.useLocale(Locale.ENGLISH);   // configura el formato de números
            numVariable= scanFile.nextInt();
            this.fuzzyLinguisticVariableNum= numVariable ;
            this.fuzzyLinguisticVariableList= new FuzzyLinguisticVariableClass[numVariable];
            
            int indexI=0, indice;
            for (int i=0; i < numVariable; i++){
                
                String name= scanFile.next();
                int consequent= scanFile.nextInt();
                int unit= scanFile.nextInt();
                double numTermAutomatic= scanFile.nextDouble();
                double infRange= scanFile.nextDouble();
                double supRange= scanFile.nextDouble();
                numTerm= scanFile.nextInt();

                double interval, a, b, c, d;
                String termName;
                FuzzyLinguisticTermClass auxLingTerm;
                if(numTermAutomatic > 0){
                  if (numTermAutomatic % 2 == 0){
                    numTermAutomatic++;
                  }
                }      

                
                if (consequent == 1){
                  consequentIndexOriginal= i;
                  indice= numVariable-1;
                }
                else{
                  indice= indexI;
                  indexI++;
                }
                this.fuzzyLinguisticVariableList[indice]= new FuzzyLinguisticVariableClass(name, 
                        unit, numTermAutomatic, consequent, infRange, supRange, numTerm);

                if (numTermAutomatic > 0){                      
                  interval= (supRange - infRange) / (double) (numTermAutomatic-1);
                  // primera etiqueta
                  a= b= c= infRange;
                  d= infRange + interval;
                  termName= "S" + (int)(numTermAutomatic / 2);

                  auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
                  this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, 0);

                  // etiquetas centrales
                  for (int j=1; j < (int) (numTermAutomatic-1); j++){
                    a= b;
                    b= c= d;
                    d= d + interval;
                    if (j < (int) (numTermAutomatic/2)){
                      termName= "S" + (((int)numTermAutomatic/2)-j);
                    }
                    else if (j > (int) (numTermAutomatic/2)){
                      termName= "B" + ((j-(int)numTermAutomatic/2));                          
                    }
                    else{
                      termName= "CE";
                    }

                    auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
                    this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, j);                        
                  }

                  // última etiqueta
                  a= b;
                  b= c= d= supRange;
                  termName= "B" + (int)(numTermAutomatic / 2);

                  auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
                  this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, (int) (numTermAutomatic-1));


                  // saltar las etiquetas ya que van a ser automáticas
                  for (int j=0; j < numTerm; j++){
                      a= scanFile.nextDouble();
                      b= scanFile.nextDouble();
                      c= scanFile.nextDouble();
                      d= scanFile.nextDouble();
                      termName= scanFile.next();
                  }
                }
                else{
                  for (int j=0; j < numTerm; j++){
                      a= scanFile.nextDouble();
                      b= scanFile.nextDouble();
                      c= scanFile.nextDouble();
                      d= scanFile.nextDouble();
                      termName= scanFile.next();

                      auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
                      this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, j);
                  }    
                }
            }//for (int i=0; i < numVariable; i++){
            scanFile.close();            
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }finally{
            try{                   
                if( scanFile != null){  
                    scanFile.close();    
                }                 
            }catch (Exception e2){
                e2.printStackTrace();
                System.exit(-1);
            }
        }                
    }

    /**
     * constructor
     * @param dupLabels indicate if duplicate the number of labels of the linguistic variable
     * @param iSet set of instances (version Keel)
     * @param shift indicate the % of shitft from original intervals
     * @param direction 1 -> shift in right way, -1 -> shift in left way
     * @param maxPorcentExamp4Label if not indicated, it calculate the number of linguistic variable with this value
     * @deprecated not used in this version
     */
    public FuzzyProblemClass(int dupLabels, InstanceSet iSet, int shift, int direction, 
            int maxPorcentExamp4Label){
      
        int numVariable, numTerm;
        
        numVariable= iSet.getAttributeDefinitions().getNumAttributes();
        this.fuzzyLinguisticVariableNum= numVariable;
        this.fuzzyLinguisticVariableList= new FuzzyLinguisticVariableClass[numVariable];
        this.shift= shift;
        this.direction= direction;
        FuzzyLinguisticVariableClass LingVarOriginal= null;

        int indexI=0, indice;
        for (int i=0; i < numVariable; i++){
                   
            String name= iSet.getAttributeDefinitions().getAttribute(i).getName();           
            int consequent= iSet.getAttributeDefinitions().getAttribute(i).getDirectionAttribute();
            if (consequent == 1){ // la dirección está invertida respecto a lo nuestro
              consequent=0;
            }
            else{
              consequent=1;
            }
            int unit= 0;
            double numTermAutomatic=5;
            double infRange= iSet.getAttributeDefinitions().getAttribute(i).getMinAttribute();
            double supRange= iSet.getAttributeDefinitions().getAttribute(i).getMaxAttribute();
            numTerm= 0;

            double interval, a, b, c, d;
            String termName;
            FuzzyLinguisticTermClass auxLingTerm;

            if (consequent == 1){
              consequentIndexOriginal= i;
              indice= numVariable-1;
            }
            else{
              indice= indexI;
            }
            this.fuzzyLinguisticVariableList[indice]= new FuzzyLinguisticVariableClass(name, 
                    unit, numTermAutomatic, consequent, infRange, supRange, numTerm);

            interval= (supRange - infRange) / (double) (numTermAutomatic-1);
            // primera etiqueta
            a= b= c= infRange + (direction*interval*(shift*consequent/100.0));
            d= a + interval;
//            termName= "S" + (int)(numTermAutomatic / 2);
            termName= "VL";

            auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
            this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, 0);

            // etiquetas centrales
            for (int j=1; j < (int) (numTermAutomatic-1); j++){
              a= b;
              b= c= d;
              d= d + interval;
              if (j < (int) (numTermAutomatic/2)){
//                termName= "S" + (((int)numTermAutomatic/2)-j);
                termName= "L";
              }
              else if (j > (int) (numTermAutomatic/2)){
//                termName= "B" + ((j-(int)numTermAutomatic/2));                          
                termName= "H";
              }
              else{
//                termName= "CE";
                termName= "M";
              }

              auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
              this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, j);                                     
            }

            // última etiqueta
            a= b;
            b= c= d;
// en el caso del desplazamiento no coincide los valores de la última etiqueta con el rango superior            
//            b= c= d= supRange; 
            
//            termName= "B" + (int)(numTermAutomatic / 2);
            termName= "VH";

            auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
            this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, (int) (numTermAutomatic-1));
                        
            LingVarOriginal= this.getFuzzyLinguisticVariableList(indice);
            
            if (adjustLabels(dupLabels, indexI, indice, iSet, maxPorcentExamp4Label) == -1){ // hay menos de 5 etiquetas -> volvemos a las originales
              this.setFuzzyLinguisticVariableList(LingVarOriginal, indice);
            };
                        
            if (consequent == 0){
              indexI++;
            }
            
        }//for (int i=0; i < numVariable; i++){

    }
    
    /**
//     * constructor
     * @param iSet set of instances (version Keel)
     * @param numLabelsInputs number of labels of antecedents variables
     * @param numLabelsOutput number of labels of consequent variable
     * @param shift indicate the % of shitft from original intervals
     * @param direction 1 -> shift in right way, -1 -> shift in left way
     */
    public void FuzzyProblemClass(InstanceSet iSet, int numLabelsInputs, 
            int numLabelsOutput, int shift, int direction){
      
        int numVariable, numTerm;
        
        numVariable= iSet.getAttributeDefinitions().getNumAttributes();
        this.fuzzyLinguisticVariableNum= numVariable;
        this.fuzzyLinguisticVariableList= new FuzzyLinguisticVariableClass[numVariable];
        this.shift= shift;
        this.direction= direction;

        int indexI=0, indice;
        for (int i=0; i < numVariable; i++){
                   
            String name= iSet.getAttributeDefinitions().getAttribute(i).getName();           
            int consequent= iSet.getAttributeDefinitions().getAttribute(i).getDirectionAttribute();
            
            if (consequent == 1){ // la dirección está invertida respecto a lo nuestro
              consequent=0;
            }
            else{
              consequent=1;
            }
            
            int numNominalValues= iSet.getAttributeDefinitions().getAttribute(i).getNumNominalValues();
            
            if (numNominalValues == -1){
                int unit= 0;
                double numTermAutomatic=0;
                if (consequent == 0){ // atributo de entrada
                  numTermAutomatic= numLabelsInputs;
                }              
                else{
                  numTermAutomatic= numLabelsOutput;
                }
                double infRange= iSet.getAttributeDefinitions().getAttribute(i).getMinAttribute();
                double supRange= iSet.getAttributeDefinitions().getAttribute(i).getMaxAttribute();
                numTerm= 0;

                double interval, a, b, c, d;
                String termName;
                FuzzyLinguisticTermClass auxLingTerm;
                if(numTermAutomatic > 0){
                  if (numTermAutomatic % 2 == 0){
                    numTermAutomatic++;
                  }
                }      

                if (consequent == 1){
                  consequentIndexOriginal= i;
                  indice= numVariable-1;
                }
                else{
                  indice= indexI;
                  indexI++;
                }
                this.fuzzyLinguisticVariableList[indice]= new FuzzyLinguisticVariableClass(name, 
                        unit, numTermAutomatic, consequent, infRange, supRange, numTerm);

                interval= (supRange - infRange) / (double) (numTermAutomatic-1);
                // primera etiqueta
                a= b= c= infRange + (direction*interval*(shift*consequent/100.0));
                d= a + interval;
                termName= "S" + (int)(numTermAutomatic / 2);

                auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
                this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, 0);

                // etiquetas centrales
                for (int j=1; j < (int) (numTermAutomatic-1); j++){
                  a= b;
                  b= c= d;
                  d= d + interval;
                  if (j < (int) (numTermAutomatic/2)){
                    termName= "S" + (((int)numTermAutomatic/2)-j);
                  }
                  else if (j > (int) (numTermAutomatic/2)){
                    termName= "B" + ((j-(int)numTermAutomatic/2));                          
                  }
                  else{
                    termName= "CE";
                  }

                  auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
                  this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, j);                        
                }

                // última etiqueta
                a= b;
                b= c= d;
    // en el caso del desplazamiento no coincide los valores de la última etiqueta con el rango superior            
    //            b= c= d= supRange; 

                termName= "B" + (int)(numTermAutomatic / 2);

                auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
                this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, (int) (numTermAutomatic-1));
            }// if (numNonimalValues == -1){
            else{
                int unit= 0;
                double numTermAutomatic=0;
                numTermAutomatic= numNominalValues;

                double infRange= 0;
                double supRange= numNominalValues-1;
                numTerm= 0;

                double interval, a, b, c, d;
                String termName;
                FuzzyLinguisticTermClass auxLingTerm;

                if (consequent == 1){
                  consequentIndexOriginal= i;
                  indice= numVariable-1;
                }
                else{
                  indice= indexI;
                  indexI++;
                }
                this.fuzzyLinguisticVariableList[indice]= new FuzzyLinguisticVariableClass(name, 
                        unit, numTermAutomatic, consequent, infRange, supRange, numTerm);

                for (int j=0; j < (int) numTermAutomatic; j++){
                    a=b=c=d=j;
                    termName= iSet.getAttributeDefinitions().getAttribute(i).getNominalValue(j);

                    auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
                    this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, j);                    
                }
                
        
            }
        }//for (int i=0; i < numVariable; i++){
      
    }
    
    /**
     * constructor
     * @param iSet set of instances (version Keel)
     * @param numLabelsInputs number of labels of antecedents variables
     * @param numLabelsOutput number of labels of consequent variable
     * @param shift indicate the % of shitft from original intervals
     * @param direction 1 -> shift in right way, -1 -> shift in left way
     * @param homogeneousLabel 1: label must be homogeneous, 0: label must cover a porcent of examples (no homogeneous)
     */
    public FuzzyProblemClass(InstanceSet iSet, int numLabelsInputs, 
            int numLabelsOutput, int shift, int direction, int homogeneousLabel){
      
        if (homogeneousLabel == 1){
            FuzzyProblemClass(iSet, numLabelsInputs,numLabelsOutput,shift,direction);
            return;
        }
        
        int numVariable, numTerm;
        
        numVariable= iSet.getAttributeDefinitions().getNumAttributes();
        this.fuzzyLinguisticVariableNum= numVariable;
        this.fuzzyLinguisticVariableList= new FuzzyLinguisticVariableClass[numVariable];
        this.shift= shift;
        this.direction= direction;
        this.homogeneousLabel= homogeneousLabel;

        if (numLabelsInputs % 2 == 0){
            numLabelsInputs++;
        }
        
        // get data
        int numExamples= iSet.getNumInstances();
        double examplesPerLabel= numExamples / (double) (numLabelsInputs); // each label must have examplesPerLabel examples
        double[][] examples= new double[numExamples][numVariable];
        double[][] examplesTranspuesta= new double[numVariable][numExamples];

        for (int i=0; i < numExamples; i++){
            double[] valAntec= iSet.getInstance(i).getAllInputValues();
            for (int j=0; j < numVariable-1; j++){ // guardar los antecedentes
              examples[i][j]= valAntec[j];
              examplesTranspuesta[j][i]= valAntec[j];
            }            
        }

        int indexI=0, indice;
        for (int i=0; i < numVariable; i++){
                   
            String name= iSet.getAttributeDefinitions().getAttribute(i).getName();           
            int consequent= iSet.getAttributeDefinitions().getAttribute(i).getDirectionAttribute();
            
            if (consequent == 1){ // la dirección está invertida respecto a lo nuestro
              consequent=0;
            }
            else{
              consequent=1;
            }
            
            int numNominalValues= iSet.getAttributeDefinitions().getAttribute(i).getNumNominalValues();
            
            if (numNominalValues == -1){
                int unit= 0;
                double numTermAutomatic=0;
                if (consequent == 0){ // atributo de entrada
                  numTermAutomatic= numLabelsInputs;
                }              
                else{
                  numTermAutomatic= numLabelsOutput;
                }
                double infRange= iSet.getAttributeDefinitions().getAttribute(i).getMinAttribute();
                double supRange= iSet.getAttributeDefinitions().getAttribute(i).getMaxAttribute();
                numTerm= 0;

                double a, b, c, d;
                String termName;
                FuzzyLinguisticTermClass auxLingTerm;

                if (consequent == 1){
                  consequentIndexOriginal= i;
                  indice= numVariable-1;
                }
                else{
                  indice= indexI;
                  indexI++;
                }
                this.fuzzyLinguisticVariableList[indice]= new FuzzyLinguisticVariableClass(name, 
                        unit, numTermAutomatic, consequent, infRange, supRange, numTerm);

                // calc intervalLabels with examplesPerLabel
                double[] exVar= new double[numExamples];
                System.arraycopy(examplesTranspuesta[i],0,exVar,0,examplesTranspuesta[i].length);
//System.out.print("\nValores de variable "+i+" sin ordenar\n");
//for (int ii=0; ii < exVar.length; ii++){
//    System.out.print(exVar[ii]+";");
//}
                Arrays.sort(exVar);
//System.out.print("\nValores de variable "+i+" ordenado\n");
//for (int ii=0; ii < exVar.length; ii++){
//    System.out.print(exVar[ii]+";");
//}
                double[] bLabelValues= new double [numLabelsInputs];
                bLabelValues[0]= infRange;
                bLabelValues[numLabelsInputs-1]= supRange;
                for (int indExVar=1; indExVar < (numLabelsInputs-1); indExVar++){ //el valor del punto medio de cada particion me da el pico de la etiqueta
                    bLabelValues[indExVar]= (exVar[(indExVar*(int)examplesPerLabel)-1] + exVar[((indExVar+1)*(int)examplesPerLabel)-1]) / (double) 2.0;
                }
                
                // primera etiqueta
                a= b= c= bLabelValues[0];
                d= bLabelValues[1];
                termName= "S" + (int)(numTermAutomatic / 2);

                auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
                this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, 0);

                // etiquetas centrales
                for (int j=1; j < (int) (numTermAutomatic-1); j++){
                  a= bLabelValues[j-1];
                  b= c= bLabelValues[j];
                  d= bLabelValues[j+1];
                  if (j < (int) (numTermAutomatic/2)){
                    termName= "S" + (((int)numTermAutomatic/2)-j);
                  }
                  else if (j > (int) (numTermAutomatic/2)){
                    termName= "B" + ((j-(int)numTermAutomatic/2));                          
                  }
                  else{
                    termName= "CE";
                  }

                  auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
                  this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, j);                        
                }

                // última etiqueta
                a= bLabelValues[numLabelsInputs-2];
                b= c= d= bLabelValues[numLabelsInputs-1];
    // en el caso del desplazamiento no coincide los valores de la última etiqueta con el rango superior            
    //            b= c= d= supRange; 

                termName= "B" + (int)(numTermAutomatic / 2);

                auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
                this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, (int) (numTermAutomatic-1));
            }// if (numNonimalValues == -1){
            else{
                int unit= 0;
                double numTermAutomatic=0;
                numTermAutomatic= numNominalValues;

                double infRange= 0;
                double supRange= numNominalValues-1;
                numTerm= 0;

                double interval, a, b, c, d;
                String termName;
                FuzzyLinguisticTermClass auxLingTerm;

                if (consequent == 1){
                  consequentIndexOriginal= i;
                  indice= numVariable-1;
                }
                else{
                  indice= indexI;
                  indexI++;
                }
                this.fuzzyLinguisticVariableList[indice]= new FuzzyLinguisticVariableClass(name, 
                        unit, numTermAutomatic, consequent, infRange, supRange, numTerm);

                for (int j=0; j < (int) numTermAutomatic; j++){
                    a=b=c=d=j;
                    termName= iSet.getAttributeDefinitions().getAttribute(i).getNominalValue(j);

                    auxLingTerm = new FuzzyLinguisticTermClass(a,b,c,d,termName);
                    this.getFuzzyLinguisticVariableList(indice).setFuzzyLinguisticTermList(auxLingTerm, j);                    
                }
                
        
            }
        }//for (int i=0; i < numVariable; i++){
      
    }
    
    /**
    * Get the value of fuzzyLinguisticVariableNum
    * @return the value of fuzzyLinguisticVariableNum
    */
    public int getFuzzyLinguisticVariableNum ( ) {
        return fuzzyLinguisticVariableNum;
    }

    /**
    * Set the value of fuzzyLinguisticVariableNum
    * @param newVar the new value of fuzzyLinguisticVariableNum
    */
    public void setFuzzyLinguisticVariableNum ( int newVar ) {
        fuzzyLinguisticVariableNum = newVar;
    }
    
    /**
    * Get the value of fuzzyLinguisticVariableList
    * @return the value of fuzzyLinguisticVariableList
    */
    public FuzzyLinguisticVariableClass[] getFuzzyLinguisticVariableList ( ) {
        return fuzzyLinguisticVariableList;
    }

    /**
    * Set the value of fuzzyLinguisticVariableList
    * @param newVar the new value of fuzzyLinguisticVariableList
    */
    public void setFuzzyLinguisticVariableList ( FuzzyLinguisticVariableClass[] newVar ) {
        fuzzyLinguisticVariableList = newVar;
    }

    /**
    * Set the value of fuzzyLinguisticVariable at position i
    * @param newVar the new value of fuzzyLinguisticVariable at position i
    * @param i index of linguistic variable to set
    * @return -1 if no valid position, i otherwise
    */
    public int setFuzzyLinguisticVariableList ( FuzzyLinguisticVariableClass newVar, int i ) {
        if (i < fuzzyLinguisticVariableNum){
            fuzzyLinguisticVariableList[i] = newVar;
            return i;
        }        
        else{
            return -1;
        }
    }

    /**
    * Get the value of fuzzyLinguisticVariable at position i
    * @param i index of linguistic variable to get
    * @return null if no valid position, fuzzyLinguisticVariable at position i otherwise
    */
    public FuzzyLinguisticVariableClass getFuzzyLinguisticVariableList (int i) {
        if (i < fuzzyLinguisticVariableNum){
            return fuzzyLinguisticVariableList[i];
        }
        else{
            return null;
        }            
    }

    /**
    * Get the value of consequentIndexOriginal
    * @return the value of consequentIndexOriginal
    */
    public int getConsequentIndexOriginal ( ) {
        return consequentIndexOriginal;
    }

    /**
    * Set the value of consequentIndexOriginal
    * @param newVar the new value of consequentIndexOriginal
    */
    public void setConsequentIndexOriginal ( int newVar ) {
        consequentIndexOriginal = newVar;
    }

    /**
    * Get the value of Shift
    * @return the value of shift
    */
    public int getShift ( ) {
        return shift;
    }

    /**
    * Set the value of shift
    * @param newVar the new value of shift
    */
    public void setShift ( int newVar ) {
        shift = newVar;
    }

    /**
    * Get the value of direction
    * @return the value of direction
    */
    public int getDirection ( ) {
        return direction;
    }

    /**
    * Set the value of direction
    * @param newVar the new value of direction
    */
    public void setDirection ( int newVar ) {
        direction = newVar;
    }

    /**
    * Get the value of homogeneousLabel
    * @return the value of homogeneousLabel
    */
    public int getHomogeneousLabel ( ) {
        return homogeneousLabel;
    }

    /**
    * Set the value of homogeneousLabel
    * @param newVar the new value of homogeneousLabel
    */
    public void setHomogeneousLabel ( int newVar ) {
        homogeneousLabel = newVar;
    }

   
    /* ......................................................................... */
    
    /**
     * 
     * @return number of active antecedents 
     */
    public int numAntecedentVariables(){
        int numero=0;        
        for (int i=0; i < fuzzyLinguisticVariableNum; i++){
            if (fuzzyLinguisticVariableList[i].getVariableType() == 0){
                numero++;
            }
        }
        return numero;
    }
    
    /**
     * 
     * @return number of linguisticTerm (labels) of ative antecedents
     */
    public int numLinguisticTermOfAntecedentsVariables(){
        int numero=0;
        for (int i=0; i < fuzzyLinguisticVariableNum; i++){
            if (fuzzyLinguisticVariableList[i].getVariableType() == 0){
                numero+= fuzzyLinguisticVariableList[i].getFuzzyLinguisticTermNum();
            }
        }
        return numero;
    }
    
    /**
     * 
     * @return number of linguisticTerm (labels) of consequent (clases)
     */
    public int numLinguisticTermOfConsequent(){

        return fuzzyLinguisticVariableList[fuzzyLinguisticVariableNum-1].getFuzzyLinguisticTermNum();
        // este código era antes de definir que el consecuente siempre va a ser la última variable
        /*
        int numero=0;
        for (int i=0; i < fuzzyLinguisticVariableNum; i++){
            if (fuzzyLinguisticVariableList[i].getVariableType() == 1){
                
                numero+= fuzzyLinguisticVariableList[i].getFuzzyLinguisticTermNum();
            }
        }
        return numero;        
        */
    }
    
    /**
     * 
     * @return index of consequent linguistic variable; -1 if no valid consequent
     */
    public int consequentIndex(){
        for (int i=this.fuzzyLinguisticVariableNum-1; i >= 0; i--){
            if (this.fuzzyLinguisticVariableList[i].getVariableType() == 1)
                return i;
        }
        return -1;
    }
    
    /**
     * Eval the "degree of membership" of "x" to linguistic variable index "variableIndex". (anterior "adaptacion")
     * The membership function is the trampf function defined in this linguistic variable
     * RETURN THE MAX DEGREE OF MEMBERSHIP IN ALL LINGUISTIC TERM
     * @param x input to eval
     * @param variableIndex 
     * @return the MAX degree of membership of x in all linguistic term (labels), -1 if ERROR
     */
    public double maxAdaptation(double x, int variableIndex){

        if (variableIndex >= 0 && variableIndex < this.fuzzyLinguisticVariableNum){ // el dominio está creado
            return this.fuzzyLinguisticVariableList[variableIndex].maxAdaptation(x);
        }
        else{
            return -1;
        }
    }    
    
    /**
     * Eval the membership degree of a "set of x'values" to a "set of labels". 
     * There are a "umbral" to decide if check a value or not.
     * The x'values is a vector of values to check the adaptation
     * The labels are represented as a ordered vector of ceros and ones. Cero represents ausence of the label,
     * oe represents presence of the label (anterior "adaptacion")
     * Eval the MAX membership degree of x to a set of labels / maxAdaptation(x)
     * @param x input to eval
     * @param labels String of labels (1101001...) to eval the membership (0 -> no presence, 1 -> presence)
     * @param values of information measures to compare with umbral
     * @param umbral to compare with values
     * @return normalized membership degree to string of labels
     */
    public double normalizedAdaptationFromVectorToLabelsWithUmbral(double[] x, 
            int[] labels, double[] values, double umbral){        
                      
        
      int numLinguisticVar= this.getFuzzyLinguisticVariableNum();
      int start, numLabels, unos;
      double max, aux;
      int[] subLabel;
      
      start=0;
      max=1;
      for (int i=0; i < numLinguisticVar && max > 0; i++){
        numLabels= this.getFuzzyLinguisticVariableList(i).getFuzzyLinguisticTermNum();
        if (values[i] > umbral){ // la medida de información es mayor que el umbral -> sí se considera
          subLabel= Util.submatrix(labels, start, numLabels);
//          for (int j= 0; j < numLabels; j++){
            unos= Util.numUnos(subLabel);
            if (unos == 0){
              max=0;
            }
            else if (unos < subLabel.length){
              aux= this.getFuzzyLinguisticVariableList(i).normalizedAdaptationToLabels(x[i], subLabel);
              if (aux < max){
                max= aux;
              }
            }
//          }
        }
        start=start+numLabels;
      }

      return max;
    }
    
    /**
     * adjust the labels of index to get a number of examples for label < maxPorcenExamp4Label
     * @param dupLabels indicate if duplicate the number of labels of the linguistic variable
     * @param indexIset index of variable in format keel to consider
     * @param indexFuzzy index of variable in our format to consider
     * @param iSet set of instances (version Keel)
     * @param maxPorcentExamp4Label if not indicated, it calculate the number of linguistic variable with this value
     * @return 1 if no error, -1 if error -> return to original labels
     * @deprecated not used in this version
     */
    private int adjustLabels(int dupLabels, int indexIset, int indexFuzzy, 
            InstanceSet iSet, double maxPorcentExamp4Label){

      int[] numEx4Labels;
      
      int numLabels= 5;
      int salir=0;
      int max= 0;
      int indMax= 0;
      
      int consequent= iSet.getAttributeDefinitions().getAttribute(indexIset).getDirectionAttribute();
  
      while (indMax != -1){
        numLabels= this.getFuzzyLinguisticVariableList(indexFuzzy).getFuzzyLinguisticTermNum();
        numEx4Labels= getNumEx4Labels(indexIset, indexFuzzy, consequent, numLabels, iSet);
        
        salir=0;
        max= 0;
        indMax= 0;        
        while (salir == 0){
          for (int i=0; i < numLabels; i++){
            if (numEx4Labels[i] > max){
              max= numEx4Labels[i];
              indMax=i;
            }
          }      

          // probar que se desdobla bien, no se desdobla quedando en las etiquetas adyacentes 0 elementos
          if (indMax == -1 ){
            salir= 1;
            max= -1;
          }
          else if ((indMax == 0 && numEx4Labels[1] == 0) 
                  || (indMax == numLabels-1 && numEx4Labels[numLabels-2] == 0)){
            numEx4Labels[indMax]=0;
            max=0;
            indMax= -1;
          }
          else if ((indMax > 0 && indMax < numLabels-1 ) 
                   && (numEx4Labels[indMax-1] == 0 && numEx4Labels[indMax+1] == 0)) {
            numEx4Labels[indMax]=0;
            max=0;
            indMax= -1;
          }
          else{
            salir=1;
          }
        }//while (salir == 0){

        if (max <= (maxPorcentExamp4Label * iSet.getNumInstances() / 100.0)){
          indMax= -1;
        }
        
        if (changeLabels(dupLabels, indexIset, indexFuzzy, consequent, iSet, indMax) == -1){ // hay menos de 5 etiquetas
          return -1;// se vuelve a las etiquetas originales
        }

// quitar más tarde, sólo para presentarlo por pantalla    
numLabels= this.getFuzzyLinguisticVariableList(indexFuzzy).getFuzzyLinguisticTermNum();
numEx4Labels= getNumEx4Labels(indexIset, indexFuzzy, consequent, numLabels, iSet);

FuzzyLinguisticVariableClass auxLinguisticVar= this.getFuzzyLinguisticVariableList(indexFuzzy);
int linguisticTermNum= auxLinguisticVar.getFuzzyLinguisticTermNum();
String auxString="Number of linguistic terms of variable (" +
        indexFuzzy + "-" + auxLinguisticVar.getName() + ") = "+ linguisticTermNum+"\n";
auxString+="\tvariableType: " + auxLinguisticVar.getVariableType() +
        "; InfRange: " + auxLinguisticVar.getInfRange() + 
        "; InfRangeIsInfinite: " + auxLinguisticVar.getInfRangeIsInf() + 
        "; SupRange: " + auxLinguisticVar.getSupRange() +
        "; SupRangeIsInfinite: " + auxLinguisticVar.getSupRangeIsInf()+"\n";

        auxString+="\tLinguistic terms"+"\n";
        for (int j=0; j < linguisticTermNum; j++){
            FuzzyLinguisticTermClass auxLinguisticTerm= auxLinguisticVar.getFuzzyLinguisticTermList(j);
            auxString+="\t\t " + numEx4Labels[j] +" Name: " + auxLinguisticTerm.getName() +
                    "; \tvalues: a=" + (auxLinguisticTerm.getA()) +
                    "; b=" + (auxLinguisticTerm.getB()) +
                    "; c=" + (auxLinguisticTerm.getC()) +
                    "; d=" + (auxLinguisticTerm.getD()) +
                    "; a=b=-inf: " + auxLinguisticTerm.getAbInf() +
                    "; c=d=+inf: " + auxLinguisticTerm.getCdInf()+ "\n";
        }    
        
System.out.println(auxString);        
// FIN - quitar más tarde, sólo para presentarlo por pantalla    
        
      }//while (indMax != -1){
      return 1;
    }
    
    /**
     * get a vector with the number of examples in each label
     * @param indexIset index of variable in format keel to consider
     * @param indexFuzzy index of variable in our format to consider
     * @param consequent indicates if the variable is consequent
     * @param numLabels number of labels of variable
     * @param iSet set of instances (version Keel)
     * @return vector with the number of examples by label
     * @deprecated not used in this version
     */
    public int[] getNumEx4Labels(int indexIset, int indexFuzzy, int consequent, int numLabels, InstanceSet iSet){
        double valExample, valAdapt;

        int[] numEx4Labels= new int[numLabels];
  
        for (int i=0; i < numLabels; i++){
          numEx4Labels[i]= 0;
        }
      
        for (int i=0; i < iSet.getNumInstances(); i++){
          if (consequent != 1) { //es una salida
            valExample= iSet.getInstance(i).getOutputRealValues(0);
          }
          else{
            valExample= iSet.getInstance(i).getInputRealValues(indexIset);
          }
          for (int k=0; k < numLabels; k++){
            valAdapt= this.getFuzzyLinguisticVariableList(indexFuzzy).getFuzzyLinguisticTermList(k).adaptation(valExample);
            if (valAdapt >= 0.5){
              numEx4Labels[k]++;
              k= numLabels+1;
            }
          }
        }//for (int i=0; i < iSet.getNumInstances(); i++){
        
        return numEx4Labels;
    }    
    
/*    
    private int calcIndDesdoble(int indexIset, int indexFuzzy, 
            InstanceSet iSet, double indMaxPorcentExamp4Label){

      // calculo del número de ejemplos que están en cada etiqueta
      int numLabels= this.getFuzzyLinguisticVariableList(indexFuzzy).getFuzzyLinguisticTermNum();
      int[] numEx4Labels= new int[numLabels];
      double valExample, valAdapt;
            
      int consequent= iSet.getAttributeDefinitions().getAttribute(indexIset).getDirectionAttribute();

      for (int i=0; i < numLabels; i++){
        numEx4Labels[i]= 0;
      }
      
      for (int i=0; i < iSet.getNumInstances(); i++){
        if (consequent != 1) { //es una salida
          valExample= iSet.getInstance(i).getOutputRealValues(0);
        }
        else{
          valExample= iSet.getInstance(i).getInputRealValues(indexIset);
        }
        for (int k=0; k < numLabels; k++){
          valAdapt= this.getFuzzyLinguisticVariableList(indexFuzzy).getFuzzyLinguisticTermList(k).adaptation(valExample);
          if (valAdapt >= 0.5){
            numEx4Labels[k]++;
            k= numLabels+1;
          }
        }
      }
      
// quitar más tarde, sólo para presentarlo por pantalla    
FuzzyLinguisticVariableClass auxLinguisticVar= this.getFuzzyLinguisticVariableList(indexFuzzy);
int linguisticTermNum= auxLinguisticVar.getFuzzyLinguisticTermNum();
String auxString="Number of linguistic terms of variable (" +
        indexFuzzy + "-" + auxLinguisticVar.getName() + ") = "+ linguisticTermNum+"\n";
auxString+="\tvariableType: " + auxLinguisticVar.getVariableType() +
        "; InfRange: " + auxLinguisticVar.getInfRange() + 
        "; InfRangeIsInfinite: " + auxLinguisticVar.getInfRangeIsInf() + 
        "; SupRange: " + auxLinguisticVar.getSupRange() +
        "; SupRangeIsInfinite: " + auxLinguisticVar.getSupRangeIsInf()+"\n";

        auxString+="\tLinguistic terms"+"\n";
        for (int j=0; j < linguisticTermNum; j++){
            FuzzyLinguisticTermClass auxLinguisticTerm= auxLinguisticVar.getFuzzyLinguisticTermList(j);
            auxString+="\t\t " + numEx4Labels[j] +" Name: " + auxLinguisticTerm.getName() +
                    "; \tvalues: a=" + (auxLinguisticTerm.getA()) +
                    "; b=" + (auxLinguisticTerm.getB()) +
                    "; c=" + (auxLinguisticTerm.getC()) +
                    "; d=" + (auxLinguisticTerm.getD()) +
                    "; a=b=-inf: " + auxLinguisticTerm.getAbInf() +
                    "; c=d=+inf: " + auxLinguisticTerm.getCdInf()+ "\n";
        }    
        
System.out.println(auxString);        
// FIN - quitar más tarde, sólo para presentarlo por pantalla    

      int max= 0;
      int indMax= 0;
      int salir=0;
      while (salir == 0){
        for (int i=0; i < numLabels; i++){
          if (numEx4Labels[i] > max){
            max= numEx4Labels[i];
            indMax=i;
          }
        }      

        // probar que se desdobla bien, no se desdobla quedando en las etiquetas adyacentes 0 elementos
        if (indMax == -1 ){
          salir= 1;
          max= -1;
        }
        else if ((indMax == 0 && numEx4Labels[1] == 0) 
                || (indMax == numLabels-1 && numEx4Labels[numLabels-2] == 0)){
          numEx4Labels[indMax]=0;
          max=0;
          indMax= -1;
        }
        else if ((indMax > 0 && indMax < numLabels-1 ) 
                 && (numEx4Labels[indMax-1] == 0 && numEx4Labels[indMax+1] == 0)) {
          numEx4Labels[indMax]=0;
          max=0;
          indMax= -1;
        }
        else{
          salir=1;
        }
      }
      
      if (max > (indMaxPorcentExamp4Label * iSet.getNumInstances() / 100.0)){
        return indMax;
      }
      else{
        return -1;
      }      
    }
*/
    
    /**
     * change the limits of labels if is possible (indMaxPorcentExamp4Labels != -1)
     * If (indMaxPorcentExamp4Labels == -1 and dupLabels) then duplicate the number of labels
     * @param dupLabels indicate if duplicate the number of labels of the linguistic variable
     * @param indexIset index of variable in format keel to consider
     * @param indexFuzzy index of variable in our format to consider
     * @param consequent indicates if the variable is consequent
     * @param iSet set of instances (version Keel)
     * @param indMaxPorcentExamp4Label (-1: real labelsif not indicated, it calculate the number of linguistic variable with this value
     * @return 1 if no error, -1 if error -> return to original labels
     * @deprecated not used in this version
     */
    private int changeLabels(int dupLabels, int indexIset, int indexFuzzy, int consequent,
            InstanceSet iSet, int indMaxPorcentExamp4Label){
      
      int numLabels= this.getFuzzyLinguisticVariableList(indexFuzzy).getFuzzyLinguisticTermNum();

      double a, b, c, d, aa, bb, cc, dd;
      String termName;
      FuzzyLinguisticTermClass auxLingTerm0= null,auxLingTerm1= null;
      FuzzyLinguisticTermClass auxLingTerm2= null,auxLingTerm3= null;
      FuzzyLinguisticTermClass auxLingTerm4= null;
      FuzzyLinguisticVariableClass auxLingVarList;
      
      auxLingVarList= this.fuzzyLinguisticVariableList[indexFuzzy];
      
      if (indMaxPorcentExamp4Label != -1){// crear las nuevas etiquetas
        if (indMaxPorcentExamp4Label == 0){ // extremo izda
          this.fuzzyLinguisticVariableList[indexFuzzy]= new FuzzyLinguisticVariableClass(
                  auxLingVarList.getName(), auxLingVarList.getUnit(), 0,
                  auxLingVarList.getVariableType(), auxLingVarList.getInfRange(), 
                  auxLingVarList.getSupRange(), numLabels+1);

          a= b= c= auxLingVarList.getFuzzyLinguisticTermList(0).getA();
          cc= auxLingVarList.getFuzzyLinguisticTermList(0).getC();
          dd= auxLingVarList.getFuzzyLinguisticTermList(0).getD();
          d= cc + ((dd-cc)/ 2.0);
          termName= auxLingVarList.getFuzzyLinguisticTermList(0).getName() + "-VL";
          auxLingTerm0= new FuzzyLinguisticTermClass(a,b,c,d,termName);

          a= b;
          b= c= d;
          d= auxLingVarList.getFuzzyLinguisticTermList(0).getD();
          termName= auxLingVarList.getFuzzyLinguisticTermList(0).getName() + "-L";
          auxLingTerm1= new FuzzyLinguisticTermClass(a,b,c,d,termName);

          a= b;
          b= c= d;
          d= auxLingVarList.getFuzzyLinguisticTermList(1).getD();
          termName= auxLingVarList.getFuzzyLinguisticTermList(0).getName() + "-M";
          auxLingTerm2= new FuzzyLinguisticTermClass(a,b,c,d,termName);

          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm0, 0);        
          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm1, 1);        
          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm2, 2);
          for (int i=2, ii=3; i < auxLingVarList.getFuzzyLinguisticTermNum(); i++,ii++){
            this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(
                    auxLingVarList.getFuzzyLinguisticTermList(i), ii);        
          }
        }
        else if (indMaxPorcentExamp4Label == (numLabels-1)){// extremo dcha
          this.fuzzyLinguisticVariableList[indexFuzzy]= new FuzzyLinguisticVariableClass(
                  auxLingVarList.getName(), auxLingVarList.getUnit(), 0,
                  auxLingVarList.getVariableType(), auxLingVarList.getInfRange(), 
                  auxLingVarList.getSupRange(), numLabels+1);

          a= auxLingVarList.getFuzzyLinguisticTermList(numLabels-2).getA();
          aa= auxLingVarList.getFuzzyLinguisticTermList(numLabels-1).getA();
          b= c= aa;
          bb= auxLingVarList.getFuzzyLinguisticTermList(numLabels-1).getB();
          d= aa + ((bb-aa)/ 2.0);
          termName= auxLingVarList.getFuzzyLinguisticTermList(numLabels-1).getName() + "-M";
          auxLingTerm2= new FuzzyLinguisticTermClass(a,b,c,d,termName);

          a= b;
          b= c= d;
          d= auxLingVarList.getFuzzyLinguisticTermList(numLabels-1).getB();
          termName= auxLingVarList.getFuzzyLinguisticTermList(numLabels-1).getName() + "-H";
          auxLingTerm3= new FuzzyLinguisticTermClass(a,b,c,d,termName);

          a= b;
          b= c= d= auxLingVarList.getFuzzyLinguisticTermList(numLabels-1).getD();
          termName= auxLingVarList.getFuzzyLinguisticTermList(numLabels-1).getName() + "-M";
          auxLingTerm4= new FuzzyLinguisticTermClass(a,b,c,d,termName);

          for (int i=0; i < numLabels-2; i++){
            this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(
                    auxLingVarList.getFuzzyLinguisticTermList(i), i);        
          }
          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm2, numLabels-2);
          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm3, numLabels-1);
          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm4, numLabels);
        }
        else{ // etiquetas centrales
          this.fuzzyLinguisticVariableList[indexFuzzy]= new FuzzyLinguisticVariableClass(
                  auxLingVarList.getName(), auxLingVarList.getUnit(), 0,
                  auxLingVarList.getVariableType(), auxLingVarList.getInfRange(), 
                  auxLingVarList.getSupRange(), numLabels+2);

          a= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label-1).getA();
          b= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label-1).getB();
          cc= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label-1).getC();
          dd= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label-1).getD();
          c= cc;
          d= cc + ((dd - cc) / 2.0);
          termName= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label).getName() + "-VL";
          auxLingTerm0= new FuzzyLinguisticTermClass(a,b,c,d,termName);

          a= b;
          b= c= d;
          d= d + ((dd - cc) / 2.0);
          termName= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label).getName() + "-L";
          auxLingTerm1= new FuzzyLinguisticTermClass(a,b,c,d,termName);

          a= b;
          b= c= d;
          aa= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label+1).getA();
          bb= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label+1).getB();
          d= d + ((bb - aa) / 2.0);
          termName= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label).getName() + "-M";
          auxLingTerm2= new FuzzyLinguisticTermClass(a,b,c,d,termName);

          a= b;
          b= c= d;
          d= d + ((bb - aa) / 2.0);
          termName= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label).getName() + "-H";
          auxLingTerm3= new FuzzyLinguisticTermClass(a,b,c,d,termName);

          a= b;
          b= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label+1).getB();
          c= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label+1).getC();
          d= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label+1).getD();
          termName= auxLingVarList.getFuzzyLinguisticTermList(indMaxPorcentExamp4Label).getName() + "-VH";
          auxLingTerm4= new FuzzyLinguisticTermClass(a,b,c,d,termName);        

          int i=0;
          for (; i < indMaxPorcentExamp4Label-1; i++){
            this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(
                    auxLingVarList.getFuzzyLinguisticTermList(i), i);        
          }
          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm0, i++);
          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm1, i++);
          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm2, i++);
          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm3, i++);
          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm4, i++);
          for (; i < numLabels+2; i++){
            this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(
                    auxLingVarList.getFuzzyLinguisticTermList(i-2), i);        
          }
        }
      }//if (indMaxPorcentExamp4Label != -1){// crear las nuevas etiquetas
      else{ // asignar los conjuntos "reales" es decir conjuntos con más de 0 elementos
        if (assignRealLabels(indexIset, indexFuzzy,consequent,iSet, auxLingVarList) == -1){
          return -1;
        }
        
        if (dupLabels == 1){
          dupLabels(indexIset, indexFuzzy,consequent,iSet, auxLingVarList);          
        }
      }//else{ // asignar los conjuntos "reales" es decir conjuntos con más de 0 elementos
      return 1;
    }
    
    /**
     * assign original real labels to liguistic term
     * @param indexIset index of variable in format keel to consider
     * @param indexFuzzy index of variable in our format to consider
     * @param consequent indicates if the variable is consequent
     * @param iSet set of instances (version Keel)
     * @param lingTerm linguistic term (variable) to consider
     * @return 1 if no error -1 if we must return to original 5 labels
     * @deprecated not used in this version
     */
    public int assignRealLabels(int indexIset, int indexFuzzy, int consequent,
            InstanceSet iSet, FuzzyLinguisticVariableClass lingTerm){

        double a, b, c, d;
        String termName;      
      
        int numLabels= this.getFuzzyLinguisticVariableList(indexFuzzy).getFuzzyLinguisticTermNum();
        int[] numEx4Labels= getNumEx4Labels(indexIset, indexFuzzy, consequent, numLabels, iSet);
        
        int count=0;
        int i=0, j;
        for (i=0; i < numLabels; i++){
          if (numEx4Labels[i] > 0){
            count++;
          }
        }
        if (count < 5){ // hay menos de 5 etiquetas -> se crea el conjunto homogéneo de 5 etiquetas
          return -1; // con -1 indica que vuelva a las etiquetas originales
        }
        // crear el espacio para las etiquetas
        this.fuzzyLinguisticVariableList[indexFuzzy]= new FuzzyLinguisticVariableClass(
                lingTerm.getName(), lingTerm.getUnit(), 0,
                lingTerm.getVariableType(), lingTerm.getInfRange(), 
                lingTerm.getSupRange(), count);
        FuzzyLinguisticTermClass[] auxLingTerm= new FuzzyLinguisticTermClass[count];

        // crear la primera etiqueta
        i=0;
        count=0;
        while (numEx4Labels[i] == 0 && i < numLabels){
          i++;
        }
        j=i+1;
        while (numEx4Labels[j] == 0 && j < numLabels){
          j++;
        }
        a= b= c= this.getFuzzyLinguisticVariableList(indexFuzzy).getInfRange();
//        a= b= c= lingTerm.getFuzzyLinguisticTermList(i).getA();
        d= lingTerm.getFuzzyLinguisticTermList(j).getC();
        termName= "Lbl-"+count;
        auxLingTerm[count]= new FuzzyLinguisticTermClass(a,b,c,d,termName);

        //crear el resto de etiquetas
        count++;
        j++;
        i=j;
        while(i < numLabels){
          while (j < numLabels && numEx4Labels[j] == 0){
            j++;
          }
          
          if (j < numLabels){
            a= b;
            b= c= d;
            d= lingTerm.getFuzzyLinguisticTermList(j).getC();
            termName= "Lbl-"+count;
            auxLingTerm[count]= new FuzzyLinguisticTermClass(a,b,c,d,termName);
            count++;
          }
          
          j++;          
          i= j;
        }

        a= b;
//        b= c= d= this.getFuzzyLinguisticVariableList(indexFuzzy).getSupRange();
        b= c= d= lingTerm.getFuzzyLinguisticTermList(numLabels-1).getC();
        termName= "Lbl-"+count;
        auxLingTerm[count]= new FuzzyLinguisticTermClass(a,b,c,d,termName);
        count++;
        for (i=0; i < count; i++){
          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm[i], i);          
        }
        return 1;
    }

    /**
     * duplicate the number of original labels
     * @param indexIset index of variable in format keel to consider
     * @param indexFuzzy index of variable in our format to consider
     * @param consequent indicates if the variable is consequent
     * @param iSet set of instances (version Keel)
     * @param lingTerm linguistic term (variable) to consider
     * @deprecated not used in this version
     */
    public void dupLabels(int indexIset, int indexFuzzy, int consequent,
            InstanceSet iSet, FuzzyLinguisticVariableClass lingTerm){

        double a, b, c, d, aa,bb,cc,dd;
        String termName;      
      
        int numLabels= this.getFuzzyLinguisticVariableList(indexFuzzy).getFuzzyLinguisticTermNum();
        int newNumLabels= (numLabels * 2) -1; 
        
        lingTerm= this.fuzzyLinguisticVariableList[indexFuzzy];
        
        // crear el espacio para las etiquetas
        this.fuzzyLinguisticVariableList[indexFuzzy]= new FuzzyLinguisticVariableClass(
                lingTerm.getName(), lingTerm.getUnit(), 0,
                lingTerm.getVariableType(), lingTerm.getInfRange(), 
                lingTerm.getSupRange(), newNumLabels);
        FuzzyLinguisticTermClass[] auxLingTerm= new FuzzyLinguisticTermClass[newNumLabels];

        // crear la primera etiqueta
        int i=0;
        a= b= c= lingTerm.getFuzzyLinguisticTermList(0).getA();
        cc= lingTerm.getFuzzyLinguisticTermList(0).getC();
        dd= lingTerm.getFuzzyLinguisticTermList(0).getD();
        d= cc + ((dd - cc) / 2.0);
        termName= "Lb-D"+i;
        auxLingTerm[i]= new FuzzyLinguisticTermClass(a,b,c,d,termName);
        i++;

        //crear el resto de etiquetas
        for (int j=1; j < numLabels-1; j++){
          a= b;
          b= c= d;
          d= lingTerm.getFuzzyLinguisticTermList(j).getC();
          termName= "Lb-D"+i;
          auxLingTerm[i]= new FuzzyLinguisticTermClass(a,b,c,d,termName);
          i++;

          a= b;
          b= c= d;
          cc= lingTerm.getFuzzyLinguisticTermList(j).getC();
          dd= lingTerm.getFuzzyLinguisticTermList(j).getD();
          d= cc + ((dd - cc) / 2.0);
          termName= "Lb-D"+i;
          auxLingTerm[i]= new FuzzyLinguisticTermClass(a,b,c,d,termName);
          i++;

//          a= b;
//          b= c= d;
//          d= lingTerm.getFuzzyLinguisticTermList(j+1).getC();
//          termName= "Lb-D"+i;
//          auxLingTerm[i]= new FuzzyLinguisticTermClass(a,b,c,d,termName);
//          i++;          
        }
        
        // crear las dos últimas etiquetas
        a= b;
        b= c= d;
        d= lingTerm.getFuzzyLinguisticTermList(numLabels-1).getC();
        termName= "Lb-D"+i;
        auxLingTerm[i]= new FuzzyLinguisticTermClass(a,b,c,d,termName);
        i++;

        a= b;
//        b= c= d= this.getFuzzyLinguisticVariableList(indexFuzzy).getSupRange();
        b= c= d= lingTerm.getFuzzyLinguisticTermList(numLabels-1).getC();
        termName= "Lb-D"+i;
        auxLingTerm[i]= new FuzzyLinguisticTermClass(a,b,c,d,termName);
        for (i=0; i < newNumLabels; i++){
          this.getFuzzyLinguisticVariableList(indexFuzzy).setFuzzyLinguisticTermList(auxLingTerm[i], i);          
        }
    }
    
}
