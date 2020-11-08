<p align="center">
  <br>
  <img src="https://i.ibb.co/StNR7TK/a2cce7bf-9e37-4bfe-ad73-12e797078639-200x200.png" alt="EMS logo">
</p>

# Example Message Service Project on Java

> ![](https://img.shields.io/badge/-new%20opportunity-yellowgreen?style=for-the-badge&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAASCAQAAAD8x0bcAAAAxUlEQVR4AZXRT44BQQDG0bcTa93cYQYRf25CHAWHEGIpIo4yZxnNFdAWNbWYdDrUxvttv+qqpH0uN7eKzWWSOk6ewn+lo7YXPVc3a0ON2MjG3UVXTcfVr++XY0UsVzm5VZNtDOh7OADknta8jdgqZcBcMJQyFsyAlaCR/FJTsEyNfmL10UJ13Sg5mgimQKa0SY52Si2Ao7ve25sGHvYqbReFvrqBi7NMTVfhbmusGZvYeTj78iJ3UNZ+8F4mKTOztDDV8qk/f2FITtOgI4oAAAAASUVORK5CYII=) <img src="https://upload.wikimedia.org/wikipedia/commons/7/79/Emoji_u1f91d_1f3fc.svg" alt="Good day bro" width="30">

    The author of this project have a lot of expertise in different business solutions and
    open to new opportunities and is ready to consider interesting offers about:
    • Consulting and analysis on architecture, choice of technologies, code and any
      problems in existing projects, if you are not sure about your staff;
    • Analysis, design, development and launch of startups;
    • Work in the company as: EMS, CTO, Tech/Team Lead or Senior/Middle Software Engineer;
    • Or other interesting offers.

    Have only ONE problem, my verbal English is A2.2 level, but if you or your company
    ready to help me with this problem I will help you too.)

> ![](https://img.shields.io/badge/-warning-red?style=for-the-badge&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAANCAYAAACZ3F9/AAABjUlEQVQoU4WRv4sTURDHZ3bf/ophs4tkF0QIErvDwmBrcQcH/gGmsbCyEMHGWmwFS/8Bq7tGwsEVh/cXyMGltBEOkiLd2yjZ/Njse2/e8QIJmyNHphnmy/czM8wg3BOO4wyI6IFSqrnLgrvERqPxYjabnSLivyAIvkwmk8u7vp0gIi7a7XYqpSyHw2GutXb2gmEYvimK4m1Zlq+M2ff9E8bY1XQ6/V6Fd02kbrfr9Hq9EQC4RPQQAAgAtrxbRRAEXwHAWywWn2zbvtFae0T0uFarfTa5KIr366lV0AIACQAmA2PsGgACKeWBqRFRdTodv9/vi1W97uC67rnneWd5nv8wmuu6v8w7pJQvTV2v118vl8sPQoijDdhsNuuc88ysVmn0TWsdCCE+btZDnLdarUeDweD/aiJj7E8Yhu/G4/HvCnhh3iCEOF5rURQ9z/P8p1LqKaZp+oRzfqWUSqrntiwrAwCbiKKqbtv2KI7jQ7QsaxzH8bMsy8z590aSJCnn/K8B50Tk7yUqBkQsbwGKrqBiFJJSbwAAAABJRU5ErkJggg==) <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Emoji_u1f60e.svg/240px-Emoji_u1f60e.svg.png" alt="Good day bro" width="28">

    This project is only for presentation development and architect hard skills of author.
    Author knows about next missing things but for presentation its hard skills these not
    included in project so do not need make issue about next:
    • Authorization;
    • SSL/TLS;
    • Encryption/Decryption user data;
    • Graceful start/stop;
    • Normal State Machine;
    • Normal Broker for message bus.

### This material are for staff or people who is:

    • Software Engineering Manager;
    • Chief Technology Officer;
    • System/Solution Architect;
    • Team/Tech Lead;
    • Senior/Middle/Junior/Trainee Software Engineer;
    • Any lovers of software development.

-   [Overview](#overview)
-   [Requirements](#requirements)
    -   [React](#react)
-   [Branches](#branches)
    -   [react-dev](#react-dev)
        -   [Status](#status)
        -   [List of environments](#list-of-environments)
        -   [CLI usage Linux(Unix)](#cli-usage-linuxunix)
-   [Author](#author)

## Overview

The Example Message Service project was created to showcase the author’s skills in software
development and architecture, it is not for use in the real world because it is not part of
some important components of data security and integrity.

## Requirements

### React

-   Node 12.19.0
-   RabbitMQ 3.8.5
-   EMS Service 
    - Java: 0.0.3

## Branches

### react-dev

<details>
  <summary>Click here to read more about git clone by branch name</summary>
  <br>
<pre><code>git clone -b react-dev {URL http or git}</code></pre>
</details>

#### Status Demo

![](https://img.shields.io/badge/-ready-green?style=for-the-badge)

#### List of environments

|       Env name        | Description                              |  Example  |
| :-------------------: | ---------------------------------------- | :-------: |
|        `PORT`         | Port number for connecting to the client |   3000    |
| `REACT_APP_BASE_URL`  | URL for connect to the EMS service       | localhost |
| `REACT_APP_PROXY_URL` | URL for connect to the proxy RabbitMQ    | localhost |
| `REACT_APP_SOCKET_PROXY_LOGIN`  | Login for connect to the proxy RabbitMQ       | rabbitmq |
| `REACT_APP_SOCKET_PROXY_PASS` | Password for connect to the proxy RabbitMQ    | rabbitmq |

#### CLI usage Linux(Unix)
##### Step1 - Run test from npm
Init test environments and run tests.
```bash
REACT_APP_VERSION=$(cat VERSION)
PORT={your client test port}
REACT_APP_BASE_URL={your EMS service test URL}
REACT_APP_PROXY_URL={your proxy RabbitMQ test URL}
REACT_APP_SOCKET_PROXY_LOGIN={your proxy RabbitMQ test login}
REACT_APP_SOCKET_PROXY_PASS={your proxy RabbitMQ test password}

npm run test
```
<br>

##### Step2 - Run prod from npm
Init prod environments and run ```serve -s build```.
```bash
REACT_APP_VERSION=$(cat VERSION)
PORT={your client prod port}
REACT_APP_BASE_URL={your EMS service prod URL}
REACT_APP_PROXY_URL={your proxy RabbitMQ prod URL}
REACT_APP_SOCKET_PROXY_LOGIN={your proxy RabbitMQ prod login}
REACT_APP_SOCKET_PROXY_PASS={your proxy RabbitMQ prod password}

npm install -g serve
npm run build && serve -s build
```
<br>

#### Docker usage Linux(Unix)
##### Step1 - Init test env
Init test environments
```bash
REACT_APP_VERSION=$(cat VERSION)
PORT={your client test port}
REACT_APP_BASE_URL={your EMS service test URL}
REACT_APP_PROXY_URL={your proxy RabbitMQ test URL}
REACT_APP_SOCKET_PROXY_LOGIN={your proxy RabbitMQ test login}
REACT_APP_SOCKET_PROXY_PASS={your proxy RabbitMQ test password}

npm run test
```
<br>

##### Step2.1 - Build Docker
Init prod environments and build docker image.
```bash
REACT_APP_VERSION=$(cat VERSION)
PORT={your client prod port}
REACT_APP_BASE_URL={your EMS service prod URL}
REACT_APP_PROXY_URL={your proxy RabbitMQ prod URL}
REACT_APP_SOCKET_PROXY_LOGIN={your proxy RabbitMQ prod login}
REACT_APP_SOCKET_PROXY_PASS={your proxy RabbitMQ prod password}

docker build -t ema/react:$REACT_APP_VERSION .
```
<br>

##### Step2.2 - Build docker-compose
Build docker image.
```bash
REACT_APP_VERSION=$(cat VERSION)
PORT={your client prod port}
REACT_APP_BASE_URL={your EMS service prod URL}
REACT_APP_PROXY_URL={your proxy RabbitMQ prod URL}
REACT_APP_SOCKET_PROXY_LOGIN={your proxy RabbitMQ prod login}
REACT_APP_SOCKET_PROXY_PASS={your proxy RabbitMQ prod password}

docker-compose build --force-rm --no-cache
```
<br>

##### Step3.1 - Run Docker
Run Docker image.
```bash
REACT_APP_VERSION=$(cat VERSION)
PORT={your client prod port}

docker run --name {your docker run name} $PORT:80 ems/react:$REACT_APP_VERSION
```
<br>

##### Step3.2 - Run docker-compose
Init prod environments and run Docker image.
```bash
REACT_APP_VERSION=$(cat VERSION)
PORT={your client prod port}

docker-compose up -d
```
<br>

## Author

<table>
  <tr>
    <td align="center"><a href="https://www.linkedin.com/in/ihor-hryshkov-87539724/"><img src="https://media.cakeresume.com/image/upload/s--lm6Vto9J--/c_fill,g_face,h_300,w_300/v1586614267/d4oq6ryxdrcd7tkzzjj0.jpg" width="100px;" alt="Ihor Hryshkov"/><br /><sub><b>Ihor Hryshkov</b></sub></a><br /></td>
  </tr>
</table>
