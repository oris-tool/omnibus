/* This program is part of the ORIS Tool.
  * Copyright (C) 2011-2023 The ORIS Authors.
  *
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU Affero General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <https://www.gnu.org/licenses/>.
  */

package org.oristool.omnibus.bestsempattern;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SemaphorePattern {

    public static final String RED_SYMBOL = "9";

    private List<VehicleFlow> involvedFlows;
    private List<VehicleFlow> remainingFlows;
    private int period;
    private int redTime;
    private VehicleFlow lastFlowServed;

    /*
     * XXX: Serve per controllare se primo e ultimo slot di verde sono assegnati
     * allo stesso flow Non � il massimo ma con il fatto che utilizziamo una stringa
     * per rappresentare il pattern: andare a ripescare l'id del primo e dell'ultimo
     * flusso in verde potrebbe non essere banale a causa del fatto che l'id
     * potrebbe essere formata anche da pi� di un carattere Si potrebbe risolvere
     * usando una classe al posto di una semplice stringa per il pattern.
     */
    private boolean empty;
    private String firstFlowId;
    private String lastFlowId;

    private StringBuilder scheduleBuilder;

    // questo non ha tutto questo senso...
    private int lastSlotTimeStep;

    public SemaphorePattern(List<VehicleFlow> flows, int period, int redTime) {
        this.involvedFlows = flows;
        this.remainingFlows = flows;
        this.period = period;
        this.redTime = redTime;
        this.lastSlotTimeStep = 0;
        this.scheduleBuilder = new StringBuilder();
        this.lastFlowServed = null;
        this.empty = true;
    }

    public SemaphorePattern(SemaphorePattern subPattern) {
        this.involvedFlows = subPattern.getInvolvedFlows();
        this.remainingFlows = subPattern.getRemainingFlows();
        this.period = subPattern.getPeriod();
        this.redTime = subPattern.getRedTime();
        this.lastSlotTimeStep = subPattern.getLastSlotTimeStep();
        this.scheduleBuilder = new StringBuilder(subPattern.getScheduleBuilder());
        this.lastFlowServed = subPattern.getLastFlowServed();
        this.empty = subPattern.isEmpty();
        this.firstFlowId = subPattern.getFirstFlowId();
        this.lastFlowId = subPattern.getLastFlowId();

    }

    public String getSchedule() {
        return scheduleBuilder.toString();
    }

    public void addGreenSlot(VehicleFlow flow, int greenSlot) {
        if (this.isEmpty()) {
            this.firstFlowId = flow.getId();
            this.empty = false;
        }
        this.lastFlowId = flow.getId();

        scheduleBuilder.append(flow.getId().repeat(greenSlot));
        scheduleBuilder.append(RED_SYMBOL.repeat(redTime));

        if (remainingFlows.contains(flow))
            remainingFlows.remove(flow);

        lastFlowServed = flow.getClone();
        lastSlotTimeStep += greenSlot + redTime;
    }

    public boolean isRemainingPatternFeasible() {
        int minimumTotalGreenTime = 0;
        for (VehicleFlow flow : getRemainingFlows()) {
            minimumTotalGreenTime += flow.getMinimumGreenSlot();
        }
        int minumumTotalRedTime = getRedTime() * getInvolvedFlows().size();
        return minimumTotalGreenTime + minumumTotalRedTime <= getRemainingTime();
    }

    public boolean firstAndLastFlowsCoincide() {
        if (firstFlowId != null && lastFlowId != null)
            return this.getFirstFlowId().equals(getLastFlowId());
        return false;
    }

    public boolean noMorePossibleSlotsExist() {
        int minimumSlot = involvedFlows.stream().min(Comparator.comparing(VehicleFlow::getMinimumGreenSlot)).get()
                .getMinimumGreenSlot();
        return minimumSlot + redTime > getRemainingTime();
    }

    public void fillRemainingWithRed() {
        scheduleBuilder.append(RED_SYMBOL.repeat(getRemainingTime()));
    }

    public void fillRemainingGreenTimeWithLastFlow() {
        int greenTime = getRemainingTime();
        if (greenTime > 0) {
            // sostituisco il rosso con la continuazione del verde
//			scheduleBuilder.replace(lastSlotTimeStep - redTime, lastSlotTimeStep, lastFlowId);
            // inserisco il verde per il tempo ulteriore
//			scheduleBuilder.append(lastFlowId.repeat(greenTime - redTime));

            // cancello ultimo rosso
            scheduleBuilder.delete(lastSlotTimeStep - redTime, lastSlotTimeStep);
            scheduleBuilder.append(lastFlowId.repeat(greenTime));
            scheduleBuilder.append(RED_SYMBOL.repeat(redTime));


        }

        // pretty useless
        lastSlotTimeStep += greenTime + redTime;
    }

    public boolean representsAllFlowsAtLeastOnce() {
        return remainingFlows.isEmpty();
    }

    public int getRemainingTime() {
        return period - lastSlotTimeStep;
    }

    public boolean hasServedThisFlowPreviously(VehicleFlow flow) {
        if (lastFlowServed == null)
            return false;
        return flow.equals(lastFlowServed);
    }

    public List<VehicleFlow> getInvolvedFlows() {
        return involvedFlows.stream().map(VehicleFlow::getClone).collect(Collectors.toList());
    }

    public List<VehicleFlow> getRemainingFlows() {
        return remainingFlows.stream().map(VehicleFlow::getClone).collect(Collectors.toList());
    }

    public VehicleFlow getLastFlowServed() {
        if (lastFlowServed == null)
            return null;
        return lastFlowServed.getClone();
    }

    public int getPeriod() {
        return period;
    }

    public int getRedTime() {
        return redTime;
    }

    public int getLastSlotTimeStep() {
        return lastSlotTimeStep;
    }

    public StringBuilder getScheduleBuilder() {
        return scheduleBuilder;
    }

    public String getFirstFlowId() {
        return firstFlowId;
    }

    public String getLastFlowId() {
        return lastFlowId;
    }

    public boolean isEmpty() {
        return empty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.scheduleBuilder.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SemaphorePattern other = (SemaphorePattern) obj;
        return this.scheduleBuilder.toString().equals(other.getSchedule());
    }
}
