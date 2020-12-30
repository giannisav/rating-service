# Docker Setup

Use Docker in order to run local environment or simulate production components.

### Create the container
docker-compose -f rating_infra.yml up --no-start

### Start the container
docker-compose -f rating_infra.yml start

### Stop the container
docker-compose -f rating_infra.yml stop


