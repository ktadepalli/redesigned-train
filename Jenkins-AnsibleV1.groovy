pipeline {
    agent any

    stages {
        stage('Retrieve Username and Password') {
            steps {
                script {
                    // Retrieve the credentials and store them in environment variables
                    withCredentials([usernamePassword(credentialsId: 'your-credentials-id', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        // Define the Ansible job URL
                        def ansibleJobURL = 'https://your-ansible-server/api/v2/job_templates/your-job-template/run/'

                        // Define the payload for the Ansible job
                        def ansibleJobPayload = [
                            extra_vars: [
                                key1: 'value1',
                                key2: 'value2',
                                // Add any extra variables your Ansible playbook needs
                            ],
                        ]

                        // Trigger the Ansible job using HTTP POST with the retrieved credentials
                        def response = httpRequest(
                            url: ansibleJobURL,
                            acceptType: 'APPLICATION_JSON',
                            contentType: 'APPLICATION_JSON',
                            httpMode: 'POST',
                            authentication: [username: USERNAME, password: PASSWORD],
                            requestBody: toJson(ansibleJobPayload)
                        )

                        if (response.status == 201) {
                            echo "Ansible job started successfully."
                        } else {
                            error "Failed to trigger Ansible job. Status code: ${response.status}"
                        }
                    }
                }
            }
        }
    }
}
