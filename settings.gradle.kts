rootProject.name = "spring-cloud-play"

include("authorization-service")
include("resource-service") // resource server that everyone is trying to access
include("standalone-client") // oauth client that comes up, starts client-credentials flow to obtain access token, talks to resource server & completes
include("frontend-service") // oauth client with authorization grant_type as authorisation_code
include("token-relaying-service") // relays access to to other resource servers
include("hybrid-resource-server-oauth-client-service") // an oauth client (that uses authorization grant_type of client_credentials) thats also resource-server for other services
