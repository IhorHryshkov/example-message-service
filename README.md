<p align="center">
  <br>
  <img src="https://i.ibb.co/StNR7TK/a2cce7bf-9e37-4bfe-ad73-12e797078639-200x200.png" alt="EMS logo">
</p>

# Example Message Service Project on Java
> ![](https://img.shields.io/badge/-warning-red?style=for-the-badge&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAANCAYAAACZ3F9/AAABjUlEQVQoU4WRv4sTURDHZ3bf/ophs4tkF0QIErvDwmBrcQcH/gGmsbCyEMHGWmwFS/8Bq7tGwsEVh/cXyMGltBEOkiLd2yjZ/Njse2/e8QIJmyNHphnmy/czM8wg3BOO4wyI6IFSqrnLgrvERqPxYjabnSLivyAIvkwmk8u7vp0gIi7a7XYqpSyHw2GutXb2gmEYvimK4m1Zlq+M2ff9E8bY1XQ6/V6Fd02kbrfr9Hq9EQC4RPQQAAgAtrxbRRAEXwHAWywWn2zbvtFae0T0uFarfTa5KIr366lV0AIACQAmA2PsGgACKeWBqRFRdTodv9/vi1W97uC67rnneWd5nv8wmuu6v8w7pJQvTV2v118vl8sPQoijDdhsNuuc88ysVmn0TWsdCCE+btZDnLdarUeDweD/aiJj7E8Yhu/G4/HvCnhh3iCEOF5rURQ9z/P8p1LqKaZp+oRzfqWUSqrntiwrAwCbiKKqbtv2KI7jQ7QsaxzH8bMsy8z590aSJCnn/K8B50Tk7yUqBkQsbwGKrqBiFJJSbwAAAABJRU5ErkJggg==) <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Emoji_u1f60e.svg/240px-Emoji_u1f60e.svg.png" alt="Good day bro" width="32">

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
Runs from the host machine.

<details>
  <summary>Click here to read more about git clone by branch name</summary>
  <br>
<pre><code>git clone -b java-dev {URL http or git}</code></pre>
</details>

#### List of environments
| Env name | Description | Example |
| :------: | ----------- | :-----: |
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

#### CLI usage
```bash
PORT=31111 \
MQ_HOST=localhost \
MQ_PORT=5690 \
MQ_USER=rabbitmq \
MQ_PASS=rabbitmq \
PG_HOST=jdbc:postgresql://127.0.0.1:5452/ems_test \
PG_USER=postgres \
PG_PASS=postgres \
SHOW_SQL=true \
FORMAT_SQL=true \
LOG_LEVEL=trace \
REDIS_PASS=redis-test \
REDIS_HOST=localhost \
REDIS_PORT=6390 \
REDIS_DB=0 \
./gradlew clean build bootRun
```
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
