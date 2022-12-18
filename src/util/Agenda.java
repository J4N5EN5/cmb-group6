package util;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;



public class Agenda {

    static int nHosts, dayStart, dayEnd, meanArrivalTime, meanStayDuration, agendaAssignmentCounter;
    static double stdArrivalTime, stdStayDuration, mensaProbability, shareLeisure, shareLecture, shareTutorial;
    static Random rand = new Random();
    static final List<Location> locations;
    static String[][] agendaTable;
    static List<Integer> timeslotBase = new ArrayList<>();
    static int timeslotLength;
    static int scenarioEndTime;

    private final String[] agenda;
    private final int[] timeSlots = new int[dayEnd-dayStart];

    public Agenda() {
        this.agenda = agendaTable[agendaAssignmentCounter];
        agendaAssignmentCounter++;
        Collections.shuffle(timeslotBase);
        for(int i = 0; i < timeSlots.length-1; i++) {
            timeSlots[i] = timeslotBase.get(i) + timeslotLength*i;
        }
        timeSlots[timeSlots.length-1] = timeSlots.length * timeslotLength;
    }

    private enum LocationTypes {ENTRANCE, MENSA, TUTORIAL, LECTURE, STUDY, LEISURE, ROUTING, DEFAULT}

    private static class Location {
        private LocationTypes locationType;
        private int limit;
        private String name, polygon;
        private List<Integer> limits = new ArrayList<>();

        public void setLimits() {this.limits.addAll(Collections.nCopies(dayEnd-dayStart, this.limit));}

        public Boolean isFull(int index) {return limits.get(index) == 0;}

        public String assignPlace(int timeslot){
            this.limits.set(timeslot, this.limits.get(timeslot)-1);
            return this.name;
        }

        @Override
        public String toString() {return "Name: " + name + " Location type: " + locationType + " Limit: " + limit;}
    }

    private static class LocationDrawer {
        private final List<Location> mensaList = new ArrayList<>();
        private final List<Location> tutorialList = new ArrayList<>();
        private final List<Location> lectureList = new ArrayList<>();
        private final List<Location> studyList = new ArrayList<>();
        private final List<Location> leisureList = new ArrayList<>();
        private final List<Location> entranceList = new ArrayList<>();
        private final List<Location> defaultList = new ArrayList<>();
        private int default_counter = 0;

        public LocationDrawer() {
            for(Location location : locations) {
                switch (location.locationType) {
                    case ENTRANCE -> entranceList.add(location);
                    case MENSA -> mensaList.add(location);
                    case TUTORIAL -> tutorialList.add(location);
                    case LECTURE -> lectureList.add(location);
                    case STUDY -> studyList.add(location);
                    case LEISURE -> leisureList.add(location);
                    case DEFAULT -> defaultList.add(location);
                }
            }
            Collections.shuffle(mensaList);
            Collections.shuffle(tutorialList);
            Collections.shuffle(lectureList);
            Collections.shuffle(studyList);
            Collections.shuffle(entranceList);
        }

        public String getLocation(LocationTypes locationType, int index) {

            List<Location> chosenList;
            switch (locationType) {
                case ENTRANCE -> {
                    return "entrance";
                }
                case MENSA -> chosenList = mensaList;
                case TUTORIAL -> chosenList = tutorialList;
                case LECTURE -> chosenList = lectureList;
                case STUDY -> chosenList = studyList;
                case DEFAULT -> chosenList = defaultList;
                default -> chosenList = leisureList;
            }
            if (chosenList.isEmpty()) {
                return getLocation(LocationTypes.DEFAULT, index);
            }
            int locationIndex = new Random().nextInt(chosenList.size());
            Location location = chosenList.get(locationIndex);
            if (!location.isFull(index)) {return location.assignPlace(index);}
            else {
                chosenList.remove(locationIndex);
                return(getLocation(locationType, index));
            }
        }

        public String getEntrance() {
            for (Location location : entranceList){
                if (location.limit != 0) {
                    location.limit -= 1;
                    return location.name;
                } else {
                    String ret_val = entranceList.get(default_counter).name;
                    default_counter = (default_counter + 1) % entranceList.size();
                    return ret_val;
                }
            }
            return "None";
        }
    }

    static {

        List<Location> jsonLocations = null;
        Properties prop = new Properties();
        try {
            String propFile = "test_settings.txt";
            String locationFile = "data/test.json";
            prop.load(Files.newInputStream(Paths.get(propFile)));
            Reader reader = Files.newBufferedReader(Paths.get(locationFile));


            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            jsonLocations = new Gson().fromJson(jsonObject.get("vertices"), new TypeToken<List<Location>>() {
            }.getType());
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        nHosts = Integer.parseInt(prop.getProperty("nHosts"));
        dayStart = Integer.parseInt(prop.getProperty("dayStart"));
        dayEnd = Integer.parseInt(prop.getProperty("dayEnd"));
        meanArrivalTime = Integer.parseInt(prop.getProperty("meanArrivalTime"));
        meanStayDuration = Integer.parseInt(prop.getProperty("meanStayDuration"));
        stdArrivalTime = Double.parseDouble(prop.getProperty("stdArrivalTime"));
        stdStayDuration = Double.parseDouble(prop.getProperty("stdStayDuration"));
        mensaProbability = Double.parseDouble(prop.getProperty("mensaProbability"));
        shareLeisure = Double.parseDouble(prop.getProperty("shareLeisure"));
        shareLecture = Double.parseDouble(prop.getProperty("shareLecture"));
        shareTutorial = Double.parseDouble(prop.getProperty("shareTutorial"));
        scenarioEndTime = Integer.parseInt(prop.getProperty("Scenario.endTime"));
        timeslotLength = scenarioEndTime / (dayEnd-dayStart);

        locations = jsonLocations;
        assert locations != null;
        for(Location location: locations){
            location.setLimits();
        }

        List<List<LocationTypes>> tmp = assignLocationTypes();
        assignLocations(tmp);
        Random rand = new Random();
        for(int i = 0; i < dayEnd-dayStart-1; i++){
            timeslotBase.add((int) ((rand.nextDouble()*(1.2-0.8) + 0.8) * timeslotLength));
        }
    }

    public String[] getAgenda() {return this.agenda;}
    public int[] getTimeSlots(){return this.timeSlots;}

    private static List<List<LocationTypes>> assignLocationTypes() {
        List<List<LocationTypes>> agendaTable = new ArrayList<>();
        for(int i = 0; i < nHosts; i++) {
            int arrivalTime = (int) Math.round(Math.max(dayStart, Math.min(dayEnd-3, rand.nextGaussian()*stdArrivalTime + meanArrivalTime)));
            int stayDuration = (int) Math.round(Math.min(dayEnd- arrivalTime, Math.max(1, rand.nextGaussian()*stdStayDuration + meanStayDuration)));
            int freeTimeSlots = stayDuration;
            int departureTime = arrivalTime + stayDuration;
            int mensaTimeslot = -1;
            if (Math.random() > 1-mensaProbability && arrivalTime < 15 && departureTime > 11){
                mensaTimeslot = (int) Math.max(11, Math.min(14, rand.nextGaussian() + 12.5));
                freeTimeSlots -= 1;
            }
            int nLecture = (int) Math.round(freeTimeSlots*shareLecture);
            int nTutorial = (int) Math.round(freeTimeSlots*shareTutorial);
            int nLeisure = (int) Math.round(freeTimeSlots*shareLeisure);
            int nStudy = freeTimeSlots - nLecture - nTutorial - nLeisure;

            List<LocationTypes> activityList= new ArrayList<>();

            activityList.addAll(Collections.nCopies(nLecture, LocationTypes.LECTURE));
            activityList.addAll(Collections.nCopies(nTutorial, LocationTypes.TUTORIAL));
            activityList.addAll(Collections.nCopies(nLecture, LocationTypes.LEISURE));
            activityList.addAll(Collections.nCopies(nStudy, LocationTypes.STUDY));

            Collections.shuffle(activityList);
            int nPrepend = arrivalTime - dayStart;
            int nAppend = dayEnd - arrivalTime - stayDuration;
            activityList.addAll(0, Collections.nCopies(nPrepend, LocationTypes.ENTRANCE));
            activityList.addAll(Collections.nCopies(nAppend, LocationTypes.ENTRANCE));
            if (mensaTimeslot != -1) {activityList.add(mensaTimeslot-dayStart, LocationTypes.MENSA);}
            agendaTable.add(activityList);
        }
        return agendaTable;
    }

    private static void assignLocations(List<List<LocationTypes>> agendaTable){
        int nTimeslots = dayEnd-dayStart;
        String[][] agenda = new String[nHosts][nTimeslots];
        LocationDrawer locationDrawer = new LocationDrawer();
        for(int i = 0; i < nHosts; i++){
            String entranceName = locationDrawer.getEntrance();
            for(int j = 0; j < nTimeslots; j++){
                if (agendaTable.get(i).get(j) == LocationTypes.ENTRANCE) {
                    agenda[i][j] = entranceName;
                } else {
                    agenda[i][j] = locationDrawer.getLocation(agendaTable.get(i).get(j), j);
                }
            }
        }
        Agenda.agendaTable = agenda;
    }

    public static void main(String[] args){
        for(int i = 0; i < 100; i ++){
            Agenda agenda = new Agenda();
            System.out.println(Arrays.toString(agenda.timeSlots));
        }

    }
}