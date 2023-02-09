# Omnibus Library

This repository provides Omnibus, a Java library implementing an efficient compositional approach to derive optimal semaphore schedules for multimodal urban intersections among road traffic flows and tram lines with right of way, minimizing the expected percentage of queued vehicles of each flow.

The approach is presented in the paper titled "Compositional analysis of multimodal urban intersections for efficient derivation of optimal semaphore schedules", authored by Nicola Bertocci, Laura Carnevali, Leonardo Scommegna, and Enrico Vicario, currently submitted to Transportation Research Part C: Emerging Technologies.

The approach combines a microscopic model of tram traffic and a macroscopic model of road transport flows:
- The microscopic model of tram traffic is defined in terms of Stochastic Time Petri Nets (STPNs) capturing periodic tram departures as well as bounded delays and travel times with general (i.e., non-Exponential) distribution, thus being open to represent arrival and travel processes estimated from operational data, and it is analyzed to derive the transient probability that the intersection is available for road vehicle.
- The macroscopic model of road transport flows is defined in terms of finite-capacity vacation queues with general vacation times determined by the intersection availability, achieving efficiecy in the evaluation of the expected queue size over time.

The distribution of the expected queue size at multiples of the hyper-period (i.e., the least common multiple of the periods of tram departures and vehicle semaphores) is proved to reach a steady state within a few hyper-periods, so that performing transient analysis from this steady-state distribution for the hyper-period duration is sufficient to characterize the behavior of road transport, enabling derivation of optimal semaphore schedules with time-varying parameters over intervals of arbitrary duration. 

## Experimental reproducibility

To support reproducibility of the experimental results reported in the paper, this repository contains the code that builds and evaluates the considered traffic scenarios, and the steps reported below illustrate how to repeat the experiments. 

Specifically, navigate to `test/java/org/oristool/omnibus` and execute the main method of the Java classes listed below to reproduce the experiments (to execute the main method of a Java class in the Eclipse IDE, just open the class and click on the menu Run > Run as > Java Application):
- `Figure3Experiment.java`: for an intersection with a tram line made of two tram tracks having the stochastic temporal parameters reported in Table 1 of the paper, the experiment derives and plots the transient probability that no tram of the first track is either approaching or crossing the intersection, the transient probability that no tram of the second track is either approaching or crossing the intersection, and the transient probability that no tram of any of the two tracks is either approaching or crossing the intersection (which we term the intersection availability). These results are shown in Figure 3 of the paper.
- `Figure5and6Experiment.java`: for an intersection between a tram line made of two tram tracks having the stochastic temporal parameters reported in Table 1 and a vehicle flow with queue size $K=31$ and arrival rate $&lambda;=0.9 s^{−1}$, the experiment performs both transient and steady-state analysis of the vehicle flow behavior, modeling it both through an M/M/1/K vacation queue with general vacation time and service rate $&mu;=1.138 s^{−1}$, and through an M/M/K/K vacation queue with general vacation time with service rate $&mu;=0.092 s^{−1}$. The results of this experiment are the expected queue size over time (starting either from empty queue or from the steady-state distribution of the expected queue size at multiples of the hyper-period), shown in Figure 5 of the paper, and the steady-state distribution of the expected queue size at multiples of the hyper-period, shwon in Figure 6 of the paper.
- `Table2SuiteTestExperiment.java`: this experiment writes partial results in the `/results` directory and should be launched before the experiment implemented by the class`Table2ErrorComputing.java`.
- `Table2ErrorComputing.java`: for an intersection between a tram line made of two tram tracks and a vehicle flow (illustrated in Section 4.1 of the paper), this experiment computes the accuracy achieved by the proposed approach (using both the M/M/1/K and the M/M/K/K vacation queue with general vacation time) in the evaluation of the expected queue size over time with respect to the SUMO microscopic traffic simulator, for multiple scenarios with different values of street length, vehicle arrival rate, and maximum vehicle speed. This experiment requires, at the relative path `results/`, the results of the experiment implemented by the class `Table2SuiteTestExperiment.java` of both this repository and the repository available at this [link](https://github.com/oris-tool/sumo), the latter providing the ground truth computed through SUMO. The results of this experiment are reported in Table 2 of the paper.
- `Table3PatternComparator.java`: this experiment analyses the intersection among three vehicle flows and a tram line made of two tracks, shown in Figure 8 of the paper (and also reported below), under 390 different schedules for vehicle semaphores, generating a csv file where for each schedule, the maximum expected percentage of queue occupation of any flow is computed (the schedule yielding the minimum value is considered the optimal schedule). The results of this experiment are reported in the first column of Table 3 of the paper.
- `Table3Generator.java`: this experiment requires the results (csv files) of the experiments implemented by the class `Table3PatternComparator.java` present in both this repository and the repository available at this [link](https://github.com/oris-tool/sumo). The csv files should be located at the relative paths `/results/table3_omnibus/table3_omnibus` and `results/table3_sumo/table3_sumo`, respectively. The script compares the outcomes of the two experiments and generates a third csv file reproducing Table 3 of the paper.
- `Table4PatternComparator.java`: this experiment is a variant of the one imlemented by the class`Table3PatternComparator.java` and produces the results reported in the first column of Table 4 of the paper.
- `Table4Generator.java`: this experiment requires the results (csv files) of the experiments implemented by the class `Table4PatternComparator.java` present in both this repository and the repository available at this [link](https://github.com/oris-tool/sumo). The csv files should be located at the relative paths `/results/table4_omnibus/table4_omnibus` and `results/table4_sumo/table4_sumo`, respectively. The script compares the outcomes of the two experiments and generates a third csv file reproducing Table 4 of the paper.
- `Table5PatternComparator.java`: this experiment is a variant of the one implemented by the class `Table3PatternComparator.java` and produces the results presented in the first column of Table 4 of the paper. 
- `Table5Generator.java`: this experiment requires the results (csv files) of the experiments implemented by the class `Table5PatternComparator.java` present in both this repository and the repository available at this [link](https://github.com/oris-tool/sumo). The csv files should be located at the relative paths `/results/table5_omnibus/table5_omnibus` and `results/table5_sumo/table5_sumo`, respectively. The script compares the outcomes of the two experiments and generates a third csv file reproducing Table 5 of the paper.
- `VaryingParamsPatternComparator.java`: this experiment analyzes the intersection among three vehicle flows and a tram line made of two tracks, with time-varying vehicle arrival rates and tram departure period over 4~time intervals, under 390 different semaphore schedules, deriving for each time interval the optimal schedule at minimizing the maximum expected percentage of queue occupation ver time.
  
<p align="center">
  <img src="imgs/threeFlows.png?raw=true" style="width:65%">
  <p align="center">
  <em>A graphical representation of an intersection among three vehicle flows and a bidirectional tram line. </em>
    </p>
</p>

## Installation

This repository provides a ready-to-use Maven project that you can easily import into an Eclipse workspace to start working with the [Omnibus library](https://github.com/oris-tool/omnibus/) (the version `2.0.0-SNAPSHOT` of the [Sirio library](https://github.com/oris-tool/sirio) is included as a Maven dependency). Just follow these steps:

1. **Install Java >= 11.** For Windows, you can download a [package from Oracle](https://www.oracle.com/java/technologies/downloads/#java11); for Linux, you can run `apt-get install openjdk-11-jdk`; for macOS, you can run `brew install --cask java`. 

2. **Download Eclipse.** The [Eclipse IDE for Java Developers](http://www.eclipse.org/downloads/eclipse-packages/) package is sufficient.

3. **Clone this project.** Inside Eclipse:
   - Select `File > Import > Maven > Check out Maven Projects from SCM` and click `Next`.
   - If the `SCM URL` dropbox is grayed out, click on `m2e Marketplace` and install `m2e-egit`. You will have to restart Eclipse.
   - As `SCM URL`, type: `git@github.com:oris-tool/omnibus.git` and click `Next` and then `Finish`.

## License

Omnibus is released under the [GNU Affero General Public License v3.0](https://choosealicense.com/licenses/agpl-3.0).
