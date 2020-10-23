export PATH=${PWD}/../bin:$PATH

export FABRIC_CFG_PATH=$PWD/../config/

echo "체인코드 가져와서 압축"

peer lifecycle chaincode package ledger.tar.gz --path ../asset-transfer-ledger-queries/chaincode-go/ --lang golang --label ledger_1.0

export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_ADDRESS=localhost:7051

echo "체인코드 설치"

peer lifecycle chaincode install ledger.tar.gz


if [[ -e "queryinstalled.txt" ]]; then
    rm -Rf queryinstalled.txt
 fi

echo "체인코드 설치 키값"
    peer lifecycle chaincode queryinstalled >queryinstalled.txt


	cat queryinstalled.txt
 

#  s/원본/바꿀문자/; 
# ^START ==> START로 시작하는 문자열
# $END ==> END로 끝나는 문자열 
# p 출력 
# 각 명령사이 ;

	CC_PACKAGE_ID=$(sed -n "s/^Installed.*//;s/^Package ID: //; s/, Label:.*$//; p;" queryinstalled.txt)

   echo $CC_PACKAGE_ID





echo "오더러에게 승인요청"
peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID mychannel --name ledger --version 1.0 --package-id $CC_PACKAGE_ID --sequence 1 --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

peer lifecycle chaincode checkcommitreadiness --channelID mychannel --name ledger --version 1.0 --sequence 1 --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem --output json

echo "최종 커밋"
peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID mychannel --name ledger --version 1.0 --sequence 1 --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem --peerAddresses localhost:7051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt 



echo "node.app 실행 하면됨 "
# peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n basic --peerAddresses localhost:7051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt  -c '{"function":"initLedger","Args":[]}'


# peer chaincode query -C mychannel -n basic -c '{"Args":["getAllAssets"]}'
