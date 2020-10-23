/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

'use strict';

const fs = require('fs');
const path = require('path');

exports.buildCCPOrg1 = () => {
	// load the common connection configuration file

	// path.resolve < -> path.join 
	//path.join 매개 변수 값을 다 이어 붙인다 
	// path .resolve 맨 오른쪽 부터 왼쪽으로 인자들을 붙이고 / 를 찾지못하면 현재 경로를 기준으로 /를 만난다 
	//ex)  path.resolve('hyperledger','go') == > /root/hyperledger/go
	// connection-org1.json : peer.org의 정보와  tlsCACerts 키 가 들어있다.
	const ccpPath = path.resolve(__dirname, '..', '..', 'test-network', 'organizations', 'peerOrganizations', 'org1.example.com', 'connection-org1.json');


	// existsSync == 동기식 파일확인  < - > exits(path,callback) 비동기식 파일 확인
	const fileExists = fs.existsSync(ccpPath);
	if (!fileExists) {
		throw new Error(`no such file or directory: ${ccpPath}`);
	}
	// readFileSync 동기식으로 파일을 불러와 utf8로 인코딩하여 읽기
	// connection-org1.json 에 들어있는 파일 그대로 불러옴 
	const contents = fs.readFileSync(ccpPath, 'utf8');

		
	// console.log(`*** contents: ${contents}`);


	// build a JSON object from the file contents
	// 파일 json형태로 인코딩 결과값 ccp: [object Object]
	const ccp = JSON.parse(contents);



	console.log(`Loaded the network configuration located at ${ccpPath}`);
	return ccp;
};

exports.buildCCPOrg2 = () => {
	// load the common connection configuration file
	const ccpPath = path.resolve(__dirname, '..', '..', 'test-network',
		'organizations', 'peerOrganizations', 'org2.example.com', 'connection-org2.json');
	const fileExists = fs.existsSync(ccpPath);
	if (!fileExists) {
		throw new Error(`no such file or directory: ${ccpPath}`);
	}
	const contents = fs.readFileSync(ccpPath, 'utf8');

	// build a JSON object from the file contents
	const ccp = JSON.parse(contents);

	console.log(`Loaded the network configuration located at ${ccpPath}`);
	return ccp;
};


exports.buildWallet = async (Wallets, walletPath) => {
	//app.js 에 wallet디렉토리가 있으면 true 없으면 false 
	// 있는 경우 

	// Create a new  wallet : Note that wallet is for managing identities.
	let wallet;
	if (walletPath) {

		// /home/hyperledger/test/fabric-samples/asset-transfer-basic/application-javascript/node_modules/fabric-network/lib/impl/wallet/wallets.d.ts
		// 해당 경로를 넣으면 wallet을 메모리에 불러온다 
		// wallet 디렉토리 생성됨
		wallet = await Wallets.newFileSystemWallet(walletPath);
		console.log(`Built a file system wallet at ${walletPath}`);
	} else {
		// wallet을 만들어 메모리에 올린다
		wallet = await Wallets.newInMemoryWallet();
		console.log('Built an in memory wallet');
	}

	return wallet;
};
