package de.tum.bgu.msm.util;

import de.tum.bgu.msm.data.jobTypes.JobTypeFactory;
import uk.cam.mrc.phm.jobTypes.MelbourneJobTypeFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MelbourneImplementationConfig implements ImplementationConfig {

    private final static MelbourneImplementationConfig instance = new MelbourneImplementationConfig();

    private MelbourneImplementationConfig() {}

    public static MelbourneImplementationConfig get() {
        return instance;
    }

    @Override
    public JobTypeFactory getJobTypeFactory() {
        return new MelbourneJobTypeFactory();
    }

    public static Properties getMelbourneProperties() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("./project.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load project.properties file", e);
        }
        return properties;
    }

    public static Properties getMitoBaseProperties() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("./mito7daysMEL_reference.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load project.properties file", e);
        }
        return properties;
    }
}
