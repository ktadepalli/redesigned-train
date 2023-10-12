pipeline {
    agent {
        label 'my-custom-agent'
    }

    stages {
        stage('Invoke Ansible Job') {
            steps {
                script {
                   def ansi_jobUrl = 'https://your-ansible-server/api/v2/job_templates/your-job-template/run/'
                   def ansi_Creds = 'your-api-credentials-id'

                   def response = httpRequest(
                        url: ansi_jobUrl,
                        acceptType: 'APPLICATION_JSON',
                        contentType: 'APPLICATION_JSON',
                        httpMode: 'POST',
                        authentication: ansi_Creds
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
