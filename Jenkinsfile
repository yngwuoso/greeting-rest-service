pipeline {
  agent {
    label 'maven'
  }

  environment {
    IMAGE = readMavenPom().getArtifactId()
    VERSION = readMavenPom().getVersion()
  }

  stages {
    stage('CI Pipeline') {
      when {
        expression {
          return env.GIT_BRANCH == 'origin/develop'
        }
      }
      steps {
        sh "echo Ejecución CI :: $IMAGE-$VERSION"
      }
    }
		
    stage('CD Pipeline') {
      when {
        expression {
          return env.GIT_BRANCH == 'origin/master'
        }
      }
      steps {
        sh "echo Ejecución CD :: $IMAGE-$VERSION"
      }
    }
		
    stage('Build stage') {
      steps {
        sh "mvn install -DskipTests=true"
      }
    }

    stage('IB creation') {
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

    stage('Image creation') {
      steps {
        script {
          openshift.withCluster() {
            openshift.selector("bc", "greeting-service").startBuild("--from-file=target/greeting-rest-service.jar", "--wait")
          }
        }
      }
    }

    stage('Promote to DES') {
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

    stage('Promote to PRE?') {
      when {
        expression {
          return env.GIT_BRANCH == 'origin/master'
        }
      }
      steps {
        input message: "¿Promocionamos a PRE?", ok: "Aceptar"
      }
    }

    stage('Promote to PRE') {
      when {
        expression {
          return env.GIT_BRANCH == 'origin/master'
        }
      }
      steps {
        script {
          openshift.withCluster() {
            openshift.withProject('picasso-pre') {
              openshift.tag("picasso-des/greeting-service:des", "greeting-service:$VERSION")
              openshift.tag("greeting-service:$VERSION", "greeting-service:pre")
            }
          }
        }
      }
    }

    stage('Promote to PRO?') {
      when {
        expression {
          return env.GIT_BRANCH == 'origin/master'
        }
      }
      steps {
        input message: "¿Promocionamos a PRO?", ok: "Aceptar"
      }
    }

    stage('Promote to PRO') {
      when {
        expression {
          return env.GIT_BRANCH == 'origin/master'
        }
      }
      steps {
        script {
          openshift.withCluster() {
            openshift.withProject('picasso-pro') {
              openshift.tag("picasso-pre/greeting-service:$VERSION", "greeting-service:pro")
            }
          }
        }
      }
    }
  }
}
