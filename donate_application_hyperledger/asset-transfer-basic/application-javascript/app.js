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
const chaincodeName = 'basic';
const mspOrg1 = 'Org1MSP';
const walletPath = path.join(__dirname, 'wallet');
const org1UserId = 'appUser';

function prettyJSONString(inputString) {
	return JSON.stringify(JSON.parse(inputString), null, 2);
}

// pre-requisites:
// - fabric-sample two organization test-network setup with two peers, ordering service,
//   and 2 certificate authorities
//         ===> from directory /fabric-samples/test-network
//         ./network.sh up createChannel -ca
// - Use any of the asset-transfer-basic chaincodes deployed on the channel "mychannel"
//   with the chaincode name of "basic". The following deploy command will package,
//   install, approve, and commit the javascript chaincode, all the actions it takes
//   to deploy a chaincode to a channel.
//         ===> from directory /fabric-samples/test-network
//         ./network.sh deployCC -ccn basic -ccl javascript
// - Be sure that node.js is installed
//         ===> from directory /fabric-samples/asset-transfer-basic/application-javascript
//         node -v
// - npm installed code dependencies
//         ===> from directory /fabric-samples/asset-transfer-basic/application-javascript
//         npm install
// - to run this test application
//         ===> from directory /fabric-samples/asset-transfer-basic/application-javascript
//         node app.js

// NOTE: If you see  kind an error like these:
/*
    2020-08-07T20:23:17.590Z - error: [DiscoveryService]: send[mychannel] - Channel:mychannel received discovery error:access denied
    ******** FAILED to run the application: Error: DiscoveryService: mychannel error: access denied

   OR

   Failed to register user : Error: fabric-ca request register failed with errors [[ { code: 20, message: 'Authentication failure' } ]]
   ******** FAILED to run the application: Error: Identity not found in wallet: appUser
*/
// Delete the /fabric-samples/asset-transfer-basic/application-javascript/wallet directory
// and retry this application.
//
// The certificate authority must have been restarted and the saved certificates for the
// admin and application user are not valid. Deleting the wallet store will force these to be reset
// with the new certificate authority.
//

/**
 *  A test application to show basic queries operations with any of the asset-transfer-basic chaincodes
 *   -- How to submit a transaction
 *   -- How to query and check the results
 *
 * To see the SDK workings, try setting the logging to show on the console before running
 *        export HFC_LOGGING='{"debug":"console"}'
 */


// async function main() {
	
// 	// 현재 경로 불러오기 
// 	console.log(`*** __dirname: ${__dirname}`);


// 	try {


// 		// build an in memory object with the network configuration (also known as a connection profile)

// 		// /home/hyperledger/test/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/connection-org1.json의 파일을 읽어와 json으로 파싱하여 리턴함
// 		const ccp = buildCCPOrg1();

// 		console.log(`*** ccp: ${ccp}`);

		
// 		// // build an instance of the fabric ca services client based on
// 		// // the information in the network configuration

// 		// ccp로 만들어온 json파일 중 certificateAuthorities 안의 ca.org1.example.com 값을 불러 그중 url ,caName ,tlsCACERTs[pem]을 가지고 
// 		// FabricCAServices 객체를 만들어 반환한다.

// 		const caClient = buildCAClient(FabricCAServices, ccp, 'ca.org1.example.com');

// 		// // setup the wallet to hold the credentials of the application user

// 		// await 비동기 처리 방법 async와 함께사용 
// 		// async로 선언한 메소드 안에서 비동기로 처리할 부분을 await을 선언하면
// 		//  비동기로 처리된 부분이 콜백을 받으면 await으로 선언된 부분이 실행된다.
// 		// 서버로부터받아오는데이터를 비동기로 처리않할경우 실행만되고 받아온값이 없기떄문에
// 		// 원하는 곳에 값이 널이 되거나 비어있을수있다.

// 		// 경로에 wallet이 있는지없는지를 확인하여 메모리에 올린 객체를 반환한다
// 		console.log(`*** walletPath: ${walletPath}`);
// 		const wallet = await buildWallet(Wallets, walletPath);

// 		// // in a real application this would be done on an administrative flow, and only once
		
// 		// caClient = FabricCAServices 로 만든 ca.org1.example.com 객체 
// 		// wallet : 지갑 메모리 경로 및 주소 
// 		// mspOrg1 : 그룹의 msp 이름 
// 		// wallet안의 admin  정보를 담는 부분
// 		// 처음 wallet에는 아무것도 들어있지않다
// 		// 해당 admin.id파일을 만들어 wallet에 넣는다.
// 		await enrollAdmin(caClient, wallet, mspOrg1);

// 		// // in a real application this would be done only when a new user was required to be added
// 		// // and would be part of an administrative flow


// 		// caClient = FabricCAServices 로 만든 ca.org1.example.com 객체 
// 		// wallet : 지갑 메모리 경로 및 주소 
// 		// mspOrg1 : 그룹의 msp 이름 
// 		// org1UserId =appUser
// 		// 처음 wallet에는 admin 정보만 들어있다.
// 		// 해당 admin.id파일을 만들어 wallet에 넣는다.
// 		await registerAndEnrollUser(caClient, wallet, mspOrg1, org1UserId, 'org1.department1');




// 		//admin 을 먼저 등록한 후 admin이 유저를 등록해주는방식 





// 		// // Create a new gateway instance for interacting with the fabric network.
// 		// // In a real application this would be done as the backend server session is setup for
// 		// // a user that has been verified.
// 		const gateway = new Gateway();

// 		try {
// 			// setup the gateway instance
// 			// The user will now be able to create connections to the fabric network and be able to
// 			// submit transactions and query. All transactions submitted by this gateway will be
// 			// signed by this user using the credentials stored in the wallet.
// 			await gateway.connect(ccp, {
// 				wallet,
// 				identity: org1UserId,
// 				discovery: { enabled: true, asLocalhost: true } // using asLocalhost as this gateway is using a fabric network deployed locally
// 			});

// 			// Build a network instance based on the channel where the smart contract is deployed
// 			const network = await gateway.getNetwork(channelName);

// 			// Get the contract from the network.
// 			const contract = network.getContract(chaincodeName);

// 			// Initialize a set of asset data on the channel using the chaincode 'InitLedger' function.
// 			// This type of transaction would only be run once by an application the first time it was started after it
// 			// deployed the first time. Any updates to the chaincode deployed later would likely not need to run
// 			// an "init" type function.

// 			// console.log('\n--> Submit Transaction: InitLedger, function creates the initial set of assets on the ledger');
// 			// await contract.submitTransaction('InitLedger');
// 			// console.log('*** Result: committed');

// 			// // Let's try a query type operation (function).
// 			// // This will be sent to just one peer and the results will be shown.
// 			console.log('\n--> Evaluate Transaction: GetAllAssets, function returns all the current assets on the ledger');
// 			let result = await contract.evaluateTransaction('GetAllAssets');
// 			console.log(`*** Result: ${prettyJSONString(result.toString())}`);
// 			var result1 = result.toString();
// 			return result1;


// 			// // Now let's try to submit a transaction.
// 			// // This will be sent to both peers and if both peers endorse the transaction, the endorsed proposal will be sent
// 			// // to the orderer to be committed by each of the peer's to the channel ledger.
// 			// console.log('\n--> Submit Transaction: CreateAsset, creates new asset with ID, color, owner, size, and appraisedValue arguments');
// 			// await contract.submitTransaction('CreateAsset', 'asset13', 'yellow', '5', 'Tom', '1300');
// 			// console.log('*** Result: committed');

// 			// console.log('\n--> Evaluate Transaction: ReadAsset, function returns an asset with a given assetID');
// 			// result = await contract.evaluateTransaction('ReadAsset', 'asset13');
// 			// console.log(`*** Result: ${prettyJSONString(result.toString())}`);

// 			// console.log('\n--> Evaluate Transaction: AssetExists, function returns "true" if an asset with given assetID exist');
// 			// result = await contract.evaluateTransaction('AssetExists', 'asset1');
// 			// console.log(`*** Result: ${prettyJSONString(result.toString())}`);

// 			// console.log('\n--> Submit Transaction: UpdateAsset asset1, change the appraisedValue to 350');
// 			// await contract.submitTransaction('UpdateAsset', 'asset1', 'blue', '5', 'Tomoko', '350');
// 			// console.log('*** Result: committed');

// 			// console.log('\n--> Evaluate Transaction: ReadAsset, function returns "asset1" attributes');
// 			// result = await contract.evaluateTransaction('ReadAsset', 'asset1');
// 			// console.log(`*** Result: ${prettyJSONString(result.toString())}`);

// 			// try {
// 			// 	// How about we try a transactions where the executing chaincode throws an error
// 			// 	// Notice how the submitTransaction will throw an error containing the error thrown by the chaincode
// 			// 	console.log('\n--> Submit Transaction: UpdateAsset asset70, asset70 does not exist and should return an error');
// 			// 	await contract.submitTransaction('UpdateAsset', 'asset70', 'blue', '5', 'Tomoko', '300');
// 			// 	console.log('******** FAILED to return an error');
// 			// } catch (error) {
// 			// 	console.log(`*** Successfully caught the error: \n    ${error}`);
// 			// }

// 			// console.log('\n--> Submit Transaction: TransferAsset asset1, transfer to new owner of Tom');
// 			// await contract.submitTransaction('TransferAsset', 'asset1', 'Tom1');
// 			// console.log('*** Result: committed');

// 			// console.log('\n--> Evaluate Transaction: ReadAsset, function returns "asset1" attributes');
// 			// result = await contract.evaluateTransaction('ReadAsset', 'asset1');
// 			// console.log(`*** Result: ${prettyJSONString(result.toString())}`);
// 		} finally {
// 			// Disconnect from the gateway when the application is closing
// 			// This will close all connections to the network
// 			gateway.disconnect();
// 		}
// 	} catch (error) {
// 		console.error(`******** FAILED to run the application: ${error}`);
// 	}
// }







//웹서버를 올리면 지갑생성 및 gateWay객체까지는 만들어 놓고대기함
// 그리고 데이터를 넣을건지 전부 받아올것인지 하나만 받을건이지 정함
let contract;
async function prepare(){
	
	try {

		// build an in memory object with the network configuration (also known as a connection profile)
		// /home/hyperledger/test/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/connection-org1.json의 파일을 읽어와 json으로 파싱하여 리턴함
		const ccp = buildCCPOrg1();
		console.log(`*** ccp: ${ccp}`);

		// // build an instance of the fabric ca services client based on
		// // the information in the network configuration

		// ccp로 만들어온 json파일 중 certificateAuthorities 안의 ca.org1.example.com 값을 불러 그중 url ,caName ,tlsCACERTs[pem]을 가지고 
		// FabricCAServices 객체를 만들어 반환한다.

		const caClient = buildCAClient(FabricCAServices, ccp, 'ca.org1.example.com');

		// // setup the wallet to hold the credentials of the application user

		// await 비동기 처리 방법 async와 함께사용 
		// async로 선언한 메소드 안에서 비동기로 처리할 부분을 await을 선언하면
		//  비동기로 처리된 부분이 콜백을 받으면 await으로 선언된 부분이 실행된다.
		// 서버로부터받아오는데이터를 비동기로 처리않할경우 실행만되고 받아온값이 없기떄문에
		// 원하는 곳에 값이 널이 되거나 비어있을수있다.

		// 경로에 wallet이 있는지없는지를 확인하여 메모리에 올린 객체를 반환한다
		console.log(`*** walletPath: ${walletPath}`);
		const wallet = await buildWallet(Wallets, walletPath);

		// // in a real application this would be done on an administrative flow, and only once
		
		// caClient = FabricCAServices 로 만든 ca.org1.example.com 객체 
		// wallet : 지갑 메모리 경로 및 주소 
		// mspOrg1 : 그룹의 msp 이름 
		// wallet안의 admin  정보를 담는 부분
		// 처음 wallet에는 아무것도 들어있지않다
		// 해당 admin.id파일을 만들어 wallet에 넣는다.
		await enrollAdmin(caClient, wallet, mspOrg1);

		// // in a real application this would be done only when a new user was required to be added
		// // and would be part of an administrative flow


		// caClient = FabricCAServices 로 만든 ca.org1.example.com 객체 
		// wallet : 지갑 메모리 경로 및 주소 
		// mspOrg1 : 그룹의 msp 이름 
		// org1UserId =appUser
		// 처음 wallet에는 admin 정보만 들어있다.
		// 해당 admin.id파일을 만들어 wallet에 넣는다.
		await registerAndEnrollUser(caClient, wallet, mspOrg1, org1UserId, 'org1.department1');

		//admin 을 먼저 등록한 후 admin이 유저를 등록해주는방식 

		// // Create a new gateway instance for interacting with the fabric network.
		// // In a real application this would be done as the backend server session is setup for
		// // a user that has been verified.
		const gateway = new Gateway();

		try {
			// setup the gateway instance
			// The user will now be able to create connections to the fabric network and be able to
			// submit transactions and query. All transactions submitted by this gateway will be
			// signed by this user using the credentials stored in the wallet.
			await gateway.connect(ccp, {
				wallet,
				identity: org1UserId,
				discovery: { enabled: true, asLocalhost: true } // using asLocalhost as this gateway is using a fabric network deployed locally
			});

			// Build a network instance based on the channel where the smart contract is deployed
			const network = await gateway.getNetwork(channelName);

			// Get the contract from the network.
			 contract = network.getContract(chaincodeName);
			 console.log(`******************************`);
			 console.log(`******************************`);
			 console.log(`******************************`);
			 console.log(`******************************`);
			 console.log(`*** contract ready complete***`);
		} finally {
			// Disconnect from the gateway when the application is closing
			// This will close all connections to the network
			gateway.disconnect();
		}
	} catch (error) {
		console.error(`******** FAILED to run the application: ${error}`);
	}
}


async function B_insert(num){
	// Now let's try to submit a transaction.
	// This will be sent to both peers and if both peers endorse the transaction, the endorsed proposal will be sent
	// to the orderer to be committed by each of the peer's to the channel ledger.
	console.log(num);
	await contract.submitTransaction('CreateAsset', num, 'yellow', '5', 'Tom', '1300');
	var result1 = "commit"
	return result1;
}

async function B_getAll(){
	let result = await contract.evaluateTransaction('GetAllAssets');
	console.log(`*** Result: ${prettyJSONString(result.toString())}`);
	var result1 = result.toString();
	return result1;
}

async function B_selectGet(id){


	console.log('\n--> Evaluate Transaction: ReadAsset, function returns an asset with a given assetID');
	let result = await contract.evaluateTransaction('ReadAsset', id);
	var result1 = result.toString();
	return result1;
}


const express = require('express');
const app = express();
// app.get('/', (req, res) => {

//   res.send('Hello World!');
// });



  //전부 받아오기 
app.get('/B_getAll', (req, res) => {
	B_getAll().then(function(result) {
		res.send(result);
	  });
	
  });

//입력
app.get('/B_insert', (req, res) => {
	const id= req.query.id

	B_insert(id).then(function(result) {
		res.send(result);
	  });
	
  });

  //하나만 받아오기 
app.get('/B_selectGet', (req, res) => {
	const id= req.query.id
	B_selectGet(id).then(function(result) {
		res.send(result);
	  });
	
});


app.listen(8080, () => {
  console.log('Express App on port 8080!');
});

prepare();

// main();
