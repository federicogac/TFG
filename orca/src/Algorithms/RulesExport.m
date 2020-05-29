% New file for TFG
classdef RulesExport < handle
    properties
        rules = [];
        knowledge_base = [];
    end
    
    methods
        function export_rules(obj,dir,name)
            addpath(fullfile(fileparts(which('RulesExport.m')),'JFML'));
            
            JFML(dir,name,obj.knowledge_base,obj.rules);
            
            rmpath(fullfile(fileparts(which('RulesExport.m')),'JFML'));
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
        
        function new_variable(obj,name,domain_left,domain_right)
            domain_left = obj.detect_number(domain_left);
            if isnan(domain_left)
                error('Domain left dont detected like a number.')
            end
            
            domain_right = obj.detect_number(domain_right);
            if isnan(domain_right)
                error('Domain right dont detected like a number.')
            end
            
            obj.knowledge_base = [obj.knowledge_base,struct('Name',name,'DomainLeft',domain_left,'DomainRight',domain_right,'Terms',[])];
        end
        
        function add_terms(obj,name,p1,p2,p3,p4)
            p1 = obj.detect_number(p1);
            p2 = obj.detect_number(p2);
            p3 = obj.detect_number(p3);
            p4 = obj.detect_number(p4);
            if isnan(p1) || isnan(p2) || isnan(p3) || isnan(p4)
                error('A value dont detected like a number.')
            end
            
            obj.knowledge_base(length(obj.knowledge_base)).Terms = [obj.knowledge_base(length(obj.knowledge_base)).Terms;{name p1 p2 p3 p4}];
        end
        
        function new_rule(obj,name,weight)
            weight = obj.detect_number(weight);
            if isnan(weight)
                error('Weight dont detected like a number.')
            end
            
            obj.rules = [obj.rules,struct('Name',name,'Weight',weight,'Antecedent',[],'Consequent',[])];
        end
        
        function add_antecedent(obj,variable,term) 
            obj.rules(length(obj.rules)).Antecedent = [obj.rules(length(obj.rules)).Antecedent;{variable term}];
        end
        
        function new_consequent(obj,variable,term)
            obj.rules(length(obj.rules)).Consequent = {variable term};
        end
    end
end

