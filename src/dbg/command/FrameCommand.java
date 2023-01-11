package dbg.command;

import com.sun.jdi.*;

import java.util.List;
import java.util.Map;

public class FrameCommand extends Command {

    private StackFrame frame;

    public FrameCommand(VirtualMachine vm) {
        super(vm);
        frame = null;
    }

    @Override
    public Object execute() {
        try {
            frame = getEvent().thread().frame(0);
            return frame;
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void print() {
        System.out.println("Location : " + frame.location());
        try {
            List<LocalVariable> variables = frame.visibleVariables();
            System.out.println("Variables : ");
            Map<LocalVariable, Value> map = frame.getValues(variables);
            variables.forEach(variable -> {
                System.out.println("name : " + variable.name());
                System.out.println("type name : " + variable.typeName());
                System.out.println("value : " + map.get(variable));
                System.out.println();
            });
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
    }
}
