package dbg;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
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
    private LocatableEvent eventCommandActual;

    public VirtualMachine connectAndLaunchVM() throws IOException, IllegalConnectorArgumentsException, VMStartException {
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(debugClass.getName());
        VirtualMachine vm = launchingConnector.launch(arguments);
        eventCommandActual = null;
        return vm;
    }

    public void attachTo(Class debuggeeClass) {

        this.debugClass = debuggeeClass;
        try {
            vm = connectAndLaunchVM();
            enableClassPrepareRequest(vm);
            startDebugger();

        } catch (VMStartException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (VMDisconnectedException e) {
            System.out.println("Virtual Machine is disconnected: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDebugger() throws VMDisconnectedException, InterruptedException, IOException {
        EventSet eventSet = null;
        initializationCommands();
        System.out.println("Debuggee output ===");
        InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        boolean isInitialized = false;
        String commandLine = "";
        boolean dispo = true;

        while ((eventSet = vm.eventQueue().remove()) != null) {
            for (Event event : eventSet) {
                char[] buf = new char[vm.process().getInputStream().available()];
                reader.read(buf);
                writer.write(buf);
                writer.flush();

                if (event instanceof VMDeathEvent) {
                    isInitialized = false;
                }

                if (event instanceof ClassPrepareEvent) {
                    isInitialized = true;
                }

                if (event instanceof VMDisconnectEvent) {
                    System.out.println("===End of program.");
                    return;
                }

                if (event instanceof BreakpointEvent) {
                    System.out.println("Breakpoint : " + event);
                    for (BreakpointRequest registerEvent : vm.eventRequestManager().breakpointRequests()) {
                        if (registerEvent.isEnabled() && event.request().equals(registerEvent)) {
                            Integer value = mapCommands.get("break-on-count").getBreakpointsToCount().get(registerEvent);
                            if (null != value && event.request().equals(registerEvent)) {
                                if (value == 0) {
                                    mapCommands.get("break-on-count").getBreakpointsToCount().remove(registerEvent);
                                    eventCommandActual = (BreakpointEvent) event;
                                    dispo = true;
                                } else {
                                    dispo = false;
                                    System.out.println("Map event : " + mapCommands.get("break-on-count").getBreakpointsToCount().get(registerEvent));
                                    mapCommands.get("break-on-count").getBreakpointsToCount().put(registerEvent, value - 1);
                                }
                            } else {
                                eventCommandActual = (BreakpointEvent) event;
                                if (mapCommands.get("break-once").getBreakpointsToDisable().contains(registerEvent)) {
                                    eventCommandActual.request().disable();
                                }
                            }
                        }
                    }
                }

                if (isInitialized && dispo) {
                    commandLine = readCommand();
                    String sub = commandLine;
                    if (commandLine.contains("(")) {
                        commandLine = commandLine.substring(0, commandLine.indexOf("("));
                    }
                    while (!mapCommands.containsKey(commandLine)) {
                        commandLine = readCommand();
                        sub = commandLine;
                        if (commandLine.contains("(")) {
                            commandLine = commandLine.substring(0, commandLine.indexOf("("));
                        }
                    }
                    mapCommands.get(commandLine).setEvent(eventCommandActual);
                    mapCommands.get(commandLine).setCommandLine(sub);

                    if (commandLine.equals("step") || commandLine.equals("step-over") || commandLine.equals("continue")) {
                        vm.eventRequestManager().stepRequests().forEach(stepRequest -> vm.eventRequestManager().deleteEventRequest(stepRequest));
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

    private void initializationCommands() {
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
        BreakOnceCommand breakOnceCommand = new BreakOnceCommand(vm);
        ReceiverCommand receiverCommand = new ReceiverCommand(vm);
        ReceiverVariablesCommand receiverVariablesCommand = new ReceiverVariablesCommand(vm);
        SenderCommand senderCommand = new SenderCommand(vm);
        BreakOnCountCommand breakOnCountCommand = new BreakOnCountCommand(vm);

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
        mapCommands.put("break-once", breakOnceCommand);
        mapCommands.put("receiver", receiverCommand);
        mapCommands.put("receiver-variables", receiverVariablesCommand);
        mapCommands.put("sender", senderCommand);
        mapCommands.put("break-on-count", breakOnCountCommand);
    }

}


