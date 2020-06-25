% New file for TFG
classdef RulesVisual < handle
    properties
        rules = [];
    end
    
    methods
        function visual_rules(obj,name)
            if isempty(obj.rules)
                error('Rules System is empty');
            end
            
            addpath(fullfile(fileparts(which('RulesVisual.m')),'VisualRules'));
            
            Visual(name,obj.rules);
            
            rmpath(fullfile(fileparts(which('RulesVisual.m')),'VisualRules'));
        end
        
        function num = detect_number(~,num)
            if iscell(num)
                if ~isempty(find(size(num) ~= 1,1))
                    num = NaN;
                else
                    num = num{1};
                end
            end
            
            switch class(num)
                case 'double'
                    if ~isempty(find(size(num) ~= 1,1))
                        num = NaN;
                    end
                case 'char'
                    num = str2double(num);
                otherwise
                    num = NaN;
            end
        end
        
        function new_rule(obj,name,weight)
            obj.rules = [obj.rules,struct('Name',name,'Weight',num2str(weight),'Antecedent',[],'Consequent',[])];
        end
        
        function add_antecedent(obj,variable,term) 
            if isempty(obj.rules)
                error('Rules System is empty');
            end
            
            term_aux = [];
            if ~iscell(term)
                term = {term};
            end
            for i = 1:size(term,1)
                try
                    for j = 1:7
                        term_aux = [term_aux;{num2str(term{i,j})}];
                    end
                catch
                    error('Terms no valids');
                end
            end
            term = term_aux;
            
            obj.rules(length(obj.rules)).Antecedent = [obj.rules(length(obj.rules)).Antecedent;{variable term}];
        end
        
        function new_consequent(obj,variable,term)
            if isempty(obj.rules)
                error('Rules System is empty');
            end
            
            obj.rules(length(obj.rules)).Consequent = {variable term};
        end
    end
end

