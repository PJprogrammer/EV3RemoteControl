import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Rumbler;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");


        int selectedController = 0;
        Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for(int i =0;i<ca.length;i++)
            if(ca[i].getType() == Controller.Type.STICK)
                selectedController=i;

        Controller controller = ca[selectedController];
        Component[] components = controller.getComponents();

        for(int i = 0;i < components.length;i++)
            System.out.println(components[i]);

        Component component = components[5];
        //Component.Identifier componentIdentifier = component.getIdentifier();
        try{
            component.getPollData();
        }catch (Exception e){
            e.printStackTrace();
        }

        Boolean stopped = false;


        while(true){
            controller.poll();
            System.out.println(components[5].getPollData());
        }




    }
}
