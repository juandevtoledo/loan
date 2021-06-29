#!/bin/bash

echo .. Configuring local enviroment ..

gradle bootRun --args='--spring.profiles.active=sandbox'

echo Local enviroment is ready..