#!/bin/bash +x

#parameters check
envs="TEST"
if [ -n "$1" ] ;then
    envs="$1"
fi

#export S3_BUCKET=""
echo "start building"
tmpDir=/tmp/dist
destFile=/tmp/artifact.zip

mkdir $tmpDir

#srcDirs=("authentication-service") 
#srcDirs=("zuulsvr" "cocktail-service" "ingredient-service" "user-service" "authentication-service" "management-service")
srcDirs=("configurationserver" "zuulsvr" "cocktail-service" "ingredient-service" "user-service" "authentication-service" "management-service")
#srcDirs=("configurationserver" "eurekasvr" "zuulsvr" "user-service" "cocktail-service" "ingredient-service" "management-service")

echo ${srcDirs[@]}
echo ${!srcDirs[@]}

for dir in ${srcDirs[@]}; do
cp $dir/target/*.jar $tmpDir
cp $dir/appspec.yml $tmpDir    
cp -r $dir/scripts $tmpDir 
  
cd $tmpDir            
pwd

zip -r /tmp/artifact.zip * # package up the application for deployment
cd -
export APPLICATIONNAME=$dir
export DEPLOYMENT_GROUP_NAME=$dir
export ENVIRONMENT=$envs
#APPLICATION_NAME
#DEPLOYMENT_GROUP_NAME
#DEPLOYMENT_CONFIG
echo "APPLICATIONNAME:"$APPLICATIONNAME
echo "DEPLOYMENT_GROUP_NAME:"$DEPLOYMENT_GROUP_NAME
echo "ENVIRONMENT:"$envs

echo "S3_BUCKET:"$S3_BUCKET

python codedeploy_deploy.py $APPLICATIONNAME

rm -rf $destFile
rm -rf $tmpDir/*

done