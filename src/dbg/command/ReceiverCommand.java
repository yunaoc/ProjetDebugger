package dbg.command;

import com.sun.jdi.StackFrame;
import com.sun.jdi.VirtualMachine;

public class ReceiverCommand extends Command {
    public ReceiverCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public Object execute() {
        FrameCommand frameCommand = new FrameCommand(getVm());
        frameCommand.setEvent(getEvent());
        StackFrame frame = (StackFrame) frameCommand.execute();
        ObjectReferenceInfo objectReferenceInfo = new ObjectReferenceInfo();
        objectReferenceInfo.setObjectReference(frame.thisObject());
        objectReferenceInfo.setLocation(frame.location());
        return objectReferenceInfo;
    }
}
