/*
 * Copyright (C) 2016 Bruce Johnson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package smile.plot.javafx;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.commons.csv.CSVFormat;
import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.CompleteLinkage;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.interpolation.BicubicInterpolation;
import smile.io.Read;
import smile.math.MathEx;
import smile.math.TimeFunction;
import smile.math.matrix.Matrix;
import smile.projection.PCA;
import smile.stat.distribution.GaussianDistribution;
import smile.stat.distribution.MultivariateGaussianDistribution;
import smile.vq.Neighborhood;
import smile.vq.SOM;

/**
 * A chart that displays rectangular bars with heights indicating data values
 * for categories. Used for displaying information when at least one axis has
 * discontinuous or discrete data.
 */
public class PlotDemo {

    Stage stage;
    Canvas canvas;
    SmileFxCanvas smileCanvas = null;
    GraphicsContextProxy gcP;
    Pane pane;
    static DataFrame iris = null;
    static DataFrame pendigits = null;
    static double[] cow = null;
    static double[][] six = null;
    static double[][] zip = null;

    public PlotDemo(Stage stage) {
        this.stage = stage;
    }

    public static DataFrame loadIRIS() {
        if (iris == null) {
            try {
                iris = Read.arff("iris.arff");
            } catch (IOException | ParseException | URISyntaxException ex) {
                Logger.getLogger(PlotDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return iris;
    }

    public static double[] loadCOW() {
        if (cow == null) {
            try {
                cow = Read.csv("cow.txt").column("V1").toDoubleArray();
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(PlotDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return cow;
    }

    public static double[][] loadSIX() {
        if (six == null) {
            try {
                six = Read.csv("six.txt", CSVFormat.DEFAULT.withDelimiter(' ')).toArray();
            } catch (IOException | URISyntaxException ex) {
                Logger.getLogger(PlotDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return six;
    }

    public static double[][] loadZIP() {
        if (zip == null) {
            try {
                zip = Read.csv("zip.train", CSVFormat.DEFAULT.withDelimiter(' ')).drop(0).toArray();
            } catch (IOException | URISyntaxException ex) {
                Logger.getLogger(PlotDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return zip;
    }

    public static DataFrame loadPenDigits() {
        try {
            pendigits = Read.csv("pendigits.txt", CSVFormat.DEFAULT.withDelimiter('\t'));
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(PlotDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pendigits;

    }

    public void showCanvasNow() {
        BorderPane borderPane = new BorderPane();
        pane = new Pane();
        borderPane.setCenter(pane);
        Button linePlotButton = new Button("Line Plot");
        linePlotButton.setOnAction(e -> addLinePlot());
        Button scatterPlotButton = new Button("Scatter Plot");
        scatterPlotButton.setOnAction(e -> addScatterPlot());
        Button scatterPlot3DButton = new Button("Scatter Plot 3D");
        scatterPlot3DButton.setOnAction(e -> addScatterPlot3D());
        Button addPlotGriddButton = new Button("Plot Grid");
        addPlotGriddButton.setOnAction(e -> addPlotGrid());
        Button heatPlotButton = new Button("Heat Map");
        heatPlotButton.setOnAction(e -> addHeatMap());
        Button surfacePlotButton = new Button("Surface Plot");
        surfacePlotButton.setOnAction(e -> addSurfacePlot());
        Button barPlotButton = new Button("Bar Plot");
        barPlotButton.setOnAction(e -> addBarPlot());
        Button boxPlotButton = new Button("Box Plot");
        boxPlotButton.setOnAction(e -> addBoxPlot());
        Button histogramButton = new Button("Histogram ");
        histogramButton.setOnAction(e -> addHistogramPlot());
        Button histogram3DButton = new Button("Histogram3D ");
        histogram3DButton.setOnAction(e -> addHistogram3DPlot());
        Button qqPlotButton = new Button("QQ Plot ");
        qqPlotButton.setOnAction(e -> addQQPlot());
        Button dendrogramButton = new Button("Dendrogram");
        dendrogramButton.setOnAction(e -> addDendrogram());
        Button hexMapButton = new Button("Hex Map");
        hexMapButton.setOnAction(e -> addHexMap());
        Button screePlotButton = new Button("Scree Plot");
        screePlotButton.setOnAction(e -> addScreePlot());
        VBox vBox = new VBox();
        vBox.getChildren().addAll(linePlotButton, scatterPlotButton,
                scatterPlot3DButton,
                heatPlotButton, surfacePlotButton,
                barPlotButton, boxPlotButton, histogramButton, histogram3DButton,
                qqPlotButton, screePlotButton,
                dendrogramButton, hexMapButton);
        borderPane.setLeft(vBox);
        canvas = new Canvas(500, 500);
        pane.getChildren().add(canvas);
        stage.setScene(new Scene(borderPane));
        stage.show();
        GraphicsContext gC = canvas.getGraphicsContext2D();
        gcP = new GraphicsContextProxy(canvas.getGraphicsContext2D());
        borderPane.widthProperty().addListener(e -> refresh());
        borderPane.heightProperty().addListener(e -> refresh());
        refresh();
    }

    public void addLinePlot() {
        double[] y = {2.0, 4.0, 0.5, 7.0, 1.4};
        LinePlot lPlot = LinePlot.of(y);
        smileCanvas = null;
        if (smileCanvas == null) {
            smileCanvas = lPlot.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(lPlot);
        }
        refresh();
    }

    public void addScatterPlot() {
        if (loadIRIS() == null) {
            return;
        }
        ScatterPlot scatterPlot = ScatterPlot.of(iris, "sepallength", "sepalwidth", "class", '*');
        smileCanvas = null;
        if (smileCanvas == null) {
            smileCanvas = scatterPlot.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(scatterPlot);
        }
        refresh();
    }

    public void addScatterPlot3D() {
        if (loadIRIS() == null) {
            return;
        }
        ScatterPlot scatterPlot = ScatterPlot.of(iris, "sepallength", "sepalwidth", "petallength", "class", '*');
        smileCanvas = null;
        if (smileCanvas == null) {
            smileCanvas = scatterPlot.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(scatterPlot);
        }
        refresh();
    }

    public void addBoxPlot() {
        if (loadIRIS() == null) {
            return;
        }
        String[] labels = ((smile.data.measure.NominalScale) iris.schema().field("class").measure).levels();
        double[][] data = new double[labels.length][];
        for (int i = 0; i < data.length; i++) {
            String label = labels[i];
            data[i] = iris.stream().
                    filter(row -> row.getString("class").equals(label)).
                    mapToDouble(row -> row.getFloat("sepallength")).
                    toArray();
        }
        BoxPlot boxPlot = BoxPlot.of(data, labels);
        smileCanvas = null;
        if (smileCanvas == null) {
            smileCanvas = boxPlot.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(boxPlot);
        }
        refresh();
    }

    public void addHistogramPlot() {
        if (loadCOW() == null) {
            return;
        }
        BarPlot barPlot = Histogram.of(Arrays.stream(cow).filter(w -> w <= 3500).toArray(), 50, true);
        smileCanvas = null;
        if (smileCanvas == null) {
            smileCanvas = barPlot.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(barPlot);
        }
        refresh();
    }

    public void addHistogram3DPlot() {
        double[] mu = {0.0, 0.0};
        double[][] v = {{1.0, 0.6}, {0.6, 2.0}};
        MultivariateGaussianDistribution gauss = new MultivariateGaussianDistribution(mu, new Matrix(v));
        double[][] data = Stream.generate(gauss::rand).limit(10000).toArray(double[][]::new);
        Histogram3D hist = Histogram3D.of(data, 50, false);
        smileCanvas = null;
        if (smileCanvas == null) {
            smileCanvas = hist.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(hist);
        }
        refresh();

    }

    public void addBarPlot() {
        double[] z = {1.0, 2.0, 4.0, 0.0, 1.0};

        BarPlot barPlot = BarPlot.of(z);
        smileCanvas = null;
        if (smileCanvas == null) {
            smileCanvas = barPlot.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(barPlot);
        }
        refresh();
    }

    public void addQQPlot() {
        GaussianDistribution gauss = new GaussianDistribution(0.0, 1.0);
        double[] data = DoubleStream.generate(gauss::rand).limit(1000).toArray();
        QQPlot qqPlot = QQPlot.of(data);
        smileCanvas = null;
        if (smileCanvas == null) {
            smileCanvas = qqPlot.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(qqPlot);
        }
        refresh();
    }

    public void addHexMap() {
        System.out.println("load zip");
        if (loadZIP() == null) {
            return;
        }
        System.out.println("som lattice");
        double[][][] lattice = SOM.lattice(30, 30, zip);
        System.out.println("build som");
        SOM som = new SOM(lattice,
                TimeFunction.constant(0.1),
                Neighborhood.Gaussian(1, zip.length * 10 / 4));
        System.out.println("update som");
        for (int i = 0; i < 10; i++) {
            for (int j : MathEx.permutate(zip.length)) {
                som.update(zip[j]);
            }
        }
        System.out.println("do hexmap");
        Hexmap hexMap = Hexmap.of(som.umatrix(), Palette.heat(256));
        smileCanvas = null;
        if (smileCanvas == null) {
            smileCanvas = hexMap.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(hexMap);
        }
        refresh();
    }

    public void addScreePlot() {
        if (loadPenDigits() == null) {
            return;
        }
        Formula formula = Formula.lhs("V17");
        double[][] x = formula.x(pendigits).toArray();
        int[] y = formula.y(pendigits).toIntArray();

        PCA pca = PCA.fit(x);
        ScreePlot screePlot = new ScreePlot(pca);
        smileCanvas = null;
        if (smileCanvas == null) {
            smileCanvas = screePlot.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(screePlot);
        }
        refresh();

//    pca.setProjection(3);
//    var x2 = pca.project(x);
//    ScatterPlot.plot(x2, y, '.', Palette.COLORS).window();
    }

    public void addPlotGrid() {
        if (loadIRIS() == null) {
            return;
        }
        refresh();
    }

    public void addHeatMap() {
        double[][] z = {
            {1.0, 2.0, 4.0, 1.0},
            {6.0, 3.0, 5.0, 2.0},
            {4.0, 2.0, 1.0, 5.0},
            {5.0, 4.0, 2.0, 3.0}
        };

        // make the matrix larger with bicubic interpolation
        double[] x = {0.0, 1.0, 2.0, 3.0};
        double[] y = {0.0, 1.0, 2.0, 3.0};
        BicubicInterpolation bicubic = new BicubicInterpolation(x, y, z);
        double[][] Z = new double[101][101];
        for (int i = 0; i <= 100; i++) {
            for (int j = 0; j <= 100; j++) {
                Z[i][j] = bicubic.interpolate(i * 0.03, j * 0.03);
            }
        }
        Heatmap heatMap = Heatmap.of(Z, Palette.jet(256));
        smileCanvas = null;
        if (smileCanvas == null) {
            smileCanvas = heatMap.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(heatMap);
        }
        smileCanvas.add(Contour.of(Z));
        refresh();
    }

    public void addSurfacePlot() {
        double[][] z = {
            {1.0, 2.0, 4.0, 1.0},
            {6.0, 3.0, 5.0, 2.0},
            {4.0, 2.0, 1.0, 5.0},
            {5.0, 4.0, 2.0, 3.0}
        };

        // make the matrix larger with bicubic interpolation
        double[] x = {0.0, 1.0, 2.0, 3.0};
        double[] y = {0.0, 1.0, 2.0, 3.0};
        BicubicInterpolation bicubic = new BicubicInterpolation(x, y, z);
        double[][] Z = new double[101][101];
        for (int i = 0; i <= 100; i++) {
            for (int j = 0; j <= 100; j++) {
                Z[i][j] = bicubic.interpolate(i * 0.03, j * 0.03);
            }
        }
        Surface surface = Surface.of(Z, Palette.jet(256));
        smileCanvas = null;
        if (smileCanvas == null) {
            smileCanvas = surface.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(surface);
        }
        refresh();
    }

    public void addDendrogram() {
        if (loadSIX() == null) {
            return;
        }
        HierarchicalClustering clusters = HierarchicalClustering.fit(CompleteLinkage.of(six));
        smileCanvas = null;
        Dendrogram dendro = new Dendrogram(clusters.getTree(), clusters.getHeight());
        if (smileCanvas == null) {
            smileCanvas = dendro.canvas();
        } else {
            smileCanvas.clear();
            smileCanvas.add(dendro);
        }
        refresh();

    }

    void refresh() {
        canvas.setWidth(pane.getWidth());
        canvas.setHeight(pane.getHeight());
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (smileCanvas != null) {
            smileCanvas.resetAxis();
            smileCanvas.paint(gcP, (int) width, (int) height);
        }
    }

    public void showCanvas() {
        runOnPlatform(() -> {
            showCanvasNow();
        });
    }

    public static void runOnPlatform(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

}
