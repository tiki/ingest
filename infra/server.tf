# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

resource "digitalocean_droplet" "ingest-dp" {
  count      = 2
  image      = "ubuntu-20-04-x64"
  name       = "ingest-dp-${local.region}"
  region     = local.region
  size       = "s-1vcpu-1gb"
  vpc_uuid   = local.vpc_uuid
  monitoring = true
  user_data = <<-EOT
    #cloud-config
    repo_update: true
    repo_upgrade: all

    packages:
      - docker.io
      - dnsmasq
      - resolvconf

    groups:
      - docker

    users:
      - name: ubuntu
        groups: docker
        home: /home/ubuntu
        shell: /bin/bash
        sudo: ALL=(ALL) NOPASSWD:ALL

    system_info:
      default_user:
        groups: [docker]

    snap:
      commands:
        00: [ 'install', 'doctl' ]
        01: [ 'connect', 'doctl:dot-docker' ]

    runcmd:
      - echo 'interface=docker0' >> /etc/dnsmasq.conf
      - echo 'bind-interfaces' >> /etc/dnsmasq.conf
      - echo 'listen-address=172.17.0.1' >> /etc/dnsmasq.conf
      - mkdir -p /etc/resolvconf/resolv.conf.d/
      - echo 'nameserver 172.17.0.1' >> /etc/resolvconf/resolv.conf.d/tail
      - resolvconf -u
      - service dnsmasq restart
      - service docker restart
      - doctl registry login --expiry-seconds 600 --access-token ${var.do_pat}
      - docker pull registry.digitalocean.com/tiki/ingest:${var.sem_ver}
      - docker run -d -p ${local.port}:${local.port} -e DOPPLER_TOKEN="${var.doppler_st}" --restart=always registry.digitalocean.com/tiki/ingest:${var.sem_ver}
      - echo init complete
  EOT
}

resource "digitalocean_firewall" "ingest-fw" {
  name = "ingest-fw"

  droplet_ids = [
    digitalocean_droplet.ingest-dp[0].id,
    digitalocean_droplet.ingest-dp[1].id
  ]

  inbound_rule {
    protocol                  = "tcp"
    port_range                = local.port
    source_load_balancer_uids = [digitalocean_loadbalancer.ingest-lb.id]
  }

  outbound_rule {
    protocol              = "tcp"
    port_range            = "1-65535"
    destination_addresses = ["0.0.0.0/0", "::/0"]
  }
}