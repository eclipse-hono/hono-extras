terraform {
  backend "gcs" {
    bucket = "<bucket_name>"    # Insert your bucket name
    prefix = "terraform/software"
  }
}
