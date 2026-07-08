# Castle

## Setup

```sh
docker run --rm -v .:/project:ro -e ANSIBLE_CONFIG=/project/ansible/ansible.cfg -w /project/ansible gatomek_ansible ansible-playbook -i inventory.ini playbooks/setup.yml
```

## Restart

```sh
docker run --rm -v .:/project:ro -e ANSIBLE_CONFIG=/project/ansible/ansible.cfg -w /project/ansible gatomek_ansible ansible-playbook -i inventory.ini playbooks/restart.yml
```
