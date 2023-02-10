Spring security general petter

Authn Request
    -> AuthenticationConverter
    -> AuthenticationManager (uses one of AuthenticationProvider)
    (success) -> AuthenticationSuccessHandler
    (failure) -> AuthenticationFailureHandler

For Basic Auth
    AuthenticationConverter -> BasicAuthenticationConverter
    Filter that uses this -> BasicAuthenticationFilter

For Form Login

For web requests, AuthenticationSuccessHandler -> SavedRequestAwareAuthenticationSuccessHandler
    as this stores the original request that resulted in the login to happen in the 1st place

