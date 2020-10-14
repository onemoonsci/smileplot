/** *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ****************************************************************************** */
package smile.plot.javafx;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Graphics provides methods to draw graphical primitives in
 * logical/mathematical coordinates. The mathematical coordinates are translated
 * into Java2D coordinates based on suitiabel projection method. Both 2D and 3D
 * shapes are supported.
 *
 * @author Haifeng Li
 */
public class Graphics {

    /**
     * Projection used to map logical/mathematical coordinates to Java2D
     * coordinates.
     */
    Projection projection;
    /**
     * Java2D graphics object to render shapes.
     */
    GraphicsContextInterface g2d;
    /**
     * Original clip shape.
     */
    java.awt.Shape originalClip;

    Text text = new Text();

    FXStroke fxStroke = null;

    /**
     * Constructor.
     */
    Graphics(Projection projection) {
        this.projection = projection;
    }

    /**
     * Reset projection object when the PlotCanvas size changed.
     */
    void resetProjection() {
        projection.reset();
    }

    /**
     * Returns the projection object.
     */
    Projection getProjection() {
        return projection;
    }

    /**
     * Returns the Java2D graphics object.
     */
    public GraphicsContextInterface getGraphics() {
        return g2d;
    }

    /**
     * Set the Java2D graphics object.
     */
    public void setGraphics(GraphicsContextInterface g2d, int width, int height) {
        this.g2d = g2d;
        projection.setSize(width, height);
        // anti-aliasing methods
        // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    /**
     * Returns the lower bounds of coordinate space.
     */
    public double[] getLowerBound() {
        return projection.canvas.base.lowerBound;
    }

    /**
     * Returns the upper bounds of coordinate space.
     */
    public double[] getUpperBound() {
        return projection.canvas.base.upperBound;
    }

    /**
     * Get the current font.
     */
    public Font getFont() {
        return g2d.getFont();
    }

    /**
     * Set the font.
     */
    public Graphics setFont(Font font) {
        g2d.setFont(font);
        return this;
    }

    /**
     * Get the current color.
     */
    public Color getColor() {
        return (Color) g2d.getStroke();
    }

    /**
     * Set the color.
     */
    public Graphics setColor(Color color) {
        g2d.setStroke(color);
        g2d.setFill(color);
        return this;
    }

    /**
     * Get the current paint object.
     */
    public Paint getPaint() {
        return g2d.getFill();
    }

    /**
     * Set the paint object.
     */
    public Graphics setPaint(Paint paint) {
        g2d.setFill(paint);
        return this;
    }

    /**
     * Get the current stroke.
     */
    public FXStroke getStroke() {
        return fxStroke;
    }

    /**
     * Set the stroke.
     */
    public Graphics setStroke(FXStroke stroke) {
        fxStroke = stroke;
        return this;
    }

    /**
     * Restrict the draw area to the valid base coordinate space.
     */
    public void clip() {
        int x = (int) (projection.width * projection.canvas.margin);
        int y = (int) (projection.height * projection.canvas.margin);
        int w = (int) (projection.width * (1 - 2 * projection.canvas.margin));
        int h = (int) (projection.height * (1 - 2 * projection.canvas.margin));
//        originalClip = g2d.getClip();
//        g2d.clipRect(x, y, w, h);
    }

    /**
     * Clear the restriction of the draw area.
     */
    public void clearClip() {
//        if (originalClip != null) {
//            g2d.setClip(originalClip);
//            originalClip = null;
//        }
    }

    /**
     * Draw a string. Reference point is the center of string. The coordinates
     * are logical coordinates.
     */
    public void drawText(String label, double[] coord) {
        drawText(label, coord, 0.5, 0.5, 0.0);
    }

    /**
     * Draw a string with given rotation angle. Reference point is the center of
     * string. The coordinates are logical coordinates. The angle of rotation is
     * in radians.
     */
    public void drawText(String label, double[] coord, double rotation) {
        drawText(label, coord, 0.5, 0.5, rotation);
    }

    /**
     * Draw a string with given reference point. (0.5, 0.5) is center, (0, 0) is
     * lower left, (0, 1) is upper left, etc. The coordinates are logical
     * coordinates.
     */
    public void drawText(String label, double[] coord, double horizontalReference, double verticalReference) {
        drawText(label, coord, horizontalReference, verticalReference, 0.0);
    }

    /**
     * Draw a string with given reference point and rotation angle. (0.5, 0.5)
     * is center, (0, 0) is lower left, (0, 1) is upper left, etc. The angle of
     * rotation is in radians. The coordinates are logical coordinates.
     */
    public void drawText(String label, double[] coord, double horizontalReference, double verticalReference, double rotation) {
        int[] sc = projection.screenProjection(coord);
        int x = sc[0];
        int y = sc[1];
        Font font = getFont();
        double w = getStringWidth(label, font);
        double h = font.getSize();

        x -= (int) (w * horizontalReference);
        y += (int) (h * verticalReference);

        g2d.save();
        g2d.translate(x, y);
        if (rotation != 0) {
            g2d.rotate(rotation * 180.0 / Math.PI);
            g2d.translate(-1.0 * w * horizontalReference, h * verticalReference);
        }
        g2d.fillText(label, 0, 0);
        g2d.restore();

    }

    /**
     * Draw a string with given rotation angle. Reference point is the center of
     * string. The logical coordinates are proportional to the base coordinates.
     */
    public void drawTextBaseRatio(String label, double[] coord) {
        drawTextBaseRatio(label, coord, 0.5, 0.5, 0.0);
    }

    /**
     * Draw a string with given rotation angle. Reference point is the center of
     * string. The angle of rotation is in radians. The logical coordinates are
     * proportional to the base coordinates.
     */
    public void drawTextBaseRatio(String label, double[] coord, double rotation) {
        drawTextBaseRatio(label, coord, 0.5, 0.5, 0.0);
    }

    /**
     * Draw a string with given reference point. (0.5, 0.5) is center, (0, 0) is
     * lower left, (0, 1) is upper left, etc. The logical coordinates are
     * proportional to the base coordinates.
     */
    public void drawTextBaseRatio(String label, double[] coord, double horizontalReference, double verticalReference) {
        drawTextBaseRatio(label, coord, horizontalReference, verticalReference, 0.0);
    }

    /**
     * Draw a string with given reference point and rotation angle. (0.5, 0.5)
     * is center, (0, 0) is lower left, (1, 0) is upper left, etc. The angle of
     * rotation is in radians. The logical are proportional to the base
     * coordinates.
     */
    public void drawTextBaseRatio(String label, double[] coord, double horizontalReference, double verticalReference, double rotation) {
        int[] sc = projection.screenProjectionBaseRatio(coord);
        int x = sc[0];
        int y = sc[1];

        Font font = getFont();
        double w = getStringWidth(label, font);
        double h = font.getSize();

        g2d.save();
        if (rotation != 0) {
            g2d.translate(x, y);
            g2d.rotate(rotation * 180.0 / Math.PI);
            g2d.translate(-1.0 * w * horizontalReference, h * verticalReference);
        } else {
            x -= (int) (w * horizontalReference);
            y += (int) (h * verticalReference);
            g2d.translate(x, y);
        }
        g2d.fillText(label, 0, 0);
        g2d.restore();

    }

    /**
     * Draw poly line.
     */
    private void drawLine(int[]... coord) {
        double[] x = new double[coord.length];
        for (int i = 0; i < coord.length; i++) {
            x[i] = coord[i][0];
        }

        double[] y = new double[coord.length];
        for (int i = 0; i < coord.length; i++) {
            y[i] = coord[i][1];
        }

        g2d.strokePolyline(x, y, coord.length);
    }

    /**
     * Draw poly line. The coordinates are in logical coordinates.
     */
    public void drawLine(double[]... coord) {
        int[][] sc = new int[coord.length][];
        for (int i = 0; i < sc.length; i++) {
            
            sc[i] = projection.screenProjection(coord[i]);
        }

        drawLine(sc);
    }

    /**
     * Draw poly line. The logical coordinates are proportional to the base
     * coordinates.
     */
    public void drawLineBaseRatio(double[]... coord) {
        int[][] sc = new int[coord.length][];
        for (int i = 0; i < sc.length; i++) {
            sc[i] = projection.screenProjectionBaseRatio(coord[i]);
        }

        drawLine(sc);
    }

    /**
     * Draw a dot. The coordinates are in logical coordinates.
     */
    public void drawPoint(double... coord) {
        drawPoint('.', coord);
    }

    /**
     * Draw a dot with given pattern. The coordinates are in logical
     * coordinates.
     *
     * @param dot the pattern of dot:
     * <ul>
     * <li> . : dot
     * <li> + : cross
     * <li> - : -
     * <li> | : |
     * <li> * : star
     * <li> x : x
     * <li> o : circle
     * <li> O : large circle
     * <li> @ : solid circle
     * <li> # : large sollid circle
     * <li> s : square
     * <li> S : large square
     * <li> q : solid square
     * <li> Q : large solid square
     * </ul>
     */
    public void drawPoint(char dot, double... coord) {
        int size = 2;
        int midSize = 3;
        int bigSize = 4;

        int[] sc = projection.screenProjection(coord);

        int x = sc[0];
        int y = sc[1];

        switch (dot) {
            case '+':
                g2d.strokeLine(x - size, y, x + size, y);
                g2d.strokeLine(x, y - size, x, y + size);
                break;

            case '-':
                g2d.strokeLine(x - size, y, x + size, y);
                break;

            case '|':
                g2d.strokeLine(x, y - size, x, y + size);
                break;

            case 'x':
                g2d.strokeLine(x - size, y - size, x + size, y + size);
                g2d.strokeLine(x + size, y - size, x - size, y + size);
                break;

            case '*':
                g2d.strokeLine(x - bigSize, y, x + bigSize, y);
                g2d.strokeLine(x, y - bigSize, x, y + bigSize);
                g2d.strokeLine(x - midSize, y - midSize, x + midSize, y + midSize);
                g2d.strokeLine(x + midSize, y - midSize, x - midSize, y + midSize);
                break;

            case 'o':
                g2d.strokeOval(x - size, y - size, 2 * size, 2 * size);
                break;

            case 'O':
                g2d.strokeOval(x - bigSize, y - bigSize, 2 * bigSize, 2 * bigSize);
                break;

            case '@':
                g2d.fillOval(x - size, y - size, 2 * size, 2 * size);
                break;

            case '#':
                g2d.fillOval(x - bigSize, y - bigSize, 2 * bigSize, 2 * bigSize);
                break;

            case 's':
                g2d.strokeRect(x - size, y - size, 2 * size, 2 * size);
                break;

            case 'S':
                g2d.strokeRect(x - bigSize, y - bigSize, 2 * bigSize, 2 * bigSize);
                break;

            case 'q':
                g2d.fillRect(x - size, y - size, 2 * size, 2 * size);
                break;

            case 'Q':
                g2d.fillRect(x - bigSize, y - bigSize, 2 * bigSize, 2 * bigSize);
                break;

            default:
                g2d.strokeRect(x, y, 1, 1);
                break;
        }
    }

    /**
     * Draw polygon. The coordinates are in logical coordinates.
     */
    public void drawPolygon(double[]... coord) {
        int[][] c = new int[coord.length][2];
        for (int i = 0; i < coord.length; i++) {
            c[i] = projection.screenProjection(coord[i]);
        }

        double[] x = new double[c.length];
        for (int i = 0; i < c.length; i++) {
            x[i] = c[i][0];
        }
        double[] y = new double[c.length];
        for (int i = 0; i < c.length; i++) {
            y[i] = c[i][1];
        }
        g2d.strokePolygon(x, y, c.length);
    }

    /**
     * Fill polygon. The coordinates are in logical coordinates.
     */
    public void fillPolygon(double[]... coord) {
        int[][] c = new int[coord.length][2];
        for (int i = 0; i < coord.length; i++) {
            c[i] = projection.screenProjection(coord[i]);
        }

        double[] x = new double[c.length];
        for (int i = 0; i < c.length; i++) {
            x[i] = c[i][0];
        }
        double[] y = new double[c.length];
        for (int i = 0; i < c.length; i++) {
            y[i] = c[i][1];
        }

        g2d.fillPolygon(x, y, c.length);
    }

    /**
     * Fill polygon. The coordinates are in logical coordinates. This also
     * supports basic alpha compositing rules for combining source and
     * destination colors to achieve blending and transparency effects with
     * graphics and images.
     *
     * @param alpha the constant alpha to be multiplied with the alpha of the
     * source. alpha must be a floating point number in the inclusive range
     * [0.0, 1.0].
     */
    public void fillPolygon(float alpha, double[]... coord) {
        int[][] c = new int[coord.length][2];
        for (int i = 0; i < coord.length; i++) {
            c[i] = projection.screenProjection(coord[i]);
        }

        double[] x = new double[c.length];
        for (int i = 0; i < c.length; i++) {
            x[i] = c[i][0];
        }
        double[] y = new double[c.length];
        for (int i = 0; i < c.length; i++) {
            y[i] = c[i][1];
        }
        g2d.fillPolygon(x, y, coord.length);
        // fixme composite

    }

    /**
     * Draw the outline of the specified rectangle.
     */
    public void drawRect(double[] topLeft, double[] rightBottom) {
        if (!(projection instanceof Projection2D)) {
            throw new UnsupportedOperationException("Only 2D graphics supports drawing rectangles.");
        }

        int[] sc = projection.screenProjection(topLeft);
        int[] sc2 = projection.screenProjection(rightBottom);

        g2d.strokeRect(sc[0], sc[1], sc2[0] - sc[0], sc2[1] - sc[1]);
    }

    /**
     * Draw the outline of the specified rectangle. The logical coordinates are
     * proportional to the base coordinates.
     */
    public void drawRectBaseRatio(double[] topLeft, double[] rightBottom) {
        if (!(projection instanceof Projection2D)) {
            throw new UnsupportedOperationException("Only 2D graphics supports drawing rectangles.");
        }

        int[] sc = projection.screenProjectionBaseRatio(topLeft);
        int[] sc2 = projection.screenProjectionBaseRatio(rightBottom);

        g2d.strokeRect(sc[0], sc[1], sc2[0] - sc[0], sc2[1] - sc[1]);
    }

    /**
     * Fill the specified rectangle.
     */
    public void fillRect(double[] topLeft, double[] rightBottom) {
        if (!(projection instanceof Projection2D)) {
            throw new UnsupportedOperationException("Only 2D graphics supports drawing rectangles.");
        }

        int[] sc = projection.screenProjection(topLeft);
        int[] sc2 = projection.screenProjection(rightBottom);

        g2d.fillRect(sc[0], sc[1], sc2[0] - sc[0], sc2[1] - sc[1]);
    }

    /**
     * Fill the specified rectangle. The logical coordinates are proportional to
     * the base coordinates.
     */
    public void fillRectBaseRatio(double[] topLeft, double[] rightBottom) {
        if (!(projection instanceof Projection2D)) {
            throw new UnsupportedOperationException("Only 2D graphics supports drawing rectangles.");
        }

        int[] sc = projection.screenProjectionBaseRatio(topLeft);
        int[] sc2 = projection.screenProjectionBaseRatio(rightBottom);

        g2d.fillRect(sc[0], sc[1], sc2[0] - sc[0], sc2[1] - sc[1]);
    }

    /**
     * Rotate the 3D view based on the changes on mouse position.
     *
     * @param x changes of mouse position on the x axis.
     * @param y changes on mouse position on the y axis.
     */
    public void rotate(double x, double y) {
        if (!(projection instanceof Projection3D)) {
            throw new UnsupportedOperationException("Only 3D graphics supports rotation.");
        }

        ((Projection3D) projection).rotate(x, y);
    }

    public double getStringWidth(String s) {
        return getStringWidth(s, getFont());
    }

    public double getStringWidth(String s, Font font) {
        text.setText(s);
        text.setFont(font);
        final double width = text.getLayoutBounds().getWidth();
        return width;
    }

}
