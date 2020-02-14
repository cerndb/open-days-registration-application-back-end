# General description

The repostiory contains an MVC based [Spring Boot](https://spring.io/projects/spring-boot) REST API application using JAVA programming language and scripts for building a portable [Docker](https://www.docker.com/) container, which runs the application.

You can find more information about the ticket reservation system in this [article](https://db-blog.web.cern.ch/blog/viktor-kozlovszky/2019-10-open-days-reservation-systems-high-level-overview-2019).

The content is orginized into different folders. You will find the description for each folder in this ReadMe.

Overview about the folder structure:
* **application**: The ticket reservation application's back-end (business logic) implementation. The application receives the API calls from the [front-end server](https://github.com/cerndb/open-days-registration-application-front-end), retrieves or applies modification on the dataset stored in a database.  Please note that the front-end server has the user interface implementation. It creates http API calls for the back-end server. For more detailed information check out the [ticket reservation application section](https://github.com/cerndb/open-days-registration-application-front-end#Ticket-reservation-application). For making the application running you will need a database server. The current repository contains the packages connecting to an Oracle database.
* **containerize**: This folder contains the helper files to build a container which hosts the two applications. For more detailed information check out the container approach section.

Note that the application was using the JDK 1.8 and for building we used Maven 3.6.3. Be aware that currently there is no plan to maintain the code.

The ticket reservation application back-end is split into 2 parts the business logic layer and data persistence layer. The business logic layer is for the code which ensures the data manipluation through API calls. The data persistence layer is for storing the information. During our development process we relied on Spring Boot's [ORM](https://en.wikipedia.org/wiki/Object-relational_mapping) (Object relational mapping) support, which allows to create the database structure from the business logic layer.
Due to this possibility you will find here two individual chapters. One describing the database layer and one describing the application layer.

# Collaborators

The final application is the collaborated work of the following people:
* [Luis Rodríguez Fernández](https://github.com/lurodrig)
* [Viktor Kozlovszky](https://github.com/kviktorman/)

# Container approach

**Environment variables**

In the docker file you need to set the following variables to their default values. Note that if you use Kubernetes for hosting the container you can override the default values in the deployment templates.

| Variable name | Description | specific values |
| --- | --- | --- |
| OP_JPA_DDL_AUTO | hibernate auto ddl (without quotes) | "create-drop" to recreate the table strucutre, "none" for production |
| OP_DATASOURCE_URL | jdbc connection string (without quotes). For more information check out the regular or wallet database section below |  |
| OP_DATASOURCE_USERNAME | jdbc connection user (without quotes). For more information check out the regular or wallet database section below |  |
| OP_DATASOURCE_PW | jdbc connection pw (without quotes). For more information check out the regular or wallet database section below |  |
| OP_SERVER_ENVIRONMENT | Environment mode "prod" has by default disabled Swagger and PasscodeController (without quotes) | "dev","qa","prod" |
| OP_USER_VALIDATION_METHOD | user is not a robot checking (just number) | 0 - always true, 1- google captcha + honeypot , 2-honeypot |
| OP_CAPTCHA_SERVER_KEY | Google captcha server key string if enabled (without quotes) |  |
| OP_JWT_TOKEN_SIGN_KEY | signing key for jwt token (without quotes) |  |
| OP_JWT_TOKEN_EXPIRATION_TIME_IN_MINUTES | JWT token expiration time (just number) |  |
| OP_REGISTRATION_START_TIME | timestamp of the start date without seconds | 2019.06.08 00:00 |
| OP_LOG_FILE_PATH | Logfile path | /var/log |
| OP_CORS_ALLOWED_ORIGINS | Comma separated list of domains that can query the backend API | "https://opendays-registration.cern.ch" for production. By default the variable is set in the application.properties to http://localhost:4200,https://opendays-registration-demo.cern.ch|
| OP_CORS_PATH_PATTERN | Allowed origin URL path. This path will be the same for all dev, test, prod profiles | "/**" In application.properties this value is already set so this environment variable is OPTIONAL|
| OP_WALLET_CONNECTION | If the connection between the application and the database is via wallet. For more information please read the wallet database connection below | true,false  |

For reCAPTCHA the solution supports the google reCAPTCHA v2. In order to use the reCAPTCHA you will need your own recaptcha site configuration and include the server key in the ./Dockerfile or in the ./application/main/resources/application.properties

**regular database connection**

Note that you need to download the Oracle database connection sepcific jars (listed under running natively) to the containerize/extra-jars folder. In case you need other jar(s) for database connection you have to include that in the Dockerfile.

If you want to go with the regular connection you need the following settings at build time:
* update the following connection string according to your database settings
"jdbc:oracle:thin:@//service:port/server_name" (service name is the name of the service,port the database connection port, server name is the server name where the database is located.) and set the "OP_DATASOURCE_URL" to that
* set the database credentials for "OP_DATASOURCE_USERNAME" and "OP_DATASOURCE_PW"
* set the "OP_WALLET_CONNECTION" to "false"

**wallet database connection**

Note that the containerize/extra-jars folder is the place where you need to put the Oracle specific connection jars (listed under running natively). In case you need other jar(s) for database connection you have to include that in the Dockerfile.

Some databases for example the Oracle Autonomous database requires a database wallet for the connection. The wallet is a zip file containing the configurations for SSL connection. 

If you want to use wallet you need the following settings at build time:
* set the "OP_WALLET_CONNECTION" variable to true and during the start 
* set the "OP_WALLET_PATH" where you want to have the settings extracted
* copy the wallet under the containerize/db_wallet folder with the following name: Wallet_DB.zip
* update the following connection string according to your database settings
"jdbc:oracle:thin:@opendays_high?TNS_ADMIN=/opt/wallet" (opendays_high is the database service which we connect, TNS_ADMIN is the location where we extract the wallet.) and set the "OP_DATASOURCE_URL" to that
* set the database credentials for "OP_DATASOURCE_USERNAME" and "OP_DATASOURCE_PW"

**Containerize folder**

This folder contains helper files for the image build.
* **extra-jars**: The jar files folder for creating the connection to an Oracle Autonomous database (download required jars listed under running natively)
* **startup_scripts**: The startup scripts for application building and hosting.

# How to use the container

To run the image you need to have [Docker installed](https://docs.docker.com/install/).

Once your environemnt is ready you update the settings in the ./Dockerfile. 
Note that you need to use a RedHat or CentOS image. You can use for example the docker hub image : "centos:centos7"

From the root folder, where the Dockerfile is run the following command.

```
>docker build -t reservation-system-back-end -f ./Dockerfile .
```
Note that the maven minor version number can change. Please check the the available [version number](https://downloads.apache.org/maven/maven-3/) before the build and update it in the Dockerfile.
For more information check the [Maven build section](#Maven_build)

To run the image you need to forward the port 

```
>docker run -it -p 8080:8080 --rm --name reservation-back-end reservation-system-back-end
```

You access your image via http://localhost:8080

# Maven build

Maven 3 is used for compiling the application code. During the docker build process we download maven into the image, download and install the additional dependency jars listed in the running natively. For the application building we used maven 3.6.3, the minor version can change by time so please check and update the version number accordingly what is available [here](https://downloads.apache.org/maven/maven-3/). 

# Running natively

You need to install JAVA 1.8 and Maven 3.6.3 to build, for running the application JAVA 1.8 is required. The solution works perfectly with open JDK 1.8.

For connectiong to an Oracle 19 database you will need extra jar-s. Before building the application you need to install the packages to your local maven repository. 
The list of jars which you need to download from the [official website](https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html):
* ojdbc8.jar
* oraclepki.jar
* osdt_cert.jar
* osdt_core.jar
* ucp.jar


For installing the dependencies from the /containerize/extra-jars folder to local maven repository execute the following commands.
Note that the package names can be the same for the different database versions, use the packages according to your database version.
```
>mvn install:install-file -Dfile=/containerize/extra-jars/ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=19.3.0.0.0 -Dpackaging=jar -q
>mvn install:install-file -Dfile=/containerize/extra-jars/oraclepki.jar -DgroupId=com.oracle -DartifactId=oraclepki -Dversion=19.3.0.0.0 -Dpackaging=jar -q 
>mvn install:install-file -Dfile=/containerize/extra-jars/ucp.jar -DgroupId=com.oracle -DartifactId=ucp -Dversion=19.3.0.0.0 -Dpackaging=jar -q 
>mvn install:install-file -Dfile=/containerize/extra-jars/osdt_cert.jar -DgroupId=com.oracle -DartifactId=osdt_cert -Dversion=3.1.0 -Dpackaging=jar -q 
>mvn install:install-file -Dfile=/containerize/extra-jars/osdt_core.jar -DgroupId=com.oracle -DartifactId=osdt_core -Dversion=3.1.0 -Dpackaging=jar -q 
```

For building the application run the following command:
```
>mvn clean compile test package -q
```

Update the application/src/main/resources/application.properties files with the correct values. Example values described in the container approach environment table.

For running the application run the following command from the generated target folder:
```
>java -jar /opt/application/target/registration-application.jar
```

# Ticket reservation application (business logic layer)

## SWAGGER config

Swagger configuration is based on activated environment profile. For "prod" we don't enable swagger. For "dev" and "qa" profile we enable it.

If you launch the application on your local machine with port 8080 then swagger will be available is available : http://localhost:8080/swagger-ui.html

## Bi-linguality

The application has bi-lingual implementation. The main supported languages are English and French. The language selection is made on the front-end side and the language preference is included in every message (message header element : "Accept-Language" , the values are : "fr" and "en" )

For simplicity reasons the front-end list elements (comboboxes, radio list) translation is coming from the database. For example transport types, point of origins, etc. For these cases we read the the selection list into the application first and based on the language preference we return the right display value in the message.

## Message handling

The application REST APIs are returning JSON messages. Each message has the following sturcture:
```
{
   "statusCode" - number, if 0 then execution was Ok, if negative then during execution there were errors
   "data" - optional, in case of 0 status code this is provided and it will be an object or an array
   "errorMessage" - optional, in case of negative status code this is provided
}
```

**error and exception handling**

During the reservation process there are possibilities when a user wanted to apply such modifications which were not part of the workflow or wrongly provided data ,etc.
To handle these cases each API functionality has its own error and exception handling. When a workflow task goes to error the system sends back 200 OK message, but the statusCode of the returned JSON will be negative.

## JWT token

As you will see in the list of the available API's, most of the API services were protected by JSON Web Token ([JWT](https://jwt.io/)). The application has a stateless design and the whole reservation process was split into smaller steps. Each step has it's own implementation, we will reference the steps as data related tasks. In order to execute these tasks parallel and keep them syncronised with the already stored data we used the previously mentioned tokens. As you see in the next section the majority of the APIs was protected by the token. The protection means in this context that in the http message request header the token has to be present otherwise the back-end will refuse the execution.

## Available APIs 

| Token protected | Front-end page | API name | description | returns |
| --- | --- | --- | --- | --- |
| N | Passcode page | request-passcode | invalidates previous passcodes and creates a new passcode for login | Ok |
| N | Login page | request-access-token | sends the passcode + mail to get a login token | JWT token |
| Y | Dashboard page | get-arrival-point-dates | request available dates | list of available dates for the logged in user |
| Y | Dashboard page | get-active-reservations | request final status reservations | array of reservation in final status for the logged in user |
| Y | Dashboard page | cancel-reservation | cancel specific reservation | Ok |
| Y | Dashboard page | create-new-reservation | get JWT token containing the reservation identifier | reservation JWT token |
| Y | Confirm arrival point and timeslot page | get-arrival-point-dates | request available dates (not having reservation) | selectable dates |
| Y | Confirm arrival point and timeslot page | request-arrival-point-timeslots | based on date request available timeslots | selectable timeslots |
| Y | Confirm arrival point and timeslot page | confirm-arrival-point | confirm user selection for arrival point and timeslot | Ok |
| Y | Confirm visitor details page | get-confirmed-visitor-details | load user provided visitor data | stored visitor data list |
| Y | Confirm visitor details page | store-visitors | user provided visitor data | Ok |
| Y | Confirm transport types page | get-transport-types-selection | load stored transport types | selected transport type |
| Y | Confirm transport types page | store-transport-types | store user transport type information | Ok |
| Y | Confirm transport types page | get-point-of-origin | point of origin + selection flag | list of origin points |
| Y | Summary page | get-reservation | load in progress reservation details | in progress reservation details |
| Y | Summary page | finalize-reservation | make reservation final | Ok |
| N | Welcome page | get-daily-available-places | get places without login  | date + yes and no if there are at least 6 places available |
| Y | Update arrival point page | update-arrival-point | store arrival point changes | Ok |
| Y | Update arrival point page | get-reservation-arrival-data | get reservation arrival point data + available timeslots | timeslot change possibilities |
| Y | Update transport types page | get-point-of-origin | point of origin + selection flag | list of origin points | 
| Y | Update arrival point page | update-transport-types | store user transport type changes | Ok |
| Y | Dashboard page | resend-reservation-confirmation | send again confirmation mail  | Ok |
| Y | Dashboard page | get-update-reservation-token | send again  | JWT token with required reservation id |
| Y | Visitrs data update page | get-visitor-details-for-update | load visitors data for update | visitors data in array |
| Y | Visitrs data update page | update-visitors | store modified visitors data | Ok |
| N | - | event-registration | test api for post message test | test values |
| N | - | request-passcode-for-testing | request passcode for automated testing. Works only for "dev" and "qa" environment | passcode |

## Workflows

This application is designed to work as individual API calls, which means they are not relying on other API calls. The [front-end user interface](https://github.com/cerndb/open-days-registration-application-front-end) creates an environment and ensures calling the APIs in order. As a result of the user interface walkthrough a reservation can be created, updated and cancelled.

### Login workflow

Using the front-end user interface you request a passcode and with that passcode you login. The front-end will receive a token generated by the back-end and to each call the back-end server will receive this token. If the token is expired or incorrect the back-end server will send back an error message. 

### Reservation workflow

Starting the reservation workflow first the back-end server will create a new token which includes an identifier for the reservation. The front-end will provide this token to each upcoming API calls. If the token is expired or incorrect the back-end server will send back an error message. Otherwise it will try to apply the requested modifications in the database. After the front-end has the token the next step in the workflow comes, which is select the arrival date, point and timeslot. 
After providing these information the system books 6 places by default for 30 min. As a next step the user has to provide the amount of visitors and their age at the time of the visit. This data is going to be stored into a visitor details table. Then as the next step the user selects the point of origin and transport type. As a final step the data is retrived from the database. When the user finalizes the reservation the system releases unused tickets from the reservation and sets the status of the reservation to active. The front-end allows back and forth navigation between these steps. This means each step will first retrieve the already stored data (if not exists then no data returned) for the in progress reservations. 

### Cancellation and update workflows

The user interface uses very similar interfaces for the reservation update possibilities. The update screens are not organised as multi step workflows, the user can change only one type of information at the time. After the modification the navigation goes back to the dashboard screen. For each update step a token and the id of the reservation is provided. The update API calls will make directly the changes on the reservation if it's possible.

The cancellation works very similar way as the update workflows. Here on the user interface there is no navigation to any other screen. There is just a pop-up window for confirmation. By confirming the cancellation a token and the id of the reservation is provided for the API call, as a result of this workflow the active reservation's status will marked as cancelled.

# Ticket reservation application (data persistence layer)

If you want to create the database structure from the code you need to set the "OP_JPA_DDL_AUTO" to "create-drop" and start the application. During the start the application will create the tables and create the database strucutre. For the creation to happen you need to have an empty database. Note that after you have created the database stucture you need to change the "OP_JPA_DDL_AUTO" to "none", otherwise the system will drop and recreate the database everytime when the back-end application starts. 

Below you will find the database tables with their column list and description.

## ACTION_HISTORY table

| PK | Column name | Description |
| --- | --- | --- |
| Y | ID_ACTION | the action which has been done |
| Y | ID_MODIFIER | user's person id or system (zeros) |
| Y | ID_CHANGE_OBJECT | the object identifier (if it has one unique column for identificatio ) else it will be the registration id (which will link to the person) |
| Y | CHANGE_DATE | date of the change |
| Y | OBJECT_TYPE | referring to the tables where the rows have been changed |
|  | CHANGE_COMMENT | Some description to understand what has happened  |

**==ID_CHANGE_OBJECT combinations==**
* confirm email address passcode: The registration id from the registration table (not the person id)
* show interest workflow: The id of the person who shows the interest

## PASSCODE table

| PK | Column name | Description |
| --- | --- | --- |
| Y | ID_REGISTRATION | this links who has requested the code |
| Y | TIMESTAMP_OF_CREATION | this shows when was it requested |
| Y | ID_OPERATION | for what action was it requested |
|  | PASSCODE | the passcode which needs to be checked |
|  | PASSCODE_STATUS | shows if the passcode is active or not |
|  | TIMESTAMP_OF_USAGE | when was it used |

One user can have only one passcode active for one operation. We don't delete the old passcode's (for tracking reason).

## REGISTRATION table

| PK | Column name | Description |
| --- | --- | --- |
| Y | ID_REGISTRATION | the id of the registration, this is a sequence from 1 to 1.000.000 (the expected max number is around 100.000). The database will create it as we create a new record into this table  |
|  | CONTACT_EMAIL | that's the mail address which identifies the user as he logs in + the passcode from the passcode table |
|  | REGISTRATION_PROCESS_STATUS_CODE | the user actual status in the workflow |
|  | LAST_UPDATE_DATE | last modification date |
|  | ID_PERSON | generated GUID for the registered person. This is what we use for action history tracking. It will be useful for creating the reports, because we are not going to relay on any personal sensitive information |
|  | NAME_FIRST | registered persons first name for e-mail |
|  | NAME_FAMILY | registered persons family name for e-mail |
|  | BARCODE | the code which will identify at registration |
|  | REGISTERED_ADULTS | SUM value for the registered adults. Since we need to store the ages for them the detailed data will live in a different table. This will allow us to make the query for the available free space calculation more easier and faster |
|  | REGISTERED_CHILDS | SUM value for the registered childs. Since we need to store the ages for them the detailed data will live in a different table. This will allow us to make the query for the available free space calculation more easier and faster |

Indexes:
* CONTACT_EMAIL => REGISTRATION_MAIL_ADDRESS: In order to make a fast query during login
* REGISTRATION_PROCESS_STATUS_CODE,LAST_UPDATE_DATE => REGISTRATION_STATUS_DATE: Query for available places

## MAIL_ACTION_HISTORY table

| PK | Column name | Description |
| --- | --- | --- |
| Y | ID_REGISTRATION | the id of the registration (FK to REGISTRATION table) |
| Y | ID_MAIL | unique id just to be here for composit key, does not have any reference else where |
| Y | TIMESTAMP_OF_CREATION | the timestamp of insert |
|  | SENDING_MAIL_IS_REQUIRED | flag 1 = Yes, 0 = No |
|  | ACTION_TYPE | List : 1 = send passcode for login, 2 = send barcode confirmation mail, 3 = unregister from event, 4 = update registration |
|  | ID_RESERVATION | In case of the action type is send barcode this value will contain the id of reservation |
|  | TIMESTAMP_OF_SEND | Timestamp of the send date |

## ARRIVAL_POINT_TRANSPORT_RATE table

| PK | Column name | Description |
| --- | --- | --- |
| Y | ID_ARRIVAL_POINT | id of the arrival |
| Y | ID_TRANSPORT_TYPE | id of the transport which you took |
|  | TRANSPORT_RATE | number , how good is the arrival point for that transport |

## TRANSPORT_TYPE table

| PK | Column name | Description |
| --- | --- | --- |
| Y | ID_TRANSPORT_TYPE | id of the transport which you took |
|  | TRANSPORT_NAME | name of the transport type |

## ARRIVAL_POINT_OPENING_HOUR table

| PK | Column name | Description |
| --- | --- | --- |
| Y | OPEN_DAY | the day which the endpoint is functioning |
| Y | ID_ARRIVAL_POINT | arrival point id |
| Y | TIMESLOT_START | when the opening timeslot begins for the arrival point |
|  | OPENING_DURRATION_IN_MINUTES | how long will be the arrival point open |
|  | TOTAL_AVAILABLE_PLACES | total available places which the arrival point can handle |
|  | FAST_TRACK_PERCENTAGE | total fast track percentage, how much of the available places is fast track for example 80 means 80% |
|  | CAPACITY_LIMITATION_PERCENTAGE | how much of the total available places is blocked |
|  | PRIVILEGED_OPENING | flag if arrival point is open for privileged visitors |

## ARRIVAL_POINT table

| PK | Column name | Description |
| --- | --- | --- |
| Y | ID_ARRIVAL_POINT | arrival point id |
|  | POINT_NAME | arrival point display name |
|  | POINT_DESCRIPTION | arrival point description |
|  | POINT_MAP_URL | map image location (probably we have to add manually, it will not come from the source data) |
|  | SITE_ACTIVITIES_INFO_URL | activity url where we have all the activities with detailed description |
|  | SITE_ACCESSIBILITY_INFO_URL | arrival information url where we have detailed information about the arrival possibilities |
|  | NUMBER_OF_SURFACE_ACTIVITIES | how many surface activities are for the people at that arrival point |
|  | NUMBER_OF_UNDERGROUND_ACTIVITIES | how many underground activities are for the people at that arrival point |

## RESERVATION table

| PK | Column name | Description |
| --- | --- | --- |
| Y | ID_RESERVATION | id of the reservation |
| Y | ID_REGISTRATION | id of the registration |
|  | VISIT_DAY | the day of the visit |
|  | ID_ARRIVAL_POINT | id of the arrival point |
|  | TIMESLOT_START | the timeslot when the visitor(s) are going to arrive |
|  | NUMBER_OF_RESERVED_TICKETS | the total number of the booked tickets |
|  | NUMBER_OF_ADULT_TICKETS | how many of the booked tickets are for adults |
|  | NUMBER_OF_CHILD_TICKETS | how many of the booked tickets are for children |
|  | NUMBER_OF_FAST_TRACK_TICKETS | how many of the booked tickets are for fast track |
|  | RESERVATION_STATUS | what is the status of the reservation (inprogress, cancelled, etc.) |
|  | BARCODE | barcode for the tickets |
|  | CHANGE_DATE | the date of the reservation change |

## VISITOR_ARRIVAL_DETAIL table

| PK | Column name | Description |
| --- | --- | --- |
| Y | ID_RESERVATION | id of the reservation |
| Y | ID_TRANSPORT_TYPE | id of the transport type |
|  | NUMBER_OF_VEHICLES | how many vehicles are you arriving with |

## VISITOR_DETAIL table 

| PK | Column name | Description |
| --- | --- | --- |
| Y | ID_VISITOR | id of the visitor |
| Y | ID_RESERVATION | id of the reservation |
|  | REQUESTED_FAST_TRACK | visitor has fast track |
|  | AGE | visitor age by the time of the visit |

## PRIVILEGED_VISIT table

| PK | Column name | Description |
| --- | --- | --- |
| Y | ID_PRIVILEGE | id privilege, we not going to use this just gives for join, just for PK |
|  | PRIVILEGE_VISITOR_IDENTIFIER | individual mail address or mail domain |
|  | PRIVILEGE_DAY | day of privilege |
|  | PRIVILEGE_TYPE_CODE | number shows 1-domain, 2- individual |
