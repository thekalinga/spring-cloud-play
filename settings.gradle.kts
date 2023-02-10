rootProject.name = "spring-cloud-play"

include("authorization-server")
include("frontend") // client with authorization grant_type as authorisation_code
include("backend-resource-server")
include("backed-client-credentials") // client with authorization grant_type as client_credentials
