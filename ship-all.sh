#! /usr/bin/env bash

for ((INSTALL_CHANNEL = 1; INSTALL_CHANNEL <= 20; ++INSTALL_CHANNEL));
do
	ant ship -Dapp.channel=$INSTALL_CHANNEL;
done
