# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

data "template_file" "userdata" {
  template = file("cloud-config.yaml")

  vars = {
    do_pat     = var.do_pat
    doppler_st = var.doppler_st
    sem_ver    = var.sem_ver
    port       = local.port
  }
}

resource "digitalocean_droplet" "ingest-dp" {
  count      = 2
  image      = "ubuntu-20-04-x64"
  name       = "ingest-dp-${local.region}"
  region     = local.region
  size       = "s-1vcpu-1gb"
  vpc_uuid   = local.vpc_uuid
  monitoring = true
  user_data = data.template_file.userdata.rendered
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