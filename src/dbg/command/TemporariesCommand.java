package dbg.command;

import com.sun.jdi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemporariesCommand extends Command {

    private Map<String, Value> temporaries;

    public TemporariesCommand(VirtualMachine vm) {
        super(vm);
        temporaries = new HashMap<>();
    }

    @Override
    public Object execute() {
        FrameCommand frameCommand = new FrameCommand(getVm());
        frameCommand.setEvent(getEvent());
        StackFrame frame = (StackFrame) frameCommand.execute();
        try {
            List<LocalVariable> variables = frame.visibleVariables();
            Map<LocalVariable, Value> map = frame.getValues(variables);
            for (Map.Entry<LocalVariable, Value> entry : map.entrySet()) {
                temporaries.put(entry.getKey().name(), entry.getValue());
            }
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
        return temporaries;
    }

    @Override
    public void print() {
        for (Map.Entry<String, Value> entry : temporaries.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}
