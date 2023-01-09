package dbg;

import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.StepRequest;
import dbg.command.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class ScriptableDebugger {

    private Class debugClass;
    private VirtualMachine vm;
    private Map<String, Command> mapCommands;
    private LocatableEvent eventCommand;

    public VirtualMachine connectAndLaunchVM() throws IOException, IllegalConnectorArgumentsException, VMStartException {
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(debugClass.getName());
        VirtualMachine vm = launchingConnector.launch(arguments);
        eventCommand = null;
        return vm;
    }
    public void attachTo(Class debuggeeClass) {

        this.debugClass = debuggeeClass;
        try {
            vm = connectAndLaunchVM();
            enableClassPrepareRequest(vm);
            startDebugger();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalConnectorArgumentsException e) {
            e.printStackTrace();
        } catch (VMStartException e) {
            e.printStackTrace();
            System.out.println(e.toString());
        } catch (VMDisconnectedException e) {
            System.out.println("Virtual Machine is disconnected: " + e.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDebugger() throws VMDisconnectedException, InterruptedException, IOException, AbsentInformationException {
        EventSet eventSet = null;
        initializationCommands();
        System.out.println("Debuggee output ===");
        InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        Boolean isInitialized = false;
        String commandLine = "";

        while ((eventSet = vm.eventQueue().remove()) != null) {
            for (Event event : eventSet) {
                char[] buf = new char[vm.process().getInputStream().available()];
                reader.read(buf);
                writer.write(buf);
                writer.flush();

                if(event instanceof VMDeathEvent){
                    isInitialized = false;
                }

                if(event instanceof ClassPrepareEvent) {
                    isInitialized = true;
                }

                if(event instanceof VMDisconnectEvent){
                    System.out.println("===End of program.");
                    return;
                }

                if(event instanceof BreakpointEvent){
                    eventCommand = (BreakpointEvent) event;
                    //TODO probleme deux event alors qu'on en save que un
                }

                if(isInitialized){
                    commandLine = readCommand();
                    String sub = commandLine;
                    if(commandLine.contains("(")){
                        commandLine = commandLine.substring(0,commandLine.indexOf("("));
                    }
                    mapCommands.get(commandLine).setEvent(eventCommand);
                    mapCommands.get(commandLine).setCommandLine(sub);

                    boolean isModifStepRequest = commandLine.equals("step") || commandLine.equals("step-over") || commandLine.equals("continue");

                    if(null != mapCommands.get("step").getStepRequest() && isModifStepRequest){
                        mapCommands.get(commandLine).setStepRequest(mapCommands.get("step").getStepRequest());
                        vm.eventRequestManager().deleteEventRequest(mapCommands.get("step").getStepRequest());
                    }
                    else if(null != mapCommands.get("step-over").getStepRequest() && isModifStepRequest){
                        mapCommands.get(commandLine).setStepRequest(mapCommands.get("step-over").getStepRequest());
                        vm.eventRequestManager().deleteEventRequest(mapCommands.get("step-over").getStepRequest());
                    }
                    mapCommands.get(commandLine).execute();
                    mapCommands.get(commandLine).print();
                }

                System.out.println(event.toString());
                vm.resume();
            }
        }
    }

    public void enableClassPrepareRequest(VirtualMachine vm) {
        ClassPrepareRequest classPrepareRequest =
                vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(debugClass.getName());
        classPrepareRequest.enable();
    }

    private String readCommand() throws IOException {
        System.out.println("Enter command : ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
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

}


