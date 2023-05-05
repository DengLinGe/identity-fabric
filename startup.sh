#!/bin/bash
./network.sh up -i 2.3.3
./network.sh createChannel
./network.sh deployCC -ccn identity -ccp ../asset-transfer-basic/chaincode-identity -ccl go

