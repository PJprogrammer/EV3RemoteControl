
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RemoteEV3;
import net.java.games.input.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main {
    Controller controller;
    RemoteEV3 ev3;
    RMIRegulatedMotor leftMotor;
    RMIRegulatedMotor rightMotor;
    RMIRegulatedMotor clawMotor;
    private static int MAXSPEED = 400;

    public Main(Controller[] controllers) {
        int selectedController = 0;

        for(int i =0;i<controllers.length;i++)
            if(controllers[i].getType() == Controller.Type.STICK)
                selectedController = i;

        controller = controllers[selectedController];

        connectToEV3();
    }

    public void detectEvents() {


    }

    public void connectToEV3(){
        // Detecting EV3 Brick
        try {
            ev3 = new RemoteEV3("10.0.0.17");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

        // Creating objects for motor
        leftMotor = ev3.createRegulatedMotor("B", 'L');
        rightMotor = ev3.createRegulatedMotor("C", 'L');
        clawMotor = ev3.createRegulatedMotor("A",'M');

        Boolean stopped = false;
        EventQueue eventQueue = controller.getEventQueue();
        Event event = new Event();

        //Controller Values
        int YAxisValue = 0;
        int RZAxisValue = 0;

        while (!stopped) {
            controller.poll();
            eventQueue.getNextEvent(event);

            Component comp = event.getComponent();
            if(comp != null) {
                Component.Identifier identifier = comp.getIdentifier();
                float compData = comp.getPollData();

                if (identifier == Component.Identifier.Axis.Y) {
                    YAxisValue = convertToPercentage(compData);
                    try {
                        leftMotor.setSpeed(YAxisValue);
                        if(YAxisValue >= 0)
                            leftMotor.forward();
                        else
                            leftMotor.backward();
                    } catch (RemoteException e) {
                        e.printStackTrace();

                    }
                }
                else if (identifier == Component.Identifier.Axis.RZ) {
                    RZAxisValue = convertToPercentage(compData);
                    try {
                        rightMotor.setSpeed(RZAxisValue);
                        if(RZAxisValue >= 0)
                            rightMotor.forward();
                        else
                            rightMotor.backward();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                else if(identifier == Component.Identifier.Button._1) {

                    try{clawMotor.setSpeed(100);
                        clawMotor.forward();} catch (Exception e) {}
                }
                else if(identifier == Component.Identifier.Button._3) {
                    try{ clawMotor.setSpeed(100);
                        clawMotor.backward();} catch (Exception e) {}
                }
                else if (identifier == Component.Identifier.Button._0){
                    stopped = true;
                }
            }
        }

        try {
            leftMotor.stop(true);
            rightMotor.stop(true);
            clawMotor.stop(true);
            leftMotor.close();
            rightMotor.close();
            clawMotor.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int convertToPercentage(float compData){
        return (int)-((((2 - (1 - compData)) * 100) / 2)-50)*(MAXSPEED/50);
    }

    public static void main(String[] args) {
        Main main = new Main(ControllerEnvironment.getDefaultEnvironment().getControllers());
       // main.detectEvents();
    }

}
