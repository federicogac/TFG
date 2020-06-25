% New file for TFG
classdef ReadFileCommon < handle
    properties
        info = [];
        categ_att = [];
    end
    
    properties (Access = protected)
        categ = 0;
    end

    methods 
        function [trainFileName,testFileName] = FormatFile(obj,dsdirectory,archive,dataSetName)
            [file_train_expr, file_test_expr] = obj.Format(dataSetName);

            file_expr = [dsdirectory '/' archive '/' file_train_expr];
            trainFileName = dir(file_expr);
            file_expr = [dsdirectory '/' archive '/' file_test_expr]; 
            testFileName = dir(file_expr);
        end

        function [file_train_expr, file_test_expr] = Format(obj,dataSetName)
            error('format should be implemented in all subclasses');
        end
        
        function datas = ReadFileFunction(obj,file,cat)
            obj.categ = cat;
            try
                datas = obj.ReadFile(file);
            catch ME
                error('Cannot read file "%s" \n %s', file, ME.message)
            end
            datas = obj.deleteNonNumericValues(datas);
            
            datas.info.personal = obj.info;
            datas.info.utilities.type = class(obj);
            datas.info.utilities.categ_att = obj.categ_att;
        end
        
        function datas = ReadFile(obj,file)
            error('ReadFile method should be implemented in all subclasses');
        end
    end
    
    methods (Access = private)
        function datas = deleteNonNumericValues(obj,datas)
            % Search invalid data on targets
            [fils,cols] = find(isnan(datas.targets) | isinf(datas.targets));
            del = fils;

            if obj.categ == 0 || ~iscell(datas.patterns)
                % Search invalid data on patterns
                [fils,cols] = find(isnan(datas.patterns) | isinf(datas.patterns));
                del = unique([del;fils]);

            else
                for i = 1:size(datas.patterns,1)
                    for j = 1:size(datas.patterns,2)
                        if ~ischar(datas.patterns{i,j}) && isnan(datas.patterns{i,j})
                            del = [del;i];
                        end
                    end
                end
                del = unique(del);
            end
            
            if isempty(del)
                return;
            end
            
            % Delete lines whit invalid data
            datas.targets(del,:) = [];
            datas.patterns(del,:) = [];
        end
    end
end