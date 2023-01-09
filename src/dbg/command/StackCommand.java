package dbg.command;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Method;
import com.sun.jdi.VirtualMachine;

import java.util.ArrayList;
import java.util.List;

public class StackCommand extends Command{

    public StackCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public Object execute() {
        List<Method> listMethods = new ArrayList<>();
        try {
            getEvent().thread().frames().forEach(frame -> listMethods.add(frame.location().method()));
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        return listMethods;
    }

    @Override
    public void print(){
        try {
            getEvent().thread().frames().forEach(frame1->{
                System.out.println(frame1.location().method().name());
            });
            System.out.println();
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
    }
}
