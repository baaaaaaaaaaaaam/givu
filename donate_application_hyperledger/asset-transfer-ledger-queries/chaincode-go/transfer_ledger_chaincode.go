/*
 SPDX-License-Identifier: Apache-2.0
*/

/*
====CHAINCODE EXECUTION SAMPLES (CLI) ==================

==== Invoke assets ====
peer chaincode invoke -C myc1 -n asset_transfer -c '{"Args":["CreateAsset","asset1","blue","5","tom","35"]}'
peer chaincode invoke -C myc1 -n asset_transfer -c '{"Args":["CreateAsset","asset2","red","4","tom","50"]}'
peer chaincode invoke -C myc1 -n asset_transfer -c '{"Args":["CreateAsset","asset3","blue","6","tom","70"]}'
peer chaincode invoke -C myc1 -n asset_transfer -c '{"Args":["TransferAsset","asset2","jerry"]}'
peer chaincode invoke -C myc1 -n asset_transfer -c '{"Args":["TransferAssetByColor","blue","jerry"]}'
peer chaincode invoke -C myc1 -n asset_transfer -c '{"Args":["DeleteAsset","asset1"]}'

==== Query assets ====
peer chaincode query -C myc1 -n asset_transfer -c '{"Args":["ReadAsset","asset1"]}'
peer chaincode query -C myc1 -n asset_transfer -c '{"Args":["GetAssetsByRange","asset1","asset3"]}'
peer chaincode query -C myc1 -n asset_transfer -c '{"Args":["GetAssetHistory","asset1"]}'

Rich Query (Only supported if CouchDB is used as state database):
peer chaincode query -C myc1 -n asset_transfer -c '{"Args":["QueryAssetsByOwner","tom"]}'
peer chaincode query -C myc1 -n asset_transfer -c '{"Args":["QueryAssets","{\"selector\":{\"owner\":\"tom\"}}"]}'

Rich Query with Pagination (Only supported if CouchDB is used as state database):
peer chaincode query -C myc1 -n asset_transfer -c '{"Args":["QueryAssetsWithPagination","{\"selector\":{\"owner\":\"tom\"}}","3",""]}'

INDEXES TO SUPPORT COUCHDB RICH QUERIES

Indexes in CouchDB are required in order to make JSON queries efficient and are required for
any JSON query with a sort. Indexes may be packaged alongside
chaincode in a META-INF/statedb/couchdb/indexes directory. Each index must be defined in its own
text file with extension *.json with the index definition formatted in JSON following the
CouchDB index JSON syntax as documented at:
http://docs.couchdb.org/en/2.3.1/api/database/find.html#db-index

This asset transfer ledger example chaincode demonstrates a packaged
index which you can find in META-INF/statedb/couchdb/indexes/indexOwner.json.

If you have access to the your peer's CouchDB state database in a development environment,
you may want to iteratively test various indexes in support of your chaincode queries.  You
can use the CouchDB Fauxton interface or a command line curl utility to create and update
indexes. Then once you finalize an index, include the index definition alongside your
chaincode in the META-INF/statedb/couchdb/indexes directory, for packaging and deployment
to managed environments.

In the examples below you can find index definitions that support asset transfer ledger
chaincode queries, along with the syntax that you can use in development environments
to create the indexes in the CouchDB Fauxton interface or a curl command line utility.


Index for docType, owner.

Example curl command line to define index in the CouchDB channel_chaincode database
curl -i -X POST -H "Content-Type: application/json" -d "{\"index\":{\"fields\":[\"docType\",\"owner\"]},\"name\":\"indexOwner\",\"ddoc\":\"indexOwnerDoc\",\"type\":\"json\"}" http://hostname:port/myc1_assets/_index


Index for docType, owner, size (descending order).

Example curl command line to define index in the CouchDB channel_chaincode database:
curl -i -X POST -H "Content-Type: application/json" -d "{\"index\":{\"fields\":[{\"size\":\"desc\"},{\"docType\":\"desc\"},{\"owner\":\"desc\"}]},\"ddoc\":\"indexSizeSortDoc\", \"name\":\"indexSizeSortDesc\",\"type\":\"json\"}" http://hostname:port/myc1_assets/_index

Rich Query with index design doc and index name specified (Only supported if CouchDB is used as state database):
peer chaincode query -C myc1 -n asset_transfer -c '{"Args":["QueryAssets","{\"selector\":{\"docType\":\"asset\",\"owner\":\"tom\"}, \"use_index\":[\"_design/indexOwnerDoc\", \"indexOwner\"]}"]}'

Rich Query with index design doc specified only (Only supported if CouchDB is used as state database):
peer chaincode query -C myc1 -n asset_transfer -c '{"Args":["QueryAssets","{\"selector\":{\"docType\":{\"$eq\":\"asset\"},\"owner\":{\"$eq\":\"tom\"},\"size\":{\"$gt\":0}},\"fields\":[\"docType\",\"owner\",\"size\"],\"sort\":[{\"size\":\"desc\"}],\"use_index\":\"_design/indexSizeSortDoc\"}"]}'
*/

package main

import (
	"encoding/json"
	"fmt"
	"log"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

const index = "color~name"

// SimpleChaincode implements the fabric-contract-api-go programming model
type SimpleChaincode struct {
	contractapi.Contract
}

//객체 통일
type record struct {
	DocType   string `json:"docType"`
	Counting  string `json:"counting"`
	Seq       string `json:"seq"`
	Id        string `json:"id"`
	Money     string `json:"money"`
	Channel   string `json:"channel"`
	TxId      string `json:"txId"`
	Timestamp string `json:"timestamp"`
}

//트랜잭션 입력 : input_counting ,input_seq, input_user,input_money,
func (t *SimpleChaincode) Create_record(ctx contractapi.TransactionContextInterface, input_doctype string, input_counting string, input_seq string, input_id string, input_money string, input_time string) error {

	// t := time.Now()
	// var current:=(t.Year()+"년"+t.Month()+"월"+t.Day()+"일"+" "+t.Hour()+"시"+t.Minute()+"분"+t.Second()+"초")

	txid := ctx.GetStub().GetTxID()
	channel := ctx.GetStub().GetChannelID()

	data := &record{
		DocType:   input_doctype,
		Counting:  input_counting,
		Seq:       input_seq,
		Id:        input_id,
		Money:     input_money,
		Channel:   channel,
		TxId:      txid,
		Timestamp: input_time,
	}

	assetBytes, err := json.Marshal(data)
	if err != nil {
		// return err
	}

	err = ctx.GetStub().PutState(input_counting, assetBytes)
	if err != nil {
		// return err
	}

	//  Create an index to enable color-based range queries, e.g. return all blue assets.
	// 색상 기반 범위 쿼리 (예 : 모든 파란색 자산을 반환합니다.
	//  An 'index' is a normal key-value entry in the ledger.
	// 'index'은 원장의 일반적인 키-값 항목입니다.

	//  The key is a composite key, with the elements that you want to range query on listed first.
	//  키는 쿼리 범위를 지정할 요소가 먼저 나열된 복합 키입니다.
	//  In our case, the composite key is based on indexName~color~name.
	//  우리의 경우 복합키는 indexNaxme~ color ~ name을 기반으로 한다.
	//  This will enable very efficient state range queries based on composite keys matching indexName~color~*
	//  이렇게하면 indexName ~ color ~ *와 일치하는 복합 키를 기반으로 매우 효율적인 상태 범위 쿼리가 가능합니다.
	//  잘 이해안됨. 검색할때 효율성을 높이기위한 방법 ?
	colorNameIndexKey, err := ctx.GetStub().CreateCompositeKey(index, []string{data.Seq, data.Id})
	if err != nil {
		// return err
	}
	//  Save index entry to world state. Only the key name is needed, no need to store a duplicate copy of the asset.
	//  Note - passing a 'nil' value will effectively delete the key from state, therefore we pass null character as value
	value := []byte{0x00}

	return ctx.GetStub().PutState(colorNameIndexKey, value)

}

// //동작하는지 확인해보기 ==> 동작함 .. 동작하긴하는데 누구의 값인지 뭘 의미하는지 모르겟음
// func (t *SimpleChaincode) Test(ctx contractapi.TransactionContextInterface) (string) {
// 	response,err:=ctx.GetClientIdentity().GetID()
// 	if err != nil {

// 	}
// 	return response
// }
// func (t *SimpleChaincode) Test1(ctx contractapi.TransactionContextInterface) (string) {
// 	response,err:=ctx.GetClientIdentity().GetMSPID()
// 	if err != nil {

// 	}
// 	return response
// }
// func (t *SimpleChaincode) Test2(b m.BlockchainInfo) uint64 {
// 	response:=b.GetHeight()

// 	return response
// }

func (t *SimpleChaincode) Test(ctx contractapi.TransactionContextInterface) {

	fmt.Println(t.GetName())
}

func (t *SimpleChaincode) Test1(ctx contractapi.TransactionContextInterface) string {

	return "test111"
}

/* 조회 1 : 아이디로 조회*/
func (t *SimpleChaincode) Query_by_id(ctx contractapi.TransactionContextInterface, doctype string, id string) ([]*record, error) {

	queryString := fmt.Sprintf(`{"selector":{"docType":"%s","id":"%s"}}`, doctype, id)
	return getQueryResultForQueryString(ctx, queryString)
}

func (t *SimpleChaincode) Query_by_only_id(ctx contractapi.TransactionContextInterface, id string) ([]*record, error) {

	queryString := fmt.Sprintf(`{"selector":{"id":"%s"}}`, id)
	return getQueryResultForQueryString(ctx, queryString)
}

/*조회 2 : 게시글 seq로 조회*/

func (t *SimpleChaincode) Query_by_seq(ctx contractapi.TransactionContextInterface, doctype string, seq string) ([]*record, error) {

	queryString := fmt.Sprintf(`{"selector":{"docType":"%s","seq":"%s"}}`, doctype, seq)
	return getQueryResultForQueryString(ctx, queryString)
}

/*조회 3 : 범위 조회*/
func (t *SimpleChaincode) GetRangeRecord(ctx contractapi.TransactionContextInterface, startKey, endKey string) ([]*record, error) {
	resultsIterator, err := ctx.GetStub().GetStateByRange(startKey, endKey)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	return constructQueryResponseFromIterator(resultsIterator)
}

// 쿼리를 통해 데이터 읽어오기
func getQueryResultForQueryString(ctx contractapi.TransactionContextInterface, queryString string) ([]*record, error) {
	resultsIterator, err := ctx.GetStub().GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	return constructQueryResponseFromIterator(resultsIterator)
}

//읽어온데이터 하나씩 unmarshal 로 객체로 변환한 후 list에 담아 하나의 json 데이터로 만들기
func constructQueryResponseFromIterator(resultsIterator shim.StateQueryIteratorInterface) ([]*record, error) {

	var assets []*record
	for resultsIterator.HasNext() {
		queryResult, err := resultsIterator.Next()
		fmt.Println("queryResult", queryResult)
		if err != nil {
			return nil, err
		}
		var asset record
		err = json.Unmarshal(queryResult.Value, &asset)

		if err != nil {
			return nil, err
		}
		assets = append(assets, &asset)
	}

	return assets, nil
}

func main() {
	chaincode, err := contractapi.NewChaincode(&SimpleChaincode{})
	if err != nil {
		log.Panicf("Error creating asset chaincode: %v", err)
	}

	if err := chaincode.Start(); err != nil {
		log.Panicf("Error starting asset chaincode: %v", err)
	}
}
