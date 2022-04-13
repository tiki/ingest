# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

resource "digitalocean_certificate" "https-cert" {
  name    = "ingest-https"
  type    = "lets_encrypt"
  domains = ["ingest.mytiki.com"]
}

resource "digitalocean_loadbalancer" "ingest-lb" {
  name     = "ingest-lb-${local.region}"
  region   = local.region
  vpc_uuid = local.vpc_uuid

  forwarding_rule {
    entry_port     = 443
    entry_protocol = "https"

    target_port     = local.port
    target_protocol = "http"

    certificate_name = digitalocean_certificate.https-cert.name
  }

  forwarding_rule {
    entry_port     = 80
    entry_protocol = "http"

    target_port     = local.port
    target_protocol = "http"
  }

  healthcheck {
    port     = local.port
    protocol = "http"
    path     = "/health"
  }

  droplet_ids = [
    digitalocean_droplet.ingest-dp[0].id,
    digitalocean_droplet.ingest-dp[1].id,
  ]
}