exchange platform
https://dhanyainnovation.com:81/?key=0x1aa494ff7a493e0ba002e2d38650d4d21bd5591b#

testing Websockets
https://dhanyainnovation.com:3002

# Charg Service 
The chgcoin.org back-end service

## WebSocket events:

	Event : **newBlockNumber**
	Description : *New ethereum network block is mined*
	Result : 
	```
	["newBlockNumber", ...]
	```

	Event : **newExchange**
	Description : *New charge exchange event. Ehtereum transaction as a result*
	Result : 
	```
	{
		address: "..."
		blockHash: "..."
		blockNumber: ...
		raw: {
			data:"..."
			topics: {
				...
			}
		}
		transactionHash: "..."
	}
	```

	Event :	**currentRates**
	Description : *Current rates of most currencies to ether*
	Result : 
	```
	["currentRates", {BTC: ..., USD: ..., LTC: ...}]
	```
	
## WebSocket requests:

	Method :	**getRates**
	Description : *Get current rates to ether*
	Parameters : none
	Result : 
	```
	{BTC: ..., USD: ..., LTC: ...}
	```

	Method :	**getCurrencies**
	Description : *Get available currencies*
	Parameters : none
	Result : 
	```
	[CHG, ETH, BTC, USD, LTC]
	```
	
	Method :	**getBlockNumber**
	Description : *Get current ethereum network block number*
	Parameters : none
	Result : 
	```
	["currentBlockNumber", ...]
	```

	Method :	**getMarket**
	Description : *Get data from another exchange market"
	Parameters : market ("ForkDelta", "EtherDelta", ...)
	Result : 
	```
	["forkdeltaMarket",{tokenAddr: "0xc4a86561cb0b7ea1214904f26e6d50fd357c7986", quoteVolume: "0", baseVolume: "0",…}]
	```


	Method :	**getPastEvents**
	Description : *Get all Charge exchange events*
	Parameters : none
	Result : 
	```
	[{
		address: "..."
		blockHash: "..."
		blockNumber: ...
		raw: {
			data:"..."
			topics: {
				...
			}
		}
		transactionHash: "..."
	},...]
	```

	Method :	**getBestSellOrder**
	Description : *Get best sell order*
	Parameters : paymentData {amountCHG: ...} 
	Result : 
	```
	{
		expire: ... ,
		get: ... ,
		give: ... ,
		hash: "...",
		rate: "...",
		seller: "..."
	}
	```

	Method :	**getBestBuyOrder**
	Description : *Get best buy order*
	Parameters : paymentData {amountCHG: ...} 
	Result : 
	```
	{
		expire: ... ,
		get: ... ,
		give: ... ,
		hash: "...",
		rate: "...",
		buyer: "..."
	}
	```

	
	Method :	**getFees**
	Description : *Get exchange fees in percents*
	Parameters : none
	Result : 
	```
	{BTC: 2, LTC: 2, USD: 4}
	```
	
	Method :	**getBitcoinAddress**
	Description : *Get addterss for payment in BTC*
	Parameters : none
	Result : 
	```
	addressBTC
	```
	
	Method :	**checkBitcoinPayment**
	Description : *This method should be called after BTC transaction is approved*
	Parameters : *paymentData {orderHash: ..., amountBTC: ..., addressBTC: ...} *
	Result : 
	```
	{result: ...}
	```


	Method :	**getLitecoinAddress**
	Description : *Get addterss for payment in LTC*
	Parameters : none
	Result : 
	```
	addressLTC
	```

	Method :	**checkLitecoinPayment**
	Description : *This method should be called after LTC transaction is approved*
	Parameters : *paymentData {orderHash: ..., amountLTC: ..., addressLTC: ...} *
	Result : 
	```
	{result: ...}
	```

	Method :	**getBraintreeToken**
	Description : *Get token for credit card payment*
	Parameters : none
	Result : 
	```
	token
	```

	Method :	**payBraintree**
	Description : *Start Braintree credit card transaction*
	Parameters : *paymentData {orderHash: ..., amountUSD: ..., nonce: ...}*
	Result : 
	```
	{result: ...}
	```

	Method :	**getLocation**
	Description : *Get client location by IP address*
	Parameters : none
	Result : 
	```
		{location: ...}
	```


