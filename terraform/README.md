# Hono Terraform Deployment

Use the environment-template to deploy Eclipse-Hono in a project.

Prerequisites:
1. Register a domain name. For instructions about how to register a domain using Cloud Domains, see [Register a domain](https://cloud.google.com/dns/docs/tutorials/create-domain-tutorial).
2. Setup DNS records for the Eclipse Hono API & MQTT endpoint (Example: api.hono.mydomain.com and mqtt.hono.mydomain.com). For instructions on how to set up DNS records, see [Setup DNS records](https://cloud.google.com/dns/docs/set-up-dns-records-domain-name).
3. Create Certificates for the previously created DNS records. For example use letsencrypt.
4. Set up your [OAuth Consent Screen](https://console.cloud.google.com/apis/credentials/consent). More information about [OAuth Consent Screen](https://support.google.com/cloud/answer/10311615?hl=en&ref_topic=3473162&sjid=5743182626460156348-EU).
5. Create an [OAuth Client](https://console.cloud.google.com/apis/credentials).
6. Create a Cloud Storage bucket, see [Create buckets](https://cloud.google.com/storage/docs/creating-buckets).


The following steps need to be followed:

1. Replace the placeholders in the following files with the name of the newly created bucket:
    * backend.tf in environment-template/infrastructure,
    * backend.tf and data.tf in environment-template/software.
2. Adjust the values in the locals.tf files in environment-template/infrastructure and environment-template/software to fit your case.
3. For transport layer security (TLS) on the API and the MQTT adapter we are expecting key and cert files, named “api_tls.key”, “api_tls.crt”, “mqtt_tls.key” and “mqtt_tls.crt”, respectively, within the folder environment-template/software. The api files will be used to create both the esp-ssl and the ingress-tls secret. Rename the files you got from creating the certificates for your DNS records accordingly and copy them into the previously mentioned folder.
4. Open a command-line interface (CLI).
5. Set your project in your gcloud config by running the following command:

   gcloud config set <b>project-id</b>

   Replace <b>project-id</b> with the ID of your project.
6. Log into your account by running:

   gcloud auth application-default login

7. Within your CLI navigate to the environment-template/infrastructure folder and run the following commands:

   terraform init

   terraform plan

   terraform apply

8. After terraform apply is complete, navigate to the environment-template/software folder within your CLI.
9. If your Helm chart is within a private Artifact Registry:

   Authenticate with the registry using the following command:

   gcloud auth application-default print-access-token | helm registry login -u oauth2accesstoken --password-stdin https://<b>location</b>-docker.pkg.dev

   Replace <b>location</b> with the location of the Artifact Registry, where the Helm chart is located.
10. Run the commands from step 7 again.


More Details to Variables and Locals can be found in the template README´s
