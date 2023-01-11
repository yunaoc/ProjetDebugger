package ihm;

import com.sun.jdi.*;
import dbg.JDISimpleDebuggee;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoutonsMouseListener implements MouseListener {
    private MainFrame mainFrame;
    private CommandeController debuggerInstance;

    public BoutonsMouseListener(MainFrame mainFrame){
        this.mainFrame = mainFrame;
        debuggerInstance = new CommandeController();
        debuggerInstance.attachTo(JDISimpleDebuggee.class);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        switch (((JButton)e.getSource()).getActionCommand()){
            case "Continue" -> {
                //debuggerInstance.getMapCommands().get("continue").execute();
                try {
                    updates();
                } catch (AbsentInformationException ex) {
                    ex.printStackTrace();
                }
            }
            case "Break" -> {}
            case "Step" -> {}
            case "StepOver" -> {}
            //TODO
        }
    }

    private void updates() throws AbsentInformationException {
        StackFrame frame = (StackFrame) debuggerInstance.getMapCommands().get("frame").execute();

        List<Method> methods = (List<Method>) debuggerInstance.getMapCommands().get("stack").execute();
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
        Map<LocalVariable, Value> mapArguments = (Map<LocalVariable, Value>) debuggerInstance.getMapCommands().get("arguments").execute();
        for (Map.Entry<LocalVariable, Value> entry : mapArguments.entrySet()) {
            arguments.add (entry.getKey().name() + " -> " + entry.getValue());
        }

        mainFrame.initTextContexte(frame.location().toString(),methodesNoms,temporaries,arguments);
        //TODO tests
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
