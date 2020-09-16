<p align="center">
  <br>
  <img src="https://i.ibb.co/StNR7TK/a2cce7bf-9e37-4bfe-ad73-12e797078639-200x200.png" alt="EMS logo">
</p>

# Example Message Service Project on Java
> ![](https://img.shields.io/badge/-new%20opportunity-yellowgreen?style=for-the-badge&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAASCAQAAAD8x0bcAAAAxUlEQVR4AZXRT44BQQDG0bcTa93cYQYRf25CHAWHEGIpIo4yZxnNFdAWNbWYdDrUxvttv+qqpH0uN7eKzWWSOk6ewn+lo7YXPVc3a0ON2MjG3UVXTcfVr++XY0UsVzm5VZNtDOh7OADknta8jdgqZcBcMJQyFsyAlaCR/FJTsEyNfmL10UJ13Sg5mgimQKa0SY52Si2Ao7ve25sGHvYqbReFvrqBi7NMTVfhbmusGZvYeTj78iJ3UNZ+8F4mKTOztDDV8qk/f2FITtOgI4oAAAAASUVORK5CYII=) <img src="https://upload.wikimedia.org/wikipedia/commons/7/79/Emoji_u1f91d_1f3fc.svg" alt="Good day bro" width="30">

	The author of this project have a lot of expertise in different business solutions and open to new opportunities 
	and is ready to consider interesting offers about:
    • Consulting and analysis on architecture, choice of technologies, code and any problems in existing projects, 
      if you are not sure about your staff;
    • Analysis, design, development and launch of startups;
    • Work in the company as: EMS, CTO, Tech/Team Lead or Senior/Middle Software Engineer;
    • Or other interesting offers.
    
	Have only ONE problem, my verbal English is A2.2 level, but if you or your company ready to help me with this 
	problem I will help you too.) 

> ![](https://img.shields.io/badge/-warning-red?style=for-the-badge&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAANCAYAAACZ3F9/AAABjUlEQVQoU4WRv4sTURDHZ3bf/ophs4tkF0QIErvDwmBrcQcH/gGmsbCyEMHGWmwFS/8Bq7tGwsEVh/cXyMGltBEOkiLd2yjZ/Njse2/e8QIJmyNHphnmy/czM8wg3BOO4wyI6IFSqrnLgrvERqPxYjabnSLivyAIvkwmk8u7vp0gIi7a7XYqpSyHw2GutXb2gmEYvimK4m1Zlq+M2ff9E8bY1XQ6/V6Fd02kbrfr9Hq9EQC4RPQQAAgAtrxbRRAEXwHAWywWn2zbvtFae0T0uFarfTa5KIr366lV0AIACQAmA2PsGgACKeWBqRFRdTodv9/vi1W97uC67rnneWd5nv8wmuu6v8w7pJQvTV2v118vl8sPQoijDdhsNuuc88ysVmn0TWsdCCE+btZDnLdarUeDweD/aiJj7E8Yhu/G4/HvCnhh3iCEOF5rURQ9z/P8p1LqKaZp+oRzfqWUSqrntiwrAwCbiKKqbtv2KI7jQ7QsaxzH8bMsy8z590aSJCnn/K8B50Tk7yUqBkQsbwGKrqBiFJJSbwAAAABJRU5ErkJggg==) <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Emoji_u1f60e.svg/240px-Emoji_u1f60e.svg.png" alt="Good day bro" width="28">

	This project is only for presentation development and architect hard skills of author. 
	Author knows about next missing things but for presentation its hard skills these not 
	included in project so do not need make issue about next:
    • Authorization;
    • SSL/TLS;
    • Encryption/Decryption user data;
    • Graceful start/stop;
    • Normal State Machine.
### This material are for staff or people who is:
    • Software Engineering Manager; 
    • Chief Technology Officer; 
    • System/Solution Architect; 
    • Team/Tech Lead; 
    • Senior/Middle/Junior/Trainee Software Engineer;
    • Any lovers of software development.

- [Overview](#overview)
- [Requirements](#requirements)
- [Branches](#branches)
    * [java-dev](#java-dev)
        * [List of environments](#list-of-environments)
        * [CLI usage](#cli-usage)
- [Documentations for clients](#documentations-for-clients)
- [Author](#author)

### Overview
The Example Message Service project was created to showcase the author’s skills in software 
development and architecture, it is not for use in the real world because it is not part of 
some important components of data security and integrity.

### Requirements
* Java 11
* Gradle 6.5.1
* PostgresDB 11.5
* RabbitMQ 3.8.5
* RedisDB 6.0.5

## Branches
### java-dev
<details>
  <summary>Click here to read more about git clone by branch name</summary>
  <br>
<pre><code>git clone -b java-dev {URL http or git}</code></pre>
</details>

#### List of environments
| Env name | Description | Example |
| :------: | ----------- | :-----: |
| `VERSION` | Version of the project | 0.0.0 |
| `PORT` | Port number for connecting to the service | 8080 |
| `MQ_HOST` | Hostname for connect to the RabbitMQ | localhost |
| `MQ_PORT` | Port number for connect to the RabbitMQ | 5672 |
| `MQ_USER` | Username for authentication to the RabbitMQ | admin |
| `MQ_PASS` | Password for authentication to the RabbitMQ | admin |
| `PG_HOST` | JDBC URI for connect to the PostgresDB | jdbc:postgresql://127.0.0.1:5432/postgres |
| `PG_USER` | Username for authentication to the PostgresDB | postgres |
| `PG_PASS` | Password for authentication to the PostgresDB | postgres |
| `SHOW_SQL` | If `true` then display SQL queries, if `false` then no. | true/false |
| `FORMAT_SQL` | If `true` then prettify SQL queries, if `false` then no. | true/false |
| `LOG_LEVEL` | Logging display level in the service. | trace/debug/info/warn/error |
| `REDIS_PASS` | Password for authentication to the RedisDB | test-redis |
| `REDIS_HOST` | Hostname for connect to the RedisDB | localhost |
| `REDIS_PORT` | Port number for connect to the RedisDB | 6379 |
| `REDIS_DB` | DB number in the RedisDB | 0 |

#### CLI usage Linux(Unix)
##### Step1 - Build project
Init test environments and build
```bash
VERSION=$(cat VERSION) \
PORT={your service test port} \
MQ_HOST={test rabbit host} \
MQ_PORT={test rabbit port} \
MQ_USER={test rabbit username} \
MQ_PASS={test rabbit password} \
PG_HOST={test postgres JDBC connection} \
PG_USER={test postgres username} \
PG_PASS={test postgres password} \
SHOW_SQL=true \
FORMAT_SQL=true \
LOG_LEVEL=trace \
REDIS_PASS={test redis password} \
REDIS_HOST={test redis host} \
REDIS_PORT={test redis port} \
REDIS_DB={test redis DB number} \
./gradlew clean build
```
<br>

##### Step2 - Run from jar
Init prod environments and run from the jar.
```bash
VERSION=$(cat VERSION) \
PORT={your service prod port} \
MQ_HOST={prod rabbit host} \
MQ_PORT={prod rabbit port} \
MQ_USER={prod rabbit username} \
MQ_PASS={prod rabbit password} \
PG_HOST={prod postgres JDBC connection} \
PG_USER={prod postgres username} \
PG_PASS={prod postgres password} \
SHOW_SQL=false \
FORMAT_SQL=false \
LOG_LEVEL=warn \
REDIS_PASS={prod redis password} \
REDIS_HOST={prod redis host} \
REDIS_PORT={prod redis port} \
REDIS_DB={prod redis DB number} \
mkdir -p {path to your release dir}/release/$VERSION \
mkdir -p {path to your release dir}/release/$VERSION/static \
cp /{path to dir when your project is build, default is project dir}/libs {path to your release dir}/release/$VERSION \
cp /{path to your project dir}/static {path to your release dir}/release/$VERSION/static \
cd {path to your release dir}/release/$VERSION
java -jar ems-$VERSION.jar
```
<br>

#### Docker usage Linux
##### Step1 - Build project
Init test environments and build
```bash
VERSION=$(cat VERSION) \
PORT={your service test port} \
MQ_HOST={test rabbit host} \
MQ_PORT={test rabbit port} \
MQ_USER={test rabbit username} \
MQ_PASS={test rabbit password} \
PG_HOST={test postgres JDBC connection} \
PG_USER={test postgres username} \
PG_PASS={test postgres password} \
SHOW_SQL=true \
FORMAT_SQL=true \
LOG_LEVEL=trace \
REDIS_PASS={test redis password} \
REDIS_HOST={test redis host} \
REDIS_PORT={test redis port} \
REDIS_DB={test redis DB number} \
./gradlew clean build
```
<br>

##### Step2 - Build Docker
Build docker image.
```bash
VERSION=$(cat VERSION) \
docker build -t ems/java:$VERSION .
```
<br>

##### Step3 - Run Docker
Init prod environments and run Docker image.
```bash
VERSION=$(cat VERSION) \
PORT={your service prod port} \
MQ_HOST={prod rabbit host} \
MQ_PORT={prod rabbit port} \
MQ_USER={prod rabbit username} \
MQ_PASS={prod rabbit password} \
PG_HOST={prod postgres JDBC connection} \
PG_USER={prod postgres username} \
PG_PASS={prod postgres password} \
SHOW_SQL=false \
FORMAT_SQL=false \
LOG_LEVEL=warn \
REDIS_PASS={prod redis password} \
REDIS_HOST={prod redis host} \
REDIS_PORT={prod redis port} \
REDIS_DB={prod redis DB number} \
docker run --name {your docker run name} \
-e MQ_HOST -e MQ_PORT -e MQ_USER -e MQ_PASS \
-e PG_HOST -e PG_USER -e PG_PASS -e SHOW_SQL \
-e FORMAT_SQL -e LOG_LEVEL -e REDIS_PASS -e REDIS_HOST \
-e REDIS_PORT -e REDIS_DB -p $PORT:8080 ems/java:$VERSION
```
<br>

## Documentations for clients
| Name | Example |
| :------: | :-----: |
| So-called REST API | `http://{hostname}:{port}/v1/docs/restapi` |
| Callback API(Web Socket) | `http://{hostname}:{port}/v1/docs/callback` |

## Author
<table>
  <tr>
    <td align="center"><a href="https://www.linkedin.com/in/ihor-hryshkov-87539724/"><img src="https://media.cakeresume.com/image/upload/s--lm6Vto9J--/c_fill,g_face,h_300,w_300/v1586614267/d4oq6ryxdrcd7tkzzjj0.jpg" width="100px;" alt="Ihor Hryshkov"/><br /><sub><b>Ihor Hryshkov</b></sub></a><br /></td>
  </tr>
</table>
