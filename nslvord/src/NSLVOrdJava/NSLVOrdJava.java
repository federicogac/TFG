package NSLVOrdJava;

// para la integración en keel
import SeeRules.VisualRules;
import keel.Dataset.*;

import java.io.*;
import java.util.*;
import java.text.*;

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

    static String[] parametersKeel;    
    static String fileTrain;
    static String fileValid;  // No se utiliza ya que en Keel es el mismo fichero de fileTrain
    static String fileTest;
    static String fileResultTrain;
    static String fileResultTest;
    static String fileResultDebug;
    static String fileCostMatrix;
    static double[][] costMatrix;
    static int seed;
    static double errorAcceptable;
    static double percentErrorAcceptable;
    static int numLabelsInputs;
    static int numLabelsOutput;
    static int shift; // 5% de la mitad del tamaño de la etiqueta para el desplazamiento
    static int clasificacion; // porcentaje de máximo de ejemplos permitido para cada etiqueta -> si es más se desdobla

    static int dupLabels=0; // indica si se duplicará o no las etiquetas cuando se crean no homogéneas
    
    static int learning=1; // indica si se tiene que aprender o se toma del fichero de reglas aprendido 
                            // se toma del fichero de parámetros
    
//    static int cuda=0;  // se toma del fichero de parámetros    
    static int homogeneousLabel=0; // se elimina el parámetro de cuda para introducir la creación de etiquetas homogéneas
    // parámetros de ponderación de la característica ordinal o nominal de la función fitness
    // alpha * CCR y (1-alpha) * MAE
    static double alpha=0.5;
    
    static String configFile = null;
    static String[] poblationParam;
    
    public static void SeeRules(String name){
        VisualRules lista = new VisualRules(fuzzyProblem[0], R[0]);
        lista.SeeRules(name);
        lista.setVisible(true);
    }
    
    public static void DeleteNSLVOrd(){
        for(FuzzyProblemClass i : fuzzyProblem){
            i = null;
        }
        fuzzyProblem = null;
        
        for(RuleSetClass i : R){
            i = null;
        }
        R = null;
        
        iSet = null;
        tSet = null;
    }
    
    public static void Prueba() {
        for(FuzzyLinguisticVariableClass i : fuzzyProblem[0].getFuzzyLinguisticVariableList()){
            System.out.println(i.getName());
        }
        
    }

    public static String[] Train(String[] _header,String[] _datas,String[] args){
        Attributes.clearAll();
        initParametersTFG(args);
        
        // Aquí se inicializa Random
        randomNum= new Random[numDesplazamientos];
        iter= new int[numDesplazamientos];
        time= new double[numDesplazamientos];
        for (int i=0; i < numDesplazamientos; i++){
          randomNum[i]= new Random(seed);          
        }

        // obtener las instancias (ejemplos) de training y test y pasarlas a "los objetos de mis clases"
        if(!ReadSetTFG(_header,_datas, true)) return null;
                
        if(!executeNSLVOrdTFG(homogeneousLabel)) return null;
        
        //String dir = "./XMLFiles/"; 
        //String name = "IrisMamdani2";
        //XMLFile(dir,name);
        
        return DebugClass.printForKeelTFG(E_par,R,1);
        
        //double[] Resultado;
        //Resultado= DebugClass.printForCalcMetricsTFG(E_par[0],R[0]);
     
        // Imprimir reglas            
        //String auxString= DebugClass.printUnderstableRuleSet(R[0], fuzzyProblem[0]);
        //System.out.println(auxString);
    }
    
    public static FuzzyProblemClass[] GetFuzzyProblem(){
        return fuzzyProblem;
    }
    
    public static RuleSetClass[] GetRules(){
        return R;
    }
    
    public static void LoadModel(FuzzyProblemClass[] _fuzzyProblem, RuleSetClass[] _R){
        fuzzyProblem = _fuzzyProblem;
        R = _R;
    }
    
    public static void XMLFile(String dir, String name){
        XML _xml = new XML(fuzzyProblem[0], R[0]);
        
        _xml.CreateFileXML(dir,name);
        
        _xml.ExportPMML();
    }
    
    public static String[] Test(String[] _datas){
        iter= new int[numDesplazamientos];
        time= new double[numDesplazamientos];
        
        // obtener las instancias (ejemplos) de training y test y pasarlas a "los objetos de mis clases"
        if(!ReadSetTFG(null,_datas, false)) return null;
        
        if(!executeNSLVOrdPredictTFG()) return null;
           
        //double[] Resultado;
        //Resultado= DebugClass.printForCalcMetricsTFG(E_par_test[0],R[0]);
     
        //SeeRules();
        
        return DebugClass.printForKeelTFG(E_par_test,R,1);
        
        /*for (String _Resultado1 : _Resultado) {
            System.out.println(_Resultado1);
        }*/
    }
    
    public static String[] ReadDatas(String _file) throws FileNotFoundException, IOException{
        String[] aux = new String[1000];
        
        File archivo = new File(_file);
        FileReader fr = new FileReader(archivo);
        BufferedReader br = new BufferedReader(fr);
        
        String linea;
        while((linea = br.readLine()) != null){
            if(linea.equals("@data")){
                break;
            }
        }
        
        int i = -1;
        String linea_aux;
        while((linea = br.readLine()) != null){
            linea_aux = linea.replaceAll(" ","");
            if(!linea_aux.equals("")){
                i++;
                aux[i] = linea;
            }
        }
        
        String[] _datas = new String[i+1];        
        System.arraycopy(aux, 0, _datas, 0, _datas.length);
        
        return _datas;
    }
    
    public static String[] ReadHeader(String _file) throws FileNotFoundException, IOException{
        String[] aux = new String[1000];
        
        File archivo = new File(_file);
        FileReader fr = new FileReader(archivo);
        BufferedReader br = new BufferedReader(fr);
        
        String linea;
        int i=0;
        while((linea = br.readLine()) != null){
            aux[i] = linea;
            if(linea.equals("@data")){
                break;
            }
            i++;
        }
        
        String[] _headers = new String[i];
        System.arraycopy(aux, 0, _headers, 0, _headers.length);
        
        return _headers;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        fileTrain = "train_toy-0.arff";
        
        String[] _Header = ReadHeader(fileTrain); 
        String[] _Datas = ReadDatas(fileTrain);
        
        //configFile = args[0];
        
        String[] param = new String[20];
        param[0] = "1286082570";
        param[1] = "5";
        param[2] = "5";
        param[3] = "35";
        param[4] = "0.5";
        
        param[5] = "-1";
        param[6] = "500";
        param[7] = "0.9";
        param[8] = "0.25";
        param[9] = "0.5";
        param[10] = "0.17";
        param[11] = "0.5"; 
        param[12] = "0.0";
        param[13] = "0.5";
        param[14] = "0.01";
        param[15] = "0.0";
        param[16] = "0.25";
        param[17] = "0.5";
        param[18] = "0.14";
        
        String[] _Resultado = Train(_Header,_Datas,param);
        
        String dir = "./XMLFiles/"; 
        String name = "IrisMamdani2";
        //XMLFile(dir,name);
        
        fileTest = "test_toy-0.arff";
        
        _Datas = ReadDatas(fileTest);
        
        _Resultado = Test(_Datas);
        
        SeeRules("");
        
        /*for(String i : _Resultado){
            System.out.println(i);
        }*/
        
      /*
      if (args.length == 4){ //para el envío de e-mails
        DebugClass.origMail= args[1];
        DebugClass.passOrig= args[2];
        DebugClass.destMail= args[3];
      }
      
      if (args.length < 1){
        System.err.println(" ERROR: debe tener al menos un argumento");
        System.out.println(writeSyntax("NSLVOrd.jar"));
        System.exit(-1);
      }
      
      initParameters(args[0]); // carga los parámetros y crea los objetos a utilizar (rules, E_par,...)
      DebugClass.fileResultDebug= fileResultDebug;

      // preparar las salidas a pantalla y fichero
      DebugClass.initFileResultsAndPrintHeads(args[0],fileResultDebug, 
              fileResultTrain, fileResultTest, numDesplazamientos);        

      if (learning == 1){ // se realiza el aprendizaje y se guarda el modelo aprendido
//        executeNSLVOrd(args[0], cuda);// ejecutar el aprendizaje
        executeNSLVOrd(args[0], homogeneousLabel);// ejecutar el aprendizaje
//        saveLearning(); // guardar el modelo aprendido
      }
      else{
        loadLearning();
        executeNSLVOrdPredict(args[0]);// ejecutar la prediccion
      }
      // ahora mismo con 1 base de reglas
      DebugClass.printForCalcMetrics(clasificacion, fileResultTrain+".4calcMetrics",E_par[0],R[0],0,1);
      DebugClass.printForCalcMetrics(clasificacion, fileResultTest+".4calcMetrics",E_par_test[0],R[0],0,1);
      DebugClass.printForKeel(fileResultTrain,E_par,R,1);
      DebugClass.printForKeel(fileResultTest,E_par_test,R,1);
      // FIN - ahora mismo con 1 base de reglas

      // ESTO ES PARA CUANDO TENEMOS 3 BASES DE REGLAS        
      //executeInference();

      // imprimir el modelo de aprendizaje
      DebugClass.printModelLearning(fuzzyProblem, E_par, E_par_test, R,
          fileResultDebug, iter, time, numDesplazamientos);      

      if (DebugClass.sendMail == 1){// ha habido algo que tengamos que revisar/considerar--> mandar email para avisar
        System.out.println(writeSyntax("NSLVOrd.jar"));
        String auxString= "\nTenemos que enviar email por:" + "\n" + DebugClass.cuerpoMail;
        System.out.println(auxString);
//        DebugClass.writeResFile(DebugClass.fileResultDebug+0,auxString);
        //DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//        DebugClass.sendMail("nslv execution",auxString,DebugClass.fileResultDebug+0);
        //DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+1);
        
      }
      */
    }

    /**
     * get the parameters from file of configuration (in keel format)
     * @param configFile file of configuration parameters
     */
    public static void initParameters(String configFile){

        String auxString;
        
        fuzzyProblem = new FuzzyProblemClass[numDesplazamientos];
        E_par= new ExampleSetProcess[numDesplazamientos];
        E_par_test= new ExampleSetProcess[numDesplazamientos];
        R= new RuleSetClass[numDesplazamientos];
        
        // Realizar el procesamiento del fichero de configuración Keel
        parametersKeel= getParametersKeel(configFile);
        fileTrain= parametersKeel[0];
        fileValid= parametersKeel[1];
        fileTest= parametersKeel[2];
        fileResultTrain= parametersKeel[3];
        fileResultTest= parametersKeel[4];
        fileResultDebug= parametersKeel[5];
        seed= Integer.parseInt(parametersKeel[6]);
        // el parámetro 7 (numIndividuals) (size population) se consideran en la inicialización de la población
        // el parámetro 8 (maxIterGenetico) (max number of iterations of genetic algorithm) se considera al inicializar la población
        learning= Integer.parseInt(parametersKeel[9]);
//        cuda= Integer.parseInt(parametersKeel[10]);
        homogeneousLabel= Integer.parseInt(parametersKeel[10]);
        numLabelsInputs= Integer.parseInt(parametersKeel[11]);
        numLabelsOutput= Integer.parseInt(parametersKeel[12]);
        shift= Integer.parseInt(parametersKeel[13]);; // 5% de la mitad del tamaño de la etiqueta para el desplazamiento
        clasificacion= Integer.parseInt(parametersKeel[14]); // indica si realiza clasificación(1) o regresion(0)
        alpha= Double.parseDouble(parametersKeel[27]); // indica si realiza clasificación(1) o regresion(0)
//        beta= Double.parseDouble(parametersKeel[28]); // indica si realiza clasificación(1) o regresion(0)
        if ((alpha + (1-alpha)) != 1){
            alpha= 0.5;
        }         
        fileCostMatrix= parametersKeel[28];
        // el resto de parámetros que corresponden a las probabilidades de 
        // inicialización, cruce y mutación de las subpoblaciones y de cada 
        // elemento de la subpoblación son consideradas en la inicialización de la población
        
    }
    
    public static void initParametersTFG(String[] param){

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
    
    /**
     * execute the learning
     * @param configFile file of configuration (keel format)
    // NON USED * @param cuda 0: don't use cuda lenguage, 1: use cuda lenguage with nvidia peripherals
     * @param homogeneousLabel 1: label must be homogeneous, 0: label must cover a porcent of examples (no homogeneous)
     * @throws InterruptedException 
     */
    public static void executeNSLVOrd(String configFile, int homogeneousLabel) throws InterruptedException {
        
        // Aquí se inicializa Random
        randomNum= new Random[numDesplazamientos];
        iter= new int[numDesplazamientos];
        time= new double[numDesplazamientos];
        for (int i=0; i < numDesplazamientos; i++){
          randomNum[i]= new Random(seed);          
        }

        // obtener las instancias (ejemplos) de training y test y pasarlas a "los objetos de mis clases"
        iSet= new InstanceSet();
        tSet= new InstanceSet();
        try{
          iSet.readSet(fileTrain,true);
          iSet.setAttributesAsNonStatic();          
          tSet.readSet(fileTest,false);          
          tSet.setAttributesAsNonStatic();          
        }catch (DatasetException e){
            System.out.println (""+"-"+"\n\n>>>Error to read training/test files");
            e.printAllErrors();
        }catch (HeaderFormatException e2){
            System.err.println (""+"-"+"Exception in header format: "+e2.getMessage());
        }
        
        //si no hay ejemplos sale directamente
        if (iSet.getNumInstances() == 0){
          System.out.println(""+"-"+"\n\t\tNO HAY EJEMPLOS");
          return;
        }

// parte de ejecución en serie        
        //Cargar la variable del problema
//        int direction=-1; // -1 -> izqda, 1 -> dcha, 0 -> no hay desplazamiento
//        for (int i=0; i < numDesplazamientos; i++){          
//          executeLearning(shift, direction, i, cuda);
//          direction++;
//        }
        randomNum[0]= new Random(seed);
//        executeLearning(0, 0, 0, cuda); // para probar por ahora nada más que con una ejecución
        executeLearning(0, 0, 0, homogeneousLabel); // para probar por ahora nada más que con una ejecución

// FIN - parte de ejecución en serie                

        
/*       
// parte de ejecución en paralelo
        Thread t1= new Thread(new ExecuteLearning(-1,0, cuda));
        Thread t2= new Thread(new ExecuteLearning(0,1, cuda));
        Thread t3= new Thread(new ExecuteLearning(1,2, cuda));
        
//        t1.start();
        t2.start();
//        t3.start();
        
        System.out.println("Waiting for MessageLoop thread to finish");
        DebugClass.writeResFile(fileResultDebug ,"\nWaiting for MessageLoop thread to finish\n");          
        
        while (t1.isAlive() || t2.isAlive() || t3.isAlive()) {
            System.out.println("Still waiting... (1seg)");
            // Wait maximum of 1 second
            // for MessageLoop thread
            // to finish.            
            t1.join(1000);
            t2.join(1000);
            t3.join(1000);
        }
        System.out.println("Finally!");
        DebugClass.writeResFile(fileResultDebug ,"\nFinally\n");          
// FIN - parte de ejecución en paralelo
*/
    }
    
    public static boolean ReadSetTFG(String[] _header, String[] _datas, boolean _train){
        // obtener las instancias (ejemplos) de training y test y pasarlas a "los objetos de mis clases"
        InstanceSet _Set= new InstanceSet();
        
        _Set.readSetTFG(_header,_datas,_train);
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
    
    public static boolean executeNSLVOrdTFG(int homogeneousLabel){
        // parte de ejecución en serie       
        randomNum[0]= new Random(seed);
        return executeLearningTFG(0, 0, 0, homogeneousLabel); // para probar por ahora nada más que con una ejecución
        // FIN - parte de ejecución en serie  
    }
    
    /**
     * execute the predict
     * @param configFile file of configuration (keel format)
     * @throws InterruptedException 
     */
    public static void executeNSLVOrdPredict(String configFile) throws InterruptedException {
        
        iter= new int[numDesplazamientos];
        time= new double[numDesplazamientos];

        // obtener las instancias (ejemplos) de training y test y pasarlas a "los objetos de mis clases"
        // Ojo, se debe dejar la carga de los ejemplos de entrenamiento porque si no da una excepción (de la parte que no es mia)
        iSet= new InstanceSet(); 
        tSet= new InstanceSet();
        try{
          iSet.readSet(fileTrain,true);
          iSet.setAttributesAsNonStatic();          
          tSet.readSet(fileTest,false);          
          tSet.setAttributesAsNonStatic();          
        }catch (DatasetException e){
            System.out.println (""+"-"+"\n\n>>>Error to read training/test files");
            e.printAllErrors();
        }catch (HeaderFormatException e2){
            System.err.println (""+"-"+"Exception in header format: "+e2.getMessage());
        }
        
        //si no hay ejemplos sale directamente
        if (tSet.getNumInstances() == 0){
          System.out.println(""+"-"+"\n\t\tNO HAY EJEMPLOS");
          return;
        }

// parte de ejecución en serie        
        //Cargar la variable del problema
//        int direction=-1; // -1 -> izqda, 1 -> dcha, 0 -> no hay desplazamiento
//        for (int i=0; i < numDesplazamientos; i++){          
//          executeLearning(shift, direction, i, cuda);
//          direction++;
//        }
        executePredict(0, 0, 0); // para probar por ahora nada más que con una ejecución

// FIN - parte de ejecución en serie                

        
/*       
// parte de ejecución en paralelo
        Thread t1= new Thread(new ExecutePredict(-1,0, cuda));
        Thread t2= new Thread(new ExecutePredict(0,1, cuda));
        Thread t3= new Thread(new ExecutePredict(1,2, cuda));
        
//        t1.start();
        t2.start();
//        t3.start();
        
        System.out.println("Waiting for MessageLoop thread to finish");
        DebugClass.writeResFile(fileResultDebug ,"\nWaiting for MessageLoop thread to finish\n");          
        
        while (t1.isAlive() || t2.isAlive() || t3.isAlive()) {
            System.out.println("Still waiting... (1seg)");
            // Wait maximum of 1 second
            // for MessageLoop thread
            // to finish.            
            t1.join(1000);
            t2.join(1000);
            t3.join(1000);
        }
        System.out.println("Finally!");
        DebugClass.writeResFile(fileResultDebug ,"\nFinally\n");          
// FIN - parte de ejecución en paralelo
*/
    }

    public static boolean executeNSLVOrdPredictTFG() {
        // parte de ejecución en serie        
        return executePredictTFG(0, 0, 0); // para probar por ahora nada más que con una ejecución
        // FIN - parte de ejecución en serie
    }

    /**
    * Devuelve una variable con la sintaxis de la aplicación
    * @param applicationName -> nombre de la aplicación
    * @return sintaxis de la aplicación junto con ejemplos de llamadas a la misma
    */
    private static String writeSyntax(String applicationName){

        String syntax;

        syntax= "syntax: \n";
        syntax= syntax + "java -jar " + applicationName + " configFile(keelFormat) [FromMail passwordMail ToMail] \n";

        syntax= syntax + "\n   Example: " + "java -jar " + applicationName + " configKeelExample.txt \n";
        syntax= syntax + "\n   Example: " + "java -jar " + applicationName + " configKeelExample.txt jcgamez.uco.es@gmail pass jcgamez@uco.es\n";

        return syntax;
    }
    
    /**
    * Chequea la sintaxis de la aplicación devolviendo -1 si hay errror (1 en caso contrario)
    * @param argc -> número de argumentos
    * @param argv -> matriz de argumentos
    * @param sintax -> la sintax de la aplicación
    * @return String con [0]=DomainFile, [1]=DataFile, [2]= GeneticFile,
     *        [3]= DataFileName, [4]=particiones. 
     *          Si ERROR -> [0]="", [1]=MENSAJE ERROR;
    * @note DomainFile -> nombre del fichero del dominio
    * @note DataFile -> nombre del fichero de datos
    * @note GeneticFile -> nombre del fichero de parámetros del algoritmo genético
    * @note numParticiones -> numero de particiones de los ejemplos
    * @note DataFileName -> nombre del fichero de datos sin extensión (para cuando hay más de un fichero de datos)
    * @deprecated not used in this version
    */
    private static String[] checkSyntax(int argc, String argv[], String sintax){

        String fileAuxString;
        String numAuxString="0";
        String[] FileNamesArguments= new String[5];
        String DomainFile="", DataFileName="", DataFile="", GeneticFile= "", particiones="0";
        int numParticiones=0;

        FileNamesArguments[0]= DomainFile;
        FileNamesArguments[1]= DataFile;
        FileNamesArguments[2]= GeneticFile;
        FileNamesArguments[3]= DataFileName;
        FileNamesArguments[4]= particiones;

        if (argc < 1 || argc > 2){
            FileNamesArguments[1]= "SINTAX ERROR - argument number: ";
            FileNamesArguments[1]= FileNamesArguments[1] + argc;
            FileNamesArguments[1]= FileNamesArguments[1] + "\n" + sintax;
            return FileNamesArguments;
        }

        // asginación de parámetros a variables
        DataFileName= argv[0];
        DomainFile= DataFileName + ".dom";
        DataFile= DataFileName + ".datos";
        GeneticFile= DataFileName + ".genetic";
        
        if (argc == 2){
            particiones= argv[1];
            numParticiones= Integer.parseInt(particiones);
        }
        
        //comprobación de existencia de ficheros
        File f= new File(DomainFile);
        if (!f.exists()){
            System.out.println("ERROR: file " + DomainFile);
            return FileNamesArguments;
        }
       
        f= new File(GeneticFile);
        if (!f.exists()){
            System.out.println("ERROR: file " + GeneticFile);
            return FileNamesArguments;
        }

        if (numParticiones != 0){
            for (int i=0; i < numParticiones;i++){
                String aux= DataFileName + i + ".datos";
                
                f= new File(aux);
                if (!f.exists()){
                    System.out.println("ERROR: file " + aux);
                    return FileNamesArguments;                    
                }
            }
        }
        else{
            f= new File(DataFile);
            if (!f.exists()){
                System.out.println("ERROR: file " + DataFile);
                return FileNamesArguments;
            }            
        }
                
        FileNamesArguments[0]= DomainFile;
        FileNamesArguments[1]= DataFile;
        FileNamesArguments[2]= GeneticFile;
        FileNamesArguments[3]= DataFileName;
        FileNamesArguments[4]= particiones;
                
        return FileNamesArguments;
    }

    /**
     * get the parameters in keel format
     * @param fileParameters file of parameters in keel format
     * @return vector of parameters
     */
    public static String[] getParametersKeel(String fileParameters){
      int numParameters= 6+23; // 6 ficheros + 18 parámetros (1+1+1+1+1+ 4*3 +2)
      String[] salida= new String[numParameters];

      // leer el fichero de configuración 
      try{
        BufferedReader reader = new BufferedReader(new FileReader(fileParameters));
        String line = null;
        StringTokenizer lineToken;
        String[] tokens, tokensFile;
        String aux;
        line= reader.readLine(); // la primera linea contiene el algoritmo -> se descarta
        tokens= line.split(" = ");
        line= reader.readLine();   // la segunda línea tiene los ficheros de entrada
        tokens= line.split("\"");
        salida[0]= tokens[1]; // fichero de training
        salida[1]= tokens[3]; // fichero de validación
        salida[2]= tokens[5]; // fichero de test
        line= reader.readLine();   // la tercera línea tiene los ficheros de salida
        tokens= line.split("\"");
        salida[3]= tokens[1]; // fichero de salida de training
        salida[4]= tokens[3]; // fichero de salida de test
        salida[5]= tokens[5]; // fichero de salida del programa (personal)
        int s=6;
        while ((line = reader.readLine()) != null) {// a partir de la 4º linea tiene los parámetros
          tokens= line.split(" = ");
          tokensFile= line.split("\"");
          if (tokens.length == 2){
              if (tokensFile.length == 2){
                salida[s]=tokensFile[1];
              }
              else{
                salida[s]= tokens[1];
              }
            s++;
          }
        }
        
      } catch (IOException e) {
       System.out.println("File I/O error! - procesando parámetros");
      }      
      
      return salida;
    }

    /**
     * get the Cost Matrix for fitness function
     * @param fileCostMatrix file of cost matrix
     * @return matrix of cost
     */
    public static double[][] getCostMatrix(String fileCostMatrix, int numClases){
        double[][] matrix;
        int numClasesFile=0;

      // leer el fichero 
      try{
        BufferedReader reader = new BufferedReader(new FileReader(fileCostMatrix));
        String line = null;
//        StringTokenizer lineToken;
        String[] tokens;

        String aux;
        line= reader.readLine(); // la primera linea contiene el número de clases
        numClasesFile= Integer.parseInt(line);
        if (numClasesFile != numClases){
            System.out.println(fileCostMatrix+" numClasesFile != numClasses -> ERROR COST MATRIX");
            return null;            
        }
        matrix= new double[numClases][numClases];

        int i=0;
        while ((line = reader.readLine()) != null) {// a partir de la 4º linea tiene los parámetros
            if (i >= numClases){ // numFilas != numClases
                System.out.println(fileCostMatrix+" numRows != numClasses -> NO COST MATRIX");
                return null;
            }
            tokens= line.split(";");
            if (tokens.length != numClases){ // numColumnas != numClases
                System.out.println(fileCostMatrix+" numColumns != numClasses -> NO COST MATRIX");
                return null;
            }
            for (int j=0; j < numClases; j++){
                matrix[i][j]= Double.parseDouble(tokens[j]);                
            }
            i++;
        }
        if (i != numClases){ // numFilas != numClases
            System.out.println(fileCostMatrix+" numRows != numClasses -> NO COST MATRIX");
            return null;
        }
        
      } catch (IOException e) {
       System.out.println(fileCostMatrix+" NOT EXIST OR NOT VALID FORMAT -> NO COST MATRIX");
       return null;
      }      
      
      return matrix;
    }
    

    
    /**
     * Execute learning. The core of application. It's ready to execute with threads
     * Store information in fileResultDebug+index file
     * @param shift indicate the % of shitft from original intervals
     * @param direction 1 -> shift in right way, -1 -> shift in left way
     * @param index of vector of shift (0->left, indice 1->center, indice 2->right)
    // NON USED  * @param cuda 0: don't use cuda lenguage, 1: use cuda lenguage with nvidia peripherals
     * @param homogeneousLabel 1: label must be homogeneous, 0: label must cover a porcent of examples (no homogeneous)
     */
    public static void executeLearning(int shift, int direction, int index, int homogeneousLabel){    
    
          String auxString="";
          int numRules;       // numero de reglas de la particion
          double varXRule;    // media de numero de variables por regla
          long initTime, endTime;

          iter[index]=0;
          // comienza a contar el tiempo
          initTime= System.currentTimeMillis();

            Date date = new java.util.Date();
            SimpleDateFormat sdf=new java.text.SimpleDateFormat("EEE, d MMM yyyy - HH:mm:ss");
            auxString+="==================================\n";
            auxString+="==================================\n";
            auxString= "\t\t\t\t EXECUTION: " + index + "\n";
            auxString+="==================================\n";
            auxString+="==================================\n";
            auxString+= "\nExperiment: " + fileResultDebug + "\n";
            auxString+= "\nDATE: " + sdf.format(date) + "\n";
            auxString+= "\n====================================\n";
            System.out.println(""+"-"+auxString);        
/*
            if (DebugClass.writeResFile(fileResultDebug+index, auxString) == -1){
                System.out.println(""+"-"+"ERROR: writeResFile(" + auxString + ")");
                String aux="NSLVOrd.executeLearning \n\n"+"ERROR: writeResFile "+ auxString;
                DebugClass.sendMail=1;
                DebugClass.cuerpoMail+= "\n" + aux;
                DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//                DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
                System.exit(-1);           
            }
*/
          if (numLabelsInputs == -1 || numLabelsOutput == -1){
            // constructor para la creación de etiquetas no homogéneas (en función del número de individuos por etiqueta)
//            fuzzyProblem[index]= new FuzzyProblemClass(dupLabels, iSet, shift, direction, clasificacion);
            
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
          E_par_test[index]= new ExampleSetProcess(fuzzyProblem[index], tSet);
          String result= E_par[index].calcAdaptExVarLab();
          if (result.compareTo("") != 0){
              System.out.println(""+"-"+"ERROR: calcAdaptExVarLab of E_par");
              System.out.println(""+"-"+"ERROR RESULT: " + result);
              String aux="NSLVOrd.executeLearning \n\n"+"ERROR: calcAdaptExVarLab of E_par"+ result;
              DebugClass.sendMail=1;
              DebugClass.cuerpoMail+= "\n" + aux;
              DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//              DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
              System.exit(-1);                                
          } 
          result= E_par_test[index].calcAdaptExVarLab();
          if (result.compareTo("") != 0){
              System.out.println(""+"-"+"ERROR: calcAdaptExVarLab of E_par_test");
              System.out.println(""+"-"+"ERROR RESULT: " + result);
              String aux="NSLVOrd.executeLearning \n\n"+"ERROR: calcAdaptExVarLab of E_partest"+ result;
              DebugClass.sendMail=1;
              DebugClass.cuerpoMail+= "\n" + aux;
              DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//              DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
              System.exit(-1);                                
          } 

          // calcular las medidas de información para agilizar los cálculos
          E_par[index].calcInformationMeasures();
//          E_par_test[index].calcInformationMeasures();

          // get and print costMatrix
          if (clasificacion == 1){
              costMatrix= getCostMatrix(fileCostMatrix, E_par[index].getProblemDefinition().numLinguisticTermOfConsequent());
          }       
          // print cost matrix
          DebugClass.printCostMatrix(fileResultDebug,costMatrix,1,1);


          // imprimir parámetros del problema por pantalla
          auxString= DebugClass.printFuzzyProblem(fuzzyProblem[index], E_par[index], E_par_test[index]);
          System.out.println(""+"-"+auxString);
          DebugClass.writeResFile(fileResultDebug, auxString);
          auxString= "\n\n\tCONJUNTO DE EJEMPLOS (train): " + E_par[index].getNumExamples() + "";
          auxString+= "\n \tCONJUNTO DE EJEMPLOS (test) : " + E_par_test[index].getNumExamples() + "\n\n";
          System.out.println(""+"-"+auxString);
          DebugClass.writeResFile(fileResultDebug, auxString);
          auxString= DebugClass.printInformationMeasures(E_par[index]);
          System.out.println(""+"-"+auxString);
          DebugClass.writeResFile(fileResultDebug, auxString);

//DebugClass.printResumeExampleSet(fileResultDebug+index, E_par[index], 1, 1, 0, E_par[index].getNumExamples());
//DebugClass.printAdaptExVarLabCod(fileResultDebug+index, E_par[index], 1, 1);
//DebugClass.printAdaptExVarLab(fileResultDebug+index, E_par[index], 1, 1);

          // crear el objeto para el algoritmo genético
          R[index]= new RuleSetClass(alpha);        

          //creación del objeto genético e inicializarlo
          GeneticAlgorithmClass GA= new GeneticAlgorithmClass(parametersKeel, E_par[index]); 
          // inicializar la población
          GA.initPopulation(randomNum[index],E_par[index],costMatrix);            
          auxString= DebugClass.printPopulationParameters(GA.getP(),E_par[index]);
          System.out.println(""+"-"+auxString);
          DebugClass.writeResFile(fileResultDebug, auxString);

          // BEGIN - aquí comenzaría el bloque de ejecuciones del algoritmo genético
          Util.initStatisticalData(GA.getP(), fuzzyProblem[index]);

          // calcular la nueva regla
          int ejemplosCubiertos=0, eliminadoReglas=0, newRule=1;
          Util.timeInitGenetic= System.currentTimeMillis();
//          int newRule= GA.findNewRule(randomNum[index], cuda);
          Util.numIterGenetic++;

          //// AQUÍ PARA AÑADIR O NO LA REGLA POR DEFECTO AL COMIENZO ... -> ESTO NO SE MODIFICARÁ LUEGO --> HABRÁ QUE TENERLO EN CUENTA EN LA PARTE DE REGRESIÓN
          int addDefaultRule=0;
          Util.classDefaultRule= GA.setDefaultRule(addDefaultRule,E_par[index],R[index]);
///auxString=(""+"-"+"\n\nsetDefaultRule");
          if (addDefaultRule == 1){ // Sí se ha includo la regla por defecto al principio.
            ejemplosCubiertos= E_par[index].calcCovered(R[index],GA.getP(), fuzzyProblem[index]);
            auxString=(""+"-"+"\n\nCalcCovered: cubiertos " + ejemplosCubiertos + " de " + E_par[index].getNumExamples());              
          }
//        DebugClass.printPopulationElementFitness(fileResultDebug+index,population[index],1,1,E_par[index],0);
//        DebugClass.printMetrics(fileResultDebug+index, R[index], 1, 1);
          

          eliminadoReglas= 1;
          while (eliminadoReglas == 1){
            while (newRule == 1 && ejemplosCubiertos < E_par[index].numExamples){  
              iter[index]++;
//              newRule= GA.findNewRule(randomNum[index],cuda,E_par[index],R[index]);
              newRule= GA.findNewRule(randomNum[index],0,E_par[index],R[index]); // en la versión de homogeneousLabel se ha eliminado la opción de cuda
              Util.numIterGenetic++;
  auxString=(""+"-"+"\n\nfindNewRule");
//  System.out.print(auxString);

              ejemplosCubiertos= E_par[index].calcCovered(R[index],GA.getP(),fuzzyProblem[index]);
  auxString=(""+"-"+"\n\nCovered: " + ejemplosCubiertos + " / " + E_par[index].getNumExamples()+"\n");
  System.out.print(auxString);
//  DebugClass.writeResFile(fileResultDebug+index, auxString);
//  DebugClass.printResumeRuleSet(fileResultDebug+index ,R[index],1,1,E_par[index]);
//  DebugClass.printPopulationElementFitness(fileResultDebug+index,population[index],1,1,E_par[index],0);
//  DebugClass.printMetrics(fileResultDebug+index, R[index], 1, 1);
  //DebugClass.printResumeExampleSet(fileResultDebug+index, E_par[index], 1, 1, 0, E_par[index].getNumExamples());

            }//while (newRule == 1){  
              eliminadoReglas= R[index].removeRulesForImproveMetric(E_par[index],GA, fuzzyProblem[index]); // probar a quitar reglas y ver si mejora la precisión
              if (eliminadoReglas == 1){
                newRule=1;
  auxString="\t\t\t removeRulesForImproveMetric() ";          
//  DebugClass.writeResFile(fileResultDebug+index, auxString);
//  DebugClass.printResumeRuleSet(fileResultDebug+index ,R[index],1,1,E_par[index]);
//  DebugClass.printPopulationElementFitness(fileResultDebug+index,population[index],1,1,E_par[index],0);
//  DebugClass.printMetrics(fileResultDebug+index, R[index], 1, 1);              
  //DebugClass.printResumeExampleSet(fileResultDebug+index, E_par[index], 1, 1, 0, E_par[index].getNumExamples());
    ejemplosCubiertos= E_par[index].calcCovered(R[index], GA.getP(), fuzzyProblem[index]);
  auxString=(""+"-"+"\n\nCalcCovered (después de removeRulesForImproveMetrics): cubiertos " + ejemplosCubiertos + " de " + E_par[index].getNumExamples());
//  System.out.print(auxString);
//  DebugClass.writeResFile(fileResultDebug+index, auxString);
//  DebugClass.printResumeRuleSet(fileResultDebug+index ,R[index],1,1,E_par[index]);
//  DebugClass.printPopulationElementFitness(fileResultDebug+index,population[index],1,1,E_par[index],0);
  //auxString= DebugClass.printUnderstableRuleSet(R[index]);
  //System.out.print(auxString);
  //DebugClass.writeResFile(fileResultDebug+index, auxString);
//  DebugClass.printMetrics(fileResultDebug+index, R[index], 1, 1);
  //DebugClass.printResumeExampleSet(fileResultDebug+index, E_par[index], 1, 1, 0, E_par[index].getNumExamples());
  }
          }// while (eliminadoReglas != 1){
          
          
          if (addDefaultRule == 0){ // No se ha includo la regla por defecto al principio. Se debe incluir al final
              R[index].addRule(Util.DefaultRule, Util.DefaultRule.getRealMatrix(Util.classDefaultRule, 4), E_par[index]);
          }
          
          
          Util.timeGenetic+= (System.currentTimeMillis() - Util.timeInitGenetic);
          // END - aquí finaliza el bloque de ejecuciones del algoritmo genético
          auxString="\n\t\t\t\t\t\t\tFIN DEL WHILE (NEWRULE == 1) \n";
//          System.out.println(""+"-"+auxString);
//          DebugClass.writeResFile(fileResultDebug+index , auxString);

          // calcular el número de ejemplos que cubre cada regla en training y test, así como las reglas que cubren los ejemplos
//          int auxIndexRule=0;
//          int auxNumRules= R[index].getNumRules();
//          int auxNumFitness= population[index].getIndividuals(0).getNumFitness();
//          int auxClassR=0;
//          int[] auxExamplesPosTrain= new int[auxNumRules];
//          int[] auxExamplesPosTest= new int[auxNumRules];
//          int[] auxExamplesNegTrain= new int[auxNumRules];
//          int[] auxExamplesNegTest= new int[auxNumRules];
//          for (int i=0; i < auxNumRules; i++){
//            auxExamplesPosTrain[i]=0;
//            auxExamplesPosTest[i]=0;
//            auxExamplesNegTrain[i]=0;
//            auxExamplesNegTest[i]=0;
//          }
////          for (int i=0; i < E_par[index].getNumExamples(); i++){
////            auxIndexRule= R[index].inference(E_par[index],i);
////            int CLASE_R = (int) R[index].getRules(auxIndexRule).getIntegerMatrix(0,0);
////            int CLASE_E=  (int) E_par[index].getData(i, E_par[index].getProblemDefinition().getConsequentIndexOriginal());
////            if ((int) R[index].getRules(auxIndexRule).getIntegerMatrix(0,0) == (int) E_par[index].getData(i, E_par[index].getProblemDefinition().getConsequentIndexOriginal())){
////                auxExamplesPosTrain[auxIndexRule]++;            
////            }
////            else{
////                auxExamplesNegTrain[auxIndexRule]++;
////            }
////            E_par[index].setIndexRuleCovered(i, auxIndexRule);
////          }
////          for (int i=0; i < E_par_test[index].getNumExamples(); i++){
////            auxIndexRule= R[index].inference(E_par_test[index],i);
////            if ((int) R[index].getRules(auxIndexRule).getIntegerMatrix(0,0) == (int) E_par_test[index].getData(i, E_par_test[index].getProblemDefinition().getConsequentIndexOriginal())){
////                auxExamplesPosTest[auxIndexRule]++;            
////            }
////            else{
////                auxExamplesNegTest[auxIndexRule]++;
////            }
////            E_par_test[index].setIndexRuleCovered(i, auxIndexRule);
////          }
////          for (int i=0; i < auxNumRules; i++){
////            auxClassR= R[index].getRules(i).getIntegerMatrix(0,0);
////            R[index].getRules(i).setRealMatrix(2+auxClassR, auxNumFitness, auxExamplesPosTrain[i]);
////            R[index].getRules(i).setRealMatrix(2+auxClassR, auxNumFitness+1, auxExamplesNegTrain[i]);
////            R[index].getRules(i).setRealMatrix(2+auxClassR, auxNumFitness+2, auxExamplesPosTest[i]);
////            R[index].getRules(i).setRealMatrix(2+auxClassR, auxNumFitness+3, auxExamplesNegTest[i]);
////          }
////          for (int r=0; r < auxNumRules; r++){
////              int numPosTra=0;
////              int numNegTra=0;
////              int numPosTest=0;
////              int numNegTest=0;
////              for (int e=0; e < E_par[index].getNumExamples(); e++){
//////            auxIndexRule= R[index].inference(E_par_test[index],i);
//////            if ((int) R[index].getRules(auxIndexRule).getIntegerMatrix(0,0) == (int) E_par_test[index].getData(i, E_par_test[index].getProblemDefinition().getConsequentIndexOriginal())){
//////                auxExamplesPosTest[auxIndexRule]++;            
//////            }
//////            else{
//////                auxExamplesNegTest[auxIndexRule]++;
//////            }
//////            E_par_test[index].setIndexRuleCovered(i, auxIndexRule);
////                  
////              }
////            auxClassR= R[index].getRules(i).getIntegerMatrix(0,0);
////            R[index].getRules(i).setRealMatrix(2+auxClassR, auxNumFitness, auxExamplesPosTrain[i]);
////            R[index].getRules(i).setRealMatrix(2+auxClassR, auxNumFitness+1, auxExamplesNegTrain[i]);
////            R[index].getRules(i).setRealMatrix(2+auxClassR, auxNumFitness+2, auxExamplesPosTest[i]);
////            R[index].getRules(i).setRealMatrix(2+auxClassR, auxNumFitness+3, auxExamplesNegTest[i]);
////          }
          
          auxString=(""+"-"+"\n\n RULES LATER GET NUM EXAMPLES COVERED BY A RULE");
//          System.out.print(auxString);
//          DebugClass.writeResFile(fileResultDebug, auxString);
//          DebugClass.printResumeRuleSet(fileResultDebug ,R[index],1,1,E_par[index]);

          auxString=(""+"-"+"\n\n TRAIN EXAMPLES WITH RULE COVERED");
          System.out.print(auxString);
          DebugClass.writeResFile(fileResultDebug, auxString);
          DebugClass.printResumeExampleSet(fileResultDebug, E_par[index], 1, 1, 0, E_par[index].getNumExamples(),R[index]);

          auxString=(""+"-"+"\n\n TEST EXAMPLES WITH RULE COVERED");
//          System.out.print(auxString);
//          DebugClass.writeResFile(fileResultDebug+index, auxString);
//          DebugClass.printResumeExampleSet(fileResultDebug+index, E_par_test[index], 1, 0, 0, E_par_test[index].getNumExamples(),R[index]);
          // FIN - calcular el número de ejemplos que cubre cada regla en training y test, así como las reglas que cubren los ejemplos

          auxString=(""+"-"+"\n\n Rules and examples with adaptation of antecedent");
          System.out.println(auxString);
          DebugClass.writeResFile(fileResultDebug, auxString);
          DebugClass.printRuleExamples(fileResultDebug, E_par[index],1,1, E_par[index].getNumExamples(),R[index]);
          
          auxString=(""+"-"+"\n\n Example and fired rule");
          System.out.println(auxString);
          DebugClass.writeResFile(fileResultDebug, auxString);
          DebugClass.printExamplesFiredRule(fileResultDebug, E_par[index],1,1, E_par[index].getNumExamples(),R[index]);
          
          
          // Imprimir reglas
//          DebugClass.printResumeRuleSet(fileResultDebug+index,R[index],1,1,E_par[index]);            
          auxString= DebugClass.printUnderstableRuleSet(R[index], fuzzyProblem[index]);
          System.out.println(""+"-"+auxString);
//          if (Util.classDefaultRule != -1){
//              int conseqIndex= E_par[0].getProblemDefinition().consequentIndex();
//              auxString= "DEFAULT RULE: \n\tIF ... THEN "+E_par[0].getProblemDefinition().getFuzzyLinguisticVariableList(conseqIndex).getName()
//                         +" is "+E_par[0].getProblemDefinition().getFuzzyLinguisticVariableList(conseqIndex).getFuzzyLinguisticTermList(Util.classDefaultRule).getName()+"\n\n";
//          }
//          System.out.println(""+"-"+auxString);
//          DebugClass.writeResFile(fileResultDebug+index, auxString);        

          //actualizar variables para estadística de la particion
          numRules= R[index].getNumRules();
          varXRule= R[index].calcVarXRule();
          endTime= System.currentTimeMillis();
          time[index]= endTime - initTime;

          // calcular las métricas para modificar la base de reglas
          double auxCCR= R[index].CCR;
          double auxOMAE= R[index].OMAE;
          
          String resultado= Util.calcMetrics(R[index], E_par[index], fuzzyProblem[index]);

          if (!resultado.equals("")){
            System.out.println("Ha habido un error en el cálculo de las métricas");
            System.out.println(resultado);
            String aux="NSLVOrd.executeLearning \n\n"+"ERROR: calcMetrics"+ resultado;
            DebugClass.sendMail=1;
            DebugClass.cuerpoMail+= "\n" + aux;
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
            System.exit(-1);
          }
          //comprobación de que las métricas están bien calculadas en el aprendizaje
          if ( Math.abs(auxCCR - R[index].CCR) > 0.0001 || Math.abs(auxOMAE - R[index].OMAE) > 0.0001){
            String aux="\n ERROR: Metricas no bien calculadas \n" + 
                    "; CCR: " + auxCCR + " R[index].CCR: " + R[index].CCR + 
                    "; OMAE: " + auxOMAE + " R[index].OMAE: " +R[index].OMAE ;
            System.out.println(aux);
//            DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
            DebugClass.sendMail=1;
            DebugClass.cuerpoMail+= "\n" + aux;            
          }
          
          //calcular métricas para el conjunto de test
          RuleSetClass auxR = new RuleSetClass(R[index]);
          // calcular las métricas para modificar la base de reglas
          resultado= Util.calcMetrics(auxR, E_par_test[index], fuzzyProblem[index]);

          if (!resultado.equals("")){
            System.out.println("Ha habido un error en el cálculo de las métricas");
            System.out.println(resultado);
            String aux="NSLVOrd.executeLearning \n\n"+"ERROR: calcMetrics"+ resultado;
            DebugClass.sendMail=1;
            DebugClass.cuerpoMail+= "\n" + aux;
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
            System.exit(-1);
          }
          // imprimir matrices de confusion
          System.out.println("METRICAS DE TRAINING");
//          DebugClass.writeResFile(fileResultDebug+index, "METRICAS DE TRAINING\n");
          DebugClass.printMetrics(fileResultDebug+index, R[index], 1, 0);         
          System.out.println("METRICAS DE TEST");
//          DebugClass.writeResFile(fileResultDebug+index, "METRICAS DE TEST\n");                  
          DebugClass.printMetrics(fileResultDebug+index, auxR, 1, 0);              
          // Imprimir valores estadísticos
          auxString= DebugClass.printStatisticalDataExample("CCR",R[index].CCR, 
                  auxR.CCR,numRules,varXRule,iter[index],time[index],fuzzyProblem[index]);            
          auxString+= DebugClass.printStatisticalDataExample("OMAE",R[index].OMAE, 
                  auxR.OMAE,numRules,varXRule,iter[index],time[index],fuzzyProblem[index]);            
//          auxString+= DebugClass.printStatisticalDataExample("OMAENormalizado",R[index].OMAENormalizado, 
//                  auxR.OMAENormalizado,numRules,varXRule,iter[index],time[index],fuzzyProblem[index]);            
          auxString+= DebugClass.printStatisticalDataExample("myMetric",R[index].metric, 
                  auxR.metric,numRules,varXRule,iter[index],time[index],fuzzyProblem[index]);            
//          auxString+= DebugClass.printStatisticalDataExample("myMetricMedia",R[index].metricMedia, 
//                  auxR.metricMedia,numRules,varXRule,iter[index],time[index],fuzzyProblem[index]);            

//          auxString+= DebugClass.printStatisticalDataLearning();

          System.out.println(""+"-"+auxString);
//          DebugClass.writeResFile(fileResultDebug+index, auxString);            

//DebugClass.sendMail("asunto","cuerpo","../results.txt0");
//DebugClass.sendMail("asunto","cuerpo","");
          
          // para sacar la pantalla de forma que "entienda" xfuzzy (lo que utiliza Andrés)
//          DebugClass.printForXfuzzy(fileResultDebug+index, R[index]);
    }

    public static boolean executeLearningTFG(int shift, int direction, int index, int homogeneousLabel){    
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

    /**
     * Execute predict. The core of application. It's ready to execute with threads
     * Store information in fileResultDebug+index file
     * @param shift indicate the % of shitft from original intervals
     * @param direction 1 -> shift in right way, -1 -> shift in left way
     * @param index of vector of shift (0->left, indice 1->center, indice 2->right)
     */
    public static void executePredict(int shift, int direction, int index){    
    
          String auxString="";
          int numRules;       // numero de reglas de la particion
          double varXRule;    // media de numero de variables por regla
          long initTime, endTime;

          iter[index]=0;
          // comienza a contar el tiempo
          initTime= System.currentTimeMillis();

            Date date = new java.util.Date();
            SimpleDateFormat sdf=new java.text.SimpleDateFormat("EEE, d MMM yyyy - HH:mm:ss");
            auxString+="==================================\n";
            auxString+="==================================\n";
            auxString= "\t\t\t\t EXECUTION: " + index + "\n";
            auxString+="==================================\n";
            auxString+="==================================\n";
            auxString+= "\nExperiment: " + fileResultDebug + "\n";
            auxString+= "\nDATE: " + sdf.format(date) + "\n";
            auxString+= "\n====================================\n";
            System.out.println(""+"-"+auxString);        
            if (DebugClass.writeResFile(fileResultDebug+index, auxString) == -1){
                System.out.println(""+"-"+"ERROR: writeResFile(" + auxString + ")");
                String aux="NSLVOrd.executePredict \n\n"+"ERROR: writeResFile"+ auxString;
                DebugClass.sendMail=1;
                DebugClass.cuerpoMail+= "\n" + aux;
                DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//                DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
                System.exit(-1);           
            }

          // pasar los ejemplos a "mis objetos"
          E_par[index]= new ExampleSetProcess(fuzzyProblem[index], iSet);
          E_par_test[index]= new ExampleSetProcess(fuzzyProblem[index], tSet);
          String result= E_par[index].calcAdaptExVarLab();
          if (result.compareTo("") != 0){
              System.out.println(""+"-"+"ERROR: calcAdaptExVarLab of E_par");
              System.out.println(""+"-"+"ERROR RESULT: " + result);
              String aux="NSLVOrd.executeLearning \n\n"+"ERROR: calcAdaptExVarLab of E_par"+ result;
              DebugClass.sendMail=1;
              DebugClass.cuerpoMail+= "\n" + aux;
              DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//              DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
              System.exit(-1);                                
          } 
          result= E_par_test[index].calcAdaptExVarLab();
          if (result.compareTo("") != 0){
              System.out.println(""+"-"+"ERROR: calcAdaptExVarLab of E_par_test");
              System.out.println(""+"-"+"ERROR RESULT: " + result);
              String aux="NSLVOrd.executeLearning \n\n"+"ERROR: calcAdaptExVarLab of E_partest"+ result;
              DebugClass.sendMail=1;
              DebugClass.cuerpoMail+= "\n" + aux;
              DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//              DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
              System.exit(-1);                                
          } 

          // imprimir parámetros del problema por pantalla
          auxString= DebugClass.printFuzzyProblem(fuzzyProblem[index], E_par[index], E_par_test[index]);
          System.out.println(""+"-"+auxString);
//          DebugClass.writeResFile(fileResultDebug+index, auxString);
          auxString= "\n\n\tCONJUNTO DE EJEMPLOS (train): " + E_par[index].getNumExamples() + "";
          auxString+= "\n \tCONJUNTO DE EJEMPLOS (test) : " + E_par_test[index].getNumExamples() + "\n\n";
          System.out.println(""+"-"+auxString);
//          DebugClass.writeResFile(fileResultDebug+index, auxString);

//DebugClass.printResumeExampleSet(fileResultDebug+index, E_par[index], 1, 1, 0, E_par[index].getNumExamples());
//DebugClass.printAdaptExVarLabCod(fileResultDebug+index, E_par[index], 1, 1);
//DebugClass.printAdaptExVarLab(fileResultDebug+index, E_par[index], 1, 1);


          //actualizar variables para estadística de la particion
          numRules= R[index].getNumRules();
          varXRule= R[index].calcVarXRule();
          endTime= System.currentTimeMillis();
          time[index]= endTime - initTime;

          // calcular métricas para el conjunto de training
          String resultado= Util.calcMetrics(R[index], E_par[index], fuzzyProblem[index]);

          if (!resultado.equals("")){
            System.out.println("Ha habido un error en el cálculo de las métricas");
            System.out.println(resultado);
            String aux="NSLVOrd.executeLearning \n\n"+"ERROR: calcMetrics"+ resultado;
            DebugClass.sendMail=1;
            DebugClass.cuerpoMail+= "\n" + aux;
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
            System.exit(-1);
          }
          
          //calcular métricas para el conjunto de test
          RuleSetClass auxR = new RuleSetClass(R[index]);
          // calcular las métricas para modificar la base de reglas
          resultado= Util.calcMetrics(auxR, E_par_test[index], fuzzyProblem[index]);

          if (!resultado.equals("")){
            System.out.println("Ha habido un error en el cálculo de las métricas");
            System.out.println(resultado);
            String aux="NSLVOrd.executeLearning \n\n"+"ERROR: calcMetrics"+ resultado;
            DebugClass.sendMail=1;
            DebugClass.cuerpoMail+= "\n" + aux;
            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//            DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
            System.exit(-1);
          }
          // Imprimir reglas
//          DebugClass.printResumeRuleSet(fileResultDebug+index,R[index],1,1,E_par[index]);            
          auxString= DebugClass.printUnderstableRuleSet(R[index], fuzzyProblem[index]);
          System.out.println(""+"-"+auxString);
//          DebugClass.writeResFile(fileResultDebug+index, auxString);        

          // imprimir matrices de confusion
          System.out.println("METRICAS DE TRAINING");
//          DebugClass.writeResFile(fileResultDebug+index, "METRICAS DE TRAINING\n");
//          DebugClass.printMetrics(fileResultDebug+index, R[index], 1, 1);         
          System.out.println("METRICAS DE TEST");
//          DebugClass.writeResFile(fileResultDebug+index, "METRICAS DE TEST\n");                  
//          DebugClass.printMetrics(fileResultDebug+index, auxR, 1, 1);              

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


          System.out.println(""+"-"+auxString);
//          DebugClass.writeResFile(fileResultDebug+index, auxString);            

//DebugClass.sendMail("asunto","cuerpo","../results.txt0");
//DebugClass.sendMail("asunto","cuerpo","");
          
          // para sacar la pantalla de forma que "entienda" xfuzzy (lo que utiliza Andrés)
//          DebugClass.printForXfuzzy(fileResultDebug+index, R[index]);
    }
    
    public static boolean executePredictTFG(int shift, int direction, int index){    
    
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
    
    /**
     * execute the inference (for threads)
     * @deprecated not use in this version
     */
    public static void executeInference(){
      
//        String auxString="";
//        SimpleDateFormat sdf=new java.text.SimpleDateFormat("EEE, d MMM yyyy - HH:mm:ss");
//      
//        // Imprimir las salidas para Keel (reagrupando resultados de las ejecuciones)        
//        double RMSETrain= 0;
//        double RMSETest= 0;
//        RMSETrain= DebugClass.printForKeel(fileResultTrain, E_par, R, numDesplazamientos);
//        RMSETest= DebugClass.printForKeel(fileResultTest, E_par_test, R, numDesplazamientos);
//
//        
//        auxString= "\nRMSE Train: " + RMSETrain + "\n";
//        auxString+= "RMSE Test: " + RMSETest + "\n";
//        auxString+= "Time (seconds): " + (time/1000.0)+"\n";        
//        auxString+= "Rules: ";
//        double rules=0;
//        for (int i=0; i < numDesplazamientos; i++){
//          auxString+= R[i].getNumRules() + " - ";
//          rules+= R[i].getNumRules();
//        }
//        auxString+= "\nMediaRules: " + rules/(double)numDesplazamientos + "\n";
//        System.out.println(""+"-"+auxString);        
//        if (DebugClass.writeResFile(fileResultDebug , auxString) == -1){
//            System.out.println("ERROR: writeResFile(" + auxString + ")");
//            System.exit(-1);           
//        }
//        
//        Date dateFin = new java.util.Date();
//        auxString= "\nExperiment: " + fileResultDebug + "\n";
//        auxString+= "\nDATE: " + sdf.format(dateFin) + "\n";
////        auxString+="\nTime: " + (time/1000.0)+ "\n";
//        auxString+= "\n====================================\n";
//        System.out.println(""+"-"+auxString);        
//        if (DebugClass.writeResFile(fileResultDebug , auxString) == -1){
//            System.out.println("ERROR: writeResFile(" + auxString + ")");
//            System.exit(-1);           
//        }
    }
    
    /**
     * load model of learning and print parameters (from fileResultTrain with .model extenssion)
     */
    public static void loadLearning(){
      try
      {
          // Se crea un ObjectInputStream
          ObjectInputStream ois = new ObjectInputStream(
                  new FileInputStream(fileResultTrain+".model"));

          Object aux;
          aux = ois.readObject();
          if (aux != null && aux instanceof FuzzyProblemClass[]){
            fuzzyProblem= (FuzzyProblemClass[]) aux;
          }
          else{
            System.out.println("ERROR al recuperar fuzzyProblem");
          }

//          aux = ois.readObject();
//          if (aux != null && aux instanceof ExampleSetProcess[]){
//            E_par= (ExampleSetProcess[]) aux;
//          }
//          else{
//            System.out.println("ERROR al recuperar E_par");
//          }
//
//          aux = ois.readObject();
//          if (aux != null && aux instanceof ExampleSetProcess[]){
//            E_par_test= (ExampleSetProcess[]) aux;
//          }
//          else{
//            System.out.println("ERROR al recuperar E_par_test");
//          }
//
//          aux = ois.readObject();
//          if (aux != null && aux instanceof PopulationClass[]){
//            population= (PopulationClass[]) aux;
//          }
//          else{
//            System.out.println("ERROR al recuperar population");
//          }

          aux = ois.readObject();
          if (aux != null && aux instanceof RuleSetClass[]){
            R= (RuleSetClass[]) aux;
          }
          else{
            System.out.println("ERROR al recuperar R");
          }
          
//          // Mientras haya objetos
//          int r=0;
//          while (aux!=null)
//          {
//              if (aux instanceof RuleSetClass){
//                R[r]= (RuleSetClass) aux;
//              }
//              aux = ois.readObject();
//              r++;
//          }
          ois.close();


//        double[] time={0,0,0};
//        int[] iter={0,0,0};
//        DebugClass.printModelLearning(fuzzyProblem, E_par, E_par_test, R,
//            fileResultDebug, iter, time, numDesplazamientos);
      }
      catch (EOFException e1)
      {
          System.out.println ("Fin de fichero");
      }
      catch (Exception e2)
      {
          System.out.println("ERROR al recuperar el modelo de aprendizaje");
          e2.printStackTrace();
      }
    }
    
    /**
     * save model of learning and print parameters (in fileResultTrain with .model extenssion)
     */
    public static void saveLearning(){
      
      try
      {
        ObjectOutputStream salida=new ObjectOutputStream(new FileOutputStream(fileResultTrain+".model"));
//        salida.writeObject("Matriz de Base de Reglas aprendidas para cada desplazamiento\n");
//        for (int r=0; r < numDesplazamientos; r++){
//          salida.writeObject(R[r]);
//        }
        salida.writeObject(fuzzyProblem);
//        salida.writeObject(E_par);
//        salida.writeObject(E_par_test);
//        salida.writeObject(population);
        salida.writeObject(R);
        salida.close();      

//        DebugClass.printModelLearning(fuzzyProblem, E_par, E_par_test, R,
//            fileResultDebug, iter, time, numDesplazamientos);
        
      } catch (Exception e)
      {
          e.printStackTrace();
      }
    }
    
    
    
    
// ...... SUBCLASS RUNNABLE THREAD .......
    
    /**
     * class for threads
     */
    private static class ExecuteLearning implements Runnable {

    int direction= 0;
    int index=0;
    int cuda=0;
    
    /**
     * constructor
     * @param direction 1 -> shift in right way, -1 -> shift in left way
     * @param index of vector of shift (0->left, indice 1->center, indice 2->right)
     * @param cuda 0: don't use cuda lenguage, 1: use cuda lenguage with nvidia peripherals
     */
    ExecuteLearning(int direction, int index, int cuda){
      this.direction= direction;
      this.index= index;
      this.cuda= cuda;
    }
    /**
     * for threads
     */
    public void run(){            
          executeLearning(shift, direction, index, cuda);
    }
  }

    
    /**
     * class for threads
     */
    private static class ExecutePredict implements Runnable {

    int direction= 0;
    int index=0;
    
    /**
     * constructor
     * @param direction 1 -> shift in right way, -1 -> shift in left way
     * @param index of vector of shift (0->left, indice 1->center, indice 2->right)
     */
    ExecutePredict(int direction, int index){
      this.direction= direction;
      this.index= index;
    }
    /**
     * for threads
     */
    public void run(){            
          executePredict(shift, direction, index);
    }
  }
}
