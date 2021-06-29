#!/bin/bash

echo .. Configuring local enviroment ..

gradle bootRun --args='--spring.profiles.active=local'

echo Local enviroment is ready..