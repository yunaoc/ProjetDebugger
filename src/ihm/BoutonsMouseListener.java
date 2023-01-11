package ihm;

import com.sun.jdi.*;
import dbg.command.*;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoutonsMouseListener implements MouseListener {
    private MainFrame mainFrame;
    private Map<String, Command> mapCommands;

    public BoutonsMouseListener(MainFrame mainFrame){

        this.mainFrame = mainFrame;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        switch (((JButton)e.getSource()).getActionCommand()){
            case "Continue" -> {
                mapCommands.get("continue").execute();
                updates();
            }
            case "Break" -> {}
            case "Step" -> {}
            case "StepOver" -> {}
            //TODO
        }
    }

    private void updates() throws AbsentInformationException {
        StackFrame frame = (StackFrame) mapCommands.get("frame").execute();

        List<Method> methods = (List<Method>) mapCommands.get("stack").execute();
        List<String> methodesNoms = new ArrayList<>();
        for(Method nomMethod : methods){
            methodesNoms.add(nomMethod.name());
        }

        List<String> temporaries = new ArrayList<>();
        List<LocalVariable> variables = frame.visibleVariables();
        Map<LocalVariable, Value> mapTempo = frame.getValues(variables);
        for(LocalVariable variable : variables){
            temporaries.add(variable.name() + " -> "+ mapTempo.get(variable));
        }

        List<String> arguments = new ArrayList<>();
        Map<LocalVariable, Value> mapArguments = (Map<LocalVariable, Value>) mapCommands.get("arguments").execute();
        for (Map.Entry<LocalVariable, Value> entry : mapArguments.entrySet()) {
            arguments.add (entry.getKey().name() + " -> " + entry.getValue());
        }

        mainFrame.initTextContexte(frame.location().toString(),methodesNoms,temporaries,arguments);
        //TODO tests
    }

    private void initializationCommands(){
        mapCommands = new HashMap<>();
        StepCommand stepCommand = new StepCommand(vm);
        StepOverCommand stepOverCommand = new StepOverCommand(vm);
        ContinueCommand continueCommand = new ContinueCommand(vm);
        FrameCommand frameCommand = new FrameCommand(vm);
        TemporariesCommand temporariesCommand = new TemporariesCommand(vm);
        StackCommand stackCommand = new StackCommand(vm);
        MethodCommand methodCommand = new MethodCommand(vm);
        ArgumentsCommand argumentsCommand = new ArgumentsCommand(vm);
        PrintVarCommand printVarCommand = new PrintVarCommand(vm);
        BreakCommand breakCommand = new BreakCommand(vm);
        BreakpointsCommand breakpointsCommand = new BreakpointsCommand(vm);

        mapCommands.put("step", stepCommand);
        mapCommands.put("step-over", stepOverCommand);
        mapCommands.put("continue", continueCommand);
        mapCommands.put("frame", frameCommand);
        mapCommands.put("temporaries", temporariesCommand);
        mapCommands.put("stack", stackCommand);
        mapCommands.put("method", methodCommand);
        mapCommands.put("arguments", argumentsCommand);
        mapCommands.put("print-var", printVarCommand);
        mapCommands.put("break", breakCommand);
        mapCommands.put("breakpoints", breakpointsCommand);
    }





    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
