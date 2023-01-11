package dbg.command;

import com.sun.jdi.*;

import java.util.ArrayList;
import java.util.Map;

public class ReceiverVariablesCommand extends Command{
    private ObjectReferenceInfo receiver;
    public ReceiverVariablesCommand(VirtualMachine vm) {
        super(vm);
        receiver = null;
    }

    @Override
    public Object execute() {
        ReceiverCommand receiverCommand = new ReceiverCommand(getVm());
        receiverCommand.setEvent(getEvent());
        receiver = (ObjectReferenceInfo) receiverCommand.execute();
        if(null != receiver.getObjectReference()){
            return new ArrayList<>(receiver.getObjectReference().getValues(receiver.getLocation().declaringType().allFields()).values());
        }
        return new ArrayList<>();
    }

    @Override
    public void print(){
        if(null != receiver.getObjectReference()) {
            Map<Field, Value> valeurs = receiver.getObjectReference().getValues(receiver.getLocation().declaringType().allFields());
            for (Map.Entry<Field, Value> entry : valeurs.entrySet()) {
                System.out.println(entry.getKey().name() + " -> " + entry.getValue());
            }
        }
        System.out.println("Methode static : " + receiver.getLocation().method().name());
    }
}
