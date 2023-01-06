package dbg.command;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;

public class StepCommand extends Command {

    public StepCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public void execute() {
         setStepRequest(getVm().eventRequestManager().createStepRequest(getEvent().thread(),
                 StepRequest.STEP_MIN,
                 StepRequest.STEP_INTO));
         getStepRequest().enable();
    }
}
