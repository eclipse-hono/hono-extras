output "values" {
  value       = jsondecode(local.values[0])
  sensitive = true
}