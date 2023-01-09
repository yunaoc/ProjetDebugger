package dbg.command;

import com.sun.jdi.*;

import java.util.List;
import java.util.Map;

public class TemporariesCommand extends Command{

    private StackFrame temporaries;

    public TemporariesCommand(VirtualMachine vm) {
        super(vm);
        temporaries = null;
    }

    @Override
    public Object execute() {
        FrameCommand frameCommand = new FrameCommand(getVm());
        frameCommand.setEvent(getEvent());
        temporaries = (StackFrame) frameCommand.execute();
        return temporaries;
    }

    @Override
    public void print(){
        try {
            List<LocalVariable> variables = temporaries.visibleVariables();
            Map<LocalVariable, Value> map = temporaries.getValues(variables);
            variables.forEach(variable ->{
                System.out.println(variable.name() + " -> "+ map.get(variable));
            });
            System.out.println();
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
    }
}
