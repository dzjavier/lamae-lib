package testcode;

import ij.*;
import ij.gui.*;
import ij.plugin.PlugIn;
import java.awt.*;

/** This plugin demonstrates how to use ImageJ's Plot class.
 *  @author Hajime Hirase
 *  @version 1.0
*/
public class Example_Plot implements PlugIn {

    public void run(String arg) {
            
        float[] x = {0.1f, 0.25f, 0.35f, 0.5f, 0.61f,0.7f,0.85f,0.89f,0.95f}; // x-coordinates
        float[] y = {2f,5.6f,7.4f,9f,9.4f,8.7f,6.3f,4.5f,1f}; // x-coordinates
        float[] e = {.8f,.6f,.5f,.4f,.3f,.5f,.6f,.7f,.8f}; // error bars

        PlotWindow.noGridLines = false; // draw grid lines
        Plot plot = new Plot("Example Plot","X Axis","Y Axis",x,y);
        plot.setLimits(0, 1, 0, 10);
        plot.setLineWidth(2);
        plot.addErrorBars(e);

        // add a second curve
        float x2[] = {.4f,.5f,.6f,.7f,.8f};
        float y2[] = {4,3,3,4,5};
        plot.setColor(Color.red);
        plot.addPoints(x2,y2,PlotWindow.X);
        plot.addPoints(x2,y2,PlotWindow.LINE);

        // add label
        plot.setColor(Color.black);
        plot.changeFont(new Font("Helvetica", Font.PLAIN, 24));
        plot.addLabel(0.15, 0.95, "This is a label");

        plot.changeFont(new Font("Helvetica", Font.PLAIN, 16));
        plot.setColor(Color.blue);
        plot.show();
    }
}
