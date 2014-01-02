
{"pageref":"https://www.worldspreads.com/tradeengine2/xeqtgatewayv3/Advanced.aspx","startedDateTime":"2011-03-30T17:10:51.533Z","time":541,"request":
{"method":"POST","url":"https://www.worldspreads.com/tradeengine2/xeqtgatewayv3/WSWorldSpreads.asmx/RequestTrade","headers":[
{"name":"Origin","value":"https://www.worldspreads.com"},
{"name":"Accept-Encoding","value":"gzip,deflate,sdch"},
{"name":"Accept-Language","value":"en-US,en;q=0.8"},
{"name":"Cookie","value":"__utmx=203616278.; __utmxx=203616278.; __utmz=203616278.1301337596.1.1.utmgclid=CLCrw6b08acCFQEY4QodHUhlaA|utmccn=(not%20set)|utmcmd=(not%20set)|utmctr=world%20spreads%200; BIGipServerWorldspreads_Site_pool=2855577792.20480.0000; ASP.NET_SessionId=i52baj55fjc2os3lazpibb55; BIGipServerTradingPlatformclusPool=1429514432.20480.0000; BIGipServerworldspreads.com_pool=4063537344.20480.0000; __utma=203616278.1618524401.1301337596.1301464445.1301464445.9; __utmc=203616278; __utmb=203616278.1.9.1301504849516"},
{"name":"Connection","value":"keep-alive"},
{"name":"Content-Length","value":"289"},
{"name":"Accept-Charset","value":"ISO-8859-1,utf-8;q=0.7,*;q=0.3"},
{"name":"Host","value":"www.worldspreads.com"},
{"name":"User-Agent","value":"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_7; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.204 Safari/534.16"},
{"name":"Content-Type","value":"application/json; charset=UTF-8"},
{"name":"Accept","value":"*/*"},
{"name":"Referer","value":"https://www.worldspreads.com/tradeengine2/xeqtgatewayv3/Advanced.aspx"}],"headersSize":-1,"bodySize":-1,"postData":
{"mimeType":"application/json; charset=UTF-8","text":"
{\"marketID\":\"19190\",\"quoteID\":\"9221\",\"price\":5925.3,\"stake\":1,\"tradeType\":1,\"tradeMode\":true,\"hasClosingOrder\":false,\"isGuaranteed\":false,\"orderModeID\":0,\"orderTypeID\":0,\"orderPriceModeID\":0,\"limitOrderPrice\":0,\"stopOrderPrice\":0,\"trailingPoint\":0,\"closePositionID\":0,\"isKaazingFeed\":true}"},"cookies":[
{"name":"__utmx","value":"203616278.","expires":null,"httpOnly":false,"secure":false},
{"name":"__utmxx","value":"203616278.","expires":null,"httpOnly":false,"secure":false},
{"name":"__utmz","value":"203616278.1301337596.1.1.utmgclid=CLCrw6b08acCFQEY4QodHUhlaA|utmccn=(not%20set)|utmcmd=(not%20set)|utmctr=world%20spreads%200","expires":null,"httpOnly":false,"secure":false},
{"name":"BIGipServerWorldspreads_Site_pool","value":"2855577792.20480.0000","expires":null,"httpOnly":false,"secure":false},
{"name":"ASP.NET_SessionId","value":"i52baj55fjc2os3lazpibb55","expires":null,"httpOnly":false,"secure":false},
{"name":"BIGipServerTradingPlatformclusPool","value":"1429514432.20480.0000","expires":null,"httpOnly":false,"secure":false},
{"name":"BIGipServerworldspreads.com_pool","value":"4063537344.20480.0000","expires":null,"httpOnly":false,"secure":false},
{"name":"__utma","value":"203616278.1618524401.1301337596.1301464445.1301464445.9","expires":null,"httpOnly":false,"secure":false},
{"name":"__utmc","value":"203616278","expires":null,"httpOnly":false,"secure":false},
{"name":"__utmb","value":"203616278.1.9.1301504849516","expires":null,"httpOnly":false,"secure":false}]},"response":
{"status":200,"statusText":"OK","headers":[
{"name":"Date","value":"Wed, 30 Mar 2011 17:10:39 GMT"},
{"name":"X-AspNet-Version","value":"2.0.50727"},
{"name":"X-Powered-By","value":"ASP.NET"},
{"name":"Content-Length","value":"331"},
{"name":"Server","value":"Microsoft-IIS/7.5"},
{"name":"Content-Type","value":"application/json; charset=utf-8"},
{"name":"Cache-Control","value":"private, max-age=0"}],"content":
{"size":331,"mimeType":"application/json"},"redirectURL":"","headersSize":-1,"bodySize":331},"timings":
{"blocked":0,"dns":-1,"connect":-1,"send":0,"wait":539,"receive":1,"ssl":-1}}

SELl REQ
{\"marketID\":\"19190\",\"quoteID\":\"9221\",\"price\":5925.3,\"stake\":1,\"tradeType\":1,\"tradeMode\":true,\"hasClosingOrder\":false,\"isGuaranteed\":false,\"orderModeID\":0,\"orderTypeID\":0,\"orderPriceModeID\":0,\"limitOrderPrice\":0,\"stopOrderPrice\":0,\"trailingPoint\":0,\"closePositionID\":0,\"isKaazingFeed\":true}"},"cookies":[


{"d":{"__type":"WorldSpreadsClient.TradeRequest","MarketID":19190,"Direction":"Sell","Market":"UK 100 - Daily Rolling Future","ExpiryDate":"31/12/2030","Price":5925.300000,"Stake":1.000000,"TradeStatus":"Opened","PositionID":10452140,"ReferralID":"0","CloseBets":
{"ProfitLoss":"0.000000","ClosedBet":[]},"Status":0,"Message":null}}

BUY REQ
{"marketID":"19190","quoteID":"9221","price":5972.5,"stake":1,"tradeType":1,"tradeMode":false,"hasClosingOrder":false,"isGuaranteed":false,"orderModeID":0,"orderTypeID":0,"orderPriceModeID":0,"limitOrderPrice":0,"stopOrderPrice":0,"trailingPoint":0,"closePositionID":0,"isKaazingFeed":true}

RES
{"d":{"__type":"WorldSpreadsClient.TradeRequest","MarketID":19190,"Direction":"Buy","Market":"UK 100 - Daily Rolling Future","ExpiryDate":"31/12/2030","Price":5973.500000,"Stake":1.000000,"TradeStatus":"Opened","PositionID":10468548,"ReferralID":"0","CloseBets":
{"ProfitLoss":"0.000000","ClosedBet":[]},"Status":0,"Message":null}
}