# Fetch artifact with proxy plugin

This is a plugin to support a proxy for fetch artifact between Go Agent and Go Server. This will be useful mainly to cache artifacts when Go server and Go agent as geographically separate. 
 
## Setup

- Host a proxy server in the network of the GoAgent(s).
- Configure it to forward all inbound requests to be forwarded to the GoServer
- On each GoAgent, create a file `/etc/go/artifact_proxy.conf`. The contents of this file should be:
    ```
    ARTIFACT_FETCH_PROXY_HOST=<PROXY_SERVER_HOST_WITH_PROTOCOL>
    ARTIFACT_FETCH_PROXY_PORT=<PROXY_SERVER_PORT>
    ```
- Use the 


## Building the code base

To build the jar, run `./gradlew clean build`

For local testing, run `./gradlew clean publishToGoServerPluginsDir` to publish the jar to `/var/lib/go-server/plugins/external/`

