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
	            return !openshift.selector("bc", "greeting-service").exists();
	          }
	        }
	      }

	      steps {
	        script {
	          openshift.withCluster() {
	            openshift.newBuild("--name=greeting-service", "registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift", "--binary=true")
	          }
	        }
	      }
	    }

	    stage('Creación de la imagen') {
	      steps {
	        script {
	          openshift.withCluster() {
	            openshift.selector("bc", "greeting-service").startBuild("--from-file=target/greeting-rest-service.jar", "--wait")
	          }
	        }
	      }
	    }

        stage('Promote to DEV') {
	      steps {
	        script {
	          openshift.withCluster() {
	            openshift.tag("greeting-service:latest", "greeting-service:dev")
	          }
	        }
	      }
	    }

	    stage('Create DEV') {
	      when {
	        expression {
	          openshift.withCluster() {
	            return !openshift.selector('dc', 'greeting-service-dev').exists()
	          }
	        }
	      }
	      steps {
	        script {
	          openshift.withCluster() {
	            openshift.newApp("greeting-service-dev:latest", "--name=greeting-service-dev").narrow('svc').expose()
	          }
	        }
	      }
	    }
	}
}
