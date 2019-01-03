#! /bin/bash

echo "$BRANCH"
IFS='.' read -r -a BRANCH_INFO <<< "$BRANCH"
if [[ "${#BRANCH_INFO[@]}" == 3 ]] && [[ "${BRANCH_INFO[0]}" =~ ^[0-9]+$ ]] && [[ "${BRANCH_INFO[1]}" =~ ^[0-9]+$ ]] && [[ "${BRANCH_INFO[2]}" =~ ^[0-9]+$ ]];
    then
    docker exec frc-testing-image /code/gradlew -p /code publishToTeamRepo --username=$USERNAME --password=$PASSWORD --url=$URL
elif [ "${BRANCH_INFO[0]}" == "master" ];
    then
    docker exec frc-testing-image /code/gradlew -p /code publishToTeamRepo --username=$USERNAME --password=$PASSWORD --url=$URL
fi
