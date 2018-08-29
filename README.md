# Stratum
Stratum test server

### Status
[![Build Status](https://travis-ci.org/sothach/stratum.png)](https://travis-ci.org/sothach/stratum)
[![Coverage Status](https://coveralls.io/repos/github/sothach/stratum/badge.svg?branch=master)](https://coveralls.io/github/sothach/stratum?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a688282e09a04ddeb6d0b29f2c8b82e1)](https://www.codacy.com/project/sothach/stratum/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=sothach/stratum&amp;utm_campaign=Badge_Grade_Dashboard)

## Stubbing Server
This is a simple Play application that exposes a REST API simulating the service provided by ********.
The intention is to deploy this server as a drop-in replacement for the real service, allowing integration testing
of client application, avoiding the charges and providing canned happy-path and error case responses. 

## Running locally
Run the server on a local system, with source change monitoring / automatic restarting
```shell
% sbt ~run
```

## Sample usage
### POST translation request
```shell
curl -X POST -d \
 '{"text": "We strongly advise you to keep your luggage with you at all times. Any unattended luggage in the terminal will be removed by the security services and may be destroyed", "language" : "de", "requestType" : "TEXT"}' \
 -H "Content-Type: application/json" \
 -H "Accept: application/json" \
 http://localhost:9000/api/translate?apiKey=eabb12404d141ed6e8ee2193688178cb
```

### GET speech request
```shell
http://localhost/api/speech?apiKey=eabb12404d141ed6e8ee2193688178cb&action=convert&text=say%20this&voice=usenglishfemale&format=mp3
```

## Testing
### Running the tests
Run the test suite to verify correct behaviour.  

From the command line:
```shell
% sbt test
```
### Test Coverage Report
To measure test coverage, this app uses the 'scoverage' SBT plugin.
To create the report, rom the command line:
```shell
% sbt coverage test coverageReport
```

## Author
* [Roy Phillips](mailto:phillips.roy@gmail.com)

## License
[![License](https://licensebuttons.net/l/by/3.0/88x31.png)](https://creativecommons.org/licenses/by/4.0/) 

(c) 2018 This project is licensed under Creative Commons License

[Attribution 4.0 International (CC BY 4.0)](LICENSE.md)


