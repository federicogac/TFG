% New file for TFG
classdef RulesVisual < handle
    properties
        rules = [];
    end
    
    methods
        function visual_rules(obj,name)
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
%             weight = obj.detect_number(weight);
%             if isnan(weight)
%                 error('Weight dont detected like a number.')
%             end
            
            obj.rules = [obj.rules,struct('Name',name,'Weight',num2str(weight),'Antecedent',[],'Consequent',[])];
        end
        
        function add_antecedent(obj,variable,term) 
            term_aux = [];
            for i = 1:size(term,1)
                for j = 1:size(term,2)
                    term_aux = [term_aux;{num2str(term{i,j})}];
                end
            end
            term = term_aux;
            obj.rules(length(obj.rules)).Antecedent = [obj.rules(length(obj.rules)).Antecedent;{variable term}];
        end
        
        function new_consequent(obj,variable,term)
            obj.rules(length(obj.rules)).Consequent = {variable term};
        end
    end
end

