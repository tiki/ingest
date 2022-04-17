# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

data "digitalocean_ssh_key" "terraform" {
  name = "terraform"
}

resource "digitalocean_droplet" "ingest-dp" {
  count      = 2
  image      = "docker-20-04"
  name       = "ingest-dp-${local.region}"
  region     = local.region
  size       = "s-1vcpu-1gb"
  vpc_uuid   = local.vpc_uuid
  monitoring = true
  ssh_keys = [ data.digitalocean_ssh_key.terraform.id ]

  user_data = <<-EOT
    #cloud-config

    package_update: true
    package_upgrade: true
    package_reboot_if_required: true

    packages:
      - dnsmasq
      - resolvconf
      - network-manager

    snap:
      commands:
        00: [ 'install', 'doctl' ]
        01: [ 'connect', 'doctl:dot-docker' ]

    runcmd:
      - echo 'interface=docker0' >> /etc/dnsmasq.conf
      - echo 'bind-interfaces' >> /etc/dnsmasq.conf
      - echo 'listen-address=172.17.0.1' >> /etc/dnsmasq.conf
      - echo 'nameserver 172.17.0.1' >> /etc/resolvconf/resolv.conf.d/tail
      - sudo service network-manager restart
      - sudo resolvconf -u
      - sudo service dnsmasq restart
      - sudo service docker restart
      - doctl registry login --expiry-seconds 600 --access-token ${var.do_pat}
      - docker pull registry.digitalocean.com/tiki/ingest:${var.sem_ver}
      - docker run -d -p ${local.port}:${local.port} -e DOPPLER_TOKEN="${var.doppler_st}" --restart=always registry.digitalocean.com/tiki/ingest:${var.sem_ver}
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