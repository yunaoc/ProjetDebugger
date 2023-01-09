package dbg.command;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Method;
import com.sun.jdi.StackFrame;
import com.sun.jdi.VirtualMachine;

public class MethodCommand extends Command{

    private Method method;

    public MethodCommand(VirtualMachine vm) {
        super(vm);
        method = null;
    }

    @Override
    public Object execute() {
        FrameCommand frameCommand = new FrameCommand(getVm());
        frameCommand.setEvent(getEvent());
        method = ((StackFrame) frameCommand.execute()).location().method();
        return method;
    }

    @Override
    public void print(){
        System.out.println(method.name());
    }
}
