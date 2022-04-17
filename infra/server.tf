# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

data "digitalocean_ssh_key" "terraform" {
  name = "terraform"
}

resource "digitalocean_droplet" "ingest-dp" {
  count      = 2
  image      = "rancheros"
  name       = "ingest-dp-${local.region}"
  region     = local.region
  size       = "s-1vcpu-1gb"
  vpc_uuid   = local.vpc_uuid
  monitoring = true
  ssh_keys = [ data.digitalocean_ssh_key.terraform.id ]

  user_data = <<-EOT
    #cloud-config

    rancher:
      console: centos
      network:
        dns:
          search:
            - 8.8.8.8
            - 8.8.4.4

    write_files:
      - path: /etc/rc.local
        permissions: '0755'
        owner: root
        content: |
          #!/bin/bash
          wait-for-docker
          cd ~ && wget https://github.com/digitalocean/doctl/releases/download/v1.72.0/doctl-1.72.0-linux-amd64.tar.gz
          tar xf ~/doctl-1.72.0-linux-amd64.tar.gz
          sudo mv ~/doctl /usr/local/bin
          doctl registry login --expiry-seconds 600 --access-token dop_v1_995f8c702e57dcf81466f9eb46f0c8a7c64a1609a6e23bfcb800dc1c42ebf0a5
          docker pull registry.digitalocean.com/tiki/ingest:0.0.2
          docker run -d -p 8464:8464 -e DOPPLER_TOKEN="dp.st.prd.wwegoHuCQ0AU2MJaGyhH8Xz3lFqSs9GAkP19q2Ki6u7" --restart=always registry.digitalocean.com/tiki/ingest:0.0.2
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