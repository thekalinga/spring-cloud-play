To retrieve token, access :8080/token

To verify token is properly signed, access :9000/oauth2/jwks

Get the json for the key with kid (not all keys) & paste it in `public key` area of jwt.io

Access userinfo endpoint on auth server using above token

http -v http://auth.localtest.me:9000/userinfo \
Accept:application/json \
Authorization:"Bearer "

