package util;

import interfaces.ControllerListener;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Component.Identifier;

/**
 * Responsible for JInput
 */
public class JoystickController implements Runnable {

    private Controller controller       = null;
    protected boolean U                 = false;
    protected boolean D                 = false;
    protected boolean L                 = false;
    protected boolean R                 = false;
    protected boolean S                 = false;
    protected boolean B                 = false;
    private long sleepMillis            = 10;
    private ControllerListener listener = null;

    /**
     * Constructor
     * @param sleepMillis
     * @param listener
     */
    public JoystickController(ControllerListener listener) {
        /* Get the available controllers */
        ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] controllers = ce.getControllers();    
        this.listener = listener;
        for (int i = 0; controllers != null && i < controllers.length; i++) {
            var temp  = controllers[i];
            if (temp.getClass().getName().equals("net.java.games.input.DIAbstractController")) {
                this.controller = temp;
                break;
            }
        }
    }

    /**
     * Verify if any controller is connected
     */
    public boolean hasAnyConnectedController() {
        return (this.controller != null);
    }

    /**
     * Run method
     */
    @Override
    public void run() {
        if (this.controller != null) {
            while (true) {
                if (!controller.poll()) {
                    this.U          = false;
                    this.D          = false;
                    this.L          = false;
                    this.R          = false;
                    this.S          = false;
                    this.B          = false;
                    this.controller = null;
                    break;
                } else {
                    /* Get the controllers event queue */
                    EventQueue queue = controller.getEventQueue();

                    /* Create an event object for the underlying plugin to populate */
                    Event event = new Event();

                    /* For each object in the queue */
                    while (queue.getNextEvent(event)) {
                        
                        Component comp = event.getComponent();
                        Identifier id = comp.getIdentifier();
                        
                        if (null != id) {
                            if ("pov".equals(id.toString())) {
                                if (comp.getPollData() == 0.25f) {
                                    this.U = true;
                                    this.D = false;
                                    this.L = false;
                                    this.R = false;
                                } else if (comp.getPollData() == 0.375f) {
                                    this.U = true;
                                    this.L = false;
                                    this.R = true;
                                    this.D = false;
                                } else if (comp.getPollData() == 0.5f) {
                                    this.L = false;
                                    this.R = true;
                                    this.U = false;
                                    this.D = false;
                                }  else if (comp.getPollData() == 0.625f) {
                                    this.L = false;
                                    this.D = true;
                                    this.U = false;
                                    this.R = true;
                                } else if (comp.getPollData() == 0.75f) {
                                    this.D = true;
                                    this.U = false;
                                    this.L = false;
                                    this.R = false;
                                } else if (comp.getPollData() == 0.875f) {
                                    this.D = true;
                                    this.R = false;
                                    this.U = false;
                                    this.L = true;
                                } else if (comp.getPollData() == 1f) {
                                    this.R = false;
                                    this.L = true;
                                    this.U = false;
                                    this.D = false;
                                } else if (comp.getPollData() == 0.125f) {
                                    this.R = false;
                                    this.U = true;
                                    this.L = true;
                                    this.D = false;
                                } else if (comp.getPollData() == 0f) {
                                    this.U = false;
                                    this.D = false;
                                    this.L = false;
                                    this.R = false;
                                }
                            }
                        }
                    }
                }

                this.listener.notify(this.U, this.D, this.L, this.R);
            
                /*
                */
                try {
                    Thread.sleep(this.sleepMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}