# Stratum
Stratum test server

### Status
[![Build Status](https://travis-ci.org/sothach/stratum.png)](https://travis-ci.org/sothach/stratum)
[![Coverage Status](https://coveralls.io/repos/github/sothach/stratum/badge.svg?branch=master)](https://coveralls.io/github/sothach/stratum?branch=master)

## Stubbing Server
This is a simple Play application that exposes a REST API simulating the service provided by ********.
The intention is to deploy this server as a drop-in replacement for the real service, allowing integration testing
of client application, avoiding the charges and providing canned happy-path and error case responses. 

## Running locally
Run the server on a local system, with source change monitoring / automatic restarting
```sbtshell
% sbt ~run
```

## Sampe usage
### POST translation request
```sbtshell
curl -X POST -d \
 '{"text": "We strongly advise you to keep your luggage with you at all times. Any unattended luggage in the terminal will be removed by the security services and may be destroyed", "language" : "de", "requestType" : "TEXT"}' \
 -H "Content-Type: application/json" \
 -H "Accept: application/json" \
 http://localhost:9000/api/translate?apiKey=eabb12404d141ed6e8ee2193688178cb
```

### GET speech request
```sbtshell
http://localhost/api/speech?apiKey=eabb12404d141ed6e8ee2193688178cb&action=convert&text=say%20this&voice=usenglishfemale&format=mp3
```

## Testing
### Running the tests
Run the test suite to verify correct behaviour.  

From the command line:
```sbtshell
% sbt test
```
### Test Coverage Report
To measure test coverage, this app uses the 'scoverage' SBT plugin.
To create the report, rom the command line:
```sbtshell
% sbt coverage test coverageReport
```

## Author
* [Roy Phillips](mailto:phillips.roy@gmail.com)

## License
[![License](https://licensebuttons.net/l/by/3.0/88x31.png)](https://creativecommons.org/licenses/by/4.0/) 

(c) 2018 This project is licensed under Creative Commons License

[Attribution 4.0 International (CC BY 4.0)](LICENSE.md)


