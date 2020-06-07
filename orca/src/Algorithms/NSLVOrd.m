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
            param_java = {...
                num2str(param.Seed),...
                num2str(param.LabelsInputs),...
                num2str(param.LabelsOutputs),...
                num2str(param.Shift),...
                num2str(param.Alpha),...
                num2str(param.Population),...
                num2str(param.MaxIteration),...
                num2str(param.IniProbBin),...
                num2str(param.CrosProbBin),...
                num2str(param.MutProbBin),...
                num2str(param.MutProbEachBin),...
                num2str(param.IniProbInt),...
                num2str(param.CrosProbInt),...
                num2str(param.MutProbInt),...
                num2str(param.MutProbEachInt),...
                num2str(param.IniProbReal),...
                num2str(param.CrosProbReal),...
                num2str(param.MutProbReal),...
                num2str(param.MutProbEachReal)};
        end
    
        function header = getHeader(datas)
            header = '@relation NSLVOrd';
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
                    header = [header;line];
                end
                
                line = strcat('@attribute',{' '},datas.info.personal.attrs(end).name,{' '});
                line = strcat(line,'{',datas.info.personal.attrs(end).info.cat(1));
                for j = 2:length(datas.info.personal.attrs(end).info.cat)
                    line = strcat(line,',',datas.info.personal.attrs(end).info.cat(j));
                end
                line = strcat(line,'}');
                header = [header;line];
                
            else
                for i = 1:size(datas.patterns,2)
                    line = strcat('@attribute x',int2str(i),' numeric');
                    header = [header;line];
                end
                line = '@attribute y numeric';
                header = [header;line];
            end
            header = [header;'@data'];
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
                datas_java = [datas_java;aux];
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
            a = zeros(size(result,1),size(trans.cat,2));
            for i = 1:size(a,1)
                for j = 1:size(a,2)
                    a(i,j) = double(strcmp(result(i),trans.cat{j}));
                end
            end
            a = a * [1:length(trans.cat)]';
            targets = trans.num(a)';
        end
        
        function res = toChar(param)
           	res = [];
            for i = 1:size(param,1)
                a1 = param(i);
                res1 = [];
                for j = 1:size(a1,1)
                    a2 = a1(j);
                    res2 = [];
                    for k = 1:size(a2,1)
                        a3 = char(a2(k));
                        res2 = [res2,{a3}];
                    end
                    res1 = [res1;{res2}];
                end
                res = [res;{res1}];
            end
        end
        
        function res = toCell(param)
            res = [];
            for i = 1:size(param,1)
                res = [res;{char(param(i))}];
            end
        end
        
        function res = toJavaString(param)
            res = javaArray('java.lang.String[][]',size(param,1));
            for i = 1:size(param,1)
                a1 = param{i};
                res1 = [];
                for j = 1:size(a1,1)
                    a2 = a1{j};
                    res2 = [];
                    for k = 1:size(a2,2)
                        a3 = java.lang.String(a2{k});
                        res2 = [res2,a3];
                    end
                    res1 = [res1;res2];
                end
                res(i) = [res1;[]];
            end
        end
    end
   
    methods    
        function obj = NSLVOrd(~,varargin)
            % Process key-values pairs of parameters
            obj.parseArgs(varargin);
            
            obj.categ = true;
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
            
            % NSLVOrd Java
            algorithmPath = fullfile(fileparts(which('Algorithm.m')),'NSLVOrd');
            jarfolder = fullfile(algorithmPath,'NSLVOrdJava.jar');
            javaaddpath(jarfolder);
            
            nslvord = javaObject('NSLVOrdJava.NSLVOrdJava');
            result = javaMethod('Train', nslvord, header,datas,param_java);
            knowledgebase = javaMethod('get_knowledge_base', nslvord);
            rulebase = javaMethod('get_rule_base', nslvord);
            rules = javaMethod('get_rules', nslvord);
            
            clear nslvord;
            javarmpath(jarfolder);
                
            % Process output
            targets = obj.ConvertCategoricToTargets(result,train.info.personal.attrs(end).info);
            projectedTrain = targets; 
            predictedTrain = targets;
            
            % Save the model
            try
                model.name = train.name;
            catch
            end
            %wres = obj.toCharkiko(knowledgebase)
            model.knowledgebase =  obj.toCell(knowledgebase);
            model.rulebase = obj.toCell(rulebase);
            model.rules = obj.toCell(rules);
            model.outPutsClass = train.info.personal.attrs(end).info;
            model.header = header;
            model.parameters = param;
            obj.model = model;
            
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
            
            % NSLVOrd Java
            algorithmPath = fullfile(fileparts(which('Algorithm.m')),'NSLVOrd');
            jarfolder = fullfile(algorithmPath,'NSLVOrdJava.jar');
            javaaddpath(jarfolder);
            
            nslvord = javaObject('NSLVOrdJava.NSLVOrdJava');
            javaMethod('LoadModel', nslvord, obj.model.knowledgebase,obj.model.rulebase);
            result = javaMethod('Test', nslvord, obj.model.header,datas);
            
            clear nslvord;
            javarmpath(jarfolder);
            
            % Procesar salidas
            targets = obj.ConvertCategoricToTargets(result,obj.model.outPutsClass);
            projected = targets;
            predicted = targets;
        end
        
        function visual_rules(obj)
            visual = RulesVisual;
            
            % RuleBase
            rb = obj.model.rules;
            numr = str2num(rb{1});
            finr = 1;
            % Rule
            for i = 1:numr
                inir = finr + 1;
                finr = inir + 2 + str2num(rb{inir + 2});
                r = rb(inir:finr);
                rname = r{1};
                rweight = str2double(r{2});
                numant = str2num(r{4}) - 1;
                con = r(end-1:end);
                ant = getant(obj,r(5:end-2),numant);
                visual.new_rule(rname,rweight);
                for j = 1:size(ant,1)
                    visual.add_antecedent(ant(j).name,ant(j).values);
                end
                visual.new_consequent(con{1},con{2});
            end
            
            % Visual
            visual.visual_rules(obj.model.name);
        end
        
        function export_rules(obj,dir)
            export = RulesExport;
            
            % KnowledgeBase
            kb = obj.model.knowledgebase;
            numfv = str2num(kb{5});
            finfv = 5;
            % FuzzyVariable
            for i = 1:numfv
                inifv = finfv + 1;
                numft = str2num(kb{inifv + 8});
                finfv = inifv + 8 + 7 * numft;
                fv = kb(inifv:finfv);
                export.new_variable(fv{1},fv{5},fv{6});
                finft = 9;
                % FuzzyTerm
                for j = 1:numft
                    inift = finft + 1;
                    finft = inift + 6;
                    ft = fv(inift:finft);
                    export.add_terms(ft{1},ft{2},ft{3},ft{4},ft{5});
                end
            end
            
            % RuleBase
            rb = obj.model.rules;
            numr = str2num(rb{1});
            finr = 1;
            % Rule
            for i = 1:numr
                inir = finr + 1;
                finr = inir + 2 + str2num(rb{inir + 2});
                r = rb(inir:finr);
                rname = r{1};
                rweight = str2double(r{2});
                numant = str2num(r{4}) - 1;
                con = r(end-1:end);
                ant = subrules(obj,r(5:end-2),numant);
                for j = 1:size(ant,1)
                    acname = rname;
                    if size(ant,1) > 1
                        acname = [rname '_' num2str(j)];
                    end
                    export.new_rule(acname,rweight);
                    antr = ant{j};
                    for k = 1:2:size(antr,2)
                        export.add_antecedent(antr{k},antr{k+1});
                    end
                    export.new_consequent(con{1},con{2});
                end
            end
            
            % Export
            export.export_rules(dir,obj.model.name);
        end
        
        function ant = subrules(obj,r,numant)
            [ant_aux,comb] = obj.getant(r,numant);
            ant = [];
            if ~isempty(ant_aux)
                ant = cell(comb,1);
                for i = 1:comb
                    start = comb;
                    index = i;
                    for j = 1:size(ant_aux,1)
                        aux = ant_aux(j);
                        name = aux.name;
                        aux = aux.values;
                        start = start / size(aux,1);
                        value = aux{floor((index-1)/start) + 1};
                        index = index - start * floor((index-1)/start);
                        ant{i} = [ant{i},{name},{value}];
                    end
                end
            end
        end
        
        function [ant,comb] = getant(obj,r,numant)
            ant = [];
            fin = 0;
            comb = 1;
            for i = 1:numant
                ini = fin + 1;
                aux.name = r{ini};
                numval = str2num(r{ini+1});
                fin = ini + 1 + numval * 7;
                vr = r(ini:fin);
                values = [];
                finv = 2;
                comb = comb * numval;
                for j = 1:numval
                    iniv = finv + 1;
                    finv = iniv + 6;
                    tr = vr(iniv:finv);
                    aux2 = {tr{1},str2double(tr{2}),str2double(tr{3}),str2double(tr{4}),str2double(tr{5}),str2num(tr{6}),str2num(tr{7})};
                    values = [values;aux2];
                end
                aux.values = values;
                ant = [ant;aux];
            end
        end
    end
end