import requests

# Ansible Tower (AWX) API URL
TOWER_API_URL = "https://your-ansible-tower-url/api/v2"

# Ansible job template ID that you want to run
JOB_TEMPLATE_ID = "your-job-template-id"

# Username and password for authentication
USERNAME = "your-username"
PASSWORD = "your-password"

# Payload for the job run (extra variables, inventory, etc.)
payload = {
    "extra_vars": {
        "key1": "value1",
        "key2": "value2"
    },
    "inventory": "your-inventory"
}

# Create a session with authentication
session = requests.Session()
session.auth = (USERNAME, PASSWORD)

# Trigger the job using requests
response = session.post(
    f"{TOWER_API_URL}/job_templates/{JOB_TEMPLATE_ID}/launch/",
    json=payload
)

# You can add error handling and processing of the response as needed
if response.status_code == 201:
    print("Ansible job started successfully.")
else:
    print(f"Failed to trigger Ansible job. Status code: {response.status_code}")
