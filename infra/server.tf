# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

data "digitalocean_ssh_key" "terraform" {
  name = "terraform"
}

resource "digitalocean_droplet" "ingest-dp" {
  count      = 2
  image      = "ubuntu-20-04-x64"
  name       = "ingest-dp-${local.region}"
  region     = local.region
  size       = "s-1vcpu-1gb"
  vpc_uuid   = local.vpc_uuid
  monitoring = true
  ssh_keys = [
    data.digitalocean_ssh_key.terraform.id
  ]

  connection {
    host        = self.ipv4_address
    user        = "root"
    type        = "ssh"
    private_key = base64decode(var.do_ssh)
    timeout     = "2m"
  }

  provisioner "remote-exec" {
    inline = [
      "sudo apt update",
      "sudo apt install apt-transport-https ca-certificates curl software-properties-common",
      "curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -",
      "sudo add-apt-repository \"deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable\"",
      "apt-cache policy docker-ce",
      "sudo apt install docker-ce -y",
      "sudo snap install doctl",
      "sudo snap connect doctl:dot-docker",
      "doctl registry login --expiry-seconds 600 --access-token ${var.do_pat}",
      "docker pull registry.digitalocean.com/tiki/ingest:${var.sem_ver}",
      "docker run -d -p ${local.port}:${local.port} -e DOPPLER_TOKEN=\"${var.doppler_st}\" registry.digitalocean.com/tiki/ingest:${var.sem_ver}"
    ]
  }
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