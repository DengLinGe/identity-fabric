package chaincode

import (
	"encoding/json"
	"fmt"
	"log"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// SmartContract provides functions for managing an Asset
type SmartContract struct {
	contractapi.Contract
}
type RequestData struct {
	Name             string `json:"name"`
	Type             string `json:"type"`
	ShortDescription string `json:"shortDescription"`
	LongDescription  string `json:"longDescription"`
}
type IssuerInfo struct {
	ID          int      `json:"id"`
	UUID        string   `json:"uuid"`
	Did         string   `json:"did"`
	Website     string   `json:"website"`
	Endpoint    string   `json:"endpoint"`
	Description string   `json:"description"`
	ServiceType string   `json:"serviceType"`
	RequestData []string `json:"RequestData"`
	Deleted     bool     `json:"deleted"`
	CreateTime  string   `json:"createTime"`
	UpdateTime  string   `json:"updateTime"`
}

var didIndexName string = "didIndex-"
var issuerIndexName string = "issuerIndex-"

func (s *SmartContract) GetDoc(ctx contractapi.TransactionContextInterface, id string) (string, error) {
	key, err := ctx.GetStub().CreateCompositeKey(didIndexName, []string{id})
	doc, err := ctx.GetStub().GetState(key)
	if err != nil {
		return "", fmt.Errorf("the DID %s does not exist", id)
	}
	return string(doc), nil

}

func (s *SmartContract) SaveDoc(ctx contractapi.TransactionContextInterface, id string, doc string) error {
	key, err := ctx.GetStub().CreateCompositeKey(didIndexName, []string{id})
	if err != nil {
		return err
	}
	log.Println("This is saveDoc!!!!!!!!!!!!!!!")
	exists, err := s.DocExists(ctx, key)
	if err != nil {
		return err
	}
	if exists {
		return fmt.Errorf("the DID-doc %s already exists", id)
	}
	return ctx.GetStub().PutState(key, []byte(doc))
}

func (t *SmartContract) GetAllDoc(ctx contractapi.TransactionContextInterface) ([]string, error) {
	resultsIterator, err := ctx.GetStub().GetStateByPartialCompositeKey(didIndexName, []string{})
	//resultsIterator, err := ctx.GetStub().GetStateByRange("", "")
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()
	var docs []string
	log.Println("This is getAllDoc!!!!!!!!!!!!!!!")
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		doc := string(queryResponse.Value)
		docs = append(docs, doc)

	}
	return docs, nil
}

func (t *SmartContract) ChangeDoc(ctx contractapi.TransactionContextInterface, id string, key string, newValue string) error {
	didID, err := ctx.GetStub().CreateCompositeKey(didIndexName, []string{id})
	exists, err := t.DocExists(ctx, didID)
	if err != nil {
		return err
	}
	if !exists {
		return fmt.Errorf("the DID-doc %s don`t exists", id)
	}

	docString, err := ctx.GetStub().GetState(didID)
	if err != nil {

		return fmt.Errorf("This did doesn`t exist in fabric")
	}
	mapDoc, err := JsonToMap(string(docString))
	if err != nil {

		return fmt.Errorf("The DID document format was incorrect and could not be converted!")
	}
	_, ok := mapDoc[key]
	if !ok {
		return fmt.Errorf("The map does`n contain this key:%v", key)
	}
	mapDoc[key] = newValue
	// 值包装结构体
	newDoc, err := MapToJson(mapDoc)
	if err != nil {
		return fmt.Errorf("The JSON String format was incorrect and could not be converted!")
	}

	return ctx.GetStub().PutState(didID, []byte(newDoc))

}

func (s *SmartContract) DocExists(ctx contractapi.TransactionContextInterface, id string) (bool, error) {
	DocJSON, err := ctx.GetStub().GetState(id)
	if err != nil {
		return false, fmt.Errorf("failed to read from world state: %v", err)
	}

	return DocJSON != nil, nil
}

func (s *SmartContract) DeleteAndInit(ctx contractapi.TransactionContextInterface) error {

	resultsIterator, err := ctx.GetStub().GetStateByPartialCompositeKey(didIndexName, []string{})

	if err != nil {
		return fmt.Errorf("failed to read from world state: %v", err)
	}
	defer resultsIterator.Close()

	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return fmt.Errorf("failed to read from resultsIterator: %v", err)
		}
		key := queryResponse.Key
		err = ctx.GetStub().DelState(key)
		if err != nil {
			return fmt.Errorf("failed to delete did from world state: %v", err)
		}

	}

	issuerIterator, err := ctx.GetStub().GetStateByPartialCompositeKey(issuerIndexName, []string{})
	if err != nil {
		return fmt.Errorf("failed to read from world state: %v", err)
	}
	defer issuerIterator.Close()
	for issuerIterator.HasNext() {
		queryResponse, err := issuerIterator.Next()
		if err != nil {
			return fmt.Errorf("failed to read from issuerIterator: %v", err)
		}
		key := queryResponse.Key
		err = ctx.GetStub().DelState(key)
		if err != nil {
			return fmt.Errorf("failed to delete issuer from world state: %v", err)
		}

	}

	return nil
}

func (s *SmartContract) DeleteDoc(ctx contractapi.TransactionContextInterface, id string) error {
	didID, err := ctx.GetStub().CreateCompositeKey(didIndexName, []string{id})
	exist, err := s.DocExists(ctx, didID)
	if err != nil {
		return fmt.Errorf("failed to read from world state: %v", err)
	}
	if !exist {
		return fmt.Errorf("the DID-doc %s don`t exists", id)
	}
	err_1 := ctx.GetStub().DelState(didID)
	if err_1 != nil {
		return fmt.Errorf("failed to delete from world state: %v", err)
	}
	return nil
}

func (s *SmartContract) AddIssuer(ctx contractapi.TransactionContextInterface, uuid string, doc string) error {
	key, _ := ctx.GetStub().CreateCompositeKey(issuerIndexName, []string{uuid})
	exist, err := s.DocExists(ctx, key)
	if err != nil {
		return fmt.Errorf("failed to read from world state: %v", err)
	}
	if exist {
		return fmt.Errorf("the issuer-doc %s exists", uuid)
	}
	err = ctx.GetStub().PutState(key, []byte(doc))
	if err != nil {
		return fmt.Errorf("failed to read from world state: %v", err)
	}
	return nil
}

func (s *SmartContract) GetIssuerList(ctx contractapi.TransactionContextInterface) ([]*IssuerInfo, error) {

	resultsIterator, err := ctx.GetStub().GetStateByPartialCompositeKey(issuerIndexName, []string{})
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()
	var docs []*IssuerInfo
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		var issuerInfo IssuerInfo
		err = json.Unmarshal(queryResponse.Value, &issuerInfo)
		if err != nil {
			return nil, err
		}
		//doc := string(queryResponse.Value)
		docs = append(docs, &issuerInfo)

	}
	return docs, nil
}

func JsonToMap(jsonStr string) (map[string]interface{}, error) {
	m := make(map[string]interface{})
	err := json.Unmarshal([]byte(jsonStr), &m)
	if err != nil {
		fmt.Printf("Unmarshal with error: %+v\n", err)
		return nil, err
	}

	for k, v := range m {
		fmt.Printf("%v: %v\n", k, v)
	}

	return m, nil
}

// Convert map json string
func MapToJson(m map[string]interface{}) (string, error) {
	jsonByte, err := json.Marshal(m)
	if err != nil {
		fmt.Printf("Marshal with error: %+v\n", err)
		return "", nil
	}

	return string(jsonByte), nil
}
