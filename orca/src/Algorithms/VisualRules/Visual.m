% New file for TFG
function Visual(name,rules)
    % Load VisualRules.jar
    algorithmPath = fileparts(which('Visual.m'));
    jarfolder = fullfile(algorithmPath,'VisualRules.jar');
    javaaddpath(jarfolder);
     
    % Initialize VisualRules
    try
        visual = javaObject('visualrules.VisualRules');
    catch(ME)
        disp('**************************');
        disp('See rules is not possible.');
        disp('**************************');
        
        % Clear Java
        clear visual;
        javarmpath(jarfolder);
        
        return;
    end
        
    % Add rules
    for i = 1:size(rules,2)
        namer = rules(i).Name;
        weight = rules(i).Weight;
        ant = rules(i).Antecedent;
        con = rules(i).Consequent;
        
        % Rule
        javaMethod('new_rule',visual,namer,weight);
        
        % Antecedent
        for j = 1:size(ant,1)
            namea = ant(j,1);
            values = ant(j,2);
            javaMethod('new_antecedent',visual,namea{1},values{1});
        end
        
        % Consequent
        javaMethod('new_consequent',visual,con{1},con{2});
    end
    
    % Activate panel
    javaMethod('SeeRules',visual,name);
    
    % Clear Java
    clear visual;
    javarmpath(jarfolder);
end

