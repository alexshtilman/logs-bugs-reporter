# home work 72

1. Apply Discovery / Client Side Load Balancing pattern we have practiced at the classwork #72 inside the Docker compose landscape
   1. Involved services
      - Logs-bugs-email-provider (registration)
      - Logs-bugs-assigner-mail-provider (registration)
      - Logs-bugs-email-notifier (client side load-balancer)
   1. The idea behind the discovery is that each sync service register its mapping between the service name and an URL. A discovery server keeps all these mappings in the registry. A service communicating with the registered service fetches the data from the registry matching a service name. The idea behind the client side load balancing is that there may be several instances of one service and load balancer provides the data of one according to the Round Robin Algorithm. Thus, you should think of the application.property files content as well as of the proper docker-compose configuration. It will be some challenge for you. Remember, whatever related to local host should be replaced with the service names in the Docker configuration
