package dbg.command;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;

public class StepOverCommand extends Command {

    public StepOverCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public void execute() {
        setStepRequest(getVm().eventRequestManager().createStepRequest(getEvent().thread(),
                StepRequest.STEP_LINE,
                StepRequest.STEP_OVER));
        getStepRequest().enable();
    }
}
