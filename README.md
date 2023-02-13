# Help

## Explore

1. Start [authorization-server](authorization-server). Refer to its [HELP.md](authorization-server/HELP.md) for more info
2. Start [backend-resource-server](backend-resource-server). Refer to its [HELP.md](backend-resource-server/HELP.md) for more info
3. Start [backed-client-credentials](backed-client-credentials). Refer to its [HELP.md](backed-client-credentials/HELP.md) for more info
4. Start [frontend](frontend). Refer to [HELP.md](frontend/HELP.md) for more info
5. Navigate  to http://frontend.localtest.me:8080[frontend]
6. Click on various links in browser & observe various flows
    * authorization code flow between `frontend` & `authorization-server` & `backend-resource-server`
    * access token relay between `frontend` & `backend-resource-server`, `frontend` & `backed-client-credentials`
    * observe automatic refresh token renewal for calls between `frontend` & `backend-resource-server`
    * client credentials flow between `backed-client-credentials` & `backend-resource-server`

## Pending activities

[] Add support for token reuse incase of client credentials flow (use caching)
[] Integrate all above flows with
    [] Spring cloud loadbalancer
    [] Spring cloud circuitbreaker + bulkhead
    [] Spring declarative client integration (@HttpExchange & its fnds)
    [] Integrate service discovery with k8s DNS service (core-dns)
    [] Integrate non-secret config with k8s configmaps
    [] Integrate secret config with k8s secrets
    [] Micrometer metrics & exporting them to Prometheus + charts in Graphana
    [] Micrometer traces shipped to 
    [] Logs aggregation to Loki
    [] Push everything behind Spring cloud gateway that terminates SSL & does url rewriting to route requests across frontend & authorisation server
    [] Create k8s resource definitions (https ingress with letsencrypt as the CA)
    [] Create helm charts
    [] Create terraform templates to launch k8s infrastructure in EKS/CIVO
    [] Client side loadbalancing with headless services
    [] More? (secret encryption & rotation with KMS in EKS?, ...)
[] Migrate all of above setup into 
[] More? (secret encryption & rotation with KMS in EKS?, ...)
