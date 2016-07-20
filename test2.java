import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CancelSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.LaunchSpecification;
import com.amazonaws.services.ec2.model.RequestSpotInstancesRequest;
import com.amazonaws.services.ec2.model.RequestSpotInstancesResult;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;


public class test2 {
	 public static void main(String[] args) throws IOException {

			
			// Retrieves the credentials from an AWSCredentials.properties file.
			AWSCredentials credentials = null;
			credentials = new ProfileCredentialsProvider().getCredentials();
		
			// Create the AmazonEC2Client object so we can call various APIs.
			AmazonEC2 ec2 = new AmazonEC2Client(credentials);
		
			// Create a new security group.
			try {
			    CreateSecurityGroupRequest securityGroupRequest = new CreateSecurityGroupRequest("GettingStartedGroup", "Getting Started Security Group");
			    ec2.createSecurityGroup(securityGroupRequest);
			} catch (AmazonServiceException ase) {
			    // Likely this means that the group is already created, so ignore.
			    System.out.println(ase.getMessage());
			}
		
			String ipAddr = "0.0.0.0/0";
		
			// Get the IP of the current host, so that we can limit the Security
			// Group by default to the ip range associated with your subnet.
			try {
			    InetAddress addr = InetAddress.getLocalHost();
		
			    // Get IP Address
			    ipAddr = addr.getHostAddress()+"/10";
			} catch (UnknownHostException e) {
			}
		
			// Create a range that you would like to populate.
			ArrayList<String> ipRanges = new ArrayList<String>();
			ipRanges.add(ipAddr);
		
			// Open up port 22 for TCP traffic to the associated IP
			// from above (e.g. ssh traffic).
			ArrayList<IpPermission> ipPermissions = new ArrayList<IpPermission> ();
			IpPermission ipPermission = new IpPermission();
			ipPermission.setIpProtocol("tcp");
			ipPermission.setFromPort(new Integer(22));
			ipPermission.setToPort(new Integer(22));
			ipPermission.setIpRanges(ipRanges);
			ipPermissions.add(ipPermission);
		
			try {
			    // Authorize the ports to the used.
			    AuthorizeSecurityGroupIngressRequest ingressRequest =
			        new AuthorizeSecurityGroupIngressRequest("GettingStartedGroup",ipPermissions);
			    ec2.authorizeSecurityGroupIngress(ingressRequest);
			} catch (AmazonServiceException ase) {
			    // Ignore because this likely means the zone has
			    // already been authorized.
			    System.out.println(ase.getMessage());
			}
			
			
			
			RequestSpotInstancesRequest requestRequest = new RequestSpotInstancesRequest();

			// Request 1 x t1.micro instance with a bid price of $0.03.
			requestRequest.setSpotPrice("0.03");
			requestRequest.setInstanceCount(Integer.valueOf(1));

			// Setup the specifications of the launch. This includes the
			// instance type (e.g. t1.micro) and the latest Amazon Linux
			// AMI id available. Note, you should always use the latest
			// Amazon Linux AMI id or another of your choosing.
			LaunchSpecification launchSpecification = new LaunchSpecification();
			launchSpecification.setImageId("ami-8c1fece5");
			launchSpecification.setInstanceType("t1.micro");

			// Add the security group to the request.
			ArrayList<String> securityGroups = new ArrayList<String>();
			securityGroups.add("GettingStartedGroup");
			launchSpecification.setSecurityGroups(securityGroups);

			// Add the launch specifications to the request.
			requestRequest.setLaunchSpecification(launchSpecification);

			// Call the RequestSpotInstance API.
			RequestSpotInstancesResult requestResult = ec2.requestSpotInstances(requestRequest);
			List<SpotInstanceRequest> requestResponses = requestResult.getSpotInstanceRequests();

			// Setup an arraylist to collect all of the request ids we want to
			// watch hit the running state.
			ArrayList<String> spotInstanceRequestIds = new ArrayList<String>();

			// Add all of the request ids to the hashset, so we can determine when they hit the
			// active state.
			for (SpotInstanceRequest requestResponse : requestResponses) {
			    System.out.println("Created Spot Request: "+requestResponse.getSpotInstanceRequestId());
			    spotInstanceRequestIds.add(requestResponse.getSpotInstanceRequestId());
			}
			boolean anyOpen;

			do {
			    // Create the describeRequest object with all of the request ids
			    // to monitor (e.g. that we started).
			    DescribeSpotInstanceRequestsRequest describeRequest = new DescribeSpotInstanceRequestsRequest();
			    describeRequest.setSpotInstanceRequestIds(spotInstanceRequestIds);

			    // Initialize the anyOpen variable to false - which assumes there
			    // are no requests open unless we find one that is still open.
			    anyOpen=false;

			    try {
			        // Retrieve all of the requests we want to monitor.
			        DescribeSpotInstanceRequestsResult describeResult = ec2.describeSpotInstanceRequests(describeRequest);
			        List<SpotInstanceRequest> describeResponses = describeResult.getSpotInstanceRequests();

			        // Look through each request and determine if they are all in
			        // the active state.
			        for (SpotInstanceRequest describeResponse : describeResponses) {
			            // If the state is open, it hasn't changed since we attempted
			            // to request it. There is the potential for it to transition
			            // almost immediately to closed or cancelled so we compare
			            // against open instead of active.
			        if (describeResponse.getState().equals("open")) {
			            anyOpen = true;
			            break;
			        }
			    }
			    } catch (AmazonServiceException e) {
			        // If we have an exception, ensure we don't break out of
			        // the loop. This prevents the scenario where there was
			        // blip on the wire.
			        anyOpen = true;
			      }

			      try {
			          // Sleep for 60 seconds.
			          Thread.sleep(60*1000);
			      } catch (Exception e) {
			          // Do nothing because it woke up early.
			      }
			  } while (anyOpen);
			
			try {
			    // Cancel requests.
			    CancelSpotInstanceRequestsRequest cancelRequest = new CancelSpotInstanceRequestsRequest(spotInstanceRequestIds);
			    ec2.cancelSpotInstanceRequests(cancelRequest);
			} catch (AmazonServiceException e) {
			    // Write out any exceptions that may have occurred.
			    System.out.println("Error cancelling instances");
			    System.out.println("Caught Exception: " + e.getMessage());
			    System.out.println("Reponse Status Code: " + e.getStatusCode());
			    System.out.println("Error Code: " + e.getErrorCode());
			    System.out.println("Request ID: " + e.getRequestId());
			}
			
			try {
			    // Terminate instances.
			    TerminateInstancesRequest terminateRequest = new TerminateInstancesRequest(instanceIds);
			    ec2.terminateInstances(terminateRequest);
			} catch (AmazonServiceException e) {
			    // Write out any exceptions that may have occurred.
			    System.out.println("Error terminating instances");
			    System.out.println("Caught Exception: " + e.getMessage());
			    System.out.println("Reponse Status Code: " + e.getStatusCode());
			    System.out.println("Error Code: " + e.getErrorCode());
			    System.out.println("Request ID: " + e.getRequestId());
			}
			
			

	 }
	 
}
