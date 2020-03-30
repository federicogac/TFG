% New file for TFG
classdef weka < Common
    properties
        attrs = [];
    end
    
    methods
        function [file_train_expr, file_test_expr] = format(obj,dataSetName)
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
            
            % Leer la cabecera
            obj.ReadHeader(file);
            
            % Leer los datos
            [patterns,targets] = obj.ReadDatas(file);
            
            fclose(file);
        end
        
        function ReadHeader(obj,file)
            while ~feof(file)
                line = fgetl(file);
                
                if ~isempty(line)
                    vec = strsplit(line,' ');
                    if strcmpi(vec(1),'@attribute') 
                        % Verifica que el atributo contenga 
                        % un nombre y el tipo de atributo
                        if length(vec) < 3
                            error('error');
                        end
                        
                        % Leer el nombre y tipo de atributo
                        name = vec(2);
                        aux = strcat(name,{' '});
                        aux = aux{1};
                        ini = length(aux) + 12;
                        type = lower(line(ini:end));
                        
                        % Añadir el atributo
                        obj.NewAttribute(name,type);
                    elseif strcmpi(vec(1),'@data')
                        break;
                    end
                end
            end
        end
        
        function NewAttribute(obj,name,type)
            % Comprobar que no exista el nombre en otro atributo
            if ~isempty(obj.attrs)
                if ismember(name,[obj.attrs.name])
                    error('error');
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
                            error('error');
                        end
                    end
                    type = 'categoric';
                else
                    error('error');
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
            
            % Guardar las entradas
            patterns = datas(:,1:end-1);
            if obj.categ == 0
                patterns_aux = [];
                att_aux = [];
                for i = 1:size(patterns,2)
                    if strcmp(obj.attrs(i).type,'categoric')
                        [patt,atti] = obj.ToOneHot(patterns(:,i),obj.attrs(i));
                        patterns_aux = [patterns_aux patt];
                        att_aux = [att_aux;atti];
                    elseif strcmp(obj.attrs(i).type,'numeric')
                        line_aux = zeros(length(patterns(:,i)),1);
                        for j = 1:length(line_aux)
                            line_aux(j) = str2double(patterns(j,i));
                        end
                        patterns_aux = [patterns_aux line_aux];
                        att_aux = [att_aux;obj.attrs(i)];
                    else
                        error('error');    
                    end
                end
                patterns = patterns_aux;
                obj.attrs = [att_aux;obj.attrs(end)];
            end
            
            % Gardar las salidas
            [targets,obj.attrs(end)] = obj.ToNumeric(datas(:,end),obj.attrs(end));
        end
        
        function [datas,attnew] = ToOneHot(obj,patterns,att)
            % Convertir datos
            val = char(att.info)';
            val_m = repmat(val,length(patterns),1);
            patterns = char(patterns);
            patterns_m = repmat(patterns,1,length(val));
            datas = double(patterns_m == val_m);
            
            % Comprobar que ninguno sea un valor no valido
            ind = ~sum(datas,2);
            datas(ind,:) = NaN;
            
            % Nuevo tipo
            attnew = [];
            for i = 1:length(val)
                att_aux.type = 'categoric';
                att_aux.name = strcat(att.name,'_',int2str(i));
                att_aux.info = ['0' '1'];
                attnew = [attnew;att_aux];
            end
        end
        
        function [datas,att] = ToNumeric(obj,datas,att)
            if att.type ~= 'categoric'
                error('error');
            end
            elements = att.info;
            [datas,convert] = obj.Categoric_to_Numeric(datas,elements);
            att.info = convert;
        end
        
        function [final_datas,targets_type] = Categoric_to_Numeric(obj,datas,elements)
            elements = sort(elements);
            
            % Apuntar la conversion
            targets_type.cat = elements;
            targets_type.num = 1:length(elements);
            
            % Convertir los datos
            cat = char(targets_type.cat)';
            cat_m = repmat(cat,length(datas),1);
            datas = char(datas);
            datas_m = repmat(datas,1,length(cat));
            final_datas = double(datas_m == cat_m) * targets_type.num';
            
            % Comprobar que ninguno sea un valor no valido
            ind = ~sum(final_datas,2);
            final_datas(ind,:) = NaN;
        end
    end
end