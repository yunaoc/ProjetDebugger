package dbg.command;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;

public class BreakOnceCommand extends Command{

    public BreakOnceCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public Object execute() {
        BreakCommand breakCommand = new BreakCommand(getVm());
        breakCommand.setEvent(getEvent());
        breakCommand.setCommandLine(getCommandLine());
        BreakpointRequest breakpointRequest = (BreakpointRequest) breakCommand.execute();
        addBreakpoint(breakpointRequest);
        return breakpointRequest;
    }
}
