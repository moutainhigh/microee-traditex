### 交易微服务 -- traditex parent
$ mvn archetype:generate -DgroupId=com.microee.traditex -DartifactId=microee-traditex -DarchetypeArtifactId=pom-root -DinteractiveMode=false -DarchetypeGroupId=org.codehaus.mojo.archetypes -DarchetypeVersion=RELEASE -DarchetypeCatalog=local

### 交易微服务 -- traditex-inbox 收件箱
mvn archetype:generate -DgroupId=com.microee.traditex.inbox -DartifactId=microee-traditex-inbox -DarchetypeArtifactId=pom-root -DinteractiveMode=false -DarchetypeGroupId=org.codehaus.mojo.archetypes -DarchetypeVersion=RELEASE -DarchetypeCatalog=local

### 交易微服务 -- traditex-inbox app 长连接管理, 封装第三方api
$ mvn archetype:generate -DgroupId=com.microee.traditex.inbox.app -DartifactId=microee-traditex-inbox-app -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local
### 交易微服务 -- traditex-inbox oem/领域模型
$ mvn archetype:generate -DgroupId=com.microee.traditex.inbox.oem -DartifactId=microee-traditex-inbox-oem -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local
### 交易微服务 -- traditex-inbox up/管理所有长链接
$ mvn archetype:generate -DgroupId=com.microee.traditex.inbox.up -DartifactId=microee-traditex-inbox-up -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local
### 交易微服务 -- traditex-inbox rmi/远程调用
$ mvn archetype:generate -DgroupId=com.microee.traditex.inbox.rmi -DartifactId=microee-traditex-inbox-rmi -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local
### 交易微服务 -- traditex-inbox mock/模拟第三方接口
$ mvn archetype:generate -DgroupId=com.microee.traditex.inbox.mock -DartifactId=microee-traditex-inbox-mock -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local

### 交易微服务 -- traditex-liqui 流动性摆盘
mvn archetype:generate -DgroupId=com.microee.traditex.liqui -DartifactId=microee-traditex-liqui -DarchetypeArtifactId=pom-root -DinteractiveMode=false -DarchetypeGroupId=org.codehaus.mojo.archetypes -DarchetypeVersion=RELEASE -DarchetypeCatalog=local
### 交易微服务 -- traditex-liqui app
$ mvn archetype:generate -DgroupId=com.microee.traditex.liqui.app -DartifactId=microee-traditex-liqui-app -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local
### 交易微服务 -- traditex-liqui oem/领域模型
$ mvn archetype:generate -DgroupId=com.microee.traditex.liqui.oem -DartifactId=microee-traditex-liqui-oem -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local
### 交易微服务 -- traditex-liqui rmi/远程调用
$ mvn archetype:generate -DgroupId=com.microee.traditex.liqui.rmi -DartifactId=microee-traditex-liqui-rmi -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local


### 交易微服务 -- traditex-liquid 流动性摆盘多账户，多币种
mvn archetype:generate -DgroupId=com.microee.traditex.liquid -DartifactId=microee-traditex-liquid -DarchetypeArtifactId=pom-root -DinteractiveMode=false -DarchetypeGroupId=org.codehaus.mojo.archetypes -DarchetypeVersion=RELEASE -DarchetypeCatalog=local
### 交易微服务 -- traditex-liquid app
$ mvn archetype:generate -DgroupId=com.microee.traditex.liquid.app -DartifactId=microee-traditex-liquid-app -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local
### 交易微服务 -- traditex-liquid oem/领域模型
$ mvn archetype:generate -DgroupId=com.microee.traditex.liquid.oem -DartifactId=microee-traditex-liquid-oem -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local
### 交易微服务 -- traditex-liquid rmi/远程调用
$ mvn archetype:generate -DgroupId=com.microee.traditex.liquid.rmi -DartifactId=microee-traditex-liquid-rmi -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local


### 交易微服务 -- traditex-archive 流动性摆盘数据归档
mvn archetype:generate -DgroupId=com.microee.traditex.archive -DartifactId=microee-traditex-archive -DarchetypeArtifactId=pom-root -DinteractiveMode=false -DarchetypeGroupId=org.codehaus.mojo.archetypes -DarchetypeVersion=RELEASE -DarchetypeCatalog=local
### 交易微服务 -- traditex-archive app
$ mvn archetype:generate -DgroupId=com.microee.traditex.archive.app -DartifactId=microee-traditex-archive-app -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local
### 交易微服务 -- traditex-archive oem/领域模型
$ mvn archetype:generate -DgroupId=com.microee.traditex.archive.oem -DartifactId=microee-traditex-archive-oem -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local


### 交易微服务 -- traditex-dashboard 流动性摆盘
mvn archetype:generate -DgroupId=com.microee.traditex.dashboard -DartifactId=microee-traditex-dashboard -DarchetypeArtifactId=pom-root -DinteractiveMode=false -DarchetypeGroupId=org.codehaus.mojo.archetypes -DarchetypeVersion=RELEASE -DarchetypeCatalog=local
### 交易微服务 -- traditex-dashboard app
$ mvn archetype:generate -DgroupId=com.microee.traditex.dashboard.app -DartifactId=microee-traditex-dashboard-app -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false -DarchetypeCatalog=local

      
      
      
      
      
      
      
      
      
   