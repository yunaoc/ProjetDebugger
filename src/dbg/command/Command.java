package dbg.command;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.StepRequest;

public abstract class Command {
    private VirtualMachine vm;
    private LocatableEvent event;
    private StepRequest stepRequest;
    private String commandLine;

    public Command(VirtualMachine vm) {
        this.vm = vm;
        stepRequest = null;
    }

    public abstract Object execute();
    public void print(){};

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
}
