# Creating the Service Account for GKE
resource "google_service_account" "gke_service_account" {
  project      = var.project_id
  account_id   = "gke-service-account"
  display_name = "gke-service-account"
}

# Setting IAM Roles for GKE Service Account
resource "google_project_iam_member" "service_account_roles" {
  for_each = toset(local.all_service_account_roles) # Basic Service Account Roles and additional Roles will be concatenated in the locals file
  project  = var.project_id
  role     = each.key
  member   = google_service_account.gke_service_account.member
}

# Creating the Service Account for Cloud Endpoints
resource "google_service_account" "cloud_endpoints_sa" {
  account_id   = "hono-cloud-endpoint-manager"
  display_name = "hono-cloud-endpoint-manager"
}

# Creating necessary iam bindings
resource "google_project_iam_member" "cloud_endpoint_sa_binding" {
  for_each = toset(["roles/servicemanagement.serviceController",
    "roles/cloudtrace.agent",
    "roles/pubsub.editor",
    "roles/servicemanagement.serviceController"
  ])
  project = var.project_id
  role    = each.key
  member  = google_service_account.cloud_endpoints_sa.member
}

resource "google_service_account_key" "endpoints_sa_key" {
  service_account_id = google_service_account.cloud_endpoints_sa.name
  public_key_type    = "TYPE_X509_PEM_FILE"
}
