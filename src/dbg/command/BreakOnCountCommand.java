package dbg.command;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;

public class BreakOnCountCommand extends Command {
    public BreakOnCountCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public Object execute() {
        BreakCommand breakCommand = new BreakCommand(getVm());
        breakCommand.setEvent(getEvent());
        breakCommand.setCommandLine(getCommandLine());
        BreakpointRequest breakpointRequest = (BreakpointRequest) breakCommand.execute();
        //breakpointRequest.disable();
        int count = Integer.parseInt(getCommandLine().substring(indexSecondCommas() + 1, getCommandLine().indexOf(")")));
        getBreakpointsToCount().put(breakpointRequest, count);
        return null;
    }
}
