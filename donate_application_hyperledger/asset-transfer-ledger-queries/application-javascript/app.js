/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

'use strict';

const { Gateway, Wallets } = require('fabric-network');
const FabricCAServices = require('fabric-ca-client');
const path = require('path');
const { buildCAClient, registerAndEnrollUser, enrollAdmin } = require('../../test-application/javascript/CAUtil.js');
const { buildCCPOrg1, buildWallet } = require('../../test-application/javascript/AppUtil.js');

const channelName = 'mychannel';
const chaincodeName = 'ledger';
const mspOrg1 = 'Org1MSP';

const walletPath = path.join(__dirname, 'wallet');
const userId = 'appUser';

function prettyJSONString(inputString) {
	return JSON.stringify(JSON.parse(inputString), null, 2);
}

// pre-requisites:
// - fabric-sample two organization test-network setup with two peers, ordering service,
//   and 2 certificate authorities, with the state database using couchdb
//         ===> from directory /fabric-samples/test-network
//         ./network.sh up createChannel -ca -s couchdb
// - Use any of the asset-transfer-ledger-queries chaincodes deployed on the channel "mychannel"
//   with the chaincode name of "ledger". The following deploy command will package,
//   install, approve, and commit the javascript chaincode, all the actions it takes
//   to deploy a chaincode to a channel.
//         ===> from directory /fabric-samples/test-network
//         ./network.sh deployCC -ccn ledger -ccl javascript
// - Be sure that node.js is installed
//         ===> from directory /fabric-samples/asset-transfer-ledger-queries/application-javascript
//         node -v
// - npm installed code dependencies
//         ===> from directory /fabric-samples/asset-transfer-ledger-queries/application-javascript
//         npm install
// - to run this test application
//         ===> from directory /fabric-samples/asset-transfer-ledger-queries/application-javascript
//         node app.js

// NOTE: If you see  kind an error like these:
/*
    2020-08-07T20:23:17.590Z - error: [DiscoveryService]: send[mychannel] - Channel:mychannel received discovery error:access denied
    ******** FAILED to run the application: Error: DiscoveryService: mychannel error: access denied

   OR

   Failed to register user : Error: fabric-ca request register failed with errors [[ { code: 20, message: 'Authentication failure' } ]]
   ******** FAILED to run the application: Error: Identity not found in wallet: appUser
*/
// Delete the /fabric-samples/asset-transfer-ledger-queries/application-javascript/wallet directory
// and retry this application.
//
// The certificate authority must have been restarted and the saved certificates for the
// admin and application user are not valid. Deleting the wallet store will force these to be reset
// with the new certificate authority.
//

/**
 *  A test application to show ledger queries operations with any of the asset-transfer-ledger-queries chaincodes
 *   -- How to submit a transaction
 *   -- How to query and check the results
 *
 * To see the SDK workings, try setting the logging to show on the console before running
 *        export HFC_LOGGING='{"debug":"console"}'
 */


 //웹서버를 올리면 지갑생성 및 gateWay객체까지는 만들어 놓고대기함
// 그리고 데이터를 넣을건지 전부 받아올것인지 하나만 받을건이지 정함
let contract;


async function main() {

	try {
		// build an in memory object with the network configuration (also known as a connection profile)
		const ccp = buildCCPOrg1();

		// build an instance of the fabric ca services client based on
		// the information in the network configuration
		const caClient = buildCAClient(FabricCAServices, ccp, 'ca.org1.example.com');

		// setup the wallet to hold the credentials of the application user
		const wallet = await buildWallet(Wallets, walletPath);

		// in a real application this would be done on an administrative flow, and only once
		await enrollAdmin(caClient, wallet, mspOrg1);

		// in a real application this would be done only when a new user was required to be added
		// and would be part of an administrative flow
		await registerAndEnrollUser(caClient, wallet, mspOrg1, userId, 'org1.department1');

		// Create a new gateway instance for interacting with the fabric network.
		// In a real application this would be done as the backend server session is setup for
		// a user that has been verified.
		const gateway = new Gateway();

		try {
			// setup the gateway instance
			// The user will now be able to create connections to the fabric network and be able to
			// submit transactions and query. All transactions submitted by this gateway will be
			// signed by this user using the credentials stored in the wallet.
			await gateway.connect(ccp, {
				wallet,
				identity: userId,
				discovery: { enabled: true, asLocalhost: true } // using asLocalhost as this gateway is using a fabric network deployed locally
			});

			// Build a network instance based on the channel where the smart contract is deployed
			const network = await gateway.getNetwork(channelName);

			// Get the contract from the network.
			 contract = network.getContract(chaincodeName);

	
		} finally {
			// Disconnect from the gateway when the application is closing
			// This will close all connections to the network
			gateway.disconnect();
		}
	} catch (error) {
		console.error(`******** FAILED to run the application: ${error}`);
	}

	console.log('*** application ending');

}


function getTime(){
	let today = new Date();
	let year = today.getFullYear(); // 년도
	let month = today.getMonth() + 1;  // 월
	let date = today.getDate();  // 날짜

	let hours = today.getHours()+9; // 시
	let minutes = today.getMinutes();  // 분
	let seconds = today.getSeconds();  // 초
	let time = (year+"년"+month+"월"+date+"일 "+hours+"시"+minutes+"분"+seconds+"초")
	return time
}


async function insert(doctype,counting,seq,id,money,time){

	var time=getTime()
	var result

	if(counting.length==1){
		counting="0"+counting
		var response
		console.log(time);
		console.log('****************** insert ******************');
		console.log("doctype:"+doctype + " ,counting : "+ counting+ " ,seq : "+ seq+ " ,id : "+ id+ " , money : "+money+ " , time : "+time );
		response= await contract.submitTransaction('Create_record',doctype, counting, seq, id, money, time);
		console.log(response);
		result = "ok";
	}else{
		console.log('****************** insert ******************');
		console.log("doctype:"+doctype + " ,counting : "+ counting+ " ,seq : "+ seq+ " ,id : "+ id+ " , money : "+money+ " , time : "+time );
		response= await contract.submitTransaction('Create_record',doctype, counting, seq, id, money, time);
		console.log(response);
		result = "ok";
	}

	
	return result;

}




//request는 id조회 , seq 조회 , 범위 조회 세가지가 있다.
async function select(request,doctype,seq,id,start_num,end_num){

	var result;
	
	if(request=="select_id"){
		try{
			console.log("select_id ,  doctype:"+doctype + " ,seq : "+ seq+ " , id : "+id );
			result = await contract.evaluateTransaction('Query_by_id', doctype,id);
		}catch(errer){
			result="fuck"
			console.log("select_id,no");
		}
		
	}else if(request=="select_only_id"){
		try{

			console.log("select_only_id ,  doctype:"+doctype + " ,seq : "+ seq+ " , id : "+id );
			result = await contract.evaluateTransaction('Query_by_only_id',id);
		}catch(errer){
			result="fuck"
			console.log("select_only_id,no");
		}
	
	}else if(request=="select_seq"){
		try{

			console.log("select_seq ,  doctype:"+doctype + " ,seq : "+ seq+ " , id : "+id );
			result = await contract.evaluateTransaction('Query_by_seq', doctype,seq);
		}catch(errer){
			result="fuck"
			console.log("select_seq,no");
		}
	
	}else if(request=="select_range"){
		try{
			console.log("select_range ,  start_num:"+start_num + " ,end_num : "+ end_num);
			result = await contract.evaluateTransaction('GetRangeRecord', start_num,end_num);
		}catch(errer){
			result="fuck"
			console.log("select_range,no");
		}
		
	}	
	console.log(result.toString());
	return  result.toString();
}



const express = require('express');
const app = express();


//입력
app.all('/*', function(req, res, next) {
	res.header("Access-Control-Allow-Origin", "*");
	res.header("Access-Control-Allow-Headers", "X-Requested-With");
	next();
  });

  
app.get('/insert', (req, res) => {

	var doctype=req.query.request
	var counting=req.query.counting
	var seq=req.query.seq
	var id=req.query.id
	var money=req.query.money
	
	
	insert(doctype,counting,seq,id,money).then(function(result) {
		res.send(result);
	  });
});



//request는 id조회 , seq 조회 , 범위 조회 세가지가 있다.
app.get('/select', (req, res) => {

	var request=req.query.request
	var doctype=req.query.doctype
	var seq=req.query.seq
	var id=req.query.id
	var start_num=req.query.start_num
	var end_num=req.query.end_num

	select(request,doctype,seq,id,start_num,end_num).then(function(result) {
		res.send(result);
	  });
});



app.get('/test', (req, res) => {

	var doctype=req.query.request
	var counting=req.query.counting
	var seq=req.query.seq
	var id=req.query.id
	var money=req.query.money
	
	
	test(doctype,counting,seq,id,money).then(function(result) {
		res.send(result);
	  });
});

async function test(doctype,counting,seq,id,money,time){

	var time=getTime()

	console.log(counting.length)
	if(counting.length==1){
		counting="0"+counting
		console.log(counting)
	}
		var result = "ok";
	return result;

}






app.listen(8080, () => {
  console.log('Express App on port 8080!');
});


main();
