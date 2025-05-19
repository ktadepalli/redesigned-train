pipeline {
    agent any

    parameters {
        string(name: 'TAGS', defaultValue: 'v1.0.5,v1.0.6', description: 'Comma-separated list of tags')
    }

    environment {
        NEXUS_URL = "nexus.company.com"
        IMAGES = "web auth payment report notification analytics user admin api-gateway scheduler"

        // Define per-account registries and regions
        DEV_ACCOUNT_ID = "111111111111"
        SIT_ACCOUNT_ID = "222222222222"
        UAT_ACCOUNT_ID = "333333333333"
        PPD_ACCOUNT_ID = "444444444444"
        PROD_ACCOUNT_ID = "555555555555"

        DEV_REGION = "us-east-1"
        SIT_REGION = "us-west-2"
        UAT_REGION = "eu-west-1"
        PPD_REGION = "ap-southeast-1"
        PROD_REGION = "eu-central-1"
    }

    stages {
        stage('Prepare') {
            steps {
                script {
                    env.TAG_LIST = params.TAGS.tokenize(',')
                    env.IMAGE_LIST = IMAGES.tokenize()
                }
            }
        }

        stage('Pull from Nexus & Push to DEV ECR') {
            steps {
                script {
                    def registry = "${DEV_ACCOUNT_ID}.dkr.ecr.${DEV_REGION}.amazonaws.com"
                    withAWS(role: "arn:aws:iam::${DEV_ACCOUNT_ID}:role/JenkinsECRPushRole", region: "${DEV_REGION}") {
                        sh "aws ecr get-login-password --region ${DEV_REGION} | docker login --username AWS --password-stdin ${registry}"

                        for (tag in TAG_LIST) {
                            for (img in IMAGE_LIST) {
                                sh """
                                    docker pull ${NEXUS_URL}/${img}:${tag}
                                    docker tag ${NEXUS_URL}/${img}:${tag} ${registry}/${img}:${tag}
                                    docker push ${registry}/${img}:${tag}
                                """
                            }
                        }
                    }
                }
            }
        }

        stage('Promote to SIT') {
            steps {
                script {
                    promoteImages("SIT", DEV_ACCOUNT_ID, DEV_REGION, SIT_ACCOUNT_ID, SIT_REGION)
                }
            }
        }

        stage('Promote to UAT') {
            steps {
                script {
                    promoteImages("UAT", SIT_ACCOUNT_ID, SIT_REGION, UAT_ACCOUNT_ID, UAT_REGION)
                }
            }
        }

        stage('Promote to PPD') {
            steps {
                script {
                    promoteImages("PPD", UAT_ACCOUNT_ID, UAT_REGION, PPD_ACCOUNT_ID, PPD_REGION)
                }
            }
        }

        stage('Promote to PROD') {
            steps {
                script {
                    promoteImages("PROD", PPD_ACCOUNT_ID, PPD_REGION, PROD_ACCOUNT_ID, PROD_REGION)
                }
            }
        }
    }

    post {
        success {
            echo "✅ All images promoted to PROD successfully."
        }
        failure {
            echo "❌ Promotion failed. Check logs."
        }
    }
}

// --------- Shared Function (Groovy Script Block) -------------
def promoteImages(envName, sourceAccount, sourceRegion, targetAccount, targetRegion) {
    def sourceRegistry = "${sourceAccount}.dkr.ecr.${sourceRegion}.amazonaws.com"
    def targetRegistry = "${targetAccount}.dkr.ecr.${targetRegion}.amazonaws.com"

    withAWS(role: "arn:aws:iam::${targetAccount}:role/JenkinsECRPushRole", region: "${targetRegion}") {
        sh "aws ecr get-login-password --region ${targetRegion} | docker login --username AWS --password-stdin ${targetRegistry}"

        for (tag in TAG_LIST) {
            for (img in IMAGE_LIST) {
                sh """
                    docker pull ${sourceRegistry}/${img}:${tag}
                    docker tag ${sourceRegistry}/${img}:${tag} ${targetRegistry}/${img}:${tag}
                    docker push ${targetRegistry}/${img}:${tag}
                """
            }
        }
    }
}
