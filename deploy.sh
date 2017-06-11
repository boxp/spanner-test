#!/bin/bash

# Exit on any error
set -e

for f in k8s/*.yml
do
	envsubst < $f > "generated-$(basename $f)"
done
sudo /opt/google-cloud-sdk/bin/gcloud docker -- push asia.gcr.io/${PROJECT_NAME}/spanner-test:$CIRCLE_SHA1
sudo chown -R ubuntu:ubuntu /home/ubuntu/.kube
kubectl apply -f generated-deployment.yml
