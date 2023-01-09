package dbg.command;

import com.sun.jdi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArgumentsCommand extends Command{
    private Map<LocalVariable, Value> valeurs;

    public ArgumentsCommand(VirtualMachine vm) {
        super(vm);
        valeurs = new HashMap<>();
    }

    @Override
    public Object execute() {
        MethodCommand methodCommand = new MethodCommand(getVm());
        methodCommand.setEvent(getEvent());
        FrameCommand frameCommand = new FrameCommand(getVm());
        frameCommand.setEvent(getEvent());

        StackFrame frame = (StackFrame) frameCommand.execute();
        Method method = (Method) methodCommand.execute();
        try {
            List<LocalVariable> arguments = method.arguments();
            valeurs = frame.getValues(arguments);
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
        return valeurs;
    }

    @Override
    public void print(){
        for (Map.Entry<LocalVariable, Value> entry : valeurs.entrySet()) {
            System.out.println(entry.getKey().name() + " -> " + entry.getValue());
        }
        System.out.println();
    }
}
