# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

locals {
  port     = 8464
  vpc_uuid = "0375b29c-32a0-4edf-86c5-5cdd33137540"
  region   = "nyc3"
}

data "digitalocean_project" "production" {
  name = "production"
}

resource "digitalocean_project_resources" "production" {
  project = data.digitalocean_project.production.id
  resources = concat(data.digitalocean_project.production.resources,
  [digitalocean_database_cluster.db-cluster-ingest.urn])
}


variable "sem_ver" {}
variable "doppler_st" {}