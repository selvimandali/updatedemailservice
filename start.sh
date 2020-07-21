#!/bin/bash
cd /home/ubuntu/emailservice
echo "Before starting email server"
kubectl apply -f email-service.yml 
kubectl apply -f email-deployment_temp.yml
echo "end of email server"