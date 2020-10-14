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
public class FXStroke {

    double width;
    StrokeLineJoin join;
    StrokeLineCap cap;
    double[] dashes;
    double dashOffset;

    public FXStroke(double width, StrokeLineCap cap, StrokeLineJoin join) {
        this.width = width;
        this.cap = cap;
        this.join = join;
        dashes = null;
        dashOffset = 0.0;
    }

    public FXStroke(double width, StrokeLineCap cap, StrokeLineJoin join,
            double dashOffset, double... dashes) {
        this.width = width;
        this.cap = cap;
        this.join = join;
        this.dashes = dashes;
        this.dashOffset = dashOffset;
    }

}
