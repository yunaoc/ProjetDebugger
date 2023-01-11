package dbg.command;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.VirtualMachine;

public class ReceiverVariableCommand extends Command{
    private ObjectReferenceInfo receiver;
    public ReceiverVariableCommand(VirtualMachine vm) {
        super(vm);
        receiver = null;
    }

    @Override
    public Object execute() {
        ReceiverCommand receiverCommand = new ReceiverCommand(getVm());
        receiverCommand.setEvent(getEvent());
        receiver = (ObjectReferenceInfo) receiverCommand.execute();
        return receiver;
    }

    @Override
    public void print(){
        receiver.getObjectReference().getValues(receiver.getLocation().declaringType().allFields());
        //TODO afficher si non null
    }
}
