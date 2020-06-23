% New file for TFG
classdef weka < ReadFileCommon
    properties
        attrs = [];
    end
    
    methods
        function [file_train_expr, file_test_expr] = Format(obj,dataSetName)
            file_train_expr = ['train_' dataSetName '-*.arff'];
            file_test_expr = ['test_' dataSetName '-*.arff'];
        end

        function datas = ReadFile(obj,file)
            [datas.patterns, datas.targets] = obj.ReadWekaFile(file);
            obj.info.attrs = obj.attrs;
        end
    end
    
    methods (Access = private)
        function [patterns,targets] = ReadWekaFile(obj,file_name)
            file = fopen(file_name, 'rt');
            
            % Read header
            obj.ReadHeader(file);
            
            % Read datas
            [patterns,targets] = obj.ReadDatas(file);
            
            fclose(file);
        end
        
        function ReadHeader(obj,file)
            while ~feof(file)
                line = fgetl(file);
                
                if ~isempty(line)
                    vec = strsplit(line,' ');
                    if strcmpi(vec(1),'@attribute') 
                        % Check if attribute have a name
                        % and type
                        if length(vec) < 3
                            error('Attribute incorrect.');
                        end
                        
                        % Read name and type
                        name = vec(2);
                        aux = strcat(name,{' '});
                        aux = aux{1};
                        ini = length(aux) + 12;
                        type = lower(line(ini:end));
                        
                        % Add attribute
                        obj.NewAttribute(name,type);
                    elseif strcmpi(vec(1),'@data')
                        if length(obj.attrs) < 2
                            error('Need more attributes.');
                        end
                        return;
                    end
                end
            end
            error('Unrecognized as WEKA format.')
        end
        
        function NewAttribute(obj,name,type)
            % Comprobar que no exista el nombre en otro atributo
            if ~isempty(obj.attrs)
                if ismember(name,[obj.attrs.name])
                    error('Attributes with same name.');
                end
            end
            
            % Comprobar el tipo de atributo
            info = [];
            if ~strcmp(type,'numeric')
                indexL = strfind(type,'{');
                indexR = strfind(type,'}');
                if length(indexL) == 1 && length(indexR) == 1 && indexL == 1 && indexR == length(type)
                    type = strrep(type(2:length(type)-1),' ','');
                    [info,num] = strsplit(type,',');
                    for i = num
                        if length(i{1}) ~= 1
                            error('Categoric attribute without type.');
                        end
                    end
                    type = 'categoric';
                else
                    error('Attributes should be numeric or categoric.');
                end
            end
            
            % Guardar los datos
            aux.name = name;
            aux.type = type;
            aux.info = info;
            obj.attrs = [obj.attrs;aux];
        end
        
        function [patterns,targets] = ReadDatas(obj,file)
            % Leer los datos
            datas = [];
            while ~feof(file)
                line = fgetl(file);
                line = strrep(line,' ','');
                if ~isempty(line)
                    att_datas = strsplit(line,',');
                    datas = [datas;att_datas];
                end
            end
            datas = lower(datas);
            
            if isempty(datas)
                error('No data found.');
            end
            
            % Guardar las entradas
            patterns_aux = datas(:,1:end-1);
            patterns = [];
            att_aux = [];
            for i = 1:size(patterns_aux,2)
                if strcmp(obj.attrs(i).type,'categoric')
                    if obj.categ == 0
                        [patt,atti] = obj.ToOneHot(patterns_aux(:,i),obj.attrs(i));
                        patterns = [patterns patt];
                        att_aux = [att_aux;atti];
                    else
                        aux = patterns_aux(:,i);
                        elements = obj.attrs(i).info;
                        [k,~] = obj.Categoric_to_Numeric(aux,elements);
                        aux(find(isnan(k),1)) = {NaN};
                        
                        patterns = [patterns aux];
                        att_aux = [att_aux;obj.attrs(i)];
                    end
                elseif strcmp(obj.attrs(i).type,'numeric')
                    line_aux = zeros(length(patterns_aux(:,i)),1);
                    for j = 1:length(line_aux)
                    	line_aux(j) = str2double(patterns_aux(j,i));
                    end
                    if obj.categ == 0
                        patterns = [patterns line_aux];
                        att_aux = [att_aux;obj.attrs(i)];
                    else
                        aux = patterns_aux(:,i);
                        aux(find(isnan(line_aux),1)) = {NaN};
                        patterns = [patterns aux];
                        att_aux = [att_aux;obj.attrs(i)];
                    end
                else
                    error('Attribute type no valid.');    
                end
            end
            obj.attrs = [att_aux;obj.attrs(end)];
            
            % Gardar las salidas
            datas = datas(:,end);
            att = obj.attrs(end);
            [targets,obj.attrs(end)] = obj.ToNumeric(datas,att);
        end
        
        function [datas,attnew] = ToOneHot(obj,patterns,att)
            % Convert datas
            datas = zeros(size(patterns,1),size(att.info,2));
            for i = 1:size(datas,1)
                for j = 1:size(datas,2)
                    datas(i,j) = double(strcmp(patterns{i},att.info{j}));
                end
            end

            % Check all values are valids
            ind = ~sum(datas,2);
            datas(ind,:) = NaN;
            
            % Update attribute
            attnew = [];
            for i = 1:size(att.info,2)
                att_aux.type = 'categoric';
                att_aux.name = strcat(att.name,'_',int2str(i));
                att_aux.info = ['0' '1'];
                attnew = [attnew;att_aux];
            end
        end
        
        function [datas,att] = ToNumeric(obj,datas,att)
            if strcmpi(att.type,'numeric')
                line_aux = zeros(size(datas));
                for j = 1:size(line_aux,1)
                	line_aux(j) = str2double(datas(j));
                end
                datas = line_aux;
            elseif strcmpi(att.type,'categoric')
                elements = att.info;
                [datas,convert] = obj.Categoric_to_Numeric(datas,elements);
                att.info = convert;
            end
        end
        
        function [final_datas,targets_type] = Categoric_to_Numeric(obj,datas,elements)
            % Apuntar la conversion
            targets_type.cat = elements;
            targets_type.num = 1:length(elements);
            
            % Convertir los datos
            final_datas = zeros(size(datas,1),size(targets_type.cat,2));
            for i = 1:size(final_datas,1)
                for j = 1:size(final_datas,2)
                    final_datas(i,j) = double(strcmp(datas{i},targets_type.cat{j}));
                end
            end
            final_datas = final_datas * targets_type.num';
            
            % Comprobar que ninguno sea un valor no valido
            ind = ~sum(final_datas,2);
            final_datas(ind,:) = NaN;
        end
    end
end