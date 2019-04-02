pipeline {

	agent {
		label 'maven'
	}

	stages {
		stage('Construcción del JAR') {
			steps {
				sh "mvn install -DskipTests=true"
			}
		}

		stage('Creación del Image Builder') {
	      when {
	        expression {
	          openshift.withCluster() {
	            return !openshift.selector("bc", "greetingService").exists();
	          }
	        }
	      }

	      steps {
	        script {
	          openshift.withCluster() {
	            openshift.newBuild("--name=greetingService", "registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift", "--binary=true")
	          }
	        }
	      }
	    }

	    stage('Creación de la imagen') {
	      steps {
	        script {
	          openshift.withCluster() {
	            openshift.selector("bc", "greetingService").startBuild("--from-file=target/greeting-rest-service.jar", "--wait")
	          }
	        }
	      }
	    }

            stage('Despliegue en desarrollo') {
              steps {
                script {
                  openshift.withCluster() {
                    openshift.withProject(env.DEV_PROJECT) {
                      openshift.selector("dc", "greetingService").rollout().latest();
                    }
                  }
                }
              }
            }
	}
}
