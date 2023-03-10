# Help

## Explore

1. Start [authorization-service](authorization-service). Refer to its [HELP.md](authorization-service/HELP.md) for more info
2. Start [resource-service](resource-service). Refer to its [HELP.md](resource-service/HELP.md) for more info
3. Start [standalone-client](standalone-client). Refer to its [HELP.md](standalone-client/HELP.md) for more info
4. Start [hybrid-resource-server-oauth-client-service](hybrid-resource-server-oauth-client-service). Refer to its [HELP.md](hybrid-resource-server-oauth-client-service/HELP.md) for more info
5. Start [token-relaying-service](token-relaying-service). Refer to its [HELP.md](token-relaying-service/HELP.md) for more info
6. Start [frontend-service](frontend-service). Refer to [HELP.md](frontend-service/HELP.md) for more info
7. Navigate to [frontend-service](http://frontend-service.localtest.me:8080)
8. Click on various links in browser & observe various flows
   * Authorization Code flow between `frontend-service` -> `authorization-service` -> `frontend-service` -> `resource-service`
   * Access token relay between `frontend-service` & `resource-service`, `frontend-service` & `hybrid-resource-server-oauth-client-service`
   * Observe automatic refresh token renewal for calls between `frontend-service` -> `resource-service`
   * Hybrid resource service + client credentials flow between `frontend-service` -> `hybrid-resource-server-oauth-client-service` -> `resource-service`

Since all our services will run on the same machine (`localhost`), use `*.localtest.me` so we can access each of these local services as if they are deployed on a independent DNS hosts. All subdomains of `localtest.me` will be resolved by public DNS server to `localhost` i.e `127.0.0.1`. The reason why we would want to do this is to ensure the cookies (uses by both frontend-service & authorisation server) can be scoped properly within the browser to appropriate subdomain.

## Pending activities

I have plans for adding more & more components in future

- [ ] Add support for token reuse incase of client credentials flow (use caching)
- [ ] Integrate all above flows with
    - [*] Spring cloud loadbalancer
    - [ ] Spring cloud circuitbreaker + bulkhead
    - [ ] Spring declarative client integration (@HttpExchange & its fnds)
    - [ ] Integrate service discovery with k8s DNS service (core-dns)
    - [ ] Integrate non-secret config with k8s configmaps
    - [ ] Integrate secret config with k8s secrets
    - [ ] Micrometer metrics & exporting them to Prometheus + charts in Graphana
    - [ ] Micrometer traces shipped to 
    - [ ] Logs aggregation to Loki
    - [ ] Push everything behind Spring cloud gateway that terminates SSL & does url rewriting to route requests across frontend-service & authorisation server
    - [ ] Create k8s resource definitions (https ingress with letsencrypt as the CA)
    - [ ] Create helm charts
    - [ ] Create terraform templates to launch k8s infrastructure in EKS/CIVO
    - [ ] Client side loadbalancing with headless services
    - [ ] More? (secret encryption & rotation with KMS in EKS?, ...)
- [ ] Migrate all of above setup into 
- [ ] More? (secret encryption & rotation with KMS in EKS?, ...)
