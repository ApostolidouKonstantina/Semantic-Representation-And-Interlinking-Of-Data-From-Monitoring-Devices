import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;

import static org.eclipse.rdf4j.model.util.Values.iri;

import java.io.IOException;
import java.util.List;

public class DataBaseHandler {

    String SERVER = "http://localhost:7200/";
    String REPOSITORY_ID = "ActivityObservation";
    String ONTOLOGY_URI = "http://www.semanticweb.org/ActivityObservation#";
    RemoteRepositoryManager manager;
    Repository repository;
    RepositoryConnection connection;
    boolean passed;

    //classes
    String BMI_PROPERTY = ONTOLOGY_URI+"BMIProperty";
    String CALORIES_PROPERTY = ONTOLOGY_URI+"CaloriesProperty";
    String HEART_RATE_PROPERTY = ONTOLOGY_URI+"HeartRateProperty";
    String SLEEP_PROPERTY = ONTOLOGY_URI+"SleepProperty";
    String STEPS_PROPERTY = ONTOLOGY_URI+"StepsProperty";
    String WEIGHT_PROPERTY = ONTOLOGY_URI+"WeightProperty";
    String DAILY_BMI = ONTOLOGY_URI+"DailyBMI";
    String DAILY_CALORIES = ONTOLOGY_URI+"DailyCalories";
    String DAILY_HEART_RATE = ONTOLOGY_URI+"DailyHeartRate";
    String DAILY_SLEEP = ONTOLOGY_URI+"DailySleep";
    String DAILY_STEPS = ONTOLOGY_URI+"DailySteps";
    String DAILY_WEIGHT = ONTOLOGY_URI+"DailyWeight";
    String HOURLY_HEART_RATE = ONTOLOGY_URI+"HourlyHeartRate";
    String HOURLY_STEPS = ONTOLOGY_URI+"HourlySteps";
    String HOURLY_CALORIES = ONTOLOGY_URI+"HourlyCalories";

    //properties
    String OBSERVED_PROPERTY = "http://www.w3.org/ns/sosa/observedProperty";
    String IS_OBSERVATION_FOR = ONTOLOGY_URI+"isObservationFor";
    String MEASURED = ONTOLOGY_URI+"measured";
    String TIME = ONTOLOGY_URI+"time";

    public void connect(){
        manager = new RemoteRepositoryManager(SERVER);
        manager.init();
        repository = manager.getRepository(REPOSITORY_ID);
        connection = repository.getConnection();
    }

    public boolean addHeartRateData(List<List<String>> HeartRateInfo, int choice) throws IOException {
        connection.begin();
        Model model = new TreeModel();
        ValueFactory fact = SimpleValueFactory.getInstance();
        for (List<String> str : HeartRateInfo){
            IRI obs;
            // str --> userID, date, heartRate
            if (choice==1) {
                obs = iri(ONTOLOGY_URI + str.get(0) + "_DailyHeartRateObservation_on_" + str.get(1));
                model.add(obs, RDF.TYPE, iri(DAILY_HEART_RATE));
            } else {
                obs = iri(ONTOLOGY_URI + str.get(0) + "_HourlyHeartRateObservation_on_" + str.get(1));
                model.add(obs, RDF.TYPE, iri(HOURLY_HEART_RATE));
            }
            IRI obs_prop = iri(ONTOLOGY_URI+"HeartRate");
            IRI userID = iri(ONTOLOGY_URI+str.get(0));
            model.add(obs, iri(OBSERVED_PROPERTY), obs_prop);
            model.add(obs_prop, RDF.TYPE, iri(HEART_RATE_PROPERTY));
            model.add(obs, iri(IS_OBSERVATION_FOR), userID);
            model.add(obs, iri(MEASURED), fact.createLiteral(str.get(2), XSD.LONG));
            model.add(obs, iri(TIME), fact.createLiteral(str.get(1), XSD.DATETIME));
        }
        connection.add(model);
        //Shacl validation
        ShaclValidation sv = new ShaclValidation();
        sv.ShaclVal();
        sv.getConnection().begin();
        sv.getConnection().add(model);
        try {
            sv.getConnection().commit();
            connection.commit();
            connection.close();
            passed = true;
        } catch (RepositoryException e) {
            connection.close();
            passed = false;
        }
        sv.closeConnection();
        return passed;
    }

    public boolean addStepData(List<List<String>> StepInfo, int choice) throws IOException {
        connection.begin();
        Model model = new TreeModel();
        ValueFactory fact = SimpleValueFactory.getInstance();
        for (List<String> str : StepInfo){
            // str --> userID, date, steps
            IRI obs;
            if (choice==1) {
                obs = iri(ONTOLOGY_URI + str.get(0) + "_DailyStepObservation_on_" + str.get(1));
                model.add(obs, RDF.TYPE, iri(DAILY_STEPS));
            }
            else {
                obs = iri(ONTOLOGY_URI + str.get(0) + "_HourlyStepObservation_on_" + str.get(1));
                model.add(obs, RDF.TYPE, iri(HOURLY_STEPS));
            }
            IRI obs_prop = iri(ONTOLOGY_URI+"Steps");
            IRI userID = iri(ONTOLOGY_URI+str.get(0));
            model.add(obs, iri(OBSERVED_PROPERTY), obs_prop);
            model.add(obs_prop, RDF.TYPE, iri(STEPS_PROPERTY));
            model.add(obs, iri(IS_OBSERVATION_FOR), userID);
            model.add(obs, iri(MEASURED), fact.createLiteral(str.get(2), XSD.LONG));
            model.add(obs, iri(TIME), fact.createLiteral(str.get(1), XSD.DATETIME));
        }
        connection.add(model);
        //Shacl validation
        ShaclValidation sv = new ShaclValidation();
        sv.ShaclVal();
        sv.getConnection().begin();
        sv.getConnection().add(model);
        try {
            sv.getConnection().commit();
            connection.commit();
            connection.close();
            passed = true;
        } catch (RepositoryException e) {
            connection.close();
            passed = false;
        }
        sv.closeConnection();
        return passed;
    }

    public boolean addSleepData(List<List<String>> SleepInfo) throws IOException {
        connection.begin();
        Model model = new TreeModel();
        ValueFactory fact = SimpleValueFactory.getInstance();
        for (List<String> str : SleepInfo){
            // str --> userID, date, steps
            IRI obs = iri(ONTOLOGY_URI+str.get(0)+"_SleepObservation_on_"+ str.get(1));
            IRI obs_prop = iri(ONTOLOGY_URI+"Sleep");
            IRI userID = iri(ONTOLOGY_URI+str.get(0));
            model.add(obs, RDF.TYPE, iri(DAILY_SLEEP));
            model.add(obs, iri(OBSERVED_PROPERTY), obs_prop);
            model.add(obs_prop, RDF.TYPE, iri(SLEEP_PROPERTY));
            model.add(obs, iri(IS_OBSERVATION_FOR), userID);
            model.add(obs, iri(MEASURED), fact.createLiteral(str.get(2), XSD.LONG));
            model.add(obs, iri(TIME), fact.createLiteral(str.get(1), XSD.DATETIME));
        }
        connection.add(model);
        //Shacl validation
        ShaclValidation sv = new ShaclValidation();
        sv.ShaclVal();
        sv.getConnection().begin();
        sv.getConnection().add(model);
        try {
            sv.getConnection().commit();
            connection.commit();
            connection.close();
            passed = true;
        } catch (RepositoryException e) {
            connection.close();
            passed = false;
        }
        sv.closeConnection();
        return passed;
    }

    public boolean addCaloriesData(List<List<String>> CalorieInfo, int choice) throws IOException {
        connection.begin();
        Model model = new TreeModel();
        ValueFactory fact = SimpleValueFactory.getInstance();
        for (List<String> str : CalorieInfo){
            IRI obs;
            // str --> userID, date, calories
            if (choice==1) {
                obs = iri(ONTOLOGY_URI + str.get(0) + "_DailyCaloriesObservation_on_" + str.get(1));
                model.add(obs, RDF.TYPE, iri(DAILY_CALORIES));
            }
            else {
                obs = iri(ONTOLOGY_URI + str.get(0) + "_HourlyCaloriesObservation_on_" + str.get(1));
                model.add(obs, RDF.TYPE, iri(HOURLY_CALORIES));
            }
            IRI obs_prop = iri(ONTOLOGY_URI+"Calories");
            IRI userID = iri(ONTOLOGY_URI+str.get(0));
            // TRIPLETS
            model.add(obs, iri(OBSERVED_PROPERTY), obs_prop);
            model.add(obs_prop, RDF.TYPE, iri(CALORIES_PROPERTY));
            model.add(obs, iri(IS_OBSERVATION_FOR), userID);
            model.add(obs, iri(MEASURED), fact.createLiteral(str.get(2), XSD.LONG));
            model.add(obs, iri(TIME), fact.createLiteral(str.get(1), XSD.DATETIME));
        }
        connection.add(model);
        //Shacl validation
        ShaclValidation sv = new ShaclValidation();
        sv.ShaclVal();
        sv.getConnection().begin();
        sv.getConnection().add(model);
        try {
            sv.getConnection().commit();
            connection.commit();
            connection.close();
            passed = true;
        } catch (RepositoryException e) {
            connection.close();
            passed = false;
        }
        sv.closeConnection();
        return passed;
    }

    public boolean addWeightData(List<List<String>> WeightInfo) throws IOException {
        connection.begin();
        Model model = new TreeModel();
        ValueFactory fact = SimpleValueFactory.getInstance();
        for (List<String> str : WeightInfo){
            // str --> userID, date, weight
            IRI obs = iri(ONTOLOGY_URI+str.get(0)+"_WeightObservation_on_"+ str.get(1));
            IRI obs_prop = iri(ONTOLOGY_URI+"Weight");
            IRI userID = iri(ONTOLOGY_URI+str.get(0));
            model.add(obs, RDF.TYPE, iri(DAILY_WEIGHT));
            model.add(obs, iri(OBSERVED_PROPERTY), obs_prop);
            model.add(obs_prop, RDF.TYPE, iri(WEIGHT_PROPERTY));
            model.add(obs, iri(IS_OBSERVATION_FOR), userID);
            model.add(obs, iri(MEASURED), fact.createLiteral(str.get(2), XSD.DOUBLE));
            model.add(obs, iri(TIME), fact.createLiteral(str.get(1), XSD.DATETIME));
        }
        connection.add(model);
        //Shacl validation
        ShaclValidation sv = new ShaclValidation();
        sv.ShaclVal();
        sv.getConnection().begin();
        sv.getConnection().add(model);
        try {
            sv.getConnection().commit();
            connection.commit();
            connection.close();
            passed = true;
        } catch (RepositoryException e) {
            connection.close();
            passed = false;
        }
        sv.closeConnection();
        return passed;
    }

    public boolean addBMIData(List<List<String>> BMIInfo) throws IOException {
        connection.begin();
        Model model = new TreeModel();
        ValueFactory fact = SimpleValueFactory.getInstance();
        for (List<String> str : BMIInfo){
            // str --> userID, date, bmi
            IRI obs = iri(ONTOLOGY_URI+str.get(0)+"_BMIObservation_on_"+ str.get(1));
            IRI obs_prop = iri(ONTOLOGY_URI+"BMI");
            IRI userID = iri(ONTOLOGY_URI+str.get(0));
            model.add(obs, RDF.TYPE, iri(DAILY_BMI));
            model.add(obs, iri(OBSERVED_PROPERTY), obs_prop);
            model.add(obs_prop, RDF.TYPE, iri(BMI_PROPERTY));
            model.add(obs, iri(IS_OBSERVATION_FOR), userID);
            model.add(obs, iri(MEASURED), fact.createLiteral(str.get(2), XSD.DOUBLE));
            model.add(obs, iri(TIME), fact.createLiteral(str.get(1), XSD.DATETIME));
        }
        connection.add(model);
        //Shacl validation
        ShaclValidation sv = new ShaclValidation();
        sv.ShaclVal();
        sv.getConnection().begin();
        sv.getConnection().add(model);
        try {
            sv.getConnection().commit();
            connection.commit();
            connection.close();
            passed = true;
        } catch (RepositoryException e) {
            connection.close();
            passed = false;
        }
        sv.closeConnection();
        return passed;
    }
}
