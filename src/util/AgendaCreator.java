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

    public int[] createAgendasStep1(int n_hosts, int n_timeslots, int earlies_arrival_time, int latest_arrival_time, float p_lectures, float p_tutorials, float p_leisure) {
        // Req1: number of hosts (int)
        // Req2: [(Location ID, Max Number)]
        // Location Types (Entry/Exit = 0; Mensa = 1; Lecture = 2; Self-Study = 3; Leisure = 4; Office = 5; Tutorial = 6)
        // Location Type not here -1
        // [id0,id1,id2,...,id_len(agenda)-1]
        // Create distribution when hosts arrive and how long they stay -> implicitly delimiting the end of their day
        // !! add a "away" polygon where NO INFECTIONS happen...
        // probability for visiting mensa with normal distribution between 11-14
        // Introduce distribution over time of stay -> with value [-minutes, + minutes]...
        // Probabilities to choose from the different entries...


        // Steps:
        // (num of nodes) -> ts_arrival, ts_leave
        // (ts_arrival, ts_leave) -> visit mensa between with probab p1 (ts_arrival,14)
        // ts_leave-ts_arrival-1 = n free timeslots to fill with study stuff
        // n -> 20% free time, 40% lecture, 40% tutorial... (n_free_time, n_lecture, n_tutorial) -> matrix with dim(hosts, len(agenda)) whith the location types in it...
        // iterate over it and fill in the different actual locatoins...


        int[][] agendaHelper = new int[n_hosts][n_timeslots+2];
        for(int i = 0; i < n_hosts; i++) {
            // choose first timeslot between 0-13,
            //index  0:  7 - 8
            //index  1:  8 - 9
            //
            //
            Arrays.fill(agendaHelper[i], -2);
            int freeTimeSlots = n_timeslots+2;
            int[] stay_times = calcArrivalTimeAndDuration(earlies_arrival_time, latest_arrival_time);
            int arrivalTime = stay_times[0];
            int stayDuration = stay_times[1];
            int departTime = arrivalTime + stayDuration;

            for (int j = 0; j <= arrivalTime; j++) {
                agendaHelper[i][j] = -1;
            }

            for (int j = departTime; j <= n_timeslots+2; j++) {
                agendaHelper[i][j] = -1;
            }

            agendaHelper[i][arrivalTime] = 0;
            agendaHelper[i][departTime] = 0;
            //int arrivalPlace = creatArrivalPlace();
            freeTimeSlots -= 2;
            // Visit Mensa... if no mensa visit ->
            int mensa_slot = assign_mensa_slot(arrivalTime, departTime);
            if (mensa_slot != -2) {
                freeTimeSlots -=1;
                agendaHelper[i][mensa_slot] = 1;
            }
            ArrayList<Integer> available_activity = new ArrayList<Integer>();
            int n_lectures = (int) Math.floor(freeTimeSlots*p_lectures);
            for (int j = 0; j < n_lectures; j++) {
                available_activity.add(2);
            }
            int n_tutorial = (int) Math.floor(freeTimeSlots*p_tutorials);
            for (int j = 0; j < n_tutorial; j++) {
                available_activity.add(6);
            }
            int n_leisure = (int) Math.floor(freeTimeSlots*p_leisure);
            for (int j = 0; j < n_leisure; j++) {
                available_activity.add(4);
            }
            int n_selfStudy = freeTimeSlots - n_lectures - n_tutorial - n_leisure;
            for (int j = 0; j < n_selfStudy; j++) {
                available_activity.add(3);
            }
            // Location Types (Entry/Exit = 0; Mensa = 1; Lecture = 2; Self-Study = 3; Leisure = 4; Office = 5; Tutorial = 6)
            //int[] free_slot_option = new int[freeTimeSlots];
            //Arrays.fill(free_slot_option, -1);
            //fill
            for (int j = 0; j < n_timeslots+2; j++) {
                if (agendaHelper[i][j] != -2){
                    continue;
                }
                //(int)(Math.random() * ((Max - Min) + 1))
                int index = (int)(Math.random() * available_activity.size());
                agendaHelper[i][j] = available_activity.get(index);
                available_activity.remove(index);
            }

            /*int[] temp_lecture = new int[n_lectures];
            Arrays.fill(temp_lecture, 2);
            int[] temp_tutorial = new int[n_tutorial];
            Arrays.fill(temp_tutorial, 6);
            int[] temp_leisure = new int[n_leisure];
            Arrays.fill(temp_leisure, 4);
            int[] temp_selfStudy = new int[n_selfStudy];
            Arrays.fill(temp_selfStudy, 3);
            int[] baseAgenda = new int[n_timeslots+2];
            Arrays.fill(temp_selfStudy, 0);*/
            /*
            [1,1]
            [2,2,2,2]
            [3,3]
            [0,0,0,0,0,0,0,0,0,0,0]
            => [1,1,2,2,Mensa,2,2,3,3] -> permutate...
            mensa index
            [0,0,0,0,0,1,2,3,3,2,3,1,0,0,0]
             */



        }

        return new int[]{1, 2, 3};

    };

    public  int[] calcArrivalTimeAndDuration(int start, int end) {
        // use indices instead of times

        int min_duration = 2;
        int arrivalTime = (int) Math.floor(Math.random()*(end-start+1)+start);
        int duration = (int) Math.floor(Math.random()*(end-arrivalTime - min_duration+1)+min_duration);

        return new int[] {arrivalTime, duration};
    }

    private int assign_mensa_slot(int arrive_time, int depart_time){
        // return -2 if people do not go to mensa
        //mensa opens at 11-15, which is index 4,5,6,7
        int NO_MENSA = -2;
        float mensa_possibility = (float) 0.8;
        if ((arrive_time >= 7) || arrive_time+depart_time <= 4){
            //arrive too late or depart too early for mensa slot
            return NO_MENSA;
        }
        if ( Math.random() > mensa_possibility){
            // not go to mensa
            return NO_MENSA;
        }

        if (arrive_time >= 4){
            int mensa_duration = 7 - arrive_time;
            int mensa_timeslot = arrive_time + (int) Math.floor(Math.random()*(mensa_duration)+1);
            return mensa_timeslot;

        } else if (depart_time <= 7) {
            int mensa_duration = depart_time - 4;
            int mensa_timeslot = depart_time - (int) Math.floor(Math.random()*(mensa_duration)+1);
            return mensa_timeslot;

        } else{
            int mensa_timeslot = (int) Math.floor(Math.random()*(7-4+1)+4);
            return mensa_timeslot;
        }


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
/*    class LocationInfo {
        int type, ID, limit;
        //Constructor
        LocationInfo(int type, int ID, int limit) {
            this.type = type;
            this.ID = ID;
            this.limit = limit;
        }
    }*/
    public int[][] CreateLocationWithType(int n_hosts, int n_timeslots, int[][] agendaTypeAllPeople, List<List<Integer>> locationInfo) {
        // I assume that the location type have been allocated with (type, ID, limit)
        // Location Types (Entry/Exit = 0; Mensa = 1; Lecture = 2; Self-Study = 3; Leisure = 4; Office = 5; Tutorial = 6
        // Location Type not here -1
        // limit = -1 meaning there's not a limit to this place
        //   note: there's no limit of leisure
        //   note: we would assume there's at least one exit/entry with no limit
        //   note: we will not have a limit of mensa
        //1. each location type assign with each List
        //1.1 also find the altogether limit of person in this TYPE of location
        //2. iterate through each person
        //2.1 assign them with a random type of spot
        //2.2 if no spot at all, change type and then assign

     /*   List<List<Integer>> exitentryList   = new ArrayList<>();
        List<List<Integer>> mensaList       = new ArrayList<>();
        List<List<Integer>> lectureList     = new ArrayList<>();
        List<List<Integer>> selfstudyList   = new ArrayList<>();
        List<List<Integer>> leisureList     = new ArrayList<>();
        List<List<Integer>> officeList      = new ArrayList<>();
        List<List<Integer>> tutorialList    = new ArrayList<>();

        int[] limitOfType = new int[7]; // altogether 7 types of location
        Arrays.fill(limitOfType, 0);

        for (List<Integer> itemInfo: locationInfo) {
            int typeElem = itemInfo.get(0);
            switch (typeElem){
                case 0: exitentryList.add(itemInfo);
                        break;
                case 1: mensaList.add(itemInfo);
                        break;
                case 2: lectureList.add(itemInfo);
                        break;
                case 3: selfstudyList.add(itemInfo);
                        break;
                case 4: leisureList.add(itemInfo);
                        break;
                case 5: officeList.add(itemInfo);
                        break;
                case 6: tutorialList.add(itemInfo);
                        break;
            }
            //calculate the overall limit in this type of location
            if (limitOfType[typeElem]!=-1) {
                if (itemInfo.get(2)==-1){
                    limitOfType[typeElem] = -1;
                }else{
                    limitOfType[typeElem] += itemInfo.get(2);
                }
            }
        }*/

        List<List<List<Integer>>> typeList   = new ArrayList<>();
        for (List<Integer> itemInfo: locationInfo) {
            int typeElem = itemInfo.get(0);
            typeList.get(typeElem).add(itemInfo);
        }

        List<List<List<List<Integer>>>> timeslotTypeList   = new ArrayList<>();
        for (int i = 0; i < n_timeslots+2; i++) {
            timeslotTypeList.add(typeList);
        }

        //

        int[][] agendaWithLocation = new int[n_hosts][n_timeslots + 2];

        // for each person
        // first initialize location with -1, meaning not here
        // iterate through the location type that it has been assigned
        //      if type is not here(-1), then go to next timeslot
        // check the corresponding location list with the same type
        //      if location list of this type is none (len=0), assign it with leisure
        // find the number of locations with the same type
        // choose a random one
        // calculate the new limit of this place
        //  if the new limit is 0, them delete this place in this timeslot



        for(int i = 0; i < n_hosts; i++) {
            Arrays.fill(agendaWithLocation[i], -1);
            for (int time = 0; j < n_timeslots + 2; j++) {
                int typeNow = agendaTypeAllPeople[i][time];
                if (typeNow == -1){
                    continue;
                }
                int num_choice =  timeslotTypeList.get(time).get(typeNow).size();
                if (num_choice == 0){
                    typeNow = 2;//leisure
                    num_choice =  timeslotTypeList.get(time).get(typeNow).size();
                }
                // (int) (Math.random() * (max - min + 1)) + min;
                // now min=0, max = num_choice-1
                int choice_index = (int) (Math.random() * num_choice);
                agendaWithLocation[i][time] = timeslotTypeList.get(time).get(typeNow).get(choice_index).get(1);
                Integer limitBefore = timeslotTypeList.get(time).get(typeNow).get(choice_index).get(2);
                if (limitBefore == 1){
                    timeslotTypeList.get(time).get(typeNow).remove(choice_index);
                } else if (limitBefore > 1) {
                    timeslotTypeList.get(time).get(typeNow).get(choice_index).set(2, limitBefore-1);
                }
            }
        }
        return agendaWithLocation;
    }




}


