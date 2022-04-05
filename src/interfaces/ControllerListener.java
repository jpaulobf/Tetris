package interfaces;
/**
 * Controller Listener
 */
public interface ControllerListener {
    public void notify(boolean U, 
                       boolean D, 
                       boolean L, 
                       boolean R, 
                       boolean HOLD, 
                       boolean DROP, 
                       boolean ROTATE);
}
