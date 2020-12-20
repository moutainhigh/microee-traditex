
target:
	mkdir target  2>/dev/null


install-inbox:
	mvn -f microee-traditex-inbox/microee-traditex-inbox-oem/pom.xml clean install
	mvn -f microee-traditex-inbox/microee-traditex-inbox-up/pom.xml clean install
	mvn -f microee-traditex-inbox/microee-traditex-inbox-rmi/pom.xml clean install

install-liqui:
	mvn -f microee-traditex-liqui/microee-traditex-liqui-oem/pom.xml clean install
	mvn -f microee-traditex-liqui/microee-traditex-liqui-rmi/pom.xml clean install

install-liquid:
	mvn -f microee-traditex-liquid/microee-traditex-liquid-oem/pom.xml clean install
	mvn -f microee-traditex-liquid/microee-traditex-liquid-rmi/pom.xml clean install

install-archive:
	mvn -f microee-traditex-archive/microee-traditex-archive-oem/pom.xml clean install
	mvn -f microee-traditex-archive/microee-traditex-archive-app/pom.xml clean install
	
package-traditex-inbox-dist: target
	mvn -f microee-traditex-inbox/microee-traditex-inbox-app/pom.xml clean package
	cp microee-traditex-inbox/microee-traditex-inbox-app/target/microee-traditex-inbox-app-1.0-SNAPSHOT.jar target/

package-traditex-liqui-dist: target
	mvn -f microee-traditex-liqui/microee-traditex-liqui-app/pom.xml clean package
	cp microee-traditex-liqui/microee-traditex-liqui-app/target/microee-traditex-liqui-app-1.0-SNAPSHOT.jar target/

package-traditex-liquid-dist: target
	mvn -f microee-traditex-liquid/microee-traditex-liquid-app/pom.xml clean package
	cp microee-traditex-liquid/microee-traditex-liquid-app/target/microee-traditex-liquid-app-1.0-SNAPSHOT.jar target/

package-traditex-dashboard-dist: target
	mvn -f microee-traditex-dashboard/microee-traditex-dashboard-app/pom.xml clean package
	cp microee-traditex-dashboard/microee-traditex-dashboard-app/target/microee-traditex-dashboard-app-1.0-SNAPSHOT.jar target/
	

build: install-inbox install-liqui install-liquid install-archive package-traditex-inbox-dist package-traditex-liqui-dist package-traditex-liquid-dist package-traditex-dashboard-dist
	echo 'build successful'




