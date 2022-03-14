#!/bin/bash
set -eux

curl 'http://localhost:8080/new-company?name=Intel'

curl 'http://localhost:8080/new-company?name=Amd'

curl 'http://localhost:8080/new-stock?name=s1&company=Intel&quantity=10&price=100'
curl 'http://localhost:8080/new-stock?name=s2&company=Intel&quantity=10&price=500'

curl 'http://localhost:8080/new-stock?name=s3&company=Amd&quantity=10&price=10'
curl 'http://localhost:8080/new-stock?name=s4&company=Amd&quantity=10&price=1000'

curl 'http://localhost:8080/stock-info'
