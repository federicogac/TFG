classdef NSLVOrd < Algorithm
    properties
        description = 'Inclusion del algoritmo NSLVOrd como TFG de Federico Garcia-Arevalo Calles';
        % Parameters to optimize and default value
        parameters = struct('Seed', 1286082570, 'LabelsInputs', 5, 'LabelsOutputs', 5,...
                            'Shift', 35, 'Alpha', 0.5, 'Population', -1, 'MaxIteration', 500,...
                            'IniProbBin', 0.9, 'CrosProbBin', 0.25, 'MutProbBin', 0.5, 'MutProbEachBin', 0.17,...
                            'IniProbInt', 0.5, 'CrosProbInt', 0.0, 'MutProbInt', 0.5,'MutProbEachInt', 0.01,...
                            'IniProbReal', 0.0, 'CrosProbReal', 0.25,'MutProbReal', 0.5, 'MutProbEachReal', 0.14);
                          
    end
    
    methods (Access = private, Static)
        function param_java = initParameters(param)
            param_java = [...
                java.lang.String(string(param.Seed)),...
                java.lang.String(string(param.LabelsInputs)),...
                java.lang.String(string(param.LabelsOutputs)),...
                java.lang.String(string(param.Shift)),...
                java.lang.String(string(param.Alpha)),...
                java.lang.String(string(param.Population)),...
                java.lang.String(string(param.MaxIteration)),...
                java.lang.String(string(param.IniProbBin)),...
                java.lang.String(string(param.CrosProbBin)),...
                java.lang.String(string(param.MutProbBin)),...
                java.lang.String(string(param.MutProbEachBin)),...
                java.lang.String(string(param.IniProbInt)),...
                java.lang.String(string(param.CrosProbInt)),...
                java.lang.String(string(param.MutProbInt)),...
                java.lang.String(string(param.MutProbEachInt)),...
                java.lang.String(string(param.IniProbReal)),...
                java.lang.String(string(param.CrosProbReal)),...
                java.lang.String(string(param.MutProbReal)),...
                java.lang.String(string(param.MutProbEachReal))];
        end
    
        function header = getHeader(datas)
            header = java.lang.String("@relation NSLVOrd");
            if(datas.info.utilities.type == "weka")
                for i = 1:length(datas.info.personal.attrs)-1
                    line = "@attribute " + datas.info.personal.attrs(i).name + " ";
                    if datas.info.personal.attrs(i).type == "numeric"
                        line = line + datas.info.personal.attrs(i).type;
                    elseif datas.info.personal.attrs(i).type == "categoric"
                        line = line + "{" + datas.info.personal.attrs(i).info(1);
                        for j = 2:length(datas.info.personal.attrs(i).info)
                            line = line + "," + datas.info.personal.attrs(i).info(j);
                        end
                        line = line + "}";
                    else
                        error('error');
                    end
                    header = [header;java.lang.String(line)];
                end
                
                line = "@attribute " + datas.info.personal.attrs(end).name + " ";
                line = line + "{" + datas.info.personal.attrs(end).info.cat(1);
                for j = 2:length(datas.info.personal.attrs(end).info.cat)
                    line = line + "," + datas.info.personal.attrs(end).info.cat(j);
                end
                line = line + "}";
                header = [header;java.lang.String(line)];
                
            else
                for i = 1:size(datas.patterns,2)
                    line = "@attribute x" + i + " numeric";
                    header = [header;java.lang.String(line)];
                end
                line = "@attribute y numeric";
                header = [header;java.lang.String(line)];
            end
            header = [header;java.lang.String("@data")];
        end
        
        function datas_java = getDatas(datas)
            [a,b] = size(datas);
            datas_java = [];
            for i = 1:a
                aux = "";
                for j = 1:b-1
                    aux = aux + datas(i,j) + ",";
                end
                aux = aux + datas(i,b);
                datas_java = [datas_java;java.lang.String(aux)];
            end
        end
    
        function targets = ConvertTargetsToCategoric(train)
            trans = train.info.personal.attrs(end).info;
            
            a = (train.targets == trans.num) * [1:length(trans.num)]';
            targets = trans.cat(a)';
        end
    
        function targets = ConvertCategoricToTargets(result,trans)
            a = (string(result) == trans.cat) * [1:length(trans.cat)]';
            targets = trans.num(a)';
        end
    end
    
    methods    
        function obj = NSLVOrd(obj,varargin)
            % Process key-values pairs of parameters
            obj.parseArgs(varargin);
            
            obj.categ = true;
            
            algorithmPath = fullfile(fileparts(which('Algorithm.m')),'NSLVOrd');
            jarfolder = fullfile(algorithmPath,'NSLVOrd.jar');
            javaaddpath(jarfolder);
        end
        
        function [projectedTrain, predictedTrain] = privfit(obj, train, param)
            % fit the model and return prediction of train set. It is called by
            % super class Algorithm.fit() method.
            
            % Convertir los datos a objetos Java
            param_java = obj.initParameters(param);
            
            header = obj.getHeader(train);
            
            targets = obj.ConvertTargetsToCategoric(train);
            datas = [train.patterns targets];
            datas = obj.getDatas(datas);
            
            % Realizar entrenamiento
            import NSLVOrd.*;
            JavaClass = NSLVOrd;
            result = JavaClass.Train(header,datas,param_java);
            
            % Crear JFML y PMML
            try % Si no existe train.name está en cross validation
                % y no interesa sacar los xml
                train.name;
                xml = true;
            catch
                xml = false;
            end
            
            if xml
                k = strfind(train.name,'.');
                name = extractBefore(train.name,k(end));
                folder = train.folder + "/Rules"; 
                JavaClass.XMLFile(folder,name);
            end
            
            % Procesar salidas
            targets = obj.ConvertCategoricToTargets(result,train.info.personal.attrs(end).info);
            
            projectedTrain = targets; 
            predictedTrain = targets;
            
            % Save the model
            model.fuzzyProblem = JavaClass.GetFuzzyProblem();
            model.rules = JavaClass.GetRules();
            model.outPutsClass = train.info.personal.attrs(end).info;
            model.parameters = param;
            obj.model = model;
        end
        
        function [projected, predicted] = privpredict(obj, patterns)
            % predict unseen patterns with 'obj.model' and return prediction and
            % projection of patterns (for threshold models)
            % It is called by super class Algorithm.predict() method.
            
            % Convertir las entradas en objeto Java
            targets = repmat(obj.model.outPutsClass.cat(1),length(patterns),1);
            
            datas = [patterns targets];
            datas = obj.getDatas(datas);
            
            % Realizar test
            import NSLVOrd.*;
            JavaClass = NSLVOrd;
            JavaClass.LoadModel(obj.model.fuzzyProblem,obj.model.rules);
            result = JavaClass.Test(datas);
            
            % Procesar salidas
            targets = obj.ConvertCategoricToTargets(result,obj.model.outPutsClass);
                        
            projected = targets;
            predicted = targets;
        end
    end
end