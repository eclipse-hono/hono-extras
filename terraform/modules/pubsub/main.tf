#Creating the pubsub topics
resource "google_pubsub_topic" "notification_registry_tenant" {
  name    = "registry-tenant.notification"
  project = var.project_id
}

resource "google_pubsub_topic" "notification_registry_device" {
  name    = "registry-device.notification"
  project = var.project_id
}
resource "google_pubsub_subscription" "registry_tenant_notification" {
  name  = "registry-tenant.notification"
  topic = google_pubsub_topic.notification_registry_tenant.name

  retain_acked_messages = false
  message_retention_duration = "600s"
  ack_deadline_seconds = 10
  retry_policy {
    minimum_backoff = "10s"
  }

  enable_message_ordering = false
}
resource "google_pubsub_subscription" "registry-tenant_notification_communication_api" {
  name  = "registry-tenant.notification-communication-api"
  topic = google_pubsub_topic.notification_registry_tenant.name

  retain_acked_messages = false
  message_retention_duration = "600s"
  ack_deadline_seconds = 10
  retry_policy {
    minimum_backoff = "10s"
  }

  enable_message_ordering = false
}
resource "google_pubsub_subscription" "registry-device_notification" {
  name  = "registry-device.notification"
  topic = google_pubsub_topic.notification_registry_device.name

  retain_acked_messages = false
  message_retention_duration = "600s"
  ack_deadline_seconds = 10
  retry_policy {
    minimum_backoff = "10s"
  }

  enable_message_ordering = false
}
