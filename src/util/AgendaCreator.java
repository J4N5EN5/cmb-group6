package util;

import sun.security.util.ArrayUtil;

import java.util.*;

class AgendaCreator {

    //TODO 1: check for right use of indices -> e.g. if day starts at 8, should the first index be 0 or (8-startOfDay)...
    //TODO 2: rename global variables to be more descriptive
    //TODO 3: add second iteration to fill the agenda with actual locations
    //TODO 4: Debug everything
    //TODO 5: parametize everything and add appropriate entries in the config file
    //TODO 6: figure out where to include the util.agendaCreator and how to instantiate it during model creation

    private final Random mensaDistr;
    private final Random randomArrival;

    public double getMensaProbability() {
        return mensaProbability;
    }

    public int getEarliestArrivalTime() {
        return earliestArrivalTime;
    }

    public int getLatestArrivalTime() {
        return latestArrivalTime;
    }

    private final double mensaProbability;
    private final int earliestArrivalTime;
    private final int latestArrivalTime;

    public AgendaCreator(double mensaProbability, int earliestArrivalTime, int latestArrivalTime, double mensaVisitProbability){
        this.mensaProbability = mensaProbability;
        this.earliestArrivalTime = earliestArrivalTime;
        this.latestArrivalTime = latestArrivalTime;
        this.mensaDistr = new Random();
        this.randomArrival = new Random();
    }


    /*
        This method creates an array by shape (number_hosts x number_timeslots + 2).
        It creates an individual agenda for each host of the mobility model based on parameters in the config file. This method is the first of two methods
        to create the agendas. It assigns location types for each timeslot with a given location-type -> ID mapping. In the second method, these location types are
        replaced by actual locations.
        :param n_hotst: The number of hosts our model creates
        :param n_timeslots: The number of single time-slots of each agenda. E.g. an agenda from 8am - 8pm with slotsize of 1h has n_timeslots = 12
        :param earliest_arrival_time: denotes the earliest time a host starts its agenda e.g. 8am
        :param latest_arrival_time: denotes the latest time a host starts its agenda e.g 12am
        :param share_lectures: The share in percent (0.x) that lectures make up of each individual agenda
        :param share_tutorials: The share in percent (0.x) that tutorials make up of each individual agenda
        :param share_leisure: The share in percent (0.x) that leisure time make up of each individual agenda
    */

    public int[][] createAgendas () {
        return new int[][] {{1,2,3}};
    }

    private int[][] createAgendaWithLocationtypes (int n_hosts, int n_timeslots, int earlies_arrival_time, int latest_arrival_time, float p_lectures, float p_tutorials, float p_leisure) {
        // create empty array to hold agendas for each host
        int[][] agendaArray = new int[n_hosts][n_timeslots+2];
        // loop to create individual agendas for each host
        for(int i = 0; i < n_hosts; i++) {
            // This method chooses a arrival time and duration of stay based on the earliest and latest arrival time and a arrival distribution and stay time distribution
            int[] stay_times = calcArrivalTimeAndDuration();
            int arrivalTime = stay_times[0];
            int stayDuration = stay_times[1];
            // As the arrival and leave time can differ, freeTimeSlots is used to keep track of the number of locations we have to assign to the current created agenda for host i
            int freeTimeSlots = stayDuration;
            // This method decides wether a host visits the mensa based on a probability and if so, chooses at what time slot the mensa is visited based on a distribution
            int mensa_slot = assign_mensa_slot(arrivalTime);
            // In case no mensa is visited, assign_mensa_slot() returns -2, in which case freeTimeSlots stay the same
            if (mensa_slot != -2) {
                freeTimeSlots -=1;
            }
            // Here, based on the available timeslots, the number of timeslots for each activity is calculated
            int n_lectures = (int) Math.floor(freeTimeSlots*p_lectures);
            int n_tutorial = (int) Math.floor(freeTimeSlots*p_tutorials);
            int n_leisure = (int) Math.floor(freeTimeSlots*p_leisure);
            int n_selfStudy = freeTimeSlots - n_lectures - n_tutorial - n_leisure;
            // Here, for each activity type, we create an array with that holds the location id times the number of time slots this activity takes up of the whole agenda
            int[] activityList = new int[n_lectures + n_tutorial + n_leisure + n_selfStudy];
            Arrays.fill(activityList, 0, n_lectures, 2);
            Arrays.fill(activityList, n_lectures, n_lectures+n_tutorial, 6);
            Arrays.fill(activityList, n_lectures+n_tutorial, n_lectures+n_tutorial+n_leisure, 4);
            Arrays.fill(activityList, n_lectures+n_tutorial+n_leisure, n_lectures + n_tutorial + n_leisure + n_selfStudy, 3);
            // Here, we create a base agenda with length n_timeslots+2 initialized with 0s. The additional first and last timeslot is used to choose the location
            // a host gets initialized at and where the host ends its day. The 0 thus represents the absence of the host from the mobility model.
            int[] baseAgenda = new int[n_timeslots+2];
            Arrays.fill(baseAgenda, 0);
            // Now, the different temp_activity arrays get concatenated and randomly pertubated to represent an individual agenda.
            activityList = shuffleAgenda(activityList, mensa_slot, arrivalTime);
            int counter = 0;
            for(int j = arrivalTime; j < (arrivalTime + stayDuration); j++) {
                baseAgenda[i] = activityList[counter];
                counter ++;
            }
            agendaArray[i] = activityList;
        }

        return agendaArray;

    }

    private int assign_mensa_slot(int arrivalTime) {
        double visitMensa = Math.random();
        double mensaSlot = 0;
        if(visitMensa < mensaProbability) {
            mensaSlot = mensaDistr.nextGaussian();
            int startMensa = Math.max(arrivalTime, 11);
            mensaSlot = mensaSlot * (14 - startMensa) / 2;
            mensaSlot = (int) (mensaSlot + (14 - startMensa) / 2);
        } else {
            mensaSlot = -2;
        }
        return (int) mensaSlot;
    }

    private int[] shuffleAgenda(int[] agenda, int mensaSlot, int arrivalTime) {
        int mensaIndex = mensaSlot - arrivalTime;
        List<Integer> shuffledAgenda = new ArrayList<>();
        for (int i = 1; i <= agenda.length; i++) {
            shuffledAgenda.add(agenda[i]);
        }
        Collections.shuffle(shuffledAgenda);
        shuffledAgenda.add(5);
        return shuffledAgenda.stream().mapToInt(Integer::intValue).toArray();
    }

    private int[] calcArrivalTimeAndDuration() {
        int arrivalTime = randomArrival.nextInt(latestArrivalTime + 1 - earliestArrivalTime) + earliestArrivalTime;
        return new int[] {1,2,3};

    }








}
