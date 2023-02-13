
* `http` command here is is `httpie` utility
* `jq` json query utility

`OAuth2AuthorizationServerMetadataEndpointFilter`

`http auth.localtest.me:9000/.well-known/oauth-authorization-server &> logs/oauth-authorization-server-metadata.json`

`OidcProviderConfigurationEndpointFilter`

http auth.localtest.me:9000/.well-known/openid-configuration &> logs/openid-configuration-metadata.json

# Access token

To retrieve token (access, refresh & id token), access `http :8080/token`

# Verify token signature

To verify token is properly signed, `access :9000/oauth2/jwks`

Get the json for the key with kid (not all keys) & paste it in `public key` area of jwt.io

# Fetch user info

Access userinfo endpoint on auth server using above token

```
http -v http://auth.localtest.me:9000/userinfo \
    Accept:application/json \
    Authorization:"Bearer "
```
