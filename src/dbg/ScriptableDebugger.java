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

    public VirtualMachine connectAndLaunchVM() throws IOException, IllegalConnectorArgumentsException, VMStartException {
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(debugClass.getName());
        VirtualMachine vm = launchingConnector.launch(arguments);
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

        while ((eventSet = vm.eventQueue().remove()) != null) {
            for (Event event : eventSet) {
                char[] buf = new char[vm.process().getInputStream().available()];
                reader.read(buf);
                writer.write(buf);
                writer.flush();

                if(event instanceof ClassPrepareEvent) {
                    setBreakPoint(debugClass.getName(), 6);
                }

                if(event instanceof VMDisconnectEvent){
                    System.out.println("===End of program.");
                    return;
                }

                if(event instanceof BreakpointEvent){
                    Command command = mapCommands.get(readCommand());
                    command.setEvent((BreakpointEvent) event);
                    command.execute();
                }

                if(event instanceof StepEvent){
                    String command = readCommand();
                    if(null != mapCommands.get("step").getStepRequest()){
                        mapCommands.get(command).setEvent(mapCommands.get("step").getEvent());
                        mapCommands.get(command).setStepRequest(mapCommands.get("step").getStepRequest());
                        vm.eventRequestManager().deleteEventRequest(mapCommands.get("step").getStepRequest());
                    }
                    if(null != mapCommands.get("step-over").getStepRequest()){
                        mapCommands.get(command).setEvent(mapCommands.get("step-over").getEvent());
                        mapCommands.get(command).setStepRequest(mapCommands.get("step-over").getStepRequest());
                        vm.eventRequestManager().deleteEventRequest(mapCommands.get("step-over").getStepRequest());
                    }
                    mapCommands.get(command).execute();
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

    public void setBreakPoint(String className, int lineNumber) throws AbsentInformationException {
        for(ReferenceType targetClass : vm.allClasses()){
            if(targetClass.name().equals(className)){
                Location location = targetClass.locationsOfLine(lineNumber).get(0);
                BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
                bpReq.enable();
            }
        }
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

        mapCommands.put("step", stepCommand);
        mapCommands.put("step-over", stepOverCommand);
        mapCommands.put("continue", continueCommand);
        mapCommands.put("frame", frameCommand);

    }

}


