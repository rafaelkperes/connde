#!/usr/bin/env sh

export WORKON_HOME=/home/rosso/python_env
source /usr/bin/virtualenvwrapper.sh

workon connde
python /opt/rmpadvertise/rmpadvertise.py --lan