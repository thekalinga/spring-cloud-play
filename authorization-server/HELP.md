OAuth2AuthorizationServerMetadataEndpointFilter

http auth.localtest.me::9000/.well-known/oauth-authorization-server &> logs/oauth-authorization-server-metadata.json

OidcProviderConfigurationEndpointFilter

http auth.localtest.me::9000/.well-known/openid-configuration &> logs/openid-configuration-metadata.json

use *.localtest.me for localhost subdomains so cookies are scoped properly within the browser

For auth server navigate to auth.localtest.me:9000
For client navigate to frontend.localtest.me:8080
For resource server navigate to backend-resource-server.localtest.me:8081
For resource server navigate to backend-client-credentials-server.localtest.me:8081

http -v frontend.localtest.me:8080/oauth2/authorize

