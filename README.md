# Help

## Explore

1. Start [authorization-server](authorization-server). Refer to its [HELP.md](authorization-server/HELP.md) for more info
2. Start [resource-service](backend-resource-service). Refer to its [HELP.md](resource-service/HELP.md) for more info
3. Start [client-credentials](backed-client-credentials). Refer to its [HELP.md](backed-client-credentials/HELP.md) for more info
4. Start [frontend](frontend). Refer to [HELP.md](frontend/HELP.md) for more info
5. Navigate to [frontend](http://frontend.localtest.me:8080)
6. Click on various links in browser & observe various flows
    * authorization code flow between `frontend` & `authorization-server` & `resource-service`
    * access token relay between `frontend` & `resource-service`, `frontend` & `backed-client-credentials`
    * observe automatic refresh token renewal for calls between `frontend` & `resource-service`
    * client credentials flow between `backed-client-credentials` & `resource-service`

Since all our services will run on the same machine (`localhost`), use `*.localtest.me` so we can access each of these local services as if they are deployed on a independent DNS hosts. All subdomains of `localtest.me` will be resolved by public DNS server to `localhost` i.e `127.0.0.1`. The reason why we would want to do this is to ensure the cookies (uses by both frontend & authorisation server) can be scoped properly within the browser to appropriate subdomain.

* For frontend navigate to [frontend](http://frontend.localtest.me:8080)
* For authorisation server navigate to [authorisation-server](http://auth.localtest.me:9000)
* For backend resource server cum client credentials server [backend-client-credentials-server](http://backend-client-credentials-server.localtest.me:8081)
* For backend resource server navigate to [resource-service](http://resource-service.localtest.me:8081)

## Pending activities

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
    - [ ] Push everything behind Spring cloud gateway that terminates SSL & does url rewriting to route requests across frontend & authorisation server
    - [ ] Create k8s resource definitions (https ingress with letsencrypt as the CA)
    - [ ] Create helm charts
    - [ ] Create terraform templates to launch k8s infrastructure in EKS/CIVO
    - [ ] Client side loadbalancing with headless services
    - [ ] More? (secret encryption & rotation with KMS in EKS?, ...)
- [ ] Migrate all of above setup into 
- [ ] More? (secret encryption & rotation with KMS in EKS?, ...)
