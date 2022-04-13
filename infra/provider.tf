# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

terraform {
  required_providers {
    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "~> 2.0"
    }
  }

  backend "remote" {
    organization = "tiki"

    workspaces {
      name = "ingest-test"
    }
  }
}

variable "do_pat" {}
variable "do_ssh" {}

provider "digitalocean" {
  token = var.do_pat
}