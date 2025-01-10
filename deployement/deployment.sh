#!/bin/bash

# Variables
REPO_PATH="/opt/Smart_House_Monitoring_System"
BRANCH="master"
WILDFLY_DEPLOY_AZURE="/path/to/azure/server/standalone/deployments"
PWA_DEPLOY="/var/www/root/app"
AZURE_SERVER="azureuser@azure-server-ip"
AZURE_WILDFLY_DEPLOY_PATH=""

# Navigate to the repository
cd "$REPO_PATH" || { echo "Repository path not found."; exit 1; }

# Fetch the latest changes from the remote repository
git fetch origin

# Compare the local branch with the remote branch
LOCAL_HASH=$(git rev-parse $BRANCH)
REMOTE_HASH=$(git rev-parse origin/$BRANCH)

if [ "$LOCAL_HASH" != "$REMOTE_HASH" ]; then
    echo "There are changes in the $BRANCH branch. Pulling the latest version and building projects..."

    # Pull the latest changes
    git pull origin $BRANCH

    # Build and deploy IAM
    echo "Building and deploying IAM..."
    cd iam || { echo "IAM directory not found."; exit 1; }
    mvn clean package verify
    # Deploy to Azure (replace with actual Azure path or use SCP/rsync)
    scp target/iam-1.0.war "$AZURE_SERVER:$AZURE_WILDFLY_DEPLOY_PATH/"
    cd ..

    # Build and deploy API
    echo "Building and deploying API..."
    cd api || { echo "API directory not found."; exit 1; }
    mvn clean package verify
    # Deploy to Azure (replace with actual Azure path or use SCP/rsync)
    scp target/api-1.0.war "$AZURE_SERVER:$AZURE_WILDFLY_DEPLOY_PATH/"
    cd ..

    # Deploy PWA
    echo "Deploying PWA..."
    # Copy the PWA content from the 'app' folder in GitHub to the deployment directory
    cd app || { echo "App directory not found."; exit 1; }
    sudo cp -r . "$PWA_DEPLOY/"
    cd ..
    echo "Build and deployment completed successfully."
else
    echo "No changes in the $BRANCH branch."
fi
