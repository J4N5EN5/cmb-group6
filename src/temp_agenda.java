import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;

public class temp_agenda {

    public int[] createAgendasStep1(int n_hosts, int n_timeslots, int earlies_arrival_time, int latest_arrival_time, float p_lectures, float p_tutorials, float p_leisure) {
        // Req1: number of hosts (int)
        // Req2: [(Location ID, Max Number)]
        // Location Types (Entry/Exit = 0; Mensa = 1; Lecture = 2; Self-Study = 3; Leisure = 4; Office = 5; Tutorial = 6)

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

        int[][] agendaHelper = new int[n_hosts][n_timeslots];
        for(int i = 0; i < n_hosts; i++) {
            // choose first timeslot between 0-
            int freeTimeSlots = n_timeslots;
            int[] stay_times = calcArrivalTimeAndDuration(earlies_arrival_time, latest_arrival_time);
            int arrivalTime = stay_times[0];
            int stayDuration = stay_times[1];
            int arrivalPlace = creatArrivalPlace();
            freeTimeSlots -= 2;
            // Visit Mensa... if no mensa visit ->
            int mensa_slot = assign_mensa_slot();
            if (mensa_slot != -2) {
                freeTimeSlots -=1;
            }
            int n_lectures = (int) Math.floor(freeTimeSlots*p_lectures);
            int n_tutorial = (int) Math.floor(freeTimeSlots*p_tutorials);
            int n_leisure = (int) Math.floor(freeTimeSlots*p_leisure);
            int n_selfStudy = freeTimeSlots - n_lectures - n_tutorial - n_leisure;
            int[] temp_lecture = new int[n_lectures];
            Arrays.fill(temp_lecture, 2);
            int[] temp_tutorial = new int[n_tutorial];
            Arrays.fill(temp_tutorial, 6);
            int[] temp_leisure = new int[n_leisure];
            Arrays.fill(temp_leisure, 4);
            int[] temp_selfStudy = new int[n_selfStudy];
            Arrays.fill(temp_selfStudy, 3);
            int[] baseAgenda = new int[n_timeslots+2];
            Arrays.fill(temp_selfStudy, 0);
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
        // use indices instead of times (e.g. 8 = 0, 12 = 4)
        return new int[] {1, 2};
    }


    public int[] createAgendasStep2() {

    }








}
