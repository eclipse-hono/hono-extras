## Requirements

No requirements.

## Providers

| Name | Version |
|------|---------|
| <a name="provider_google"></a> [google](#provider\_google) | n/a |
| <a name="provider_random"></a> [random](#provider\_random) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [google_sql_database.hono_sql_db](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/sql_database) | resource |
| [google_sql_database_instance.hono_sql](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/sql_database_instance) | resource |
| [google_sql_user.hono-sql-user](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/sql_user) | resource |
| [random_password.password](https://registry.terraform.io/providers/hashicorp/random/latest/docs/resources/password) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_network_id"></a> [network\_id](#input\_network\_id) | Reference to VPC Network ID only | `string` | n/a | yes |
| <a name="input_project_id"></a> [project\_id](#input\_project\_id) | The project ID to deploy to | `string` | n/a | yes |
| <a name="input_region"></a> [region](#input\_region) | The region for the Cloud SQL Instance | `string` | n/a | yes |
| <a name="input_service_networking"></a> [service\_networking](#input\_service\_networking) | Reference to service\_networking connection from VPC\_network to SQL Instance | `any` | n/a | yes |
| <a name="input_sql_database_name"></a> [sql\_database\_name](#input\_sql\_database\_name) | The name of the database in the Cloud SQL instance. This does not include the project ID or instance name. | `string` | n/a | yes |
| <a name="input_sql_db_user_name"></a> [sql\_db\_user\_name](#input\_sql\_db\_user\_name) | The name of the user. Changing this forces a new resource to be created. | `string` | n/a | yes |
| <a name="input_sql_instance_activation_policy"></a> [sql\_instance\_activation\_policy](#input\_sql\_instance\_activation\_policy) | This specifies when the instance should be active. Can be either ALWAYS, NEVER or ON\_DEMAND. | `string` | n/a | yes |
| <a name="input_sql_instance_deletion_policies"></a> [sql\_instance\_deletion\_policies](#input\_sql\_instance\_deletion\_policies) | Used to block Terraform from deleting a SQL Instance. Defaults to false. | `bool` | n/a | yes |
| <a name="input_sql_instance_disk_type"></a> [sql\_instance\_disk\_type](#input\_sql\_instance\_disk\_type) | Disk Type of the SQL Instance | `string` | n/a | yes |
| <a name="input_sql_instance_machine_type"></a> [sql\_instance\_machine\_type](#input\_sql\_instance\_machine\_type) | Machine Type of the SQL Instance | `string` | n/a | yes |
| <a name="input_sql_instance_name"></a> [sql\_instance\_name](#input\_sql\_instance\_name) | Name of the SQL Instance | `string` | n/a | yes |
| <a name="input_sql_instance_version"></a> [sql\_instance\_version](#input\_sql\_instance\_version) | Database Version | `string` | n/a | yes |
| <a name="input_sql_public_ip_enable"></a> [sql\_public\_ip\_enable](#input\_sql\_public\_ip\_enable) | Whether this Cloud SQL instance should be assigned a public IPV4 address. At least ipv4\_enabled must be enabled or a private\_network must be configured. | `bool` | n/a | yes |
| <a name="input_storage_size_gb"></a> [storage\_size\_gb](#input\_storage\_size\_gb) | The storage size in GB for the Cloud SQL Instance (100 or 200) | `number` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_sql_database"></a> [sql\_database](#output\_sql\_database) | Name of the Postgres Database |
| <a name="output_sql_db_pw"></a> [sql\_db\_pw](#output\_sql\_db\_pw) | Output of the SQL user password |
| <a name="output_sql_ip"></a> [sql\_ip](#output\_sql\_ip) | URL of the Postgres Database |
| <a name="output_sql_user"></a> [sql\_user](#output\_sql\_user) | Output of the SQL user name |
