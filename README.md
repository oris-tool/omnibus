# Omnibus Library

This repository provides Omnibus, a Java library implementing an efficient compositional approach to derive optimal semaphore schedules for multimodal urban intersections among road traffic flows and tram lines with right of way, minimizing the expected percentage of queued vehicles of each flow.

The approach is presented in the paper titled "An efficient compositional approach to the derivation of optimal semaphore schedules for multimodal urban intersections", authored by Nicola Bertocci, Laura Carnevali, Leonardo Scommegna, and Enrico Vicario, currently submitted to Transportation Research Part C: Emerging Technologies.

The approach combines a microscopic microscopic model of tram traffic and a macroscopic model of road transport flows:
- The microscopic model of tram traffic is defined in terms of Stochastic Time Petri Nets (STPNs) capturing periodic tram departures as well as bounded delays and travel times with general (i.e., non-Exponential) distribution, thus being open to represent arrival and travel processes estimated from operational data, and it is analyzed to derive the transient probability that the intersection is available for road vehicle.
- The macroscopic model of road transport flows is defined in terms of finite-capacity vacation queues with general vacation times determined by the intersection availability, achieving efficiecy in the evaluation of the expected queue size over time.

The distribution of the expected queue size at multiples of the hyper-period (i.e., the least common multiple of the periods of tram departures and vehicle semaphores) is proved to reach a steady state within a few hyper-periods, so that performing transient analysis from this steady-state distribution for the hyper-period duration is sufficient to characterize the behavior of road transport, enabling derivation of optimal semaphore schedules with time-varying parameters over intervals of arbitrary duration. 

## Experimental reproducibility

To support reproducibility of the experimental results reported in the paper, this repository contains the code that builds and evaluates the considered traffic scenarios, and the steps reported below illustrate how to repeat the experiments. 

Specifically, navigate to `test/java/org/oristool/omnibus` and execute the main method of the Java classes listed below to reproduce the experiments (to execute the main method of a Java class in the Eclipse IDE, just open the class and click on the menu Run > Run as > Java Application):
- `Figure3Experiment`: for a tram line made of two tram tracks crossing an intersection (with the stochastic temporal parameters reported in Table 1 of the paper), it derives and plots the transient probability that no tram of the first track is either approaching or crossing the intersection, the transient probability that no tram of the second track is either approaching or crossing the intersection, and the transient probability that no tram of any of the two tracks is either approaching or crossing the intersection (which we term the intersection availability), producing the results shown in Figure 3 of the paper.
- `Figure5and6Experiment`: it analyzes an intersection made of two tram tracks with the same parameters of the previous experiment and a vehicle flow with queue size of $K=31$, arrival rate $&lambda;=0.9 s^{−1}$, the analysis is carried out with both the M/M/1/K and M/M/K/K vacation queues with a variation in the leaving rate that corresponds to $&mu;=1.138 s^{−1}$ for the M/M/1/K model, and $&mu;= 0.092 s^{−1}$ for the M/M/K/K model. As a result, the class will plot the transient and steady state behavior of the intersection with the two type of vacation queues reproducing results reported in Figure 5 and Figure 6 of the paper.
- `Table1SuiteTestExperiment`: it writes partial results in the `/results` directory and should be launched before `Table1ErrorComputing`
- `Table1ErrorComputing`: it requires results from both the previous experiment and from SUMO which is used here as the ground truth (to generate the GT please refer to [this repository](https://github.com/oris-tool/sumo). As a result the experiment will generate a csv file that compares accuracy of both the M/M/1/K and M/M/K/K approach with respect to SUMO ground truth in scenarios with different values of street length, queue capacity, vehicle arrival rate and maximum vehicle speed. This experiments enables the reproducibility of Table 1 of the paper.
- `Table2PatternComparator`: it analyses the intersection of Fig. 1 with arrival rates $&lambda;_1=0.05 s^{−1}$, $&lambda;_2=0.1 s^{−1}$ and $&lambda;_3=0.15 s^{−1}$ for each vehicle flow and maximum speed $V = 50 km h^{-1}$. Specifically the experiment will generate a csv file where for each schedule, the maximum expected percentage of queue occupation of any flow computed by the omnibus analysis method. It reproduces results presented in the paper in Table 2  related to the Omnibus approach.
- `Table2Generator`: requires the csv files obtained from the `Table2PatternComparator` experiment present in both this repository and the SUMO experiment repository (please refer to this [link](https://github.com/oris-tool/sumo)). Files should be located at the relative path `/results/table2_omnibus/table2_omnibus` and `results/table2_sumo/table2_sumo` respectively, the script will compare the outcomes of the two experiments and will generate a third csv file reproducing the whole Table 2 of the paper.
- `Table3PatternComparator`: it is a variant of the `Table2PatternComparator` experiment with arrival rates $&lambda;_1=0.1 s^{−1}$, $&lambda;_2=0.2 s^{−1}$ and $&lambda;_3=0.3 s^{−1}$. It reproduces results presented in the paper in Table 3 related to the Omnibus approach.
- `Table3Generator`: requires the csv files obtained from the `Table3PatternComparator` experiment present in both this repository and the SUMO experiment repository. Files should be located at the relative path `/results/table3_omnibus/table3_omnibus` and `results/table3_sumo/table3_sumo` respectively, the script will compare the outcomes of the two experiments and will generate a third csv file reproducing the whole Table 3 of the paper.
- `Table4PatternComparator`: it is a variant of the `Table2PatternComparator` experiment with maximum vehicle speed $V = 30 km h^{-1}$. It reproduces results presented in the paper in Table 4  related to the Omnibus approach.
- `Table4Generator`: requires the csv files obtained from the `Table4PatternComparator` experiment present in both this repository and the SUMO experiment repository. Files should be located at the relative path `/results/table4_omnibus/table4_omnibus` and `results/table4_sumo/table4_sumo` respectively, the script will compare the outcomes of the two experiments and will generate a third csv file reproducing the whole Table 4 of the paper.
- `VaryingParamsPatternComparator`: still referring to the intersection of Fig. 1, it analyses a scenario where the tram arrival period $T$ and the arrival rates $\lambda_1$, $\lambda_2$, and $\lambda_3$ of vehicle flows vary within time intervals 1, 2, 3, and 4 reproducing the experiment of Sect. $4.2.2$:
    - within time interval 1, $T=440 s$, $\lambda_1=0.025 s$, $\lambda_2= 0.025 s$, and $\lambda_3= 0.025 s$;
    - within time interval 2, $T=220 s$, $\lambda_1=0.1 s$, $\lambda_2=0.2 s$, and $\lambda_3=0.3 s$;
    - within time interval 3, $T=330 s$, $\lambda_1=0.05 s$, $\lambda_2=0.1 s$, and $\lambda_3=0.15 s$;
    - within time interval 4, $T=220 s$, $\lambda_1=0.075 s$, $\lambda_2=0.15 s$, and $\lambda_3=0.2 s$.

  
<p align="center">
  <img src="imgs/threeFlows.png?raw=true" style="width:65%">
  <p align="center">
  <em>Fig. 1 A graphical representation of an intersection among three vehicle flows and a bidirectional tram line. </em>
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
