package com.sm.mvn;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public void readPOMfile(String fileName) throws IOException, XmlPullParserException {

        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader(fileName));

        for (Dependency d : model.getDependencies()) {
            System.out.println("Artifact Id=" + d.getArtifactId() + " Group Id=" + d.getGroupId() + " Version=" + d.getVersion());
        }

        for (Dependency d : model.getDependencyManagement().getDependencies()) {
            System.out.println("Artifact Id=" + d.getArtifactId() + " Group Id=" + d.getGroupId() + " Version=" + d.getVersion());
        }
    }

    public Model  getMavenModel(String fileName) {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            return reader.read(new FileReader(fileName));
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void buildMavenDependencyGraph(Model model) {
        String createDependencyQuery = "merge (a1:Artifact { artifactId : {artifactId1}, groupId: {groupId1}, version: {version1}}) "
        		+ " merge (a2:Artifact { artifactId : {artifactId2}, groupId: {groupId2}, version:{version2} }) "
        		+ " merge (a1) -[:DEPENDS_ON] -> (a2)"; 
        
        try (Session session = getNeo4JSession()) {
        	
            for (Dependency d : model.getDependencies()) {
            	LOGGER.info("Adding dependency to - [{}, {}] ",d.getArtifactId(), d.getGroupId() );
                session.run(createDependencyQuery, Values.parameters(
                		"artifactId1", model.getArtifactId(), 
                		"groupId1", model.getGroupId(), 
                		"version1", model.getVersion(),
                		"artifactId2", d.getArtifactId(), 
                		"version2", d.getVersion(),
                		"groupId2", d.getGroupId())).consume();
            }
            
        }

    }

    private Session getNeo4JSession() {

        Config noSSL = Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig();
        Driver driver = GraphDatabase.driver("bolt://localhost",AuthTokens.basic("neo4j","admin"),noSSL); // <password>
        return driver.session();
    }
    
    public void loadMavenDependencyFromPom(String fileName) {
    	
    	buildMavenDependencyGraph(getMavenModel(fileName));
   
    }
    
    public static void main( String[] args ) throws Exception {

    	try {
			System.out.println("Hello World!");
			App app = new App();
			
			app.loadMavenDependencyFromPom("src/main/resources/sample_pom.xml");

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    }
}
