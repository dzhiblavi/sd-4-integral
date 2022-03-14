#!/bin/bash

curl 'http://localhost:8081/new-user?name=Ivan&funds=100'
curl 'http://localhost:8081/get-total?name=Ivan'

curl 'http://localhost:8081/add-funds?name=Ivan&delta=1000'
curl 'http://localhost:8081/get-total?name=Ivan'
curl 'http://localhost:8081/get-stocks?name=Ivan'

curl 'http://localhost:8081/buy-sell?name=Ivan&stock-name=s1&company-name=Intel&delta=5'
curl 'http://localhost:8081/get-total?name=Ivan'
curl 'http://localhost:8081/get-stocks?name=Ivan'

curl 'http://localhost:8081/buy-sell?name=Ivan&stock-name=s1&company-name=Intel&delta=-2'
curl 'http://localhost:8081/get-total?name=Ivan'
curl 'http://localhost:8081/get-stocks?name=Ivan'

curl 'http://localhost:8081/buy-sell?name=Ivan&stock-name=s3&company-name=Amd&delta=2'
curl 'http://localhost:8081/get-total?name=Ivan'
curl 'http://localhost:8081/get-stocks?name=Ivan'
