package NSLVOrd;

import static NSLVOrd.NSLVOrd.E_par;
import java.io.*;
import java.text.*;
import java.util.*;

import keel.Dataset.*;
/**
 * @file DebugClass.java
 * @brief file to print messages for debug
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date diciembre 2015
 * @note Implement the debug information
 */
public class DebugClass {
    
    public static String dataFileName="";
    public static DecimalFormat format= new DecimalFormat("##0.000");
    public static DecimalFormat numFormat=  new DecimalFormat("00000000.##");
    public static String origMail="";
    public static String passOrig=""; 
    public static String destMail=""; 
    public static String fileResultDebug="";
    public static int sendMail=0;
    public static String cuerpoMail="";

    /**
     * Constructor
     */
    public DebugClass(){
      DebugClass.dataFileName= null;
      format= new DecimalFormat("##0.000");
      numFormat =  new DecimalFormat("00000000.##");
    }

    /**
     * Constructor
     * @param data dataFileName
     */
    public DebugClass(String data){
      DebugClass.dataFileName= data;
      format= new DecimalFormat("##0.000");
      numFormat =  new DecimalFormat("00000000.##");
    }
    
    /**
     * print the Files arguments
     * @param domainFile -> Domain File Name
     * @param dataFile -> Data File Name
     * @param geneticFile -> Genetic Parameters File Name
     * @param dataFileName -> DataFileName without extension
     * @param partitionNum -> Number of partitions of examples
     * @return string that contains this information
     * @deprecated not use in this version
     */
    public static String printFilesArguments(String domainFile, String dataFile, String geneticFile,
            String dataFileName, int partitionNum){
            
      String aux= "============= "+"\n";
      aux+= "DomainFile: " + domainFile+"\n";
      aux+= "DataFile: " + dataFile+"\n";
      aux+= "GeneticFile: " + geneticFile+"\n";
      aux+= "DataFileName: " + dataFileName+"\n";
      aux+= "partitionNum: " + partitionNum+"\n";
      
      return aux;
    }
    
    /**
     * Print Fuzzy Problem features
     * @param fuzzyProblem FuzzyProblemClass object
     * @param E_par set of training examples
     * @param E_par_test set of test examples
     * @return string that contains this information
     */
    public static String printFuzzyProblem(FuzzyProblemClass fuzzyProblem, 
            ExampleSetProcess E_par, ExampleSetProcess E_par_test){
        int linguisticVarNum= fuzzyProblem.getFuzzyLinguisticVariableNum();
        int linguisticTermNum;
        FuzzyLinguisticVariableClass auxLinguisticVar;
        FuzzyLinguisticTermClass auxLinguisticTerm;
        String auxString="";

        auxString+="============= "+"\n";
        auxString+="Fuzzy Problem"+"\n";
        auxString+="Number of linguistic variables: " + linguisticVarNum+"\n";
        for (int i= 0; i < linguisticVarNum; i++){
            auxLinguisticVar= fuzzyProblem.getFuzzyLinguisticVariableList(i);
            if (auxLinguisticVar == null){
                auxString+="ERROR: no valid Linguistic Variable position"+"\n";
                String aux="DebugClass.printFuzzyProblem \n\n\n"+auxString;
                DebugClass.sendMail=1;
                DebugClass.cuerpoMail+= "\n" + aux;
                DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);                
                DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
                System.exit(-1);
            }
            linguisticTermNum= auxLinguisticVar.getFuzzyLinguisticTermNum();
            auxString+="Number of linguistic terms of variable (" +
                    i + "-" + auxLinguisticVar.getName() + ") = "+ linguisticTermNum+"\n";
            auxString+="\tvariableType: " + auxLinguisticVar.getVariableType() +
                    "; InfRange: " + auxLinguisticVar.getInfRange() + 
                    "; InfRangeIsInfinite: " + auxLinguisticVar.getInfRangeIsInf() + 
                    "; SupRange: " + auxLinguisticVar.getSupRange() +
                    "; SupRangeIsInfinite: " + auxLinguisticVar.getSupRangeIsInf()+"\n";
            
            double tamVariable= auxLinguisticVar.getSupRange() - auxLinguisticVar.getInfRange();
            double numEtiquetas= linguisticTermNum;
            double tamMitadEtiqueta= (tamVariable / (numEtiquetas-1));
            double errorAsumido= tamMitadEtiqueta;
            double errorPorcent= errorAsumido * 100 / tamVariable;
            double desplPorcent= fuzzyProblem.getShift() * fuzzyProblem.getDirection();
            double despl= desplPorcent * tamMitadEtiqueta / 100.0;
//            double despl= 
//                    auxLinguisticVar.getFuzzyLinguisticTermList(auxLinguisticVar.getFuzzyLinguisticTermNum()-1).getD() -
//                    auxLinguisticVar.getSupRange();
//            double desplPorcent= despl * 100 / tamMitadEtiqueta;
            
            auxString+="\ttamaño variable: " + format.format(tamVariable);
            auxString+= "; número etiquetas: " + (int) numEtiquetas;
            if (fuzzyProblem.getHomogeneousLabel() == 1){
                auxString+= "; tamaño etiquetas: " + format.format(tamMitadEtiqueta*2);
                auxString+= "; error asumido: " + format.format(errorAsumido)+"(" + format.format(errorPorcent)+")%";
                auxString+= "; desplazamiento: " + format.format(despl)+"(" + format.format(desplPorcent) + ")%";
            }
            auxString+="\n";

            int[] exampTrain= E_par.getNumExamXLabel(i);
            int[] exampTest= E_par_test.getNumExamXLabel(i);
            
            auxString+="\tLinguistic terms"+"\n";
            for (int j=0; j < linguisticTermNum; j++){
                auxLinguisticTerm= auxLinguisticVar.getFuzzyLinguisticTermList(j);
                auxString+="\t\tName: " + auxLinguisticTerm.getName() +
                        "; \tvalues: a=" + format.format(auxLinguisticTerm.getA()) +
                        "; b=" + format.format(auxLinguisticTerm.getB()) +
                        "; c=" + format.format(auxLinguisticTerm.getC()) +
                        "; d=" + format.format(auxLinguisticTerm.getD()) +
                        "; a=b=-inf: " + auxLinguisticTerm.getAbInf() +
                        "; c=d=+inf: " + auxLinguisticTerm.getCdInf() +
                        "; exampTrain: " + exampTrain[j] + "-" + format.format(exampTrain[j]*100.0/(double)E_par.getNumExamples()) +"%"+ 
                        "; exampTest: " + exampTest[j] + "-" + format.format(exampTest[j]*100.0/(double)E_par_test.getNumExamples())+"%\n";
            }                
        }            
        return auxString;
    }
    
    /**
     * print memory usage
     * @param E ExampleSetClass object
     * @return string that contains this information
     */
    
    /**
     * print memory usage
     * @param fileName File Name where this information will be printed
     * @param cad string of beginning of information
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     */
    public static void printMemoryUsage(String fileName, String cad, int printOut, int printFile){
      
        String auxString="";
        long memory;

        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        memory = runtime.totalMemory() - runtime.freeMemory();
        
        auxString= cad;
        auxString+= "\n Total Memory: " + runtime.totalMemory() + "(bytes) " + runtime.totalMemory()/(1024*1024) + "(MB)";
        auxString+= "\n Max Memory: " + runtime.maxMemory() + "(bytes) " + runtime.maxMemory()/(1024*1024) + "(MB)";
        auxString+= "\n Free Memory: " + runtime.freeMemory() + "(bytes) " + runtime.freeMemory()/(1024*1024) + "(MB)";
        auxString+= "\n Used memory is bytes: " + memory;
        auxString+= "\n Used memory is megabytes: " + memory / (1024L * 1024L);
        auxString+= "\n Used memory is gigabytes: " + memory / (1024L * 1024L * 1024L);

        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();            
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
    }
    
    /**
     * Init  ResFileName the str argument
     * @param fileName Name of file to inittialize
     * @return -1 if error; 1 otherwise
     */
    public static int initResFile(String fileName){
        try{
            File file = new File(fileName);
            if (file.exists()) {
              file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }
    
    /**
     * Write in ResFileName the str argument
     * @param fileName name of file where the "str" string will be printed
     * @param str string for write to ResFileName
     * @return -1 if error; 1 otherwise
     */
    public static int writeResFile(String fileName, String str){
        try {
            File file = new File(fileName);
            FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(str);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }
    
    /**
     * init files of results (debug, train and test) and print head of files
     * @param configFile file of parameters of configuration (keel format)
     * @param fileResultDebug file of results for debug purpose
     * @param fileResultTrain file of trainint results
     * @param fileResultTest file of test results
     * @param numShifts num of shifts (threads) to consider
     */
    public static void initFileResultsAndPrintHeads(String configFile,
            String fileResultDebug, String fileResultTrain, String fileResultTest, 
            int numShifts){
        String auxString= "";
        
        Date date = new java.util.Date();
        SimpleDateFormat sdf=new java.text.SimpleDateFormat("EEE, d MMM yyyy - HH:mm:ss");

        auxString= "\nExperiment: " + fileResultDebug + "\n";
        auxString+= DebugClass.printParametersKeel(configFile);        
        auxString+= "\nDATE: " + sdf.format(date) + "\n";
        auxString+= "\n====================================\n";
        System.out.println(Thread.currentThread().getName()+"-"+auxString);        
        if (DebugClass.initResFile(fileResultDebug) == -1){
            System.out.println(Thread.currentThread().getName()+"-"+"ERROR: initResFile: "+ fileResultDebug );
            String aux="DebugClass.initFileResultsAndPrintHeads \n\n\n"+auxString;
            DebugClass.sendMail=1;
            DebugClass.cuerpoMail+= "\n" + aux;
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
            System.exit(-1);
        }
        if (DebugClass.writeResFile(fileResultDebug , auxString) == -1){
            System.out.println(Thread.currentThread().getName()+"-"+"ERROR: writeResFile(" + auxString + ")");
            String aux="DebugClass.initFileResultsAndPrintHeads \n\n\n"+auxString;
            DebugClass.sendMail=1;
            DebugClass.cuerpoMail+= "\n" + aux;
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
            System.exit(-1);
        }
/*
        for (int i=0; i < numShifts; i++){
          if (DebugClass.initResFile(fileResultDebug+i) == -1){
              System.out.println(Thread.currentThread().getName()+"-"+"ERROR: initResFile: "+ fileResultDebug +i);
              String aux="DebugClass.initFileResultsAndPrintHeads \n\n\n"+auxString;
              DebugClass.sendMail=1;
              DebugClass.cuerpoMail+= "\n" + aux;
              DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
              DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
              System.exit(-1);
          }
          auxString= "\nExperiment: " + fileResultDebug + "\n";
          auxString+= DebugClass.printParametersKeel(configFile);        
          auxString+= "\nDATE: " + sdf.format(date) + "\n";
          auxString+= "\n====================================\n";
          System.out.println(Thread.currentThread().getName()+"-"+auxString);        
          if (DebugClass.writeResFile(fileResultDebug+i , auxString) == -1){
              System.out.println(Thread.currentThread().getName()+"-"+"ERROR: writeResFile(" + auxString + ")");
              String aux="DebugClass.initFileResultsAndPrintHeads \n\n\n"+auxString;
              DebugClass.sendMail=1;
              DebugClass.cuerpoMail+= "\n" + aux;
              DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
              DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
              System.exit(-1);
          }
        }//for (int i=0; i < numShifts; i++){
*/        
        // inicializar ficheros de resultados para Keel
        if (DebugClass.initResFile(fileResultTrain) == -1){
            System.out.println(Thread.currentThread().getName()+"-"+"ERROR: initResFile: "+fileResultTrain);
            String aux="DebugClass.initFileResultsAndPrintHeads \n\n\n"+auxString;
            DebugClass.sendMail=1;
            DebugClass.cuerpoMail+= "\n" + aux;
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
            System.exit(-1);
        }
        if (DebugClass.initResFile(fileResultTest) == -1){
            System.out.println(Thread.currentThread().getName()+"-"+"ERROR: initResFile"+fileResultTest);
            String aux="DebugClass.initFileResultsAndPrintHeads \n\n\n"+auxString;
            DebugClass.sendMail=1;
            DebugClass.cuerpoMail+= "\n" + aux;
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
            System.exit(-1);
        }

        // inicializar ficheros de resultados para calcMetrics        
        if (DebugClass.initResFile(fileResultTrain+".4calcMetrics") == -1){
            System.out.println(Thread.currentThread().getName()+"-"+"ERROR: initResFile: "+fileResultTrain+".4calcMetrics");
            String aux="DebugClass.initFileResultsAndPrintHeads \n\n\n"+auxString;
            DebugClass.sendMail=1;
            DebugClass.cuerpoMail+= "\n" + aux;
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
            System.exit(-1);
        }
        if (DebugClass.initResFile(fileResultTest+".4calcMetrics") == -1){
            System.out.println(Thread.currentThread().getName()+"-"+"ERROR: initResFile"+fileResultTest+".4calcMetrics");
            String aux="DebugClass.initFileResultsAndPrintHeads \n\n\n"+auxString;
            DebugClass.sendMail=1;
            DebugClass.cuerpoMail+= "\n" + aux;
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
            System.exit(-1);
        }
        
        
    }
    
    /**
     * print parameters and results for model learnt
     * @param fuzzyProblem vector of Fuzzy Problems
     * @param E_par vector of set of examples for trainig
     * @param E_par_test vector of set of examples for test
     * @param R vector or rules learnt
     * @param fileResultDebug file to print
     * @param iter vector of iterations in learning (in learning mode)
     * @param time vector of times in learning (in learning mode)
     * @param numShifts num of shifts (threads) to consider
     */
    public static void printModelLearning(FuzzyProblemClass[] fuzzyProblem, 
            ExampleSetProcess[] E_par, ExampleSetProcess[] E_par_test, RuleSetClass[] R,
            String fileResultDebug, int[] iter, double[] time, int numShifts){
        String auxString="";
        auxString= "\n\n\tCONJUNTO DE EJEMPLOS (train): " + E_par[0].getNumExamples() + "";
        auxString+= "\n \tCONJUNTO DE EJEMPLOS (test) : " + E_par_test[0].getNumExamples() + "\n\n";
        DebugClass.writeResFile(fileResultDebug, auxString);
        for (int index= 0; index < numShifts; index++){
          auxString= "\n====================================\n";
          auxString+= "\n Execution: " + index+ "\n";
          DebugClass.writeResFile(fileResultDebug, auxString);
          auxString= DebugClass.printFuzzyProblem(fuzzyProblem[index], E_par[index], E_par_test[index]);
          DebugClass.writeResFile(fileResultDebug, auxString);
          DebugClass.printResumeRuleSet(fileResultDebug ,R[index],0,1,E_par[index]);
          auxString= DebugClass.printUnderstableRuleSet(R[index], fuzzyProblem[index]);
          DebugClass.writeResFile(fileResultDebug, auxString);        
//          if (Util.classDefaultRule != -1){
//              int conseqIndex= E_par[0].getProblemDefinition().consequentIndex();
//              auxString= "DEFAULT RULE: \n\tIF ... THEN "+E_par[0].getProblemDefinition().getFuzzyLinguisticVariableList(conseqIndex).getName()
//                         +" is "+E_par[0].getProblemDefinition().getFuzzyLinguisticVariableList(conseqIndex).getFuzzyLinguisticTermList(Util.classDefaultRule).getName()+"\n\n";
//              DebugClass.writeResFile(fileResultDebug, auxString);          
//          }

          int numRules= R[index].getNumRules();
          double varXRule= R[index].calcVarXRule();

          //calcular métricas para el conjunto de test
          RuleSetClass auxR = new RuleSetClass(R[index]);
          // calcular las métricas para modificar la base de reglas
          String resultado= Util.calcMetrics(auxR, E_par_test[index], fuzzyProblem[index]);

          if (!resultado.equals("")){
            System.out.println("Ha habido un error en el cálculo de las métricas");
            System.out.println(resultado);
            String aux="DebugClass.PrintModelLearning \n\n"+resultado;
            DebugClass.sendMail=1;
            DebugClass.cuerpoMail+= "\n" + aux;
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
            System.exit(-1);
          }
          // imprimir matrices de confusion
          DebugClass.writeResFile(fileResultDebug, "METRICAS DE TRAINING\n");
          DebugClass.printMetrics(fileResultDebug, R[index], 0, 1);         
          DebugClass.writeResFile(fileResultDebug, "METRICAS DE TEST\n");                  
          DebugClass.printMetrics(fileResultDebug, auxR, 0, 1);              
          
          // Imprimir valores estadísticos
          auxString= DebugClass.printStatisticalDataExample("CCR",R[index].CCR, 
                  auxR.CCR,numRules,varXRule,iter[index],time[index],fuzzyProblem[index]);            
          auxString+= DebugClass.printStatisticalDataExample("OMAE",R[index].OMAE, 
                  auxR.OMAE,numRules,varXRule,iter[index],time[index],fuzzyProblem[index]);            
          auxString+= DebugClass.printStatisticalDataExample("OMAENormalizado",R[index].OMAENormalizado, 
                  auxR.OMAENormalizado,numRules,varXRule,iter[index],time[index],fuzzyProblem[index]);            
          auxString+= DebugClass.printStatisticalDataExample("myMetric",R[index].metric, 
                  auxR.metric,numRules,varXRule,iter[index],time[index],fuzzyProblem[index]);            
          auxString+= DebugClass.printStatisticalDataExample("myMetricMedia",R[index].metricMedia, 
                  auxR.metricMedia,numRules,varXRule,iter[index],time[index],fuzzyProblem[index]);            
          DebugClass.writeResFile(fileResultDebug, auxString);
        }//for (int index= 0; index < numDesplazamientos; index++){
    }
          
    /**
     * print the informationMeasures matrix for the Example E
     * @param E exampleSet
     * @return string that contains this information
     */
    public static String printInformationMeasures(ExampleSetProcess E){

        int numVariablesAntecedentes= E.getProblemDefinition().numAntecedentVariables();
        int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();

        String auxString="";
        
        auxString+="···························"+"\n";
        auxString+="···Information Measures····"+"\n";
        auxString+="···························"+"\n";
        auxString+="Number of antecedent variables: " + numVariablesAntecedentes + "\n";
        auxString+="Number of classes: " + (numClases) + "\n";
        auxString+="\n Information Measures for classes";

        auxString+="\n         " + " \t";
        for (int i=0; i < numVariablesAntecedentes && i < 10; i++){
            auxString+= "Var" + i +"  ";
        }
        for (int i=10; i < numVariablesAntecedentes; i++){
            auxString+= "Var" + i +" ";
        }
        
        for (int j=0; j < numClases; j++){
            auxString+="\n Class " + (j) + ":\t";
            for (int i=0; i < numVariablesAntecedentes; i++){
                auxString+=format.format(E.getInformationMeasures(i, j)) + " ";
            }
        }
        auxString+="\n Media:   \t";
        for (int i=0; i < numVariablesAntecedentes; i++){
            auxString+=format.format(E.getInformationMeasures(i, numClases)) + " ";
        }           
        auxString+="\n";
        return auxString;
    }

    /**
     * print the adaptExVarLab matrix for the Example E
     * @param fileName File Name where this information will be printed
     * @param E exampleSet
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     */
    public static void printAdaptExVarLab(String fileName, ExampleSetProcess E, int printOut, int printFile){

        int numExamples= E.getNumExamples();
        int numVariables= E.getNumVariables();
        int numMayLabels= E.getIndMayLabels();

        String auxString="";
        
        auxString+="·······················"+"\n";
        auxString+="···Adaptation Table····"+"\n";
        auxString+="·······················"+"\n";
        auxString+="Number of examples: " + numExamples + "\n";
        auxString+="Number of variables: " + numVariables + "\n";
        auxString+="Mayor number of labels (in all variables): " + numMayLabels + "\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
        for (int i=0; i < numExamples; i++){
            auxString="\nExample : " + i;
            for (int j=0; j < numVariables; j++){
                auxString+="\n\tVariable; " + j;
                int numLabels= E.getProblemDefinition().getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermNum();
                auxString+=" (Num Labels: " + numLabels + ")";
                auxString+="\tValues:";
                for (int k=0; k < numLabels; k++){
                    auxString+=format.format(E.getAdaptExVarLab(i,j,k)) + " ";
                }
//                for (int k=numLabels; k < E.getIndMayLabels(); k++){
//                    auxString+=format.format(E.getAdaptExVarLab(i,j,k)) + " ";
//                }
            }
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }
        }           
        auxString= "\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
    }

    /**
     * print the adaptExVarLab matrix for the Example E
     * @param fileName File Name where this information will be printed
     * @param E exampleSet
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     */
    public static void printAdaptExVarLabCod(String fileName, ExampleSetProcess E, int printOut, int printFile){

        int numExamples= E.getNumExamples();
        int numVariables= E.getNumVariables();
        int numMayLabels= E.getIndMayLabels();

        String auxString="";
        
        auxString+="·····························"+"\n";
        auxString+="···Adaptation Table Coded····"+"\n";
        auxString+="·····························"+"\n";
        auxString+="Number of examples: " + numExamples + "\n";
        auxString+="Number of variables: " + numVariables + "\n";
        auxString+="Mayor number of labels (in all variables): " + numMayLabels + "\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
        for (int i=0; i < numExamples; i++){
            auxString="\nExample : " + i;
            for (int j=0; j < numVariables; j++){
                auxString+=" ";
                int numLabels= E.getProblemDefinition().getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermNum();
                
                for (int k=0; k < numLabels; k++){
                  if (E.getAdaptExVarLab(i,j,k) > 0){
                    auxString+="1";
                  }
                  else{
                    auxString+="0";
                  }
                }
            }
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }
        }           
        auxString= "\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
    }
    
    /**
     * Print the number of examples per each class
     * @param E exampleSet
     * @return string that contains this information
     */
    public static String printNumExamNotCoveredXClass(ExampleSetProcess E){

        String auxString="";
        int[] numExamNotCoveredXClass= E.getNumExamNotCoveredXClass();
      
        auxString+="········································"+"\n";
        auxString+="···Number of examples for each class····"+"\n";
        auxString+="········································"+"\n";
        for (int i= 0;  i < numExamNotCoveredXClass.length; i++){
            auxString+=numExamNotCoveredXClass[i] + "(" + format.format((numExamNotCoveredXClass[i]/(double)E.getNumExamples())*100)+"%) ";
        }
        auxString+="\n";
        return auxString;
    }

    /**
     * Print the number of examples per each label
     * @param E exampleSet
     * @param varIndex index of variable to consider
     * @return string that contains this information
     */
    public static String printNumExamXLabel(ExampleSetProcess E, int varIndex){

        String auxString="";
        int[] numExamXLabel= E.getNumExamXLabel(varIndex);
double max=0;
      
//        auxString+="···············································"+"\n";
//        auxString+="···Number of examples for label of variable····"+"\n";
//        auxString+="···············································"+"\n";
//        auxString+="Variable: " + E.getProblemDefinition().getFuzzyLinguisticVariableList(varIndex).getName()+ "\n";
        for (int i= 0;  i < numExamXLabel.length; i++){
            auxString+=i+":"+numExamXLabel[i] + "(" + format.format((numExamXLabel[i]/(double)E.getNumExamples())*100)+"%);  ";
//            auxString+= format.format((numExamXLabel[i]/(double)E.getNumExamples())*100)+";";
            
if (max < (numExamXLabel[i]/(double)E.getNumExamples())*100){
  max= (numExamXLabel[i]/(double)E.getNumExamples())*100;
}            
        }

//auxString+= "\n  MAX: " + format.format(max);
        
//        auxString+="\n";
        return auxString;
    }

    /**
     * Print the number of rules per each class
     * @param rulesXClass matrix of number of rules per each class
     * @return string that contains this information
     */
    public static String printNumRulesXClass(int[] rulesXClass){

        String auxString="";
      
        auxString+="········································"+"\n";
        auxString+="···Number of RULES for each class····"+"\n";
        auxString+="········································"+"\n";
        for (int i= 0;  i < rulesXClass.length; i++){
            auxString+=rulesXClass[i] + " ";
        }
        auxString+="\n";
        return auxString;
    }
    
    /**
     * Print the number of executions per each rule
     * @param numExamXRule matrix
     * @return string that contains this information
     */
    public static String printNumExecXRule(int[] numExecXRule){

        String auxString="";
      
        auxString+="········································"+"\n";
        auxString+="···Number of EXECUTIONS for each rule···"+"\n";
        auxString+="········································"+"\n";
        for (int i= 0;  i < numExecXRule.length; i++){
            auxString+=numExecXRule[i] + " ";
        }
        auxString+="\n";
        return auxString;
    }

    /**
     * Print statistical data of example set with a set of rule
     * @param String metric to consider to print
     * @param accuracy vector of accuracy of partitions
     * @param accuracyTest vector of accuracy test of partitions
     * @param numRules vector of number of rules of partitions
     * @param varXRule vector of variables per rules of partitions
     * @param iter vector of number of iterations of partitions
     * @param time vector of time of processing of partitions
     * @return string that contains this information
     */
    public static String printStatisticalDataExample(String metric, double accuracy, 
                    double accuracyTest, int numRules, 
                    double varXRule, int iter, double time, 
                    FuzzyProblemClass fuzzyProblem){

        String auxString="";
      
        auxString+="··································"+"\n";
        auxString+="···Statistical Data of RuleSet····"+"\n";
        auxString+="··································"+"\n";
        auxString+=metric + " Value of trainning= " + format.format(accuracy)+"\n";
        auxString+=metric + " Value of test= " + format.format(accuracyTest)+"\n";
        auxString+="Number of Rules= " + numRules+"\n";
        auxString+="Media of variables per rules= " + format.format(varXRule)+"\n";
        auxString+="Number of iterations= " + iter+"\n";
        auxString+="Time of process (seconds)= " + format.format(time/1000.0)+
                "; (min): " + format.format((time/1000.0)/60.0) +
                "; (hour): " + format.format(((time/1000.0)/60.0)/60.0) +"\n";
        auxString+="\n";

        double tamVariable= fuzzyProblem.getFuzzyLinguisticVariableList(fuzzyProblem.consequentIndex()).getSupRange()-
                fuzzyProblem.getFuzzyLinguisticVariableList(fuzzyProblem.consequentIndex()).getInfRange();
        double numEtiquetas= fuzzyProblem.getFuzzyLinguisticVariableList(fuzzyProblem.consequentIndex()).getFuzzyLinguisticTermNum();
        double tamMitadEtiqueta= (tamVariable / (numEtiquetas-1));
        double errorAsumido= tamMitadEtiqueta;
        double errorPorcent= errorAsumido * 100 / tamVariable;
            
//        auxString+="\n Variable Consecuente: " + fuzzyProblem.getFuzzyLinguisticVariableList(fuzzyProblem.consequentIndex()).getName() +
//                "; tamaño : " + format.format(tamVariable) + 
//                "; número etiquetas: " + (int) numEtiquetas +
//                "; tamaño etiquetas: " + format.format(tamMitadEtiqueta*2) +
//                "; error asumido: " + format.format(errorAsumido) +
//                "; porcentaje de error: " + format.format(errorPorcent)+"%\n\n";
        
        
        return auxString;
    }

    
    /**
     * print PopulationClass parameters (features)
     * @param population PopulationClass objects
     * @return string that contains this information
     */
    public static String printPopulationParameters(PopulationClass population, ExampleSetProcess E){

        String auxString="";
      
        auxString+="····························································"+"\n";
        auxString+="···Parámetros de la población para el algoritmo genético····"+"\n";
        auxString+="····························································"+"\n";
        auxString+="Number of individuals: " + population.getNumIndividuals();
        auxString+="\tNumFitness: " + population.getIndividuals(0).getNumFitness()+"\n";
        auxString+="\tNumBlocs (binary populations): " + population.getBinaryBlocs()+"\n";
        for (int i=0; i < population.getBinaryBlocs(); i++){
            auxString+="\t\tSubpopulation "+ i ;
            auxString+="  ProbInit: " + format.format(population.getProbInitBin(i));
            auxString+="  ProbCru: " + format.format(population.getProbCruBin(i));                    
            auxString+="  ProbMut: " + format.format(population.getProbMutBin(i));
            auxString+="  ProbMutValue: " + format.format(population.getProbMutBinValue(i))+"\n";
        }
        auxString+="\tNumBlocs (integer populations): " + population.getIntegerBlocs()+"\n";
        for (int i=0; i < population.getIntegerBlocs(); i++){
            auxString+="\t\tSubpopulation "+ i;
            auxString+="  ProbInit: " + format.format(population.getProbInitInt(i));                    
            auxString+="  ProbCru: " + format.format(population.getProbCruInt(i));                    
            auxString+="  ProbMut: " + format.format(population.getProbMutInt(0));
            auxString+="  ProbMutValue: " + format.format(population.getProbMutIntValue(0));
            auxString+="  Rango: " + population.getIndividuals(0).getIntegerRange(i)+"\n";
        }
        auxString+="\tNumBlocs (real populations): " + population.getRealBlocs()+"\n";
        for (int i=0; i < population.getRealBlocs(); i++){
            auxString+="\t\tSubpopulation "+ i;
            auxString+="  ProbInit: " + format.format(population.getProbInitReal(i));                                
            auxString+="  ProbCru: " + format.format(population.getProbCruReal(i));
            auxString+="  ProbMut: " + format.format(population.getProbMutReal(i));
            auxString+="  ProbMutValue: " + format.format(population.getProbMutRealValue(i));
            auxString+="  Rango: " + format.format(population.getIndividuals(0).getRealInfRange(i));
            auxString+= " - " + format.format(population.getIndividuals(0).getRealSupRange(i))+"\n";
        }

        int numExamples= E.getNumExamples();
        int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();        
        
        auxString+="\nConsecuents Adaptations: \n";
        for (int i=0; i < numClases; i++){
          for (int j=0; j < numExamples+1; j++){
            auxString+= format.format(population.getAdaptCons(i, j)) + " ";
          }
          auxString+="\n";
        }
        auxString+="\nPonderedNoConsecuents Adaptations: \n";
        for (int i=0; i < numClases; i++){
          for (int j=0; j < numExamples+1; j++){
            auxString+= format.format(population.getAdaptNoConsPond(i, j)) + " ";
          }
          auxString+="\n";
        }
        
        
        
        auxString+="\n";
        return auxString;
    }

    /**
     * print parameters of individual "index" of population
     * @param indiv object of individualClass to print values
     * @return string that contains this information
     */
    public static String printIndividualsParameters(IndividualClass indiv){

        String auxString="";
        auxString+=" Individuo ";
        auxString+="\tFitness: ";
        for (int j=0; j < indiv.getNumFitness(); j++){
            auxString+=format.format(indiv.getFitness(j)) + " ";
        }
        auxString+="\n  Binary: ";
        for (int j=0; j < indiv.getBinaryBlocs(); j++){
            auxString+="  \n|  ";
            for (int k=0; k < indiv.getSizeBinaryBlocs(j); k++){
                auxString+=indiv.getBinaryMatrix(j, k)+ " ";
            }
        }
        auxString+="\n  Integer: ";
        for (int j=0; j < indiv.getIntegerBlocs(); j++){
            auxString+="  \n|  ";
            for (int k=0; k < indiv.getSizeIntegerBlocs(j); k++){
                auxString+=indiv.getIntegerMatrix(j, k)+ " ";
            }
        }
        auxString+="\n  Real: ";
        for (int j=0; j < indiv.getRealBlocs(); j++){
            auxString+="  \n|  ";
            for (int k=0; k < indiv.getSizeRealBlocs(j); k++){
                auxString+=format.format(indiv.getRealMatrix(j, k))+ " ";
            }
        }
        auxString+="\n";
        
        return auxString;
    }
    
    /**
     * print parameters of individual "index" of population
     * @param population 
     * @return string that contains this information
     */
    
    /**
     * Print values of individual of population 
     * @param fileName File Name where this information will be printed
     * @param i index of individual of population
     * @param population population of individual
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param E exampleSet
     */
    public static void printFitnessIndividualPopulation(String fileName, int i, PopulationClass population, int printOut, int printFile, ExampleSetProcess E){

        int classR= population.getIndividuals(i).getIntegerMatrix(0,0);
        String auxString="";
//        String auxString="";

        auxString+= i;
        auxString+="\t Fitness: ";
//        for (int j=0; j < population.getIndividuals(i).getNumFitness(); j++){
//            auxString+= format.format(population.getIndividuals(i).getFitness(j))+" ; ";
//        }
        for (int j=0; j < population.getIndividuals(i).getNumFitness()+4; j++){
            auxString+= format.format(population.getIndividuals(i).getRealMatrix(2+classR, j))+" ; ";
        }
        auxString+="\t  Binary: ";
        int v=0, l=1;
        for (int k=0; k < population.getIndividuals(i).getSizeBinaryBlocs(0); k++){
            auxString+=population.getIndividuals(i).getBinaryMatrix(0, k);
            if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
              l++;
            }
            else{
              v++;
              l=1;
              auxString+= " ; ";
            }
        }
        auxString+="\t Integer: ";
        for (int j=0; j < population.getIndividuals(i).getIntegerBlocs(); j++){
            for (int k=0; k < population.getIndividuals(i).getSizeIntegerBlocs(j); k++){
                auxString+=population.getIndividuals(i).getIntegerMatrix(j, k)+ " ; ";
            }
        }
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }            

//        auxString="\t  Real: ";
//        for (int j=0; j < population.getIndividuals(i).getRealBlocs(); j++){
//            for (int k=0; k < population.getIndividuals(i).getSizeRealBlocs(j); k++){
//                auxString+=format.format(population.getIndividuals(i).getRealMatrix(j, k))+ " ";
//            }
//            auxString+=";|; ";
//        }
//        if (printOut == 1){
//            System.out.print(auxString);
//            System.out.flush();
//        }
//        if (printFile == 1){
//            writeResFile(fileName, auxString);
//        }            

        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
//        if (printFile == 1){
//            writeResFile(fileName, auxString);
//        }        
    }
    
    /**
     * Print values of a set of individual of population (extended way)
     * @param fileName File Name where this information will be printed
     * @param population population of individual
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param E exampleSet
     * @param numIndividuals number of individuals to print (-1 for all)
     */
    public static void printFitnessPopulation(String fileName, PopulationClass population, int printOut, int printFile, ExampleSetProcess E, int numIndividuals){

        String auxString="\n";
        auxString+="····························································"+"\n";
        auxString+="···Población para el algoritmo genético····"+"\n";
        auxString+="····························································"+"\n";
        auxString+="Number of individuals: " + population.getNumIndividuals()+"\n";

        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
        if (numIndividuals == -1){
          numIndividuals= population.getNumIndividuals();
        }
        for (int i=0; i < numIndividuals; i++){
            auxString="\n" + i + " " + population.getIndividuals(i).hashCode();
            auxString+=" Fitness: ";
            for (int j=0; j < population.getIndividuals(i).getNumFitness(); j++){
                auxString+= format.format(population.getIndividuals(i).getFitness(j))+" ; ";
            }
            auxString+="\t  Binary: ";
            int v=0, l=1;
            for (int k=0; k < population.getIndividuals(i).getSizeBinaryBlocs(0); k++){
                auxString+=population.getIndividuals(i).getBinaryMatrix(0, k);
                if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                  l++;
                }
                else{
                  v++;
                  l=1;
                  auxString+= " ; ";
                }
            }
            auxString+="\t Integer: ";
            for (int j=0; j < population.getIndividuals(i).getIntegerBlocs(); j++){
                for (int k=0; k < population.getIndividuals(i).getSizeIntegerBlocs(j); k++){
                    auxString+=population.getIndividuals(i).getIntegerMatrix(j, k)+ " ; ";
                }
            }
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }            
        }//for (int i=0; i < population.getNumIndividuals(); i++){
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
    }


    /**
     * Print values of a set of individual of population (coded way)
     * @param fileName File Name where this information will be printed
     * @param population population of individual
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param E exampleSet
     * @param numIndividuals number of individuals to print (-1 for all)
     */
    public static void printResumePopulation(String fileName, PopulationClass population, 
            int printOut, int printFile, ExampleSetProcess E, int numIndividuals){

        int numExamples= E.getNumExamples();
        int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();
      
        String auxString="";
        auxString+="····························································"+"\n";
        auxString+="···Población para el algoritmo genético····"+"\n";
        auxString+="····························································"+"\n";
        auxString+="Number of individuals: " + population.getNumIndividuals()+"\n";

        auxString+="\nConsecuents Adaptations: \n";
        for (int i=0; i < numClases; i++){
          for (int j=0; j < numExamples+1; j++){
            auxString+= format.format(population.getAdaptCons(i, j)) + " ";
          }
          auxString+="\n";
        }
        auxString+="\nPonderedNoConsecuents Adaptations: \n";
        for (int i=0; i < numClases; i++){
          for (int j=0; j < numExamples+1; j++){
            auxString+= format.format(population.getAdaptNoConsPond(i, j)) + " ";
          }
          auxString+="\n";
        }
        
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
        if (numIndividuals == -1){
          numIndividuals= population.getNumIndividuals();
        }
        for (int i=0; i < numIndividuals; i++){
            auxString="\n" + population.getIndividuals(i).hashCode() + " Individual: " + i;
            auxString+="\tModified: " + population.getIndividuals(i).getModified()+" ";
            auxString+="\tFitness: ";
            for (int j=0; j < population.getIndividuals(i).getNumFitness(); j++){
                auxString+= format.format(population.getIndividuals(i).getFitness(j))+" ; ";
            }
            auxString+="\n  Binary: ";
            for (int j=0; j < population.getIndividuals(i).getBinaryBlocs(); j++){
              auxString+="  \n|  ";
              int v=0, l=1;
              for (int k=0; k < population.getIndividuals(i).getSizeBinaryBlocs(j); k++){
                  auxString+=population.getIndividuals(i).getBinaryMatrix(j, k);
                  if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                    l++;
                  }
                  else{
                    v++;
                    l=1;
                    auxString+= " ";
                  }
              }
//                auxString+="  \n|  ";
            }
            auxString+="\n  Integer: ";
            for (int j=0; j < population.getIndividuals(i).getIntegerBlocs(); j++){
                auxString+="  \n|  ";
                for (int k=0; k < population.getIndividuals(i).getSizeIntegerBlocs(j); k++){
                    auxString+=population.getIndividuals(i).getIntegerMatrix(j, k)+ " ";
                }
            }
            auxString+="\n  Real: 0->informationMeasures; 1->AdaptAnt; 2...Q-1->AdaptClassB; Q+2..Q-1:fitnessClassB; Q=" +
                    E.getProblemDefinition().numLinguisticTermOfConsequent();
            for (int j=0; j < population.getIndividuals(i).getRealBlocs(); j++){
                auxString+="  \n"+j+"|  ";
                for (int k=0; k < population.getIndividuals(i).getSizeRealBlocs(j); k++){
                    auxString+=format.format(population.getIndividuals(i).getRealMatrix(j, k))+ " ";
                    if (k % 100 == 0){
                        if (printOut == 1){
                            System.out.print(auxString);
                            System.out.flush();
                        }
                        if (printFile == 1){
                            writeResFile(fileName, auxString);
                        }
                        auxString="";
                    }
                }
            }
            auxString+="\n";
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }            
        }
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
    }

    /**
     * Print values of a element (individual) of population (coded way)
     * @param fileName File Name where this information will be printed
     * @param population population of individual
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param E exampleSet
     * @param i index of individual to print
     */
    public static void printPopulationElement(String fileName, PopulationClass population, 
            int printOut, int printFile, ExampleSetProcess E, int i){

        int numClases=E.getProblemDefinition().numLinguisticTermOfConsequent();

        String auxString="";
            auxString="\n" + population.getIndividuals(i).hashCode() + " Individual: " + i;
            auxString+="\tModified: " + population.getIndividuals(i).getModified()+" ";
            auxString+="\tFitness: ";
            for (int j=0; j < population.getIndividuals(i).getNumFitness(); j++){
                auxString+= format.format(population.getIndividuals(i).getFitness(j))+" ; ";
            }
            auxString+="\n  Binary: ";
            for (int j=0; j < population.getIndividuals(i).getBinaryBlocs(); j++){
              auxString+="  \n|  ";
              int v=0, l=1;
              for (int k=0; k < population.getIndividuals(i).getSizeBinaryBlocs(j); k++){
                  auxString+=population.getIndividuals(i).getBinaryMatrix(j, k);
                  if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                    l++;
                  }
                  else{
                    v++;
                    l=1;
                    auxString+= " ";
                  }
              }
//              auxString+="  \n|  ";
            }
            auxString+="\n  Integer: ";
            for (int j=0; j < population.getIndividuals(i).getIntegerBlocs(); j++){
                auxString+="  \n|  ";
                for (int k=0; k < population.getIndividuals(i).getSizeIntegerBlocs(j); k++){
                    auxString+=population.getIndividuals(i).getIntegerMatrix(j, k)+ " ";
                }
            }
            auxString+="\n  Real: 0->informationMeasures; 1->AdaptAnt; 2...Q-1->AdaptClassB; Q+2..Q-1:fitnessClassB; Q=" +
                    E.getProblemDefinition().numLinguisticTermOfConsequent();
            for (int j=0; j < population.getIndividuals(i).getRealBlocs(); j++){
                auxString+="  \n"+j;
                if ((((j-2) % numClases) >= 0) && (((j-2) % numClases) < numClases)){
                  auxString+="-"+(j-2) % numClases;
                }
                auxString+="|\t";
                for (int k=0; k < population.getIndividuals(i).getSizeRealBlocs(j); k++){
                    auxString+=format.format(population.getIndividuals(i).getRealMatrix(j, k))+ " ";
                    if (k % 100 == 0){
                        if (printOut == 1){
                            System.out.print(auxString);
                            System.out.flush();
                        }
                        if (printFile == 1){
                            writeResFile(fileName, auxString);
                        }
                        auxString="";
                    }
                }
            }
            auxString+="\n";
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }            
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
    }

    /**
     * Print values of a element (individual) of population (coded way)
     * @param fileName File Name where this information will be printed
     * @param population population of individual
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param E exampleSet
     * @param i index of individual to print
     */
    public static void printPopulationElementFitness(String fileName, PopulationClass population, 
            int printOut, int printFile, ExampleSetProcess E, int i){

        int numClases=E.getProblemDefinition().numLinguisticTermOfConsequent();

        String auxString="";
            auxString="\n" + population.getIndividuals(i).hashCode() + " Individual: " + i;
            auxString+="\tModified: " + population.getIndividuals(i).getModified()+" ";
            auxString+="\tFitness: ";
            for (int j=0; j < population.getIndividuals(i).getNumFitness(); j++){
                auxString+= format.format(population.getIndividuals(i).getFitness(j))+" ; ";
            }
            auxString+="\n  Binary: ";
            for (int j=0; j < population.getIndividuals(i).getBinaryBlocs(); j++){
              auxString+="  \n|  ";
              int v=0, l=1;
              for (int k=0; k < population.getIndividuals(i).getSizeBinaryBlocs(j); k++){
                  auxString+=population.getIndividuals(i).getBinaryMatrix(j, k);
                  if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                    l++;
                  }
                  else{
                    v++;
                    l=1;
                    auxString+= " ";
                  }
              }
//              auxString+="  \n|  ";
            }
            auxString+="\n  Integer: ";
            for (int j=0; j < population.getIndividuals(i).getIntegerBlocs(); j++){
                auxString+="  \n|  ";
                for (int k=0; k < population.getIndividuals(i).getSizeIntegerBlocs(j); k++){
                    auxString+=population.getIndividuals(i).getIntegerMatrix(j, k)+ " ";
                }
            }
            auxString+="\n  Real: 0->informationMeasures; 1->AdaptAnt; 2...Q-1->fitnessClass'J'; Q=" +
                    E.getProblemDefinition().numLinguisticTermOfConsequent();
            for (int j=0; j < population.getIndividuals(i).getRealBlocs(); j++){
//              if ((j == 0) || (j >= (3*numClases+2))){
                auxString+="  \n"+j + "| ";
//                if ((((j-2) % numClases) >= 0) && (((j-2) % numClases) < numClases)){
//                  auxString+="-"+(j-2) % numClases;
//                }
//                auxString+="|\t";
                for (int k=0; k < population.getIndividuals(i).getSizeRealBlocs(j); k++){
                    auxString+=format.format(population.getIndividuals(i).getRealMatrix(j, k))+ " ";
                    if (k % 100 == 0){
                        if (printOut == 1){
                            System.out.print(auxString);
                            System.out.flush();
                        }
                        if (printFile == 1){
                            writeResFile(fileName, auxString);
                        }
                        auxString="";
                    }
                }
//              }//if ((j == 0) || (j >= (3*numClases+2))){
//              else{ //para el resto de valores reales se pone el del número de clases que es la suma
//                if (j == 1){
//                  auxString+="  \n"+j+"|\t";;
//                }
//                else if ((j-2) % numClases == 0){
//                  auxString+="  \n"+j+".."+(j+numClases-1)+"|\t";;
//                }
//                auxString+= format.format(population.getIndividuals(i).getRealMatrix(j, population.getIndividuals(i).getSizeRealBlocs(j)-1))+ " ";
//              }
            }
            auxString+="\n";
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }            
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
    }

    /**
     * Print values of a element (individual) of a set of neighbour (coded way)
     * @param fileName File Name where this information will be printed
     * @param population population of individual
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param E exampleSet
     * @param i index of individual to print
     */
    public static void printNeighbourElement(String fileName, IndividualClass[] vecinos, 
            int printOut, int printFile, ExampleSetProcess E, int i){

        String auxString="";
            auxString="\n" + vecinos[i].hashCode() + " Individual: " + i;
            auxString+="\tModified: " + vecinos[i].getModified()+" ";
            auxString+="\tFitness: ";
            for (int j=0; j < vecinos[i].getNumFitness(); j++){
                auxString+= format.format(vecinos[i].getFitness(j))+" ; ";
            }
            auxString+="\n  Binary: ";
            for (int j=0; j < vecinos[i].getBinaryBlocs(); j++){
              auxString+="  \n|  ";
              int v=0, l=1;
              for (int k=0; k < vecinos[i].getSizeBinaryBlocs(j); k++){
                  auxString+=vecinos[i].getBinaryMatrix(j, k);
                  if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                    l++;
                  }
                  else{
                    v++;
                    l=1;
                    auxString+= " ";
                  }
              }
            }
            auxString+="\n  Integer: ";
            for (int j=0; j < vecinos[i].getIntegerBlocs(); j++){
                auxString+="  \n|  ";
                for (int k=0; k < vecinos[i].getSizeIntegerBlocs(j); k++){
                    auxString+=vecinos[i].getIntegerMatrix(j, k)+ " ";
                }
            }
            auxString+="\n  Real: 0->informationMeasures; 1->AdaptAnt; 2...Q-1->AdaptClassB; Q+2..Q-1:fitnessClassB; Q=" +
                    E.getProblemDefinition().numLinguisticTermOfConsequent();
            for (int j=0; j < vecinos[i].getRealBlocs(); j++){
                auxString+="  \n"+j;
                if (j >= 2 && j < vecinos[i].getRealBlocs()-1){
                  auxString+="-"+(j-2);
                }
                auxString+="|\t";
                for (int k=0; k < vecinos[i].getSizeRealBlocs(j); k++){
                    auxString+=format.format(vecinos[i].getRealMatrix(j, k))+ " ";
                    if (k % 100 == 0){
                        if (printOut == 1){
                            System.out.print(auxString);
                            System.out.flush();
                        }
                        if (printFile == 1){
                            writeResFile(fileName, auxString);
                        }
                        auxString="";
                    }
                }
            }
            auxString+="\n";
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }            
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
    }

    /**
     * Print values of a population (coded way)
     * @param fileName File Name where this information will be printed
     * @param population population of individual
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param E exampleSet
     */
    public static void printResumePopulationCod(String fileName, PopulationClass population, int printOut, int printFile, ExampleSetProcess E){

        String auxString="";
        auxString+="····························································"+"\n";
        auxString+="···Población para el algoritmo genético CODEC····"+"\n";
        auxString+="····························································"+"\n";
        auxString+="Number of individuals: " + population.getNumIndividuals()+"\n";

        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
        for (int i=0; i < population.getNumIndividuals(); i++){
            auxString="\n"+ i + "\t";
            for (int j=0; j < population.getIndividuals(i).getBinaryBlocs(); j++){
              int v=0, l=1;
              for (int k=0; k < population.getIndividuals(i).getSizeBinaryBlocs(j); k++){
                  auxString+=population.getIndividuals(i).getBinaryMatrix(j, k);
                  if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                    l++;
                  }
                  else{
                    v++;
                    l=1;
                    auxString+= " ";
                  }
              }
              auxString+="\t";
            }
//            for (int j=0; j < E.getProblemDefinition().numLinguisticTermOfConsequent(); j++){
//              if (population.getIndividuals(i).getIntegerMatrix(0, 0) == j){
//                auxString+="1";
//              }
//              else{
//                auxString+="0";
//              }
//            }
            auxString+=population.getIndividuals(i).getIntegerMatrix(0,0);
            auxString+="\t";

            for (int k=0; k < population.getIndividuals(i).getSizeRealBlocs(0); k++){
                auxString+=format.format(population.getIndividuals(i).getRealMatrix(0, k));
                auxString+=" ";
            }
            auxString+="\t";
                
            for (int j=0; j < population.getIndividuals(i).getNumFitness(); j++){
                auxString+= format.format(population.getIndividuals(i).getFitness(j))+" ";
            }
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }            
        }
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
    }

    /**
     * Print values of two last individuals of population (coded way)
     * @param fileName File Name where this information will be printed
     * @param population population of individual
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param E exampleSet
     */
    public static void printResumePopulation2Last(String fileName, PopulationClass population, int printOut, int printFile, ExampleSetProcess E){

        String auxString="";
        auxString+="····························································"+"\n";
        auxString+="···Población para el algoritmo genético····"+"\n";
        auxString+="····························································"+"\n";
        auxString+="Number of individuals: " + population.getNumIndividuals()+"\n";

        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
        for (int i=population.getNumIndividuals() - 2; i < population.getNumIndividuals(); i++){
            auxString="\n" + population.getIndividuals(i).hashCode() + " Individual: " + i;
            auxString+="\tModified: " + population.getIndividuals(i).getModified()+" ";
            auxString+="\tFitness: ";
            for (int j=0; j < population.getIndividuals(i).getNumFitness(); j++){
                auxString+= format.format(population.getIndividuals(i).getFitness(j))+" ; ";
            }
            auxString+="\n  Binary: ";
            for (int j=0; j < population.getIndividuals(i).getBinaryBlocs(); j++){
              auxString+="  \n|  ";
              int v=0, l=1;
              for (int k=0; k < population.getIndividuals(i).getSizeBinaryBlocs(j); k++){
                  auxString+=population.getIndividuals(i).getBinaryMatrix(j, k);
                  if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                    l++;
                  }
                  else{
                    v++;
                    l=1;
                    auxString+= " ";
                  }
              }
//                auxString+="  \n|  ";
            }
            auxString+="\n  Integer: ";
            for (int j=0; j < population.getIndividuals(i).getIntegerBlocs(); j++){
                auxString+="  \n|  ";
                for (int k=0; k < population.getIndividuals(i).getSizeIntegerBlocs(j); k++){
                    auxString+=population.getIndividuals(i).getIntegerMatrix(j, k)+ " ";
                }
            }
            auxString+="\n  Real: ";
            for (int j=0; j < population.getIndividuals(i).getRealBlocs(); j++){
                auxString+="  \n|  ";
                for (int k=0; k < population.getIndividuals(i).getSizeRealBlocs(j); k++){
                    auxString+=format.format(population.getIndividuals(i).getRealMatrix(j, k))+ " ";
                    if (k % 100 == 0){
                        if (printOut == 1){
                            System.out.print(auxString);
                            System.out.flush();
                        }
                        if (printFile == 1){
                            writeResFile(fileName, auxString);
                        }
                        auxString="";
                    }
                }
            }
            auxString+="\n";
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }            
        }
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
    }
    
    /**
     * Print values of a set of rules (resume way)
     * @param fileName File Name where this information will be printed
     * @param R set of rules
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param E exampleSet
     */
    public static void printResumeRuleSet(String fileName, RuleSetClass R, int printOut, int printFile, 
            ExampleSetProcess E){

        int j;
        int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();
        int classR;
        String auxString="\n";

        auxString+="···················"+"\n";
        auxString+="···Set of Rules····"+"\n";
        auxString+="···················"+"\n";
        auxString+="Total Number of Rules: " + R.getNumRules();
        auxString+= "; Number of classes: " + numClases;
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
        for (int i=0; i < R.getNumRules(); i++){
          auxString="\n " + i + " Antecedent: ";
            int v=0, l=1;
            j=0;
            for (int k=0; k < R.getRules(i).getSizeBinaryBlocs(j); k++){
                auxString+=R.getRules(i).getBinaryMatrix(j, k);
                if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                  l++;
                }
                else{
                  v++;
                  l=1;
                  auxString+= " ";
                }
            }
          auxString+="\t Consequent: ";
          j=0;
              for (int k=0; k < R.getRules(i).getSizeIntegerBlocs(j); k++){
                  auxString+=R.getRules(i).getIntegerMatrix(j, k)+ " ";
              }
          auxString+="\t Fitness: ";
          classR= R.getRules(i).getIntegerMatrix(0,0);
//          j= R.getRules(i).getRealBlocs()-1;
          j= 2+classR;
          for (int k=0; k < R.getRules(i).getSizeRealBlocs(j); k++){
              auxString+= format.format(R.getRules(i).getRealMatrix(j, k))+ " ";
                if (k % 100 == 0){
                    if (printOut == 1){
                        System.out.print(auxString);
                        System.out.flush();
                    }
                    if (printFile == 1){
                        writeResFile(fileName, auxString);
                    }
                    auxString="";
                }
          }
          auxString+="\n " + i + "             ";
            v=0; l=1;
            j=1;
            for (int k=0; k < R.getRules(i).getSizeBinaryBlocs(j); k++){
                auxString+=R.getRules(i).getBinaryMatrix(j, k);
                if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                  l++;
                }
                else{
                  v++;
                  l=1;
                  auxString+= " ";
                }
            }
          if (printOut == 1){
              System.out.print(auxString);
              System.out.flush();
          }
          if (printFile == 1){
              writeResFile(fileName, auxString);
          }
        }
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
    }

    /**
     * Print a set of rules (coded way)
     * @param fileName File Name where this information will be printed
     * @param R set of rules
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param E exampleSet
     */
    public static void printRuleSet(String fileName, RuleSetClass R, int printOut, int printFile, 
            ExampleSetProcess E){
      
        String auxString="";

        auxString+="···················"+"\n";
        auxString+="···Set of Rules····"+"\n";
        auxString+="···················"+"\n";
        auxString+="Total Number of Rules: " + R.getNumRules();
        auxString+= "; Number of classes: " + E.getProblemDefinition().numLinguisticTermOfConsequent();
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
        for (int i=0; i < R.getNumRules(); i++){
          auxString="\n " + i + " Binary: ";
          for (int j=0; j < R.getRules(i).getBinaryBlocs(); j++){
              auxString+=" \n|  ";
              int v=0, l=1;
              for (int k=0; k < R.getRules(i).getSizeBinaryBlocs(j); k++){
                auxString+=R.getRules(i).getBinaryMatrix(j, k);
                if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                  l++;
                }
                else{
                  v++;
                  l=1;
                  auxString+= " ";
                }
            }
          }
//          for (int j=1; j < R.getRules(i).getBinaryBlocs(); j++){
//              auxString+=" \n|  ";
//              for (int k=0; k < R.getRules(i).getSizeBinaryBlocs(j); k++){
//                  auxString+=R.getRules(i).getBinaryMatrix(j, k)+ " ";
//              }
//          }
          if (printOut == 1){
              System.out.print(auxString);
              System.out.flush();
          }
          if (printFile == 1){
              writeResFile(fileName, auxString);
          }
          auxString="\n Integer: ";
          for (int j=0; j < R.getRules(i).getIntegerBlocs(); j++){
              auxString+=" \n|  ";
              for (int k=0; k < R.getRules(i).getSizeIntegerBlocs(j); k++){
                  auxString+=R.getRules(i).getIntegerMatrix(j, k)+ " ";
              }
          }
          if (printOut == 1){
              System.out.print(auxString);
              System.out.flush();
          }
          if (printFile == 1){
              writeResFile(fileName, auxString);
          }
          auxString="\n Real: ";
          for (int j=0; j < R.getRules(i).getRealBlocs(); j++){
              auxString+=" \n|  ";
              for (int k=0; k < R.getRules(i).getSizeRealBlocs(j); k++){
                  auxString+= format.format(R.getRules(i).getRealMatrix(j, k))+ " ";
                    if (k % 100 == 0){
                        if (printOut == 1){
                            System.out.print(auxString);
                            System.out.flush();
                        }
                        if (printFile == 1){
                            writeResFile(fileName, auxString);
                        }
                        auxString="";
                    }
              }
          }
          auxString+="\n";
          if (printOut == 1){
              System.out.print(auxString);
              System.out.flush();
          }
          if (printFile == 1){
              writeResFile(fileName, auxString);
          }
        }
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
    }
    
    /**
     * Print set of examples (resume way)
     * @param fileName File Name where this information will be printed
     * @param E exampleSet
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param covered 0 -> show all ; 1 -> show not covered (covered==0) only
     * @param numExamples number of examples to print (-1 for all)
     */
    public static void printResumeExampleSet(String fileName, ExampleSetProcess E, int printOut, 
            int printFile, int covered, int numExamples, RuleSetClass R){

        int indexRule=0;
      
        String auxString="";

        auxString+="···························"+"\n";
        auxString+="·······set of examples·····"+"\n";
        auxString+="···························"+"\n";
        auxString+="Total Number of Examples: " + E.getNumExamples()+"\n";
        auxString+="Number of variables: " + E.getNumVariables()+"\n";
        auxString+="Number of partitions: " + E.getNumPartitions()+"\n";
        
        auxString+="ExId ExIndex [";
            for (int j=0; j < E.getNumVariables(); j++){
                auxString+="D"+j+" ";
            }
        auxString+= ": (clase)]       ";
//        auxString+="Part  ";
        auxString+="Cov ";
        auxString+="indexCov   ";
        auxString+="ClasRule   ";
        auxString+="lambdaPos ";
        auxString+="lambdaNeg ";
        auxString+="posWeight ";
        auxString+="negWeight ";
        auxString+="indexLambdaPos ";
        auxString+="indexLambdaNeg ";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
        if (numExamples == -1){
          numExamples= E.getNumExamples();
        }
        for (int i=0; i < numExamples; i++){
          if (covered == 0 || 
              (covered == 1 && E.getCovered(i) == 0)){
            auxString="\n" + numFormat.format(E.getExample(i).hashCode()) + " " + i;
            auxString+=" [";
            for (int j=0; j < E.getNumVariables(); j++){
                auxString+=format.format(E.getData(i,j)) + " ";
                if (j % 100 == 0){
                    if (printOut == 1){
                        System.out.print(auxString);
                        System.out.flush();
                    }
                    if (printFile == 1){
                        writeResFile(fileName, auxString);
                    }
                    auxString="";
                }
            }
            auxString+=": ";
            int[] classes= E.getBetterTwoLabels(i, E.getProblemDefinition().consequentIndex());
            auxString+= "(" + classes[0];
//            auxString+= "-" + classes[1];
            auxString+= ")";
            auxString+="]\t";
//            int classes= E.getBetterLabel(i, E.getProblemDefinition().consequentIndex());
//            auxString+= classes + "]  ";

//            auxString+= "" + E.getPartition(i)+"  ";
            auxString+= E.getCovered(i)+" ";
            auxString+= E.getIndexRuleCovered(i)+"   ";
            indexRule= E.getIndexRuleCovered(i);
            if (indexRule == -1)
              indexRule= 0;
            auxString+= R.getRules(indexRule).getIntegerMatrix(0,0)+ " ";
            auxString+= format.format(E.getLambdaPos(i))+" ";
            auxString+= format.format(E.getLambdaNeg(i))+" ";
            auxString+= format.format(E.getPosWeight(i))+" ";
            auxString+= format.format(E.getNegWeight(i))+" ";
            auxString+= E.getIndexLambdaPos(i)+" ";
            auxString+= E.getIndexLambdaNeg(i)+" ";
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }        
          }
        }//for (int i=0; i < E.getNumExamples(); i++){
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
    }

    /**
     * Print set of examples (resume way)
     * @param fileName File Name where this information will be printed
     * @param E exampleSet
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param covered 0 -> show all ; 1 -> show not covered (covered==0) only
     * @param numExamples number of examples to print (-1 for all)
     */
    public static void printExamplesFiredRule(String fileName, ExampleSetProcess E, int printOut, 
            int printFile, int numExamples, RuleSetClass R){

        int indexRule=0, classR, sizeRealBlocs;
        double[][][] adaptExVarLab= E.getAdaptExVarLab();
      
        String auxString="";

        auxString+="···························"+"\n";
        auxString+="·······Set of Examples and Fired Rule·····"+"\n";
        auxString+="···························"+"\n";
        auxString+="Total Number of Examples: " + E.getNumExamples()+"\n";
        auxString+="Number of variables: " + E.getNumVariables()+"\n";
        auxString+="Number of partitions: " + E.getNumPartitions()+"\n";
        
        auxString+="\n Ex \t CodedExample \t Class \t covered \t FiredRule \t CodedRule \t Fitness \t n+ePondTra \t n-ePondTra \t numExPosTra \t numExNegTra \n";
        
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
        if (numExamples == -1){
          numExamples= E.getNumExamples();
        }

        for (int e=0; e < numExamples; e++){
            // print example data
            auxString="\n "+e+" \t ";
            for (int v=0; v < E.getNumVariables()-1; v++){
                for (int l=0; l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum(); l++){
                    auxString+=(int) E.getAdaptExVarLab(e,v,l);
                }
                auxString+= " ";
            }
            auxString+= " \t "+(int)E.getData(e, E.getProblemDefinition().getConsequentIndexOriginal());

            auxString+= " \t "+(int)E.getCovered(e);

            // print fired rule          
            indexRule= R.inference(E, e);
            auxString+=" \t R"+indexRule+" \t ";
            if (indexRule == -1){ // no hay nada de la inferencia. Te devuelve la regla por defecto
                indexRule= Util.classDefaultRule;
            }
            int v=0, l=1, j=0;
            for (int k=0; k < R.getRules(indexRule).getSizeBinaryBlocs(j); k++){
                auxString+=R.getRules(indexRule).getBinaryMatrix(j, k);
                if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                  l++;
                }
                else{
                  v++;
                  l=1;
                  auxString+= " ";
                }
            }
            auxString+=" \t ";
            j=0;
                for (int k=0; k < R.getRules(indexRule).getSizeIntegerBlocs(j); k++){
                    auxString+=R.getRules(indexRule).getIntegerMatrix(j, k)+ " ";
                }
            auxString+=" \t ";
            classR= R.getRules(indexRule).getIntegerMatrix(0,0);
            j= 2+classR; // 0-informationMeasures, 1-adaptations, 2...numClasses, n+ePondTra, n-ePondTra, numExPosTra, numExNegTra
            sizeRealBlocs= R.getRules(indexRule).getSizeRealBlocs(j);
            for (int k=0; k < sizeRealBlocs - 5; k++){
                auxString+= format.format(R.getRules(indexRule).getRealMatrix(j, k))+ " ";
            }

            auxString+= "\t " + (int) R.getRules(indexRule).getRealMatrix(j, sizeRealBlocs-5);
            auxString+= "\t " + (int) R.getRules(indexRule).getRealMatrix(j, sizeRealBlocs-4);
            auxString+= "\t " + (int) R.getRules(indexRule).getRealMatrix(j, sizeRealBlocs-3);
            auxString+= "\t " + (int) R.getRules(indexRule).getRealMatrix(j, sizeRealBlocs-2);
            
            if (e % 100 == 0){
                if (printOut == 1){
                    System.out.print(auxString);
                    System.out.flush();
                }
                if (printFile == 1){
                    writeResFile(fileName, auxString);
                }
                auxString="";
            }
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }
            
        }//for (int i=0; i < E.getNumExamples(); i++){
    }

    /**
     * Print set of rules and all examples with their consequent for those whose antecedent has adaptation with the rule
     * @param fileName File Name where this information will be printed
     * @param E exampleSet
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param numExamples number of examples to print (-1 for all)
     */
    public static void printRuleExamples(String fileName, ExampleSetProcess E, int printOut, 
            int printFile, int numExamples, RuleSetClass R){

        int j;
        int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();
        int classR, sizeRealBlocs;
        String auxString="\n";

        auxString+="···························"+"\n";
        auxString+="·······set of rules with examples ·····"+"\n";
        auxString+="·······consequent of examples which have adaptation·········"+"\n";
        auxString+="Total Number of Rules: " + R.getNumRules();
        auxString+= "; Number of classes: " + numClases;

        auxString+="\n R \t Antecedent \t Consequent \t Fitness \t n+ePondTra \t n-ePondTra \t numExPosTra \t numExNegTra" ;
        for (int i =0; i < numExamples; i++){
            auxString+= " \t " + i;
        }

        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }
        
        for (int i=0; i < R.getNumRules(); i++){
            // print rules
             auxString="\n "+i+"\t";

            int v=0, l=1;
            j=0;
            for (int k=0; k < R.getRules(i).getSizeBinaryBlocs(j); k++){
                auxString+=R.getRules(i).getBinaryMatrix(j, k);
                if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                  l++;
                }
                else{
                  v++;
                  l=1;
                  auxString+= " ";
                }
            }
          auxString+=" \t ";
          j=0;
              for (int k=0; k < R.getRules(i).getSizeIntegerBlocs(j); k++){
                  auxString+=R.getRules(i).getIntegerMatrix(j, k)+ " ";
              }
          auxString+=" \t ";
          classR= R.getRules(i).getIntegerMatrix(0,0);
          j= 2+classR; // 0-informationMeasures, 1-adaptations, 2...numClasses, n+ePondTra, n-ePondTra, numExPosTra, numExNegTra
          sizeRealBlocs= R.getRules(i).getSizeRealBlocs(j);
          for (int k=0; k < sizeRealBlocs - 5; k++){
              auxString+= format.format(R.getRules(i).getRealMatrix(j, k))+ " ";
                if (k % 100 == 0){
                    if (printOut == 1){
                        System.out.print(auxString);
                        System.out.flush();
                    }
                    if (printFile == 1){
                        writeResFile(fileName, auxString);
                    }
                    auxString="";
                }
          }
          
          auxString+= "\t " + (int) R.getRules(i).getRealMatrix(j, sizeRealBlocs-5);
          auxString+= "\t " + (int) R.getRules(i).getRealMatrix(j, sizeRealBlocs-4);
          auxString+= "\t " + (int) R.getRules(i).getRealMatrix(j, sizeRealBlocs-3);
          auxString+= "\t " + (int) R.getRules(i).getRealMatrix(j, sizeRealBlocs-2);
          auxString+="\t";
          if (printOut == 1){
              System.out.print(auxString);
              System.out.flush();
          }
          if (printFile == 1){
              writeResFile(fileName, auxString);
          }
          // print examples
          auxString="\t";
          j= 1; // 1-adaptations of examples
          for (int e=0; e < numExamples; e++){
            if (R.getRules(i).getRealMatrix(j, e) == 1){
                if (E.getCovered(e) == 1){
                    auxString+= " "+(int) E.getData(e, E.getProblemDefinition().getConsequentIndexOriginal()) + "\t";
                }
                else{
                    auxString+= " -"+(int) E.getData(e, E.getProblemDefinition().getConsequentIndexOriginal()) + "\t";                    
                }
            }
            else{
                auxString+= "   " + "\t";
////                auxString+= " -1" + "\t";
            }
          }
          if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
          }
          if (printFile == 1){
            writeResFile(fileName, auxString);
          }

        }
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }

/*       
        auxString= ""
        auxString+="ExId ExIndex [";
            for (int j=0; j < E.getNumVariables(); j++){
                auxString+="D"+j+" ";
            }
        auxString+= ": (clase)]       ";
//        auxString+="Part  ";
        auxString+="Cov ";
        auxString+="indexCov   ";
        auxString+="ClasRule   ";
        auxString+="lambdaPos ";
        auxString+="lambdaNeg ";
        auxString+="posWeight ";
        auxString+="negWeight ";
        auxString+="indexLambdaPos ";
        auxString+="indexLambdaNeg ";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
        if (numExamples == -1){
          numExamples= E.getNumExamples();
        }
        for (int i=0; i < numExamples; i++){
          if (covered == 0 || 
              (covered == 1 && E.getCovered(i) == 0)){
            auxString="\n" + numFormat.format(E.getExample(i).hashCode()) + " " + i;
            auxString+=" [";
            for (int j=0; j < E.getNumVariables(); j++){
                auxString+=format.format(E.getData(i,j)) + " ";
                if (j % 100 == 0){
                    if (printOut == 1){
                        System.out.print(auxString);
                        System.out.flush();
                    }
                    if (printFile == 1){
                        writeResFile(fileName, auxString);
                    }
                    auxString="";
                }
            }
            auxString+=": ";
            int[] classes= E.getBetterTwoLabels(i, E.getProblemDefinition().consequentIndex());
            auxString+= "(" + classes[0];
//            auxString+= "-" + classes[1];
            auxString+= ")";
            auxString+="]\t";
//            int classes= E.getBetterLabel(i, E.getProblemDefinition().consequentIndex());
//            auxString+= classes + "]  ";

//            auxString+= "" + E.getPartition(i)+"  ";
            auxString+= E.getCovered(i)+" ";
            auxString+= E.getIndexRuleCovered(i)+"   ";
            indexRule= E.getIndexRuleCovered(i);
            if (indexRule == -1)
              indexRule= 0;
            auxString+= R.getRules(indexRule).getIntegerMatrix(0,0)+ " ";
            auxString+= format.format(E.getLambdaPos(i))+" ";
            auxString+= format.format(E.getLambdaNeg(i))+" ";
            auxString+= format.format(E.getPosWeight(i))+" ";
            auxString+= format.format(E.getNegWeight(i))+" ";
            auxString+= E.getIndexLambdaPos(i)+" ";
            auxString+= E.getIndexLambdaNeg(i)+" ";
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }        
          }
        }//for (int i=0; i < E.getNumExamples(); i++){
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }  
*/        
    }

    /**
     * Print a element (example) set of examples (resume way)
     * @param fileName File Name where this information will be printed
     * @param E exampleSet
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param i index of element to print
     */
    public static void printExampleElement(String fileName, ExampleSetProcess E, int printOut, 
            int printFile, int i){
      
        String auxString="Example [Data : (clase)]       ";
//        auxString+="Part  ";
        auxString+="Cov ";
        auxString+="indexCov   ";
        auxString+="lambdaPos ";
        auxString+="lambdaNeg ";
        auxString+="posWeight ";
        auxString+="negWeight ";
        auxString+="indexLambdaPos ";
        auxString+="indexLambdaNeg ";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
            auxString="\n" + numFormat.format(E.getExample(i).hashCode()) + " " + i;
            auxString+=" [";
            for (int j=0; j < E.getNumVariables(); j++){
                auxString+=format.format(E.getData(i,j)) + " ";
                if (j % 100 == 0){
                    if (printOut == 1){
                        System.out.print(auxString);
                        System.out.flush();
                    }
                    if (printFile == 1){
                        writeResFile(fileName, auxString);
                    }
                    auxString="";
                }
            }
            auxString+=": ";
            int[] classes= E.getBetterTwoLabels(i, E.getProblemDefinition().consequentIndex());
            auxString+= "(" + classes[0] + ")";
            auxString+=" ]\t";
//            int classes= E.getBetterLabel(i, E.getProblemDefinition().consequentIndex());
//            auxString+= classes + "]  ";

//            auxString+= "" + E.getPartition(i)+"  ";
            auxString+= E.getCovered(i)+" ";
            auxString+= E.getIndexRuleCovered(i)+"   ";
            auxString+= format.format(E.getLambdaPos(i))+" ";
            auxString+= format.format(E.getLambdaNeg(i))+" ";
            auxString+= format.format(E.getPosWeight(i))+" ";
            auxString+= format.format(E.getNegWeight(i))+" ";
            auxString+= E.getIndexLambdaPos(i)+" ";
            auxString+= E.getIndexLambdaNeg(i)+" ";
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }        
        auxString="\n";
// ahora codificando los valores en las etiquetas
        auxString="\n" + numFormat.format(E.getExample(i).hashCode()) + " " + i;        
            auxString+=" [";           
            
    int numVariablesAntecedentes= E.getProblemDefinition().numAntecedentVariables();
    int numLabels, indexLabel;
    double adapt=0;
  
    for (int v=0; v < numVariablesAntecedentes; v++){
      numLabels= E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
        for (int l=0; l < numLabels; l++){ //recorrer la codificación binaria de las etiquetas que considera la regla para esta variable
            adapt= E.getAdaptExVarLab(i,v,l);
            if (adapt > 0){ // tiene adapt con una etiq anterior y ahora con esta -> adaptNormalizada=1
              auxString+= "1";
            }            
            else{
              auxString+="0";
            }
        }// for (int l=0; l < numLabels && adaptLabel < 1; l++){
        auxString+=" ";
    }
            
            
            
            auxString+=": ";
            int clase= E.getBetterLabel(i, E.getProblemDefinition().consequentIndex());
            auxString+= "(" + clase + ")";
            auxString+=" ]\t";
//            int classes= E.getBetterLabel(i, E.getProblemDefinition().consequentIndex());
//            auxString+= classes + "]  ";

//            auxString+= "" + E.getPartition(i)+"  ";
            auxString+= E.getCovered(i)+" ";
            auxString+= E.getIndexRuleCovered(i)+"   ";
            auxString+= format.format(E.getLambdaPos(i))+" ";
            auxString+= format.format(E.getLambdaNeg(i))+" ";
            auxString+= format.format(E.getPosWeight(i))+" ";
            auxString+= format.format(E.getNegWeight(i))+" ";
            auxString+= E.getIndexLambdaPos(i)+" ";
            auxString+= E.getIndexLambdaNeg(i)+" ";
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }        
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
    }

    /**
     * Print values of a element (individual) of population (coded way)
     * @param fileName File Name where this information will be printed
     * @param population population of individual
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     * @param E exampleSet
     * @param i index of individual to print
     */
    public static void printIndividual(String fileName, IndividualClass indiv, 
            int printOut, int printFile, ExampleSetProcess E){
        int numClases=E.getProblemDefinition().numLinguisticTermOfConsequent();

        String auxString="";
            auxString="\n" + indiv.hashCode() + " Individual: ";
            auxString+="\tModified: " + indiv.getModified()+" ";
            auxString+="\tFitness: ";
            for (int j=0; j < indiv.getNumFitness(); j++){
                auxString+= format.format(indiv.getFitness(j))+" ; ";
            }
            auxString+="\n  Binary: ";
            for (int j=0; j < indiv.getBinaryBlocs(); j++){
              auxString+="  \n|  ";
              int v=0, l=1;
              for (int k=0; k < indiv.getSizeBinaryBlocs(j); k++){
                  auxString+=indiv.getBinaryMatrix(j, k);
                  if (l < E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum()){
                    l++;
                  }
                  else{
                    v++;
                    l=1;
                    auxString+= " ";
                  }
              }
//              auxString+="  \n|  ";
            }
            auxString+="\n  Integer: ";
            for (int j=0; j < indiv.getIntegerBlocs(); j++){
                auxString+="  \n|  ";
                for (int k=0; k < indiv.getSizeIntegerBlocs(j); k++){
                    auxString+=indiv.getIntegerMatrix(j, k)+ " ";
                }
            }
            auxString+="\n  Real: 0->informationMeasures; 1->AdaptAnt; 2...Q-1->AdaptClassB; Q+2..Q-1:fitnessClassB; Q=" +
                    E.getProblemDefinition().numLinguisticTermOfConsequent();
            for (int j=0; j < indiv.getRealBlocs(); j++){
                auxString+="  \n"+j;
                if ((((j-2) % numClases) >= 0) && (((j-2) % numClases) < numClases)){
                  auxString+="-"+(j-2) % numClases;
                }
                auxString+="|\t";
                for (int k=0; k < indiv.getSizeRealBlocs(j); k++){
                    auxString+=format.format(indiv.getRealMatrix(j, k))+ " ";
                    if (k % 100 == 0){
                        if (printOut == 1){
                            System.out.print(auxString);
                            System.out.flush();
                        }
                        if (printFile == 1){
                            writeResFile(fileName, auxString);
                        }
                        auxString="";
                    }
                }
            }
            auxString+="\n";
            if (printOut == 1){
                System.out.print(auxString);
                System.out.flush();
            }
            if (printFile == 1){
                writeResFile(fileName, auxString);
            }            
        auxString="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }                
    }
    /**
     * print parameters of individual "index" of population
     * @param gen 
     * @return string that contains this information
     */
    public static String printGenetCode(GenetCodeClass gen){

        String auxString="";
        auxString+="·················"+"\n";
        auxString+="···Genet Code····"+"\n";
        auxString+="·················"+"\n";

        auxString+="\n  Binary: ";
        for (int j=0; j < gen.getBinaryBlocs(); j++){
            for (int k=0; k < gen.getSizeBinaryBlocs(j); k++){
                auxString+=gen.getBinaryMatrix(j, k)+ " ";
            }
            auxString+="  |  ";
        }
        auxString+="\n  Integer: ";
        for (int j=0; j < gen.getIntegerBlocs(); j++){
            for (int k=0; k < gen.getSizeIntegerBlocs(j); k++){
                auxString+=gen.getIntegerMatrix(j, k)+ " ";
            }
            auxString+="  |  ";
        }
        auxString+="\n  Real: ";
        for (int j=0; j < gen.getRealBlocs(); j++){
            for (int k=0; k < gen.getSizeRealBlocs(j); k++){
                auxString+=format.format(gen.getRealMatrix(j, k))+ " ";
            }
            auxString+="  |  ";
        }
        auxString+="\n";
        
        return auxString;
    }
    
    /**
     * print RuleSetClass features of understable way
     * @param R RuleSetClass object
     * @return string that contains this information
     */
    public static String printUnderstableRuleSet(RuleSetClass R, FuzzyProblemClass problem){
      
        int numLabels, numVariables, valueLabel, numClases, conseqIndex;
        int numRules= R.getNumRules();
        int tamBloc, start;
        double infMeasureClass, actInfMeasure;
        String auxString="";

        auxString+="···················"+"\n";
        auxString+="···Set of Rules····"+"\n";
        auxString+="···················"+"\n";
        auxString+="Total Number of Rules: " + numRules;
        auxString+= "; Number of classes: " + problem.numLinguisticTermOfConsequent() +"\n";
        for (int i=0; i < numRules; i++){
            auxString+= "R" + i + ": IF\n";
            numVariables= problem.getFuzzyLinguisticVariableNum();
            numClases= R.getRules(i).getSizeIntegerBlocs(0);
            conseqIndex= problem.consequentIndex();
            tamBloc= R.getRules(i).getSizeRealBlocs(0);
            infMeasureClass= R.getRules(i).getRealMatrix(0, tamBloc-1);
            start=0;
            for (int j=0; j < numVariables-1; j++){
                actInfMeasure= R.getRules(i).getRealMatrix(0, j);
                numLabels= problem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermNum();
                if ((R.getRules(i).binaryMatrix0AllToOne(start,numLabels) != 1) && // si todas las etiquetas están a 1 --> irrelevante
                   (j != conseqIndex && actInfMeasure >= infMeasureClass)){// la medida de información de la variable es >= que la de la clase
                    auxString+= "\t"+problem.getFuzzyLinguisticVariableList(j).getName();
                    auxString+= " = { ";
                    for (int k=0; k < numLabels; k++){
                        valueLabel= R.getRules(i).getBinaryMatrix(0,start+k);
                        if (valueLabel == 1){
                            auxString+= problem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermList(k).getName() + " ";
                        }
                    }
                    auxString+=" } \n";
                }
                start= start+numLabels;
            }
            auxString+= "THEN ";
            auxString+= problem.getFuzzyLinguisticVariableList(conseqIndex).getName();
            auxString+= " IS ";
            for (int j=0; j < numClases; j++){
                valueLabel= R.getRules(i).getIntegerMatrix(0,j);
                auxString+= problem.getFuzzyLinguisticVariableList(conseqIndex).getFuzzyLinguisticTermList(valueLabel).getName() + " ";
            }
        int classR= R.getRules(i).getIntegerMatrix(0,0);
        numClases= problem.numLinguisticTermOfConsequent();
        auxString+= "\n WEIGHT=" + format.format(R.getRules(i).getRealMatrix(2+classR,4));
        auxString+= " numExPos= "+ (int) R.getRules(i).getRealMatrix(2+classR, 7);
        auxString+= " numExNeg= "+ (int) R.getRules(i).getRealMatrix(2+classR, 10);
        auxString+= " numExNegPondDist= "+ (int) R.getRules(i).getRealMatrix(2+classR, 8)+ "\n";
        }
        return auxString;
    }

    /**
     * Print results prepared for plotting 
     * @param fileName File Name where this information will be printed
     * @param E_par exampleSet of training
     * @param E_par_test exampleSet of test
     * @param R set of rules
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     */
    public static void printForPlot(String fileName, ExampleSetProcess E_par, 
            ExampleSetProcess E_par_test,RuleSetClass R, int printOut, int printFile){
      
        int varCons, indexRule;
        int clase, claseInference;
        double valueRegla, valueEjemplo, valueEjemploClase;
        double infRange, supRange;
        double range, range1, range5, range10, range20;
      
        String auxString="";

        auxString+="·························"+"\n";
        auxString+="......PLOT-TRAINING......"+"\n";
        auxString+="·························"+"\n";
        auxString+= E_par.getNumExamples()+ "\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
        auxString="";
        varCons= E_par.getProblemDefinition().consequentIndex();
        infRange= E_par.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getInfRange();
        supRange= E_par.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getSupRange();
 
        range= (supRange-infRange);
        range1= range/(double)100;
        range5= range*5/(double)100;
        range10= range*10/(double)100;
        range20= range*20/(double)100;
        
        for (int i=0; i < E_par.getNumExamples(); i++){
          indexRule= R.inference(E_par,i);      
          if (indexRule != -1){ // hay adaptación del ejemplo con alguna regla
            claseInference= R.getRules(indexRule).getIntegerMatrix(0,0);
            valueRegla= Util.getCentralValue(claseInference, varCons, E_par);
          }
          else{
String aux= "\n No ha resuelto nada la inferencia - indexRule="+ indexRule + "\n";
//DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
DebugClass.sendMail=1;
DebugClass.cuerpoMail+= "\n" + aux;
            claseInference= -1;
            valueRegla= -1;
          }
          clase= E_par.getBetterLabel(i, varCons);
          valueEjemploClase= Util.getCentralValue(clase, varCons, E_par);
          valueEjemplo= E_par.getData(i, varCons);
          
          auxString+=i+" "+format.format(valueEjemplo);
          auxString+=" "+format.format(valueEjemploClase);
          auxString+=" "+format.format(valueRegla);
          auxString+=" "+format.format(valueEjemplo-range1);
          auxString+=" "+format.format(valueEjemplo+range1);
          auxString+=" "+format.format(valueEjemplo-range5);
          auxString+=" "+format.format(valueEjemplo+range5);
          auxString+=" "+format.format(valueEjemplo-range10);
          auxString+=" "+format.format(valueEjemplo+range10);
          auxString+=" "+format.format(valueEjemplo-range20);
          auxString+=" "+format.format(valueEjemplo+range20);
          auxString+= "\n";
          auxString= auxString.replaceAll(",",".");
          if (i % 1000 == 0){
              if (printOut == 1){
                  System.out.print(auxString);
                  System.out.flush();
              }
              if (printFile == 1){
                  writeResFile(fileName, auxString);
              }
              auxString="";
          }
        }//for (int i=0; i < E.getNumExamples(); i++){
        auxString+="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
        auxString="";

              
        auxString+="·························"+"\n";
        auxString+=".......PLOT-TEST........."+"\n";
        auxString+="·························"+"\n";
        auxString+= E_par_test.getNumExamples()+ "\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
        auxString="";
        varCons= E_par_test.getProblemDefinition().consequentIndex();
        for (int i=0; i < E_par_test.getNumExamples(); i++){
          indexRule= R.inference(E_par_test,i);      
          if (indexRule != -1){ // hay adaptación del ejemplo con alguna regla
            claseInference= R.getRules(indexRule).getIntegerMatrix(0,0);
            valueRegla= Util.getCentralValue(claseInference, varCons, E_par_test);
          }
          else{
            claseInference= -1;
            valueRegla= -1;
          }
          clase= E_par_test.getBetterLabel(i, varCons);
          valueEjemploClase= Util.getCentralValue(clase, varCons, E_par_test);
          valueEjemplo = E_par_test.getData(i, varCons);
          
          auxString+=i+" "+format.format(valueEjemplo);
          auxString+=" "+format.format(valueEjemploClase);
          auxString+=" "+format.format(valueRegla);
          auxString+=" "+format.format(valueEjemplo-range1);
          auxString+=" "+format.format(valueEjemplo+range1);
          auxString+=" "+format.format(valueEjemplo-range5);
          auxString+=" "+format.format(valueEjemplo+range5);
          auxString+=" "+format.format(valueEjemplo-range10);
          auxString+=" "+format.format(valueEjemplo+range10);
          auxString+=" "+format.format(valueEjemplo-range20);
          auxString+=" "+format.format(valueEjemplo+range20);
          auxString+= "\n";
          auxString= auxString.replaceAll(",",".");
          if (i % 1000 == 0){
              if (printOut == 1){
                  System.out.print(auxString);
                  System.out.flush();
              }
              if (printFile == 1){
                  writeResFile(fileName, auxString);
              }
              auxString="";
          }
        }//for (int i=0; i < E.getNumExamples(); i++){
        auxString+="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        

    }

    /**
     * Print results prepared for calculate Metrics
     * @param fileName File Name where this information will be printed
     * @param E_par exampleSet of training
     * @param R set of rules
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     */
    public static void printForCalcMetrics(int clasificacion, String fileName, ExampleSetProcess E_par, 
            RuleSetClass R, int printOut, int printFile){
      
        int indexRule;
        int clase, claseInference;
        double valueRegla, valueEjemplo, valueEjemploClase;
        double infRange, supRange;
        int varCons= E_par.getProblemDefinition().consequentIndex();
        int numClases= E_par.getProblemDefinition().numLinguisticTermOfConsequent();
        
//        infRange= E_par.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getInfRange();
//        supRange= E_par.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getSupRange();
      
        String auxString="";

        auxString+="#numberOfClasses \n"+
                "#RegressionValues (if RegressionValues == 0 -> there aren't definitions of class, directly with @data) \n"+
                "#Class1 inittial \n"+
                "#Class2 inittial \n"+
                "#...\n"+
                "#ClassN inittial final \n"+
                "#@data \n"+
                "#RealValue PredictedValue (each example in one row) \n";
        if (clasificacion == 1){ // se trata originalmente de una clasificación
          auxString+= numClases + "\n"+
                  "0\n"+
                  "@data\n";
          
        }
        else{ // se trata originalmente de una regresión
          double a,bi,bii,d;
          a= E_par.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(0).getA();
          auxString+= numClases + "\n"+
                  "1\n";
          auxString+= format.format(a) + "\n";           
          for (int i= 0; i < numClases-2; i++){
            bi= E_par.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(i).getB();
            bii= E_par.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(i+1).getB();
            auxString+= format.format((bi+bii)/2.0) + "\n";           
          }
          bi= E_par.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(numClases-2).getB();
          bii= E_par.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(numClases-1).getB();
          d= E_par.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(numClases-1).getD();
          auxString+= format.format((bi+bii)/2.0) + " " + format.format(d) + "\n";           
          auxString+= "@data\n";

          auxString= auxString.replaceAll(",",".");
          
        }
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        
        auxString="";
 
        for (int i=0; i < E_par.getNumExamples(); i++){
          indexRule= R.inference(E_par,i);      
          if (indexRule != -1){ // hay adaptación del ejemplo con alguna regla
            claseInference= R.getRules(indexRule).getIntegerMatrix(0,0);
            valueRegla= Util.getCentralValue(claseInference, varCons, E_par);
          }
          else{
            // no ha acertado -> se toma la clase central
String aux= "\n No ha resuelto nada la inferencia - indexRule="+ indexRule + "\n";
//DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
DebugClass.sendMail=1;
DebugClass.cuerpoMail+= "\n" + aux;
            claseInference= -1; 
            valueRegla= Util.getCentralValue(E_par.getProblemDefinition().numLinguisticTermOfConsequent()/2, varCons, E_par);
//            valueRegla= (infRange + supRange) / 2.0;
          
          }

          clase= E_par.getBetterLabel(i, varCons);
          valueEjemploClase= Util.getCentralValue(clase, varCons, E_par);
          valueEjemplo= E_par.getData(i, varCons);
          
          auxString+=format.format(valueEjemplo);
//          auxString+=" "+format.format(valueEjemploClase);
          auxString+=" "+format.format(valueRegla);
          auxString+="\n";
          auxString= auxString.replaceAll(",",".");
          if (i % 1000 == 0){
              if (printOut == 1){
                  System.out.print(auxString);
                  System.out.flush();
              }
              if (printFile == 1){
                  writeResFile(fileName, auxString);
              }
              auxString="";
          }
        }//for (int i=0; i < E.getNumExamples(); i++){
        auxString+="\n";
        if (printOut == 1){
            System.out.print(auxString);
            System.out.flush();
        }
        if (printFile == 1){
            writeResFile(fileName, auxString);
        }        

    }

    public static double[] printForCalcMetricsTFG(ExampleSetProcess E_par,RuleSetClass R){
      
        int indexRule;
        int claseInference;
        double valueRegla;
        int varCons= E_par.getProblemDefinition().consequentIndex();
        int numExamples= E_par.getNumExamples();
        
        double[] Resultados = new double[numExamples];
        
        String auxString;
 
        for (int i=0; i < numExamples; i++){
            indexRule= R.inference(E_par,i);      
            if (indexRule != -1){ // hay adaptación del ejemplo con alguna regla
                claseInference= R.getRules(indexRule).getIntegerMatrix(0,0);
                valueRegla= Util.getCentralValue(claseInference, varCons, E_par);
            }else{
                return null;
            }

            auxString= format.format(valueRegla);
            auxString= auxString.replaceAll(",",".");
            Resultados[i]= Double.parseDouble(auxString);
        }//for (int i=0; i < E.getNumExamples(); i++){        
        return Resultados;
    }
    
    /**
     * Print results prepared for Keel software
     * @param fileName File Name where this information will be printed
     * @param E_par exampleSet of training
     * @param R set of rules
     * @param numShifts number of shifts in labels or data base of training
     * @return RMSE
     * @use calcValueRegla
     */
    public static double printForKeel(String fileName, ExampleSetProcess[] E_par, 
            RuleSetClass[] R, int numShifts){

      String auxString="";
      int resInt;
      int varCons= E_par[0].getProblemDefinition().consequentIndex();
      int indexRule=0;
      int claseInference;
      int numClases= E_par[0].getProblemDefinition().numLinguisticTermOfConsequent();
      double[] valueRegla;
      int[] indRegla;
      double[] pesoRegla, inf, sup, difInf, difSup, difInfSup;
      double[] A,B,C,D;
      double valueEjemplo, valueReglaCombinado;
      double RMSE=0;

      resInt= initResFile(fileName);
      if (resInt == -1){
        return resInt;
      }

      auxString= "@relation " + Attributes.getRelationName() + "\n";
      auxString+= Attributes.getInputAttributesHeader();
      auxString+= Attributes.getOutputAttributesHeader();
      auxString+= Attributes.getInputHeader() + "\n";
      auxString+= Attributes.getOutputHeader() + "\n";
      auxString+= "@data\n";      
      
      resInt= writeResFile(fileName, auxString);
      if (resInt == -1){
        return resInt;
      }

      auxString="";
      valueRegla= new double[numShifts];
      inf= new double[numShifts];
      sup= new double[numShifts];
      difInf= new double[numShifts];
      difSup= new double[numShifts];
      difInfSup= new double[numShifts];
      indRegla= new int[numShifts];
      pesoRegla= new double[numShifts];
      A= new double[numShifts];
      B= new double[numShifts];
      C= new double[numShifts];
      D= new double[numShifts];

      for (int i=0; i < E_par[0].getNumExamples(); i++){
        for (int d=0; d < numShifts; d++){
          indexRule= R[d].inference(E_par[d],i);      
          indRegla[d]= indexRule;
        }//for (int d=0; d < numShifts; d++){  
        valueEjemplo= E_par[0].getData(i, varCons);
        claseInference= R[0].getRules(indexRule).getIntegerMatrix(0,0);        
        valueReglaCombinado= Util.getCentralValue(claseInference, varCons, E_par[0]);
//        valueReglaCombinado= calcValueRegla(varCons, R, indRegla, numShifts); // preparado para cuando se hagan los desplazamientos
//System.out.println("i: " + i + "; Ejemplo: " + valueEjemplo+ "; Regla: "+valueReglaCombinado);
        RMSE+= Math.pow((valueEjemplo - valueReglaCombinado),2);
      
//        auxString+=format.format(valueEjemplo);
        auxString+= E_par[0].getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList((int)valueEjemplo).getName();
//        auxString+=" "+format.format(valueReglaCombinado);
        auxString+=" "+E_par[0].getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList((int)valueReglaCombinado).getName();;
        auxString+= "\n";
        if (i % 1000 == 0){
          auxString= auxString.replaceAll(",",".");
//          auxString= auxString.replaceAll(" ",",");
          resInt= writeResFile(fileName, auxString);
          if (resInt == -1){
            return resInt;
          }
          auxString="";
        }//if (i % 1000 == 0){        
      }//for (int i=0; i < E.getNumExamples(); i++){
      auxString= auxString.replaceAll(",",".");
//      auxString= auxString.replaceAll(" ",",");
//      auxString+="\n";
      resInt= writeResFile(fileName, auxString);
      if (resInt == -1){
        return resInt;
      }
      
      
/* Esta parte se sustituye por la anterior porque el formato de la salida debe ser el mismo que el del problema      
      for (int i=0; i < E_par[0].getNumExamples(); i++){
        for (int d=0; d < numShifts; d++){
          indexRule= R[d].inference(E_par[d],i);      
          indRegla[d]= indexRule;
        }//for (int d=0; d < numShifts; d++){  
        valueEjemplo= E_par[0].getData(i, varCons);
        claseInference= R[0].getRules(indexRule).getIntegerMatrix(0,0);        
        valueReglaCombinado= Util.getCentralValue(claseInference, varCons, E_par[0]);
//        valueReglaCombinado= calcValueRegla(varCons, R, indRegla, numShifts); // preparado para cuando se hagan los desplazamientos
//System.out.println("i: " + i + "; Ejemplo: " + valueEjemplo+ "; Regla: "+valueReglaCombinado);
        RMSE+= Math.pow((valueEjemplo - valueReglaCombinado),2);
      
        auxString+=format.format(valueEjemplo);
        auxString+=" "+format.format(valueReglaCombinado);
        auxString+= "\n";
        if (i % 1000 == 0){
          auxString= auxString.replaceAll(",",".");
//          auxString= auxString.replaceAll(" ",",");
          resInt= writeResFile(fileName, auxString);
          if (resInt == -1){
            return resInt;
          }
          auxString="";
        }//if (i % 1000 == 0){        
      }//for (int i=0; i < E.getNumExamples(); i++){
      auxString= auxString.replaceAll(",",".");
//      auxString= auxString.replaceAll(" ",",");
//      auxString+="\n";
      resInt= writeResFile(fileName, auxString);
      if (resInt == -1){
        return resInt;
      }
*/      
      RMSE= RMSE / (double) E_par[0].getNumExamples();
      RMSE= Math.sqrt(RMSE);            
      
      return RMSE;
    }

    public static String[] printForKeelTFG(ExampleSetProcess[] E_par, 
            RuleSetClass[] R, int numShifts){

      String auxString="";
      int resInt;
      int varCons= E_par[0].getProblemDefinition().consequentIndex();
      int indexRule=0;
      int claseInference;
      int numClases= E_par[0].getProblemDefinition().numLinguisticTermOfConsequent();
      double[] valueRegla;
      int[] indRegla;
      double[] pesoRegla, inf, sup, difInf, difSup, difInfSup;
      double[] A,B,C,D;
      double valueEjemplo, valueReglaCombinado;
      double RMSE=0;
      

      auxString="";
      valueRegla= new double[numShifts];
      inf= new double[numShifts];
      sup= new double[numShifts];
      difInf= new double[numShifts];
      difSup= new double[numShifts];
      difInfSup= new double[numShifts];
      indRegla= new int[numShifts];
      pesoRegla= new double[numShifts];
      A= new double[numShifts];
      B= new double[numShifts];
      C= new double[numShifts];
      D= new double[numShifts];

      String[] _Resultado = new String[E_par[0].getNumExamples()];
      
      for (int i=0; i < E_par[0].getNumExamples(); i++){
        for (int d=0; d < numShifts; d++){
          indexRule= R[d].inference(E_par[d],i);      
          indRegla[d]= indexRule;
        }//for (int d=0; d < numShifts; d++){  
        valueEjemplo= E_par[0].getData(i, varCons);
        claseInference= R[0].getRules(indexRule).getIntegerMatrix(0,0);        
        valueReglaCombinado= Util.getCentralValue(claseInference, varCons, E_par[0]);
        RMSE+= Math.pow((valueEjemplo - valueReglaCombinado),2);
      
        auxString+= E_par[0].getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList((int)valueEjemplo).getName();;
        auxString+=" "+E_par[0].getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList((int)valueReglaCombinado).getName();;
        _Resultado[i] = E_par[0].getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList((int)valueReglaCombinado).getName();
        auxString+= "\n";
        if (i % 1000 == 0){
        }//if (i % 1000 == 0){        
      }//for (int i=0; i < E.getNumExamples(); i++){
       
      RMSE= RMSE / (double) E_par[0].getNumExamples();
      RMSE= Math.sqrt(RMSE);            
      
      return _Resultado;
    }

    
/*............................................................................*/    

    /**
     * Calculate value combined of a set rule with shifts
     * @param varCons index of consequent variable
     * @param R matrix of set of rules
     * @param indRegla matrix of index of rule to consider
     * @param numShifts number of shifts in labels or data base of training
     * @return value combined of a set of rules with shifts
     * @use calcParamRectasyCorte
     */
    private static double calcValueRegla(int varCons, RuleSetClass[] R, 
            int[] indRegla, int numShifts, FuzzyProblemClass[] problem){
      
      int indexRule, claseInference;
      double valueReglaCombinado=0;
      int numClases= problem[0].getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermNum();
      
      double[] valueRegla;
      double[] pesoRegla, inf, sup, difInf, difSup, difInfSup;
      double[] A,B,C,D;
      double[] pendiente, desplazamiento;
      double[] xCorte, yCorte;
      double[] pend, desp;

      valueRegla= new double[numShifts];
      inf= new double[numShifts];
      sup= new double[numShifts];
      difInf= new double[numShifts];
      difSup= new double[numShifts];
      difInfSup= new double[numShifts];
      pesoRegla= new double[numShifts];
      A= new double[numShifts];
      B= new double[numShifts];
      C= new double[numShifts];
      D= new double[numShifts];
      
      xCorte= new double[3]; //0->corte et0 x et1; 1->corte et1 x et2; 2->corte et2 x et0
      yCorte= new double[3];
      pend= new double[6];
      desp= new double[6];
      
        for (int d=0; d < numShifts; d++){
          indexRule= indRegla[d];
          if (indexRule != -1){ // hay adaptación del ejemplo con alguna regla
            claseInference= R[d].getRules(indexRule).getIntegerMatrix(0,0);
            inf[d]= problem[d].getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(claseInference).getA();
            sup[d]= problem[d].getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(claseInference).getD();
            A[d]= problem[d].getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(claseInference).getA();
            B[d]= problem[d].getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(claseInference).getB();
            C[d]= problem[d].getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(claseInference).getC();
            D[d]= problem[d].getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(claseInference).getD();
            valueRegla[d]= (C[d] + B[d]) / 2.0;
            pesoRegla[d]= R[d].getRules(indexRule).getRealMatrix((3*numClases)+2+claseInference,4);
            difInf[d]= valueRegla[d] - inf[d];
            difSup[d]= sup[d] - valueRegla[d];
            difInfSup[d]= sup[d] - inf[d];
          }
          else{
String aux= "\n No ha resuelto nada la inferencia - indexRule="+ indexRule + "\n";
//DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
DebugClass.sendMail=1;
DebugClass.cuerpoMail+= "\n" + aux;
            inf[d]= problem[d].getFuzzyLinguisticVariableList(varCons).getInfRange();
            sup[d]= problem[d].getFuzzyLinguisticVariableList(varCons).getSupRange();
            A[d]= B[d]= problem[d].getFuzzyLinguisticVariableList(varCons).getInfRange();
            C[d]= D[d]= problem[d].getFuzzyLinguisticVariableList(varCons).getSupRange();
            valueRegla[d]= (sup[d] + inf[d]) / 2.0;
            pesoRegla[d]= 0;
            difInf[d]= valueRegla[d] - inf[d];
            difSup[d]= sup[d] - valueRegla[d];
            difInfSup[d]= sup[d] - inf[d];
          }
        }//for (int d=0; d < numShifts; d++){  

//System.out.println("-----------------");        
//System.out.println(A[0] + " 0");
//System.out.println(B[0] + " 1");
//System.out.println(C[0] + " 1");
//System.out.println(D[0] + " 0");
//System.out.println();
//System.out.println(A[1] + " 0");
//System.out.println(B[1] + " 1");
//System.out.println(C[1] + " 1");
//System.out.println(D[1] + " 0");
//System.out.println();
//System.out.println(A[2] + " 0");
//System.out.println(B[2] + " 1");
//System.out.println(C[2] + " 1");
//System.out.println(D[2] + " 0");
//System.out.println();
        
        
        double param[];                
        // corte entre et0 y et1
        param= calcParamRectasyCorte(A,B,C,D,0,1);
        xCorte[0]= param[0];
        yCorte[0]= param[1];
        pend[0]= param[2];
        pend[1]= param[3];
        desp[0]= param[4];
        desp[1]= param[5];
        // corte entre et1 y et2
        param= calcParamRectasyCorte(A,B,C,D,1,2);
        xCorte[1]= param[0];
        yCorte[1]= param[1];
        pend[2]= param[2];
        pend[3]= param[3];
        desp[2]= param[4];
        desp[3]= param[5];
        // corte entre et0 y et2
        param= calcParamRectasyCorte(A,B,C,D,0,2);
        xCorte[2]= param[0];
        yCorte[2]= param[1];
        pend[4]= param[2];
        pend[5]= param[3];
        desp[4]= param[4];
        desp[5]= param[5];
        
        // ordenamos los puntos en el eje x (de menor a mayor)
        int indMin=0, indMax=2;
        int[] ordenInv= new int[3];
        double min=xCorte[0], max= xCorte[2];
        for (int i=0; i < 3; i++){
          if (xCorte[i] > max){
            max= xCorte[i];    
            indMax= i;
          }
          if (xCorte[i] < min){
            min= xCorte[i];
            indMin= i;
          }
        }
        for (int i= 0; i < 3; i++){
          if (indMin==i){
            ordenInv[0]= i;
          }
          else if (indMax == i){
            ordenInv[2]= i;
          }
          else{
            ordenInv[1]= i;
          }
        }

        // analizamos si el punto central está por debajo o por encima de las rectas que se han formado con los puntos extremos
        if ((yCorte[0] > 0 && yCorte[0] <= 1) // hay 3 puntos de corte
            && (yCorte[1] > 0 && yCorte[1] <= 1)
            && (yCorte[2] > 0 && yCorte[2] <= 1)){

          double xizda, xdcha, xcentro, yizda, ydcha, ycentro;
          double pendIzda, despIzda, pendDcha, despDcha;
          ycentro= yCorte[ordenInv[1]];
          yizda= yCorte[ordenInv[0]];
          ydcha= yCorte[ordenInv[2]];
          xcentro=xCorte[ordenInv[1]];
          xizda= xCorte[ordenInv[0]];
          xdcha= xCorte[ordenInv[2]];
          pendIzda= pend[ordenInv[0]*2];
          despIzda= desp[ordenInv[0]*2];
          if (pendIzda > 0){
            pendIzda= pend[ordenInv[0]*2+1];
            despIzda= desp[ordenInv[0]*2+1];
          }
          pendDcha= pend[ordenInv[2]*2];
          despDcha= desp[ordenInv[2]*2];
          if (pendDcha < 0){
            pendDcha= pend[ordenInv[2]*2+1];
            despDcha= desp[ordenInv[2]*2+1];
          }
          double xValor, yValor;
          if (despDcha == despIzda && pendDcha == pendIzda){ // estamos en las etiquetas de los extremos -> se devuelve el centro
            valueReglaCombinado= (valueRegla[0] + valueRegla[1] + valueRegla[2])/ 3;
          }
          else{
            xValor = -(despDcha - despIzda) / (pendDcha-pendIzda);
            yValor = (((pendIzda*xValor) + despIzda) + ((pendDcha*xValor) + despDcha)) / 2.0;

            if (yValor > 0 && yValor <=1){ //devuelve el valor en "x" del punto que tiene el minimo "y"
              if (yValor < ycentro){
                valueReglaCombinado= xValor;
              }
              else{
                valueReglaCombinado= xcentro;
              }              
            }
            else{
              valueReglaCombinado= xcentro;
            }          
          }
        }//if ((yCorte[0] > 0 && yCorte[0] <= 1) // hay 3 puntos de corte
        else if ((yCorte[0] > 0 && yCorte[0] <= 1) // hay 2 puntos de corte
            && (yCorte[1] > 0 && yCorte[1] <= 1)){
// hay 2 etiquetas separadas, hay que ver cómo se "unen"          
          valueReglaCombinado= (xCorte[0] + xCorte[1])/ 2.0;          
        }
        else if ((yCorte[1] > 0 && yCorte[1] <= 1) // hay 2 puntos de corte
            && (yCorte[2] > 0 && yCorte[2] <= 1)){
// hay 2 etiquetas separadas, hay que ver cómo se "unen"          
          valueReglaCombinado= (xCorte[1] + xCorte[2])/ 2.0;          
        }
        else if ((yCorte[0] > 0 && yCorte[0] <= 1) // hay 2 puntos de corte
            && (yCorte[2] > 0 && yCorte[2] <= 1)){
// hay 2 etiquetas separadas, hay que ver cómo se "unen"          
          valueReglaCombinado= (xCorte[0] + xCorte[2])/ 2.0;          
        }
        else if ((yCorte[0] > 0 && yCorte[0] <= 1)){ // hay 1 punto de corte
          // se considera que la etiqueta "aislada" es errónea
          valueReglaCombinado= xCorte[0];          
        }
        else if ((yCorte[1] > 0 && yCorte[1] <= 1)){ // hay 1 punto de corte
          // se considera que la etiqueta "aislada" es errónea
          valueReglaCombinado= xCorte[1];          
        }
        else if ((yCorte[2] > 0 && yCorte[2] <= 1)){ // hay 1 punto de corte
          // se considera que la etiqueta "aislada" es errónea
          valueReglaCombinado= xCorte[2];          
        }
        else{ // NO hay punto de corte
// hay que estudiarlo para ver si se consideran las medias por ejemplo ponderadas con el peso de las reglas          
          valueReglaCombinado= (valueRegla[0] + valueRegla[1] + valueRegla[2])/ 3;
        }
        
//System.out.println(xCorte[0]+","+yCorte[0]+";"+xCorte[1]+","+yCorte[1]+";"+xCorte[2]+","+yCorte[2]);
//System.out.println(pend[0]+","+desp[0]+";"+pend[1]+","+desp[1]+";"+pend[2]+","+desp[2]
//        +";"+pend[3]+","+desp[3]+";"+pend[4]+","+desp[4]+";"+pend[5]+","+desp[5]);
//System.out.println(valueReglaCombinado);        
        
        return valueReglaCombinado;
      
    }

    /**
     * Calculate of parameters of intersection of lines (gradient, intercept, 
     * @param A matrix of A's values of labels [label0,label1]
     * @param B matrix of B's values of labels 
     * @param C matrix of C's values of labels
     * @param D matrix of D's values of labels
     * @param ind0 index of first label
     * @param ind1 index of second label
     * @return matrix of parameters: 0,1->x,y value of intersection, 2,3->gradients of lines, 4,5-> intercept of lines
     */
    public static double[] calcParamRectasyCorte(double[] A, double[] B, double[] C, double[] D,
                                      int ind0, int ind1){

        double[] resultado= new double[6];
      
        double[] corte;
      
        double[] x1, x2, y1, y2, pendiente, desplazamiento;

        corte= new double[2]; // 0 -> x, 1 -> y
        
        pendiente= new double[2];
        desplazamiento= new double[2];
        
        x1= new double[2];
        x2= new double[2];
        y1= new double[2];
        y2= new double[2];
        
        for (int i=0; i < 2; i++){
          x1[i]= x2[i]= y1[i]= y2[i]= pendiente[i]= desplazamiento[i]= corte[i]= 0;
        }

        if (B[ind0] < B[ind1]){ // et0 a izda de et1
          x1[0]= C[ind0];
          y1[0]= 1;
          x2[0]= D[ind0];
//          y2[0]= 0;
          if (D[ind0] != B[ind0]){
            y2[0]= 0;
          }
          else{
            y2[0]= 1;
          }
          
          x1[1]= A[ind1];
//          y1[1]= 0;
          if (A[ind1] != C[ind1]){
            y1[1]= 0;
          }
          else{
            y1[1]= 1;
          }
          x2[1]= B[ind1];
          y2[1]= 1;
        }
        else{ //if (B[ind0] > B[ind1]){ // et0 a dcha de et1 (también cubre el caso en que la cima de las etiquetas sea la misma
          x1[0]= A[ind0];
//          y1[0]= 0;
          if (A[ind0] != C[ind0]){
            y1[0]= 0;
          }
          else{
            y1[0]= 1;
          }
          x2[0]= B[ind0];
          y2[0]= 1;
          
          x1[1]= C[ind1];
          y1[1]= 1;
          x2[1]= D[ind1];
//          y2[1]= 0;
          if (D[ind1] != B[ind1]){
            y2[1]= 0;
          }
          else{
            y2[1]= 1;
          }
        }
//        else{ // et0 y et1 parten del mismo punto --> esto no debe darse por el desplazamiento...
//          // el punto de corte sería el B[0]
//          x2[0]= x1[1]= B[ind0];
//          y2[0]= y1[1]= 1;
//          x1[0]= x2[1]= -B[ind0];
//          y1[0]= y2[1]= -1;          
//        }
        for (int i= 0; i < 2; i++){
          if (y1[i] == y2[i]){ // la recta es horizontal
            pendiente[i]= 0;
          }
          else if (x1[i] == x2[i]){ // la recta es vertical
            pendiente[i]= 1;
          }
          else{
            pendiente[i]= (y1[i] - y2[i]) / (x1[i] - x2[i]);
          }
          desplazamiento[i]= ((y1[i] - pendiente[i]*x1[i]) + (y2[i] - pendiente[i]*x2[i])) / 2.0;
        }
        if (pendiente[0] == pendiente[1]){
          corte[0]= -1;
          corte[1]= -1;          
        }
        else{
          corte[0] = -(desplazamiento[1] - desplazamiento[0]) / (pendiente[1]-pendiente[0]);
          corte[1] = (((pendiente[0]*corte[0]) + desplazamiento[0]) + ((pendiente[1]*corte[0]) + desplazamiento[1])) / 2.0;
        }
        
        resultado[0]= corte[0];
        resultado[1]= ((int) (corte[1]*1000)) / 1000.0; // redondeamos algo para no considerar tantos decimales ya que comprobamos que esté entre 0 y 1
        resultado[2]= pendiente[0];
        resultado[3]= pendiente[1];
        resultado[4]= desplazamiento[0];
        resultado[5]= desplazamiento[1];
        
        return resultado;

    }
    
/*............................................................................*/    

    /**
     * print parameters of fileParameters (keel format)
     * @param fileParameters file of parameters (keel format)
     * @return string with parameters obtained of fileParameters
     */
    public static String printParametersKeel(String fileParameters){
      String salida= "\n";

     
      // leer el fichero de configuración 
      try{
        BufferedReader reader = new BufferedReader(new FileReader(fileParameters));
        String line = null;
        while ((line = reader.readLine()) != null) {// a partir de la 4º linea tiene los parámetros
          salida+= line+"\n";
        }
        
      } catch (IOException e) {
       System.out.println("File I/O error! - imprimiendo parámetros");
      }      
      
      return salida;
    }
    
        // para sacar la pantalla de forma que "entienda" xfuzzy (lo que utiliza Andrés)
    /**
     * print set of rules in format Xfuzzy
     * @param fileName name of file to will be printed
     * @param R set of rules
     * @return 1->ok, -1 if error
     */
    public static int printForXfuzzy(String fileName, RuleSetClass R, FuzzyProblemClass problem){
      
      String auxString="";
      int resInt;
      FuzzyLinguisticVariableClass v;
      FuzzyLinguisticTermClass t;

//      resInt= initResFile(fileName);
//      if (resInt == -1){
//        return resInt;
//      }

      auxString+="······························"+"\n";
      auxString+=".......FORMATO XFUZZY........."+"\n";
      auxString+="······························"+"\n";

      // operatorSet
      auxString= "operatorset " + Attributes.getRelationName() + "{\n" +
              "  and xfl.prod();\n"+
              "  defuz xfl.FuzzyMean();\n"+
              "}\n\n";
      // type
      for (int i=0; i < problem.getFuzzyLinguisticVariableNum(); i++){
        v= problem.getFuzzyLinguisticVariableList(i);
        auxString+= "type T"+v.getName()+
                " ["+v.getInfRange()+
                ","+v.getSupRange()+
                ";256] {\n";
        
        for (int j=0; j < v.getFuzzyLinguisticTermNum(); j++){
          t= v.getFuzzyLinguisticTermList(j);
          auxString+= "  " + t.getName() + " xfl.triangle("+
                  t.getA() + "," + t.getB() + "," + t.getD()+");\n";
        }
        auxString+= "}\n\n";
      }
             
      //rulebase
      auxString+="rulebase global (";
      for (int i=0; i < problem.getFuzzyLinguisticVariableNum()-1; i++){
        v= problem.getFuzzyLinguisticVariableList(i);
        auxString+= "T"+v.getName()+ " " + v.getName();
        if (i != problem.getFuzzyLinguisticVariableNum()-2){
          auxString+=", ";
        }
      }      
      auxString+= " : ";
      v= problem.getFuzzyLinguisticVariableList(problem.getFuzzyLinguisticVariableNum()-1);
      auxString+= "T"+v.getName()+ " " + v.getName() + ")";
      auxString+= " using " + Attributes.getRelationName() + "{\n";
      // reglas
      for (int i=0; i < R.getNumRules(); i++){
          auxString+= "  if((";
          int numVarAntec, numEtiqVar;
          numVarAntec= 0;
          for (int j=0; j < problem.getFuzzyLinguisticVariableNum()-1; j++){
            v= problem.getFuzzyLinguisticVariableList(j);
            numEtiqVar=0;
            for (int k=0; k < v.getFuzzyLinguisticTermNum(); k++){
              t= v.getFuzzyLinguisticTermList(k);
              if (R.getRules(i).getBinaryMatrix(0, k) != 0){
                if (numEtiqVar == 0 && numVarAntec > 0){
                  auxString+= ") & (";
                }
                if (numEtiqVar > 0){
                  auxString+= " | ";
                }
                auxString+= v.getName() + " == " + t.getName();
                numEtiqVar++;
              }
            }
            if (numEtiqVar > 0){
              numVarAntec++;
            }
          }
          v= problem.getFuzzyLinguisticVariableList(problem.getFuzzyLinguisticVariableNum()-1);
          auxString+= ")) -> " + v.getName() + " = " + 
                  v.getFuzzyLinguisticTermList(R.getRules(i).getIntegerMatrix(0, 0)).getName() +
                  ";\n";
      }
      auxString+= "}\n\n";
      
      //system
      auxString+= "system (";
      for (int i=0; i < problem.getFuzzyLinguisticVariableNum()-1; i++){
        v= problem.getFuzzyLinguisticVariableList(i);
        auxString+= "T"+v.getName()+ " " + v.getName();
        if (i != problem.getFuzzyLinguisticVariableNum()-2){
          auxString+=", ";
        }
      }      
      auxString+= " : ";
      v= problem.getFuzzyLinguisticVariableList(problem.getFuzzyLinguisticVariableNum()-1);
      auxString+= "T"+v.getName()+ " " + v.getName() + ") {\n"+
              "  global(";
      for (int i=0; i < problem.getFuzzyLinguisticVariableNum()-1; i++){
        v= problem.getFuzzyLinguisticVariableList(i);
        auxString+= v.getName();
        if (i != problem.getFuzzyLinguisticVariableNum()-2){
          auxString+=", ";
        }
      }      
      auxString+= " : ";
      v= problem.getFuzzyLinguisticVariableList(problem.getFuzzyLinguisticVariableNum()-1);
      auxString+= v.getName() + ");\n }\n";
      
      auxString+="······························"+"\n";
//      auxString+="\n";
      resInt= writeResFile(fileName, auxString);
      if (resInt == -1){
        return resInt;
      }
      
      return 1;
    }
    
    /**
     * Print confussion matrix
     * @param fileName File Name where this information will be printed
     * @param confussion matrix of confussion 
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     */
    public static void printConfussionMatrix(String fileName, double[][] confussion, int printOut, int printFile){
      
      String auxString="-- Confusion Matrix -- \n";
      if (confussion != null){
        int numClases= confussion.length-1;
        for (int j=0; j < numClases; j++){
            for (int k=0; k < numClases; k++){
                auxString+= (int) confussion[j][k] + "\t";
            }

            auxString+= "|\t" + (int) confussion[j][numClases] + "\n";
        }
        for (int j=0; j < numClases; j++){
            auxString+= "------\t";
        }
        auxString+="|\t------\n";
        for (int j=0; j < numClases; j++){
           auxString+= (int) confussion[numClases][j] + "\t";
        }
        auxString+= "|\t" + (int) confussion[numClases][numClases] + "\n";
      }
      if (printOut == 1){
          System.out.print(auxString);
          System.out.flush();
      }
      if (printFile == 1){
          writeResFile(fileName, auxString);
      }
    }

    /**
     * Print cost matrix
     * @param fileName File Name where this information will be printed
     * @param costMatrix matrix of cost
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     */
    public static void printCostMatrix(String fileName, double[][] costMatrix, int printOut, int printFile){
      
      String auxString="-- Cost Matrix -- \n";
      if (costMatrix == null){
          auxString+="\n\t NO COST MATRIX \n\n";
      }
      else{
        int numClases= costMatrix.length-1;
        for (int j=0; j <= numClases; j++){
            for (int k=0; k <= numClases; k++){
                auxString+= format.format(costMatrix[j][k]) + "\t";
            }
            auxString+="\n";
        }
        auxString+="\n";
      }
      if (printOut == 1){
          System.out.print(auxString);
          System.out.flush();
      }
      if (printFile == 1){
          writeResFile(fileName, auxString);
      }
    }

    /**
     * Print confussion matrix and metrics 
     * @param fileName File Name where this information will be printed
     * @param R set of rules
     * @param printOut 1 -> print in std out
     * @param printFile 1 -> print in fileName out
     */
    public static void printMetrics(String fileName, RuleSetClass R, int printOut, int printFile){
      
      printConfussionMatrix(fileName, R.confusion, printOut, printFile);

      String auxString= "";
      
//      auxString+="-- Classification metrics -- \n";
//      auxString+="CCR:\t" + format.format(R.CCR) + "\n";
//      auxString+="TPR:\t" + format.format(R.TPR) + "\n";
//      auxString+="SM:\t" + format.format(R.SM) + "\n";
//      auxString+="TNR:\t" + format.format(R.TNR) + "\n";
//      auxString+="FPR:\t" + format.format(R.FPR) + "\n";
//      auxString+="Kappa:\t" + format.format(R.Kappa) + "\n";
//      auxString+="AUC:\t" + format.format(R.AUC) + "\n";
//
//      auxString+="-- Regression metrics -- \n";
//      auxString+="MSE:\t" + format.format(R.MSE) + "\n";
//      auxString+="RMSE:\t" + format.format(R.RMSE) + "\n";
//      auxString+="RMAE:\t" + format.format(R.RMAE) + "\n";
//      
//      auxString+="-- Ordinal metrics -- \n";
//      auxString+="OMAE:\t" + format.format(R.OMAE) + "\n";
//      auxString+="AMAE:\t"+ format.format(R.AMAE) + "\n";
//      auxString+="MMAE:\t"+ format.format(R.MMAE) + "\n";
//      auxString+="mMAE:\t"+ format.format(R.mMAE) + "\n";
//      auxString+="Rs:\t" + format.format(R.Spearman) + "\n";
//      auxString+="Taub:\t" + format.format(R.Kendall) + "\n";
//      auxString+="OC:\t" + format.format(R.OC) + "\tbeta:\t" + format.format(R.beta) + "\n";
      
      auxString+="-- metrics -- \n";
//      auxString+="myMet\tCCR\tTPR\tSM\tTNR\tFPR\tKappa\tAUC\t\tMSE\tRMSE\tRMAE\t\tOMAE\tAMAE\tMMAE\tmMAE\tRs\tTaub\tOC\tbeta\n";
      auxString+="myMet\tmyMtMd\tCCR\tOMAE\tOMAENm\tNoOMAENm\n";
      auxString+= format.format(R.metric) + "\t";
      auxString+= format.format(R.metricMedia) + "\t";
      auxString+= format.format(R.CCR) + "\t";
//      auxString+= format.format(R.TPR) + "\t";
//      auxString+= format.format(R.SM) + "\t";
//      auxString+= format.format(R.TNR) + "\t";
//      auxString+= format.format(R.FPR) + "\t";
//      auxString+= format.format(R.Kappa) + "\t";
//      auxString+= format.format(R.AUC) + "\t";
//
//      auxString+="\t" + format.format(R.MSE) + "\t";
//      auxString+= format.format(R.RMSE) + "\t";
//      auxString+= format.format(R.RMAE) + "\t";
      
      auxString+= format.format(R.OMAE) + "\t";
      auxString+= format.format(R.OMAENormalizado) + "\t";
      auxString+= format.format(1-R.OMAENormalizado) + "\n";
//      auxString+="\t" + format.format(R.OMAE) + "\t";
//      auxString+= format.format(R.AMAE) + "\t";
//      auxString+= format.format(R.MMAE) + "\t";
//      auxString+= format.format(R.mMAE) + "\t";
//      auxString+= format.format(R.Spearman) + "\t";
//      auxString+= format.format(R.Kendall) + "\t";
//      auxString+= format.format(R.OC) + "\t";
//      auxString+= format.format(R.beta) + "\n";

      if (printOut == 1){
          System.out.print(auxString);
          System.out.flush();
      }
      if (printFile == 1){
          writeResFile(fileName, auxString);
      }
    }

    public static String printStatisticalDataLearning(){

        String auxString="";
      
        auxString+="··································"+"\n";
        auxString+="···Statistical Data of Learning····"+"\n";
        auxString+="··································"+"\n";
        auxString+="NumIterGenetic: "+ Util.numIterGenetic +"\n";
        auxString+="  NumIterGeneticIn: ";
        for (int i=0; i < Util.numIterGeneticIn.size(); i++){
          auxString+= Util.numIterGeneticIn.get(i).toString() + " ";
        }
        auxString+="\n";
        auxString+="NumIndividuals: " + Util.numIndividuals+"\n";
        auxString+="NumAtrib: " + Util.numAtrib+"\n";
        auxString+="  NumLabels: ";
        for (int i=0; i < Util.numLabels.size(); i++){
          auxString+= Util.numLabels.get(i).toString() + " ";
        }
        auxString+="\n";
        auxString+="  timeGeneticIn(ms): ";
        for (int i=0; i < Util.timeGeneticIn.size(); i++){
          auxString+= (int) Double.parseDouble(Util.timeGeneticIn.get(i).toString()) + " ";
        }
        auxString+="\n";
        auxString+="  timeCalcAdapt(ms): ";
        for (int i=0; i < Util.timeCalcAdapt.size(); i++){
          auxString+= (int) Double.parseDouble(Util.timeCalcAdapt.get(i).toString()) + " ";
        }
        auxString+="\n";
        auxString+="  timeCalcFitness(ms): ";
        for (int i=0; i < Util.timeCalcFitness.size(); i++){
          auxString+= (int) Double.parseDouble(Util.timeCalcFitness.get(i).toString()) + " ";
        }
        auxString+="\n";
        auxString+="timeGenetic(ms): " + (int) Util.timeGenetic + "\n";
        
//        int numOperations=0;
//        auxString+="  numOperationsIn: ";
//        for (int i=0; i < Util.numIterGenetic; i++){
//          int auxNumIter= Integer.parseInt(Util.numIterGeneticIn.get(i).toString());
//          int auxNumOperIter= Util.numIndividuals + (2*auxNumIter);
//          numOperations+= auxNumOperIter;
//          auxString+= auxNumOperIter + " ";
//        }
//        auxString+="numOperations: " +  numOperations + "\n";
        
        
        return auxString;
    }
    
    
    
// uso:     
//DebugClass.sendMail("asunto","cuerpo","../results.txt0");
//DebugClass.sendMail("asunto","cuerpo","");
    /**
     * send email 
     * @param subject
     * @param body
     * @param fileName
     * @return 1 -> no error, -1 if error
     */
    public static int sendMail(String subject, String body, String fileName){

      if (origMail == ""){
        return -1;
      }
      String user= origMail.substring(0, origMail.indexOf("@"));
      Runtime rt = Runtime.getRuntime();
      Process p;
      
      try {
        if (fileName.isEmpty() == false){
          String[] cmd = {"sendemail","-f",origMail,"-t",destMail,
            "-s","smtp.gmail.com:587","-u","'"+
                  java.net.InetAddress.getLocalHost().getHostName()+
                  " : " + System.getProperty("user.dir")+
                  " : "+ subject+"'","-m","'"+body+"'","-v","-xu",
            user,"-xp",passOrig,"-a",fileName,"-o","tls=yes"};        
         
//          Runtime.getRuntime().exec(cmd);
          p= rt.exec(cmd);
          p.waitFor();
          System.out.println("email sent with exit value : " +p.exitValue());          
        }
        else{
          String[] cmd = {"sendemail","-f",origMail,"-t",destMail,
            "-s","smtp.gmail.com:587","-u","'"+
                  java.net.InetAddress.getLocalHost().getHostName()+
                  " : " + System.getProperty("user.dir")+
                  ":"+ subject+"'","-m","'"+body+"'","-v","-xu",
            user,"-xp",passOrig,"-o","tls=yes"};        

//          Runtime.getRuntime().exec(cmd);
          p= rt.exec(cmd);
          p.waitFor();
          System.out.println("email sent with exit value : " +p.exitValue());          
        }
      } catch (Exception err) {
        System.out.println (err);
        return -1;
      }

      return 1;
    }
    
}

