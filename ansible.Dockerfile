FROM registry.invalid/projects/ansible
COPY . /project
WORKDIR /project/ansible
ENV ANSIBLE_CONFIG=/project/ansible/ansible.cfg
