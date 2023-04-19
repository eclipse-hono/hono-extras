# Minimal Permissions needed for the GKE Service Account
locals {
  all_service_account_roles = concat(var.service_account_roles_gke_sa, [
    "roles/pubsub.editor",
    "roles/iam.serviceAccountOpenIdTokenCreator",
    "roles/monitoring.dashboardViewer",
    "roles/container.nodeServiceAccount"
  ])
}
