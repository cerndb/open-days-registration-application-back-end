# Copyright (C) 2019, CERN
# This software is distributed under the terms of the GNU General Public
# Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
# In applying this license, CERN does not waive the privileges and immunities
# granted to it by virtue of its status as Intergovernmental Organization
# or submit itself to any jurisdiction.

# configure home directories
export JAVA_HOME=/usr/lib/jvm/java
export PATH=$JAVA_HOME/bin:$PATH 
export M2_HOME=/usr/local/src/apache-maven
export M2=$M2_HOME/bin
export MAVEN_OPTS="-Xmx1048m -Xms256m"
export PATH=$M2:$PATH 
