cd src/main/git-app

echo "======================================================================="
echo "Installing Node modules..."
echo "======================================================================="


npm install

echo "======================================================================="
echo "Building frontend for tracker app..."
echo "======================================================================="

ng build

echo "======================================================================="
echo "Frontend build complete..."
echo "======================================================================="

cd ../../..


mvn clean install package -Dmaven.test.skip=false

echo "======================================================================="
echo "Deploying the TRACKER project..."
echo "======================================================================="
pkill -9 -f tomcat
java -jar target/tracker-0.0.1-SNAPSHOT.jar
