# Help

Start `com.example.standalone.StandaloneClientApplication`

# Additional notes

* `http` command here is is `httpie` utility
* `jq` json query utility

## Access token

```
http --verbose \
--auth standalone-client:standalone-client-secret \
--form authorization-service.localtest.me:9000/oauth2/token \
grant_type=client_credentials
```

## Raw access token value

```
http --print b \
--auth standalone-client:standalone-client-secret \
--form authorization-service.localtest.me:9000/oauth2/token \
grant_type=client_credentials | jq --raw-output '.access_token'
```

## Access backend with bearer token

```
BEARER_TOKEN=$(
    http --print b \
        --auth standalone-client:standalone-client-secret \
        --form authorization-service.localtest.me:9000/oauth2/token \
        grant_type=client_credentials \
        | jq --raw-output '.access_token'
)

http --verbose \
--auth-type bearer \
--auth $BEARER_TOKEN \
:55555/
```
