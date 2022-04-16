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

    package_update: true
    package_upgrade: true
    package_reboot_if_required: true

    manage-resolv-conf: true
    resolv_conf:
      nameservers:
        - '8.8.8.8'
        - '8.8.4.4'

    users:
    - name: ubuntu
      lock_passwd: true
      shell: /bin/bash
      groups:
        - ubuntu
        - docker
      sudo:
        - ALL=(ALL) NOPASSWD:ALL

    packages:
      - apt-transport-https
      - ca-certificates
      - curl
      - gnupg-agent
      - software-properties-common
      - docker.io

    snap:
      commands:
        00: [ 'install', 'doctl' ]
        01: [ 'connect', 'doctl:dot-docker' ]

    runcmd:
      - doctl registry login --expiry-seconds 600 --access-token ${var.do_pat}
      - docker pull registry.digitalocean.com/tiki/ingest:${var.sem_ver}
      - docker run -d -p ${local.port}:${local.port} -e DOPPLER_TOKEN="${var.doppler_st}" --restart=always registry.digitalocean.com/tiki/ingest:${var.sem_ver}

    final_message: 'The server is up, after $UPTIME seconds'
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