import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    static String filepath = "FitBitData\\2\\";

    public static void main(String[] args) throws IOException, ParseException {
        FileHandler ifh = new FileHandler();
        DataBaseHandler dbh = new DataBaseHandler();

        Scanner sc = new Scanner(System.in);
        System.out.print("Type 1 to load Heart Rate Data \nType 2 to load Sleep Data \nType 3 to load Step Data\n" +
                "Type 4 to load Calorie Data \nType 5 to load Weight Data \nType 6 to load BMI Data \nYour choice: ");
        int choice = sc.nextInt();
        boolean res;

        if (choice==1){
            System.out.print("Type 1 to load Daily Heart Rate Data \nType 2 to load Hourly Heart Rate Data\nYour choice: ");
            int choice2 = sc.nextInt();
            if (choice2==1 || choice2==2){
                dbh.connect();
                ifh.HeartRateReader(filepath+"heartrate_seconds_merged.csv", choice2);
                List<List<String>> Data;
                if (choice2==1)
                    Data = ifh.getFinalDailyHeartRate();
                else
                    Data = ifh.getFinalHourlyHeartRate();
                res = dbh.addHeartRateData(Data, choice2);
            } else {
                System.out.println("Wrong Input");
                return;
            }
        } else if (choice==2){
            dbh.connect();
            //read sleep data
            ifh.SleepReader(filepath+"minuteSleep_merged.csv");
            res = dbh.addSleepData(ifh.getFinalSleep());
        } else if (choice==3){
            System.out.print("Type 1 to load Daily Step Data \nType 2 to load Hourly Step Data\nYour choice: ");
            int choice2 = sc.nextInt();
            if (choice2==1){
                dbh.connect();
                ifh.StepsReader(filepath+"dailyActivity_merged.csv", choice2);
                res = dbh.addStepData(ifh.getFinalDailySteps(), choice2);
            } else if (choice2==2) {
                dbh.connect();
                ifh.StepsReader(filepath+"hourlySteps_merged.csv", choice2);
                res = dbh.addStepData(ifh.getFinalHourlySteps(), choice2);
            } else {
                System.out.println("Wrong Input");
                return;
            }
        } else if (choice==4){
            System.out.print("Type 1 to load Daily Calorie Data \nType 2 to load Hourly Calorie Data\nYour choice: ");
            int choice2 = sc.nextInt();
            if (choice2==1){
                dbh.connect();
                ifh.CaloriesReader(filepath+"dailyActivity_merged.csv", choice2);
                res = dbh.addCaloriesData(ifh.getFinalDailyCalories(), choice2);
            } else if (choice2==2) {
                dbh.connect();
                ifh.CaloriesReader(filepath+"hourlyCalories_merged.csv", choice2);
                res = dbh.addCaloriesData(ifh.getFinalHourlyCalories(), choice2);
            } else {
                System.out.println("Wrong Input");
                return;
            }
        } else if (choice==5){
            dbh.connect();
            //read weight log data
            ifh.WeightReader(filepath+"weightLogInfo_merged.csv");
            res = dbh.addWeightData(ifh.getFinalWeight());
        } else if (choice==6){
            dbh.connect();
            //read bmi data
            ifh.BMIReader(filepath+"weightLogInfo_merged.csv");
            res = dbh.addBMIData(ifh.getFinalBMI());
        } else {
            System.out.println("Wrong Input");
            return;
        }

        //RESULT
        if (res)
            System.out.println("DONE");
        else
            System.out.println("SHACL Violation!");

    }

}
