/** *****************************************************************************
 * Copyright (c) 2010-2020 Haifeng Li. All rights reserved.
 *
 * Smile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Smile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Smile.  If not, see <https://www.gnu.org/licenses/>.
 *****************************************************************************
 */
package smile.plot.javafx;

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * This class represents a poly line in the plot.
 *
 * @author Haifeng Li
 */
public class Line extends Shape {

    /**
     * The supported styles of lines.
     */
    public enum Style {
        SOLID,
        DOT,
        DASH,
        DOT_DASH,
        LONG_DASH
    }

    /**
     * The stroke for solid line.
     */
    private static final BasicFXStroke SOLID_STROKE = new BasicFXStroke(1F,
            StrokeLineCap.BUTT, StrokeLineJoin.ROUND);
    /**
     * The stroke for dotted line.
     */
    private static final BasicFXStroke DOT_STROKE = new BasicFXStroke(1.0,
            StrokeLineCap.BUTT, StrokeLineJoin.ROUND, 1.0, new double[]{2.0});
    /**
     * The stroke for dash line.
     */
    private static final BasicFXStroke DASH_STROKE = new BasicFXStroke(1.0,
            StrokeLineCap.BUTT, StrokeLineJoin.ROUND, 1F, new double[]{10F});
    /**
     * The stroke for dot-dash line.
     */
    private static final BasicFXStroke DOT_DASH_STROKE = new BasicFXStroke(1.0,
            StrokeLineCap.BUTT, StrokeLineJoin.ROUND, 1.0, new double[]{10.0, 2.0, 2F, 2F});
    /**
     * The stroke for long dash line.
     */
    private static final BasicFXStroke LONG_DASH_STROKE = new BasicFXStroke(1F,
            StrokeLineCap.BUTT, StrokeLineJoin.ROUND, 1F, new double[]{20F});
    /**
     * The stroke object to rendering the line.
     */
    private final FXStroke stroke;
    /**
     * The end points of the line.
     */
    final double[][] points;
    /**
     * The style of line.
     */
    final Style style;
    /**
     * The mark of points.
     */
    final char mark;

    /**
     * Constructor.
     *
     * @param points a n-by-2 or n-by-3 matrix that are the coordinates of
     * points.
     * @param style the style of line.
     * @param mark the mark of points.
     * <ul>
     * <li> white space : don't draw the points.
     * <li> . : dot
     * <li> + : +
     * <li> - : -
     * <li> | : |
     * <li> * : star
     * <li> x : x
     * <li> o : circle
     * <li> O : large circle
     * <li> @ : solid circle
     * <li> # : large solid circle
     * <li> s : square
     * <li> S : large square
     * <li> q : solid square
     * <li> Q : large solid square
     * <li> others : dot
     * </ul>
     * @param color the color of line.
     */
    public Line(double[][] points, Style style, char mark, Color color) {
        super(color);
        this.points = points;
        this.style = style;
        this.mark = mark;

        switch (style) {
            case SOLID:
                stroke = SOLID_STROKE;
                break;
            case DOT:
                stroke = DOT_STROKE;
                break;
            case DASH:
                stroke = DASH_STROKE;
                break;
            case DOT_DASH:
                stroke = DOT_DASH_STROKE;
                break;
            case LONG_DASH:
                stroke = LONG_DASH_STROKE;
                break;
            default:
                throw new IllegalArgumentException("Unknown style: " + style);
        }
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(color);

        FXStroke s = g.getStroke();
        g.setStroke(stroke);
        g.drawLine(points);

        if (mark != ' ') {
            for (double[] point : points) {
                g.drawPoint(mark, point);
            }
        }

        g.setStroke(s);
    }

    /**
     * Returns a 2-dimensional array with the index as the x coordinate.
     *
     * @param y the data vector of y coordinates. The x coordinates will be [0,
     * n), where n is the length of y.
     */
    public static double[][] zipWithIndex(double[] y) {
        int n = y.length;
        double[][] data = new double[n][2];
        for (int i = 0; i < n; i++) {
            data[i][0] = i;
            data[i][1] = y[i];
        }

        return data;
    }

    /**
     * Creates a Line with solid stroke and black color.
     */
    public static Line of(double[][] points) {
        return new Line(points, Style.SOLID, ' ', Color.BLACK);
    }

    /**
     * Creates a Line.
     */
    public static Line of(double[][] points, Style style) {
        return new Line(points, style, ' ', Color.BLACK);
    }

    /**
     * Creates a Line.
     */
    public static Line of(double[][] points, char mark) {
        return new Line(points, Style.SOLID, mark, Color.BLACK);
    }

    /**
     * Creates a Line.
     */
    public static Line of(double[][] points, Color color) {
        return new Line(points, Style.SOLID, ' ', color);
    }

    /**
     * Creates a Line.
     */
    public static Line of(double[][] points, Style style, Color color) {
        return new Line(points, style, ' ', color);
    }
}
