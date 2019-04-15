pipeline {

	agent {
		label 'maven'
	}

	stages {
		stage('Log de variables') {
			steps {
				sh "echo URL GIT :: ${GIT_URL}"
				sh "echo Rama GIT :: $GIT_BRANCH"
			}
		}
		
		stage('Ejecución CI') {
			when {
              expression {
                return env.GIT_BRANCH == 'origin/develop'
           	  }
            }
			steps {
				sh "echo Ejecución CI"
			}
		}
		
		stage('Ejecución CD') {
			when {
              expression {
                return env.GIT_BRANCH == 'origin/master'
           	  }
            }
			steps {
				sh "echo Ejecución CD"
			}
		}
		
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

        stage('Promocionar a DES') {
          when {
            expression {
              return env.GIT_BRANCH == 'origin/develop'
           	}
          }

	      steps {
	        script {
	          openshift.withCluster() {
	            openshift.withProject('picasso-des') {
	              openshift.tag("picasso-cicd/greeting-service:latest", "greeting-service:des")
	            }
	          }
	        }
	      }
	    }

		stage('¿Promoción a PRE?') {
			when {
              expression {
                return env.GIT_BRANCH == 'origin/master'
           	  }
            }
			steps {
				input message: "¿Promocionamos a PRE?", ok: "Aceptar"
			}
		}

	    stage('Promocionar a PRE') {
	      when {
            expression {
              return env.GIT_BRANCH == 'origin/master'
         	}
          }
	      steps {
	        script {
	          openshift.withCluster() {
	            openshift.withProject('picasso-pre') {
	              openshift.tag("picasso-des/greeting-service:des", "greeting-service:pre")
	            }
	          }
	        }
	      }
	    }
	}
}