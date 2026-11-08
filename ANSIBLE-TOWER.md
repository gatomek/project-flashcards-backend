# Setup

## Tower

## Prerequisites

* docker context `docker-on-tower` prepared on local machine with docker agent

```
docker context create docker-on-tower --docker host=tcp://tower:2375
```

### Setup App

```sh
docker --context docker-on-tower compose --file remote-ansible.yml run --rm --build setup-app
```

### Update SSL

```sh
docker --context docker-on-tower compose --file remote-ansible.yml run --rm --build flashcards-update-ssl
```

### Setup Flashcards Restarter

```sh
docker --context docker-on-tower compose --file remote-ansible.yml run --rm --build flashcards-restarter
```
