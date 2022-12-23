package org.oristool.omnibus;

import org.oristool.omnibus.crossroad.CarFlow;
import org.oristool.omnibus.plotter.LineToPlot;
import org.oristool.omnibus.plotter.PlotUtils;
import org.oristool.omnibus.plotter.Plotter;
import org.oristool.omnibus.tramway.TramCrossing;
import org.oristool.omnibus.tramway.analysis.ParallelGreenProbabilityVisitor;
import org.oristool.omnibus.tramway.pn.PetriNetTramTrackBuilder;
import org.oristool.omnibus.utils.Config;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class Figure3Experiment {

    public static void main(String[] args) {

        // TRAM LINE DEFINITIONS

        TramCrossing bin1 = new TramCrossing(
                PetriNetTramTrackBuilder.getInstance("bin1",
                        BigInteger.valueOf(220),
                        BigInteger.valueOf(30),
                        BigInteger.ZERO,
                        BigInteger.valueOf(120),
                        BigInteger.valueOf(5),
                        BigInteger.valueOf(6),
                        BigInteger.valueOf(14)));
        bin1.analyze(new ParallelGreenProbabilityVisitor(), Config.timeStep);

        TramCrossing bin2 = new TramCrossing(
                PetriNetTramTrackBuilder.getInstance("bin2",
                        BigInteger.valueOf(220),
                        BigInteger.valueOf(30),
                        BigInteger.ZERO,
                        BigInteger.valueOf(120),
                        BigInteger.valueOf(5),
                        BigInteger.valueOf(6),
                        BigInteger.valueOf(14)));
        bin2.analyze(new ParallelGreenProbabilityVisitor(), Config.timeStep);

        // CAR FLOW DEFINITION

        CarFlow carFlow = new CarFlow("queue");

        ArrayList<LineToPlot> linesToPlot = new ArrayList<>();

        double[] linSpace = PlotUtils.getLinSpace(0., BigInteger.valueOf(220).doubleValue(), BigDecimal.valueOf(0.1).doubleValue(), false);


        carFlow.addObstacle(bin1);

        double[] singleTramAvailability = carFlow.getIntersectionAvailability(
                (int) (BigInteger.valueOf(220).doubleValue() / BigDecimal.valueOf(0.1).doubleValue()));

        linesToPlot.add(new LineToPlot("singleTramAvailability",
                linSpace,
                singleTramAvailability));


        carFlow.addObstacle(bin2);

        double[] twoTramAvailability = carFlow.getIntersectionAvailability(
                (int) (BigInteger.valueOf(220).doubleValue() / BigDecimal.valueOf(0.1).doubleValue()));

        linesToPlot.add(new LineToPlot("twoTramAvailability",
                linSpace,
                twoTramAvailability));


        Plotter.plot("Figure3Experiment", "s", "availability", linesToPlot, true, null, true);
    }

}

//public class Figure3Experiment {
//
//    public static void main(String[] args) {
//
//        // TRAM LINE DEFINITIONS
//
//        TramCrossing bin1 = new TramCrossing(
//                PetriNetTramTrackBuilder.getInstance("bin1",
//                        BigInteger.valueOf(220),
//                        BigInteger.valueOf(0),
//                        BigInteger.valueOf(0),
//                        BigInteger.valueOf(120),
//                        BigInteger.valueOf(5),
//                        BigInteger.valueOf(6),
//                        BigInteger.valueOf(14)));
//        bin1.analyze(new ParallelGreenProbabilityVisitor(), Config.timeStep);
//
//        TramCrossing bin2 = new TramCrossing(
//                PetriNetTramTrackBuilder.getInstance("bin2",
//                        BigInteger.valueOf(220),
//                        BigInteger.valueOf(110),
//                        BigInteger.valueOf(0),
//                        BigInteger.valueOf(40),
//                        BigInteger.valueOf(5),
//                        BigInteger.valueOf(6),
//                        BigInteger.valueOf(14)));
//        bin2.analyze(new ParallelGreenProbabilityVisitor(), Config.timeStep);
//
//        // CAR FLOW DEFINITION
//
//        CarFlow carFlow = new CarFlow("queue");
//
//        ArrayList<LineToPlot> linesToPlot = new ArrayList<>();
//
//        double[] linSpace = PlotUtils.getLinSpace(0., BigInteger.valueOf(220).doubleValue(), BigDecimal.valueOf(0.1).doubleValue(), false);
//
//
//        carFlow.addObstacle(bin1);
//
//        double[] firstTramAvailability = carFlow.getIntersectionAvailability(
//                (int) (BigInteger.valueOf(220).doubleValue() / BigDecimal.valueOf(0.1).doubleValue()));
//
//        linesToPlot.add(new LineToPlot("firstTramAvailability",
//                linSpace,
//                firstTramAvailability));
//
//        carFlow.removeObstacle(bin1);
//
//        carFlow.addObstacle(bin2);
//
//        double[] secondTramAvailability = carFlow.getIntersectionAvailability(
//                (int) (BigInteger.valueOf(220).doubleValue() / BigDecimal.valueOf(0.1).doubleValue()));
//
//        linesToPlot.add(new LineToPlot("secondTramAvailability",
//                linSpace,
//                secondTramAvailability));
//
//        carFlow.addObstacle(bin1);
//        carFlow.addObstacle(bin2);
//
//        double[] intersectionAvailability = carFlow.getIntersectionAvailability(
//                (int) (BigInteger.valueOf(220).doubleValue() / BigDecimal.valueOf(0.1).doubleValue())
//        );
//
//        linesToPlot.add(new LineToPlot("intersectionAvailability",
//                linSpace,
//                intersectionAvailability));
//
//        Plotter.plot("Figure3Experiment", "s", "availability", linesToPlot, true, null, true);
//    }
//
//}
