package dbg.command;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.VirtualMachine;

public class FrameCommand extends Command{

    private StackFrame frame;

    public FrameCommand(VirtualMachine vm) {
        super(vm);
        frame = null;
    }

    @Override
    public void execute() {
        try {
            frame = getEvent().thread().frame(getEvent().thread().frameCount()-1);
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void print(){
        //TODO Afficher les infos de la frame
        System.out.println(frame);
    }
}
