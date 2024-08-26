import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;
import java.io.*;

import java.io.StringReader;

public class ShaclValidation {

    RepositoryConnection connection;

    String PREFIXES = "@prefix act:<http://www.semanticweb.org/ActivityObservation#>. @prefix sosa: <http://www.w3.org/ns/sosa/>." +
            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>. @prefix sh: <http://www.w3.org/ns/shacl#> ." ;

    String USER_SHAPE = PREFIXES+"act:UserShape a sh:NodeShape; sh:targetClass sosa:Observation; " +
            "sh:property [sh:path act:isObservationFor; sh:maxCount 1; sh:minCount 1;].";

    String DATE_SHAPE = PREFIXES + "act:DateShape a sh:NodeShape; sh:targetClass sosa:Observation; " +
            "sh:property [sh:path sh:datatype; act:time xsd:dateTime;];"+
            "sh:property [sh:path act:time; sh:maxCount 1; sh:minCount 1;].";

    String MEASURED_SHAPE = PREFIXES + "act:MeasuredShape a sh:NodeShape; sh:targetClass sosa:Observation; " +
            "sh:property [sh:path sh:datatype; sh:or ([act:measured xsd:double] [act:measured xsd:long]);];"+
            "sh:property [sh:path act:measured; sh:maxCount 1; sh:minCount 1;].";

    String OBSERVATION_SHAPE = PREFIXES + "act:ObservationShape a sh:NodeShape;  sh:targetClass sosa:Observation ;" +
            "sh:property [sh:path sosa:observedProperty; sh:or ([sh:class act:StepsProperty] [sh:class act:SleepProperty] " +
            "[sh:class act:HeartRateProperty] [sh:class act:BMIProperty] [sh:class act:CaloriesProperty] [sh:class act:WeightProperty]);].";


    public void ShaclVal() throws IOException {
        ShaclSail ss = new ShaclSail(new MemoryStore());
        Repository rep = new SailRepository(ss);
        connection = rep.getConnection();
        connection.begin();
        StringReader shaclRules = new StringReader(String.join(USER_SHAPE, DATE_SHAPE, MEASURED_SHAPE, OBSERVATION_SHAPE));
        connection.add(shaclRules, "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH);
        connection.commit();
    }

    public void closeConnection(){ connection.close(); }
    public RepositoryConnection getConnection() { return connection; }
}
