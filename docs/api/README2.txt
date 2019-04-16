# Charg Service 
The chgcoin.org back-end service

	
## JSON API methods:

	Method :	**getRates**
	Description : *Get current rates to ether*
	Parameters : none
	Result : 
	```
	{BTC: ..., USD: ..., LTC: ...}
	```

	Method :	**getFees**
	Description : *Get payment fees*
	Parameters : none
	Result : 
	```
	{BTC: ..., USD: ..., LTC: ...}
	```

	Method :	**getPaymentMethods**
	Description : *Get available payment methods and currencies*
	Parameters : none
	Result : 
	```
	[method: [currencies]]
	```
	
	Method :	**getBlockNumber**
	Description : *Get current ethereum network block number*
	Parameters : none
	Result : 
	```
	["blockNumber", ...]
	```

	Method :	**getBestSellOrder**
	Description : *Get best sell order for provided amount*
	Parameters : 
		amountCHG: number
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

	Method :	**getPaymentData**
	Description : *Get payment data for provided gateway and currency*
	Parameters : 
		gateway: string (braintree, litecoin, ... )
	Result : 
	```
		{paymentData: object}
	```

	Method :	**confirmPayment**
	Description : *Start/Confirm payment transaction*
	Parameters : 
		gateway: string (braintree, litecoin, ... )
		currency: string (USD, LTC)
		station: string (the node ethereum network address)
		orderHash: string 
		amount: number (in provided currency)
		paymentId: string (data/address/nonce from getPaymentData function)
	Result : 
	```
		{paymentResult: object}
	```







