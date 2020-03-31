% New file for TFG
classdef NSLVOrd < Algorithm
    properties
        description = 'Inclusion del algoritmo NSLVOrd como TFG de Federico Garcia-Arevalo Calles';
        % Parameters to optimize and default value
        parameters = struct('Seed', 1286082570, 'LabelsInputs', 5, 'LabelsOutputs', 5,...
                            'Shift', 35, 'Alpha', 0.5, 'Population', -1, 'MaxIteration', 500,...
                            'IniProbBin', 0.9, 'CrosProbBin', 0.25, 'MutProbBin', 0.5, 'MutProbEachBin', 0.17,...
                            'IniProbInt', 0.5, 'CrosProbInt', 0.0, 'MutProbInt', 0.5,'MutProbEachInt', 0.01,...
                            'IniProbReal', 0.0, 'CrosProbReal', 0.25,'MutProbReal', 0.5, 'MutProbEachReal', 0.14,...
                            'SeeRules', 0);
                          
    end
    
    methods (Access = private, Static)
        function param_java = initParameters(param)
            param_java = [...
                java.lang.String(num2str(param.Seed)),...
                java.lang.String(num2str(param.LabelsInputs)),...
                java.lang.String(num2str(param.LabelsOutputs)),...
                java.lang.String(num2str(param.Shift)),...
                java.lang.String(num2str(param.Alpha)),...
                java.lang.String(num2str(param.Population)),...
                java.lang.String(num2str(param.MaxIteration)),...
                java.lang.String(num2str(param.IniProbBin)),...
                java.lang.String(num2str(param.CrosProbBin)),...
                java.lang.String(num2str(param.MutProbBin)),...
                java.lang.String(num2str(param.MutProbEachBin)),...
                java.lang.String(num2str(param.IniProbInt)),...
                java.lang.String(num2str(param.CrosProbInt)),...
                java.lang.String(num2str(param.MutProbInt)),...
                java.lang.String(num2str(param.MutProbEachInt)),...
                java.lang.String(num2str(param.IniProbReal)),...
                java.lang.String(num2str(param.CrosProbReal)),...
                java.lang.String(num2str(param.MutProbReal)),...
                java.lang.String(num2str(param.MutProbEachReal))];
        end
    
        function header = getHeader(datas)
            header = java.lang.String('@relation NSLVOrd');
            if strcmp(datas.info.utilities.type,'weka')
                for i = 1:length(datas.info.personal.attrs)-1
                    line = strcat('@attribute',{' '},datas.info.personal.attrs(i).name,{' '});
                    if strcmp(datas.info.personal.attrs(i).type,'numeric')
                        line = strcat(line,datas.info.personal.attrs(i).type);
                    elseif strcmp(datas.info.personal.attrs(i).type,'categoric')
                        line = strcat(line,'{',datas.info.personal.attrs(i).info(1));
                        for j = 2:length(datas.info.personal.attrs(i).info)
                            line = strcat(line,',',datas.info.personal.attrs(i).info(j));
                        end
                        line = strcat(line,'}');
                    else
                        error('error');
                    end
                    header = [header;java.lang.String(line)];
                end
                
                line = strcat('@attribute',{' '},datas.info.personal.attrs(end).name,{' '});
                line = strcat(line,'{',datas.info.personal.attrs(end).info.cat(1));
                for j = 2:length(datas.info.personal.attrs(end).info.cat)
                    line = strcat(line,',',datas.info.personal.attrs(end).info.cat(j));
                end
                line = strcat(line,'}');
                header = [header;java.lang.String(line)];
                
            else
                for i = 1:size(datas.patterns,2)
                    line = strcat('@attribute x',int2str(i),' numeric');
                    header = [header;java.lang.String(line)];
                end
                line = '@attribute y numeric';
                header = [header;java.lang.String(line)];
            end
            header = [header;java.lang.String('@data')];
        end
        
        function datas_java = getDatas(datas)
            [a,b] = size(datas);
            datas_java = [];
            for i = 1:a
                aux = '';
                for j = 1:b-1
                    aux = strcat(aux,datas(i,j),',');
                end
                aux = strcat(aux,datas(i,b));
                datas_java = [datas_java;java.lang.String(aux)];
            end
        end
    
        function targets = ConvertTargetsToCategoric(train)
            trans = train.info.personal.attrs(end).info;
            targets_m = repmat(train.targets,1,length(trans.num));
            num_m = repmat(trans.num,length(train.targets),1);
            a = (targets_m == num_m) * [1:length(trans.num)]';
            targets = trans.cat(a)';
        end
    
        function targets = ConvertCategoricToTargets(result,trans)
            result_m = repmat(char(result),1,length(trans.cat));
            cat_m = repmat(char(trans.cat)',length(result),1);
            a = (result_m == cat_m) * [1:length(trans.cat)]';
            targets = trans.num(a)';
        end
    end
   
    methods    
        function obj = NSLVOrd(~,varargin)
            % Process key-values pairs of parameters
            obj.parseArgs(varargin);
            
            obj.categ = true;
            
            algorithmPath = fullfile(fileparts(which('Algorithm.m')),'NSLVOrd');
            jarfolder = fullfile(algorithmPath,'NSLVOrdJava.jar');
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
            
            % Do train
            try
                import NSLVOrdJava.*;
                JavaClass = NSLVOrdJava;
                result = JavaClass.Train(header,datas,param_java);
                fuzzyProblem = JavaClass.GetFuzzyProblem();
                rules = JavaClass.GetRules();
            catch ME
                % Delete
                clear NSLVOrdJava;
                error(ME.message)
            end
                
            % Process output
            targets = obj.ConvertCategoricToTargets(result,train.info.personal.attrs(end).info);
            projectedTrain = targets; 
            predictedTrain = targets;
            
            % Save the model
            try
                model.name = train.name;
            catch
            end
            model.fuzzyProblem = fuzzyProblem;
            model.rules = rules;
            model.outPutsClass = train.info.personal.attrs(end).info;
            model.parameters = param;
            obj.model = model;
            
            % Delete
            clear NSLVOrdJava;
            
            % See rules
            if param.SeeRules
                obj.visual_rules();
            end
        end
        
        function [projected, predicted] = privpredict(obj, patterns)
            % predict unseen patterns with 'obj.model' and return prediction and
            % projection of patterns (for threshold models)
            % It is called by super class Algorithm.predict() method.
            
            % Convert inputs to java objects
            targets = repmat(obj.model.outPutsClass.cat(1),size(patterns,1),1);
            
            datas = [patterns targets];
            datas = obj.getDatas(datas);
            
            % Do test
            try
                import NSLVOrdJava.*;
                JavaClass = NSLVOrdJava;
                JavaClass.LoadModel(obj.model.fuzzyProblem,obj.model.rules);
                result = JavaClass.Test(datas);
            catch ME
                % Delete
                clear NSLVOrdJava;
                error(ME.message)
            end
            
            % Procesar salidas
            targets = obj.ConvertCategoricToTargets(result,obj.model.outPutsClass);
            projected = targets;
            predicted = targets;
            
            % Delete
            clear NSLVOrdJava;
        end
        
        function visual_rules(obj)
            % See Rules
            try
                s
                import NSLVOrdJava.*;
                JavaClass = NSLVOrdJava;
                JavaClass.LoadModel(obj.model.fuzzyProblem,obj.model.rules);
                JavaClass.SeeRules(obj.model.name);
            catch ME
                clear NSLVOrdJava;
                error(ME.message);
            end
            clear NSLVOrdJava;
        end
        
        function save_rules(obj,dir)
            % Crear JFML y PMML
            try
                import NSLVOrdJava.*;
                JavaClass = NSLVOrdJava;
                JavaClass.LoadModel(obj.model.fuzzyProblem,obj.model.rules);
                JavaClass.XMLFile(dir,obj.model.name);
            catch ME
                clear NSLVOrdJava;
                error(ME.message);
            end
            clear NSLVOrdJava;
        end
    end
end