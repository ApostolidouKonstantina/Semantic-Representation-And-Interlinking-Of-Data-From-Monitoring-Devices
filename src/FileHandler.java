import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import com.opencsv.CSVReader;

public class FileHandler {

    List<List<String>> finalDailyHeartRate;
    List<List<String>> finalHourlyHeartRate;
    List<List<String>> finalSleep;
    List<List<String>> finalDailySteps;
    List<List<String>> finalHourlySteps;
    List<List<String>> finalDailyCalories;
    List<List<String>> finalHourlyCalories;
    List<List<String>> finalWeight;
    List<List<String>> finalBMI;

    public void HeartRateReader(String filepath, int choice) throws IOException, ParseException {
        //initializing the file reader
        FileReader f = new FileReader(filepath);
        CSVReader csv = new CSVReader(f);
        String[] next;
        //skipping first row
        csv.readNext();
        //reading the csv line by line
        HashMap<List<String>, List<Integer>> map= new HashMap<>();
        while ((next = csv.readNext()) != null) {
            String id = next[0];
            String date = next[1];
            int rate = Integer.parseInt(next[2]);
            if (choice==1){
                //get date without time
                String[] st = date.split(" ");
                date = st[0];
                //bring date to yyyy/MM/dd format for xsd:dateTime
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Date conv = sdf.parse(date);
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                date = sdf2.format(conv);
            } else {
                //get date with hour info
                String[] st1 = date.split(" ");
                String[] st2 = date.split(":");
                date = st2[0]+":00:00 "+st1[2];
                //bring date to yyyy/MM/ddTHH:mm:ss a format for xsd:dateTime
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                Date conv = sdf.parse(date);
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = sdf2.format(conv);
                String[] st3 = date.split(" ");
                date = st3[0]+"T"+st3[1];
            }
            //group measurements that belong to the same user on the same day/hour
            List<String> s = new ArrayList<>();
            s.add(id); s.add(date);
            if (map.isEmpty() || !(map.containsKey(s))){
                List<Integer> rateList = new ArrayList<>();
                rateList.add(rate);
                map.put(s,rateList);
            }
            else {
                List<Integer> values = map.get(s);
                values.add(rate);
                map.remove(s);
                map.put(s, values);
            }
        }
        if (choice==1)
            finalDailyHeartRate = new ArrayList<>();
        else
            finalHourlyHeartRate = new ArrayList<>();
        //calculating daily heart rate
        if (!map.isEmpty()) {
            for (List<String> s : map.keySet()) {
                int sum = 0, i = 0;
                for (int n : map.get(s)) {
                    sum += n;
                    i++;
                }
                int avg=sum/i;
                s.add(String.valueOf(avg));
                if (choice==1)
                    finalDailyHeartRate.add(s);
                else
                    finalHourlyHeartRate.add(s);
            }
        }
        //System.out.println(finalDailyHeartRate);
        //System.out.println(finalHourlyHeartRate);
    }

    public void SleepReader(String filepath) throws IOException, ParseException {
        //initializing the file reader
        FileReader f = new FileReader(filepath);
        CSVReader csv = new CSVReader(f);
        String[] next;
        //skipping first row
        csv.readNext();
        //reading the csv line by line
        HashMap<List<String>, Integer> map= new HashMap<>();
        while ((next = csv.readNext()) != null) {
            String id = next[0];
            String date = next[1];
            //get date without time
            String[] st = date.split(" ");
            date = st[0];
            //bring date to yyyy/MM/dd format for xsd:dateTime
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date conv = sdf.parse(date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf2.format(conv);
            //group measurements that belong to the same user on the same day
            List<String> s = new ArrayList<>();
            s.add(id); s.add(date);
            if (map.isEmpty() || !(map.containsKey(s)))
                map.put(s,1);
            else {
                int minutesOfSleep = map.get(s);
                map.remove(s);
                map.put(s, minutesOfSleep+1);
            }
        }
        finalSleep = new ArrayList<>();
        //calculating daily heart rate
        if (!map.isEmpty()) {
            for (List<String> s : map.keySet()) {
                int minutes = map.get(s);
                s.add(String.valueOf(minutes));
                finalSleep.add(s);
            }
        }
        //System.out.println(finalSleep);
    }

    public void StepsReader(String filepath, int choice) throws IOException, ParseException {
        //initializing the file reader
        FileReader f = new FileReader(filepath);
        CSVReader csv = new CSVReader(f);
        String[] next;
        //skipping first row
        csv.readNext();
        if (choice==1) {
            //reading the csv line by line
            finalDailySteps = new ArrayList<>();
            while ((next = csv.readNext()) != null) {
                List<String> s = new ArrayList<>();
                //get date without time
                String date = next[1];
                String[] st = date.split(" ");
                date = st[0];
                //bring date to yyyy/MM/dd format for xsd:dateTime
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Date conv = sdf.parse(date);
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                date = sdf2.format(conv);
                s.add(next[0]);
                s.add(date);
                s.add(next[2]);
                finalDailySteps.add(s);
            }
            //System.out.println(finalDailySteps);
        } else {
            finalHourlySteps = new ArrayList<>();
            while ((next = csv.readNext()) != null) {
                List<String> s = new ArrayList<>();
                String date = next[1];
                //get date with hour info
                String[] st1 = date.split(" ");
                String[] st2 = date.split(":");
                date = st2[0]+":00:00 "+st1[2];
                //bring date to yyyy/MM/ddTHH:mm:ss a format for xsd:dateTime
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                Date conv = sdf.parse(date);
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = sdf2.format(conv);
                String[] st3 = date.split(" ");
                date = st3[0]+"T"+st3[1];
                s.add(next[0]); s.add(date); s.add(next[2]);
                finalHourlySteps.add(s);
            }
            //System.out.println(finalHourlySteps);
        }
    }

    public void CaloriesReader(String filepath, int choice) throws IOException, ParseException {
        //initializing the file reader
        FileReader f = new FileReader(filepath);
        CSVReader csv = new CSVReader(f);
        String[] next;
        //skipping first row
        csv.readNext();
        //reading the csv line by line
        if (choice==1) {
            finalDailyCalories = new ArrayList<>();
            while ((next = csv.readNext()) != null) {
                List<String> s = new ArrayList<>();
                //get date without time
                String date = next[1];
                String[] st = date.split(" ");
                date = st[0];
                //bring date to yyyy/MM/dd format for xsd:dateTime
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Date conv = sdf.parse(date);
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                date = sdf2.format(conv);
                s.add(next[0]);
                s.add(date);
                s.add(next[14]);
                finalDailyCalories.add(s);
            }
            //System.out.println(finalDailyCalories);
        } else {

            finalHourlyCalories = new ArrayList<>();
            while ((next = csv.readNext()) != null) {
                List<String> s = new ArrayList<>();
                String date = next[1];
                //get date with hour info
                String[] st1 = date.split(" ");
                String[] st2 = date.split(":");
                date = st2[0]+":00:00 "+st1[2];
                //bring date to yyyy/MM/ddTHH:mm:ss a format for xsd:dateTime
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                Date conv = sdf.parse(date);
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = sdf2.format(conv);
                String[] st3 = date.split(" ");
                date = st3[0]+"T"+st3[1];
                s.add(next[0]); s.add(date); s.add(next[2]);
                finalHourlyCalories.add(s);
            }
            //System.out.println(finalHourlyCalories);
        }
    }

    public void WeightReader(String filepath) throws IOException, ParseException {
        //initializing the file reader
        FileReader f = new FileReader(filepath);
        CSVReader csv = new CSVReader(f);
        String[] next;
        //skipping first row
        csv.readNext();
        //reading the csv line by line
        finalWeight = new ArrayList<>();
        while ((next = csv.readNext()) != null) {
            float weight = Float.parseFloat(next[2]);
            // get date without time
            String date = next[1];
            String[] st = date.split(" ");
            date = st[0];
            //bring date to yyyy/MM/dd format for xsd:dateTime
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date conv = sdf.parse(date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf2.format(conv);
            List<String> s = new ArrayList<>();
            s.add(next[0]); s.add(date); s.add(String.format("%.2f",weight));
            finalWeight.add(s);
        }
        //System.out.println(finalWeight);
    }

    public void BMIReader(String filepath) throws IOException, ParseException {
        //initializing the file reader
        FileReader f = new FileReader(filepath);
        CSVReader csv = new CSVReader(f);
        String[] next;
        //skipping first row
        csv.readNext();
        //reading the csv line by line
        finalBMI = new ArrayList<>();
        while ((next = csv.readNext()) != null) {
            float bmi = Float.parseFloat(next[5]);
            // get date without time
            String date = next[1];
            String[] st = date.split(" ");
            date = st[0];
            //bring date to yyyy/MM/dd format for xsd:dateTime
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date conv = sdf.parse(date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf2.format(conv);
            List<String> s = new ArrayList<>();
            s.add(next[0]); s.add(date); s.add(String.format("%.2f",bmi));
            finalBMI.add(s);
        }
        //System.out.println(finalBMI);
    }

    public List<List<String>> getFinalDailySteps() { return finalDailySteps; }
    public List<List<String>> getFinalHourlySteps() { return finalHourlySteps; }
    public List<List<String>> getFinalDailyHeartRate() { return finalDailyHeartRate; }
    public List<List<String>> getFinalHourlyHeartRate() { return finalHourlyHeartRate; }
    public List<List<String>> getFinalSleep() { return finalSleep; }
    public List<List<String>> getFinalDailyCalories() { return finalDailyCalories; }
    public List<List<String>> getFinalHourlyCalories() { return finalHourlyCalories; }
    public List<List<String>> getFinalWeight() { return finalWeight; }
    public List<List<String>> getFinalBMI() { return finalBMI; }

}
