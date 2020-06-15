% New file for TFG
classdef TFGFileReadClass
    properties (Access = private)
        path = fullfile(fileparts(which('TFGFileReadClass.m')),'TFGReadFiles');
    end
    
    methods 
        function valid_archive(obj,archive)     
            addpath(obj.path);
            
            if exist(fullfile(obj.path,[archive '.m']),'file') ~= 2
                error('"%s" es un tipo de archivo no soportado', archive)
            end
            
            rmpath(obj.path);
        end
        
        function datas = ReadFile(obj,directory,file,cat)
            addpath(obj.path);
                        
            folders = strsplit(directory,'/');
            archive = char(folders(end));
            
            TFGReadFiles = feval(archive); 
            raw = [directory '/' file];
            datas = TFGReadFiles.ReadFileFunction(raw,cat);
            
            rmpath(obj.path);
        end
    
        function [trainFileName,testFileName] = TFGFileName(obj,dsdirectory, archive, dataSetName)
            addpath(obj.path);
            
            format = feval(archive);
            [trainFileName,testFileName] = format.FormatFile(dsdirectory,archive,dataSetName);

            rmpath(obj.path);
        end
    end
    
    methods (Static, Access = private)
        function cols = searchInvalidValue(datas)
            [~,cols] = find(isnan(datas) | isinf(datas));
        end
    end
end