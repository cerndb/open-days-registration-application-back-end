# Copyright (C) 2019, CERN
# This software is distributed under the terms of the GNU General Public
# Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
# In applying this license, CERN does not waive the privileges and immunities
# granted to it by virtue of its status as Intergovernmental Organization
# or submit itself to any jurisdiction.

# This docker file serves as an example. It was using a CentOS 7 image. In case you want to use this file for image build please point the following line to a CentOS 7 image. 

#################################
# action needed before use      # 
# please read the readme        #

FROM "$your_image"

USER root

ARG MAVEN_MAIN_VERSION_NUMBER="3"
ARG MAVEN_FULL_VERSION_NUMBER="3.6.3"
ARG DUMB_INIT_VERSION="1.2.2"
ARG DUMB_INIT_SHA256="37f2c1f0372a45554f1b89924fbb134fc24c3756efaedf11e07f599494e0eff9"
ARG OP_DATASOURCE_URL="$jdbc_connection_string"
ARG OP_DATASOURCE_USERNAME="$jdbc_username"
ARG OP_DATASOURCE_PW="$jdbc_password"
ARG OP_USER_VALIDATION_METHOD="2"
ARG OP_CAPTCHA_SERVER_KEY="$captcha_key"
ARG OP_JWT_TOKEN_SIGN_KEY="$token_sign_key"
ARG OP_JWT_TOKEN_EXPIRATION_TIME_IN_MINUTES="30"
ARG OP_SERVER_ENVIRONMENT="dev"
ARG OP_JPA_DDL_AUTO="none"
ARG OP_REGISTRATION_START_TIME="2019.06.08 00:00"
ARG OP_LOG_FILE_PATH="/var/log"
ARG OP_WALLET_CONNECTION="false"
ARG OP_WALLET_PATH="/opt/wallet"
#################################

ENV OP_DATASOURCE_URL=${OP_DATASOURCE_URL}
ENV OP_DATASOURCE_USERNAME=${OP_DATASOURCE_USERNAME}
ENV OP_DATASOURCE_PW=${OP_DATASOURCE_PW}
ENV OP_USER_VALIDATION_METHOD=${OP_USER_VALIDATION_METHOD}
ENV OP_CAPTCHA_SERVER_KEY=${OP_CAPTCHA_SERVER_KEY}
ENV OP_JWT_TOKEN_SIGN_KEY=${OP_JWT_TOKEN_SIGN_KEY}
ENV OP_JWT_TOKEN_EXPIRATION_TIME_IN_MINUTES=${OP_JWT_TOKEN_EXPIRATION_TIME_IN_MINUTES}
ENV OP_SERVER_ENVIRONMENT=${OP_SERVER_ENVIRONMENT}
ENV OP_JPA_DDL_AUTO=${OP_JPA_DDL_AUTO}
ENV OP_REGISTRATION_START_TIME=${OP_REGISTRATION_START_TIME}
ENV OP_LOG_FILE_PATH=${OP_LOG_FILE_PATH}
ENV OP_WALLET_CONNECTION=${OP_WALLET_CONNECTION}
ENV OP_WALLET_PATH=${OP_WALLET_PATH}

# install and update packages and
# remove orphaned packages to lower the size of the image
# install maven
RUN yum install -y unzip && \
  yum install -y exclude java-1.8.0-openjdk-devel wget gettext jq -q && \
  yum -y remove `package-cleanup --leaves` -q && \
  wget "http://www-us.apache.org/dist/maven/maven-${MAVEN_MAIN_VERSION_NUMBER}/${MAVEN_FULL_VERSION_NUMBER}/binaries/apache-maven-${MAVEN_FULL_VERSION_NUMBER}-bin.tar.gz" && \
  tar -xf apache-maven-${MAVEN_FULL_VERSION_NUMBER}-bin.tar.gz && \
  mv apache-maven-${MAVEN_FULL_VERSION_NUMBER}/ /usr/local/src/apache-maven && \
  rm -rf apache-maven-${MAVEN_FULL_VERSION_NUMBER}-bin.tar.gz && \
  curl -s -L -o /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v${DUMB_INIT_VERSION}/dumb-init_${DUMB_INIT_VERSION}_amd64 && \
  echo "$DUMB_INIT_SHA256  /usr/local/bin/dumb-init" | sha256sum -c - && \
  chmod +x /usr/local/bin/dumb-init && \
  chmod a+rwx /opt -R && \
  yum clean all -q && rm -rf /var/cache/yum/*

# copy resources into docker
COPY application /opt/application
COPY containerize/startup_scripts /opt/scripts
COPY containerize/extra-jars /opt/extra-jars
COPY containerize/db_wallet /home/nobody

# set startup scripts
RUN mv /opt/scripts/set_environment_variables.sh /etc/profile.d/set_environment_variables.sh && \
  chmod a+x /opt/scripts/start_application.sh && \
  find /etc/profile.d/ -type f -iname "*.sh" -exec chmod +x {} \;

# start bash session so startup script is going to be executed
SHELL ["/bin/bash", "-l", "-c"]

# build JAVA applications
RUN rm -rf /home/nobody/empty.txt && \
  rm -rf /opt/extra-jars/empty.txt && \
  mvn install:install-file -Dfile=/opt/extra-jars/ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=19.3.0.0.0 -Dpackaging=jar -q && \
  mvn install:install-file -Dfile=/opt/extra-jars/oraclepki.jar -DgroupId=com.oracle -DartifactId=oraclepki -Dversion=19.3.0.0.0 -Dpackaging=jar -q && \
  mvn install:install-file -Dfile=/opt/extra-jars/ucp.jar -DgroupId=com.oracle -DartifactId=ucp -Dversion=19.3.0.0.0 -Dpackaging=jar -q && \
  mvn install:install-file -Dfile=/opt/extra-jars/osdt_cert.jar -DgroupId=com.oracle -DartifactId=osdt_cert -Dversion=3.1.0 -Dpackaging=jar -q && \
  mvn install:install-file -Dfile=/opt/extra-jars/osdt_core.jar -DgroupId=com.oracle -DartifactId=osdt_core -Dversion=3.1.0 -Dpackaging=jar -q && \
  cd /opt/application && \
  chmod a+rw /var/log -R && \
  mvn clean compile test package -q

EXPOSE 8080

USER nobody

ENTRYPOINT ["/usr/local/bin/dumb-init","--"]

CMD ["/opt/scripts/start_application.sh"]
