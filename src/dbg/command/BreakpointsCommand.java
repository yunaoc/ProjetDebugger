package dbg.command;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequest;

import java.util.ArrayList;
import java.util.List;

public class BreakpointsCommand extends Command {

    List<BreakpointRequest> breakpointRequestList;


    public BreakpointsCommand(VirtualMachine vm) {
        super(vm);
        breakpointRequestList = new ArrayList<>();
    }

    @Override
    public Object execute() {
        List<BreakpointRequest> allBreakpointRequestList = getVm().eventRequestManager().breakpointRequests();
        breakpointRequestList = allBreakpointRequestList.stream().filter(EventRequest::isEnabled).toList();
        return breakpointRequestList;
    }

    @Override
    public void print() {
        breakpointRequestList.forEach(breakpoint -> System.out.println(breakpoint.location().method().name() + " : " + breakpoint.location().lineNumber()));
        System.out.println();
    }
}
