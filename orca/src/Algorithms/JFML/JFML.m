% New file for TFG
function JFML(dir,name,knowledge_base,rules)
    % Load JFML.jar
    algorithmPath = fileparts(which('JFML.m'));
    jarfolder = fullfile(algorithmPath,'JFML.jar');
    javaaddpath(jarfolder);
     
    % Initialize FuzzyInferenceSystem
    f = javaObject('jfml.FuzzyInferenceSystem');
    
    % KNOWLEDGE BASE
    KnowledgeBase(f,knowledge_base);
    
    % RULE BASE
    RuleBase(f,rules);
       
    % WRITTING INTO AN XML FILE
    WriteFile(f,dir,name);
    
    % Clear java
    clear f;
    javarmpath(jarfolder);
end

function KnowledgeBase(f,knowledge_base)
    % Initialize KnowledgeBaseType
    kb = javaObject('jfml.knowledgebase.KnowledgeBaseType');
    javaMethod('setKnowledgeBase',f,kb);
    
    % FUZZY VARIABLE
    for i = 1:length(knowledge_base)-1
        s = javaObject('jfml.knowledgebase.variable.FuzzyVariableType',knowledge_base(i).Name,knowledge_base(i).DomainLeft,knowledge_base(i).DomainRight);
        
        % FUZZY TERM
        for j = 1:size(knowledge_base(i).Terms,1)
            term = knowledge_base(i).Terms(j,:);
            st = javaObject('jfml.term.FuzzyTermType',term{1},7,[term{2},term{3},term{4},term{5}]);
            
            javaMethod('addFuzzyTerm',s,st);
        end
        
        javaMethod('addVariable',kb,s);
    end
    
    % OUTPUT CLASS
    s = javaObject('jfml.knowledgebase.variable.FuzzyVariableType',knowledge_base(length(knowledge_base)).Name,knowledge_base(length(knowledge_base)).DomainLeft,knowledge_base(length(knowledge_base)).DomainRight);
    javaMethod('setType',s,'output');
    
    % FUZZY TERM OUTPUT CLASS
    for j = 1:size(knowledge_base(length(knowledge_base)).Terms,1)
        term = knowledge_base(length(knowledge_base)).Terms(j,:);
            
        st = javaObject('jfml.term.FuzzyTermType',term{1},7,[term{2},term{3},term{4},term{5}]);
            
        javaMethod('addFuzzyTerm',s,st);
    end
    
    javaMethod('addVariable',kb,s);
end

function RuleBase(f,rules)
    % Initialize MamdaniRuleBaseType
    rb = javaObject('jfml.rulebase.MamdaniRuleBaseType','');
    kb = javaMethod('getKnowledgeBase',f);
         
    % RULES
    for i = 1:length(rules)
        % ANTECEDENT
        ant = javaObject('jfml.rule.AntecedentType');
        for j = 1:size(rules(i).Antecedent,1)
            ant_aux = rules(i).Antecedent(j,:);
            a = javaMethod('getVariable',kb,ant_aux{1});
            b = javaMethod('getTerm',a,ant_aux{2});
            javaMethod('addClause',ant,javaObject('jfml.rule.ClauseType',a,b));
        end
        
        % CONSEQUENT
        con = javaObject('jfml.rule.ConsequentType');
        a = javaMethod('getVariable',kb,rules(i).Consequent{1});
        b = javaMethod('getTerm',a,rules(i).Consequent{2});
        javaMethod('addThenClause',con,a,b);
        
        % ADD RULE
        r = javaObject('jfml.rule.FuzzyRuleType',rules(i).Name,'and','MIN',javaObject('java.lang.Float',rules(i).Weight));
        javaMethod('setAntecedent',r,ant);
        javaMethod('setConsequent',r,con);
        
        javaMethod('addRule',rb,r);
    end
    
    javaMethod('addRuleBase',f,rb);
end

function WriteFile(f,dir,name)
    % Configure directory
    if ~exist(dir,'dir')
        mkdir(dir);
    end
    
    % Export JFML
    try
        disp('Export JFML...');
        a = javaObject('jfml.JFML');
        file = javaObject('java.io.File',[dir '/' name '_JFML.xml']);
        javaMethod('writeFSTtoXML',a,f,file);
    catch 
        disp('JFML can not export');
    end
    
    % Export PMML
    try
        disp('Export PMML...');
        b = javaObject('jfml.compatibility.ExportPMML');
        file = [dir '/' name '_PMML.xml'];
        javaMethod('exportFuzzySystem',b,f,file);
    catch 
        disp('PMML can not export');
    end
end