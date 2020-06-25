package NSLVOrdJava;

// para la integración en keel
import keel.Dataset.*;

import java.util.*;

/**
 * @file NSLVOrd.java
 * @brief main file of proyect
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date diciembre 2015
 * @note Implement of NSLV algorithm for ordinal classification
 */
public class NSLVOrdJava {

    // habrá 3 valores: indice 0->izda, indice 1->centro, indice 2->dcha
//    static int numDesplazamientos=3;
    static int numDesplazamientos=1;
    static double[] time;
    static int[] iter;

    static FuzzyProblemClass[] fuzzyProblem;
    static ExampleSetProcess[] E_par, E_par_test;
    static RuleSetClass[] R;

    static InstanceSet iSet;
    static InstanceSet tSet;
    
    static Random[] randomNum;

    static String fileResultDebug;
    static double[][] costMatrix;
    static int seed;
    static int numLabelsInputs;
    static int numLabelsOutput;
    static int shift; // 5% de la mitad del tamaño de la etiqueta para el desplazamiento
 
    static int homogeneousLabel=0; // se elimina el parámetro de cuda para introducir la creación de etiquetas homogéneas
    // parámetros de ponderación de la característica ordinal o nominal de la función fitness
    // alpha * CCR y (1-alpha) * MAE
    static double alpha=0.5;
    
    static String[] poblationParam;
   
    public static String[] Train(String[] _header,String[] _datas,String[] args){
        Attributes.clearAll();
        initParameters(args);
        
        // Aquí se inicializa Random
        randomNum= new Random[numDesplazamientos];
        iter= new int[numDesplazamientos];
        time= new double[numDesplazamientos];
        for (int i=0; i < numDesplazamientos; i++){
          randomNum[i]= new Random(seed);          
        }

        // obtener las instancias (ejemplos) de training y test y pasarlas a "los objetos de mis clases"
        if(!ReadSet(_header,_datas, true)) return null;
                
        if(!executeNSLVOrd(homogeneousLabel)) return null;
        
        //String dir = "./XMLFiles/"; 
        //String name = "IrisMamdani2";
        //XMLFile(dir,name);
        
        return Targets(E_par,1);
        
        //double[] Resultado;
        //Resultado= DebugClass.printForCalcMetricsTFG(E_par[0],R[0]);
     
        // Imprimir reglas            
        //String auxString= DebugClass.printUnderstableRuleSet(R[0], fuzzyProblem[0]);
        //System.out.println(auxString);
    }
    
    public static FuzzyProblemClass[] GetFuzzyProblem(){
        return fuzzyProblem;
    }
    
    public static String[] get_knowledge_base(){
        RuleSystem _exp = new RuleSystem(fuzzyProblem[0], R[0]);
        String[] kb = _exp.Export_KnowledgeBase();
        return kb;
    }
    
    public static String[] get_rules(){
        RuleSystem _exp = new RuleSystem(fuzzyProblem[0], R[0]);
        String[] rules = _exp.Export_Rules();
        return rules;
    }
    
    public static String[] get_rule_base(){
        RuleSystem _exp = new RuleSystem(fuzzyProblem[0], R[0]);
        String[]  rb = _exp.Export_RuleBase();
        return rb;
    }
   
    public static void LoadModel(String[] _fuzzyProblem,String[] _R){
        fuzzyProblem = new FuzzyProblemClass[numDesplazamientos];
        R= new RuleSetClass[numDesplazamientos];
        E_par_test= new ExampleSetProcess[numDesplazamientos];
        
        Load_KnowledgeBase(_fuzzyProblem);
        Load_RuleBase(_R);
    }
     
    public static void Load_KnowledgeBase(String[] sfp){
        // FUZZY PROBLEM
        FuzzyProblemClass fp = new FuzzyProblemClass();
        fp.setConsequentIndexOriginal(Integer.parseInt(sfp[0]));
        fp.setShift(Integer.parseInt(sfp[1]));
        fp.setDirection(Integer.parseInt(sfp[2]));
        fp.setHomogeneousLabel(Integer.parseInt(sfp[3]));
        fp.setFuzzyLinguisticVariableNum(Integer.parseInt(sfp[4]));
        
        // FUZZY VARIABLE
        FuzzyLinguisticVariableClass[] fv = new FuzzyLinguisticVariableClass[fp.getFuzzyLinguisticVariableNum()];
        int pos = 5;
        for(int i = 0; i < fp.getFuzzyLinguisticVariableNum(); i++){
            fv[i] = new FuzzyLinguisticVariableClass();
            fv[i].setName(sfp[pos]); pos++;
            fv[i].setUnit(Integer.parseInt(sfp[pos])); pos++;
            fv[i].setNumTermAutomatic(Double.parseDouble(sfp[pos])); pos++;
            fv[i].setVariableType(Integer.parseInt(sfp[pos])); pos++;
            fv[i].setInfRange(Double.parseDouble(sfp[pos])); pos++;
            fv[i].setSupRange(Double.parseDouble(sfp[pos])); pos++;
            fv[i].setInfRangeIsInf(Integer.parseInt(sfp[pos])); pos++;
            fv[i].setSupRangeIsInf(Integer.parseInt(sfp[pos])); pos++;
            fv[i].setFuzzyLinguisticTermNum(Integer.parseInt(sfp[pos])); pos++;
            
            // FUZZY TERM
            FuzzyLinguisticTermClass[] ft = new FuzzyLinguisticTermClass[fv[i].getFuzzyLinguisticTermNum()];
            for(int j = 0; j < fv[i].getFuzzyLinguisticTermNum(); j++){
                ft[j] = new FuzzyLinguisticTermClass();
                ft[j].setName(sfp[pos]); pos++;
                ft[j].setA(Double.parseDouble(sfp[pos])); pos++;
                ft[j].setB(Double.parseDouble(sfp[pos])); pos++;
                ft[j].setC(Double.parseDouble(sfp[pos])); pos++;
                ft[j].setD(Double.parseDouble(sfp[pos])); pos++;
                ft[j].setAbInf(Integer.parseInt(sfp[pos])); pos++;
                ft[j].setCdInf(Integer.parseInt(sfp[pos])); pos++;
            }
            
            fv[i].setFuzzyLinguisticTermList(ft);
        }
        
        fp.setFuzzyLinguisticVariableList(fv);
        
        fuzzyProblem[0] = fp;
    }
    
    public static void Load_RuleBase(String[] srs){
        // RULE SET
        RuleSetClass rs = new RuleSetClass();
        rs.setNumRules(Integer.parseInt(srs[0]));
        rs.CCR = Double.parseDouble(srs[1]);
        rs.SM = Double.parseDouble(srs[2]);
        rs.TPR = Double.parseDouble(srs[3]);
        rs.TNR = Double.parseDouble(srs[4]);
        rs.FPR = Double.parseDouble(srs[5]);
        rs.Kappa = Double.parseDouble(srs[6]);
        rs.AUC = Double.parseDouble(srs[7]);
        rs.MSE = Double.parseDouble(srs[8]);
        rs.RMSE = Double.parseDouble(srs[9]);
        rs.RMAE = Double.parseDouble(srs[10]);
        rs.OMAE = Double.parseDouble(srs[11]);
        rs.OMAENormalizado = Double.parseDouble(srs[12]);
        rs.MMAE = Double.parseDouble(srs[13]);
        rs.mMAE = Double.parseDouble(srs[14]);
        rs.AMAE = Double.parseDouble(srs[15]);
        rs.Spearman = Double.parseDouble(srs[16]);
        rs.Kendall = Double.parseDouble(srs[17]);
        rs.OC = Double.parseDouble(srs[18]);
        rs.beta = Double.parseDouble(srs[19]);
        rs.metric = Double.parseDouble(srs[20]);
        rs.metricMedia = Double.parseDouble(srs[21]);
        rs.Precision = Double.parseDouble(srs[22]);
        rs.alphaMetric = Double.parseDouble(srs[23]);
        rs.confusion = new double[Integer.parseInt(srs[24])][];
        int pos = 25;
        for(int i = 0; i < rs.confusion.length; i++){
            rs.confusion[i] = new double[Integer.parseInt(srs[pos])]; pos++;
            for(int j = 0; j < rs.confusion[i].length; j++){
                rs.confusion[i][j] = Double.parseDouble(srs[pos]); pos++;
            }
        }
        
        // RULE
        GenetCodeClass[] rul = new GenetCodeClass[rs.getNumRules()];
        for(int i = 0; i < rul.length; i++){
            // Binary elements
            int binaryBlocs;
            int[] sizeBinaryBlocs;
            int[][] binaryMatrix;
            binaryBlocs = Integer.parseInt(srs[pos]); pos++;
            sizeBinaryBlocs = new int[binaryBlocs];
            binaryMatrix = new int[binaryBlocs][];
            for(int j = 0; j < binaryBlocs; j++){
                sizeBinaryBlocs[j] = Integer.parseInt(srs[pos]); pos++;
                binaryMatrix[j] = new int[sizeBinaryBlocs[j]];
                for(int k = 0; k < sizeBinaryBlocs[j]; k++){
                    binaryMatrix[j][k] = Integer.parseInt(srs[pos]); pos++;
                }
            }
            
            // Integer elements
            int integerBlocs;
            int[] sizeIntegerBlocs;
            int[][] integerMatrix;
            int[] integerRange;
            integerBlocs = Integer.parseInt(srs[pos]); pos++;
            sizeIntegerBlocs = new int[integerBlocs];
            integerMatrix = new int[integerBlocs][];
            for(int j = 0; j < integerBlocs; j++){
                sizeIntegerBlocs[j] = Integer.parseInt(srs[pos]); pos++;
                integerMatrix[j] = new int[sizeIntegerBlocs[j]];
                for(int k = 0; k < sizeIntegerBlocs[j]; k++){
                    integerMatrix[j][k] = Integer.parseInt(srs[pos]); pos++;
                }
            }
            integerRange = new int[Integer.parseInt(srs[pos])]; pos++;
            for(int j = 0; j < integerRange.length; j++){
                integerRange[j] = Integer.parseInt(srs[pos]); pos++;
            }
            
            // Real elements
            int realBlocs;
            int[] sizeRealBlocs;
            double[][] realMatrix;
            double[] realInfRange;
            double[] realSupRange;
            realBlocs = Integer.parseInt(srs[pos]); pos++;
            sizeRealBlocs = new int[realBlocs];
            realMatrix = new double[realBlocs][];
            for(int j = 0; j < realBlocs; j++){
                sizeRealBlocs[j] = Integer.parseInt(srs[pos]); pos++;
                realMatrix[j] = new double[sizeRealBlocs[j]];
                for(int k = 0; k < sizeRealBlocs[j]; k++){
                    realMatrix[j][k] = Double.parseDouble(srs[pos]); pos++;
                }
            }
            realInfRange = new double[Integer.parseInt(srs[pos])]; pos++;
            for(int j = 0; j < realInfRange.length; j++){
               realInfRange[j] = Double.parseDouble(srs[pos]); pos++;
            }
            realSupRange = new double[Integer.parseInt(srs[pos])]; pos++;
            for(int j = 0; j < realSupRange.length; j++){
                realSupRange[j] = Double.parseDouble(srs[pos]); pos++;
            }
            
            
            rul[i] = new GenetCodeClass(binaryBlocs,integerBlocs,realBlocs, 
                       sizeBinaryBlocs,sizeIntegerBlocs,sizeRealBlocs,
                       integerRange,realInfRange,realSupRange);
            rul[i].setBinaryMatrix(binaryMatrix);
            rul[i].setIntegerMatrix(integerMatrix);
            rul[i].setRealMatrix(realMatrix);
        }
        rs.setRules(rul);
        
        R[0] = rs;
    }
    
    public static String[] Test(String[] _header,String[] _datas){
        Attributes.clearAll();
        iter= new int[numDesplazamientos];
        time= new double[numDesplazamientos];
        
        // obtener las instancias (ejemplos) de training y test y pasarlas a "los objetos de mis clases"
        if(!ReadSet(_header,_datas,false)) return null;
        
        if(!executeNSLVOrdPredict()) return null;
           
        //double[] Resultado;
        //Resultado= DebugClass.printForCalcMetricsTFG(E_par_test[0],R[0]);
     
        //SeeRules();
        
        return Targets(E_par_test,1);
        
        /*for (String _Resultado1 : _Resultado) {
            System.out.println(_Resultado1);
        }*/
    }
    
    public static void initParameters(String[] param){

        String auxString;
        
        fuzzyProblem = new FuzzyProblemClass[numDesplazamientos];
        E_par= new ExampleSetProcess[numDesplazamientos];
        E_par_test= new ExampleSetProcess[numDesplazamientos];
        R= new RuleSetClass[numDesplazamientos];
        
        // Realizar el procesamiento del fichero de configuración Keel
        //parametersKeel= getParametersKeel(configFile);
        //fileTrain= parametersKeel[0];
        //fileTest= parametersKeel[2];
        seed= Integer.parseInt(param[0]);
        // el parámetro 7 (numIndividuals) (size population) se consideran en la inicialización de la población
        // el parámetro 8 (maxIterGenetico) (max number of iterations of genetic algorithm) se considera al inicializar la población
        homogeneousLabel = 0;
        numLabelsInputs= Integer.parseInt(param[1]);
        numLabelsOutput= Integer.parseInt(param[2]);
        shift= Integer.parseInt(param[3]); // 5% de la mitad del tamaño de la etiqueta para el desplazamiento
        alpha= Double.parseDouble(param[4]); // indica si realiza clasificación(1) o regresion(0)
//        beta= Double.parseDouble(parametersKeel[28]); // indica si realiza clasificación(1) o regresion(0)
        if ((alpha + (1-alpha)) != 1){
            alpha= 0.5;
        }
        // el resto de parámetros que corresponden a las probabilidades de 
        // inicialización, cruce y mutación de las subpoblaciones y de cada 
        // elemento de la subpoblación son consideradas en la inicialización de la población
        
        poblationParam = new String[14];
        poblationParam[0] = param[5];
        poblationParam[1] = param[6];
        poblationParam[2] = param[7];
        poblationParam[3] = param[8];
        poblationParam[4] = param[9];
        poblationParam[5] = param[10];
        poblationParam[6] = param[11];
        poblationParam[7] = param[12];
        poblationParam[8] = param[13];
        poblationParam[9] = param[14];
        poblationParam[10] = param[15];
        poblationParam[11] = param[16];
        poblationParam[12] = param[17];
        poblationParam[13] = param[18];
    }
    
    public static boolean ReadSet(String[] _header, String[] _datas, boolean _train){
        // obtener las instancias (ejemplos) de training y test y pasarlas a "los objetos de mis clases"
        InstanceSet _Set= new InstanceSet();
        _Set.readSetTFG(_header,_datas,true/*_train*/);
        _Set.setAttributesAsNonStatic();
        
        //si no hay ejemplos sale directamente
        if (_Set.getNumInstances() == 0){
          return false;
        }
        
        if(_train){
            iSet = _Set;
        }else{
            tSet = _Set;
        }
        
        return true;
    }
    
    public static boolean executeNSLVOrd(int homogeneousLabel){
        // parte de ejecución en serie       
        randomNum[0]= new Random(seed);
        return executeLearning(0, 0, 0, homogeneousLabel); // para probar por ahora nada más que con una ejecución
        // FIN - parte de ejecución en serie  
    }
    
    public static boolean executeNSLVOrdPredict() {
        // parte de ejecución en serie        
        return executePredict(0, 0, 0); // para probar por ahora nada más que con una ejecución
        // FIN - parte de ejecución en serie
    }

    public static boolean executeLearning(int shift, int direction, int index, int homogeneousLabel){    
          iter[index]=0;
          if (numLabelsInputs == -1 || numLabelsOutput == -1){
            // constructor para la creación de etiquetas no homogéneas (en función del número de individuos por etiqueta)
            numLabelsInputs = 11;
            numLabelsOutput = 11;
            fuzzyProblem[index]= new FuzzyProblemClass(iSet, numLabelsInputs, numLabelsOutput, shift, direction, homogeneousLabel);
          }
          else{
            // constructor original para la creación de etiquetas homogéneas
            fuzzyProblem[index]= new FuzzyProblemClass(iSet, numLabelsInputs, numLabelsOutput, shift, direction, homogeneousLabel);
          }
          // pasar los ejemplos a "mis objetos"
          E_par[index]= new ExampleSetProcess(fuzzyProblem[index], iSet);
          String result= E_par[index].calcAdaptExVarLabTFG();
          if (result.compareTo("") != 0){
              return false;                                
          } 

          // calcular las medidas de información para agilizar los cálculos
          E_par[index].calcInformationMeasures();

          // crear el objeto para el algoritmo genético
          R[index]= new RuleSetClass(alpha);        

          //creación del objeto genético e inicializarlo
          GeneticAlgorithmClass GA= new GeneticAlgorithmClass(poblationParam, E_par[index]); 
          // inicializar la población
          GA.initPopulation(randomNum[index],E_par[index],costMatrix);            
          

          // BEGIN - aquí comenzaría el bloque de ejecuciones del algoritmo genético
          Util.initStatisticalData(GA.getP(), fuzzyProblem[index]);

          // calcular la nueva regla
          int ejemplosCubiertos=0, eliminadoReglas=0, newRule=1;
          Util.numIterGenetic++;

          //// AQUÍ PARA AÑADIR O NO LA REGLA POR DEFECTO AL COMIENZO ... -> ESTO NO SE MODIFICARÁ LUEGO --> HABRÁ QUE TENERLO EN CUENTA EN LA PARTE DE REGRESIÓN
          int addDefaultRule=0;
          Util.classDefaultRule= GA.setDefaultRule(addDefaultRule,E_par[index],R[index]);
          if (addDefaultRule == 1){ // Sí se ha includo la regla por defecto al principio.
            ejemplosCubiertos= E_par[index].calcCoveredTFG(R[index],GA.getP(), fuzzyProblem[index]);             
          }

          eliminadoReglas= 1;
          while (eliminadoReglas == 1){
            while (newRule == 1 && ejemplosCubiertos < E_par[index].numExamples){  
              iter[index]++;
              newRule= GA.findNewRuleTFG(randomNum[index],0,E_par[index],R[index]); // en la versión de homogeneousLabel se ha eliminado la opción de cuda
              Util.numIterGenetic++;

              ejemplosCubiertos= E_par[index].calcCoveredTFG(R[index],GA.getP(),fuzzyProblem[index]);
  

            }//while (newRule == 1){  
              eliminadoReglas= R[index].removeRulesForImproveMetricTFG(E_par[index],GA, fuzzyProblem[index]); // probar a quitar reglas y ver si mejora la precisión
              if (eliminadoReglas == 1){
                newRule=1;      
                ejemplosCubiertos= E_par[index].calcCoveredTFG(R[index], GA.getP(), fuzzyProblem[index]);
              }
          }// while (eliminadoReglas != 1){
          
          
          if (addDefaultRule == 0){ // No se ha includo la regla por defecto al principio. Se debe incluir al final
              R[index].addRule(Util.DefaultRule, Util.DefaultRule.getRealMatrix(Util.classDefaultRule, 4), E_par[index]);
          }
          
          return true;
    }

    public static boolean executePredict(int shift, int direction, int index){    
    
          String auxString="";
          int numRules;       // numero de reglas de la particion
          double varXRule;    // media de numero de variables por regla

          iter[index]=0;
          
          // pasar los ejemplos a "mis objetos"
          E_par_test[index]= new ExampleSetProcess(fuzzyProblem[index], tSet);
          String result= E_par_test[index].calcAdaptExVarLabTFG();
          if (result.compareTo("") != 0){
              return false;                                
          }
          
          return true;
    }

    public static String[] Targets(ExampleSetProcess[] _par,int numShifts){
        int indexRule=0;
        int claseInference;
        int varCons= _par[0].getProblemDefinition().consequentIndex();
        double valueReglaCombinado;
        int[] indRegla;
        String[] _Resultado;
      
        indRegla= new int[numShifts];
        _Resultado = new String[_par[0].getNumExamples()];
      
        for (int i=0; i < _par[0].getNumExamples(); i++){
            for (int d=0; d < numShifts; d++){
                indexRule= R[d].inference(_par[d],i);      
                indRegla[d]= indexRule;
            }
            claseInference= R[0].getRules(indexRule).getIntegerMatrix(0,0);        
            valueReglaCombinado= Util.getCentralValue(claseInference, varCons, _par[0]);
      
            _Resultado[i] = _par[0].getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList((int)valueReglaCombinado).getName();        
        }
       
        return _Resultado;
    }
}
