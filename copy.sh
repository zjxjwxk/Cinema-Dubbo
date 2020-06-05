#!/usr/bin/env bash

cp guns-alipay/target/guns-alipay-0.0.1.jar /Users/zjxjwxk/Desktop/Cinema-deploy/back/backend/film_backend/alipay/
cp guns-alipay/target/classes/{application.yml,zfbinfo.properties} /Users/zjxjwxk/Desktop/Cinema-deploy/back/backend/film_backend/alipay/

cp guns-cinema/target/guns-cinema-0.0.1.jar guns-cinema/target/classes/application.yml /Users/zjxjwxk/Desktop/Cinema-deploy/back/backend/film_backend/cinema/

cp guns-film/target/guns-film-0.0.1.jar guns-film/target/classes/application.yml /Users/zjxjwxk/Desktop/Cinema-deploy/back/backend/film_backend/film/

cp guns-gateway/target/guns-gateway-0.0.1.jar guns-gateway/target/classes/application.yml /Users/zjxjwxk/Desktop/Cinema-deploy/back/backend/film_backend/gateway/

cp guns-order/target/guns-order-0.0.1.jar guns-order/target/classes/application.yml /Users/zjxjwxk/Desktop/Cinema-deploy/back/backend/film_backend/order/

cp guns-user/target/guns-user-0.0.1.jar guns-user/target/classes/application.yml /Users/zjxjwxk/Desktop/Cinema-deploy/back/backend/film_backend/user/