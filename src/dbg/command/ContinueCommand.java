package dbg.command;

import com.sun.jdi.VirtualMachine;

public class ContinueCommand extends Command {

    public ContinueCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public Object execute() {
        return null;
    }
}
