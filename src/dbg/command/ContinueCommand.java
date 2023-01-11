package dbg.command;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.request.BreakpointRequest;

import java.util.List;

public class ContinueCommand extends Command{

    public ContinueCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public Object execute() {
        while(!(getEvent() instanceof BreakpointEvent)){
            StepCommand stepCommand = new StepCommand(getVm());
            stepCommand.setEvent(getEvent());
            getVm().eventRequestManager().stepRequests().forEach(stepRequest ->getVm().eventRequestManager().deleteEventRequest(stepRequest));
            stepCommand.execute();
            setStepRequest(stepCommand.getStepRequest());
        }
        return null;
    }
}
