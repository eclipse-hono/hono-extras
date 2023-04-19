locals {
  project_id     = "<project_id>"                                           # Insert your project id
  region         = "europe-west1"                                           # Insert the region for your cluster
  zone           = "europe-west1-b"                                         # Insert the zone for your cluster and SQL database
  node_locations = ["europe-west1-c", "europe-west1-b", "europe-west1-d"]   # Insert the node locations for your cluster
}
