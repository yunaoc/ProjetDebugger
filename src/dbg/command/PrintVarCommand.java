package dbg.command;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class PrintVarCommand extends Command{

    private Value valeur;

    public PrintVarCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public Object execute() {
        String variable = getCommandLine().substring(getCommandLine().indexOf("(")+1, getCommandLine().indexOf(")"));
        FrameCommand frameCommand = new FrameCommand(getVm());
        frameCommand.setEvent(getEvent());
        StackFrame frame = (StackFrame) frameCommand.execute();
        try {
            valeur = frame.getValue(frame.visibleVariableByName(variable));
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
        return valeur;
    }

    @Override
    public void print(){
        System.out.println(valeur);
        System.out.println();
    }
}
