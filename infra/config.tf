# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

locals {
  port     = 8464
  vpc_uuid = "0375b29c-32a0-4edf-86c5-5cdd33137540"
  region   = "nyc3"
}

resource "digitalocean_project" "production" {
  name        = "production"
  description = "https://github.com/tiki"
  purpose     = "Service or API"
  environment = "Production"
  resources = [
    digitalocean_database_cluster.db-cluster-ingest.urn
  ]
}

variable "sem_ver" {}
variable "doppler_st" {}