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

	    stage('Creación de la imagen única') {
	      steps {
	        script {
	          openshift.withCluster() {
	            openshift.selector("bc", "greeting-service").startBuild("--from-file=target/greeting-rest-service.jar", "--wait")
	          }
	        }
	      }
	    }

        stage('Promocionar a DEV') {
	      steps {
	        script {
	          openshift.withCluster() {
	            openshift.tag("greeting-service:latest", "greeting-service:dev")
	          }
	        }
	      }
	    }

	    stage('Crear entorno DEV') {
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
	            openshift.newApp("greeting-service:dev", "--name=greeting-service-dev").narrow('svc').expose()
	          }
	        }
	      }
	    }
	}
}
