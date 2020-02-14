#!/bin/bash

# Copyright (C) 2019, CERN
# This software is distributed under the terms of the GNU General Public
# Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
# In applying this license, CERN does not waive the privileges and immunities
# granted to it by virtue of its status as Intergovernmental Organization
# or submit itself to any jurisdiction.

if [ $OP_WALLET_CONNECTION = "true" ];
then
  echo "wallet connection"
  mkdir -p $OP_WALLET_PATH
  unzip /home/nobody/Wallet_DB.zip -d $OP_WALLET_PATH
else
  echo "normal connection"
fi

# start the application
java -jar /opt/application/target/registration-application.jar