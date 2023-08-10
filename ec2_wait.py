import boto3
import time

def lambda_handler(event, context):
    # Replace this with your instance ID
    instance_id = 'YOUR_INSTANCE_ID'

    # Create a boto3 EC2 resource
    ec2 = boto3.resource(
        'ec2',
        region_name=region,
        aws_access_key_id=aws_access_key,
        aws_secret_access_key=aws_secret_key
    )

    # Get the EC2 instance object
    instance = ec2.Instance(instance_id)

    # Loop to wait for the instance to reach the "running" state
    max_retries = 18  # Wait for a total of 15 minutes (18 * 5 seconds)
    for retry in range(max_retries):
        instance.reload()  # Refresh instance attributes
        if instance.state['Name'] == 'running':
            print("Instance is now running")
            return "Instance is now running"
        else:
            # Wait for 5 seconds before checking again
            time.sleep(5)
    
    print("Timeout: Instance did not reach 'running' state within the specified time.")
    return "Timeout: Instance did not reach 'running' state within the specified time."
