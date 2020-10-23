/*
Copyright 2020 IBM All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package main

import (
	"errors"
	"fmt"
	"io/ioutil"
	"os"
	"path/filepath"

	"github.com/hyperledger/fabric-sdk-go/pkg/core/config"
	"github.com/hyperledger/fabric-sdk-go/pkg/gateway"
)

func main() {
	fmt.Println("============ application-golang starts ============")

	os.Setenv("DISCOVERY_AS_LOCALHOST", "true")
	// /home/hyperledger/go/pkg/mod/github.com/hyperledger/fabric-sdk-go@v1.0.0-beta2/pkg/gateway/filesystemwallet.go 안의 NewFileSystemWallet
	// wallet 디렉토리를 만들고 wallet 객체에 해당 주소를저장해서 돌려받음


	wallet, err := gateway.NewFileSystemWallet("wallet")
	if err != nil {
		fmt.Printf("failed to create wallet: %v\n", err)
		os.Exit(1)
	}

	// /home/hyperledger/go/pkg/mod/github.com/hyperledger/fabric-sdk-go@v1.0.0-beta2/pkg/gateway/wallet.go
	// wallet 안에 appUser라는파일이 있는지 없는지
	
	if !wallet.Exists("appUser") {
		err = populateWallet(wallet)
		if err != nil {
			fmt.Printf("failed to populate wallet contents: %v\n", err)
			os.Exit(1)
		}
	}

	ccpPath := filepath.Join(
		"..",
		"..",
		"test-network",
		"organizations",
		"peerOrganizations",
		"org1.example.com",
		"connection-org1.yaml",
	)

	// /home/hyperledger/go/pkg/mod/github.com/hyperledger/fabric-sdk-go@v1.0.0-beta2/pkg/gateway/gateway.go

	gw, err := gateway.Connect(
		// filepath.Clean == > "foo/../../bar" 를 ../bar 로 간략하게 표시함  
		gateway.WithConfig(config.FromFile(filepath.Clean(ccpPath))),
		gateway.WithIdentity(wallet, "appUser"),
	)
	if err != nil {
		fmt.Printf("failed to connect to gateway: %v\n", err)
		os.Exit(1)
	}
	defer gw.Close()

	// /home/hyperledger/go/pkg/mod/github.com/hyperledger/fabric-sdk-go@v1.0.0-beta2/pkg/gateway/gateway.go
	// /home/hyperledger/go/pkg/mod/github.com/hyperledger/fabric-sdk-go@v1.0.0-beta2/pkg/gateway/network.go

	network, err := gw.GetNetwork("mychannel")
	if err != nil {
		fmt.Printf("failed to get network: %v\n", err)
		os.Exit(1)
	}

	// /home/hyperledger/go/pkg/mod/github.com/hyperledger/fabric-sdk-go@v1.0.0-beta2/pkg/gateway/network.go
	// /home/hyperledger/go/pkg/mod/github.com/hyperledger/fabric-sdk-go@v1.0.0-beta2/pkg/gateway/contract.go

	contract := network.GetContract("basic")

	// /home/hyperledger/go/pkg/mod/github.com/hyperledger/fabric-sdk-go@v1.0.0-beta2/pkg/gateway/contract.go

	result, err := contract.EvaluateTransaction("GetAllAssets")
	if err != nil {
		fmt.Printf("failed to evaluate transaction: %v\n", err)
		os.Exit(1)
	}
	fmt.Println(string(result))


	// SubmitTransaction:/home/hyperledger/go/pkg/mod/github.com/hyperledger/fabric-sdk-go@v1.0.0-beta2/pkg/gateway/contract.go
	// CreateTransaction:/home/hyperledger/go/pkg/mod/github.com/hyperledger/fabric-sdk-go@v1.0.0-beta2/pkg/gateway/contract.go
	// newTransaction : /home/hyperledger/go/pkg/mod/github.com/hyperledger/fabric-sdk-go@v1.0.0-beta2/pkg/gateway/transaction.go



	// result, err = contract.SubmitTransaction("CreateAsset", "asset13", "yellow",  "5", "Tom","1300")
	// if err != nil {
	// 	fmt.Printf("failed to submit transaction: %v\n", err)
	// 	os.Exit(1)
	// }
	// fmt.Println(string(result))



	// result, err = contract.EvaluateTransaction("ReadAsset", "asset4")
	// if err != nil {
	// 	fmt.Printf("failed to evaluate transaction: %v\n", err)
	// 	os.Exit(1)
	// }
	// fmt.Println(string(result))

	// // _, err = contract.SubmitTransaction("TransferAsset", "asset1", "Tom")
	// // if err != nil {
	// // 	fmt.Printf("Failed to submit transaction: %v\n", err)
	// // 	os.Exit(1)
	// // }

	// result, err = contract.EvaluateTransaction("ReadAsset", "asset1")
	// if err != nil {
	// 	fmt.Printf("failed to evaluate transaction: %v\n", err)
	// 	os.Exit(1)
	// }
	// fmt.Println(string(result))
	// fmt.Println("============ application-golang ends ============")
}

func populateWallet(wallet *gateway.Wallet) error {
	fmt.Println("============ populate wallet starts ============")
	// credPath := filepath.Join(
	// 	"..",
	// 	"..",
	// 	"test-network",
	// 	"organizations",
	// 	"peerOrganizations",
	// 	"org1.example.com",
	// 	"users",
	// 	"User1@org1.example.com",
	// 	"msp",
	// )
	credPath :="/home/hyperledger/test/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp"
	// certPath := filepath.Join(credPath, "signcerts", "User1@org1.example.com-cert.pem")
	certPath := filepath.Join(credPath, "signcerts", "cert.pem")
	// read the certificate pem
	
	fmt.Println("============ cert============")
	cert, err := ioutil.ReadFile(filepath.Clean(certPath))
	if err != nil {
		return err
	}
	fmt.Println("============ keystore============")
	keyDir := filepath.Join(credPath, "keystore")
	fmt.Println(keyDir)
	// there's a single file in this dir containing the private key
	files, err := ioutil.ReadDir(keyDir)
	fmt.Println("============ keystore============")
	if err != nil {
		return err
	}
	if len(files) != 1 {
		return errors.New("keystore folder should have contain one file")
	}
	fmt.Println("============ keyPath============")
	fmt.Println(files[0].Name())
	keyPath := filepath.Join(keyDir, files[0].Name())
	key, err := ioutil.ReadFile(filepath.Clean(keyPath))
	if err != nil {
		return err
	}

	fmt.Println("============ identity============")
	identity := gateway.NewX509Identity("Org1MSP", string(cert), string(key))

	err = wallet.Put("appUser", identity)
	if err != nil {
		return err
	}
	fmt.Println("============ populate wallet ends ============")
	return nil
}
