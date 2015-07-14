Macro "Run TCB" (type,name,arg1,arg2,arg3)
    // this is a wrapper method to force a hard-crash.  
    // if you get an error here, trace up the stack in the GISDK debugger to see where the real error occurs
    //arguments are "Procedure"/"Operation", "name", [run order index], options array, [extra_message]
    if TypeOf(arg1) = "int" then do
        opts = arg2
        extra_message = arg3
    end
    else do
        opts = arg1
        extra_message = arg2
    end
    ret_val = RunMacro("TCB Run " + type,name,opts)
    if !ret_val then do
        if extra_message <> null then do
            ShowMessage("TCB  " + type + " " + name + " failed to complete. " + extra_message)
        end
        else do
            ShowMessage("TCB  " + type + " " + name + " failed to complete.")
        end
        //put some code in here to stop model from running, I guess
        ShowMessage(2)
    end
EndMacro

Macro "Run Procedure" (name,arg1,arg2,arg3)
    RunMacro("Run TCB","Procedure",name,arg1,arg2,arg3)
EndMacro 

Macro "Run Operation" (name,arg1,arg2,arg3)
    RunMacro("Run TCB","Operation",name,arg1,arg2,arg3)
EndMacro
