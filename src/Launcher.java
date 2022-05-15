import game.SplashScreen;

/**
 * Laucher class
 */
public class Launcher {

    public static final boolean setOpenGL = false;

    public static void main(String[] args) {
        //enable the openGL
        if (setOpenGL) {
            System.setProperty("sun.java2d.opengl", "True");
        } else {
            System.setProperty("sun.java2d.d3d", "True");
        }
        System.setProperty("sun.java2d.translaccel", "true");
		System.setProperty("sun.java2d.ddforcevram", "true");
        
        
        //start the thread
        //--->>> FPS options (SplashScreen constructor) - 0 (unlimited) - 30/60/90/120/240
        Thread thread = new Thread(new SplashScreen(60), "engine");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }
}
