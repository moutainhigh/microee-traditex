#!/bin/bash
#./non-hash.sh
#rsync -azP --exclude 'assets'  dist/* ../microee-shedlanding-app/src/main/resources/public/assets/
#rsync -azP  dist/assets/* ../microee-shedlanding-app/src/main/resources/public/assets/
rsync -azP  dist/* ../microee-traditex-dashboard-app/src/main/resources/public/assets/
# 更新时间戳
java -jar nohash.jr pubdate ../microee-traditex-dashboard-app/src/main/resources/application.properties

