package dbg.command;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.StepRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Command {
    private VirtualMachine vm;
    private LocatableEvent event;
    private StepRequest stepRequest;
    private String commandLine;
    private final List<BreakpointRequest> breakpointsToDisable;
    private final Map<BreakpointRequest, Integer> breakpointsToCount;

    public Command(VirtualMachine vm) {
        this.vm = vm;
        stepRequest = null;
        breakpointsToDisable = new ArrayList<>();
        breakpointsToCount = new HashMap<>();
    }

    public abstract Object execute();

    public void print() {
    }

    public VirtualMachine getVm() {
        return vm;
    }

    public void setVm(VirtualMachine vm) {
        this.vm = vm;
    }

    public LocatableEvent getEvent() {
        return event;
    }

    public void setEvent(LocatableEvent event) {
        this.event = event;
    }

    public StepRequest getStepRequest() {
        return stepRequest;
    }

    public void setStepRequest(StepRequest stepRequest) {
        this.stepRequest = stepRequest;
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public void addBreakpoint(BreakpointRequest breakpointRequest) {
        breakpointsToDisable.add(breakpointRequest);
    }

    public List<BreakpointRequest> getBreakpointsToDisable() {
        return breakpointsToDisable;
    }

    public Map<BreakpointRequest, Integer> getBreakpointsToCount() {
        return breakpointsToCount;
    }

    int indexSecondCommas() {
        int index = 0;
        for (int i = getCommandLine().indexOf("("); i < getCommandLine().indexOf(")"); i++) {
            char c = getCommandLine().charAt(i);
            if (c == ',') {
                if (index != 0) {
                    index = i;
                    break;
                }
                index = i;
            }
        }
        return index;
    }
}
