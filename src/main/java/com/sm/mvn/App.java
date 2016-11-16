package com.sm.mvn;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {

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

    public static void main( String[] args ) throws Exception {

        System.out.println("Hello World!");
        App app = new App();
    }
}
