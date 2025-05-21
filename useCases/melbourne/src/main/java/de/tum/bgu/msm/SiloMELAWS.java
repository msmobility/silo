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
 * Implements SILO for the Great Melbourne
 *
 * @author Qin Zhang*/


public class SiloMELAWS {

    private final static Logger logger = LogManager.getLogger(SiloMELAWS.class);
    private final static String outputDir = "/home/ubuntu/melbourne/scenOutput/base/"; // Replace with your output directory
    private final static String bucketName = "phmlandusetransportmatsimhealthdata";// Replace with your S3 bucket name
    private final static String folderName = "melbourne/simulationResults/base/"; //Replace with your target folder name in S3 bucket
    private final static String region = "ap-southeast-2";// Replace with your AWS region (e.g., “us-east-1")
    private final static String instanceId = "i-0994f34e4fb1b499d"; // Replace with your instance id


    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Great Melbourne");
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForMelbourne(properties, config);
        DataBuilderHealth.read(properties, dataContainer, config);
        ModelContainer modelContainer = ModelBuilderMEL.getModelContainerForMelbourne(dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        //model.addResultMonitor(new ResultsMonitorMuc(dataContainer, properties));
        model.addResultMonitor(new MultiFileResultsMonitor(dataContainer, properties));
        //model.addResultMonitor(new HouseholdSatisfactionMonitor(dataContainer, properties, modelContainer));
        //model.addResultMonitor(new ModalSharesResultMonitor(dataContainer, properties));
        model.runModel();
        logger.info("Finished SILO.");

        // Upload files to S3
        //Note that: File size larger than 5 GB is not allowed for single PUT uploading in AWS S3. It is possible to upload it via terminal
        //TODO: now we copy scenOutput to S3 via terminal, later can improve it by adapting the code to implement Multipart upload
        //uploadToS3(outputDir, bucketName, folderName, region);
        // Stop the EC2 instance
        stopEC2Instance(region, instanceId);
    }

    public static void uploadToS3(String outputDir, String bucketName, String folderName, String region) {
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
        File folder = new File(outputDir);        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("The specified directory does not exist or is not a directory: " + outputDir);
            return;
        }        uploadDirectoryRecursively(folder, bucketName, folderName, s3Client);        s3Client.close();
    }


    private static void uploadDirectoryRecursively(File folder, String bucketName, String folderName, S3Client s3Client) {
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                // Generate the full key for the file in S3 (include subfolder structure)
                String keyName = folderName + (folderName.endsWith("/") ? "" : "/") + file.getPath()
                        .replace(outputDir, "");
                keyName = keyName.startsWith("/") ? keyName.substring(1) : keyName;                System.out.println("Uploading file: " + keyName);
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build();
                s3Client.putObject(putObjectRequest, Path.of(file.getAbsolutePath()));
                System.out.println("Uploaded " + keyName + " to bucket " + bucketName);
            } else if (file.isDirectory()) {
                // Recursively process subdirectories
                uploadDirectoryRecursively(file, bucketName, folderName, s3Client);
            }
        }
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
