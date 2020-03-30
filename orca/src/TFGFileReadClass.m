classdef TFGFileReadClass
    methods (Static)
        function valid_archive(archive)     
            addpath(fullfile(fileparts(which('TFGFileReadClass.m')),'TFGReadFiles'));
            
            if exist([pwd '/TFGReadFiles/' archive],'file') ~= 2
                error('"%s" es un tipo de archivo no soportado', archive)
            end
            
            rmpath(fullfile(fileparts(which('TFGFileReadClass.m')),'TFGReadFiles'));
        end
        
        function datas = ReadFile(directory,file,cat)
            addpath(fullfile(fileparts(which('TFGFileReadClass.m')),'TFGReadFiles'));
                        
            folders = strsplit(directory,'/');
            archive = char(folders(end));
            
            TFGReadFiles = feval(archive); 
            raw = [directory '/' file];
            datas = TFGReadFiles.ReadFileFunction(raw,cat);
            
            rmpath(fullfile(fileparts(which('TFGFileReadClass.m')),'TFGReadFiles'));
        end
    
        function [trainFileName,testFileName] = TFGFileName(dsdirectory, archive, dataSetName)
            addpath(fullfile(fileparts(which('TFGFileReadClass.m')),'TFGReadFiles'));
            
            format = feval(archive);
            [trainFileName,testFileName] = format.FormatFile(dsdirectory,archive,dataSetName);

            rmpath(fullfile(fileparts(which('TFGFileReadClass.m')),'TFGReadFiles'));
        end
    end
    
    methods (Static, Access = private)
        function cols = searchInvalidValue(datas)
            [~,cols] = find(isnan(datas) | isinf(datas));
        end
    end
end