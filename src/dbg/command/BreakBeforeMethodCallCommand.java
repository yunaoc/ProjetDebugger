package dbg.command;

import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;

import java.util.List;

public class BreakBeforeMethodCallCommand extends Command{
    public BreakBeforeMethodCallCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public Object execute() {
        String methodeName = getCommandLine().substring(getCommandLine().indexOf("(") + 1, getCommandLine().indexOf(")"));
        int firstLineMethod = -1;
        ReferenceType classe = getVm().classesByName(getClassDebugged().getName()).get(0);

        if(null != classe){
            List<Method> methods = classe.methodsByName(methodeName);
            firstLineMethod = methods.get(0).location().lineNumber();
            if(firstLineMethod>0) {
                BreakCommand breakCommand = new BreakCommand(getVm());
                breakCommand.setEvent(getEvent());
                breakCommand.setCommandLine("(" + getClassDebugged().getName() + "," + firstLineMethod + ")");
                breakCommand.execute();
            }
        }
        return null;
    }
}
