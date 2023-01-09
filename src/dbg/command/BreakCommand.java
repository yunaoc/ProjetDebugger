package dbg.command;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;

import java.util.List;

public class BreakCommand extends Command{
    public BreakCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public Object execute() {
        String classe = getCommandLine().substring(getCommandLine().indexOf("(")+1, getCommandLine().indexOf(","));
        String line = getCommandLine().substring(getCommandLine().indexOf(",")+1, getCommandLine().indexOf(")"));
        String classeComplete = "";
        for(ReferenceType cl : getVm().allClasses()){
            if(cl.name().split("\\.")[cl.name().split("\\.").length-1].equals(classe)){
                classeComplete = cl.name();
            }
        }

        for(ReferenceType targetClass : getVm().allClasses()){
            if(targetClass.name().equals(classeComplete)){
                Location location = null;
                try {
                    location = targetClass.locationsOfLine(Integer.parseInt(line)).get(0);
                } catch (AbsentInformationException e) {
                    e.printStackTrace();
                }
                BreakpointRequest bpReq = getVm().eventRequestManager().createBreakpointRequest(location);
                bpReq.enable();
            }
        }
        return null;
    }
}
