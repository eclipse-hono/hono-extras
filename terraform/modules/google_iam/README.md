## Requirements

No requirements.

## Providers

| Name | Version |
|------|---------|
| <a name="provider_google"></a> [google](#provider\_google) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [google_project_iam_member.cloud_endpoint_sa_binding](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/project_iam_member) | resource |
| [google_project_iam_member.service_account_roles](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/project_iam_member) | resource |
| [google_service_account.cloud_endpoints_sa](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/service_account) | resource |
| [google_service_account.gke_service_account](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/service_account) | resource |
| [google_service_account_key.endpoints_sa_key](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/service_account_key) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_project_id"></a> [project\_id](#input\_project\_id) | n/a | `string` | n/a | yes |
| <a name="input_service_account_roles_gke_sa"></a> [service\_account\_roles\_gke\_sa](#input\_service\_account\_roles\_gke\_sa) | Additional roles to be added to the service account. | `list(string)` | n/a | yes |
| <a name="input_service_name_communication"></a> [service\_name\_communication](#input\_service\_name\_communication) | Name of the Cloud Endpoint service for device communication | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_cloud_endpoints_key_file"></a> [cloud\_endpoints\_key\_file](#output\_cloud\_endpoints\_key\_file) | Service Account Key File for Cloud Endpoints Service Account |
| <a name="output_cloud_endpoints_sa_name"></a> [cloud\_endpoints\_sa\_name](#output\_cloud\_endpoints\_sa\_name) | Name of the Cloud Endpoints Service Account |
| <a name="output_gke_service_account_email"></a> [gke\_service\_account\_email](#output\_gke\_service\_account\_email) | Email of the GKE Service Account |
| <a name="output_gke_service_account_name"></a> [gke\_service\_account\_name](#output\_gke\_service\_account\_name) | Name of the GKE Service Account |
