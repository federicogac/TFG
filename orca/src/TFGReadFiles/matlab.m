% New file for TFG
classdef matlab < Common
    methods 
        function [file_train_expr, file_test_expr] = format(obj,dataSetName)
            file_train_expr = ['train_' dataSetName '.*']; 
            file_test_expr = ['test_' dataSetName '.*']; 
        end

        function datas = ReadFile(obj,file)    
            raw = load(file);

            datas.targets = raw(:,end);
            datas.patterns = raw(:,1:end-1);
        end
    end
end