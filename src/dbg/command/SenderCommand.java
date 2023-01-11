package dbg.command;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.VirtualMachine;

public class SenderCommand extends Command{
    public SenderCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public Object execute() {
        try {
            if(getEvent().thread().frames().size() > 1){
                StackFrame frame = getEvent().thread().frame(1);
                ObjectReferenceInfo objectReferenceInfo = new ObjectReferenceInfo();
                objectReferenceInfo.setObjectReference(frame.thisObject());
                objectReferenceInfo.setLocation(frame.location());
                return objectReferenceInfo;
            }
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        return null;
    }
}
