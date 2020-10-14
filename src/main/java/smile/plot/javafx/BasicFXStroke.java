/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smile.plot.javafx;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 *
 * @author brucejohnson
 */
public class BasicFXStroke extends FXStroke{

    public BasicFXStroke(double width, StrokeLineCap cap, StrokeLineJoin join) {
            super(width, cap, join);
    }

    public BasicFXStroke(double width, StrokeLineCap cap, StrokeLineJoin join,
            double dashOffset, double... dashes) {
               super(width, cap, join, dashOffset, dashes);
    }

}
