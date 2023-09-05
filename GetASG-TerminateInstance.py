import boto3
import datetime

# Define your AWS region and Auto Scaling Group name
region = 'your-region'
asg_name = 'your-auto-scaling-group-name'

# Create a Boto3 Auto Scaling client
client = boto3.client('autoscaling', region_name=region)

# Get the current date
current_date = datetime.datetime.now()

# Get the instances in the Auto Scaling Group
response = client.describe_auto_scaling_groups(AutoScalingGroupNames=[asg_name])
if 'AutoScalingGroups' in response:
    asg = response['AutoScalingGroups'][0]
    instances = asg.get('Instances', [])
    
    for instance in instances:
        launch_time = instance['LaunchTime']
        # Check if the instance was NOT launched today
        if (current_date - launch_time).days != 0:
            instance_id = instance['InstanceId']
            print(f"Instance {instance_id} was not created today. Deleting...")
            
            # Terminate the instance
            client.terminate_instance_in_auto_scaling_group(
                InstanceId=instance_id,
                ShouldDecrementDesiredCapacity=False
            )
        else:
            print(f"Instance {instance['InstanceId']} was created today.")

else:
    print(f"No Auto Scaling Group found with the name {asg_name}.")
