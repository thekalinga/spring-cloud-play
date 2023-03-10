# Help

1. Start `com.example.hybrid.HybridApplication`
2. Server starts on `44444` port. Access it [here](http://hybrid-resource-server-oauth-client-service-server.localtest.me:44444)

# Additional notes

* `http` command here is is `httpie` utility
* `jq` json query utility

## Access token

```
http --verbose \
--auth backend-client-credentials-client:backend-client-credentials-client-secret \
--form authorization-service.localtest.me:9000/oauth2/token \
grant_type=client_credentials
```

## Raw access token value

```
http --print b \
--auth backend-client-credentials-client:backend-client-credentials-client-secret \
--form authorization-service.localtest.me:9000/oauth2/token \
grant_type=client_credentials | jq --raw-output '.access_token'
```

## Access backend with bearer token

```
BEARER_TOKEN=$(
    http --print b \
        --auth backend-client-credentials-client:backend-client-credentials-client-secret \
        --form authorization-service.localtest.me:9000/oauth2/token \
        grant_type=client_credentials \
        | jq --raw-output '.access_token'
)

http --verbose \
--auth-type bearer \
--auth $BEARER_TOKEN \
:55555/
```
