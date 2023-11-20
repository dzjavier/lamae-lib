function OPTIONS = test_function_options(DEFAULT_OPTIONS,INPUT_OPTIONS)
    # usage: OPTIONS = test_function_options(DEFAULT_OPTIONS,INPUT_OPTIONS)
    OPTIONS=DEFAULT_OPTIONS;
    optionNames = fieldnames(OPTIONS);
    if isstruct(INPUT_OPTIONS)
      input_optionNames = fieldnames(INPUT_OPTIONS); 
      for j=1:length(input_optionNames)
        if (isfield(DEFAULT_OPTIONS, lower(input_optionNames{j}))) # make case insensitive
          ## overwrite options. If you want you can test for the right class here
          ## Also, if you find out that there is an option you keep getting wrong,
          ## you can use "if strcmp(inpName,'problemOption'),testMore,end"-statements
          OPTIONS=setfield(OPTIONS,lower(input_optionNames{j}),getfield(INPUT_OPTIONS,input_optionNames{j}));
        else
          error("%s is not a recognized parameter name",input_optionNames{j})
        endif
      endfor
    else 
      if(iscell(INPUT_OPTIONS))      
        for pair = reshape(INPUT_OPTIONS,2,[]) # pair is {propName;propValue}
          inpName = lower(pair{1}); # make case insensitive
          if any(strcmp(inpName,optionNames))
            ## overwrite options. If you want you can test for the right class here
            ## Also, if you find out that there is an option you keep getting wrong,
            ## you can use "if strcmp(inpName,'problemOption'),testMore,end"-statements
            OPTIONS.(inpName) = pair{2};
          else
            error('%s is not a recognized parameter name',inpName)
          endif
        endfor
      else
        error("EXAMPLE needs a comma separated list or struct with propertyName/propertyValue pairs")
      endif
    endif
  endfunction
