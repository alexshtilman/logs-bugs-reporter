# class work 76

1. Add dependencies 2 to pom file
   - `jjwt` - for main json web token generation
   - `jaxb-api` - for fix trouble with BASE64 decoding
1. update `securityFiltersChain`:
   - disable `httpBasic`
   - add `pathMatchers("/login").permitAll()`
   - add `authenticationManager(authenticatior)`
   - add `securityContextRepository(securityContext)`
1. add component that implements `ServerSecurityContextRepository`
   - implement method `load`
1. add component that implements `ReactiveAuthenticationManager`
   - add `authenticate` method
1. add component `JwtUtilService` that can:
   - generateToken
   - validateToken
1. add to restcontroller method `Post "/login"`
   - should return json web token if credentials are valid
