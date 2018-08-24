curl -X POST -d \
 '{"text": "We strongly advise you to keep your luggage with you at all times. Any unattended luggage in the terminal will be removed by the security services and may be destroyed", "language" : "de", "requestType" : "TEXT"}' \
 -H "Content-Type: application/json" \
 -H "Accept: application/json" \
 -H "Authorization: Basic dGVzdDpzZWNyZXQ=" \
 http://localhost:9000/api/translate
