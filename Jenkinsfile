node {
   stage 'Checkout'
   checkout scm

   stage 'Build'
   sh "chmod +x gradlew"
   sh "./gradlew build publish --refresh-dependencies"
}