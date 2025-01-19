package de.tum.bgu.msm;

import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.health.DataBuilderHealth;
import de.tum.bgu.msm.health.HealthDataContainerImpl;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.nio.file.Path;

/**
 * Implements SILO for the Great Manchester
 *
 * @author Qin Zhang*/


public class SiloMCRAWS {

    private final static Logger logger = LogManager.getLogger(SiloMCRAWS.class);
    private final static String outputDir = "/home/ubuntu/manchester/scenOutput/base"; // Replace with your output directory
    private final static String bucketName = "phmlandusetransportmatsimhealthdata";// Replace with your S3 bucket name
    private final static String folderName = "manchester/simulationResults/base/"; //Replace with your target folder name in S3 bucket
    private final static String region = "ap-southeast-2";// Replace with your AWS region (e.g., “us-east-1")
    private final static String instanceId = "i-0994f34e4fb1b499d"; // Replace with your instance id


    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Great Manchester");
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForManchester(properties, config);
        DataBuilderHealth.read(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilderMCR.getModelContainerForManchester(dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        //model.addResultMonitor(new ResultsMonitorMuc(dataContainer, properties));
        model.addResultMonitor(new MultiFileResultsMonitor(dataContainer, properties));
        //model.addResultMonitor(new HouseholdSatisfactionMonitor(dataContainer, properties, modelContainer));
        //model.addResultMonitor(new ModalSharesResultMonitor(dataContainer, properties));
        model.runModel();
        logger.info("Finished SILO.");

        // Upload files to S3
        uploadToS3(outputDir, bucketName, folderName, region);
        // Stop the EC2 instance
        stopEC2Instance(region, instanceId);
    }

    private static void uploadToS3(String outputDir, String bucketName, String folderName, String region) {
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
        File folder = new File(outputDir);
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No files found in the directory: " + outputDir);
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                String keyName = file.getName();
                System.out.println("Uploading file: " + keyName);
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(folderName + keyName)
                        .build();
                s3Client.putObject(putObjectRequest, Path.of(file.getAbsolutePath()));
                System.out.println("Uploaded " + keyName + " to bucket " + bucketName);
            }
        }
        s3Client.close();
    }
    private static void stopEC2Instance(String region, String instanceId) {
        Ec2Client ec2Client = Ec2Client.builder()
                .region(Region.of(region))
                .credentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
        if (instanceId == null) {
            System.err.println("Unable to retrieve instance ID.");
            return;
        }
        StopInstancesRequest stopRequest = StopInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();
        StopInstancesResponse response = ec2Client.stopInstances(stopRequest);
        System.out.println("Stopping instance: " + instanceId);
        System.out.println("State: " + response.stoppingInstances());
        ec2Client.close();
    }
}
