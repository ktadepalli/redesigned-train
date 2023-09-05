#============== get files
import boto3

# Replace 'your_access_key' and 'your_secret_key' with your AWS access key and secret key
# You can also use environment variables or IAM roles for credentials.
aws_access_key_id = 'your_access_key'
aws_secret_access_key = 'your_secret_key'
bucket_name = 'your_bucket_name'
prefix = '/myfiles'  # Specify the key prefix for the folder you want to list

# Create an S3 client
s3 = boto3.client('s3', aws_access_key_id=aws_access_key_id, aws_secret_access_key=aws_secret_access_key)

# List objects (files) in the specified folder within the bucket
try:
    response = s3.list_objects_v2(Bucket=bucket_name, Prefix=prefix)

    # Check if objects were found in the folder
    if 'Contents' in response:
        print(f"Files in the folder '{prefix}' within the bucket:")
        for obj in response['Contents']:
            print(obj['Key'])
    else:
        print(f"No files found in the folder '{prefix}' within the bucket.")
except Exception as e:
    print(f"Error: {e}")

#====================================check files=

import boto3

# Replace 'your_access_key' and 'your_secret_key' with your AWS access key and secret key
# You can also use environment variables or IAM roles for credentials.
aws_access_key_id = 'your_access_key'
aws_secret_access_key = 'your_secret_key'
bucket_name = 'your_bucket_name'
prefix = '/myfiles/'  # Specify the key prefix for the folder you want to check

# List of files you want to check for existence
files_to_check = ['file1.txt', 'file2.txt', 'file3.txt']

# Create an S3 client
s3 = boto3.client('s3', aws_access_key_id=aws_access_key_id, aws_secret_access_key=aws_secret_access_key)

# Check if the specified files exist in the specified folder within the bucket
try:
    for file_name in files_to_check:
        object_key = prefix + file_name
        response = s3.list_objects_v2(Bucket=bucket_name, Prefix=object_key)

        # Check if the object was found
        if 'Contents' in response:
            print(f"File '{file_name}' exists in the folder '{prefix}' within the bucket.")
        else:
            print(f"File '{file_name}' does not exist in the folder '{prefix}' within the bucket.")
except Exception as e:
    print(f"Error: {e}")


