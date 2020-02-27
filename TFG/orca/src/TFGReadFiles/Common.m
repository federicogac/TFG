classdef Common < handle
    properties
        categ = 0;
        info = [];
        categ_att = [];
    end

    methods 
        function [trainFileName,testFileName] = FormatFile(obj,dsdirectory,archive,dataSetName)
            [file_train_expr, file_test_expr] = obj.format(dataSetName);

            file_expr = [dsdirectory '/' archive '/' file_train_expr];
            trainFileName = dir(file_expr);
            file_expr = [dsdirectory '/' archive '/' file_test_expr]; 
            testFileName = dir(file_expr);
        end

        function [file_train_expr, file_test_expr] = format(dataSetName)
            error('format should be implemented in all subclasses');
        end
        
        function datas = ReadFileFunction(obj,file,cat)
            obj.categ = cat;
            %try
                datas = obj.ReadFile(file);
            %catch
            %    error('Cannot read file "%s"', file)
            %end
            datas.info.personal = obj.info;
            datas.info.utilities.type = class(obj);
            datas.info.utilities.categ_att = obj.categ_att;
        end
        
        function datas = ReadFile(obj,file)
            error('ReadFile method should be implemented in all subclasses');
        end
    end
end