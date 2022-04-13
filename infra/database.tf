# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

resource "digitalocean_database_cluster" "db-cluster-ingest" {
  name                 = "db-cluster-ingest"
  #name                 = "ingest-db-cluster-${local.region}"
  engine               = "pg"
  version              = "14"
  size                 = "db-s-1vcpu-1gb"
  region               = local.region
  node_count           = 1
  private_network_uuid = local.vpc_uuid
}

resource "digitalocean_database_db" "db-ingest" {
  cluster_id = digitalocean_database_cluster.db-cluster-ingest.id
  name       = "ingest"
}

resource "digitalocean_database_firewall" "db-cluster-ingest-fw" {
  cluster_id = digitalocean_database_cluster.db-cluster-ingest.id

  rule {
    type  = "droplet"
    value = digitalocean_droplet.ingest-dp[0].id
  }

  rule {
    type  = "droplet"
    value = digitalocean_droplet.ingest-dp[1].id
  }
}

resource "digitalocean_database_user" "db-user-ingest" {
  cluster_id = digitalocean_database_cluster.db-cluster-ingest.id
  name       = "ingest-service"
}