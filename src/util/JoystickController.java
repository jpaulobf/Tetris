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
public class JoystickController {

    private Controller controller           = null;
    protected boolean U                     = false;
    protected boolean D                     = false;
    protected boolean L                     = false;
    protected boolean R                     = false;
    protected boolean HOLD                  = false;
    protected boolean DROP                  = false;
    protected boolean ROTATE                = false;
    private ControllerListener listener     = null;
    private volatile long framecounter      = 0;
    private volatile boolean actionTrigger  = false;
    private volatile long actionCounter     = 0;
    private final long TRIGGER_MAXTIME      = 40_000_000L;
    private volatile boolean canAction      = true;

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
     * Update Method
     */
    public void update(long frametime) {
        this.framecounter += frametime;

        if (this.controller != null && this.framecounter > 50_000_000L) {
            this.framecounter = 0;
            
            if (this.actionTrigger) {
                this.actionCounter += frametime;
                if (this.actionCounter >= this.TRIGGER_MAXTIME) {
                    this.canAction      = true;
                    this.actionCounter  = 0;
                    this.actionTrigger  = false;
                    System.out.println("aqui...");
                } else {
                    this.ROTATE         = false;
                    this.HOLD           = false;
                    this.DROP           = false;
                }
            }

            if (!controller.poll()) {
                this.U          = false;
                this.D          = false;
                this.L          = false;
                this.R          = false;
                this.HOLD       = false;
                this.DROP       = false;
                this.controller = null;
            } else {
                /* Get the controllers event queue */
                EventQueue queue = controller.getEventQueue();

                /* Create an event object for the underlying plugin to populate */
                Event event = new Event();

                /* For each object in the queue */
                while (queue.getNextEvent(event)) {
                    System.out.println("entrei");
                    
                    Component comp = event.getComponent();
                    Identifier id = comp.getIdentifier();
                    
                    if (null != id) {
                        //if () { //Movements
                            if ("pov".equals(id.toString()) && comp.getPollData() == 0.25f) {
                                this.U = true;
                                this.D = false;
                                this.L = false;
                                this.R = false;
                            } else if ("pov".equals(id.toString()) && comp.getPollData() == 0.375f) {
                                this.U = true;
                                this.L = false;
                                this.R = true;
                                this.D = false;
                            } else if ("pov".equals(id.toString()) && comp.getPollData() == 0.5f) {
                                this.L = false;
                                this.R = true;
                                this.U = false;
                                this.D = false;
                            }  else if ("pov".equals(id.toString()) && comp.getPollData() == 0.625f) {
                                this.L = false;
                                this.D = true;
                                this.U = false;
                                this.R = true;
                            } else if ("pov".equals(id.toString()) && comp.getPollData() == 0.75f) {
                                this.D = true;
                                this.U = false;
                                this.L = false;
                                this.R = false;
                            } else if ("pov".equals(id.toString()) && comp.getPollData() == 0.875f) {
                                this.D = true;
                                this.R = false;
                                this.U = false;
                                this.L = true;
                            } else if ("pov".equals(id.toString()) && comp.getPollData() == 1f) {
                                this.R = false;
                                this.L = true;
                                this.U = false;
                                this.D = false;
                            } else if ("pov".equals(id.toString()) && comp.getPollData() == 0.125f) {
                                this.R = false;
                                this.U = true;
                                this.L = true;
                                this.D = false;
                            } else if ("pov".equals(id.toString()) && comp.getPollData() == 0f) {
                                this.U = false;
                                this.D = false;
                                this.L = false;
                                this.R = false;
                            }
                        //} else { //Actions
                            
                            String action = comp.getIdentifier().toString();
                            if (("0".equals(action) ||
                                 "1".equals(action) ||
                                 "2".equals(action) ||
                                 "3".equals(action) || 
                                 "5".equals(action)) && canAction) {

                                if (event.getValue() == 1.0f) {
                                    if ("0".equals(action) || "2".equals(action)) {
                                        this.ROTATE = true;
                                    } else if ("5".equals(action)) {
                                        this.HOLD = true;
                                    } else if ("1".equals(action) || "3".equals(action)) {
                                        this.DROP = true;
                                    }
                                    //action triggered
                                    this.actionTrigger  = true;
                                    this.canAction      = false;
                                }
                            }
                        //}
                    }
                }
            }
            this.listener.notify(this.U, this.D, this.L, this.R, this.HOLD, this.DROP, this.ROTATE);
        }
    }
}