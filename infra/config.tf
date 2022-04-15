# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

locals {
  port     = 8464
  vpc_uuid = "0375b29c-32a0-4edf-86c5-5cdd33137540"
  region   = "nyc3"
}

variable "sem_ver" {
  default = "0.0.1"
}

variable "doppler_st" {}

resource "digitalocean_project" "ingest" {
  name        = "ingest"
  description = "https://github.com/tiki/ingest"
  purpose     = "Service or API"
  environment = "Production"
  resources = [
    digitalocean_droplet.ingest-dp[0].urn,
    digitalocean_droplet.ingest-dp[1].urn,
    digitalocean_loadbalancer.ingest-lb.urn,
    digitalocean_database_cluster.db-cluster-ingest.urn
  ]
}