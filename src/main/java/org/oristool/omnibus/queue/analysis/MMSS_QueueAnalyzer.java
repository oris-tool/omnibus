package org.oristool.omnibus.queue.analysis;

import org.oristool.omnibus.queue.BaseQueue;

import java.math.BigDecimal;

/**
 * This is an analyzer for queues, based on differential equations.
 */
public class MMSS_QueueAnalyzer extends QueueAnalyzer {

    /**
     * This is the real analyzer. It receives the queue, the array of the
     * availabilities of the obstacles, the max number of denials of which computing probabilities,
     * and the temporal resolution. It should be the same of the availability.
     *
     * @param queue        an instance of BaseQueue
     * @param availability the array of the availability of the obstacles to be
     *                     passed (intersection availability)
     * @param timeStep     the temporal resolution
     * @return the QueueAnalyzer itself, to allow iterative calls
     */
    public MMSS_QueueAnalyzer analyze(BaseQueue queue, double[] availability, double timeStep) {
        return analyze(queue, availability, 0, timeStep);
    }

    /**
     * This is the real analyzer. It receives the queue, the array of the
     * availabilities of the obstacles, the max number of denials of which computing probabilities,
     * and the temporal resolution. It should be the same of the availability.
     *
     * @param queue        an instance of BaseQueue
     * @param availability the array of the availability of the obstacles to be
     *                     passed (intersection availability)
     * @param maxDenials   the max number of denials
     * @param timeStep     the temporal resolution
     * @return the QueueAnalyzer itself, to allow iterative calls
     */
    public MMSS_QueueAnalyzer analyze(BaseQueue queue, double[] availability, int maxDenials, double timeStep) {
        this.queue = queue.getClone();
        this.availability = availability;
        this.timeStep = timeStep;
        this.maxDenials = maxDenials;

        checkLegality();

        double pArrival = taylorFirstOrderExpansion(queue.getArrivalDistribution(), getTimeStep());
        double pService = taylorFirstOrderExpansion(queue.getServiceDistribution(), getTimeStep());

        int queueSize = queue.getSize().intValue();
        this.stateMatrix = new double[getAvailability().length][queueSize + 1][maxDenials + 1];

        BigDecimal[] initialDistribution = queue.getInitialDistribution();
        for (int i = 0; i < queueSize + 1; i++) {
            this.stateMatrix[0][i][0] = initialDistribution[i].doubleValue();
        }

        // this.stateProbabilitiesAlongTime[0][queue.getInitialElements().intValue()] = 1.;

        for (int t = 1; t < getAvailability().length; t++) {

            double sum = 0;

            for (int d = 0; d <= maxDenials; d++) {

                // Case n = 0
                stateMatrix[t][0][d] = stateMatrix[t - 1][0][d]
                        + stateMatrix[t - 1][1][d] * getAvailability()[t - 1] * pService // * 1
                        - stateMatrix[t - 1][0][d] * pArrival;
                sum += stateMatrix[t][0][d];

                // Case n in [1, QUEUE_SIZE -1]
                for (int n = 1; n < queueSize; n++) {
                    stateMatrix[t][n][d] = stateMatrix[t - 1][n][d]
                            + stateMatrix[t - 1][n + 1][d] * getAvailability()[t - 1] * pService * (n+1)
                            + stateMatrix[t - 1][n - 1][d] * pArrival
                            - stateMatrix[t - 1][n][d] * getAvailability()[t - 1] * pService * n
                            - stateMatrix[t - 1][n][d] * pArrival;

                    sum += stateMatrix[t][n][d];
                }

                // Case n = QUEUE_SIZE

                if (d < maxDenials) {
                    if (d == 0) {
                        stateMatrix[t][queueSize][0] = stateMatrix[t - 1][queueSize][0]
                                + stateMatrix[t - 1][queueSize - 1][0] * pArrival
                                - stateMatrix[t - 1][queueSize][0] * getAvailability()[t - 1] * pService * queueSize
                                - stateMatrix[t - 1][queueSize][0] * pArrival;

                    } else { // d > 0 && d != maxDenials
                        stateMatrix[t][queueSize][d] = stateMatrix[t - 1][queueSize][d]
                                + stateMatrix[t - 1][queueSize - 1][d] * pArrival
                                - stateMatrix[t - 1][queueSize][d] * getAvailability()[t - 1] * pService * queueSize
                                + stateMatrix[t - 1][queueSize][d - 1] * pArrival
                                - stateMatrix[t - 1][queueSize][d] * pArrival;
                    }
                } else { // d == maxDenials
                    if (d == 0) {
                        stateMatrix[t][queueSize][0] = stateMatrix[t - 1][queueSize][0]
                                + stateMatrix[t - 1][queueSize - 1][0] * pArrival
                                - stateMatrix[t - 1][queueSize][0] * getAvailability()[t - 1] * pService * queueSize;
                    } else {
                        stateMatrix[t][queueSize][d] = stateMatrix[t - 1][queueSize][d]
                                + stateMatrix[t - 1][queueSize - 1][d] * pArrival
                                - stateMatrix[t - 1][queueSize][d] * getAvailability()[t - 1] * pService * queueSize
                                + stateMatrix[t - 1][queueSize][d - 1] * pArrival;
                    }
                }

                sum += stateMatrix[t][queueSize][d];

                for (int i = 0; i <= queueSize; i++) {
                    if (stateMatrix[t][i][d] < 0 || stateMatrix[t][i][d] > 1) {
                        throw new IllegalArgumentException(
                                "Probability out of bounds results. Please, try with a lower timeStep. "
                                + " [t, i, d] -> [" + t + ", " + i + ", " + d + "] = " + stateMatrix[t][i][d]);
                    }
                }
            }

            // fase di normalizzazione
            for (int n = 0; n <= queueSize; n++) {
                for (int d = 0; d <= maxDenials; d++) {
                    stateMatrix[t][n][d] = stateMatrix[t][n][d] / sum;
                }
            }
        }

        return this;
    }

}
